package com.example.circculate.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.circculate.Adapter.CalendarEventAdapter;
import com.example.circculate.Helper;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    private CalendarView eventCalendar;
    private String date;
    private static final String TAG = "select";
    private RecyclerView calenderRecycler;
    private CalendarEventAdapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<EventModel> events;
    private UserModel user;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            user = (UserModel) getArguments().getSerializable("LoggedUser");
//            Log.d("username", user.getUsername());
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<>();
        setCalendarListener();
        initRecyclerView();
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

//        detailBtn = (Button)root.findViewById(R.id.detail_button);
//        detailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), DetailPage.class);
//                startActivity(intent);
//
//            }
//        });

        // Inflate the layout for this fragment
        return root;
    }

    private void getTodayEvents(){
        Log.d(TAG, "getTodayEvents: get today's event");
        Date currentDate = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto")).getTime();
        String currentDateString = Helper.transformTimestampDate(currentDate.getYear() + 1900, currentDate.getMonth(),
                currentDate.getDate());
        String minTime = currentDateString + "0000";
        String maxTime = currentDateString + "2359";
        Log.d(TAG, "getTodayEvents: " + currentDateString);

        db.collection("events").whereGreaterThan("timestamp", minTime)
                .whereLessThan("timestamp", maxTime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    events.clear();
                    tranDocs(task.getResult().getDocuments());
                }else {
                    showToast(task.getException().toString());
                }
            }
        });


    }

    private void tranDocs(List<DocumentSnapshot> documents){
        for(DocumentSnapshot document: documents){
            events.add(document.toObject(EventModel.class));
        }
        Collections.sort(events, EventModel.eventComparator);
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        calenderRecycler = (RecyclerView)getView().findViewById(R.id.recycler_calendar_event);
        calenderRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get the event according to calendar date
        mAdapter = new CalendarEventAdapter(getActivity(), events, user);
        calenderRecycler.setAdapter(mAdapter);
        getTodayEvents();
        Log.d(TAG, "initRecyclerView: recyclerview init");
        //set on click listener

    }

    private void setCalendarListener(){
        if(eventCalendar == null){
            eventCalendar = (CalendarView) getView().findViewById(R.id.event_calendar);
        }

        eventCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if(view != null){
                    Log.d(TAG, "not null.");
                }
                String currentDate = Helper.transformTimestampDate(year, month, dayOfMonth);
                if(!currentDate.equals(date)){
                    date = currentDate;
                    String minTime = currentDate + "0000";
                    String maxTime = currentDate + "2359";
                    db.collection("events").whereGreaterThan("timestamp", minTime)
                            .whereLessThan("timestamp", maxTime).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                events.clear();
                                tranDocs(task.getResult().getDocuments());
                            }else {
                                showToast(task.getException().toString());
                            }
                        }
                    });
                }
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
