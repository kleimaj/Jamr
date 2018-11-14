package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.kleimaj.jamr_v2.DatabaseManager.indicator;

public class MyInfoActivity extends AppCompatActivity {

    public final static int minAge = 15;
    public final static int maxAge = 99;
    Spinner genderSpinner, ageSpinner;
    EditText nameEditText, bioEditText, bandNameEditText, bandBioEditText;
    MultiAutoCompleteTextView identityMulti, genreMulti, bandGenreMulti;
    Button artistSave, bandSave;
    DatabaseManager db;

    String selectedAge;
    String selectedGender;
    String bioInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (RegisterActivity.isBand == false) {
            setContentView(R.layout.activity_artist_info);
        }
        else {
            setContentView(R.layout.activity_band_info);
        }*/
        db = new DatabaseManager();

        if (!MainActivity.currentUser.isBand()) {//if an artist
            //System.out.println("Am an Artist!!!!");
            setContentView(R.layout.activity_artist_info);
            genderSpinner = findViewById(R.id.spinner_gender);
            ageSpinner = findViewById(R.id.spinner_age);
            nameEditText = findViewById(R.id.editText_name);
            bioEditText = findViewById(R.id.editText_bio);
            identityMulti = findViewById(R.id.multiComplete_identity);
            genreMulti = findViewById(R.id.multiComplete_genre);
            artistSave = findViewById(R.id.saveButton);
            initializeSpinners();
            initializeMultiAutoCompletes(1);
            bioInfoText = readUserFile();
            if(bioInfoText != null){
                setArtistInfo(bioInfoText);
            }
        }
        else if (MainActivity.currentUser.isBand()) {//db.indicator == 2){ //it's a band
            System.out.println("IM A BAND!!!!!");
            setContentView(R.layout.activity_band_info);
            bandNameEditText = findViewById(R.id.editText_name_band);
            bandBioEditText = findViewById(R.id.editText_bio_band);
            bandSave = findViewById(R.id.saveButtonBand);
            initializeMultiAutoCompletes(2);
            bioInfoText = readUserFile();
            if(bioInfoText != null){
                setBandInfo(bioInfoText);
            }
        }
    }

    private void setArtistInfo(String text) {
        String[] lines = text.split("\n");
        nameEditText.setText(lines[0]);
        //ageSpinner.set text?
        // Set Gender
        //Set Identities
        //Set Genres
        bioEditText.setText(lines[5]);
    }

    private void setBandInfo(String text){
        String[] lines = text.split("\n");
        bandNameEditText.setText(lines[0]);
        //Set band Genres
        bandBioEditText.setText(lines[2]);

    }

    public void onSaveArtistInfo(View v) {
        if(MainActivity.currentUser.isBand()){
            onSaveBandInfo(v);
            return;
        }
        String name = nameEditText.getText().toString();
        String bio = bioEditText.getText().toString();
        String genres = genreMulti.getText().toString();
        String[] genresArray = genres.split(", ");
        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));
        String musicIdentities = identityMulti.getText().toString();
        String[] musicIdentitiesArray = musicIdentities.split(", ");
        ArrayList<String> musicIdentitiesArrayList = new ArrayList<String>(Arrays.asList(musicIdentitiesArray));

        String success = "Save successful";
        String failure = "Failure to save info";
        if (db.setArtistInfo(name, selectedGender, selectedAge, musicIdentitiesArrayList, genresArrayList, bio)) {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
            writeAristInfoToFile();
            this.finish();
        } else {
            Toast.makeText(this, failure, Toast.LENGTH_LONG).show();
        }
    }

    public void onSaveBandInfo(View v) {
        System.out.println("IN SAVEBANDINFO!!!");
        String name = bandNameEditText.getText().toString();
        String bio = bandBioEditText.getText().toString();
        System.out.println("GOT HERE!!!");
        String genres = bandGenreMulti.getText().toString();
        System.out.println("NOT HERE!");
        String[] genresArray = genres.split(", ");
        System.out.println("BUT heRE!!!@!");
        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));

        // put in strings resource file
        String success = "Save successful";
        String failure = "Failure to save info";
        System.out.println("PROBLEM LIES IN DATABASE CLASS!");
        if (db.setBandInfo(name, genresArrayList, bio)) {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
            writeBandInfoToFile();
            this.finish();
        } else {
            Toast.makeText(this, failure, Toast.LENGTH_LONG).show();
        }
    }

    public void setFieldsWithCurrentVals() {

    }

    public void writeBandInfoToFile(){
        Context context = getApplicationContext();
        try {
            FileOutputStream output = context.openFileOutput("bioInfo.txt", Context
              .MODE_PRIVATE);
            StringBuilder text = new StringBuilder();
            text.append(bandNameEditText.getText().toString() + " \n");
            text.append(bandBioEditText.getText().toString() + " \n");
            text.append(bandGenreMulti.getText().toString() + " \n");
            text.append(MainActivity.returnPicturePath() + " \n");
            output.write(text.toString().getBytes());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAristInfoToFile(){
        Context context = getApplicationContext();
        try {
            FileOutputStream output = context.openFileOutput("bioInfo.txt", Context.MODE_PRIVATE);
            StringBuilder text = new StringBuilder();
            text.append(nameEditText.getText().toString() + " \n");
            text.append(selectedAge + " \n");
            text.append(selectedGender + " \n");
            text.append(identityMulti.getText().toString() + " \n");
            text.append(genreMulti.getText().toString() + " \n");
            text.append(bioEditText.getText().toString() + " \n");
            text.append(MainActivity.returnPicturePath() + " \n");
            output.write(text.toString().getBytes());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
            if (pos > 0) {
                switch(parent.getId()) {
                    case R.id.spinner_gender:
                        selectedGender = parent.getItemAtPosition(pos).toString();
                        break;
                    case R.id.spinner_age:
                        selectedAge = parent.getItemAtPosition(pos).toString();
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }

    public void initializeMultiAutoCompletes(int indicator) {
        if (MainActivity.currentUser.isBand()) { //for bands
            bandGenreMulti = findViewById(R.id.multiComplete_genre_band);

            System.out.println("inside function!");
            ArrayAdapter<CharSequence> genresAdapter = ArrayAdapter.createFromResource(this,
                    R.array.genres, android.R.layout.simple_dropdown_item_1line);
            System.out.println("LAST LINE WORKED!");
            bandGenreMulti.setAdapter(genresAdapter);
            bandGenreMulti.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            System.out.println("fine here");
        }
        else if (!MainActivity.currentUser.isBand()) { //for artists
            ArrayAdapter<CharSequence> genresAdapter = ArrayAdapter.createFromResource(this,
                    R.array.genres, android.R.layout.simple_dropdown_item_1line);
            genreMulti.setAdapter(genresAdapter);
            genreMulti.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

            ArrayAdapter<CharSequence> identityAdapter = ArrayAdapter.createFromResource(this,
                    R.array.identities, android.R.layout.simple_dropdown_item_1line);
            identityMulti.setAdapter(identityAdapter);
            identityMulti.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }
        //System.out.println("success");

    }

    public void initializeSpinners() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_dropdown_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new SpinnerActivity());

        ArrayList<String> ageList = new ArrayList<String>();
        ageList.add("Age");
        for (int i = minAge; i <= maxAge; i++)
            ageList.add(Integer.toString(i));

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, ageList) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);
        ageSpinner.setOnItemSelectedListener(new SpinnerActivity());
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(ageSpinner);
            popupWindow.setHeight(700);
        }
        catch (Exception e) { }
    }


    public String readUserFile(){
        Context context = getApplicationContext();
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput("bioInfo.txt");
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
