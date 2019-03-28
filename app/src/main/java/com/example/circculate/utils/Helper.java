package com.example.circculate.utils;
//
//import android.widget.Toast;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
import android.support.annotation.NonNull;

import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import android.util.Log;

public class Helper {
    private static final String TAG = "Helper";
    private boolean addNotificationFlag = false;
    public static String getTimedate(String timestamp){
        int len = timestamp.length();
        String timedate = timestamp.substring(0,8);
        return timedate;
    }

    public static String getTime(String timestamp){
        int len = timestamp.length();
        String time = timestamp.substring(8,len);
        return time;
    }
    public static String transformMon(String monthOfYear){
        String month;
        switch (monthOfYear){
            case "01": month = "Jan"; break;

            case "02": month = "Feb"; break;

            case "03": month = "Mar"; break;

            case "04": month = "Apr"; break;

            case "05": month = "May"; break;

            case "06": month = "Jun"; break;

            case "07": month = "Jul"; break;

            case "08": month = "Aug"; break;

            case "09": month = "Sept"; break;

            case "10": month = "Oct"; break;

            case "11": month = "Nov"; break;

            case "12": month = "Dec"; break;

            default: month = "Dec"; break;

        }
        return month;
    }

    public static String transformDate(int year, int monthOfYear, int dayOfMonth){
        String month;
        switch (monthOfYear){
            case 0: month = "Jan"; break;

            case 1: month = "Feb"; break;

            case 2: month = "Mar"; break;

            case 3: month = "Apr"; break;

            case 4: month = "May"; break;

            case 5: month = "Jun"; break;

            case 6: month = "Jul"; break;

            case 7: month = "Aug"; break;

            case 8: month = "Sept"; break;

            case 9: month = "Oct"; break;

            case 10: month = "Nov"; break;

            case 11: month = "Dec"; break;

            default: month = "Dec"; break;

        }

        String day;
        if(dayOfMonth < 10){
            day = "0" + dayOfMonth;
        }else{
            day = Integer.toString(dayOfMonth);
        }
        String result = month + " " + day + ", " + year;

        return result;
    }


    public static String transformTime(int hourOfDay, int minuteOfHour){
        String hour;
        String minute;
        if(hourOfDay < 10){
            hour = "0" + Integer.toString(hourOfDay);
        }else{
            hour = Integer.toString(hourOfDay);
        }

        if(minuteOfHour < 10){
            minute = "0" + minuteOfHour;
        }else{
            minute = Integer.toString(minuteOfHour);
        }
        return hour + ": " + minute;
    }

    public static String transformTimestampDate(int year, int month, int day){
        String result = Integer.toString(year);
        int month_new = month + 1;
        if(month_new < 10){
            result = result + "0" + month_new;
        }else {
            result = result + month_new;
        }

        if(day < 10){
            result = result + "0" + day;
        }else {
            result = result + day;
        }

        return result;
    }

    public static String transformTimestampTime(int hour, int minute){
        String result;
        if(hour < 10){
            result = "0" + hour;
        }else {
            result = Integer.toString(hour);
        }

        if(minute < 10){
            result = result +"0" + minute;
        }else {
            result = result + minute;
        }

        return result;
    }

    public static String transformAppointTitle(String title, String timestamp){
        String result = timestamp.substring(8, 10) + ":" + timestamp.substring(10);
        result = result + " - " + title;


        return result;
    }

    public static String tranformAppointLoca(String location){
        return "Location: " + location;

    }

    public static String transformAppointPerson(String username){
        return "Person: " + username;
    }

    public static String transformDetailTime(String timestamp){
        return timestamp.substring(8, 10) + ":" + timestamp.substring(10)
                + " " + timestamp.substring(4, 6) + "/" + timestamp.substring(6, 8)
                + "/" + timestamp.substring(0, 4);
    }

    public static String MonthDayTime(String timestamp){
        String month, day;
        if(timestamp.charAt(4)=='0')
        {
            month = timestamp.substring(5,6);
        }else{
            month = timestamp.substring(4,6);
        }

        if(timestamp.charAt(6)=='0'){
            day = timestamp.substring(7,8);
        }else{
            day = timestamp.substring(6,8);
        }
        return month+"/"+day;
    }

    public static ArrayList<String> getAllTitles(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMdd");
        currentTime.setTimeZone(cal.getTimeZone());
        String timestamp = currentTime.format(cal.getTime());
        String min_time = timestamp + "0000";
        String max_time = timestamp + "2400";
        final ArrayList<String> titleList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<DocumentSnapshot> eventsDoc;
        db.collection("events").whereGreaterThan("timestamp", min_time).whereLessThan("timestamp",max_time).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> eventDoc = task.getResult().getDocuments();
                for(DocumentSnapshot doc:eventDoc){
                    titleList.add(doc.toObject(EventModel.class).getTitle());
                }
                Log.d(TAG, "onComplete: " + titleList.size());


            }
        });

        Log.d(TAG, "getAllTitles: " + titleList.size());
        return titleList;


    }


    public static String getCurrentTimestamp(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"));
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        currentTime.setTimeZone(cal.getTimeZone());
        String timestamp = currentTime.format(cal.getTime());
        return timestamp;
    }

    public static String getRelativePostTime(String postTime){
        String currentTime = getCurrentTimestamp();
        currentTime = currentTime.substring(0, 12);
        String postTimeShort = postTime.substring(0, 12);
        long currentTimeLong = Long.parseLong(currentTime);
        long postTimeLong = Long.parseLong(postTimeShort);
        int current_month = Integer.parseInt(currentTime.substring(4, 6));
        int current_day = Integer.parseInt(currentTime.substring(6, 8));

        if(currentTimeLong - postTimeLong >= 10000){
            int month = Integer.parseInt(postTimeShort.substring(4, 6));
            int dayOfMonth = Integer.parseInt(postTimeShort.substring(6, 8));
            int year = Integer.parseInt(postTimeShort.substring(0, 4));
            if(month == current_month){
                String result = Integer.toString(current_month-dayOfMonth)+"d";
                return result;
            }else{
                int days = (current_month - month -1) * 30 + 31 - dayOfMonth + current_day;
                return (Integer.toString(days)+"d");

            }
        }else if(currentTimeLong - postTimeLong >= 100){
                int hours = (int)(currentTimeLong - postTimeLong)/100;
                String result = hours == 1 ? "1h" : hours + "h";
                return result;
        }else {
            return "now";
        }

    }

    public static void addNotificationToDb(UserModel user, String content){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TimelineItemModel newNotification = new TimelineItemModel(user.getIconRef(),
                user.getUsername(), content, getCurrentTimestamp(), true);

//        db.collection("timelines").add(newNotification).addOnCompleteListener(
//                new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if(task.isSuccessful()){
//                            return;
//                        }else {
//                            Log.d(TAG, "onComplete: cannot add notification to db.");
//                        }
//                    }
//                }
//        );
        db.collection("timelines").document(newNotification.getTimestamp())
                .set(newNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    return;
                }else {
                    Log.d(TAG, "onComplete: cannot add notification to db.");
                }
            }
        });
    }

}
