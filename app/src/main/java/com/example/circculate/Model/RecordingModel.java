package com.example.circculate.Model;

import java.io.Serializable;

public class RecordingModel implements Serializable {
    private String timestamp;
    private String title;
    private String audioRef;
    private String textRef;
    public boolean expanded = false;
    public boolean shouldBeExpanded = false;

    public boolean isShouldBeExpanded() {
        return shouldBeExpanded;
    }

    public void setShouldBeExpanded(boolean shouldBeExpanded) {
        this.shouldBeExpanded = shouldBeExpanded;
    }

    public RecordingModel(String timestamp, String title, String audioRef, String textRef) {
        this.timestamp = timestamp;
        this.title = title;
        this.audioRef = audioRef;
        this.textRef = textRef;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudioRef() {
        return audioRef;
    }

    public void setAudioRef(String audioRef) {
        this.audioRef = audioRef;
    }

    public String getTextRef() {
        return textRef;
    }

    public void setTextRef(String textRef) {
        this.textRef = textRef;
    }
}
