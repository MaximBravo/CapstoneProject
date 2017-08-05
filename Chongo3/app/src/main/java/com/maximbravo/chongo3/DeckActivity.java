package com.maximbravo.chongo3;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.maximbravo.chongo3.Notification.Initializer;

public class DeckActivity extends AppCompatActivity implements DeckFragment.OnDeckClickedListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);


        android.support.v7.app.ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle("Your Decks");
        }
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.deck_list_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            DeckFragment deckFragment = new DeckFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.deck_list_fragment_container, deckFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.action_stop_notifications:
                Initializer initializer = new Initializer();
                initializer.stopAlarmManager(this);
                Log.i("DeckActivity", "***Stopped AlarmManager");
        }
        return true;
    }


    @Override
    public void onDeckClicked(Deck item) {
        Intent intent = new Intent(this, WordListActivity.class);
        intent.putExtra("deckName", item.getName());
        startActivity(intent);
    }
}
