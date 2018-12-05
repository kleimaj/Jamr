package com.example.kleimaj.jamr_v2;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    public static ArtistModel currentUser;
    DatabaseReference currentUserDb;
    private FirebaseAuth mAuth;
    DatabaseManager db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragment1();
                    return true;
                case R.id.navigation_dashboard:
                    switchToFragmentFeed();
                    return true;
                case R.id.navigation_notifications:
                    switchToFragment3();
                    return true;
                /*case R.id.navigation_feed:
                    switchToFragmentFeed();
                    return true;*/
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = new DatabaseManager();

        if (RegisterActivity.isJustRegistered()) {
            //save contents to local file
            saveContents();
        }
        else { //they're signing in

        }

        //can't find display??
        //TextView display = (TextView) findViewById(R.id.ArtistName);
        //must load data from local file first... still need to figure that out
       // display.setText(currentUser.getName());
      //  initialize();
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (RegisterActivity.isJustRegistered())
            navigation.setSelectedItemId(R.id.navigation_notifications);
        else
            navigation.setSelectedItemId(R.id.navigation_home); //change to home
    }

    // Switch to Fragment Feed
    private void switchToFragmentFeed() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, FeedFragment.newInstance());
        ft.commit();
    }

    public void switchToFragment1() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen.newInstance());
        ft.commit();
    }

    public void switchToFragment2(){

    }


    public void switchToFragment3() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
    }


    /* FUNCTIONS FOR MATCHING ALGORITHM */

    //use for users who have full preferences, algo works for unfilled preferences --
    // (i.e if maxAge is set to 0, if gender is null, arraylists are empty)
    public static ArrayList<ArtistModel> getArtists(ArrayList<String> genres, ArrayList<String>
      identities, String gender, int maxAge) {
        //arraylist of all artists from another function
        ArrayList<ArtistModel> usersFromDatabase = new ArrayList<>(); //should be full

        ArrayList<ArtistModel> returnedUsers = new ArrayList<ArtistModel>();

        for (int i = 0; i < usersFromDatabase.size(); i++) {
            ArtistModel currentUser = usersFromDatabase.get(i);
            if (matchingAge(maxAge,currentUser.getAge()) &&
                matchingGender(gender, currentUser.getGender()) &&
                matchingIdentities(identities,currentUser.getIdentities()) &&
                matchingGenres(genres, currentUser.getGenres())) {

                returnedUsers.add(currentUser);
            }
        }
        return returnedUsers;
    }
    //if userIdentities contains AT LEAST ONE identity in identityPreferences, return true
    public static boolean matchingIdentities(ArrayList<String> identityPreferences,
                                             ArrayList<String> userIdentities) {
        //if this user has no preferences, they will see every user
        if (identityPreferences.size() == 0) {
            return true;
        }
        for (int i = 0; i < identityPreferences.size(); i++) {
            String thisIdentity = identityPreferences.get(i);
            if (userIdentities.contains(thisIdentity)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchingGenres(ArrayList<String> genrePreferences,
                                         ArrayList<String> userGenres) {
        if (genrePreferences.size() == 0) {
            return true;
        }
        for (int i = 0; i < genrePreferences.size(); i++) {
            String thisGenre = genrePreferences.get(i);
            if (userGenres.contains(thisGenre)){
                return true;
            }
        }
        return false;
    }

    public static boolean matchingGender(String genderPreference, String userGender) {
        if (genderPreference.isEmpty()) {
            return true;
        }
        return userGender.equals(genderPreference);
    }

    public static boolean matchingAge(int agePref, int userAge) {
        if (agePref == 0) {
            return true;
        }
        else return agePref >= userAge;
    }

    protected void saveContents() {
        Context context = getApplicationContext();
        try {
            FileOutputStream output = context.openFileOutput(RegisterActivity.userId+"profileInfo.txt", Context
              .MODE_PRIVATE);
            String text = RegisterActivity.display_name + " \n" +
                    RegisterActivity.isBand + " \n";
            output.write(text.getBytes());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUserFile(boolean isBand){
        Context context = getApplicationContext();
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        String userId = mAuth.getCurrentUser().getUid();
        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput(userId+"profileInfo.txt");
            InputStreamReader streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            String line;
            while((line = reader.readLine()) !=null){
                text.append(line);
                text.append('\n');
            }
            reader.close();
            stream.close();
            streamReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
