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

    public void switchToFragment3() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
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
}
