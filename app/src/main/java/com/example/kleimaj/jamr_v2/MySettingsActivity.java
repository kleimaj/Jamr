package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MySettingsActivity extends AppCompatActivity {

    public final static int minAge = 15;
    SeekBar ageSeek;
    SeekBar distanceSeek;
    Spinner genderSpinner;
    MultiAutoCompleteTextView identityMulti, genreMulti;
    TextView ageRange;
    TextView distanceText;
    int maxAge;
    int distance;
    String preferredGender;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        db = new DatabaseManager();

        ageSeek = findViewById(R.id.set_seek_age);
        distanceSeek = findViewById(R.id.set_seek_distance);
        ageRange = findViewById(R.id.set_text_ageVal);
        distanceText = findViewById(R.id.set_text_distanceVal);
        genderSpinner = findViewById(R.id.set_spinner_gender);
        identityMulti = findViewById(R.id.set_multiComplete_identity);
        genreMulti = findViewById(R.id.set_multiComplete_genre);
        initializeSpinner();
        initializeMultiAutoCompletes();
        initializeSeekBars();

        String fileInput = readUserFile();

        if (fileInput!=null) { //adjust widgets
            String[] lines = fileInput.split("\n");
            //age
            String age = lines[0];
            age = age.trim();
            int intAge = Integer.parseInt(age);
            ageSeek.setProgress(intAge);

            //gender
            String gender = lines[1];

            //identity
            String identity = lines[2];
            identityMulti.setText(identity);
            //genre
            String genre = lines[3];
            genreMulti.setText(genre);
        }
        maxAge = ageSeek.getProgress();
        ageRange.setText(minAge + " - " + maxAge);

        distance = distanceSeek.getProgress();
        distanceText.setText(Integer.toString(distance));

    }

    public void initializeSeekBars() {
        ageSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxAge=progress;
                ageRange.setText(minAge + " - " + maxAge);
            }
        });

        distanceSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance=progress;
                distanceText.setText(Integer.toString(distance));
            }
        });
    }

    public void initializeMultiAutoCompletes() {
        ArrayAdapter<CharSequence> genresAdapter = ArrayAdapter.createFromResource(this,
                R.array.genres, android.R.layout.simple_dropdown_item_1line);
        genreMulti.setAdapter(genresAdapter);
        genreMulti.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<CharSequence> identityAdapter = ArrayAdapter.createFromResource(this,
                R.array.identities, android.R.layout.simple_dropdown_item_1line);
        identityMulti.setAdapter(identityAdapter);
        identityMulti.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
            preferredGender = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }

    public void initializeSpinner() {
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Any");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new MySettingsActivity.SpinnerActivity());
    }

    public void onSaveInfo(View v) {
        String genres = genreMulti.getText().toString();
        String[] genresArray = genres.split(", ");
        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));
        String musicIdentities = identityMulti.getText().toString();
        String[] musicIdentitiesArray = musicIdentities.split(", ");
        ArrayList<String> musicIdentitiesArrayList = new ArrayList<String>(Arrays.asList(musicIdentitiesArray));
        String success = "Save successful";
        try {
            db.setPrefAge(maxAge);
            db.setPrefGender(preferredGender);
            db.setPrefIdentity(musicIdentitiesArrayList);
            db.setPrefGenre(genresArrayList);

            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
            saveSettingsToFile();
            this.finish();
        }
        catch (FirebaseException e) {
            e.printStackTrace();
        }
    }

    public void saveSettingsToFile() {
        Context context = getApplicationContext();
        String UID = MainActivity.currentUser.getUID();
        try {
            //for bio and genres
            FileOutputStream output = context.openFileOutput(UID + "Preferences.txt", Context
              .MODE_PRIVATE);

            //for name
            String preferenceText = String.valueOf(maxAge) + " \n" +
                    preferredGender + " \n" +
                    identityMulti.getText().toString() + " \n" +
                    genreMulti.getText().toString() + " \n";
            output.write(preferenceText.getBytes());
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUserFile(){
        Context context = getApplicationContext();
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        boolean success = false;
        String UID = MainActivity.currentUser.getUID();
        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput(UID + "Preferences.txt");
            InputStreamReader streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            String line;
            while((line = reader.readLine()) !=null){
                text.append(line);
                text.append('\n');
            }
            reader.close();
            stream.close();
            streamReader.close();
            success = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(success){
            return text.toString();
        }else{
            return null;
        }

    }

    public void onLogOut(View v) {
        db.logout();
        Intent myIntent = new Intent(MySettingsActivity.this, StartUpActivity.class);
        startActivity(myIntent);
        finish();
    }
}
