package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.circculate.Adapter.CarePreferenceAdapter;
import com.example.circculate.Model.CarePreferenceAnswerModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private static final String PATIENT_ID = "rUSzZ8NTnthxkk36ItXBRGWRkRr2";
//    private ArrayList<CarePreferenceAnswerModel> carePreferences;
//    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private CarePreferenceAnswerModel patientPreference;
    private CarePreferenceAdapter preAdapter;
    private ArrayList<String> answers;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private String username;
    private static final int MAX_ANSWERS = 6;

//    private CarePreferenceAdapter carePreferenceAdapter;

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
//        recyclerView = root.findViewById(R.id.care_pre_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.pre_recycler);
        getPatientCarePreference(root);

        return root;
    }

    private void getPatientCarePreference(final View root){
        db.collection("carepreferences").document(PATIENT_ID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            patientPreference = task.getResult().toObject(CarePreferenceAnswerModel.class);

                            setupRecyclerView(root);
                        }
                    }
                });
    }

    private void setupRecyclerView(View root){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        answers = new ArrayList<>();
        answers.add(patientPreference.getAnswer0());
        answers.add(patientPreference.getAnswer1());
        answers.add(patientPreference.getAnswer2());
        answers.add(patientPreference.getAnswer3());
        answers.add(patientPreference.getAnswer4());
        answers.add(patientPreference.getAnswer5());
        db.collection("users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            UserModel user = task.getResult().toObject(UserModel.class);
                            username = user.getUsername();
                            preAdapter = new CarePreferenceAdapter(getActivity(), answers, username);
                            recyclerView.setAdapter(preAdapter);
                        }
                    }
                });
//        preAdapter = new CarePreferenceAdapter(getActivity(), answers);
//        recyclerView.setAdapter(preAdapter);

    }



//    private void getAllCarePreference(){
//        db.collection("carepreferences").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    displayCarePreferences(task.getResult().getDocuments());
//                }
//            }
//        });
//    }
//
//    private void displayCarePreferences(List<DocumentSnapshot> carePreferenceDocs){
//        carePreferences = new ArrayList<>();
//        for(DocumentSnapshot doc: carePreferenceDocs){
//            carePreferences.add(doc.toObject(CarePreferenceAnswerModel.class));
//        }
//
//        carePreferenceAdapter = new CarePreferenceAdapter(getActivity(), carePreferences);
//        recyclerView.setAdapter(carePreferenceAdapter);
//    }

}
