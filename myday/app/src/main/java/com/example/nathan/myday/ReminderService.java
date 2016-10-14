package com.example.nathan.myday;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Jamie on 09/03/16. Service Class to run in background
 */
public class ReminderService extends IntentService {


    ReminderService() {
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String intentTpye = intent.getExtras().getString("caller");
        if(intentTpye == null) return;
        if(intentTpye.equals("RebootReceiver")) {
            // do reboot stuff -> schedule alarms and maybe reschedule notifications

        }
    }

}
