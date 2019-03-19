package com.example.circculate.Fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.AudioModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.example.circculate.service.SpeechService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;


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
//    private MediaRecorder recorder;
    private AudioRecord recorder;
    private Thread recordingThred;
    private String timestamp;
    private boolean hasPermissionToRecord = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private String filename;

    private static final int SAMPLE_RATE = 16000;//16k for emulater, change to 44.1k for device use
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_IN_CONFIG, AUDIO_FORMAT);
    private String translatedText = "";

    private SpeechService mSpeechService;

    private UserModel currentUser;

    private FirebaseAuth mAuth;
    private String filenameNew;

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

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(new Intent(getActivity(), SpeechService.class), mServiceConnection, getActivity().BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {


        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        getActivity().unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
//                    Log.d(TAG, "onSpeechRecognized: recognize " + text);
                    if(isFinal){
                        if(!text.equals("not recognize")){
                            translatedText = translatedText + text;
//                            Looper.prepare();
//                            Toast.makeText(getActivity(), "Speech translate succeed.", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }else {
//                            Looper.prepare();
//                            Toast.makeText(getActivity(), "Translation failed.", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }
                        Log.d(TAG, "onSpeechRecognized: audio file recognize: " + text);
//                        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                    }
                }
            };


    private void initComponent(final View root){
        final FloatingActionButton recordButton = root.findViewById(R.id.record_bt);
        FloatingActionButton stopButton = root.findViewById(R.id.stop_bt);
        filename = getContext().getExternalCacheDir().getAbsolutePath();
        filename += "/audiorecord.raw";

        currentUser = (UserModel) getArguments().getSerializable("LoggedUser");
//        getActivity().getEx
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(!startRecordFlag){
                    //start recording
                    //set recorder
//                    recorder = new MediaRecorder();
//                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                    recorder.setOutputFile(filename);
//                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                    try{
//                        recorder.prepare();
//                    }catch (IOException e){
//                        Log.e(TAG, "onClick: " + e.toString());
//                    }
//                    Log.d(TAG, "onClick: CurrentTime: " + currentTime.format(cal.getTime()));
                    recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE,
                            CHANNEL_IN_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
                    translatedText = "";
                    recorder.startRecording();
                    recordingThred = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            writeAudioDataToFile();
                        }
                    }, "Audio recording thread");
                    recordingThred.start();
//                    mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
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
                        recorder = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE,
                                CHANNEL_IN_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

                        recorder.startRecording();
                        recordingThred = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                writeAudioDataToFile();
                            }
                        }, "Audio recording thread");
                        recordingThred.start();
