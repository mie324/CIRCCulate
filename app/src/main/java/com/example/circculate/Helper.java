package com.example.circculate;

import android.widget.Toast;

public class Helper {
    public static String transformDate(int year, int monthOfYear, int dayOfMonth){
        String month;
        switch (monthOfYear){
            case 0: month = "January"; break;

            case 1: month = "February"; break;

            case 2: month = "March"; break;

            case 3: month = "April"; break;

            case 4: month = "May"; break;

            case 5: month = "June"; break;

            case 6: month = "July"; break;

            case 7: month = "August"; break;

            case 8: month = "September"; break;

            case 9: month = "October"; break;

            case 10: month = "November"; break;

            case 11: month = "December"; break;

            default: month = "December"; break;

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


}
