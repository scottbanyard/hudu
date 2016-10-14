package com.example.nathan.myday;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register_1 extends AppCompatActivity {
    Button next_button;
    private String firstname;
    private String lastname;
    private EditText inputFirstName;
    private EditText inputLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);

        // next button to register page 2
        next_button = (Button) findViewById(R.id.button_next);
        next_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                next_button_click();
            }
        });

        // get firstname and lastnames from fields
        inputFirstName = (EditText) findViewById(R.id.firstname);
        inputLastName = (EditText) findViewById(R.id.lastname);

    }

    Boolean next_button_click() {
        firstname = inputFirstName.getText().toString();
        lastname = inputLastName.getText().toString();

        if (!(firstname.isEmpty() || lastname.isEmpty())) {
            Intent i = new Intent(getApplicationContext(),
                    register_2.class);

            // pass firstname and lastname to register 2 activity
            i.putExtra("firstname", inputFirstName.getText().toString());
            i.putExtra("lastname", inputLastName.getText().toString());

            startActivity(i);
            return true;
        } else {
            Toast.makeText(register_1.this, "Please complete fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}




