package com.example.kleimaj.jamr_v2;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private String name, email,password;
    private EditText nameText,emailText,passwordText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private View passingView;
    static boolean isBand;
    boolean radioClicked;

    private static final String TAG = "Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        nameText = findViewById(R.id.NameEditText);
        emailText = findViewById(R.id.emailRegisterEditText);
        passwordText = findViewById(R.id.passwordRegisterEditText);

        radioClicked = false; //false until radio button is triggered
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(Registration.this, "Already signed-in",Toast.LENGTH_SHORT).show();
                }
            }
        } ;
    }

    public void artistClick(View view){
        radioClicked = true;
        isBand = false;
    }

    public void bandClick(View view){
        radioClicked = true;
        isBand = true;
    }

    public void SignUpClick(View view){
        email = emailText.getText().toString();
        password = passwordText.getText().toString();
        name = nameText.getText().toString();
        passingView = view;
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || radioClicked == false) {
            Toast.makeText(Registration.this, "Unfinished Sign-up Fields",Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) { //registration failed
                        Toast.makeText(Registration.this, "Unable to register", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                    else {
                        Toast.makeText(Registration.this, "Register successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        DatabaseReference currentUserDb;
                        String userId = user.getUid();
                        if (isBand){
                            currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("name");
                            currentUserDb.setValue(name);
                            currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Band").child(userId).child("isBand");
                            currentUserDb.setValue("true");
                            Intent myIntent = new Intent(passingView.getContext(),MainActivity.class);
                            startActivity(myIntent);
                            finish();
                            return;
                        }else{
                            currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("name");
                            currentUserDb.setValue(name);
                            currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Artist").child(userId).child("isBand");
                            currentUserDb.setValue("false");
                            Intent myIntent = new Intent(passingView.getContext(),MainActivity.class);
                            startActivity(myIntent);
                            finish();
                            return;
                        }

                    }
                }
            });
        }
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
