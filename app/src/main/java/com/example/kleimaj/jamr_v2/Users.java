package com.example.kleimaj.jamr_v2;


import java.util.ArrayList;

//  Firebase automatic match this to database.  so the name must be the same as in database
//  You also need to name your fields and getters so that they match the JSON property names
//  https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md
public class Users {
    public String name;
    public String image;

    public ArrayList<String> music_identity;


    // Needed for Firebase
    public Users(){

    }


    public Users(String name, String image, ArrayList<String> music_identity) {
        this.name = name;
        this.image = image;
        this.music_identity = music_identity;
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


    public ArrayList<String> getMusic_identity() {
        return music_identity;
    }

    public void setMusic_identity(ArrayList<String> music_identity) {
        this.music_identity = music_identity;
    }



}
