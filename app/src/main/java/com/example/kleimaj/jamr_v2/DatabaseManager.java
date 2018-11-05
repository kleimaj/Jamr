package com.example.kleimaj.jamr_v2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static boolean hasProfilePicture() {
        String userId = mAuth.getCurrentUser().getUid();
        return false;
    }

    public static boolean isBand() {
        return false;
    }

}
