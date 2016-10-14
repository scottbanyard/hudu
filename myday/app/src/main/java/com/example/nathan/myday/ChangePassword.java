package com.example.nathan.myday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity {

    // firebase reference
    private Firebase firebaseRef;
    Button confirmButton;
    private EditText inputOldPassword;
    private EditText inputPassword;
    private EditText confirmPassword;
    private String email;
    private String password;
    private String confirmpassword;
    private String oldPassword;
    private String fbError;
    private String error;
    private String isResetPassword;

    // variables for password validation
    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,22})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sets content view as a layout depending on whether changing or resetting password
        Intent i = getIntent();
        isResetPassword = i.getExtras().getString("reset");
        if (isResetPassword.equals("true")) {
            setContentView(R.layout.reset_password);
        } else {
            setContentView(R.layout.change_password);
        }

        // firebase objects
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);

        confirmButton = (Button) findViewById(R.id.confirmChangePassword);
        confirmButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                changePassword();
            }
        });
    }

    // function that checks password is valid
    private boolean validatePassword(String password, String confirmpassword) {
        if (!(password.equals(confirmpassword))) {
            Toast.makeText(ChangePassword.this, "Passwords are not the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(validate(password))) {
            Toast.makeText(ChangePassword.this, "Password must be 6 characters, must be alphanumeric with a capital.", Toast.LENGTH_SHORT).show();
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

    // function that changes password on firebase
    protected void changePassword() {
        inputPassword = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);

        password = inputPassword.getText().toString();
        confirmpassword = confirmPassword.getText().toString();

        Intent intent = getIntent();
        // gets email
        email = intent.getExtras().getString("email");
        // gets oldPassword depending on whether reset password or changing password layout - i.e.
        // if reset password will take from login page, if changing password then will take from
        // old password edit text box
        if (isResetPassword.equals("true")) {
            oldPassword = intent.getExtras().getString("password");
        } else {
            inputOldPassword = (EditText) findViewById(R.id.oldpassword);
            oldPassword = inputOldPassword.getText().toString();
        }

        // checks if fields are empty
        if (!(email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty() || oldPassword.isEmpty())) {
                // checks if password is valid
                if (validatePassword(password, confirmpassword)) {
                    firebaseRef.changePassword(email, oldPassword, password, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            System.out.println("Successfully changed password.");
                            Toast.makeText(ChangePassword.this, "Successfully changed password.", Toast.LENGTH_SHORT).show();
                            // logs out user and redirects to login page so can login with new password
                            firebaseRef.unauth();
                            Intent i = new Intent(getApplicationContext(), Login.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            fbError = firebaseError.toString();
                            switch (firebaseError.getCode()) {
                                case FirebaseError.INVALID_PASSWORD:
                                    error = "Details are incorrect.";
                                    break;
                                case FirebaseError.NETWORK_ERROR:
                                    error = "Error connecting to network.";
                                    break;
                                default:
                                    error = "Unknown Error occured.";
                                    break;
                            }

                            Toast.makeText(ChangePassword.this, error, Toast.LENGTH_SHORT).show();
                            System.out.println(fbError);
                        }
                    });
                }
        } else {
            Toast.makeText(ChangePassword.this, "Please complete fields.", Toast.LENGTH_SHORT).show();
        }
    }
}
