package com.maximbravo.chongo3;

import android.content.Intent;
import android.content.res.Configuration;
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
    private boolean isTablet = false;
    private static final String TAG = "DeckActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);
        isTablet = getResources().getBoolean(R.bool.isTablet);

        android.support.v7.app.ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(R.string.deck_activity_title);
        }

        if(isTablet) {
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Intent sendIntent = new Intent(this, TabletActivity.class);
                startActivity(sendIntent);
            }
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
        Initializer initializer = new Initializer();
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.action_stop_notifications:
                initializer.stopAlarmManager(this);
                Log.i(TAG, getString(R.string.stop_alarmmanager));
                break;
            case R.id.action_start_notifications:
                initializer.startAlarmManager(this);
                Log.i(TAG, getString(R.string.start_alarmmanager));
                break;
        }
        return true;
    }


    @Override
    public void onDeckClicked(Deck item) {
        Intent intent = new Intent(this, WordListActivity.class);
        intent.putExtra(getString(R.string.deck_name_key), item.getName());
        startActivity(intent);
    }
}
