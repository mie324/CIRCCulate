package com.example.circculate.Model;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private String notiTitle;
    private String notiBody;

    public NotificationModel(){
    }

    public NotificationModel(String title, String body){
        this.notiTitle = title;
        this.notiBody = body;
    }

    public void setNotiBody(String notiBody) {
        this.notiBody = notiBody;
    }

    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }

    public String getNotiBody() {
        return notiBody;
    }

    public String getNotiTitle() {
        return notiTitle;
    }
}
