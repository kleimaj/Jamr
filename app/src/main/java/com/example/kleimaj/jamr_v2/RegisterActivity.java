package com.example.kleimaj.jamr_v2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String display_name, email, password;
    private TextInputLayout mDisplayName, mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private View passingView;
    private RadioGroup radioGroup;
    private DatabaseReference mDatabase;
    static boolean isBand;

    // Progress Dialog
    private ProgressDialog mRegProgress;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        radioGroup = findViewById(R.id.reg_select_radioGroup);

        mAuth = FirebaseAuth.getInstance();

        mRegProgress = new ProgressDialog(this);

        // TODO
        // This need to be cleaned up
        // Need a Have a count button to go back to main page
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(RegisterActivity.this,
                      "Already signed-in", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void artistClick(View view) {
        isBand = false;
    }

    public void bandClick(View view) {
        isBand = true;
    }

    public void SignUpClick(View view) {
        email = mEmail.getEditText().getText().toString();
        password = mPassword.getEditText().getText().toString();
        display_name = mDisplayName.getEditText().getText().toString();
        passingView = view;

        // check if radio button is checked or not
        if (radioGroup.getCheckedRadioButtonId() == - 1) {
            Toast.makeText(RegisterActivity.this,
              "Please Select Artist or Band", Toast.LENGTH_SHORT).show();
        } else {
            // one of the radio buttons is checked
            // check for valid data
            if (checkDataEntered()) {
                mRegProgress.setTitle("Registering User");
                mRegProgress.setMessage("Please wait while we create your account!");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();
                registerUserToDB();
            }

        }
    }

    // sign up the user
    protected void registerUserToDB() {
        //make a singleton artistmodel
        MainActivity.currentUser = new ArtistModel(display_name);
        MainActivity.currentUser.setBand(isBand);
        saveContents(); //writes to local file

        mAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                  if (! task.isSuccessful()) { //registration failed
                      mRegProgress.dismiss();
                      Toast.makeText(RegisterActivity.this,
                        task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                      Log.w(TAG, "createUserWithEmail:failure", task.getException());
                  } else {
                      Toast.makeText(RegisterActivity.this, "Register successful",
                        Toast.LENGTH_SHORT).show();
                      FirebaseUser user = mAuth.getCurrentUser();
                      String userId = user.getUid();

                      mDatabase = FirebaseDatabase.getInstance().getReference
                        ().child("Users").child(userId);
                      HashMap<String, String> userMap = new HashMap<>();
                      userMap.put("name", display_name);
                      userMap.put("thumb_image", "default");
                      userMap.put("image", "default");
                      userMap.put("isBand", String.valueOf(isBand));

                      mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()) {
                                  mRegProgress.dismiss();
                                  Intent myIntent = new Intent(passingView.getContext(),
                                    MainActivity.class);
                                  startActivity(myIntent);
                              }
                          }
                      });
                  }
              }
          });
    }
    //writes name and isBand to file, to be used on login
    protected void saveContents() {
        Context context = getApplicationContext();
        String userId = mAuth.getCurrentUser().getUid();
        try {
            FileOutputStream output = context.openFileOutput(userId+"profileInfo.txt", Context
              .MODE_PRIVATE);
            StringBuilder text = new StringBuilder();
            text.append(display_name + " \n");
            text.append(isBand + " \n");
            output.write(text.toString().getBytes());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check for empty name/ email / password
    protected boolean checkDataEntered() {
        if (display_name.isEmpty()) {
            Toast t = Toast.makeText(this, R.string.reg_name_empty, Toast
              .LENGTH_SHORT);
            t.show();
            return false;
        }
        if (email.isEmpty()) {
            Toast t = Toast.makeText(this, R.string.reg_email_empty, Toast
              .LENGTH_SHORT);
            t.show();
            return false;
        }
        if (password.isEmpty()) {
            Toast t = Toast.makeText(this, R.string.reg_password_empty, Toast
              .LENGTH_SHORT);
            t.show();
            // TODO:
            // check for good password
            return false;
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        finish();
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
