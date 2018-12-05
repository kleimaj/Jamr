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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwipeScreen1 extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private Query query;
    private RecyclerView mFeedsList;
    private static final String TAG = "ssiy";
    public static View view;
    private static String mCurrent_state;
    private static DatabaseReference mFriendReqDatabase;
    private static DatabaseReference mEnemyDatabase;
    private static DatabaseReference mNotificationDatase;
    //private static DatabaseReference mFriendDatabase = FirebaseDatabase.getInstance()
      //.getReference().child("Friends");

    private static FirebaseUser mCurrent_user;

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
        //filter users to preferences first, if there are any
        convertArray(users);

        mFriendReqDatabase = FirebaseDatabase.getInstance()
          .getReference().child("Friend_req");
        mEnemyDatabase = FirebaseDatabase.getInstance()
          .getReference().child("Enemy");
        mCurrent_state = "not_friends";
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationDatase = FirebaseDatabase.getInstance().getReference().child("notifications");
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

    public static void seeProfile(String UID){
        Intent userProfileIntent = new Intent(view.getContext(),
          UserProfile.class);
        userProfileIntent.putExtra("user_id", UID);
        view.getContext().startActivity(userProfileIntent);
    }

    public static void sendRequest(final String UID){
        System.out.println(UID +" IN FRIEND REQUEST Sent");
        // --------- Not Friend State
        if(mCurrent_state.equals("not_friends")){
            mFriendReqDatabase.child(mCurrent_user.getUid()).child(UID).child
              ("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mFriendReqDatabase.child(UID).child(mCurrent_user.getUid())
                          .child("request_type").setValue("received")
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {

                                  HashMap<String, String> notificationData = new
                                    HashMap<>();
                                  notificationData.put("from", mCurrent_user.getUid());
                                  notificationData.put("type", "request");

                                  mNotificationDatase.child(UID).push().setValue
                                    (notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          //mCurrent_state = "req_sent";
                                      }
                                  });



//                                        Toast.makeText(UserProfile.this, "Request sent~", Toast.LENGTH_SHORT).show();
                              }
                          });
                    }else{
                        Toast.makeText(view.getContext(), "Failed Sending Request", Toast
                          .LENGTH_SHORT)
                          .show();
                    }
                }
            });
        }

    }

    public static void declineRequest(final String UID) {
        Date now = Calendar.getInstance().getTime();
        final String currentDate = SimpleDateFormat.getDateTimeInstance().format(now);
        //add to this child UID the UID of the enemy
        mEnemyDatabase.child(mCurrent_user.getUid()).child(UID).setValue
          (currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                /*
            }
                //add to the UID of the enemy, this UID

                mEnemyDatabase.child(UID).child(mCurrent_user.getUid()).setValue
                  (currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        mFriendReqDatabase.child(mCurrent_user.getUid()).child(UID).removeValue()
                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  mFriendReqDatabase.child(UID).child(mCurrent_user.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {

                                      }
                                  });
                              }
                          });
                    }
                });
                */
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_swipe_screen1, container, false);
        return view;
    }

    public static void convertArray(ArrayList<ArtistModel> users){
        profiles.clear();
        for (int i = 0; i < users.size(); i++) {
            ProfileModel profile = new ProfileModel();
            profile.setUID(users.get(i).getUID());
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
                String identities = "";
                for (int j = 0; j < users.get(i).getIdentities().size(); j++) {
                    identities += users.get(i).getIdentities().get(j);
                    if (j != users.get(i).getIdentities().size()-1) {
                        identities += ", ";
                    }
                }
                profile.setLocation(identities);
            }
            else {
                profile.setAge("");
                String genres = "";
                for (int j = 0; j < users.get(i).getGenres().size(); j++) {
                    System.out.println("THIS GENRE IS!!!! : "+users.get(i).getGenres().get(j));
                    genres += users.get(i).getGenres().get(j);
                    if (j != users.get(i).getGenres().size()-1) {
                        genres+=", ";
                    }
                }
                profile.setLocation(genres);
                System.out.println(genres +" GENRES!!!!!!!!!!");
            }
            profiles.add(profile);
        }
    }
}
