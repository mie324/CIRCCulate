package com.example.circculate.Model;

import java.util.ArrayList;

public class UserModel {
    private String username;
    private String email;
//    private ArrayList<EventModel> events;

    public UserModel(String email, String username){
        this.email = email;
        this.username = username;
    }

    public UserModel(){}

    public String getUsername(){return this.username;}

    public String getEmail(){return  this.email;}
}
