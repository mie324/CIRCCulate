package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.circculate.Adapter.CalendarEventAdapter;
import com.example.circculate.Helper;
import com.example.circculate.HomePage;
import com.example.circculate.Model.EventModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
public class CalendarFragment extends Fragment {
    private CalendarView eventCalendar;
    private String date;
    private static final String TAG = "calendarFragment";
    private RecyclerView calenderRecycler;
    private CalendarEventAdapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<EventModel> events;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<>();
        setCalendarListener(root);
        setScrollListener(root);
        initRecyclerView(root);
        setHasOptionsMenu(true);
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

    private void initRecyclerView(View root){
        calenderRecycler = (RecyclerView)root.findViewById(R.id.recycler_calendar_event);
        calenderRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get the event according to calendar date
        mAdapter = new CalendarEventAdapter(getActivity(), events);
        calenderRecycler.setAdapter(mAdapter);
        getTodayEvents();
        Log.d(TAG, "initRecyclerView: recyclerview init");
        //set on click listener

    }

    private void setCalendarListener(View root){
        if(eventCalendar == null){
            eventCalendar = (CalendarView) root.findViewById(R.id.event_calendar);
        }

        eventCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if(view != null){
                    Log.d(TAG, "not null.");
                }
                String currentDate = Helper.transformTimestampDate(year, month, dayOfMonth);

                Toast.makeText(getActivity(), currentDate, Toast.LENGTH_SHORT).show();
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

    private void setScrollListener(View root){
        NestedScrollView scrollView = root.findViewById(R.id.scroll_calendar);
        final HomePage hostActivity = (HomePage)getActivity();

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    hostActivity.animateNavigation(false);

                }
                if (scrollY > oldScrollY) { // down
                    hostActivity.animateNavigation(true);

                }
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
