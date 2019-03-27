package com.example.circculate.Model;

import java.io.Serializable;

public class CommentModel implements Serializable {
    private UserModel user;
    private String content;
    private String timestamp;

    public CommentModel(UserModel user, String content, String timestamp) {
        this.user = user;
        this.content = content;
        this.timestamp = timestamp;
    }

    public CommentModel(){}

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
