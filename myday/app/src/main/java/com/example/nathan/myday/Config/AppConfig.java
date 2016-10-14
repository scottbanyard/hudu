package com.example.nathan.myday.Config;

public class AppConfig {
    // firebase url to our database
    public static String FIREBASE_URL = "https://myday.firebaseio.com/";
    public static boolean remindersEnabled = true;

    // (default reminder) times 24hour (HH:MM)
    public static final int MORNING_HH = 11;
    public static final int MORNING_MM = 45;
    public static final int NOON_HH = 16;
    public static final int NOON_MM = 30;
    public static final int EVE_HH = 21;
    public static final int EVE_MM = 00;

    public static final int ALARM_DELAY = 1000 * 60 * 60 * 24;

    public static final int MORN_ALARM = 100;
    public static final int NOON_ALARM = 200;
    public static final int EVE_ALARM = 300;

    public static final String FILE_ALARMS_ENABLED = "alarms_enabled";

    public static final String FILE_NOTI_recordType = "file_alarm_";  // "morning" || "afternoon" || "evening"
    public static final String RECORD_TYPE_MORN = "morning";
    public static final String RECORD_TYPE_NOON = "afternoon";
    public static final String RECORD_TYPE_EVE  = "evening";
}