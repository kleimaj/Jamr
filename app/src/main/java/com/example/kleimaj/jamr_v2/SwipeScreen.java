package com.example.kleimaj.jamr_v2;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class SwipeScreen extends android.support.v4.app.Fragment {

    private ProgressDialog mLoadProgress;
    public static ArrayList<ArtistModel> users = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_swipe_screen);
        mLoadProgress = new ProgressDialog(getActivity());

        mLoadProgress.setTitle("Finding Musicians");
        mLoadProgress.setMessage("Please wait while we retrieve users!");
        mLoadProgress.setCanceledOnTouchOutside(false);
        mLoadProgress.show();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //users = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    String isBand = ds.child("isBand").getValue(String.class);
                    ArtistModel user= new ArtistModel(name,isBand);
                    if (isBand.equals("true")) {
                        ArrayList<String> genre = new ArrayList<String>();
                        //add all genres to genre array
                        for (DataSnapshot genreSnapshot : ds.child("genre").getChildren()) {
                            genre.add(genreSnapshot.getValue(String.class));
                        }
                        user.setGenres(genre);
                    }
                    else {
                        String gender = ds.child("gender").getValue(String.class);
                        user.setGender(gender);
                        String age = ds.child("age").getValue(String.class);
                        user.setAge(age);

                        ArrayList<String> identity = new ArrayList<String>();
                        //add all genres to genre array
                        for (DataSnapshot identitySnapshot : ds.child("genre").getChildren()) {
                            identity.add(identitySnapshot.getValue(String.class));
                        }
                        user.setIdentities(identity);
                    }
                    //get image URL, might contain value "default"
                    String image = ds.child("image").getValue(String.class);
                    user.setImage(image);
                    users.add(user);
                    dataLoaded();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }

    public static SwipeScreen newInstance() {
        SwipeScreen fragment = new SwipeScreen();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_swipe_screen, container, false);
    }

    public void dataLoaded() {
        mLoadProgress.dismiss();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
        ft.commit();
    }
}
