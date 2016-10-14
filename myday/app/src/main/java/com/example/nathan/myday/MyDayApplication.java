package com.example.nathan.myday;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Jamie on 23/02/16.
 * Enables disk persistence  to preserve data across app restarts (in theory)
 */
public class MyDayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

    }
}
