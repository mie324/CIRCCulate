package com.example.circculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.circculate.Adapter.CommentAdapter;
import com.example.circculate.Adapter.TimelineAdapter;
import com.example.circculate.Model.CommentModel;
import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.utils.Helper;
import com.example.circculate.utils.SwipeItemTouchHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class commentPage extends AppCompatActivity {
    TimelineItemModel timeline;
    FirebaseStorage storage;
    static final String TAG = "commentPage";
    private View bottom_sheet;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private EditText commentCon;
    private AppCompatButton post_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private UserModel user_c;
    private String timeStamp;
    private RecyclerView rv_comment;
    private CommentAdapter commentAdapter;
    private CommentModel thisComment;
    private TextView comment_num;
    private TextView post_time;
    ItemTouchHelper itemTouchHelper;
    ArrayList<CommentModel> commentList;
    private int listSize;
//    CommentModel thisComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page);

        rv_comment = (RecyclerView) findViewById(R.id.rv_comment);
        post_time = findViewById(R.id.post_time);
        comment_num = findViewById(R.id.comment_num);
        commentList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                Bundle bundle = new Bundle();
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        timeline = (TimelineItemModel) intent.getSerializableExtra("TimeLine");
        if(timeline == null){
            Log.d(TAG, "nothing");
        }
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        commentAdapter = new CommentAdapter();
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        InitPage();
        getPreviousComments();
        rv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("rv","clicked");
            }
        });
//        rv_comment.findViewById(R.id.buttonViewOption).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                comment_num.setText(timeline.getListOfComment());
//            }
//        });
        commentCon = findViewById(R.id.comment_content);
        post_btn = (AppCompatButton)findViewById(R.id.post_btn);

        commentCon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "changed");
                post_btn.setEnabled(!charSequence.toString().trim().isEmpty());


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_btn.setEnabled(false);
                final DocumentReference docRef = db.collection("users").document(currentUser.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                Log.d("USER_ACCESS", "success" + doc.getData());
                                user_c = doc.toObject(UserModel.class);
                                updateComment(user_c);
                            }

                        }
                    }
                });

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));



    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String size = intent.getStringExtra("size");
            if(size == "0"){
                comment_num.setText("0 comments");
            }else{
                comment_num.setText(size+"comments");

            }


        }
    };

    private void getPreviousComments() {
//        ArrayList<String> com_time_list = timeline.getListOfComment();
//        if(com_time_list.size() == 0)
//            return;
        listSize = timeline.getListOfComment();
        db.collection("comments").whereEqualTo("timeline_ref", timeline.getTimestamp()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    for(DocumentSnapshot doc:docs){
                        thisComment = doc.toObject(CommentModel.class);
                        commentList.add(thisComment);
                    }
                    comment_num.setText(Integer.toString(commentList.size())+" comments");
                    Log.d("display", Integer.toString(commentList.size()));
                    Collections.sort(commentList, CommentModel.commentComparator);
                    displayComments(commentList);

                }else{

                }
            }
        });
        Log.d("display", "out " + Integer.toString(commentList.size()));


    }

    private void displayComments(ArrayList<CommentModel> commentList) {
        commentAdapter = new CommentAdapter(this, commentList);
        rv_comment.setAdapter(commentAdapter);
    }

    private void updateComment(UserModel user_c) {
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        final CommentModel newComment = new CommentModel(user_c.getIconRef(), user_c.getUsername(), commentCon.getText().toString(), timeStamp, timeline.getTimestamp());

        db.collection("timelines").document(timeline.getTimestamp()).update("listOfComment", listSize+1);
        db.collection("comments").document(newComment.getTimestamp()).set(newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    commentList.add(0, newComment);
                    commentAdapter.notifyItemInserted(0);
                    comment_num.setText(Integer.toString(commentList.size())+" comments");
                    Log.d("newcomment", "succeed");

                }

            }
        });
        Log.d("size1", Integer.toString(commentList.size()));



    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_floating, null);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        EditText commentContent = mBottomSheetDialog.findViewById(R.id.comment_content);

    }

    private void InitPage() {
        TextView usernameText, contentText, commentNumText, postTimeText;
        final ImageView commentImage, timelineImg;
        CircularImageView userIconImage;
        usernameText = findViewById(R.id.username);
        contentText = findViewById(R.id.timeline_content);
        commentNumText = findViewById(R.id.comment_num);
        postTimeText = findViewById(R.id.post_time);
        commentImage = findViewById(R.id.comment_bt);
        userIconImage = findViewById(R.id.user_icon);
        timelineImg = findViewById(R.id.time_img);
        String relativePostTime = Helper.getRelativePostTime(timeline.getTimestamp());
        postTimeText.setText(relativePostTime);
        contentText.setText(timeline.getContent());
        if(timeline.getImgRef() == null){
            ViewGroup.LayoutParams params = timelineImg.getLayoutParams();
            params.height = 0;
            timelineImg.setLayoutParams(params);
        }else {
            try {
                StorageReference imgRef = storage.getReference().child(timeline.getImgRef());
                final File localImg = File.createTempFile(timeline.getTimestamp(), "jpg");
                imgRef.getFile(localImg).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Bitmap image = BitmapFactory.decodeFile(localImg.getAbsolutePath());
                            timelineImg.setImageBitmap(image);
                        }
                    }
                });
            }catch (IOException e){
                Log.d(TAG, "onBindViewHolder: " + e.toString());
            }

        }

    }

}
