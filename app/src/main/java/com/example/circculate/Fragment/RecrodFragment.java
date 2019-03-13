package com.example.circculate.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecrodFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private boolean recordFlag = false;
    private Handler updateTimeTaskHandler = new Handler();
    private boolean startRecordFlag = false;
    private long startTime = 0;
    private long beforePauseTime = 0;
    private static final String TAG = "RECORDER";


    public RecrodFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Recording Fragment.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recrod, container, false);
        initComponent(root);


        return root;
    }

    private void initComponent(final View root){
        final FloatingActionButton recordButton = root.findViewById(R.id.record_bt);
        FloatingActionButton stopButton = root.findViewById(R.id.stop_bt);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!startRecordFlag){
                    //start recording
                    ((TextView)root.findViewById(R.id.recording_time)).setText("00 : 00");
                    startTime = SystemClock.uptimeMillis();
//                    Log.d(TAG, "onClick: " + startTime);
                    startRecordFlag = true;
                    recordFlag = true;
                    recordButton.setImageResource(R.drawable.ic_pause_white_large);
                    updateTimeTaskHandler.post(updateRecordTimeTask);
                }else {
                    // already in recording.
                    if(!recordFlag){
                        // click record, start recording.
                        recordFlag = true;
                        startTime = SystemClock.uptimeMillis();
                        updateTimeTaskHandler.post(updateRecordTimeTask);
                        recordButton.setImageResource(R.drawable.ic_pause_white_large);
                    }else {
                        // click pause, pause recording.
                        recordFlag = false;
                        beforePauseTime += SystemClock.uptimeMillis() - startTime;
                        startTime = 0;
                        updateTimeTaskHandler.removeCallbacks(updateRecordTimeTask);
                        recordButton.setImageResource(R.drawable.ic_mic_white_large);
                    }
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startRecordFlag){
                    Toast.makeText(getActivity(), "Fragment pop up for saving recording", Toast.LENGTH_SHORT).show();
                    startRecordFlag = false;
                    beforePauseTime = 0;
                    recordButton.setImageResource(R.drawable.ic_mic_white_large);
                    updateTimeTaskHandler.removeCallbacks(updateRecordTimeTask);
                }else {
                    Toast.makeText(getActivity(), "You haven't started recording yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Runnable updateRecordTimeTask = new Runnable() {
        @Override
        public void run() {
            updateRecordTime();

            updateTimeTaskHandler.postDelayed(this, 200);

        }
    };

    private void updateRecordTime(){
        long currentRecordTime = SystemClock.uptimeMillis() - startTime;
        TextView recordTime = getView().findViewById(R.id.recording_time);
//        Log.d(TAG, "updateRecordTime: " + beforePauseTime);
//        Log.d(TAG, "updateRecordTime: " + currentRecordTime);
        currentRecordTime += beforePauseTime;
        int sec = (int)(currentRecordTime / 1000);
        int min = sec / 60;
        sec %= 60;

        recordTime.setText(String.format("%02d", min) + " : " + String.format("%02d", sec));
    }

}
