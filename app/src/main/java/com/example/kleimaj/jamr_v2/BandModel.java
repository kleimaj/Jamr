package com.example.kleimaj.jamr_v2;

import android.media.Image;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kleimaj on 10/20/18.
 */

public class BandModel {

    private String name;
    private String bio;
    private ArrayList<ImageView> profilePictures;
    private ArrayList<String> genres;

    /* Constructor*/

    public BandModel(String name){
        this.name = name;
    }

    /*Setters*/

    public void setBio(String bio){
        this.bio = bio;
    }

    public void setGenres(ArrayList<String> genres){
        genres = new ArrayList<String>(genres);
    }

    public void setProfilePictures(ArrayList<ImageView> pictures) {
        profilePictures = new ArrayList<ImageView>(pictures);
    }

    /*Getters*/

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<ImageView> getProfilePictures() {
        return profilePictures;
    }
}
