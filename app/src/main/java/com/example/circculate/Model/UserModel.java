package com.example.circculate.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {
    private String username;
    private String email;
    private int colorCode;
//    private ArrayList<EventModel> events;

    public UserModel(String email, String username, int colorCode){
        this.email = email;
        this.username = username;
        this.colorCode = colorCode;
    }

    public UserModel(){}

    public String getUsername(){return this.username;}

    public int getColorCode(){
        return this.colorCode;
    }

    public String getEmail(){return  this.email;}
}
