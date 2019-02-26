package com.example.circculate;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;



public class AddEvent extends AppCompatActivity {
    AppCompatEditText appointDate;
    AppCompatEditText appointTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        addPickerListener();
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

}
