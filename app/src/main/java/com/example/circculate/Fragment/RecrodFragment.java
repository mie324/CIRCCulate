package com.example.circculate.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecrodFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private boolean recordFlag = false;
    private Handler updateTimeTaskHandler = new Handler();
    private boolean startRecordFlag = false;
    private long startTime = 0;
    private long beforePauseTime = 0;
    private static final String TAG = "RECORDER";
    private MediaRecorder recorder;

    private boolean hasPermissionToRecord = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private String filename;

    private UserModel currentUser;

    private FirebaseAuth mAuth;
    public RecrodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Recording Fragment.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                hasPermissionToRecord = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
//        if(!hasPermissionToRecord) switchToHomePage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recrod, container, false);
        initComponent(root);
        mAuth = FirebaseAuth.getInstance();


        return root;
    }

    private void initComponent(final View root){
        final FloatingActionButton recordButton = root.findViewById(R.id.record_bt);
        FloatingActionButton stopButton = root.findViewById(R.id.stop_bt);
        filename = getContext().getExternalCacheDir().getAbsolutePath();
        filename += "/audiorecord.3gp";

        currentUser = (UserModel) getArguments().getSerializable("LoggedUser");
//        getActivity().getEx
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(!startRecordFlag){
                    //start recording
                    //set recorder
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setOutputFile(filename);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try{
                        recorder.prepare();
                    }catch (IOException e){
                        Log.e(TAG, "onClick: " + e.toString());
                    }

                    recorder.start();

                    //set UI
                    ((TextView)root.findViewById(R.id.recording_time)).setText("00 : 00");
                    startTime = SystemClock.uptimeMillis();
//                    Log.d(TAG, "onClick: " + startTime);
                    startRecordFlag = true;
                    recordFlag = true;
                    recordButton.setImageResource(R.drawable.ic_pause_white_large);
                    updateTimeTaskHandler.post(updateRecordTimeTask);
                }else {
                    // already in recording.
                    if(!recordFlag){
                        // click record, resume recording.
                        recordFlag = true;
                        recorder.resume();
                        startTime = SystemClock.uptimeMillis();
                        updateTimeTaskHandler.post(updateRecordTimeTask);
                        recordButton.setImageResource(R.drawable.ic_pause_white_large);
                    }else {
                        // click pause, pause recording.
                        recorder.pause();

                        recordFlag = false;
                        beforePauseTime += SystemClock.uptimeMillis() - startTime;

                        startTime = 0;
                        updateTimeTaskHandler.removeCallbacks(updateRecordTimeTask);
                        recordButton.setImageResource(R.drawable.ic_mic_white_large);
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startRecordFlag){
                    recorder.stop();
                    recorder.release();
                    recorder = null;

//                    Toast.makeText(getActivity(), "Fragment pop up for saving recording", Toast.LENGTH_SHORT).show();
                    startRecordFlag = false;
                    beforePauseTime = 0;
                    recordButton.setImageResource(R.drawable.ic_mic_white_large);
                    updateTimeTaskHandler.removeCallbacks(updateRecordTimeTask);

                    showConfirmDialog();
                }else {
                    Toast.makeText(getActivity(), "You haven't started recording yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showConfirmDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_recording);

        dialog.setCancelable(true);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView)dialog.findViewById(R.id.username)).setText(currentUser.getUsername());

        final AppCompatEditText recordTitle = dialog.findViewById(R.id.record_title);
        AppCompatEditText rerordDesc = dialog.findViewById(R.id.record_desc);

        ((AppCompatButton)dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ((AppCompatButton)dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recordTitleText = recordTitle.getText().toString();
                if(TextUtils.isEmpty(recordTitleText)){
                    Toast.makeText(getActivity(), "You must fill in title.", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFileToStorage();
                    Toast.makeText(getActivity(), recordTitleText, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void uploadFileToStorage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Uri uploadedFile = Uri.fromFile(new File(filename));
        final StorageReference fileRef = storageRef.child(mAuth.getUid() + "/" + uploadedFile.getLastPathSegment());
        fileRef.putFile(uploadedFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Upload Successed.", Toast.LENGTH_SHORT).show();
//                    transRecordToText();
                    Log.d(TAG, "onComplete: filepath: " + fileRef.getPath());
                }else {
                    Log.d(TAG, "onComplete: " + task.getException().toString());
                    Toast.makeText(getActivity(), "Upload Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

//    private void transRecordToText() throws Exception{
//        try (SpeechClient client = SpeechClient.create()){
//            RecognitionConfig config = RecognitionConfig.newBuilder()
//                    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
//                    .setLanguageCode("en-US")
//                    .setSampleRateHertz(16000).build();
//
////            RecognitionAudio record = RecognitionAudio.newBuilder().setUri()
//        }
//    }

    private Runnable updateRecordTimeTask = new Runnable() {
        @Override
        public void run() {
            updateRecordTime();

            updateTimeTaskHandler.postDelayed(this, 200);

        }
    };

    private void updateRecordTime(){
        long currentRecordTime = SystemClock.uptimeMillis() - startTime;
        TextView recordTime = getView().findViewById(R.id.recording_time);
//        Log.d(TAG, "updateRecordTime: " + beforePauseTime);
//        Log.d(TAG, "updateRecordTime: " + currentRecordTime);
        currentRecordTime += beforePauseTime;
        int sec = (int)(currentRecordTime / 1000);
        int min = sec / 60;
        sec %= 60;

        recordTime.setText(String.format("%02d", min) + " : " + String.format("%02d", sec));
    }

}
