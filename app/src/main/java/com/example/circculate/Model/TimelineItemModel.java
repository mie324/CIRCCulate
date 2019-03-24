package com.example.circculate.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class TimelineItemModel implements Serializable {
    private String userIconRef;
    private String userName;
    private String content;
    private String imgRef;
    private ArrayList<String> listOfComment;
    private String timestamp;

    public TimelineItemModel(){}

    //with img ref
    public TimelineItemModel(String userIconRef, String userName, String content,
                             String imgRef, String timestamp){
        this.userIconRef = userIconRef;
        this.userName = userName;
        this.content = content;
        this.imgRef = imgRef;
        this.timestamp = timestamp;
        this.listOfComment = new ArrayList<>();
    }

    //with no img ref
    public TimelineItemModel(String userIconRef, String userName, String content,
                             String timestamp){
        this.userIconRef = userIconRef;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.listOfComment = new ArrayList<>();
        this.imgRef = null;
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

    public ArrayList<String> getListOfComment() {
        return listOfComment;
    }

    public void setListOfComment(ArrayList<String> listOfComment) {
        this.listOfComment = listOfComment;
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
}
