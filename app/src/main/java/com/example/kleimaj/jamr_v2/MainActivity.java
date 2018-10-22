package com.example.kleimaj.jamr_v2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText email,password;
    private String emailString,passwordString;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    //register click fails, problem with firebase authstatelisteners ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //current logged in user, or null
                if (user != null){
                    //user is already logged in
                    //TODO: create intent for swipe screen
                }
            }
        };

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailString = email.getText().toString();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordString = password.getText().toString();
            }
        });
    }

    public void SignInClick(View view){
        if (emailString == null && passwordString == null) {
            Toast.makeText(MainActivity.this, "Unfinished Sign-in Fields",Toast.LENGTH_SHORT).show();
        }
        else {
            //TODO: sign user in
            mAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){ //firebase login failed
                        Toast.makeText(MainActivity.this, "Sign-in Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void RegisterClick(View view){
        Intent myIntent = new Intent(view.getContext(),Registration.class);
        startActivity(myIntent);
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
