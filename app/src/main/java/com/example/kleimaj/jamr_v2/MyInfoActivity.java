package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyInfoActivity extends AppCompatActivity {

    public final static int minAge = 15;
    public final static int maxAge = 99;
    Spinner genderSpinner, ageSpinner;
    EditText nameEditText, bioEditText;
    MultiAutoCompleteTextView identityMulti, genreMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        genderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        ageSpinner = (Spinner) findViewById(R.id.spinner_age);
        nameEditText = (EditText) findViewById(R.id.editText_name);
        bioEditText = (EditText) findViewById(R.id.editText_bio);
        identityMulti = (MultiAutoCompleteTextView) findViewById(R.id.multiComplete_identity);
        genreMulti = (MultiAutoCompleteTextView) findViewById(R.id.multiComplete_genre);
        initializeSpinners();

        // set fields with current preferences
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
            String selectedGender = null;
            if (pos > 0) {
                selectedGender = parent.getItemAtPosition(pos).toString();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
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
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else {
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
    }
}
