package com.example.kleimaj.jamr_v2;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.kleimaj.jamr_v2.MyInfoActivity.minAge;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);

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
        ArrayList<String> genderList = new ArrayList<String>();
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

    public void saveSettings() {

    }

    public void onSaveInfo(View v) {
        String genres = genreMulti.getText().toString();
        String[] genresArray = genres.split(", ");
        ArrayList<String> genresArrayList = new ArrayList<String>(Arrays.asList(genresArray));
        String musicIdentities = identityMulti.getText().toString();
        String[] musicIdentitiesArray = musicIdentities.split(", ");
        ArrayList<String> musicIdentitiesArrayList = new ArrayList<String>(Arrays.asList(musicIdentitiesArray));

        String success = "Save successful";
        Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();

        // saveSettings()
        this.finish();
    }

    public void onLogOut(View v) {

    }
}
