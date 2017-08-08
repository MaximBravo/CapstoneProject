package com.maximbravo.chongo3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class TabletActivity extends AppCompatActivity
        implements DeckFragment.OnDeckClickedListener,
        WordListFragment.OnWordClickedListener {

    private FragmentManager fragmentManager;
    private String currentDeckName;
    private DeckFragment deckFragment;
    private WordListFragment wordListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        fragmentManager = getSupportFragmentManager();
        deckFragment = (DeckFragment) fragmentManager.findFragmentById(R.id.deck_fragment);
        wordListFragment = (WordListFragment) fragmentManager.findFragmentById(R.id.word_list_fragment);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra("deckName") != null) {
                currentDeckName = intent.getStringExtra("deckName");
                inflateWordListFragment();
            }
        }
        if(savedInstanceState == null) {
            deckFragment = new DeckFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.deck_fragment, deckFragment).commit();
        } else {
            currentDeckName = savedInstanceState.getString("deckName");
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
    }

    @Override
    public void onWordClicked(Word item) {
        WordFragment wordFragment = new WordFragment();

        Bundle bundle = new Bundle();
        bundle.putString("deckName", currentDeckName);
        bundle.putString("key", item.getCharacter());
        wordFragment.setArguments(bundle);

        if(fragmentManager.findFragmentById(R.id.word_fragment) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.word_fragment, wordFragment).commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.word_fragment, wordFragment).commit();
        }
    }
}
