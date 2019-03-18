package com.example.circculate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.circculate.Adapter.NotificationAdapter;
import com.example.circculate.Model.NotificationModel;
import com.example.circculate.utils.Tools;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    private ArrayList<NotificationModel> notifications = new ArrayList<>();
    private static final String TAG = "notification";
    private RecyclerView recyclerView;
    private NotificationAdapter notifAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initToobar();
        initComponent();
    }

    private void initToobar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifications.clear();
                Intent intent = new Intent(getApplication(), HomePage.class);
                startActivity(intent);
//                finish();
            }
        });
        Tools.setSystemBarColor(this);
    }

    private void initComponent(){
        notifications = (ArrayList<NotificationModel>) getIntent().getSerializableExtra("notifications");
        recyclerView = findViewById(R.id.noti_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notifAdapter = new NotificationAdapter(this, notifications);
        recyclerView.setAdapter(notifAdapter);
    }
}
