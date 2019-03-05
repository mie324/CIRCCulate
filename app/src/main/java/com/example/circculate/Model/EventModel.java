package com.example.circculate.Model;

import java.io.Serializable;
import java.util.Comparator;

public class EventModel implements Serializable{
    private String title, timestamp, location, userId, note, userName;
    private int userColorCode;
    EventModel(){}

    public EventModel(String title, String timestamp, String location, String note) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.note = note;
        this.userName = null;
        this.userId = null;
        this.userColorCode = 0;
    }

//    public EventModel(String title, String timestamp, String location) {
//        this.title = title;
//        this.timestamp = timestamp;
//        this.location = location;
//        note = null;
//        userId = null;
//    }

    public EventModel(String title, String timestamp, String location, String userName, String userId, String note,
                      int colorCode) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.userName = userName;
        this.userId = userId;
        this.note = note;
        this.userColorCode = colorCode;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserColorCode(){
        return this.userColorCode;
    }
    public void setUserColorCode(int colorCode){
        this.userColorCode = colorCode;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId(){
        return  this.userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public static Comparator<EventModel> eventComparator = new Comparator<EventModel>() {
        @Override
        public int compare(EventModel event1, EventModel event2) {

            long timestamp1 = Long.parseLong(event1.getTimestamp());

            long timestamp2 = Long.parseLong(event2.getTimestamp());
            if(timestamp1 > timestamp2){
                return 1;
            }else {
                return -1;
            }
//            return (int)(timestamp1 - timestamp2);
        }
    };


//    @Override
//    public int compare(EventModel event1, EventModel event2) {
//
//        return 0;
//    }
}
