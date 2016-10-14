package com.example.nathan.myday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.nathan.myday.Config.AppConfig;

import java.util.Calendar;

public class Settings extends AppCompatActivity {


    Button confirmButton;
    Button changePWButton;
    RadioGroup radioReminderGroup;
    RadioButton selectedReminderRadioButton;

    // alarm handling
    private PendingIntent pendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(R.string.title_activity_settings);





        confirmButton = (Button) findViewById(R.id.confirmSettings);
        confirmButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                // commit setting changes and return to MainActivity





                getSelectedReminderStatus(); // check which radio button option is selected
                if (AppConfig.remindersEnabled) {
                    //enableReminders();
                    // set 3 reminder times
                   // enableReminderAt24HourTime(AppConfig.MORNING_HH, AppConfig.MORNING_MM);
                    enableReminderAt24HourTime(AppConfig.NOON_ALARM, AppConfig.NOON_HH, AppConfig.NOON_MM);
                   // enableReminderAt24HourTime(AppConfig.EVE_HH, AppConfig.EVE_MM);

                }
                else {
                    disableReminders();
                }
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        changePWButton = (Button) findViewById(R.id.changePasswordButton);
        changePWButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v2) {
                // link to change password activity screen





                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        // reminder scheduling end

    }

    private boolean getSelectedReminderStatus() {
        radioReminderGroup = (RadioGroup) findViewById(R.id.remindersRadio);
        int selectedID = radioReminderGroup.getCheckedRadioButtonId();
        selectedReminderRadioButton = (RadioButton) findViewById(selectedID);
        String optionChosen = selectedReminderRadioButton.getText().toString();

        if(optionChosen.equals("Enabled") || optionChosen.equals("Ar Enabled") ) { // need to change Ar Enabled to actual Arabic string
            AppConfig.remindersEnabled = true;
            return true;
        }
        else {
            AppConfig.remindersEnabled = false;
            return false;
        }
    }

    private void enableReminders() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                                    interval, pendingIntent);
        Toast.makeText(this, "Reminders Enabled", Toast.LENGTH_SHORT).show();

    }

    private void disableReminders() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Reminders Disabled", Toast.LENGTH_SHORT).show();
    }

    public void enableReminderAt24HourTime(int id, int HH, int MM) {

        int interval = 1000 * 60 * 60; // interval in milliseconds -> set to 0?

        /* Set the alarm to start at HH:MM  */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, HH);
        calendar.set(Calendar.MINUTE, MM);
        long timeInMilliS = calendar.getTimeInMillis();


        // create an pending intent for the given time period
        Intent alarmIntent = new Intent(Settings.this, AlarmReceiver.class);


        // getBroadcast(Context context, int requestCode, Intent intent, int flags)
        PendingIntent sender = PendingIntent.getBroadcast(Settings.this, id, alarmIntent,
                                  pendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Repeating -> every 24 hours (every 5 mins for testing)
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, timeInMilliS, interval, sender);

        Toast.makeText(this, "Reminders Enabled", Toast.LENGTH_SHORT).show();
    }


}


