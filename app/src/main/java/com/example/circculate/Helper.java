package com.example.circculate;

public class Helper {
    public static String transformDate(int year, int monthOfYear, int dayOfMonth){
        String month;
        switch (monthOfYear){
            case 1: month = "January"; break;

            case 2: month = "February"; break;

            case 3: month = "March"; break;

            case 4: month = "April"; break;

            case 5: month = "May"; break;

            case 6: month = "June"; break;

            case 7: month = "July"; break;

            case 8: month = "August"; break;

            case 9: month = "September"; break;

            case 10: month = "October"; break;

            case 11: month = "November"; break;

            case 12: month = "December"; break;

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
}
