package com.example.kleimaj.jamr_v2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kleimaj on 10/20/18.
 */

public class ArtistModel {
    private String name ="";
    private String bio = "";
    private String gender = "";
    private int age;
    private String image = "";
    private boolean isBand;
    private String UID ="";
    private ArrayList<String> identities; //e.g. Producer, Guitarist, Vocalist
    private ArrayList<String> genres; //can switch to a String[] if need be
    private FirebaseAuth mAuth;

    /*Constructors*/

    public ArtistModel(String name){ //for testing
        this.name = name;
        age = -1;
        genres = new ArrayList<>();
        identities = new ArrayList<>();
    }

    public ArtistModel(String name, boolean isBand) {
        this.name = name;
        this.isBand = isBand;
        age = -1;
        genres = new ArrayList<>();
        identities = new ArrayList<>();
    }

    public ArtistModel(String name, String gender) { //for testing
        this.name = name;
        this.gender = gender;
        age = -1;
        genres = new ArrayList<>();
        identities = new ArrayList<>();
    }

    public ArtistModel(String name, String gender, int age){ //for actual use
        this.name = name;
        this.gender = gender;
        this.age = age;
        genres = new ArrayList<>();
        identities = new ArrayList<>();
    }

    /*Setters*/

    public void setBio(String bio){
        this.bio = bio;
    }

    public void setBand(boolean bool) { isBand = bool;}

    public void setName(String name) { this.name = name; }

    public void setImage(String image) { this.image = image; }

    public void setGender(String gender){
        this.gender = gender;
    }

    public void setGenres(ArrayList<String> genres){
        this.genres = new ArrayList<>(genres);
    }

    public void setIdentities(ArrayList<String> identities){
        this.identities = identities;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUID(String UID) {this.UID = UID;}

    public void setAge(String age) {
        this.age = Integer.parseInt(age);
    }

    /*Getters*/

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getGender(){
        return gender;
    }

    public String getImage() { return image; }

    public boolean isBand() {return isBand; }

    public int getAge(){
        return age;
    }

    public String getUID() {return UID;}

    public ArrayList<String> getIdentities() {
        return identities;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

}
