package com.example.circculate.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Adapter.TimelineAdapter;
import com.example.circculate.HomePage;
import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.example.circculate.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.grpc.internal.IoUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {

    private UserModel user;
    private Dialog dialog;
    private static final int LOAD_IMG = 200;
    private static final int OPEN_CAM = 201;
    private Bitmap selectedImg;
    private String imgPath;
    private FirebaseAuth mAuth;
    private ArrayList<TimelineItemModel> timelineList;
    private RecyclerView timelineRecycler;
    private TimelineAdapter timelineAdapter;
    private List<DocumentSnapshot> timelineDoc;
    private FirebaseFirestore db;
    private static final String TAG = "TimeLine";
    private static final int ONE_MB = 1024 * 1024;
    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
        user = (UserModel) getArguments().getSerializable("LoggedUser");
        View root = inflater.inflate(R.layout.fragment_nearby, container, false);
        initComponent(root);
        mAuth = FirebaseAuth.getInstance();
        return root;
    }

    private void initComponent(View root){
        //add listener
        root.findViewById(R.id.add_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPostDialog();
            }
        });
        db = FirebaseFirestore.getInstance();
        timelineRecycler = root.findViewById(R.id.timeline_recyclerview);
        timelineRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getAllTimelines();
        setRecyclerViewListener();
    }

    private void getAllTimelines(){
        db.collection("timelines").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    timelineDoc = task.getResult().getDocuments();
                    displayTimelines(timelineDoc);
                }else {
                    Log.d(TAG, "onComplete: fail to get documents. " + task.getException().toString());
                }
            }
        });
    }

    private void displayTimelines(List<DocumentSnapshot> timelineDoc){
        timelineList = new ArrayList<>();
        for(DocumentSnapshot timeline : timelineDoc){
            timelineList.add(timeline.toObject(TimelineItemModel.class));
        }

        Collections.sort(timelineList, TimelineItemModel.timelineComparator);
        timelineAdapter = new TimelineAdapter(getActivity(), timelineList);
        timelineRecycler.setAdapter(timelineAdapter);

    }

    private void showAddPostDialog(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_post);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        ((TextView)dialog.findViewById(R.id.username)).setText(user.getUsername());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference iconRef = storage.getReference().child(user.getIconRef());
        iconRef.getBytes(ONE_MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    Bitmap userIcon = BitmapFactory.decodeByteArray(task.getResult(),
                            0, task.getResult().length);
                    ((CircularImageView)dialog.findViewById(R.id.user_icon)).setImageBitmap(userIcon);
                }
            }
        });

        final AppCompatButton submitButton = (AppCompatButton) dialog.findViewById(R.id.bt_submit);
        final AppCompatButton submitButtonBottom = dialog.findViewById(R.id.bt_submit_bottom);
        ((EditText) dialog.findViewById(R.id.content)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitButton.setEnabled(!s.toString().trim().isEmpty());
                submitButtonBottom.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                submitNewTimeline();
                Toast.makeText(getActivity(), "Post Submitted", Toast.LENGTH_SHORT).show();
            }
        });

        submitButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                submitNewTimeline();
                Toast.makeText(getActivity(), "Post Submitted", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Camera Clicked", Toast.LENGTH_SHORT).show();
                openCamera();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_album)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Open Gallery Clicked", Toast.LENGTH_SHORT).show();
                openGallery();
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void openCamera(){
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(openCamera.resolveActivity(getActivity().getPackageManager()) != null){
            File imgFile = null;
            try {
                imgFile = createImageFile();
            }catch (IOException e){
                Log.d(TAG, "openCamera: ");
            }

            if(imgFile != null){
                Uri imgUri = FileProvider.getUriForFile(getActivity(), "com.example.circculate.fileprovider",
                        imgFile);
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(openCamera, OPEN_CAM);
            }
        }
    }

    private void openGallery(){
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, LOAD_IMG);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "img_file";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imgPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OPEN_CAM && resultCode == Activity.RESULT_OK){
            selectedImg = (Bitmap) BitmapFactory.decodeFile(imgPath);
//            selectedImg = (Bitmap)(data.getExtras().get("data"));
            ImageView imgHolder = dialog.findViewById(R.id.img_holder);
            imgHolder.setImageBitmap(selectedImg);
//            Log.d(TAG, "onActivityResult: " + selectedImg.getWidth());
//            Log.d(TAG, "onActivityResult: " + selectedImg.getHeight());
        }else if(requestCode == LOAD_IMG && resultCode == Activity.RESULT_OK){
            try {
                Uri imgUri = data.getData();
                InputStream imgStream = getActivity().getContentResolver().openInputStream(imgUri);
                selectedImg = BitmapFactory.decodeStream(imgStream);
                ImageView imgHolder = dialog.findViewById(R.id.img_holder);
                imgHolder.setImageBitmap(selectedImg);
            }catch (IOException e){
                Log.d(TAG, "open gallery fail " + e.toString());
            }
        }
    }


    private void submitNewTimeline(){
        final String timestamp = Helper.getCurrentTimestamp();
        final String content = ((EditText)dialog.findViewById(R.id.content)).getText().toString();
        if(selectedImg != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference();
            final String imgRef = mAuth.getUid() + "/" + timestamp + ".jpg";
            StorageReference childRef = ref.child(imgRef);
            try {
                InputStream stream = new FileInputStream(new File(imgPath));
                childRef.putStream(stream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            TimelineItemModel newTimeline = new TimelineItemModel(user.getIconRef(),
                                    user.getUsername(), content, imgRef, timestamp, false, "photo");
                            addTimelineToDb(newTimeline);
                        }
                    }
                });
            }catch (IOException e){
                Log.d(TAG, "submitNewTimeline: cannot open input stream. " + e.toString());
            }


        }else {
            TimelineItemModel newTimeline = new TimelineItemModel(user.getIconRef(), user.getUsername(),
                    content, timestamp, false, "note");
            addTimelineToDb(newTimeline);
        }
    }

    private void addTimelineToDb(final TimelineItemModel newTimeline){
        db.collection("timelines").document(newTimeline.getTimestamp()).set(newTimeline)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            timelineList.add(0, newTimeline);
                            timelineAdapter.notifyItemChanged(0);
                            Log.d(TAG, "onComplete: upload to db");
                            Toast.makeText(getActivity(), "succeed add to db", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.d(TAG, "onComplete: " + task.getException().toString());
                            Toast.makeText(getActivity(), "upload fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setRecyclerViewListener(){
        final HomePage hostActivity = (HomePage)getActivity();
        timelineRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) { // up
                    hostActivity.animateNavigation(false);

                }
                if (dy > 0) { // down
                    hostActivity.animateNavigation(true);

                }
            }
        });
    }
}
