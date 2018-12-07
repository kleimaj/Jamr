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

import java.lang.reflect.Array;
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
    private static ArrayList<ArtistModel> artistDontAdd = new ArrayList<>();
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
        System.out.println("IN ON CREATE");

        super.onCreate(savedInstanceState);
        artistDontAdd.clear();
        ArrayList<ArtistModel> users = SwipeScreen.users; //full of users now
        //filter users to preferences first, if there are any
        printArtists(users);

        //filter age
        int pref_age = SwipeScreen.pref_age;
        if (pref_age != 0) {
            for (int i = 0; i < users.size(); i++) {
                ArtistModel artist = users.get(i);
                if (!matchingAge(pref_age,artist.getAge())) {
                    artistDontAdd.add(artist);
                }
            }
        }

        //filter gender
        String genderPref = SwipeScreen.pref_gender;
        if (genderPref != null && (!genderPref.equals(""))) {
            if (!genderPref.equals("Any")) {
                for (int i = 0; i <users.size(); i++) {
                    ArtistModel artist = users.get(i);
                    if (!artist.getGender().equals(genderPref)) {
                        artistDontAdd.add(artist);
                    }
                }
            }
        }
        ArrayList<String> pref_Genre = new ArrayList<>(SwipeScreen.pref_genre);
        printArray(pref_Genre);
        for (int i = 0; i < users.size();i++) {
            ArtistModel artist = users.get(i);
            if (!matchingGenres(pref_Genre,artist.getGenres())) {
                artistDontAdd.add(artist);
            }
        }

        ArrayList<String> pref_identity = new ArrayList<String>(SwipeScreen.pref_identity);
        printArray(pref_identity);
        for (int i = 0; i < users.size(); i++) {
            ArtistModel artist = users.get(i);
            if (!matchingIdentities(pref_identity,artist.getIdentities())) {
                artistDontAdd.add(artist);
            }
        }
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
        if (SwipeScreen.received.contains(UID)) { //if card swiped sent a request
            //call accept friend request
            acceptRequest(UID);
        }
        else {
            //System.out.println(UID +" IN FRIEND REQUEST Sent");
            // --------- Not Friend State
            if (mCurrent_state.equals("not_friends")) {
                mFriendReqDatabase.child(mCurrent_user.getUid()).child(UID).child
                  ("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
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
                        } else {
                            Toast.makeText(view.getContext(), "Failed Sending Request", Toast
                              .LENGTH_SHORT)
                              .show();
                        }
                    }
                });
            }
        }

    }

    public static void acceptRequest(final String UID) {
        Date now = Calendar.getInstance().getTime();
        final String currentDate = SimpleDateFormat.getDateTimeInstance().format(now);
        final DatabaseReference mFriendDatabase = FirebaseDatabase.getInstance().getReference()
          .child
          ("Friends");
        mFriendDatabase.child(mCurrent_user.getUid()).child(UID).setValue
          (currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendDatabase.child(UID).child(mCurrent_user.getUid()).setValue
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
            }
        });
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

    public static void printArtists(ArrayList<ArtistModel> artists) {
        for (int i = 0; i < artists.size(); i++) {
            System.out.println(artists.get(i).getName() + "!!!");
        }
    }
    public static void printProfiles(List<ProfileModel> profiles) {
        for (int i = 0; i < profiles.size(); i++) {
            ProfileModel profile = profiles.get(i);
            System.out.println(profile.getName());
        }
    }

    public static void printArray(ArrayList<String> pref) {
        System.out.println("?????????? IN PRINT ARRAY ???????");
        for (int i = 0; i < pref.size(); i++) {
            System.out.println(pref.get(i)+ "!!!");
        }
    }

    public static void convertArray(ArrayList<ArtistModel> users){
        ArrayList<String> dontAdd = SwipeScreen.dontAdd;
        System.out.println("~~~~~~~~~~~ ARTISTS DONT ADD ~~~~~~~~~~");
        printArray(dontAdd);
        System.out.println("~~~~~~~~~~~ END OF ARTIST DONT ADD ~~~~~~~~");
        profiles.clear();
        for (int i = 0; i < users.size(); i++) {
            if (!artistDontAdd.contains(users.get(i))) {
                System.out.println("preferences match for "+ users.get(i).getName());
                ProfileModel profile = new ProfileModel();
                profile.setUID(users.get(i).getUID());
                if (!dontAdd.contains(profile.getUID())) {
                    System.out.println(users.get(i).getName()+" Unswiped ~~~~~~~~~");
                    profile.setName(users.get(i).getName());
                    String image = users.get(i).getImage();
                    //System.out.println("NAME IS: "+users.get(i).getName());
                    //System.out.println("IMAGE IS "+image);
                    if (image.equals("default")) {
                        image = "https://firebasestorage.googleapis.com" +
                          "/v0/b/jamr-679a0.appspot.com/o/profile_images" +
                          "%2Fdefault_avatar.jpeg?alt=media&token=db40887" +
                          "f-838d-4f7a-a00a-4a4285c836d2";
                    }
                    profile.setImageUrl(image);

                    if (!users.get(i).isBand()) {
                        System.out.println(users.get(i).getName());
                        profile.setAge(new Integer(users.get(i).getAge()));
                        String identities = "";
                        for (int j = 0; j < users.get(i).getIdentities().size(); j++) {
                            identities += users.get(i).getIdentities().get(j);
                            if (j != users.get(i).getIdentities().size() - 1) {
                                identities += ", ";
                            }
                        }
                        profile.setLocation(identities);
                    } else {
                        profile.setAge("");
                        String genres = "";
                        for (int j = 0; j < users.get(i).getGenres().size(); j++) {
                            System.out.println("THIS GENRE IS!!!! : " + users.get(i).getGenres().get(j));
                            genres += users.get(i).getGenres().get(j);
                            if (j != users.get(i).getGenres().size() - 1) {
                                genres += ", ";
                            }
                        }
                        profile.setLocation(genres);
                        System.out.println(genres + " GENRES!!!!!!!!!!");
                    }
                    profiles.add(profile);
                }
            }
        }

        System.out.println("END OF CONVERT ARRAY");
        printProfiles(profiles);
        System.out.println("END OF PROFILES");
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
    //returns true if the user's age is less than or equal to the max age
    public static boolean matchingAge(int agePref, int userAge) {
        if (agePref == 0) {
            return true;
        }
        else return agePref >= userAge;
    }
}
