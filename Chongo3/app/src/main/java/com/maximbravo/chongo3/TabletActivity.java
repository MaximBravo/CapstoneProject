package com.maximbravo.chongo3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.maximbravo.chongo3.Notification.Initializer;

public class TabletActivity extends AppCompatActivity
        implements DeckFragment.OnDeckClickedListener,
        WordListFragment.OnWordClickedListener {

    private FragmentManager fragmentManager;
    private String currentDeckName;
    private String currentWordName;
    private DeckFragment deckFragment;
    private WordListFragment wordListFragment;
    private boolean isTablet = true;
    private WordFragment wordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        fragmentManager = getSupportFragmentManager();
        deckFragment = (DeckFragment) fragmentManager.findFragmentById(R.id.deck_fragment);
        wordListFragment = (WordListFragment) fragmentManager.findFragmentById(R.id.word_list_fragment);
        wordFragment = (WordFragment) fragmentManager.findFragmentById(R.id.word_fragment);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra("deckName") != null) {
                currentDeckName = intent.getStringExtra("deckName");
                inflateWordListFragment();
                if(intent.getStringExtra("key") != null) {
                    currentWordName = intent.getStringExtra("key");
                    inflateWordFragment();
                }
            }
        }
        if(savedInstanceState == null) {
            deckFragment = new DeckFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.deck_fragment, deckFragment).commit();
        } else {
            currentDeckName = savedInstanceState.getString("deckName");
            currentWordName = savedInstanceState.getString("wordName");
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent sendIntent;
            if(wordFragment != null) {
                sendIntent = new Intent(this, WordActivity.class);
                sendIntent.putExtra("key", currentWordName);
            } else if (wordListFragment != null) {
                sendIntent = new Intent(this, WordListActivity.class);
            } else {
                sendIntent = new Intent(this, DeckActivity.class);
            }
            sendIntent.putExtra("deckName", currentDeckName);
            sendIntent.putExtra("noParent", true);
            startActivity(sendIntent);
        }

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.add_action_button);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wordListFragment == null) {
                    deckFragment.addDeck();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TabletActivity.this);

                    builder.setTitle("Choose which to add:");
                    builder.setPositiveButton("Word", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wordListFragment.addWord();
                        }
                    });

                    builder.setCancelable(true);
                    builder.setNegativeButton("Deck", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deckFragment.addDeck();
                        }
                    });
                    builder.show();

                }
            }
        });
    }

    private void inflateWordListFragment() {
        wordListFragment = new WordListFragment();

        Bundle args = new Bundle();
        args.putString("deckName", currentDeckName);
        wordListFragment.setArguments(args);

        if(fragmentManager.findFragmentById(R.id.word_list_fragment) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.word_list_fragment, wordListFragment).commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.word_list_fragment, wordListFragment).commit();
        }
    }

    private void inflateWordFragment() {
        wordFragment = new WordFragment();

        Bundle bundle = new Bundle();
        bundle.putString("deckName", currentDeckName);
        bundle.putString("key", currentWordName);
        wordFragment.setArguments(bundle);

        if(fragmentManager.findFragmentById(R.id.word_fragment) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.word_fragment, wordFragment).commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.word_fragment, wordFragment).commit();
        }
    }
    @Override
    public void onDeckClicked(Deck item) {
        currentDeckName = item.getName();
        Fragment wordFragment = fragmentManager.findFragmentById(R.id.word_fragment);
        if(wordFragment != null) {
            fragmentManager.beginTransaction().remove(wordFragment).commit();
        }
       inflateWordListFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("deckName", currentDeckName);
        outState.putString("wordName", currentWordName);
    }

    @Override
    public void onWordClicked(Word item) {
        currentWordName = item.getCharacter();
        inflateWordFragment();
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
                Log.i("DeckActivity", "***Stopped AlarmManager");
                break;
            case R.id.action_start_notifications:
                initializer.startAlarmManager(this);
                Log.i("DeckActivity", "***Started AlarmManager");
                break;
        }
        return true;
    }
}
