package com.example.venteran;

public class firebasemodel {
    String username;
    String image;
    String uid;
    String status;

    public firebasemodel() {
    }

    public firebasemodel(String username, String image, String uid, String status) {
        this.username = username;
        this.image = image;
        this.uid = uid;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
