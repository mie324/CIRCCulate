package com.example.circculate;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.circculate.Fragment.RecrodFragment;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailPage extends AppCompatActivity {
    private EventModel eventToDisplay;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView appointPerson;
    private UserModel currentUser;
    private boolean isTitleEditable = false;
    private boolean isLocationEditable = false;
    private boolean isNoteEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventToDisplay = (EventModel) getIntent().getSerializableExtra("clickedEvent");
        currentUser = (UserModel) getIntent().getSerializableExtra("loggedUser");
        initToolbar();
        initPage();
        FloatingActionButton fab = findViewById(R.id.micro_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.putExtra("openFragment", "record");
                startActivity(intent);
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), HomePage.class);
//                startActivity(intent);
                finish();
            }
        });

    }
    private void initPage(){
        if(eventToDisplay == null){
            return;
        }
        TextView appointTitle = findViewById(R.id.title);
        appointTitle.setText(eventToDisplay.getTitle());

        TextView appointTime = findViewById(R.id.date_and_time);
        appointTime.setText(Helper.transformDetailTime(eventToDisplay.getTimestamp()));

        TextView appointLoca = findViewById(R.id.location);
        appointLoca.setText(eventToDisplay.getLocation());

        appointPerson = findViewById(R.id.person);
        if(eventToDisplay.getUserId() == null){
            appointPerson.setText("No one sign up for this event.");
        }else {
            appointPerson.setText(eventToDisplay.getUserName());
        }

        TextView appointNote = findViewById(R.id.note_content);
        appointNote.setText(eventToDisplay.getNote());
        addTitleViewSwitcherListener();
        addLocationViewSwitcherListener();
        addNoteViewSwitcherListener();
        addSwitchListener();
    }


    private void addTitleViewSwitcherListener(){
        final ViewSwitcher titleSwitcher = findViewById(R.id.title_switcher);
        titleSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isTitleEditable){
                    isTitleEditable = true;
                    titleSwitcher.showNext();
                    ((EditText)findViewById(R.id.title_edit)).setText(((TextView)findViewById(R.id.title)).getText().toString());

                }
            }
        });

        ImageButton titleEditDone = findViewById(R.id.title_edit_bt);
        titleEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTitleEditable){
                    String newTitle = ((EditText)findViewById(R.id.title_edit)).getText().toString();
                    eventToDisplay.setTitle(newTitle);
                    db.collection("events").document(eventToDisplay.getTimestamp())
                            .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                isTitleEditable = false;
                                ((TextView)findViewById(R.id.title)).setText(eventToDisplay.getTitle());
                                titleSwitcher.showPrevious();
//                                String content = currentUser.getUsername() + " has changed event " +
//
//                                Helper.addNotificationToDb(currentUser, );
                                showToast("Update successfully.");
                            }else {
                                titleSwitcher.showPrevious();
                                showToast("Update failed.");
                            }
                        }
                    });
                }
            }
        });
    }

    private void addLocationViewSwitcherListener(){
        final ViewSwitcher locationViewSwticher = findViewById(R.id.location_switcher);
        locationViewSwticher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLocationEditable){
                    isLocationEditable = true;
                    ((EditText)findViewById(R.id.location_edit))
                            .setText(((TextView)findViewById(R.id.location)).getText().toString());
                    locationViewSwticher.showNext();
                }
            }
        });

        ImageButton locationEditButton = findViewById(R.id.location_edit_bt);
        locationEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationEditable){
                    String newLocation = ((EditText)findViewById(R.id.location_edit)).getText().toString();
                    eventToDisplay.setLocation(newLocation);
                    db.collection("events").document(eventToDisplay.getTimestamp())
                            .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                ((TextView)findViewById(R.id.location)).setText(eventToDisplay.getLocation());
                                isTitleEditable = false;
                                showToast("Update successfully.");
                                locationViewSwticher.showPrevious();
                            }else {
                                showToast("Update failed.");
                                locationViewSwticher.showPrevious();
                            }
                        }
                    });
                }
            }
        });
    }

    private void addNoteViewSwitcherListener(){
        final ViewSwitcher noteViewSwticher = findViewById(R.id.note_switcher);
        noteViewSwticher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNoteEditable){
                    isNoteEditable = true;
                    ((EditText)findViewById(R.id.note_content_edit))
                            .setText(((TextView)findViewById(R.id.note_content)).getText().toString());
                    noteViewSwticher.showNext();
                }
            }
        });

        ImageButton noteEditButton = findViewById(R.id.note_edit_bt);
        noteEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNoteEditable){
                    String newNote = ((EditText)findViewById(R.id.note_content_edit)).getText().toString();
                    eventToDisplay.setNote(newNote);
                    db.collection("events").document(eventToDisplay.getTimestamp())
                            .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                ((TextView)findViewById(R.id.note_content)).setText(eventToDisplay.getNote());
                                isNoteEditable = false;
                                showToast("Update successfully.");
                                noteViewSwticher.showPrevious();
                            }else {
                                showToast("Update failed.");
                                noteViewSwticher.showPrevious();
                            }
                        }
                    });
                }
            }
        });
    }

    private void addSwitchListener(){
        final Switch signupSwitch = findViewById(R.id.signup_switch);
        if(mAuth.getUid().equals(eventToDisplay.getUserId())){
            signupSwitch.setChecked(true);
        }
        signupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(eventToDisplay.getUserId() != null){
                    //someone already sign up
                    if(eventToDisplay.getUserId().equals(mAuth.getUid())){
                        //user sign up, user can cancel
                        if(isChecked){
                            return;
                        }else {
                            //cancel the sign up
                            eventToDisplay.setUserId(null);
                            eventToDisplay.setUserName(null);
                            db.collection("events").document(eventToDisplay.getTimestamp())
                                    .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        appointPerson.setText("No one sign up for this event.");
                                        showToast("Cancel the sign up.");
                                        Helper.addNotificationToDb(currentUser,
                                                currentUser.getUsername() + " has cancel the sign up for " + eventToDisplay.getTitle()
                                                        + " at " + eventToDisplay.getLocation(), "EventUpdated");
                                    }else {
                                        showToast("Fail to cancel the signing up");
                                    }
                                }
                            });
                        }
                    }else {
                        signupSwitch.setChecked(false);
                        showToast("Someone else already sign up for this event.");
                    }
                }else{
                    //check to sign up
                    if(isChecked){
                        eventToDisplay.setUserId(mAuth.getUid());
                        eventToDisplay.setUserName(currentUser.getUsername());
                        db.collection("events").document(eventToDisplay.getTimestamp())
                                .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showToast("You have signed up for this event.");
                                    appointPerson.setText(eventToDisplay.getUserName());
                                    Helper.addNotificationToDb(currentUser,
                                            currentUser.getUsername() + " has signed up for " + eventToDisplay.getTitle()
                                                    + " at " + eventToDisplay.getLocation(), "EventUpdated");
                                }else {
                                    showToast("Fail to sign up for the event");
                                }
                            }
                        });
                    }
                }
            }
        });
    }



    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
