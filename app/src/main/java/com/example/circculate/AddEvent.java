package com.example.circculate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import java.util.Calendar;



public class AddEvent extends AppCompatActivity {
    private AppCompatEditText appointDate;
    private AppCompatEditText appointTime;
    private AppCompatCheckBox checkmark;
    private String title, timestamp, location, note, timestamp_date, timestamp_time;
    private boolean checkBoxFlag = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        appointTime = findViewById(R.id.time_pick_result);
        appointDate = findViewById(R.id.date_pick_result);
        checkmark = findViewById(R.id.check_box);
        initToolbar();
        addPickerListener();
        addCheckBoxListner();
    }

    private void addCheckBoxListner() {
        checkmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxFlag = isChecked;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            //submit the event and return
            if(isValidInput()){
//                dialog.setMessage("Creating new event.");
//                dialog.setCancelable(false);
//                dialog.show();
                //do something
                addEventToDb();
                //to homepage
            };
            
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEventToDb() {
        EventModel newEvent;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progressdialog);
        builder.setTitle("Adding event");
        builder.show();



        if(checkBoxFlag){
            mAuth = FirebaseAuth.getInstance();
            newEvent = new EventModel(this.title, this.timestamp, this.location, mAuth.getCurrentUser().getUid(),this.note);
        }else {
            newEvent = new EventModel(this.title, this.timestamp, this.location, this.note);
        }
        db = FirebaseFirestore.getInstance();
        db.collection("events").document(this.timestamp).set(newEvent)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showToast("Create a new event.");
                            Intent intent = new Intent(getApplicationContext(), HomePage.class);
                            startActivity(intent);
//                            appointDate.setText(null);
                        }else{
                            showToast("Fail to create event.");
                        }
                    }
                });


//        dialog.hide();


    }

    private boolean isValidInput() {
        this.timestamp = null;
        AppCompatEditText title, location, note;
        title = findViewById(R.id.appoint_title);
        location = findViewById(R.id.appoint_location);
        note = findViewById(R.id.appoint_note);
        if(TextUtils.isEmpty(title.getText().toString())){
            title.setError("Required");
            return false;
        }else {
            this.title = title.getText().toString();
        }

        if(TextUtils.isEmpty(location.getText().toString())){
            location.setError("Required");
            return false;
        }else{
            this.location = location.getText().toString();
        }

        if(TextUtils.isEmpty(appointDate.getText().toString())){
            appointDate.setError("Required");
            return false;
        }else {
            timestamp = timestamp_date;
        }

        if(TextUtils.isEmpty(appointTime.getText().toString())){
            appointTime.setError("Required");
            return false;
        }else {
            timestamp = timestamp + timestamp_time;
        }

        if(TextUtils.isEmpty(note.getText().toString())){
            this.note = "No notes added.";
        }else {
            this.note = note.getText().toString();
        }

        return true;

    }
    

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void addPickerListener(){
        ((Button)findViewById(R.id.date_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePicker((Button) view);
            }
        });

        ((Button)findViewById(R.id.time_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimePicker((Button) view);
            }
        });
        if(appointDate == null){
            appointDate = findViewById(R.id.date_pick_result);

        }
        appointDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePicker((Button)findViewById(R.id.date_picker));
            }
        });
        if(appointTime == null){
            appointTime = findViewById(R.id.time_pick_result);
        }
        appointTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimePicker((Button)findViewById(R.id.time_picker));
            }
        });
    }

    private void dialogDatePicker(Button datePickerBt){
        Calendar cur_calendar = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.YEAR, year);
//                        calendar.set(Calendar.MONTH, monthOfYear);
//                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        long dateMillis =calendar.getTimeInMillis();
                        String selectDate = Helper.transformDate(year, monthOfYear, dayOfMonth);
                        timestamp_date = Helper.transformTimestampDate(year, monthOfYear, dayOfMonth);
                        if(appointDate == null){
                            appointDate = findViewById(R.id.date_pick_result);

                        }
                        appointDate.setText(selectDate);
                    }
                }, cur_calendar.get(Calendar.YEAR), cur_calendar.get(Calendar.MONTH),cur_calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calendar);
        datePicker.show(getFragmentManager(), "DatePickerDialog");
    }

    private void dialogTimePicker(Button timePickerBt){
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String selectTime = Helper.transformTime(hourOfDay, minute);
                timestamp_time = Helper.transformTimestampTime(hourOfDay, minute);
                if(appointTime == null){
                    appointTime = findViewById(R.id.time_pick_result);

                }
                appointTime.setText(selectTime);
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Timepickerdialog");
    }


    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
