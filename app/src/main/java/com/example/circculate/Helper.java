package com.example.circculate;

public class Helper {
    public static String transformDate(int year, int monthOfYear, int datOfMonth){
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

        String result = month + " " + datOfMonth + ", " + year + ".";

        return result;
    }


    public static String transformTime(int hourOfDate, int minute){
        return hourOfDate + ": " + minute;
    }
}
