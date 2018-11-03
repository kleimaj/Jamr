package com.example.kleimaj.jamr_v2;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    switchToFragment1();
                    break;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_inbox);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_me);
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchToFragment1();
    }

    public void switchToFragment1() {
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
        ft.commit();
    }


    public void switchToFragment3() {
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
    }

    public void myInfoClick(View v) {
        Log.v("pok", "view clicked");
        Intent myIntent = new Intent(v.getContext(), MyInfoActivity.class);
        this.startActivity(myIntent);
    }
}
