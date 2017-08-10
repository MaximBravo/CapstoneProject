package com.maximbravo.chongo3;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class WordActivity extends AppCompatActivity {

    private String deckName;
    private WordFragment wordFragment;
    private boolean isTablet = false;
    private String wordName;
    private String transitionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        android.support.v7.app.ActionBar toolbar = this.getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle("");
        }

        Intent intent = getIntent();
        if(intent != null) {
            deckName = intent.getStringExtra(getString(R.string.deck_name_key));
            wordName = intent.getStringExtra(getString(R.string.word_name_key));
            if(intent.hasExtra(WordListActivity.TEXT_TRANSITION_NAME)) {
                transitionName = intent.getStringExtra(WordListActivity.TEXT_TRANSITION_NAME);
            }
        }
       // if(!getIntent().hasExtra("noParent")) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        //}

        if(isTablet) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Intent sendIntent = new Intent(this, TabletActivity.class);
                sendIntent.putExtra(getString(R.string.deck_name_key), deckName);
                sendIntent.putExtra(getString(R.string.word_name_key), wordName);
                startActivity(sendIntent);
            }
        }
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.word_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }


            // Create a new Fragment to be placed in the activity layout
            wordFragment = new WordFragment();

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.deck_name_key), deckName);
            bundle.putString(getString(R.string.word_name_key), wordName);
            if(transitionName != null) {
                bundle.putString(WordListActivity.TEXT_TRANSITION_NAME, transitionName);
            }
            wordFragment.setArguments(bundle);
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.word_fragment_container, wordFragment).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent inputIntent = getIntent();
        switch (item.getItemId()) {
            case android.R.id.home:
                if(inputIntent != null) {
                    if(inputIntent.hasExtra(getString(R.string.no_parent_key))) {
                        Intent intent = new Intent(this, WordListActivity.class);
                        intent.putExtra(getString(R.string.deck_name_key), deckName);
                        intent.putExtra(getString(R.string.no_parent_key), true);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                }
        }
        return true;
    }
}
