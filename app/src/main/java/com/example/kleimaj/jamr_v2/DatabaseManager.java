package com.example.kleimaj.jamr_v2;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by kleimaj on 11/5/18.
 */

public class DatabaseManager {
    private static FirebaseAuth mAuth;
    static DatabaseReference currentUserDb;
    static FirebaseDatabase database;

    public DatabaseManager() {
        mAuth = FirebaseAuth.getInstance();

    }

    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public static void setBandName(String name) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("name");
        currentUserDb.setValue(name);
    }

    public static void setArtistName(String name) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("name");
        currentUserDb.setValue(name);
    }

    public static void setArtistGender(String gender) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("gender");
        currentUserDb.setValue(gender);
    }

    public static void setArtistAge(String age) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("age");
        currentUserDb.setValue(age);
    }

    public static void setArtistBio(String bio) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("bio");
        currentUserDb.setValue(bio);
    }

    public static void setBandBio(String bio) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("bio");
        currentUserDb.setValue(bio);
    }

    public boolean setArtistInfo(String name, String gender, String age, ArrayList<String> musicIdentities,
                                ArrayList<String> genres, String bio) {
        try {
            setArtistName(name);
            setArtistGender(gender);
            setArtistAge(age);
            setArtistBio(bio);
            return true;
        } catch (Exception e) {
            Log.v("artist save error", e.getMessage());
            return false;
        }
    }

    public boolean setBandInfo(String name, ArrayList<String> genres, String bio) {
        try {
            setBandName(name);
            setBandBio(bio);
            return true;
        } catch (Exception e) {
            Log.v("band save error", e.getMessage());
            return false;
        }
    }

    public static boolean hasProfilePicture() {
        String userId = mAuth.getCurrentUser().getUid();
        return false;
    }

    public static boolean isBand() {
        return false;
    }

}
