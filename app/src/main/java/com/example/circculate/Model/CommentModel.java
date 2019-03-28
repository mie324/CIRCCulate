package com.example.circculate.Model;

import java.io.Serializable;
import java.util.Comparator;

public class CommentModel implements Serializable{
    private String userPhoto;
    private String userName;
    private String content;
    private String timestamp;
    private String timeline_ref;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CommentModel(String userPhoto, String userName, String content, String timestamp, String timeline_ref) {
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.timeline_ref = timeline_ref;
    }

    public String getTimeline_ref() {
        return timeline_ref;
    }

    public void setTimeline_ref(String timeline_ref) {
        this.timeline_ref = timeline_ref;
    }

    public CommentModel(){}



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Comparator<CommentModel> commentComparator = new Comparator<CommentModel>() {
        @Override
        public int compare(CommentModel c1, CommentModel c2) {
            long timestamp1 = Long.parseLong(c1.getTimestamp());
            long timestamp2 = Long.parseLong(c2.getTimestamp());
            if(timestamp1 > timestamp2){
                return -1;
            }else {
                return 1;
            }

        }
    };


}
