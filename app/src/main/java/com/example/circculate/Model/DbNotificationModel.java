package com.example.circculate.Model;

import java.util.Comparator;

public class DbNotificationModel {
    private String userIconRef;
    private String userName;
    private String content;
    private String timestamp;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public DbNotificationModel(){}

    //with img ref
    public DbNotificationModel(String userIconRef, String userName, String content,
                              String timestamp, String type){
        this.userIconRef = userIconRef;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
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


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static Comparator<DbNotificationModel> notificationComparator = new Comparator<DbNotificationModel>() {
        @Override
        public int compare(DbNotificationModel notification1, DbNotificationModel notification2) {
            long timestamp1 = Long.parseLong(notification1.getTimestamp());
            long timestamp2 = Long.parseLong(notification2.getTimestamp());

            if(timestamp1 > timestamp2){
                return -1;
            }else {
                return 1;
            }


        }
    };

}
