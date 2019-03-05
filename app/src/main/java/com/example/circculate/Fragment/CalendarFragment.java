package com.example.circculate.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.circculate.Adapter.CalendarEventAdapter;
import com.example.circculate.Helper;
import com.example.circculate.HomePage;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
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
    private static final String TAG = "FragmentLifeCycle";
    private RecyclerView calenderRecycler;
    private CalendarEventAdapter mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<EventModel> events;
    private UserModel currentUser;
    private ProgressDialog progressDialog;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
        Log.d(TAG, "onCreateOptionsMenu: Calendar create option menu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        Log.d(TAG, "onCreateView: Calendar onCreateView");
//        if(getArguments() == null){
//            Log.d(TAG, "onCreateView: get argument null");
//        }
//        if(getArguments() != null){
//            currentUser = (UserModel) getArguments().getSerializable("LoggedUser");
//            Log.d(TAG, currentUser.getUsername());
//        }
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        events = new ArrayList<>();
//        setCalendarListener(root);
//        setScrollListener(root);
//        initRecyclerView(root);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() == null){
            Log.d(TAG, "onCreateView: get argument null");
        }
        if(getArguments() != null){
            currentUser = (UserModel) getArguments().getSerializable("LoggedUser");
            Log.d(TAG, currentUser.getUsername());
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<>();
        setCalendarListener();
        setScrollListener();
        initRecyclerView();
        Log.d(TAG, "onStart: Calendar on start");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Calendar on create");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Calendar on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Calendar on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        eventCalendar = null;

        Log.d(TAG, "onStop: Calendar on stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Calendar on destroy");
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
        progressDialog.hide();
    }

    private void initRecyclerView(){
        calenderRecycler = (RecyclerView)getView().findViewById(R.id.recycler_calendar_event);
        calenderRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get the event according to calendar date
        Log.d(TAG, currentUser.getUsername());
        mAdapter = new CalendarEventAdapter(getActivity(), events, currentUser);
        calenderRecycler.setAdapter(mAdapter);
        getTodayEvents();
        Log.d(TAG, "initRecyclerView: recyclerview init");
        //set on click listener

    }

    private void setCalendarListener(){
        if(eventCalendar == null){
            eventCalendar = (CalendarView) getView().findViewById(R.id.event_calendar);
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting the events...");
        Log.d(TAG, "setCalendarListener: set Calendar listener");
        eventCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                progressDialog.show();
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

    private void setScrollListener(){
        NestedScrollView scrollView = getView().findViewById(R.id.scroll_calendar);
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
