package com.example.kleimaj.jamr_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    DatabaseReference currentUserDb;
    private FirebaseAuth mAuth;
    DatabaseManager db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    switchToFragmentSwipeScreen();
                    break;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_inbox);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_me);
                    switchToFragmentProfile();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = new DatabaseManager();


        // initialize();
        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToFragmentSwipeScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if(picturePath != null) {
                ImageView imageView = (ImageView) findViewById(R.id.profile_image);
                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                db.isBand();
                if(db.indicator == 1) {
                    System.out.println("ARTIST ADDED TO DB");
                    currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("image");
                    currentUserDb.setValue(imageString);
                }else if (db.indicator == 2){
                    System.out.println("Fo Swizzle a band was added to the db");
                    currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("image");
                    currentUserDb.setValue(imageString);
                }
            }
        }
    }

    //THIS FUNCTION NEEDS WORK TO DISPLAY THE NEW USER INFORMATION WHEN THE USER SWITCHES ACTIVITIES
    public void initialize(){
        ImageView imageView = (ImageView) findViewById(R.id.profile_image);
        String imageString = db.hasProfilePicture();
        if(imageString != null){
           /// System.out.println("Image String = " + imageString );
            byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodeImage = BitmapFactory.decodeByteArray(imageBytes,0, imageBytes.length);
            imageView.setImageBitmap(decodeImage);
        }
    }

    public void switchToFragmentSwipeScreen() {
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
        ft.commit();
    }


    public void switchToFragmentProfile() {
        //initialize();
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
        //initialize();
    }

    public void myInfoClick(View v) {
        Log.v("pok", "view clicked");
        Intent myIntent = new Intent(v.getContext(), MyInfoActivity.class);
        this.startActivity(myIntent);
    }

    public void imageClick(View v){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Log.v("pok", "View Clicked");
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
}
