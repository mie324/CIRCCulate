package com.example.circculate;

public class EventModel {
    private String title, timestamp, location, userId, note, date;
    EventModel(){}

    public EventModel(String title, String timestamp, String location, String note, String date) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.note = note;
        this.date = date;
        userId = null;
    }

    public EventModel(String title, String timestamp, String location, String date) {
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        this.date = date;
        note = null;
        userId = null;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
