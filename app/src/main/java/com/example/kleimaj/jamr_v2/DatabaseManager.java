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
    static FirebaseDatabase database;
    public static String userId;
    public static String imageString;
    public static String gender;
    public static String name;
    public static int indicator = 0;

    public DatabaseManager() {
        mAuth = FirebaseAuth.getInstance();

    }

    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public static void setBandName(String name) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("name");
        currentUserDb.setValue(name);
    }

    public static void setArtistName(String name) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("name");
        currentUserDb.setValue(name);
    }

    public static void setArtistGender(String gender) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("gender");
        currentUserDb.setValue(gender);
    }

    public static void setArtistAge(String age) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("age");
        currentUserDb.setValue(age);
    }

    public static void setArtistBio(String bio) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("bio");
        currentUserDb.setValue(bio);
    }

    public static void setBandBio(String bio) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("bio");
        currentUserDb.setValue(bio);
    }

    public static void setArtistMusicIdentities(ArrayList<String> musicIdentities) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("music identities");
        currentUserDb.setValue(musicIdentities);
    }

    public static void setArtistGenres(ArrayList<String> genres) throws FirebaseException  {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("genres");
        currentUserDb.setValue(genres);
    }

    public static void setBandGenres(ArrayList<String> genres) throws FirebaseException {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("genres");
        currentUserDb.setValue(genres);
    }

    public boolean setArtistInfo(String name, String gender, String age, ArrayList<String> musicIdentities,
                                 ArrayList<String> genres, String bio) {
        try {
            setArtistName(name);
            setArtistGender(gender);
            setArtistAge(age);
            setArtistBio(bio);
            setArtistMusicIdentities(musicIdentities);
            setArtistGenres(genres);
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
            setBandGenres(genres);
            return true;
        } catch (Exception e) {
            Log.v("band save error", e.getMessage());
            return false;
        }
    }


    public static String hasProfilePicture() {
        isBand();
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference();
        imageString = null;
        if (indicator == 1) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("image");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String image = dataSnapshot.getValue(String.class);
                    imageString = image;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (indicator == 2){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("image");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String image = dataSnapshot.getValue(String.class);
                    imageString = image;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            System.out.println("ERRRROOOOOOOOOOOOROR");
        }
        System.out.println(imageString);
        return imageString;
    }

    public static void isBand() {
        // indicator = 0;
        userId = mAuth.getCurrentUser().getUid();
        //currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId);

        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Band").child(userId).exists()){ // child("isBand").getValue().equals("true")){
                    indicator = 2;
                    System.out.println(indicator + " THIS IS THE INDICATOR");
                    System.out.println("I AM DEFININTELY A BAND AND INDACTOR IS SET TO 2");
                }else if(dataSnapshot.child("Artist").child(userId).exists()){
                    indicator = 1;
                    System.out.println(indicator + " THIS IS THE INDICATOR");
                    System.out.println("FO SWIZZLE DOG I AM AN ARTIST ON THE TOWN LOOKIN TO JAM WHAD UP BOI");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //GLITCH: THIS WILL RUN BEFORE THE FUNCTION ABOVE hasPircture AND WE DO NOT KNOW WHY
        System.out.println(indicator + " THIS IS THE INDICATOR AFTER ALL THE GODDAMN BULLSHIT");
    }

}