package com.example.mobileproj;

import com.google.firebase.Timestamp;

public class note {
    private String text;
    private boolean completed;
    private Timestamp created;
    private String userId;

    public note(){
    }

    public note(String t, boolean c, Timestamp d, String u){
        text = t;
        completed= c;
        created = d;
        userId = u;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "note{" +
                "text='" + text + '\'' +
                ", completed=" + completed +
                ", created=" + created +
                ", userId='" + userId + '\'' +
                '}';
    }
}
