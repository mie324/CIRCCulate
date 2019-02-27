package com.example.circculate;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;



public class AddEvent extends AppCompatActivity {
    AppCompatEditText appointDate;
    AppCompatEditText appointTime;
    AppCompatCheckBox checkmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        appointTime = findViewById(R.id.time_pick_result);
        appointDate = findViewById(R.id.date_pick_result);
        checkmark = findViewById(R.id.check_box);
        initToolbar();
        addPickerListener();
        addCheckmarkListner();
    }

    private void addCheckmarkListner() {
        checkmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
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
                //do something
                addEventToDb();
                //to homepage
                Intent intent = new Intent(this, HomePage.class);
                showToast("Done");
                startActivity(intent);
            };
            
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEventToDb() {
    }

    private boolean isValidInput() {
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
