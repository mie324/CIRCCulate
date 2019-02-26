package com.example.circculate;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    private CalendarView eventCalendar;
    private String date;
    private static final String TAG = "select";
    private Button detailBtn;

    public FavoritesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onStart() {
        super.onStart();
        if(eventCalendar == null){
            eventCalendar = (CalendarView) getView().findViewById(R.id.event_calendar);
        }

        eventCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if(view != null){
                    Log.d(TAG, "not null.");
                }
                month += 1;
                String currentDate = Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(dayOfMonth);
                if(!currentDate.equals(date)){
                    date = currentDate;
                    String msg = "You select " + month + "/" + dayOfMonth + "in year " + year;
                    Log.d(TAG, "onSelectedDayChange: " + msg);
                    showToast(msg);
                }
            }
        });

        FloatingActionButton addEventButton = getView().findViewById(R.id.fab_add_event);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEvent.class);
                startActivity(intent);
                showToast("Click button");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        detailBtn = (Button)root.findViewById(R.id.detail_button);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailPage.class);
                startActivity(intent);

            }
        });
        // Inflate the layout for this fragment
        return root;
    }


    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
