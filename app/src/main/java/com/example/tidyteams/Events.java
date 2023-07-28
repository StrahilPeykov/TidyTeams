package com.example.tidyteams;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Events {
    public String catchphrase, date, description, postimage, time, title, uid, postID;
    public String country, region, postcode, number, street;
    private double latitude, longitude;

    // No-argument constructor
    public Events() {
    }

    public Events(String title1, String date1, String time1, String country1, String region1,
                  String postcode1, String street1, String number1, String image1) {

    }

    public Events(String catchphrase, String date, String description, String place,
                  String postimage, String time, String title, String uid, String postID,
                  double latitude, double longitude, String country, String region,
                  String postcode, String number, String street) {
        this.catchphrase = catchphrase;
        this.date = date;
        this.description = description;
        this.country = country;
        this.region = region;
        this.postcode = postcode;
        this.number = number;
        this.street = street;
        this.postimage = postimage;
        this.time = time;
        this.title = title;
        this.uid = uid;
        this.postID = postID;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getCatchphrase() {
        return catchphrase;
    }

    public void setCatchphrase(String catchphrase) {
        this.catchphrase = catchphrase;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}