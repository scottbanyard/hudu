package com.example.nathan.myday;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Locale;

public class Login extends AppCompatActivity {

    // firebase reference
    private Firebase firebaseRef;
    private Firebase checkShowIntro;

    Button login_button;
    Button register_button;
    Button switch_button;
    Button forgotpassword_button;
    private EditText inputEmail;
    private EditText inputPassword;
    private String email;
    private String password;
    private String error;
    private String fbError;
    private AuthData authData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // firebase objects
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);


        // If user is already authenticated skips straight to MainActivity across app restarts
        if (firebaseRef.getAuth() != null) {
            authData = firebaseRef.getAuth();
            System.out.println(authData.toString());
            // user authenticated
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(i);
            finish();
        }



        login_button = (Button) findViewById(R.id.button_login);
        login_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                login_button_click();
            }
        });

        register_button = (Button) findViewById(R.id.button_register);
        register_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                register_button_click();
            }
        });

        switch_button = (Button) findViewById(R.id.button_change_language);
        switch_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                switch_button_click();
            }
        });

        forgotpassword_button = (Button) findViewById(R.id.button_forgotpassword);
        forgotpassword_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                forgotPassword();
            }
        });
    }

    // function that sends reset email to email entered which contains a temp password
    protected void forgotPassword() {
        inputEmail = (EditText) findViewById(R.id.email);
        email = inputEmail.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(Login.this, "Please enter e-mail.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseRef.resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    System.out.println("Reset password e-mail has been sent successfully.");
                    Toast.makeText(Login.this, "Temporary password sent to e-mail.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    fbError = firebaseError.toString();
                    switch (firebaseError.getCode()) {
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            error = "User not registered.";
                            break;
                        case FirebaseError.INVALID_EMAIL:
                            error = "Details are incorrect.";
                            break;
                        case FirebaseError.NETWORK_ERROR:
                            error = "Error connecting to network.";
                            break;
                        default:
                            error = "Unknown Error occured.";
                            break;
                    }

                    Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                    System.out.println(fbError);
                }
            });
        }
    }

    // attempts login using email and password
    protected void attemptLogin() {
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // takes user to intro page
                Intent i = new Intent(getApplicationContext(),
                            Intro.class);
                // passes over fromLogin bool and password for reset pw
                i.putExtra("password", password);
                i.putExtra("fromLogin", "true");
                startActivity(i);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                fbError = firebaseError.toString();
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        error = "User not registered.";
                        break;
                    case FirebaseError.INVALID_EMAIL:
                        error = "Details are incorrect.";
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        error = "Details are incorrect.";
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        error = "Error connecting to network.";
                        break;
                    case FirebaseError.INVALID_TOKEN:
                        error = "Temporary password has expired.";
                        break;
                    default:
                        error = "Unknown Error occured.";
                        break;
                }

                Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                System.out.println(fbError);
            }
        };

        firebaseRef.authWithPassword(email, password, authResultHandler);
    }


    Boolean login_button_click() {
        // gets details from email/password fields
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();

        // checks if fields are not empty then attempts login
        if (!(email.isEmpty() || password.isEmpty())) {
            attemptLogin();
        } else {
            Toast.makeText(Login.this, "Please complete both fields.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    Boolean register_button_click() {
        Intent i = new Intent(getApplicationContext(),
                register_1.class);
        startActivity(i);
        return true;
    }

    Boolean switch_button_click() {
        Locale current=getResources().getConfiguration().locale;
        String loadlange;
        if (current.toString().equals("ar")) {
            loadlange = "en";
        }
        else
        {
            loadlange="ar";
        }
        Locale localee = new Locale(loadlange);
        Locale.setDefault(localee);
        Configuration confige = new Configuration();
        confige.locale=localee;
        getBaseContext().getResources().updateConfiguration(confige,getBaseContext().getResources().getDisplayMetrics());
        Intent intente = new Intent(Login.this, Login.class);
        intente.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intente);

        return true;
    }

}