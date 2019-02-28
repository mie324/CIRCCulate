package com.example.circculate.Model;

public class UserModel {
    private String username;
    private String email;

    public UserModel(String email, String username){
        this.email = email;
        this.username = username;
    }

    public UserModel(){}

    public String getUsername(){return this.username;}

    public String getEmail(){return  this.email;}
}
