package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.Adapter.AlleventsAdapter;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class AllEventFragment extends Fragment {

    private ArrayList<EventModel> eventList;
    private RecyclerView allEventsRv;
    private AlleventsAdapter alleventsAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private UserModel currentUser;
    private List<DocumentSnapshot> EventsDoc;

    public AllEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_all_event, container, false);
        if(getArguments() != null){
            currentUser = (UserModel)getArguments().getSerializable("LoggedUser");
        }
        //init recycler view
        allEventsRv = root.findViewById(R.id.allEventsRv);
        allEventsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));

        //firebase set up
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        getAllEvents();

        return root;
    }


    private void getAllEvents() {
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        Log.d("Num", Integer.toString(eventList.size()));
        Collections.sort(eventList, EventModel.eventComparator);
        alleventsAdapter = new AlleventsAdapter(getActivity(), eventList, currentUser);
        allEventsRv.setAdapter(alleventsAdapter);
    }
}
