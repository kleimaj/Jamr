package com.example.kleimaj.jamr_v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwipeScreen1 extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private Query query;
    private RecyclerView mFeedsList;
    private static final String TAG = "ssiy";
    public ArrayList<ArtistModel> users = new ArrayList<>();

    public SwipeScreen1() {
        // required empty public constructor
    }

    public static SwipeScreen1 newInstance() {
        SwipeScreen1 fragment = new SwipeScreen1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //users = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    System.out.println("NAME : "+name+"!!!!!!!!!!!!!!!!!!!");
                    String isBand = ds.child("isBand").getValue(String.class);
                    System.out.println("ISBAND : "+isBand+"!!!!!!!!!!!!!!!!!!!");
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSwipeView = getView().findViewById(R.id.swipeView);
        mContext = getActivity().getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        //loads profiles
        for(ProfileModel profile : Utils.loadProfiles(this.getActivity().getApplicationContext())){
            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
        }

        getView().findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        getView().findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swipe_screen1, container, false);
    }
}
