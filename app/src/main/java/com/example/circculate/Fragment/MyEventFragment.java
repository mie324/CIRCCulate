package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.Adapter.AlleventsAdapter;
import com.example.circculate.Adapter.yourEventsAdapter;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventFragment extends Fragment {

    private ArrayList<EventModel> eventList;
    private RecyclerView myEventsRv;
    private yourEventsAdapter myEventsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "FragmentLifeCycle";
    private List<DocumentSnapshot> EventsDoc;
    private UserModel currentUser;
    public MyEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_event, container, false);
        currentUser = (UserModel) getArguments().getSerializable("LoggedUser");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myEventsRv = root.findViewById(R.id.yourEventsRv);
        myEventsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
        Log.d(TAG, "onCreateOptionsMenu: my event create option menu");
        getYourEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: my event on resume");
//        getYourEvents();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: my event on start");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: my event on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: my event on stop");
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
        Collections.sort(eventList, EventModel.eventComparator);
//        Log.d("Num", Integer.toString(eventList.size()));

        myEventsAdapter = new yourEventsAdapter(getActivity(), eventList, currentUser);
        myEventsRv.setAdapter(myEventsAdapter);
    }

}
