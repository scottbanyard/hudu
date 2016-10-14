package com.example.nathan.myday;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Record extends AppCompatActivity {

    private int counter;
    Button up;
    Button down;
    Button submit;
    private TextView eng;
    private TextView ar;
    private String[] num_ar;
    // firebase reference
    private Firebase firebaseRef;
    private Firebase dataRef;
    private String recordType;
    private String recordDate;
    private AuthData authData;
    private String uid;
    private ImageView pic;

    private boolean recordSubmissionFlag;

    // notification handling
    private NotificationManager noti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setTitle(R.string.title_activity_record);

        // firebase objects
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);

        // get record type i.e. morning/afternoon/evening
        Intent intent = getIntent();
        recordType = intent.getExtras().getString("recordtype");


        // get current date
        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date today = Calendar.getInstance().getTime();
        recordDate = df.format(today);

        // get uid
        authData = firebaseRef.getAuth();
        uid = authData.getUid();


        num_ar = new String[10];
        num_ar[0]="٠";
        num_ar[1]="١";
        num_ar[2]="٢";
        num_ar[3]="٣";
        num_ar[4]="٤";
        num_ar[5]="٥";
        num_ar[6]="٦";
        num_ar[7]="٧";
        num_ar[8]="٨";
        num_ar[9]="٩";

        // checks if counter needs updating and updates if necessary
        //checkCounter();
        this.recordSubmissionFlag = checkStorageCounter();

        up = (Button) findViewById(R.id.button_up);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 9) {
                    counter++;
                    eng = (TextView) findViewById(R.id.num_en);
                    ar = (TextView) findViewById(R.id.num_ar);
                    ar.setText(num_ar[counter]);
                    eng.setText(Integer.toString(counter));
                }
            }
        });
        up.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(15);
                return false;
            }
        });

        down = (Button) findViewById(R.id.button_down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter > 0) {
                    counter--;
                    eng = (TextView) findViewById(R.id.num_en);
                    ar = (TextView) findViewById(R.id.num_ar);
                    ar.setText(num_ar[counter]);
                    eng.setText(Integer.toString(counter));
                }
            }
        });
        down.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(15);
                return false;
            }
        });

        submit = (Button) findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addToDB(recordDate, recordType);
                addToFile(recordDate, recordType);
                deleteNotification();
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });
        submit.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(20);
                return false;
            }
        });

        pic = (ImageView) findViewById(R.id.imageView);
        if (recordType.equals("morning")) {
            if (recordSubmissionFlag) {
                // set filled
                pic.setImageResource(R.mipmap.sunrise);
            }
            else {
                pic.setImageResource(R.mipmap.sunrise_outline_2);
            }
        } else if (recordType.equals("afternoon")) {
            if (recordSubmissionFlag) {
                // set filled
                pic.setImageResource(R.mipmap.sun);
            }
            else {
                pic.setImageResource(R.mipmap.sun_outline);
            }
        } else if (recordType.equals("evening")) {
            if (recordSubmissionFlag) {
                // set filled
                pic.setImageResource(R.mipmap.moon_icon);
            }
            else {
                pic.setImageResource(R.mipmap.moon_outline);
            }
        }

    }

    // updates counter
    protected void updateCounter(int counter) {
        this.counter = counter;
        eng = (TextView) findViewById(R.id.num_en);
        eng.setText(Integer.toString(counter));
        eng.setGravity(Gravity.CENTER);
        ar = (TextView) findViewById(R.id.num_ar);
        ar.setText(num_ar[counter]);
        ar.setGravity(Gravity.CENTER);
    }


    // checks internal storage for previous counter value
    protected boolean checkStorageCounter() {
        String Filename_UID_recordType = getFilenameFromUID() + "_" + recordType;
        String line;

        if (checkFileExists(Filename_UID_recordType)) {

            // if file exists, read and parse line
            try {
                FileInputStream fis = this.openFileInput(Filename_UID_recordType);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bReader = new BufferedReader(isr);
                line = bReader.readLine();

                // parse line into index 0 : "date" index 1: "recordType" index 3 : "count"
                String[] parsedLineData = line.split(" ");

                bReader.close();
                fis.close();
                isr.close();

                // compare the stored recordDate with the current Record Date
                if(parsedLineData[0].equals(recordDate)) {
                    // set counter to stored value as submission is from same day
                    int val = Integer.parseInt(parsedLineData[2]);
                    updateCounter(val);
                    // set imageConditionFlag to True -> load filled image
                    return true;
                }
                else {
                    // set counter to 0 as submission is from a previous day
                    updateCounter(0);
                    // set imageConditionFlag to False -> load outlined image
                    return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading counter record file");
            }
        }
        else {
            // if file !exist set counter to 0
            updateCounter(0);
        }
        return false;
    }


    private boolean checkFileExists(String filename) {
        File varTmp = new File(this.getFilesDir(), filename);
        boolean exists = varTmp.exists();
        return exists;
    }

    // checks database for any current value for the record type and updates counter to it <-- depreciated method
    protected void checkCounter() {
        dataRef = firebaseRef.child("data").child(uid).child(recordDate).child(recordType);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    counter = Integer.parseInt(snapshot.getValue().toString());
                    updateCounter(counter);
                } else {
                    System.out.println("Doesn't exist");
                    counter = 0;
                    updateCounter(counter);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println(firebaseError.toString());
                counter = 0;
                updateCounter(counter);
            }
        });
    }


    // adds record to database
    protected void addToDB(String recordDate, String recordType) {
        dataRef = firebaseRef.child("data").child(uid).child(recordDate).child(recordType);
        dataRef.setValue(counter, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("GOT HERE ! NULL");
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });

    }


    // converts record instance into a string :: recordDate recordType counter\n
    protected String valuesToString(int counter, String recordDate, String recordType) {
        String s;
        s = recordDate + " " + recordType + " " + Integer.toString(counter) + "\n";
        return s;
    }


    // store a text file named with current UID storing strings of unsent records
    protected void storeRecordInternal(String record) {
        String Filename_UID = getFilenameFromUID();
        FileOutputStream fos;
        try {
            fos = openFileOutput(Filename_UID, this.MODE_APPEND); // append to bottom if file exists
            fos.write(record.getBytes());
            fos.close();

        } catch(IOException e) {
            Toast.makeText(Record.this, "Error writing to file" , Toast.LENGTH_SHORT).show();
        }
    }


    private String getFilenameFromUID() {
        String uid = authData.getUid();
        String filename = uid.replace("-","");
        return filename;
    }


    protected void addToFile(String recordDate, String recordType) {
        String record = valuesToString(counter, recordDate, recordType);
        storeRecordInternal(record);
        storeCounterRecord(record);
        System.out.println("Stored record to file");
        storeNotificationRecord();
        System.out.println("Stored notification record to file");
        //String data = recordType + ": " + counter + " - " + "submitted successfully.";
        String data = recordType + " " + counter + " submitted";
        //Toast.makeText(Record.this, data, Toast.LENGTH_LONG).show();
    }


    protected void storeCounterRecord(String record) {
        String Filename_UID_recordType = getFilenameFromUID() + "_" + recordType;
        FileOutputStream fos;
        try {
            fos = openFileOutput(Filename_UID_recordType, this.MODE_PRIVATE); // overwrite existing file
            fos.write(record.getBytes());
            fos.close();

        } catch(IOException e) {
            Toast.makeText(Record.this, "Error writing counter to file" , Toast.LENGTH_SHORT).show();
        }
    }

    protected void storeNotificationRecord() {
        String FILE = AppConfig.FILE_NOTI_recordType + recordType;
        FileOutputStream fos;
        try {
            fos = openFileOutput(FILE, this.MODE_PRIVATE); // overwrite existing file
            fos.write(recordDate.getBytes());
            fos.close();

        } catch(IOException e) {
            Toast.makeText(Record.this, "Error writing notification record to file" , Toast.LENGTH_SHORT).show();
        }
    }

    // method to delete any corresponding pending notifications upon submission
    private void deleteNotification() {
        NotificationManager noti = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(recordType.equals(AppConfig.RECORD_TYPE_MORN)) {
            noti.cancel(AppConfig.MORN_ALARM);
        }
        else if (recordType.equals(AppConfig.RECORD_TYPE_NOON)) {
            noti.cancel(AppConfig.NOON_ALARM);
        }
        else if ( recordType.equals(AppConfig.RECORD_TYPE_EVE)) {
            noti.cancel(AppConfig.EVE_ALARM);
        }
    }

}

