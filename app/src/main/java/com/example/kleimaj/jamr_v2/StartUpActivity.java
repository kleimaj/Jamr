package com.example.kleimaj.jamr_v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartUpActivity extends AppCompatActivity {

    private TextInputLayout email,password;
    private String emailString,passwordString;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    static String UID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.activity_start_up);

        mAuth = FirebaseAuth.getInstance(); //must initialize firebase auth
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //must initialize authstatelistener
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //current logged in user, or null
                if (user != null){
                    UID = user.getUid();
                    //user is already logged in
                    //TODO: create intent for swipe screen
                    Context context = getApplicationContext();
                    BufferedReader reader = null;
                    // StringBuilder text = new StringBuilder();
                    String name = "";
                    String isBand = "";
                    StringBuilder image = new StringBuilder();
                    String userId = mAuth.getCurrentUser().getUid();
                    //Try Catch block to open/read files from directory and put into view
                    try {
                        FileInputStream stream = context.openFileInput(userId+"profileInfo" +
                                ".txt");
                        InputStreamReader streamReader = new InputStreamReader(stream);
                        reader = new BufferedReader(streamReader);

                        String line;
                        int count = 0;
                        while((line = reader.readLine()) !=null){
                            //text.append(line);
                            //text.append('\n');
                            if (count == 0) {
                                name = line;
                            }
                            else if (count == 1) {
                                isBand = line;
                            }
                            else if (count >= 2) {
                                image.append(line);
                            }
                            count++;
                        }
                        reader.close();
                        stream.close();
                        streamReader.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MainActivity.currentUser = new ArtistModel(name);
                    MainActivity.currentUser.setBand(Boolean.parseBoolean(isBand));
                    MainActivity.currentUser.setImage(image.toString());
//
                    Intent myIntent = new Intent(StartUpActivity.this, MainActivity.class);
                    startActivity(myIntent);
                    finish();


                }
            }
        };

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
    }

    public void SignInClick(View view){
        System.out.println("Inside SignIn");
        emailString = email.getEditText().getText().toString();
        passwordString = password.getEditText().getText().toString();
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(StartUpActivity.this, "Unfinished Sign-in Fields",Toast.LENGTH_SHORT).show();
        }
        else {
            //TODO: sign user in
            //Need to tell user what is the error is.
            mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(StartUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){ //firebase login failed
                        Toast.makeText(StartUpActivity.this, "Sign-in Success",Toast.LENGTH_SHORT).show();
                        //here we need to read from local file, store name and isBand in
                        // MainActivity.currentUser
                        System.out.println("Sign in Success, about to read file");
                        Context context = getApplicationContext();
                        BufferedReader reader = null;
                       // StringBuilder text = new StringBuilder();
                        String name = "";
                        String isBand = "";
                        String image = "";
                        //Try Catch block to open/read files from directory and put into view
                        try {
                            System.out.println("USER ID IS : "+UID);
                            FileInputStream stream = context.openFileInput(UID+"profileInfo" +
                              ".txt");
                            InputStreamReader streamReader = new InputStreamReader(stream);
                            reader = new BufferedReader(streamReader);

                            String line;
                            int count = 0;
                            System.out.println("ABOUT TO READ!!!");
                            while((line = reader.readLine()) !=null){
                                //text.append(line);
                                //text.append('\n');
                                if (count == 0) {
                                    name = line;
                                    System.out.println("PARSING THE FILE, HERE'S THE NAME!! : " +
                                      ""+line);
                                }
                                else if (count == 1) {
                                    isBand = line;
                                }
                                else if (count >= 2) {
                                    image += line;
                                }
                                count++;
                            }
                            reader.close();
                            stream.close();
                            streamReader.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        MainActivity.currentUser = new ArtistModel(name);
                        isBand = isBand.trim();
                        if (isBand.equals("true")) {
                            System.out.println("HERE!!!");
                            MainActivity.currentUser.setBand(true);
                        }
                        else {
                            MainActivity.currentUser.setBand(Boolean.parseBoolean(isBand));
                        }
                        MainActivity.currentUser.setImage(image);
                        System.out.println("THE FILE !!!!!!!! WWWWWWWWWWWWWW");
                        System.out.println("MAIn BOOL IS : "+MainActivity.currentUser.isBand());

                        Intent myIntent = new Intent(StartUpActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                    else {
                        Toast.makeText(StartUpActivity.this, "Sign-in Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void RegisterClick(View view){
        Intent myIntent = new Intent(view.getContext(),RegisterActivity.class);
        startActivity(myIntent);
       // finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
