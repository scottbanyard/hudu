package com.example.nathan.myday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register_2 extends AppCompatActivity {
    Button next_button2;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String confirmpassword;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;

    // firebase object
    Firebase firebaseRef;

    // variables for password validation
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,22})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        // firebase objects
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);

        // get firstname and last name from register_1 activity
        firstname = getIntent().getExtras().getString("firstname");
        lastname = getIntent().getExtras().getString("lastname");
        //Toast.makeText(register_2.this, firstname + " " + lastname, Toast.LENGTH_LONG).show();

        // next button to register page 2
        next_button2 = (Button) findViewById(R.id.button_next2);
        next_button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                next_button_click();
            }
        });
    }

    // function that checks password is valid
    private boolean validatePassword(String password, String confirmpassword) {
        if (!(password.equals(confirmpassword))) {
            Toast.makeText(register_2.this, "Passwords are not the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(validate(password))) {
            Toast.makeText(register_2.this, "Password must be 6 characters, must be alphanumeric with a capital.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    // regular expression validation of password
    public boolean validate(String password){
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    // checks if email is valid
    public boolean emailValidation(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    Boolean next_button_click() {
        // get email and passwords from fields
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);

        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();
        confirmpassword = confirmPassword.getText().toString();

        Intent i = new Intent(getApplicationContext(),
                register_3.class);

        // checks if fields are empty
        if (!(email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty())) {
            // if email is not valid
            if (!(emailValidation(email))) {
                Toast.makeText(register_2.this, "Please enter a valid e-mail.", Toast.LENGTH_SHORT).show();
                return false;
            }
            // checks if password is valid
            if (validatePassword(password, confirmpassword)) {
                // pass firstname, lastname, email and password to register 3 activity
                i.putExtra("firstname", firstname);
                i.putExtra("lastname", lastname);
                i.putExtra("email", inputEmail.getText().toString());
                i.putExtra("password", inputPassword.getText().toString());

                startActivity(i);
                return true;
            }
        } else {
            Toast.makeText(register_2.this, "Please complete fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}