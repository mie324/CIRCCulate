package com.example.circculate.Fragment;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.example.circculate.HomePage;
import com.example.circculate.Model.AudioModel;
import com.example.circculate.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
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
    private List<DocumentSnapshot> recordingDoc;
    private MediaPlayer player;
    private Handler mHandler;

    public LibraryFrament() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Library Fragment", Toast.LENGTH_SHORT).show();
        getRecordings();
    }

    @Override
    public void onStart() {
        super.onStart();

        setRecycerViewListener();
        Log.d(TAG, "onStart: All event on start");
    }

    private void setRecycerViewListener() {
        RecyclerView rv = getView().findViewById(R.id.recyclerView_library);
        final HomePage hostActivity = (HomePage)getActivity();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) { // up
                    hostActivity.animateNavigation(false);

                }
                if (dy > 0) { // down
                    hostActivity.animateNavigation(true);

                }
            }
        });
    }

    private void getRecordings() {
        audioList = new ArrayList<AudioModel>();
        player = new MediaPlayer();
        try{
            player.reset();
            player.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/DAWcN2mG7GZ37bI3Zu6r86rMCGa2%2Fdo_not_delete.wav?alt=media&token=86d96505-af66-40a5-ab04-28aa87128808");
            player.prepare();
        }catch (IOException e){

        }
//        player.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/DAWcN2mG7GZ37bI3Zu6r86rMCGa2%2Fdo_not_delete.wav?alt=media&token=86d96505-af66-40a5-ab04-28aa87128808");
        db.collection("recordings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    recordingDoc = task.getResult().getDocuments();
                    for(DocumentSnapshot doc:recordingDoc){
                        audioList.add(doc.toObject(AudioModel.class));
                    }
                    Collections.sort(audioList, AudioModel.audioComparator);
                    libraryAdapter = new LibraryAdapter(getActivity(), audioList, player);
                    libraryRv.setAdapter(libraryAdapter);


                }else{

                }

            }
        });
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

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Calendar on destroy");
    }


}
