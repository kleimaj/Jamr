package com.example.kleimaj.jamr_v2;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by kleimaj on 11/5/18.
 */

public class DatabaseManager {

    private static FirebaseAuth mAuth;
    static DatabaseReference currentUserDb;
    public static String name;
    public static int indicator = 0;

    public DatabaseManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public static void setName(String name) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("name");
        currentUserDb.setValue(name);
    }

    public static void setArtistGender(String gender) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("gender");
        currentUserDb.setValue(gender);
    }

    public static void setArtistAge(String age) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("age");
        currentUserDb.setValue(age);
    }

    public static void setBio(String bio) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("bio");
        currentUserDb.setValue(bio);
    }

    public static void setMusicIdentities(ArrayList<String> musicIdentities) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("music_identity");
        currentUserDb.setValue(musicIdentities);
    }

    public static void setGenres(ArrayList<String> genres) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("genres");
        currentUserDb.setValue(genres);
    }

    public static void setImage(String imageString) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child
          (userId).child("thumb_image");
        currentUserDb.setValue(imageString);
    }

    //acts as max age
    public static void setPrefAge(int age) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child
          (userId).child("pref_age");
        currentUserDb.setValue(age);
    }

    public static void setPrefGender(String gender) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child
          (userId).child("pref_gender");
        currentUserDb.setValue(gender);
    }

    public static void setPrefIdentity(ArrayList<String> identity) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child
          (userId).child("pref_identity");
        currentUserDb.setValue(identity);
    }

    public static void setPrefGenre(ArrayList<String> genre) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child
          (userId).child("pref_genre");
        currentUserDb.setValue(genre);
    }


    public boolean setArtistInfo(String name, String gender, String age, ArrayList<String> musicIdentities,
                                 ArrayList<String> genres, String bio) {
        try {
            setName(name);
            setArtistGender(gender);
            setArtistAge(age);
            setBio(bio);
            setMusicIdentities(musicIdentities);
            setGenres(genres);
            return true;
        } catch (Exception e) {
            Log.v("artist save error", e.getMessage());
            return false;
        }
    }

    public void logout() {
        mAuth.getInstance().signOut();
    }

    public boolean setBandInfo(String name, ArrayList<String> genres, String bio) {
        try {
            setName(name);
            setBio(bio);
            setGenres(genres);
            return true;
        } catch (Exception e) {
            Log.v("band save error", e.getMessage());
            return false;
        }
    }
}