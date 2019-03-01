package com.example.circculate;

import android.widget.Toast;

public class Helper {
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


}
