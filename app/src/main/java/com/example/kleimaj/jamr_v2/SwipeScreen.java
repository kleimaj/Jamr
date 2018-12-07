package com.example.kleimaj.jamr_v2;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Handler;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static ArrayList<String> received = new ArrayList<>();
    public static String pref_gender = "";
    public static ArrayList<String> pref_genre = new ArrayList<>();
    public static ArrayList<String> pref_identity = new ArrayList<>();
    public static int pref_age = 0;
    public static ArrayList<String> dontAdd = new ArrayList<>(); //dont add to swipe cards
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mFriendReqDatabase;
    String UID = mAuth.getCurrentUser().getUid();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_swipe_screen);
        pref_genre.clear();
        pref_identity.clear();
        mLoadProgress = new ProgressDialog(getActivity());

        mLoadProgress.setTitle("Finding Musicians");
        mLoadProgress.setMessage("Please wait while we retrieve users!");
        mLoadProgress.setCanceledOnTouchOutside(false);
        mLoadProgress.show();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        mFriendReqDatabase = rootRef.child("Friend_req");
        DatabaseReference usersdRef = rootRef;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot ds: dataSnapshot.child("Users").getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    String isBand = ds.child("isBand").getValue(String.class);
                    ArtistModel user= new ArtistModel(name,isBand);
                    if (isBand!=null)
                    if (isBand.equals("true")) {
                        user.setBand(true);
                        ArrayList<String> genre = new ArrayList<String>();
                        //add all genres to genre array
                        for (DataSnapshot genreSnapshot : ds.child("genres").getChildren()) {
                            genre.add(genreSnapshot.getValue(String.class));
                        }
                        user.setGenres(genre);
                    }
                    else {
                        String gender = ds.child("gender").getValue(String.class);
                        user.setGender(gender);
                        String age = ds.child("age").getValue(String.class);
                        System.out.println(ds.child("name").getValue().toString());
                        user.setAge(age);

                        ArrayList<String> genre = new ArrayList<String>();
                        //add all genres to genre array
                        for (DataSnapshot identitySnapshot : ds.child("genres").getChildren()) {
                            genre.add(identitySnapshot.getValue(String.class));
                        }
                        user.setGenres(genre);

                        ArrayList<String> identity = new ArrayList<String>();
                        //add all genres to genre array
                        for (DataSnapshot identitySnapshot : ds.child("music_identity").getChildren
                          ()) {
                            identity.add(identitySnapshot.getValue(String.class));
                        }
                        user.setIdentities(identity);
                    }
                    //get image URL, might contain value "default"
                    String image = ds.child("image").getValue(String.class);
                    user.setImage(image);
                    user.setUID(ds.getKey());
                    if (!ds.getKey().equals(UID)) {
                        users.add(user);
                    }
                    else {
                        MainActivity.currentUser.setBand(Boolean.parseBoolean(isBand));
                        //get preferences
                        if (ds.child("pref_age").exists()) {
                            String prefAgeString = ds.child("pref_age").getValue().toString();
                            pref_age = Integer.parseInt(prefAgeString);
                        }
                        if (ds.child("pref_gender").exists()) {
                            String prefGender = ds.child("pref_gender").getValue().toString();
                            pref_gender = prefGender;
                        }
                        if (ds.child("pref_genre").exists()) {
                            for (DataSnapshot genreSnapshot : ds.child("pref_genre").getChildren()) {
                                pref_genre.add(genreSnapshot.getValue().toString());
                            }
                        }
                        if (ds.child("pref_identity").exists()) {
                            for (DataSnapshot identitySnapshot : ds.child("pref_identity")
                              .getChildren
                              ()) {
                                pref_identity.add(identitySnapshot.getValue().toString());
                            }
                        }
                    }
                }
                //iterate through friend_req
                for (DataSnapshot ds : dataSnapshot.child("Friend_req").child(UID).getChildren()) {
                    if (ds.child("request_type").getValue().equals("received")) {
                        received.add(ds.getKey().toString()); //add the UID to the arraylist
                        //System.out.println(ds.getKey().toString() +" SENT A FRIEND " +
                        //"REQUEST!!!!!!");
                    }
                }

                //iterate through friends and enemies, add to dont add
                for (DataSnapshot ds : dataSnapshot.child("Friends").child(UID).getChildren()) {
                    dontAdd.add(ds.getKey().toString());
                }
                for (DataSnapshot ds : dataSnapshot.child("Enemy").child(UID).getChildren()) {
                    dontAdd.add(ds.getKey().toString());
                }
                for (DataSnapshot ds : dataSnapshot.child("Friend_req").child(UID).getChildren()) {
                    if (ds.child("request_type").getValue().toString().equals("sent"))
                    dontAdd.add(ds.getKey().toString());
                }
                dataLoaded();
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
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i).getName());
            System.out.println("HEEEEEEEEEEEEYYYYYYYYYYYYYYYYYY");
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadProgress.dismiss();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
                ft.commit();
            }
        }, 1000);
    }
}
