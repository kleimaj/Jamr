package com.example.kleimaj.jamr_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MyInfoChipsActivity extends AppCompatActivity {

    public static final int ARTIST_IDENTITY = 0;
    public static final int ARTIST_GENRE = 1;
    public static final int BAND_GENRE = 2;
    public static final int SETTINGS_IDENTITY = 3;
    public static final int SETTINGS_GENRE = 4;
    public final static int MAX_CHOICES = 5;

    ChipCloud chips;
    String[] chipValues;
    String[] identities;
    String[] genres;
    TextView title;
    ArrayList<String> currentValues;
    int chipsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_chips);
        chipsContext = getIntent().getIntExtra("context", 0);
        chips = findViewById(R.id.chip_cloud);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.chipsTitle);
        initializeChips();
        loadChips();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void loadChips() {
        switch (chipsContext) {
            case ARTIST_IDENTITY: {
                title.setText("Music Identities");
                currentValues = MyInfoActivity.chosenIdentities;
                for (String s: identities) {
                    if (currentValues.contains(s)) {
                        chips.setSelectedChip(Arrays.asList(identities).indexOf(s));
                    }
                }
                break;
            }
            case ARTIST_GENRE: {
                title.setText("Music Genres");
                currentValues = MyInfoActivity.chosenGenres;
                for (String s: genres) {
                    if (currentValues.contains(s)) {
                        chips.setSelectedChip(Arrays.asList(genres).indexOf(s));
                    }
                }
                break;
            }
            case BAND_GENRE: {
                title.setText("Music Genres");
                currentValues = MyInfoActivity.chosenGenres;
                for (String s: genres) {
                    if (currentValues.contains(s)) {
                        System.out.println(s);
                        chips.setSelectedChip(Arrays.asList(genres).indexOf(s));
                    }
                }
                break;
            }
            case SETTINGS_IDENTITY: {
                title.setText("Music Identities");
                currentValues = MySettingsActivity.chosenIdentities;
                for (String s: identities) {
                    if (currentValues.contains(s)) {
                        chips.setSelectedChip(Arrays.asList(identities).indexOf(s));
                    }
                }
                break;
            }
            case SETTINGS_GENRE: {
                title.setText("Music Genres");
                currentValues = MySettingsActivity.chosenGenres;
                for (String s: genres) {
                    if (currentValues.contains(s)) {
                        chips.setSelectedChip(Arrays.asList(genres).indexOf(s));
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            switch (chipsContext) {
                case ARTIST_IDENTITY: {
                    MyInfoActivity.updateIdentities();
                    break;
                }
                case ARTIST_GENRE: {
                    MyInfoActivity.updateGenres();
                    break;
                }
                case BAND_GENRE: {
                    MyInfoActivity.updateGenres();
                    break;
                }
                case SETTINGS_IDENTITY: {
                    MySettingsActivity.updateIdentities();
                    break;
                }
                case SETTINGS_GENRE: {
                    MySettingsActivity.updateGenres();
                    break;
                }
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeChips() {
        identities = getResources().getStringArray(R.array.identities);
        Arrays.sort(identities);
        genres = getResources().getStringArray(R.array.genres);
        Arrays.sort(genres);

        if (chipsContext == ARTIST_IDENTITY || chipsContext == SETTINGS_IDENTITY) {
            chipValues = identities;
        } else {
            chipValues = genres;
        }
        new ChipCloud.Configure().chipCloud(chips).labels(chipValues).chipListener(new ChipListener() {

            @Override
            public void chipSelected(int index) {
                if (currentValues.size() >= MAX_CHOICES) {
                    Toast.makeText(getApplicationContext(), "No more than 5 selections can be chosen", Toast.LENGTH_LONG).show();
                } else if (!currentValues.contains(chipValues[index])) {
                    currentValues.add(chipValues[index]);
                }
            }

            @Override
            public void chipDeselected(int index) {
                if (currentValues.contains(chipValues[index]))
                    currentValues.remove(chipValues[index]);
            }
        }).build();
    }

}
