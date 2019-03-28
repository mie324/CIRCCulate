package com.example.circculate.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class TimelineItemModel implements Serializable {
    private String userIconRef;
    private String userName;
    private String content;
    private String imgRef;
    private int listOfComment;
    private String timestamp;
    private boolean isNotification;



    public int getListOfComment() {
        return listOfComment;
    }

    public void setListOfComment(int listOfComment) {
        this.listOfComment = listOfComment;
    }

    public TimelineItemModel(){}

    //with img ref
    public TimelineItemModel(String userIconRef, String userName, String content,
                             String imgRef, String timestamp, Boolean isNotification){
        this.userIconRef = userIconRef;
        this.userName = userName;
        this.content = content;
        this.imgRef = imgRef;
        this.timestamp = timestamp;
        this.listOfComment = 0;
        this.isNotification = isNotification;
    }

    //with no img ref
    public TimelineItemModel(String userIconRef, String userName, String content,
                             String timestamp, Boolean isNotification){
        this.userIconRef = userIconRef;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.listOfComment = 0;
        this.imgRef = null;
        this.isNotification = isNotification;
    }


    public String getUserIconRef() {
        return userIconRef;
    }

    public void setUserIconRef(String userIconRef) {
        this.userIconRef = userIconRef;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }



    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static Comparator<TimelineItemModel> timelineComparator = new Comparator<TimelineItemModel>() {
        @Override
        public int compare(TimelineItemModel timeline1, TimelineItemModel timeline2) {
            long timestamp1 = Long.parseLong(timeline1.getTimestamp());
            long timestamp2 = Long.parseLong(timeline2.getTimestamp());

            if(timestamp1 > timestamp2){
                return -1;
            }else {
                return 1;
            }


        }
    };

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }
}

