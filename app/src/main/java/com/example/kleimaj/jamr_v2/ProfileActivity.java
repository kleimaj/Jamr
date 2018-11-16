package com.example.kleimaj.jamr_v2;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Fragment implements View.OnClickListener {
    private static int RESULT_LOAD_IMAGE = 1;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Adnroid Layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

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

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child
                ("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getActivity(), dataSnapshot.toString(), Toast
                        .LENGTH_SHORT).show();
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue()
                        .toString();
                String thumb_image = dataSnapshot.child("thumb_image")
                        .getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //changeDisplay(MainActivity.currentUser.getName());
        //setContentView(R.layout.activity_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile, container, false);
//        setDisplayName(view);
//        setImage(view);
        Button  settingButton = view.findViewById(R.id.profile_setting_button);
        settingButton.setOnClickListener(this);

        Button  collectionButton = view.findViewById(R.id.profile_collection_button);
        collectionButton.setOnClickListener(this);
//
//        ImageButton infoRoundButton = view.findViewById(R.id.InfoRoundButton);
//        infoRoundButton.setOnClickListener(this);
//
//        ImageButton profilePictureButton = view.findViewById(R.id.ProfilePictureButton);
//        profilePictureButton.setOnClickListener(this);

        return view;
    }


    // onlick method for buttoms
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_collection_button:
                Intent infoIntent = new Intent(v.getContext(), MyInfoActivity.class);
                this.startActivity(infoIntent);
                break;
            case R.id.profile_setting_button:
                Intent settingIntent = new Intent(v.getContext(), MySettingsActivity.class);
                this.startActivity(settingIntent);
                break;
//            case R.id.ProfilePictureButton:
//                String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permissionsList, 1);
//                Log.v("pok", "View Clicked");
//                Intent i = new Intent(
//                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

//    public void setImage(View v) {
//        String image = MainActivity.currentUser.getImage();
//        System.out.println("HERE ... IMAGE IS "+image);
//        if (image!=null) {
//            if (!image.isEmpty()) {
//                //convert base64 to bitmap
//                byte[] decodedBytes = Base64.decode(image, 0); //flag maybe Base64.DEFAULT
//                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
//                imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes
//                  .length));
//            }
//        }
//    }
//
//    public void setDisplayName(View v) {
//        TextView display = v.findViewById(R.id.ArtistName);
//        display.setText(MainActivity.currentUser.getName());
//    }

}
