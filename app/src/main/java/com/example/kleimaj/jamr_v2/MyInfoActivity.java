package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.example.kleimaj.jamr_v2.DatabaseManager.indicator;

public class MyInfoActivity extends AppCompatActivity {

    public final static int minAge = 15;
    public final static int maxAge = 99;
    public static final int ARTIST_IDENTITY = 0;
    public static final int ARTIST_GENRE = 1;
    public static final int BAND_GENRE = 2;

    Spinner genderSpinner, ageSpinner;
    EditText nameEditText, bioEditText, bandNameEditText, bandBioEditText;
    Button artistSave, bandSave;
    DatabaseManager db;

    String selectedAge;
    String selectedGender;
    String bioInfoText;

    public static ArrayList<String> chosenIdentities;
    public static ArrayList<String> chosenGenres;
    public static EditText identityMulti;
    public static EditText genreMulti;
    public static int chipsContext;

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

        if (!MainActivity.currentUser.isBand()) { //if an artist
            //System.out.println("Am an Artist!!!!");
            setContentView(R.layout.activity_artist_info);
            genderSpinner = findViewById(R.id.spinner_gender);
            ageSpinner = findViewById(R.id.spinner_age);
            nameEditText = findViewById(R.id.editText_name);
            bioEditText = findViewById(R.id.editText_bio);
            identityMulti = findViewById(R.id.multi_identity);
            genreMulti = findViewById(R.id.multiComplete_genre);
            artistSave = findViewById(R.id.saveButton);
            initializeSpinners();
            bioInfoText = readUserFile();

            if(bioInfoText != null){
                setArtistInfo(bioInfoText);
            }
            else {
                nameEditText.setText(MainActivity.currentUser.getName());
            }

            if (identityMulti.getText().toString().equals("")) {
                chosenIdentities = new ArrayList<String>();
            }
            if (genreMulti.getText().toString().equals("")) {
                chosenGenres = new ArrayList<String>();
            }
        }
        else if (MainActivity.currentUser.isBand()) { //it's a band
            setContentView(R.layout.activity_band_info);
            bandNameEditText = findViewById(R.id.editText_name_band);
            bandBioEditText = findViewById(R.id.editText_bio_band);
            bandSave = findViewById(R.id.saveButtonBand);
            bioInfoText = readUserFile();
            genreMulti = findViewById(R.id.multiComplete_genre_band);
            if (bioInfoText!=null) {
                setBandInfo(bioInfoText);
            }
            else {
                bandNameEditText.setText(MainActivity.currentUser.getName());
            }

            if (genreMulti.getText().toString().equals("")) {
                chosenGenres = new ArrayList<String>();
            }
        }
    }

    private void setArtistInfo(String text) {
        String[] lines = text.split("\n");
        nameEditText.setText(MainActivity.currentUser.getName());
        // ageSpinner.set text?
        // Set Gender and age dont work
        ageSpinner.setSelection(0);
        identityMulti.setText(lines[4]);
        bioEditText.setText(lines[1]);
        genreMulti.setText(lines[0]);
    }

    private void setBandInfo(String text){
        String[] lines = text.split("\n");
        bandNameEditText.setText(MainActivity.currentUser.getName());
        genreMulti.setText(lines[0]);
        bandBioEditText.setText(lines[1]);
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

    public void onSaveArtistInfo(View v) {
        if(MainActivity.currentUser.isBand()){
            onSaveBandInfo(v);
            return;
        }
        String name = nameEditText.getText().toString();
        String bio = bioEditText.getText().toString();
//        String genres = genreMulti.getText().toString();
//        String[] genresArray = genres.split(", ");
//        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));

        String success = "Save successful";
        String failure = "Failure to save info";
        if (db.setArtistInfo(name, selectedGender, selectedAge, chosenIdentities, chosenGenres, bio)) {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
            writeAristInfoToFile();
            this.finish();
        } else {
            Toast.makeText(this, failure, Toast.LENGTH_LONG).show();
        }
    }

    public void selectIdentity(View v) {
        chipsContext = ARTIST_IDENTITY;
        Intent intent = new Intent(v.getContext(), MyInfoChipsActivity.class);
        this.startActivity(intent);
    }

    public void selectGenreArtist(View v) {
        chipsContext = ARTIST_GENRE;
        Intent intent = new Intent(v.getContext(), MyInfoChipsActivity.class);
        this.startActivity(intent);
    }

    public void selectGenreBand(View v) {
        chipsContext = BAND_GENRE;
        Intent intent = new Intent(v.getContext(), MyInfoChipsActivity.class);
        this.startActivity(intent);
    }

    public void onSaveBandInfo(View v) {
        String name = bandNameEditText.getText().toString();
        String bio = bandBioEditText.getText().toString();
        String genres = genreMulti.getText().toString();
        String[] genresArray = genres.split(", ");
        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));

        // put in strings resource file
        String success = "Save successful";
        String failure = "Failure to save info";
        if (db.setBandInfo(name, genresArrayList, bio)) {
            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
            writeBandInfoToFile();
            this.finish();
        } else {
            Toast.makeText(this, failure, Toast.LENGTH_LONG).show();
        }
    }

    /* ProfileInfo.txt stores:
      name
      isBand
      image

   bioInfo.txt for bands stores:
      genres
      bio
 */
    public void writeBandInfoToFile(){
        Context context = getApplicationContext();
        String UID = MainActivity.currentUser.getUID();
        try {
            //for bio and genres
            FileOutputStream output = context.openFileOutput(UID+"bioInfo.txt", Context
              .MODE_PRIVATE);

            //for name
            StringBuilder bioText = new StringBuilder();
            StringBuilder profileText = new StringBuilder();
            //text.append(bandNameEditText.getText().toString() + " \n");
            bioText.append(genreMulti.getText().toString() + " \n");
            bioText.append(bandBioEditText.getText().toString() + " \n");
            output.write(bioText.toString().getBytes());
            output.close();
            //check if name is null, if user didn't edit name, don't write
            //if user changed name, write to file profileInfo.txt
            String newName = bandNameEditText.getText().toString();
            if (newName.equals(MainActivity.currentUser.getName())) {
            }
            else {
                FileOutputStream output2 = context.openFileOutput(UID+"profileInfo.txt",               Context.MODE_PRIVATE);
                MainActivity.currentUser.setName(newName);
                profileText.append(newName+ " \n");
                profileText.append(MainActivity.currentUser.isBand() + " \n");
                //dont enter image to file if null
                if (!MainActivity.currentUser.getImage().isEmpty()) {
                    profileText.append(MainActivity.currentUser.getImage() + " \n");
                }
                output2.write(profileText.toString().getBytes());
                output2.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* ProfileInfo.txt stores:
          name
          isBand
          image

       bioInfo.txt for artists stores:
          genres
          bio
          age
          gender
          identities
     */
    public void writeAristInfoToFile(){
        Context context = getApplicationContext();
        String UID = MainActivity.currentUser.getUID();
        try {
            FileOutputStream output = context.openFileOutput(UID+"bioInfo.txt", Context
              .MODE_PRIVATE);

            StringBuilder text = new StringBuilder();
            StringBuilder profileText = new StringBuilder();
            //text.append(nameEditText.getText().toString() + " \n");
            text.append(genreMulti.getText().toString() + " \n");
            text.append(bioEditText.getText().toString() + " \n");
            text.append(selectedAge + " \n");
            text.append(selectedGender + " \n");
            text.append(identityMulti.getText().toString() + " \n");
            //text.append(MainActivity.returnPicturePath() + " \n");
            output.write(text.toString().getBytes());
            output.close();

            String newName = nameEditText.getText().toString();
            if (newName.equals(MainActivity.currentUser.getName())) {
            }
            else {
                FileOutputStream output2 = context.openFileOutput(UID+"profileInfo.txt",               Context.MODE_PRIVATE);
                MainActivity.currentUser.setName(newName);
                profileText.append(newName+ " \n");
                profileText.append(MainActivity.currentUser.isBand() + " \n");
                //dont enter image to file if null
                if (!MainActivity.currentUser.getImage().isEmpty()) {
                    profileText.append(MainActivity.currentUser.getImage() + " \n");
                }
                output2.write(profileText.toString().getBytes());
                output2.close();
            }

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

    /* bioInfo.txt for bands stores:
        genres
        bio

       bioInfo.txt for artists stores:
          genres
          bio
          age
          gender
          identities
     */

    public String readUserFile(){
        Context context = getApplicationContext();
        BufferedReader reader = null;
        StringBuilder text = new StringBuilder();
        boolean success = false;
        String UID = MainActivity.currentUser.getUID();
        //Try Catch block to open/read files from directory and put into view
        try {
            FileInputStream stream = context.openFileInput(UID+"bioInfo.txt");
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


    /*



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
            int count = 0;
            while((line = reader.readLine()) !=null){
                text.append(line);
                text.append('\n');
                if (count == 0) { //it's the genre

                }
                else if (count == 1) { //it's the bio

                }
                else if (count == 2) { //it's the age

                }
                else if (count == 3) { //it's the gender

                }
                else if (count == 4) { //it's the identity

                }
                count++;
            }
            reader.close();
            stream.close();
            streamReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    */
}
