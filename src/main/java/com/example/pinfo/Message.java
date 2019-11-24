package com.example.pinfo;

import java.io.Serializable;

public class Message implements Serializable {
    private double latitude;
    private double longitude;
    private String post_title;
    private String post_text;
    private String firebase_autogenID;
    private String owner_id;
    private String state;
    private String city;
    private String postalCode;
    private String address;
    private String msg_id;
    private String nick;

    public String getmsg_id() {
        return msg_id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setFirebase_autogenID(String firebase_autogenID) {
        this.firebase_autogenID = firebase_autogenID;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setmsg_id(String msgId) {
        this.msg_id = msgId;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirebase_autogenID() {
        return firebase_autogenID;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPost_text() {
        return post_text;
    }

    public String getPost_title() {
        return post_title;
    }

    public String getOwner_id() {
        return owner_id;
    }
    public  Message(String post_text, String post_title){
        this.post_text=post_text;
        this.post_title=post_title;
    }

    //post text
    public  Message(){}

    public Message(double latitude, double longitude, String post_title, String post_text, String firebase_autogenID, String owner_id, String state, String city, String postalCode, String address, String msg_id, String nick) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.post_title = post_title;
        this.post_text = post_text;
        this.firebase_autogenID = firebase_autogenID;
        this.owner_id = owner_id;
        this.state = state;
        this.city = city;
        this.postalCode = postalCode;
        this.address = address;
        this.msg_id = msg_id;
        this.nick = nick;
    }

//    public Message(double latitude, double longitude, String post_title, String post_text,String firebase_autogenID, String owner_id, String state, String city, String postalCode, String address) {
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.post_title = post_title;
//        this.post_text = post_text;
//        this.firebase_autogenID = firebase_autogenID;
//        this.owner_id = owner_id;
//        this.state = state;
//        this.city = city;
//        this.postalCode=postalCode;
//        this.address=address;
//    }

}

