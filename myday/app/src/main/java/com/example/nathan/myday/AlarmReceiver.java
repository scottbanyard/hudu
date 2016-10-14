package com.example.nathan.myday;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.nathan.myday.Config.AppConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jamie on 05/03/16.
 */
public class AlarmReceiver  extends BroadcastReceiver {

    private String recordDate;
    private Context context;
    private String recordType;
    private int id;



    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        // get current date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date today = Calendar.getInstance().getTime();
        recordDate = df.format(today);

        // get "ID" extra from the incoming intent
        this.id = intent.getExtras().getInt("ID");
        // get "recordType" extra from incoming intent
        this.recordType = intent.getExtras().getString("recType");


        if(id==100) {
            System.out.println("ALARM : Morning");
            if(checkSendNotification()) {
                buildNotification();
            }

        }
        else if (id==200) {
            System.out.println("ALARM : Afternoon");
            if(checkSendNotification()) {
                buildNotification();
            }
        }
        else if (id==300) {
            System.out.println("ALARM : Evening");
            if(checkSendNotification()) {
                buildNotification();
            }
        }
    }



    private void buildNotification() {
        /*
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.notification_large_icon); */

        int pink = ContextCompat.getColor(context, R.color.pink_neon);


        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String contentTitle = context.getResources().getString(R.string.app_name) +  " " +
                context.getResources().getString(R.string.message_box_title)+ " " + recordType;
        String contentText = context.getResources().getString(R.string.context_box_title)+ " " + recordType;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(contentTitle)  //context.getResources().getString(R.string.message_box_title)
                        .setContentText(contentText)
                        .setColor(pink)
                        .setOnlyAlertOnce(true)
                        .setSound(uri)
                        .setVibrate(new long[]{200, 200, 200})
                        .setPriority(2)
                        .setVisibility(1) // VISIBILITY_PUBLIC
                        .setLights(Color.RED, 300,300 )
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, Login.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Login.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
        System.out.println(recordType + " : Sent Notification");
    }



    // checks internal storage to see if the relevant record type has been updated today
    protected boolean checkSendNotification() { // send = true, don't send = false
        String FILE = AppConfig.FILE_NOTI_recordType + recordType;
        String line;

        if (checkFileExists(FILE)) {
            // if file exists, read and parse line
            try {
                FileInputStream fis = context.openFileInput(FILE);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bReader = new BufferedReader(isr);
                line = bReader.readLine();

                bReader.close();
                fis.close();
                isr.close();

                // compare the stored recordDate with the current Record Date if same don't build
                if(line.equals(recordDate))  {

                    return false; // dates match so don't build notification
                }
                else {

                    return true; // dates don't match so build notification
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading notification record file");
            }
        }
        return true; // file doesn't exist
    }


    private boolean checkFileExists(String filename) {
        File varTmp = new File(context.getFilesDir(), filename);
        boolean exists = varTmp.exists();
        return exists;
    }

}

