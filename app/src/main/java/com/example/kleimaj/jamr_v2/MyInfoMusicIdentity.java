package com.example.kleimaj.jamr_v2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MyInfoMusicIdentity extends AppCompatActivity {

    ChipCloud chips;
    String[] identities;
    ArrayList<String> chosenIdentities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_music_identity);
        Bundle extras = getIntent().getExtras();
        chosenIdentities = extras.getStringArrayList("chosenIdentities");

        chips = findViewById(R.id.chip_cloud);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeChips();
        for (String s: chosenIdentities) {
            System.out.println("THIS " + s);
        }

        for (String s: identities) {
            if (chosenIdentities.contains(s)) {
                // problem here?
                //System.out.println("THIS " + s);
                chips.setSelectedChip(Arrays.asList(identities).indexOf(s));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {

            System.out.println("Exiting");
            for (String s: chosenIdentities) {
                System.out.println("RETURNING: " + s);
            }
            Intent intent = new Intent();
            intent.putExtra("chosenIdentities", chosenIdentities);
            setResult(1, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeChips() {
        identities = getResources().getStringArray(R.array.identities);
        Arrays.sort(identities);
        new ChipCloud.Configure().chipCloud(chips).labels(identities).chipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                chosenIdentities.add(identities[index]);
            }
            @Override
            public void chipDeselected(int index) {
                chosenIdentities.remove(identities[index]);
            }
        }).build();
    }

}