//                        recorder.resume();
                        startTime = SystemClock.uptimeMillis();
                        updateTimeTaskHandler.post(updateRecordTimeTask);
                        recordButton.setImageResource(R.drawable.ic_pause_white_large);
                    }else {
                        // click pause, pause recording.
//                        recorder.pause();

                        recordFlag = false;
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        recordingThred = null;

                        File localRecordFile = new File(filename);
                        try {
                            FileInputStream testInpuStream = new FileInputStream(localRecordFile);
                            Log.d(TAG, "onClick: get record file input stream");
                            mSpeechService.recognizeInputStream(testInpuStream);
                        }catch (IOException e){
                            Log.d(TAG, "onClick: cannot find file");
                        }

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
                    recordFlag = false;
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    recordingThred = null;

//                    Toast.makeText(getActivity(), "Fragment pop up for saving recording", Toast.LENGTH_SHORT).show();
                    startRecordFlag = false;
                    beforePauseTime = 0;
                    recordButton.setImageResource(R.drawable.ic_mic_white_large);
                    updateTimeTaskHandler.removeCallbacks(updateRecordTimeTask);
                    File localRecordFile = new File(filename);
                    try {
                        FileInputStream testInpuStream = new FileInputStream(localRecordFile);
                        Log.d(TAG, "onClick: get record file input stream");
                        mSpeechService.recognizeInputStream(testInpuStream);
                    }catch (IOException e){
                        Log.d(TAG, "onClick: cannot find file");
                    }

//                    mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
//                    mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio20190315094703));
//                    FileInputStream testInpuStream = new FileInputStream(localRecordFile);
//                    mSpeechService.recognizeInputStream(testInpuStream);
                    showConfirmDialog();
                }else {
                    Toast.makeText(getActivity(), "You haven't started recording yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void writeAudioDataToFile(){
        byte[] audioData = new byte[BUFFER_SIZE];
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(filename));
        }catch (FileNotFoundException e){
            Log.d(TAG, "writeAudioDataToFile: " + e.toString());
        }
        if(recorder == null){
            Log.d(TAG, "writeAudioDataToFile: recorder null");
        }
        
        if(audioData == null){
            Log.d(TAG, "writeAudioDataToFile: audio data null");
        }
        while (recordFlag){
//            Log.d(TAG, "writeAudioDataToFile: read data from recorder");
            if(recorder == null){
                Log.d(TAG, "writeAudioDataToFile: recorder null");
            }
            int status = recorder.read(audioData, 0, audioData.length);
            if(status == AudioRecord.ERROR_INVALID_OPERATION || status == AudioRecord.ERROR_BAD_VALUE){
                Log.d(TAG, "writeAudioDataToFile: read audio buffer fail with code" + status);
                return;
            }

            try {
                os.write(audioData, 0, audioData.length);
            }catch (IOException e){
                Log.d(TAG, "writeAudioDataToFile: cannot write file " + e.toString());
                return;
            }
        }

        try{
            os.close();
        }catch (IOException e){
            Log.d(TAG, "writeAudioDataToFile: cannot close output stream.");
        }
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
                translatedText = "";
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
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
                    SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmmss");
                    currentTime.setTimeZone(cal.getTimeZone());
                    timestamp = currentTime.format(cal.getTime());
                    uploadFileToStorage(recordTitleText, dialog);
                    Toast.makeText(getActivity(), recordTitleText, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void uploadFileToStorage(final String recordTitle, final Dialog dialog){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        rawToWave();

        // to wave


        Uri uploadedFile = Uri.fromFile(new File(filenameNew));
        final StorageReference fileRef = storageRef.child(mAuth.getUid() + "/" + timestamp + ".wav");
        fileRef.putFile(uploadedFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Upload Successed.", Toast.LENGTH_SHORT).show();
//                    transRecordToText();

                    addRefToDb(recordTitle, dialog);
//                    Log.d(TAG, "onComplete: filepath: " + fileRef.getPath());
                }else {
                    Log.d(TAG, "onComplete: " + task.getException().toString());
                    Toast.makeText(getActivity(), "Upload Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!translatedText.equals("")){
            String textPath = getContext().getExternalCacheDir().getAbsolutePath();
            textPath += "/audiotrans.txt";
            File textFile = null;
            try{
                textFile = new File(textPath);
                if(!textFile.exists()){
                    textFile.createNewFile();
                }

                FileWriter writer = new FileWriter(textFile);
                writer.append(translatedText);
                writer.flush();
                writer.close();
            }catch (IOException e){
                Log.d(TAG, "uploadFileToStorage: Cannot write file");
            }

            Uri uploadedText = Uri.fromFile(new File(textPath));

            StorageReference textRef = storageRef.child(mAuth.getUid() + "/" + timestamp + ".txt");
            textRef.putFile(uploadedText).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        addRefToDb(recordTitle, dialog);
                    }
                }
            });
        }




    }

    private void rawToWave() {
//        byte buffer[] = null;
//        int TOTAL_SIZE = 0;
//        File file = new File(filename);
//        if (!file.exists()) {
//            return false;
//        }
//        TOTAL_SIZE = (int) file.length();
//        WaveHeader header = new WaveHeader();
//        header.sampleRateInHertz = 16000;
//        header.channelNum = CHANNEL_IN_CONFIG;
//        header.bitRate = 16;
        filenameNew = getContext().getExternalCacheDir().getAbsolutePath();
        filenameNew += "/audiorecord.wav";
        File waveFile = new File(filenameNew);
//        File waveFile = DirectoryOperations.createDirAndAudioFile("vocal.wav");
        File rawFile = new File(filename);
        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        }catch (IOException e){

        }
        finally {
            if(input!=null){
                try{
                    input.close();

                }catch(Exception ignore){

                }
            }
        }

        DataOutputStream output = null;
        try{
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, SAMPLE_RATE); // sample rate
            writeInt(output, SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            output.write(fullyReadFileToBytes(rawFile));

        }catch(IOException e){

        }

        finally {
            try{
                if(output!=null){
                    output.close();

                }

            }catch (Exception e){

            }
        }



    }

    private byte[] fullyReadFileToBytes(File f) {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        try (FileInputStream fis = new FileInputStream(f)) {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }catch (IOException e){

        }

        return bytes;
    }

    private void writeShort(DataOutputStream output, short value) throws IOException {
        output.write(value);
        output.write(value >> 8);
    }

    private void writeInt(DataOutputStream output, final int value) throws IOException {
        output.write(value);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeString(DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public static class WaveHeader{
        private int byteNumber;
        private int sampleRateInHertz;
        private short channelNum;
        private short bitRate;
        public WaveHeader(){};


        /**
         *
         * @param sampleRateInHertz The rate at which the recording samples audio data. Valid values are: 44100, 22050, 16000, 11025 hertz.
         * @param channelNum The type of audio channel the .PCM file uses (Mono(1) or Stereo(2))
         * @param bitRate The bit rate of the PCM file (8 or 16).
         * @throws IllegalArgumentException If any parameters are invalid.
         */
        public WaveHeader(final int sampleRateInHertz, final short channelNum, final short bitRate){
            this(sampleRateInHertz, channelNum, bitRate,-1);
        }

        /**
         *
         * @param sampleRateInHertz The rate at which the recording samples audio data. Valid values are: 44100, 22050, 16000, 11025 hertz.
         * @param channelNum The type of audio channel the .PCM file uses (Mono(1) or Stereo(2))
         * @param bitRate The bit rate of the PCM file (8 or 16).
         * @param byteNumber The number of bytes in the PCM file. -1 for the converter to find the file size for you. Anything lower than -1 not allowed.
         * @throws IllegalArgumentException If any parameters are invalid.
         */
        public WaveHeader(final int sampleRateInHertz, final short channelNum, final short bitRate,final int byteNumber){
            if (channelNum!=1 && channelNum !=2){
                throw new IllegalArgumentException("Channel number must be 1(mono) or 2(stereo)");
            }
            this.channelNum=channelNum;

            if (sampleRateInHertz!=44100 && sampleRateInHertz !=22050
                    && sampleRateInHertz != 16000 && sampleRateInHertz !=11025){
                throw new IllegalArgumentException("Invalid sample rate given");
            }
            this.sampleRateInHertz=sampleRateInHertz;

            if (bitRate !=8 && bitRate !=16){
                throw new IllegalArgumentException("Invalid bit rate (must be 8 or 16)");
            }
            this.bitRate=bitRate;

            if (byteNumber<-1){
                throw new IllegalArgumentException("Invalid number of bytes for file.");
            }
            this.byteNumber=byteNumber;
        }

        /**
         * The number of bytes the WAV header represents. -1 if it doesn't know, but will determine upon PCM conversion.
         * @return A number equal to -1 or greater.
         */
        public int getByteNumber() {
            return byteNumber;
        }

        /**
         * The rate at which the recording samples audio data.
         * @return 44100, 22050, 16000, or 11025
         */
        public int getSampleRateInHertz() {
            return sampleRateInHertz;
        }

        /**
         * The type of audio channel the .PCM file uses .
         * @return Mono(1) or Stereo(2)
         */
        public short getChannelNum() {
            return channelNum;
        }

        /**
         * bitRate The bit rate of the PCM file.
         * @return 8 or 16
         */
        public short getBitRate() {
            return bitRate;
        }
    }



    private void addRefToDb(String recordTitle, final Dialog dialog){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String audioRef = mAuth.getUid() + "/" + timestamp + ".wav";
        String textRef = mAuth.getUid() + "/" + timestamp + ".txt";
        AudioModel newRecording = null;
        if(translatedText.equals("")){
            newRecording = new AudioModel(timestamp, recordTitle, audioRef, null);
        }else {
            newRecording = new AudioModel(timestamp, recordTitle, audioRef, textRef);
        }

        db.collection("recordings").document(timestamp).set(newRecording)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Add Ref to db", Toast.LENGTH_SHORT).show();
                            translatedText = "";
                            dialog.dismiss();
                        }else {
                            Log.d(TAG, "onComplete: " + task.getException().toString());
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
