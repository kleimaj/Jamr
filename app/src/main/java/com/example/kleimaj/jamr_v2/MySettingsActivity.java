package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.kleimaj.jamr_v2.MyInfoActivity.chosenGenres;
import static com.example.kleimaj.jamr_v2.MyInfoActivity.chosenIdentities;
import static com.example.kleimaj.jamr_v2.MyInfoActivity.minAge;

public class MySettingsActivity extends AppCompatActivity {

    public final static int minAge = 18;
    public static final int SETTINGS_IDENTITY = 3;
    public static final int SETTINGS_GENRE = 4;
    SeekBar ageSeek;
    SeekBar distanceSeek;
    Spinner genderSpinner;
    public static EditText identityMulti, genreMulti;
    TextView ageRange;
    TextView distanceText;
    int maxAge;
    int distance;
    String preferredGender;
    DatabaseManager db;
    public static ArrayList<String> chosenIdentities;
    public static ArrayList<String> chosenGenres;
    public static ArrayList<String> genderList;

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
        initializeSeekBars();

        String fileInput = readUserFile();

        if (fileInput!=null) {
            String[] lines = fileInput.split("\n");
            String age = lines[0];
            age = age.trim();
            int intAge = Integer.parseInt(age);
            ageSeek.setProgress(intAge);

            String gender = lines[1].replaceAll("\\s+","");
            String identity = lines[2];
            genderSpinner.setSelection(genderList.indexOf(gender));
            identityMulti.setText(identity);
            String genre = lines[3];
            genreMulti.setText(genre);
        }
        maxAge = ageSeek.getProgress();
        ageRange.setText(minAge + " - " + maxAge);

        distance = distanceSeek.getProgress();
        distanceText.setText(Integer.toString(distance));

        if (chosenIdentities == null || identityMulti.getText().toString().equals(""))
            chosenIdentities = new ArrayList<String>();
        if (chosenGenres == null || genreMulti.getText().toString().equals(""))
            chosenGenres = new ArrayList<String>();
    }

    public void selectIdentity(View v) {
        int chipsContext = SETTINGS_IDENTITY;
        Intent intent = new Intent(v.getContext(), MyInfoChipsActivity.class);
        intent.putExtra("context", chipsContext);
        this.startActivity(intent);
    }

    public void selectGenre(View v) {
        int chipsContext = SETTINGS_GENRE;
        Intent intent = new Intent(v.getContext(), MyInfoChipsActivity.class);
        intent.putExtra("context", chipsContext);
        this.startActivity(intent);
    }

    public static void updateIdentities() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: chosenIdentities) {
            if (!stringBuilder.toString().equals("")) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(s);
        }
        String text = stringBuilder.toString();
        identityMulti.setText(text);
    }

    public static void updateGenres() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: chosenGenres) {
            if (!stringBuilder.toString().equals("")) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(s);
        }
        String text = stringBuilder.toString();
        genreMulti.setText(text);
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

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
            preferredGender = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }

    public void initializeSpinner() {
        genderList = new ArrayList<String>();
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
        String success = "Save successful";
        try {
            db.setPrefAge(maxAge);
            db.setPrefGender(preferredGender);
            db.setPrefIdentity(chosenIdentities);
            db.setPrefGenre(chosenGenres);

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
            FileOutputStream output = context.openFileOutput(UID + "Preferences.txt", Context
              .MODE_PRIVATE);

            StringBuilder preferenceText = new StringBuilder();
            preferenceText.append(maxAge + " \n");
            preferenceText.append(preferredGender + " \n");
            preferenceText.append(identityMulti.getText().toString() + " \n");
            preferenceText.append(genreMulti.getText().toString() + " \n");
            output.write(preferenceText.toString().getBytes());
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
