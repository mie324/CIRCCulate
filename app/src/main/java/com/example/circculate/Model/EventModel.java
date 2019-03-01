package com.example.circculate.Model;

public class EventModel {
    private String title, timestamp, location, userName, note,userId;
    public EventModel(){}

    public EventModel(String title, String timestamp, String location, String note) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.note = note;
        userName = null;
        userId = null;
    }

//    public EventModel(String title, String timestamp, String location) {
//        this.title = title;
//        this.timestamp = timestamp;
//        this.location = location;
//        note = null;
//        userId = null;
//    }

    public EventModel(String title, String timestamp, String location, String userName, String userId, String note) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.userName = userName;
        this.userId = userId;
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
