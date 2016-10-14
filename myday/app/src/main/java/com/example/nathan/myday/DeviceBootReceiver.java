package com.example.nathan.myday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nathan.myday.Config.AppConfig;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Jamie on 05/03/16.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    private Context context;

    // reminder handling
    public static Intent iMorn;
    public static Intent iNoon;
    public static Intent iEve;

    public static PendingIntent pMorn;
    public static PendingIntent pNoon;
    public static PendingIntent pEve;

    public static AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            this.context = context;

            // reminder handling
            this.iMorn = new Intent(context, AlarmReceiver.class);
            this.iNoon = new Intent(context, AlarmReceiver.class);
            this.iEve = new Intent(context, AlarmReceiver.class);
            this.iMorn.putExtra("ID", AppConfig.MORN_ALARM);
            this.iMorn.putExtra("recType", AppConfig.RECORD_TYPE_MORN);
            this.iNoon.putExtra("ID", AppConfig.NOON_ALARM);
            this.iNoon.putExtra("recType",AppConfig.RECORD_TYPE_NOON);
            this.iEve.putExtra("ID", AppConfig.EVE_ALARM);
            this.iEve.putExtra("recType", AppConfig.RECORD_TYPE_EVE);

            this.pMorn = PendingIntent.getBroadcast(context, AppConfig.MORN_ALARM, iMorn, PendingIntent.FLAG_CANCEL_CURRENT); // FLAG_UPDATE_CURRENT
            this.pNoon = PendingIntent.getBroadcast(context, AppConfig.NOON_ALARM, iNoon, PendingIntent.FLAG_CANCEL_CURRENT);
            this.pEve = PendingIntent.getBroadcast(context, AppConfig.EVE_ALARM, iEve, PendingIntent.FLAG_CANCEL_CURRENT);

            this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            setupReminders();
        }
    }

    private boolean checkFileExists(String filename) {
        File varTmp = new File(context.getFilesDir(), filename);
        boolean exists = varTmp.exists();
        return exists;
    }

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
        if(checkFileExists(AppConfig.FILE_ALARMS_ENABLED)) { // for on reboot mod so only do if this file exists

            // Alarms need setting
            createReminderAtTime(pMorn, AppConfig.MORNING_HH, AppConfig.MORNING_MM);
            createReminderAtTime(pNoon, AppConfig.NOON_HH, AppConfig.NOON_MM);
            createReminderAtTime(pEve, AppConfig.EVE_HH, AppConfig.EVE_MM);

            return;
        }

        return;
    }



}


