package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarePreferenceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CarePreference";

    public CarePreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_care_preference, container, false);
    }

}
