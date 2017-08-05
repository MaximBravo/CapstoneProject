package com.maximbravo.chongo3.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import com.maximbravo.chongo3.Word;
/**
 * Created by Maxim Bravo on 8/4/2017.
 */

public class NotificationReciever extends BroadcastReceiver {
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private ArrayList<Word> packToStudy;

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        root = database.getReference(mCurrentUser.getUid());

        packToStudy = new ArrayList<Word>();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot deck : dataSnapshot.getChildren()) {
                    for(DataSnapshot word : deck.getChildren()) {
                        String currentCharacter = word.getKey();
                        boolean addToStudyList = false;
                        HashMap<String, String> allDetails = new HashMap<String, String>();
                        for(DataSnapshot details : word.getChildren()) {
                            allDetails.put(details.getKey(), (String) details.getValue());
                            if(details.getKey().equals("rounds")) {
                                if(Integer.parseInt(""+details.getValue()) <= 0) {
                                    addToStudyList = true;
                                }
                            }
                        }
                        if (addToStudyList) {
                            Word toStudy = new Word(currentCharacter, allDetails);
                            packToStudy.add(toStudy);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.v("NotificationReciever**", firebaseUser.getDisplayName());

        if(packToStudy.size() > 0) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent repeatingIntent = new Intent(context, QuizActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            repeatingIntent.putExtra("pack", packToStudy);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentTitle(packToStudy.get(0).getCharacter())
                    .setContentText("click to study")
                    .setAutoCancel(true);

            notificationManager.notify(100, builder.build());
        }
    }
}
