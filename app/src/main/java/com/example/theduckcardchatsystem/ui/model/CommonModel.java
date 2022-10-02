package com.example.theduckcardchatsystem.ui.model;

public class CommonModel {
    private String fullname;
    private String phone;
    private String id;
    private String uid;
    private String username;
    private String bio;
    private String photourl;
    private String state;
    private String text;
    private String type;
    private String from;
    private Object timeStamp;
    private String imageUrl;
    private String lastMessage;
    private Boolean choice = false;
    private String senderImg;


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public CommonModel() {
    }

    public CommonModel(String fullname, String phone, String id
            , String username, String bio, String photourl
            , String state, String uid, boolean choice,String senderImg) {
        this.fullname = fullname;
        this.phone = phone;
        this.id = id;
        this.username = username;
        this.bio = bio;
        this.photourl = photourl;
        this.state = state;
        this.uid = uid;
        this.choice = choice;
        this.senderImg = senderImg;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getChoice() {
        return choice;
    }

    public void setChoice(Boolean choice) {
        this.choice = choice;

    }
    public String getSenderImg() {
        return senderImg;
    }

}
