package com.example.kleimaj.jamr_v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileModel {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private String age ="";

    @SerializedName("location")
    @Expose
    private String location;

    private String UID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAge() {
        return age;
    }

    public String getUID(){return UID;}

    public void setAge(Integer age) {
        this.age = age.toString();
        // this.age = age;
    }

    public void setAge(String age) {

    }

    public void setUID(String UID) { this.UID = UID;}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}