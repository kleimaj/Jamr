package com.example.kleimaj.jamr_v2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child(name);
        currentUserDb.setValue(name);
    }

    public static void setArtistName(String name) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child(name);
        currentUserDb.setValue(name);
    }

    public static void setGender(String gender) {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("gender");
        currentUserDb.setValue(gender);
    }

//    public static void setArtistInfo() {
//
//    }

    public static boolean hasProfilePicture() {
        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = currentUserDb.child("Users").child("");
      //  Query imageQuery =
        return false;
    }

    public static boolean isBand() {

        String userId = mAuth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId);
        if(currentUserDb.getParent().getKey().equals("Artist")){
            return false;
        }else{
            return true;
        }
    }

}
