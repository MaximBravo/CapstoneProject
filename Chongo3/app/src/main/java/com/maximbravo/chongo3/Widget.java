package com.maximbravo.chongo3;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maximbravo.chongo3.Notification.QuizActivity;

import java.util.ArrayList;
import java.util.HashMap;




/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    private static final String TAG = "Widget";
    private DatabaseReference root;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private ArrayList<String> packToStudy;
    private static int position = 0;

    void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.click_to_study_pack_freely);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(context, QuizActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.container, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }

        //ComponentName me=new ComponentName(context, Widget.class);
        updatePack(context, appWidgetManager, appWidgetIds);
        //(appWidgetManager.updateAppWidget(me, buildUpdate(context, appWidgetManager, appWidgetIds));
    }

    private RemoteViews buildUpdate(final Context context, int[] appWidgetIds) {
        RemoteViews updateViews=new RemoteViews(context.getPackageName(),
                R.layout.widget);

        Log.i(TAG, "***Calling updatePack from buildUpdate");




        String character = context.getString(R.string.havent_signed_in);
        String pinyin = "";
        String definition = "";
        if(packToStudy != null && packToStudy.size() > 0) {
            Word currentWord = Word.fromString(context, packToStudy.get(position));
            if (currentWord != null) {
                character = currentWord.getCharacter();
                pinyin = currentWord.getPinyin();
                definition = currentWord.getDefinition();
            }
            if(packToStudy.size()-1 == position) {
                position = 0;
            } else {
                position++;
            }
        }

        updateViews.setTextViewText(R.id.character_widget, character);
        updateViews.setTextViewText(R.id.pinyin_widget, pinyin);
        updateViews.setTextViewText(R.id.definition_widget, definition);


        Intent intent = new Intent(context, Widget.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        updateViews.setOnClickPendingIntent(R.id.container, pi);

        return updateViews;
    }
    @Override
    public void onEnabled(final Context context) {
//        Log.i(TAG, "***Calling updatePack from onEnabled");
//        updatePack(context);
    }

    void updatePack(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // Enter relevant functionality for when the first widget is created
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "***updatePack called");
        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        if(database != null && mCurrentUser != null) {
            root = database.getReference(mCurrentUser.getUid());

            packToStudy = new ArrayList<>();
            Log.i(TAG, "***root.addListenerForSingleValueEvent called");

            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot deck : dataSnapshot.getChildren()) {
                        for (DataSnapshot word : deck.getChildren()) {
                            String currentCharacter = word.getKey();
                            HashMap<String, String> allDetails = new HashMap<String, String>();
                            for (DataSnapshot details : word.getChildren()) {
                                allDetails.put(details.getKey(), (String) details.getValue());
                            }
                            Word toStudy = new Word(context, currentCharacter, allDetails);
                            //if(Integer.parseInt(toStudy.getRounds()) <= 0) {
                            packToStudy.add(toStudy.toString());
                            //}
                        }
                        Log.i(TAG, "***Added words from deck: " + deck.getKey());
                        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.container);
                        ComponentName me=new ComponentName(context, Widget.class);
                        appWidgetManager.updateAppWidget(me, buildUpdate(context, appWidgetIds));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

