package com.example.tictactoegame.models;

public class User {
    private String uid,displayName,photoUrl;

     public User(String uid, String displayName, String photoUrl){
        this.uid=uid;
        this.displayName=displayName;
        this.photoUrl=photoUrl;
     }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
