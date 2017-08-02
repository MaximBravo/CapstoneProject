package com.maximbravo.chongo3;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        String deckName = null;
        String cardCount = null;
        Intent intent = getIntent();
        if(intent != null) {
            deckName = intent.getStringExtra("deckName");
            cardCount = intent.getStringExtra("deckCardCount");
        }

        ActionBar toolbar = getSupportActionBar();
        if(deckName != null) {
            toolbar.setTitle(deckName);
        }
        toolbar.setDisplayHomeAsUpEnabled(true);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.word_list_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            WordListFragment wordListFragment = new WordListFragment();
            if(deckName != null) {
                wordListFragment.setDeckName(deckName);
            }
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.word_list_fragment_container, wordListFragment).commit();
        }
    }
}
