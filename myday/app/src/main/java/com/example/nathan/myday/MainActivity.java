package com.example.nathan.myday;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    // firebase reference
    private Firebase firebaseRef;
    private AuthData authData;
    ImageButton morning;
    ImageButton afternoon;
    ImageButton evening;
    private String recordtype;
    private String getRecordDate;
    private String isTempPassword;
    private String oldPassword;
    private String actualEmail;
    private String recordDate;
    // File Handling
    private Firebase dataRef;
    private String uid;


    // reminder handling
    public static Intent iMorn;
    public static Intent iNoon;
    public static Intent iEve;

    public static PendingIntent pMorn;
    public static PendingIntent pNoon;
    public static PendingIntent pEve;

    public static AlarmManager alarmManager;

    private TextView currentDate;
    private Firebase nameRef;
    private String name;
    private TextView welcome;

    // time handling
    private Boolean isItMorning;
    private Boolean isItAfternoon;
    private Boolean isItEvening;
    private ImageButton mornButton;
    private ImageButton aftButton;
    private ImageButton eveButton;
    private int newColour = Color.RED;



    //nathan
    private int counter;
    private TextView eng;
    private TextView ar;
    private String[] num_ar;
    private boolean recordSubmissionFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // firebase object
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(AppConfig.FIREBASE_URL);

        // reminder handling
        this.iMorn = new Intent(this, AlarmReceiver.class);
        this.iNoon = new Intent(this, AlarmReceiver.class);
        this.iEve = new Intent(this, AlarmReceiver.class);
        this.iMorn.putExtra("ID", AppConfig.MORN_ALARM);
        this.iMorn.putExtra("recType", AppConfig.RECORD_TYPE_MORN);
        this.iNoon.putExtra("ID", AppConfig.NOON_ALARM);
        this.iNoon.putExtra("recType",AppConfig.RECORD_TYPE_NOON);
        this.iEve.putExtra("ID", AppConfig.EVE_ALARM);
        this.iEve.putExtra("recType", AppConfig.RECORD_TYPE_EVE);

        this.pMorn = PendingIntent.getBroadcast(this, AppConfig.MORN_ALARM, iMorn, PendingIntent.FLAG_UPDATE_CURRENT);
        this.pNoon = PendingIntent.getBroadcast(this, AppConfig.NOON_ALARM, iNoon, PendingIntent.FLAG_UPDATE_CURRENT);
        this.pEve = PendingIntent.getBroadcast(this, AppConfig.EVE_ALARM, iEve, PendingIntent.FLAG_UPDATE_CURRENT);

        this.alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // checks what time of day it is to change colours of buttons
        checkTimeConstraints();

        // get current date and set label at bottom of activity as date
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        Date today = Calendar.getInstance().getTime();
        recordDate = df.format(today);
        currentDate = (TextView) findViewById(R.id.currentDate);
        currentDate.append(recordDate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //loading filled/unfilled images:
        this.getPics();

        recordtype="";
        afternoon = (ImageButton) findViewById(R.id.sunButton);
        afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordtype = "afternoon";
                Intent i = new Intent(getApplicationContext(), Record.class);
                i.putExtra("recordtype", recordtype);
                startActivity(i);

            }
        });
        afternoon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(15);
                return false;
            }
        });
        morning = (ImageButton) findViewById(R.id.sunriseButton);
        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordtype = "morning";
                Intent i = new Intent(getApplicationContext(), Record.class);
                i.putExtra("recordtype", recordtype);
                startActivity(i);
            }
        });
        morning.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(15);
                return false;
            }
        });
        evening = (ImageButton) findViewById(R.id.moonButton);
        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordtype = "evening";
                Intent i = new Intent(getApplicationContext(), Record.class);
                i.putExtra("recordtype", recordtype);
                startActivity(i);
            }
        });
        evening.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(15);
                return false;
            }
        });

        // gets user data
        authData = firebaseRef.getAuth();

        // File handling
        uid = authData.getUid();
        // databaseSynchronisationController();

        // checks if name is stored on file - if so returns name, if not returns ""
        name = checkStoredName();

        if (name.equals("")) {
            // gets name to set Welcome + name label
            nameRef = firebaseRef.child("users").child(uid).child("firstname");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue().toString();
                    welcome = (TextView) findViewById(R.id.welcome);
                    welcome.append(" " + name);
                    // store name to file to save time from getting name from firebase constantly
                    storeName(name);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println(firebaseError.toString());
                }
            });
        } else {
            welcome = (TextView) findViewById(R.id.welcome);
            welcome.append(" " + name);
        }


        actualEmail = authData.getProviderData().get("email").toString();
        // checks if temp password used, if so, immediately switches to ChangePassword activity
        isTempPassword = authData.getProviderData().get("isTemporaryPassword").toString();
        if (isTempPassword.equals("true")) {
            Intent i = new Intent(getApplicationContext(), ChangePassword.class);
            Intent intent = getIntent();
            oldPassword = intent.getExtras().getString("password");
            i.putExtra("email", actualEmail);
            i.putExtra("password", oldPassword);
            i.putExtra("reset", "true");
            startActivity(i);
            finish();
        }
        // checks whether user is logged in or not, and if not, takes them back to login screen
        firebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    Intent i2= new Intent(getApplicationContext(),Login.class);
                    startActivity(i2);
                    finish();
                    // user is not logged in
                }
            }
        });

        // File handling
        uid = authData.getUid();

        setupReminders();

    }

    // gets current time and sets booleans for the times of the day then calls function to change colours of buttons
    protected void checkTimeConstraints() {
        isItMorning = false;
        isItAfternoon = false;
        isItEvening = false;
        Calendar calendar = new GregorianCalendar();
        String am_pm;
        int hour = calendar.get( Calendar.HOUR );
        int minute = calendar.get( Calendar.MINUTE );
        // int second = calendar.get(Calendar.SECOND);
        if( calendar.get( Calendar.AM_PM ) == 0 ){
            am_pm = "AM";
            isItMorning = true;
        } else {
            if (hour > 4) {
                am_pm = "PM";
                isItEvening = true;
            } else {
                am_pm = "PM";
                isItAfternoon = true;
            }
        }

        // now we know what time of day it is so can change colours of buttons
        changeColours();
    }

    // changes colour of buttons depending if its their time of day
    protected void changeColours() {

        mornButton = (ImageButton) findViewById(R.id.sunriseButton);
        aftButton = (ImageButton) findViewById(R.id.sunButton);
        eveButton = (ImageButton) findViewById(R.id.moonButton);

        if (isItMorning) {
            aftButton.setBackgroundResource(R.drawable.layout_round_changecolour);
            eveButton.setBackgroundResource(R.drawable.layout_round_changecolour);
        }
        if (isItAfternoon) {
            mornButton.setBackgroundResource(R.drawable.layout_round_changecolour);
            eveButton.setBackgroundResource(R.drawable.layout_round_changecolour);
        }
        if (isItEvening) {
            mornButton.setBackgroundResource(R.drawable.layout_round_changecolour);
            aftButton.setBackgroundResource(R.drawable.layout_round_changecolour);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseSynchronisationController(); // should be run everytime MainActivity becomes focus
        this.getPics();
        checkTimeConstraints();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        switch (id) {
            // go to intro page
            case R.id.action_intro :
                Intent i= new Intent(this,Intro.class);
                // puts fromLogin bool as false so intro doesn't try and get password from this intent as doesn't exist
                i.putExtra("fromLogin", "false");
                startActivity(i);
                return true;
            // log out
            case R.id.action_logout :
                disableAlarms();
                firebaseRef.unauth();
                Intent i2= new Intent(this,Login.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
                finish();

                return true;
            // change language
            case R.id.action_language_change :
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
                Intent intente = new Intent(MainActivity.this, MainActivity.class);
                intente.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intente);
                finish();

                return true;
            // change password
            case R.id.action_changepassword :
                Intent i3 = new Intent(this, ChangePassword.class);
                i3.putExtra("email", actualEmail);
                i3.putExtra("password", oldPassword);
                i3.putExtra("reset", "false");
                startActivity(i3);
                return true;

            }

        return super.onOptionsItemSelected(item);
    }

    protected boolean databaseSynchronisationController() {
        // launch in onCreate of MainActivity
        int skipValue = 0;
        String submissionFileName = getFilenameFromUID();
        String skipFilename = submissionFileName + "_skip";
        int submissionNumLines;

        // 1.a check if submissionFile exists
        if (!checkFileExists(submissionFileName)) return false;

        // 1.b check if skip value = number of lines in submission file, if so delete
        submissionNumLines = countLines(submissionFileName);
        skipValue = readSkip(skipFilename);
        System.out.println("skipValue before: " + skipValue);
        if(skipValue == submissionNumLines) {
            // delete files and break as DB already upto date
            File submissionFile = new File(this.getFilesDir(), submissionFileName);
            File skipFile = new File(this.getFilesDir(), skipFilename);
            submissionFile.delete();
            skipFile.delete();
            System.out.println("Files deleted");
            return true;
        }

        // 2. check if network avaialble
        if (!isNetworkConnected()) return false;

        // 3. read skip value if exists
        skipValue = readSkip(skipFilename);

        // 4. read line(s) 1 at a time -> built DB object, confirm submission, add to skip list and repeat
        readAndSubmitToDB(skipValue);

        // 5. if skip file value == number of lines in submission file, delete the files
        submissionNumLines = countLines(submissionFileName);
        System.out.println("submissionNumLines: " + submissionNumLines);
        System.out.println("skipValue after: " + skipValue);


        return true;
    }



    // read submission file, build submit to DB
    protected void readAndSubmitToDB(int skipVal) {
        String Filename_UID =  getFilenameFromUID();

        try {
            FileInputStream fis = this.openFileInput(Filename_UID);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bReader = new BufferedReader(isr);
            String line;

            int currentLineCounter = 0;
            while ((line = bReader.readLine()) != null) {
                    if(currentLineCounter >= skipVal) {
                        if(!isNetworkConnected()) break;

                        // parse line into index 0 : "date" index 1: "recordType" index 2 : "count"
                        String[] recordData = line.split(" ");
                        // build firebase object
                        addToFirebaseDB(recordData[0],recordData[1],recordData[2],skipVal);
                    }
                    currentLineCounter++;
            }

            bReader.close();
            fis.close();
            isr.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading data file");
        }
    }

    private String getFilenameFromUID() {
        String uid = authData.getUid();
        String filename = uid.replace("-", "");
        return filename;
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void increaseSkipVal() {

        String Filename_UID_Skip = getFilenameFromUID() + "_skip";
        int currentVal = readSkip(Filename_UID_Skip);
        int newVal = currentVal+1;
        String sVal = Integer.toString(newVal); // read current skip value

        FileOutputStream fos;
        try {
            fos = openFileOutput(Filename_UID_Skip, this.MODE_PRIVATE); // private so only readable by this app
            fos.write(sVal.getBytes());
            fos.close();

        } catch(IOException e) {
            Toast.makeText(MainActivity.this, "Error writing skip to file", Toast.LENGTH_SHORT).show();
        }
    }

    private int readSkip(String filename) {
        int value=0;
        if(checkFileExists(filename)) {
            try {
                FileInputStream fis = this.openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bReader = new BufferedReader(isr);
                String line = bReader.readLine();

                value = Integer.parseInt(line);

                bReader.close();
                fis.close();
                isr.close();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading skip file");
            }
        }
        return value;
    }


    private boolean checkFileExists(String filename) {
        File varTmp = new File(this.getFilesDir(), filename);
        boolean exists = varTmp.exists();
        return exists;
    }

    protected void addToFirebaseDB(String recordDate, String recordType, String counter, final int skipVal) {
        dataRef = firebaseRef.child("data").child(uid).child(recordDate).child(recordType);
        dataRef.setValue(Integer.parseInt(counter), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("GOT HERE ! NULL");
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());

                } else {
                    System.out.println("Data saved successfully.");
                    // update skip counter -> proceed to next line
                    increaseSkipVal();
                }
            }
        });
    }

    protected int countLines(String filename) {
        int numLines =0;
        if(checkFileExists(filename)) {
            try {
                LineNumberReader lnr = new LineNumberReader((new FileReader(new File(this.getFilesDir(), filename))));
                lnr.skip(Long.MAX_VALUE);
                numLines = lnr.getLineNumber();
                lnr.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading lines of file");
            }
        }

        return numLines;
    }

    /// Alarm and notification handling ///

    //public void createReminderAtTime(int id, int HH, int MM) {
    private void createReminderAtTime(PendingIntent p, int HH, int MM) {

        int interval = AppConfig.ALARM_DELAY; // interval in milliseconds -> set to 24 hours

        // Set the alarm to start at HH:MM  //
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, HH);
        calendar.set(Calendar.MINUTE, MM);
        long timeInMilliS = calendar.getTimeInMillis();

        // Repeating -> every 24 hours (every 3 mins for testing)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMilliS, interval, p);
    }

    // need function in oncreate to setup the 3 reminders and then persist a flag, subsequent oncreates will see flag and not re create
    private void setupReminders() {
        // check alarms not already setup
        if(!checkFileExists(AppConfig.FILE_ALARMS_ENABLED)) { // for on reboot mod so only do if this file exists

            // Alarms need setting
            createReminderAtTime(pMorn, AppConfig.MORNING_HH, AppConfig.MORNING_MM);
            createReminderAtTime(pNoon, AppConfig.NOON_HH, AppConfig.NOON_MM);
            createReminderAtTime(pEve, AppConfig.EVE_HH, AppConfig.EVE_MM);

            // write set alarm file upon setting
            storeAlarmSetFile();

            System.out.println("REMINDERS enabled");
            return;
        }

        System.out.println("REMINDERS : already enabled ");
        return;
    }

    protected void storeAlarmSetFile() {
        FileOutputStream fos;
        try {
            fos = openFileOutput(AppConfig.FILE_ALARMS_ENABLED, this.MODE_PRIVATE); // overwrite existing file
            fos.write("true".getBytes());
            fos.close();

        } catch(IOException e) {
            Toast.makeText(this, "Error writing alarm flag file" , Toast.LENGTH_SHORT).show();
        }
    }


    // function to deactivate all alarms and delete set flag on logout
    protected void disableAlarms() {

        deleteNotifications();
        deleteAlarmRecords();

        // check if the pending is register, if so cancel it
        alarmManager.cancel(pMorn);
        alarmManager.cancel(pNoon);
        alarmManager.cancel(pEve);

        File alarmFile = new File(this.getFilesDir(), AppConfig.FILE_ALARMS_ENABLED);
        alarmFile.delete();
        System.out.println("LOGOUT : Alarm Flag file deleted");

        // temporary toast
        System.out.println("LOGOUT : alarms disabled");

    }

    private void deleteNotifications() {
        NotificationManager noti = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        noti.cancel(AppConfig.MORN_ALARM);
        noti.cancel(AppConfig.NOON_ALARM);
        noti.cancel(AppConfig.EVE_ALARM);
    }

    private void deleteAlarmRecords() {
        String FILENAME_MORN = AppConfig.FILE_NOTI_recordType+AppConfig.RECORD_TYPE_MORN;
        String FILENAME_NOON = AppConfig.FILE_NOTI_recordType+AppConfig.RECORD_TYPE_NOON;
        String FILENAME_EVE = AppConfig.FILE_NOTI_recordType+AppConfig.RECORD_TYPE_EVE;

        File FILE_MORN = new File(this.getFilesDir(), FILENAME_MORN);
        File FILE_NOON = new File(this.getFilesDir(), FILENAME_NOON);
        File FILE_EVE = new File(this.getFilesDir(), FILENAME_EVE);

        FILE_MORN.delete();
        FILE_NOON.delete();
        FILE_EVE.delete();

        System.out.println("LOGOUT : Alarm Records Deleted");
    }

    // check stored name in file
    protected String checkStoredName() {
        String FILE_UID_NAME = getFilenameFromUID() + "_NAME";
        String line;

        if (checkFileExists(FILE_UID_NAME)) {

            // if file exists, read line to get stored name
            try {
                FileInputStream fis = this.openFileInput(FILE_UID_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bReader = new BufferedReader(isr);
                name = bReader.readLine(); // line is user's first name
                bReader.close();
                fis.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading name from file");
            }
        }
        else {
            // if file !exist set name to ""
            name = "";
        }
        return name;
    }

    // function to store name in file
    protected void storeName(String name) {
        String FILE_UID_NAME = getFilenameFromUID() + "_NAME";
        FileOutputStream fos;
        try {
            fos = openFileOutput(FILE_UID_NAME, this.MODE_PRIVATE); // overwrite existing file
            fos.write(name.getBytes());
            fos.close();
        } catch(IOException e) {
            Toast.makeText(MainActivity.this, "Error writing name to file" , Toast.LENGTH_SHORT).show();
        }
    }

    /* checks internal storage to see if the relevant record type has been updated today
        recordType = AppConfig.RECORD_TYPE_MORN | AppConfig.RECORD_TYPE_NOON | AppConfig.RECORD_TYPE_EVE
        No submission returns FALSE = show icon outline,
        submission returns TRUE = show icon filled in
     */
    protected boolean checkForSubmission(String recordType) {
        String FILE = AppConfig.FILE_NOTI_recordType + recordType;
        String line;

        if (checkFileExists(FILE)) {
            // if file exists, read and parse line
            try {
                FileInputStream fis = this.openFileInput(FILE);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bReader = new BufferedReader(isr);
                line = bReader.readLine();

                bReader.close();
                fis.close();
                isr.close();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date today = Calendar.getInstance().getTime();
                String todayDate = df.format(today);

                // compare the stored recordDate with the current Record Date
                if(line.equals(todayDate))  {
                    return true; // dates match so show icon filled
                }
                else {
                    return false; // dates don't match so show icon outline
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Submission Check :: Error reading record file in Main");
            }
        }
        return false; // file doesn't exist
    }

    protected void getPics() {
        if (checkForSubmission("morning")) {
            this.mornButton.setImageResource(R.mipmap.sunrise);
        } else {
            this.mornButton.setImageResource(R.mipmap.sunrise_outline_2);
        }
        if (checkForSubmission("afternoon")) {
            this.aftButton.setImageResource(R.mipmap.sun);
        } else {
            this.aftButton.setImageResource(R.mipmap.sun_outline);
        }
        if (checkForSubmission("evening")) {
            this.eveButton.setImageResource(R.mipmap.moon_icon);
        } else {
            this.eveButton.setImageResource(R.mipmap.moon_outline);
        }
    }


}
