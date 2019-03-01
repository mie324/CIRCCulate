
package com.example.circculate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.circculate.Adapter.AlleventsAdapter;
import com.example.circculate.Model.EventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class YourEvents extends AppCompatActivity {
    private ArrayList<EventModel> eventList;
    private RecyclerView yourEventsRv;
    private AlleventsAdapter youreventsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<DocumentSnapshot> EventsDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_events);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        yourEventsRv = findViewById(R.id.yourEventsRv);
        yourEventsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        getYourEvents();
    }

    private void getYourEvents() {
        db.collection("events").whereEqualTo("userId", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    EventsDoc = task.getResult().getDocuments();
//                    int a = EventsDoc.size();
//                    Log.d("Num", Integer.toString(a));
                    displayEvents(EventsDoc);

                }else{

                }

            }
        });
    }

    private void displayEvents(List<DocumentSnapshot> eventsDoc) {
        eventList = new ArrayList<>();
        for(DocumentSnapshot doc:eventsDoc){
            eventList.add(doc.toObject(EventModel.class));
        }
//        Log.d("Num", Integer.toString(eventList.size()));

        youreventsAdapter = new AlleventsAdapter(this, eventList);
        yourEventsRv.setAdapter(youreventsAdapter);
    }

    public void gotoAllEventsPage(View view) {
        Intent intent = new Intent(this, AllEvents.class);
        startActivity(intent);
    }
}
