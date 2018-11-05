package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MyInfoActivity extends AppCompatActivity {

    public final static int minAge = 15;
    public final static int maxAge = 99;
    Spinner genderSpinner, ageSpinner;
    EditText nameEditText, bioEditText;
    MultiAutoCompleteTextView identityMulti, genreMulti;
    DatabaseManager db;

    String selectedAge;
    String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Registration.isBand == false) {
            setContentView(R.layout.activity_artist_info);
        }
        else {
            setContentView(R.layout.activity_band_info);
        }
        db = new DatabaseManager();
        genderSpinner = findViewById(R.id.spinner_gender);
        ageSpinner = findViewById(R.id.spinner_age);
        nameEditText = findViewById(R.id.editText_name);
        bioEditText = findViewById(R.id.editText_bio);
        identityMulti = findViewById(R.id.multiComplete_identity);
        genreMulti = findViewById(R.id.multiComplete_genre);
        initializeSpinners();
        initializeMultiAutoCompletes();
        // set fields with current preferences upon opening

    }

    public void onSaveArtistInfo(View v) {
        // name
        // gender, age
        // genres
        // identities
        // bio
        String name = nameEditText.getText().toString();
        String bio = bioEditText.getText().toString();
        // db.setGender(selectedGender);
        // db.setArtistInfo()
        // Toast "save success" or "error"
    }

    public void onSaveBandInfo(View v) {

        // name
        // genres
        // bio
        String name = nameEditText.getText().toString();
        String bio = bioEditText.getText().toString();

        // db.setBandInfo()

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

    public void initializeSpinners() {
        // later on must apply hint
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
}
