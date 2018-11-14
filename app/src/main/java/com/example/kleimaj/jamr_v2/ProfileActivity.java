package com.example.kleimaj.jamr_v2;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Fragment {

    public ProfileActivity() {
        // Required empty public constructor
    }

    public static ProfileActivity newInstance() {
        ProfileActivity fragment = new ProfileActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //changeDisplay(MainActivity.currentUser.getName());
        //setContentView(R.layout.activity_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        TextView display = view.findViewById(R.id.ArtistName);
        display.setText(MainActivity.currentUser.getName());
        return view;
    }

    public void setImage(View v) {
        String image = MainActivity.currentUser.getImage();
        //convert base64 to bitmap
        byte[] decodedBytes = Base64.decode(image,0);

    }

}
