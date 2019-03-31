package com.example.circculate.Model;

import java.io.Serializable;

public class CarePreferenceAnswerModel implements Serializable {
    private String userId;
    private String username;
    private String answer0;
    private String answer1;
    private String answer3;
    private String answer4;
    private String answer5;
    private String answer2;

    public CarePreferenceAnswerModel(){}

    public CarePreferenceAnswerModel(String id, String username, String[] answers){
        this.userId = id;
        this.username = username;
        this.answer0 = answers[0];
        this.answer1 = answers[1];
        this.answer2 = answers[2];
        this.answer3 = answers[3];
        this.answer4 = answers[4];
        this.answer5 = answers[5];

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnswer0() {
        return answer0;
    }

    public void setAnswer0(String answer0) {
        this.answer0 = answer0;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public void setAnswer5(String answer5) {
        this.answer5 = answer5;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }
}
