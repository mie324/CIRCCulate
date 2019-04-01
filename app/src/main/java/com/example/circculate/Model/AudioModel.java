package com.example.circculate.Model;

import java.io.Serializable;
import java.util.Comparator;

public class AudioModel implements Serializable {
    private String timestamp;
    private String title;
    private String audioRef;
    private String textRef;
    public boolean expanded = false;



    public AudioModel(){

    }

    public AudioModel(String timestamp, String title, String audioRef, String textRef) {
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

    public static Comparator<AudioModel> audioComparator = (new Comparator<AudioModel>() {
        @Override
        public int compare(AudioModel t1, AudioModel t2) {
            long timestamp1 = Long.parseLong(t1.getTimestamp());

            long timestamp2 = Long.parseLong(t2.getTimestamp());
            if(timestamp1 > timestamp2){
                return -1;
            }else {
                return 1;
            }
        }
    });
}
