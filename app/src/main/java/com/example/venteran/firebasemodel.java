package com.example.venteran;

public class firebasemodel {
    String username;
    String image;
    String uid;
    String status;
    String phone;
    int points;
    String role;

    public firebasemodel() {
    }

    public firebasemodel(String username, String image, String uid, String status, String phone, int points, String role) {
        this.username = username;
        this.image = image;
        this.uid = uid;
        this.status = status;
        this.phone = phone;
        this.points = points;
        this.role = role;
    }





    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Number getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
