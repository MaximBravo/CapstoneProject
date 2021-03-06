package com.maximbravo.chongo3.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

/**
 * Created by Maxim Bravo on 8/4/2017.
 */

public class Initializer {

    public static final int REQUEST_CODE = 100;
    private boolean testing = false;//TEST***false;

    public void startAlarmManager(Context context) {


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {

            Calendar calendar = Calendar.getInstance();
            Intent intent = new Intent(context, NotificationReciever.class);

            int firstAlarm = 10;
            int interval = 5 * 60 * 1000;
            if(testing) {
                firstAlarm = 5;
                interval = 60 * 1000;
            }
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + firstAlarm);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    interval,
                    pendingIntent);
        }

    }

    public void stopAlarmManager(Context context) {
        Intent intent = new Intent(context, NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
