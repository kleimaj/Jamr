package com.example.kleimaj.jamr_v2;

public class Users {
    public String name;
    public String image;
    public String identities;

    public Users(){

    }

    // Needed for Firebase
    public Users(String name, String image, String identities) {
        this.name = name;
        this.image = image;
        this.identities = identities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdentities() {
        return identities;
    }

    public void setIdentities(String identities) {
        this.identities = identities;
    }



}
