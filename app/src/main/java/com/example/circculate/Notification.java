package com.example.circculate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.circculate.Adapter.NotificationAdapter;
import com.example.circculate.Model.NotificationModel;
import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.utils.SwipeItemTouchHelper;
import com.example.circculate.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Notification extends AppCompatActivity {

    private ArrayList<String> notifications = new ArrayList<>();
    private static final String TAG = "notification";
    private RecyclerView recyclerView;
    private NotificationAdapter notifAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserModel currentUser;
    private ItemTouchHelper itemTouchHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getAllNotifications();
        initToobar();
//        initComponent();
    }

    private void initToobar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notifications.clear();
//                Intent intent = new Intent(getApplication(), HomePage.class);
//                startActivity(intent);
                finish();
            }
        });
        Tools.setSystemBarColor(this);
    }

    private void initComponent(){
//        notifications = (ArrayList<NotificationModel>) getIntent().getSerializableExtra("notifications");
        recyclerView = findViewById(R.id.noti_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifAdapter = new NotificationAdapter(this, notifications, currentUser);
        recyclerView.setAdapter(notifAdapter);

        ItemTouchHelper.Callback callback = new SwipeItemTouchHelper(notifAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void getAllNotifications(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db.collection("users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: get notification timestamps");
                            currentUser = task.getResult().toObject(UserModel.class);
                            notifications.addAll(currentUser.getListOfNotification());
                            Collections.sort(notifications, Collections.<String>reverseOrder());
                            initComponent();
                        }
                    }
                });
    }

//    private void getNotificationById(List<String> notificationIds){
//        for(String notificationId : notificationIds){
//            db.collection("timelines").document(notificationId)
//                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful()){
//                        TimelineItemModel notification = task.getResult().toObject(TimelineItemModel.class);
//                        notifications.add(notification);
//                    }
//                }
//            });
//        }
//    }
}
