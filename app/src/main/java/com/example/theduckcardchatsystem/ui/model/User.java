package com.example.theduckcardchatsystem.ui.model;

public class User {

    private String fullname ="Full Name";
    private String phone;
    private String uid;
    private String username = "UserName";
    private String bio = "bio";
    private String photourl;
    private String state;

    public User() {
    }

    public User(String fullname, String phone, String uid
            , String username, String bio, String photourl, String state) {
        this.fullname = fullname;
        this.phone = phone;
        this.uid = uid;
        this.username = username;
        this.bio = bio;
        this.photourl = photourl;
        this.state = state;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
