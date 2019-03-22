package com.example.circculate.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {
    private String username;
    private String email;
    private int colorCode;
    private String tokenId;
    private String iconRef;
//    private ArrayList<EventModel> events;

    public UserModel(String email, String username, int colorCode, String iconRef){
        this.email = email;
        this.username = username;
        this.colorCode = colorCode;
        this.iconRef = iconRef;
    }

    public UserModel(){}

    public String getUsername(){return this.username;}

    public int getColorCode(){
        return this.colorCode;
    }

    public String getEmail(){return  this.email;}

    public void setTokenId(String tokenId){
        this.tokenId = tokenId;
    }

    public String getTokenId(){
        return this.tokenId;
    }


    public String getIconRef() {
        return iconRef;
    }

    public void setIconRef(String iconRef) {
        this.iconRef = iconRef;
    }
}
