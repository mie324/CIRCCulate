package com.example.circculate;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailPage extends AppCompatActivity {
    private EventModel eventToDisplay;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView appointPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventToDisplay = (EventModel) getIntent().getSerializableExtra("clickedEvent");
        initPage();

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
            appointPerson.setText(eventToDisplay.getUserId());
        }

        TextView appointNote = findViewById(R.id.note_content);
        appointNote.setText(eventToDisplay.getNote());

        addSwitchListener();
    }

    private void addSwitchListener(){
        Switch signupSwitch = findViewById(R.id.signup_switch);
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
                            db.collection("events").document(eventToDisplay.getTimestamp())
                                    .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        appointPerson.setText("No one sign up for this event.");
                                        showToast("Cancel the sign up.");
                                    }else {
                                        showToast("Fail to cancel to sign up");
                                    }
                                }
                            });
                        }
                    }else {
                        showToast("Someone else already sign up for this event.");
                    }
                }else{
                    //check to sign up
                    if(isChecked){
                        eventToDisplay.setUserId(mAuth.getUid());
                        db.collection("events").document(eventToDisplay.getTimestamp())
                                .set(eventToDisplay).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showToast("You have signed up for this event.");
                                    appointPerson.setText(eventToDisplay.getUserId());
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
