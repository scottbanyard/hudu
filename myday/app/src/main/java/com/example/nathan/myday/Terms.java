package com.example.nathan.myday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class Terms extends AppCompatActivity {
    // firebase reference
    private Firebase firebaseRef;

    Button next_button;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String dob;
    private String nationality;
    private Object uidObj;
    private String uid;
    private String error;
    private String fbError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // firebase objects
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);

        // get firstname, lastname, email, password, gender, dob and nationality from register_3 activity
        firstname = getIntent().getExtras().getString("firstname");
        lastname = getIntent().getExtras().getString("lastname");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");
        gender = getIntent().getExtras().getString("gender");
        dob = getIntent().getExtras().getString("dob");
        nationality = getIntent().getExtras().getString("country");

        next_button = (Button) findViewById(R.id.button_next4);
        next_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                next_button_click();
            }
        });
    }

    // adds user to firebase login & auth page ensuring no emails are used twice (will give error)
    protected void createUser() {
        firebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                uidObj = result.get("uid");
                uid = uidObj.toString();
                // adds user to database if successful with the created unique id
                addUserToDB(uid);
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                fbError = firebaseError.toString();
                switch (firebaseError.getCode()) {
                    case FirebaseError.EMAIL_TAKEN:
                        error = "Error: Email already exists.";
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        error = "Error connecting to network.";
                        break;
                    default:
                        error = "Unknown Error occured.";
                        break;
                }

                Toast.makeText(Terms.this, error, Toast.LENGTH_SHORT).show();
                System.out.println(fbError);
            }
        });
    }

    // adds user to firebase data page using unique id as root i.e. myday -> users -> uid
    protected void addUserToDB(String uid) {
        // register details object of the user
        RegisterDetails regDetails = new RegisterDetails(uid, email, firstname, lastname, gender, dob, nationality);
        // sets root to the users database with the unique ID of the user
        Firebase userRef = firebaseRef.child("users").child(uid);
        // sets values using reference to the database
        userRef.setValue(regDetails, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    Toast.makeText(Terms.this, "Error creating user.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Data saved successfully.");
                    Toast.makeText(Terms.this, "User created successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    Boolean next_button_click() {
        // creates a user
        createUser();
        Intent i = new Intent(getApplicationContext(),
                Login.class);
        // clears all activities so can't go back to register forms
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return true;
    }

}

