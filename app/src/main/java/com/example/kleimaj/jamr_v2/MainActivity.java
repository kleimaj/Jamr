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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static String picturePath;
    private static int RESULT_LOAD_IMAGE = 1;
    public static ArtistModel currentUser;
    DatabaseReference currentUserDb;
    private FirebaseAuth mAuth;
    DatabaseManager db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragment1();
                    break;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    switchToFragment3();
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
        db.isBand();

        TextView display = (TextView) findViewById(R.id.ArtistName);
        //must load data from local file first... still need to figure that out
        //display.setText(currentUser.getName());

      //  initialize();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToFragment1();
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
                //need to change, no more Artist/Band branches in realtime database
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

    public void switchToFragment1() {
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, SwipeScreen1.newInstance());
        ft.commit();
    }


    public void switchToFragment3() {
        //initialize();
        FragmentManager fm = getSupportFragmentManager();
// replace
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, ProfileActivity.newInstance());//MyInfor.newInstance());
        ft.commit();
        //initialize();
    }

    public void myInfoClick(View v) {
        Intent myIntent = new Intent(v.getContext(), MyInfoActivity.class);
        this.startActivity(myIntent);
    }

    public void mySettingsClick(View v) {
        Intent myIntent = new Intent(v.getContext(), MySettingsActivity.class);
        this.startActivity(myIntent);
    }

    public void imageClick(View v){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Log.v("pok", "View Clicked");
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    public static String returnPicturePath() {
        return picturePath;
    }


    /* FUNCTIONS FOR MATCHING ALGORITHM */

    //use for users who have full preferences, algo works for unfilled preferences --
    // (i.e if maxAge is set to 0, if gender is null, arraylists are empty)
    public static ArrayList<ArtistModel> getArtists(ArrayList<String> genres, ArrayList<String>
      identities, String gender, int maxAge) {
        //arraylist of all artists from another function
        ArrayList<ArtistModel> usersFromDatabase = new ArrayList<ArtistModel>(); //should be full

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
        if (userGender.equals(genderPreference)) {
            return true;
        }
        return false;
    }

    public static boolean matchingAge(int agePref, int userAge) {
        if (agePref == 0) {
            return true;
        }
        else if (agePref >= userAge) {
            return true;
        }
        else {
            return false;
        }
    }

    public String readUserFile(boolean isBand){
        Context context = getApplicationContext();
        BufferedReader reader = null;

        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput("profileInfo.txt");
            StringBuilder text = new StringBuilder();
            InputStreamReader streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            String line;
            String numRating;
            while((line = reader.readLine()) !=null){
                text.append(line);
                text.append('\n');
            }
            reader.close();
            stream.close();
            streamReader.close();

            return text;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
