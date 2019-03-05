package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.circculate.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {


    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

}
