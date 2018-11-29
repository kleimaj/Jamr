package com.example.kleimaj.jamr_v2;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class UserProfile extends AppCompatActivity {

    private TextView mDisplayID;
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileMusicIdentity, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileDeclineReqBtn;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatase;

    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_content_layout);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child
          (user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationDatase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mProfileImage = findViewById(R.id.user_profile_iamge);

        mProfileName = findViewById(R.id.user_profile_name);
        mProfileMusicIdentity = findViewById(R.id.user_profile_music_identity);
        mProfileFriendsCount = findViewById(R.id.user_profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.user_profile_send_req_btn);
        mProfileDeclineReqBtn = findViewById(R.id.user_profile_decline_req_btn);

        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);



        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String music_identity = dataSnapshot.child("music_identity")
                  .getValue().toString()
                  .replace("[", "")  //remove the right bracket
                  .replace("]", "")  //remove the left bracket
                  .trim()
                  .replaceAll(",$", ""); // remove the last comma
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileMusicIdentity.setText(music_identity);

                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                // ------- Friend List / Request Feature
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent
                  (new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if (dataSnapshot.hasChild(user_id)){
                              String req_type = dataSnapshot.child(user_id).child("request_type")
                                .getValue().toString();

                              if (req_type.equals("received")){

                                  mCurrent_state = "req_received";
                                  mProfileSendReqBtn.setText("Accept Friend Request");

                                  mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                  mProfileDeclineReqBtn.setEnabled(true);


                              }else if (req_type.equals("sent")){

                                  mCurrent_state = "req_sent";
                                  mProfileSendReqBtn.setText("Cancel Friend Request");
                                  mProfileSendReqBtn.setEnabled(true);

                                  mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                  mProfileDeclineReqBtn.setEnabled(false);
                              }
                              mProgressDialog.dismiss();
                          } else{
                              mFriendDatabase.child(mCurrent_user.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user_id)){

                                            mCurrent_state = "friends";
                                            mProfileSendReqBtn.setText("Unfriend this Person");

                                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReqBtn.setEnabled(false);

                                        }
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        mProgressDialog.dismiss();
                                    }
                                });
                          }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);

                // --------- Not Friend State
                if(mCurrent_state.equals("not_friends")){
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child
                      ("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())
                                  .child("request_type").setValue("received")
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {

                                          HashMap<String, String> notificationData = new
                                            HashMap<>();
                                          notificationData.put("from", mCurrent_user.getUid());
                                          notificationData.put("type", "request");

                                          mNotificationDatase.child(user_id).push().setValue
                                            (notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  mCurrent_state = "req_sent";
                                                  mProfileSendReqBtn.setText("Cancel Friend Request");
                                                  mProfileSendReqBtn.setEnabled(true);

                                                  mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                  mProfileDeclineReqBtn.setEnabled(false);
                                              }
                                          });



//                                        Toast.makeText(UserProfile.this, "Request sent~", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                            }else{
                                Toast.makeText(UserProfile.this, "Failed Sending Request", Toast
                                  .LENGTH_SHORT)
                                  .show();
                            }
                        }
                    });
                }

                // ------ Cancel Request
                if (mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      mProfileSendReqBtn.setEnabled(true);
                                      mCurrent_state = "not_friends";
                                      mProfileSendReqBtn.setText("Send Friend Request");

                                      mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                      mProfileDeclineReqBtn.setEnabled(false);
                                  }
                              });
                          }
                      });
                }


                // req received state
                if (mCurrent_state.equals("req_received")){

                    Date now = Calendar.getInstance().getTime();
                    final String currentDate = SimpleDateFormat.getDateTimeInstance().format(now);

                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue
                      (currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue
                              (currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid) {
                                                      mProfileSendReqBtn.setEnabled(true);
                                                      mCurrent_state = "friends";
                                                      mProfileSendReqBtn.setText("UnFriend this " +
                                                        "Person");

                                                      mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                                      mProfileDeclineReqBtn.setEnabled(false);
                                                  }
                                              });
                                          }
                                      });
                                }
                            });
                        }
                    });
                }


            }
        });


    }
}
