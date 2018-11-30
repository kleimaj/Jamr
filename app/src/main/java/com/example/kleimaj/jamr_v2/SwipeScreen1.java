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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwipeScreen1 extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private Query query;
    private RecyclerView mFeedsList;
    private static final String TAG = "ssiy";
    static List<ProfileModel> profiles = new ArrayList<>();

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

        ArrayList<ArtistModel> users = SwipeScreen.users; //full of users now

        convertArray(users);

        for (int i = 0 ; i < users.size(); i++) {
            System.out.println("!!!!!!");
            System.out.println(users.get(i).getName());
        }

    }

    @Override //called immediately after onCreateView
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
        int count = 0;
        for(ProfileModel profile : profiles){//Utils.loadProfiles(this.getActivity()
            // .getApplicationContext
            // ())){

            count++;
            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
        }
        System.out.println(count);

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

    public static void convertArray(ArrayList<ArtistModel> users){
        profiles.clear();
        for (int i = 0; i < users.size(); i++) {
            ProfileModel profile = new ProfileModel();
            profile.setName(users.get(i).getName());
            String image = users.get(i).getImage();
            if (image.equals("default")) {
                image = "https://firebasestorage.googleapis.com" +
                  "/v0/b/jamr-679a0.appspot.com/o/profile_images" +
                  "%2Fdefault_avatar.jpeg?alt=media&token=db40887" +
                  "f-838d-4f7a-a00a-4a4285c836d2";
            }
            profile.setImageUrl(image);

            if  (!users.get(i).isBand()) {
                profile.setAge(new Integer(users.get(i).getAge()));
            }
            profiles.add(profile);
        }
    }
}
