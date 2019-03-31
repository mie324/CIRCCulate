package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.Adapter.CarePreferenceAdapter;
import com.example.circculate.Model.CarePreferenceAnswerModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarePreferenceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CarePreference";
    private ArrayList<CarePreferenceAnswerModel> carePreferences;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private CarePreferenceAdapter carePreferenceAdapter;

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
        View root = inflater.inflate(R.layout.fragment_care_preference, container, false);
        recyclerView = root.findViewById(R.id.care_pre_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        db = FirebaseFirestore.getInstance();
        getAllCarePreference();

        return root;
    }

    private void getAllCarePreference(){
        db.collection("carepreferences").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    displayCarePreferences(task.getResult().getDocuments());
                }
            }
        });
    }

    private void displayCarePreferences(List<DocumentSnapshot> carePreferenceDocs){
        carePreferences = new ArrayList<>();
        for(DocumentSnapshot doc: carePreferenceDocs){
            carePreferences.add(doc.toObject(CarePreferenceAnswerModel.class));
        }

        carePreferenceAdapter = new CarePreferenceAdapter(getActivity(), carePreferences);
        recyclerView.setAdapter(carePreferenceAdapter);
    }

}
