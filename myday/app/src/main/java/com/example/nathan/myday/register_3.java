package com.example.nathan.myday;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class register_3 extends AppCompatActivity {
    Button next_button;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String dob;
    private String nationality;
    private EditText inputDOB;
    private EditText inputGender;
    private EditText inputNationality;
    private RadioGroup radioGenderGroup;
    private RadioButton selectedGenderButton;
    private String genderChosen;
    private Spinner spinner;
    private String country;


    // DATE OF BIRTH FUNCTIONS - makes calendar
    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    // updates text on date of birth field according to selection
    private void updateLabel() {

        String myFormat = "yyyy-MM-dd"; // format of string
        ////// TO DO : WORRY ABOUT LATER ON DATE FORMATS WITH UK / OTHER ETC /////
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        inputDOB.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_3);

        // get firstname, lastname, email, password and uid form register_2 activity
        firstname = getIntent().getExtras().getString("firstname");
        lastname = getIntent().getExtras().getString("lastname");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");

        // get gender, dob and nationality from text fields
        ////////// TO DO : GENDER RADIO BUTTON AND DROP DOWN NATIONALITY AND CALENDAR FOR DOB ////////////
        inputDOB = (EditText) findViewById(R.id.dob);
        //inputNationality = (EditText) findViewById(R.id.nationality);

        // next button
        next_button = (Button) findViewById(R.id.button_next3);
        next_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                // finds radio buttons
                radioGenderGroup = (RadioGroup) findViewById(R.id.genderRadio);
                // gets the selected radio button's ID
                int selectedID = radioGenderGroup.getCheckedRadioButtonId();
                // gets the certain button's name using the selected ID
                selectedGenderButton = (RadioButton) findViewById(selectedID);
                // if no button selected, set genderChosen to empty, when selected, set to name of button
                if (selectedGenderButton == null) {
                    genderChosen = "";
                } else {
                    genderChosen = selectedGenderButton.getText().toString();
                }
                // sets gender depending on radio button ID (2131492984 = male, 2131492985 = female), else if empty, set empty
                if (genderChosen.equals("Male")) { // Male for arabic needs to go here
                    gender = "M";
                } else if (genderChosen.equals("Female")) {
                    gender = "F";
                } else {
                    gender = "";
                    System.out.println("GENDER RADIO BUTTON ERROR: Gender not selected!");
                }
                //Toast.makeText(register_3.this, gender, Toast.LENGTH_LONG).show()

                next_button_click();
            }
        });

        // date of birth - brings up calendar when clicked
        inputDOB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(register_3.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // creates spinner and calls an object that will check whether item is selected
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    Boolean next_button_click() {

        spinner = (Spinner) findViewById(R.id.spinner1);
        dob = inputDOB.getText().toString();
        //nationality = inputNationality.getText().toString();
        // gets value of selected item
        country = String.valueOf(spinner.getSelectedItem());
        Intent i = new Intent(getApplicationContext(),
                Terms.class);

        if (!(gender.isEmpty() || dob.isEmpty() || country.isEmpty() || country.equals("Select One"))) {
            // pass firstname, lastname, email and password to register 3 activity
            i.putExtra("firstname", firstname);
            i.putExtra("lastname", lastname);
            i.putExtra("email", email);
            i.putExtra("password", password);
            i.putExtra("gender", gender);
            i.putExtra("dob", dob);
            i.putExtra("country", country);

            startActivity(i);
            return true;
        } else {
            Toast.makeText(register_3.this, "Please complete fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
