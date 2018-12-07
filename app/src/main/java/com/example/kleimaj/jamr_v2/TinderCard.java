package com.example.kleimaj.jamr_v2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private ProfileModel mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    int swipe = 0;

    public TinderCard(Context context, ProfileModel profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());
    }

    @SwipeOut
    private void onSwipedOut(){
        SwipeScreen1.declineRequest(mProfile.getUID());
        swipe = 0;
        Log.d("EVENT", "onSwipedOut");
        //mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        System.out.println(swipe);
        if (swipe == 0) {
            //create intent
            SwipeScreen1.seeProfile(mProfile.getUID());
            /*Intent userProfileIntent = new Intent(this,
              UserProfile.class);
            userProfileIntent.putExtra("user_id", user_id);
            startActivity(userProfileIntent);*/
        }
        else if (swipe > 0)
            swipe = 0;
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        swipe = 0;
        SwipeScreen1.sendRequest(mProfile.getUID(),mProfile.getName());
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        swipe++;
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        swipe++;
        Log.d("EVENT", "onSwipeOutState");
    }
}