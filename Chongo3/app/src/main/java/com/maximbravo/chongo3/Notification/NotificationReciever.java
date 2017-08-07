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
    private ArrayList<String> packToStudy;


    @Override
    public void onReceive(final Context context, Intent intent) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        root = database.getReference(mCurrentUser.getUid());

        packToStudy = new ArrayList<String>();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Word firstWord = null;
                for(DataSnapshot deck : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap<String, String>> words = new HashMap<>();
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
                                allDetails.put(details.getKey(), "" + (Integer.parseInt(""+details.getValue())-1));

                            }
                        }
                        Word toStudy = new Word(currentCharacter, allDetails);
                        //toStudy.updateSelf(root.child(toStudy.getInDeck()));

                        HashMap<String, String> details = new HashMap<>();
                        details.putAll(allDetails);
                        words.put(currentCharacter, details);

                        if (addToStudyList) {
                            if(firstWord == null) {
                                firstWord = toStudy;
                            }
                            packToStudy.add(toStudy.toString());
                        }
                    }
                    HashMap<String, Object> deckMap = new HashMap<>();
                    deckMap.put(deck.getKey(), words);
                    root.updateChildren(deckMap);
                }

                if(packToStudy.size() > 0) {
                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent repeatingIntent = new Intent(context, QuizActivity.class);
                    repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    repeatingIntent.putExtra("pack", packToStudy);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 100,
                            repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(android.R.drawable.btn_star)
                            .setContentTitle(firstWord.getCharacter())
                            .setContentText("click to study")
                            .setAutoCancel(true);

                    notificationManager.notify(100, builder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.v("NotificationReciever**", firebaseUser.getDisplayName());


    }
}
