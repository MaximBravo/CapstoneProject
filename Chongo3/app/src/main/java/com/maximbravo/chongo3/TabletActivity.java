package com.maximbravo.chongo3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.maximbravo.chongo3.Notification.Initializer;

public class TabletActivity extends AppCompatActivity
        implements DeckFragment.OnDeckClickedListener,
        WordListFragment.OnWordClickedListener {

    private static final String TAG = "TabletActivity";
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
            if(intent.getStringExtra(getString(R.string.deck_name_key)) != null) {
                currentDeckName = intent.getStringExtra(getString(R.string.deck_name_key));
                inflateWordListFragment();
                if(intent.getStringExtra(getString(R.string.word_name_key)) != null) {
                    currentWordName = intent.getStringExtra(getString(R.string.word_name_key));
                    inflateWordFragment();
                }
            }
        }
        if(savedInstanceState == null) {
            deckFragment = new DeckFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.deck_fragment, deckFragment).commit();
        } else {
            currentDeckName = savedInstanceState.getString(getString(R.string.deck_name_key));
            currentWordName = savedInstanceState.getString(getString(R.string.word_name_key));
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent sendIntent;
            if(wordFragment != null) {
                sendIntent = new Intent(this, WordActivity.class);
                sendIntent.putExtra(getString(R.string.word_name_key), currentWordName);
            } else if (wordListFragment != null) {
                sendIntent = new Intent(this, WordListActivity.class);
            } else {
                sendIntent = new Intent(this, DeckActivity.class);
            }
            sendIntent.putExtra(getString(R.string.deck_name_key), currentDeckName);
            sendIntent.putExtra(getString(R.string.no_parent_key), true);
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

                    builder.setTitle(R.string.choose_deck_or_word_add_prompt);
                    builder.setPositiveButton(R.string.word_choice, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wordListFragment.addWord();
                        }
                    });

                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.deck_choice, new DialogInterface.OnClickListener() {
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
        args.putString(getString(R.string.deck_name_key), currentDeckName);
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
        bundle.putString(getString(R.string.deck_name_key), currentDeckName);
        bundle.putString(getString(R.string.word_name_key), currentWordName);
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
        outState.putString(getString(R.string.deck_name_key), currentDeckName);
        outState.putString(getString(R.string.word_name_key), currentWordName);
    }

    @Override
    public void onWordClicked(Word item, TextView sharedTextView) {
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
                Log.i(TAG, getString(R.string.stop_alarmmanager));
                break;
            case R.id.action_start_notifications:
                initializer.startAlarmManager(this);
                Log.i(TAG, getString(R.string.start_alarmmanager));
                break;
        }
        return true;
    }
}
