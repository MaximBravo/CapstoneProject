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

    public void startAlarmManager(Context context) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.v("Initializer", firebaseUser.getDisplayName());


        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(context, NotificationReciever.class);

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 10);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                10*1000,
                pendingIntent);

    }
}
