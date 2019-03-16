package com.example.circculate.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.circculate.Adapter.LibraryAdapter;
import com.example.circculate.Model.AudioModel;
import com.example.circculate.Model.RecordingModel;
import com.example.circculate.R;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFrament extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView libraryRv;
    private List<AudioModel> audioList;
    private FirebaseFirestore db;
    StorageReference storageReference;
    private static final String TAG = "LibraryLifeCycle";
    private LibraryAdapter libraryAdapter;


    public LibraryFrament() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Library Fragment", Toast.LENGTH_SHORT).show();
        getRecordings();
    }

    private void getRecordings() {
        audioList = new ArrayList<AudioModel>();
        audioList.add(new AudioModel("201903172100", "Test", "url","url"));
        libraryAdapter = new LibraryAdapter(getActivity(), audioList);
        libraryRv.setAdapter(libraryAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        libraryRv = root.findViewById(R.id.recyclerView_library);
        libraryRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false));
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // Inflate the layout for this fragment
        return root;

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

}
