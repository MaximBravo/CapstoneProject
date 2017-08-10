package com.maximbravo.chongo3;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class WordListActivity extends AppCompatActivity implements WordListFragment.OnWordClickedListener {

    public static final String TEXT_TRANSITION_NAME = "transitionName";
    private String deckName;
    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        isTablet = getResources().getBoolean(R.bool.isTablet);
        if(isTablet) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Intent sendIntent = new Intent(this, TabletActivity.class);
                sendIntent.putExtra(getString(R.string.deck_name_key), deckName);
                startActivity(sendIntent);
            }
        }

        String fileString = null;
        Intent intent = getIntent();
        if(intent != null) {
            deckName = intent.getStringExtra(getString(R.string.deck_name_key));
            if(intent.hasExtra(getString(R.string.file_key))) {
                fileString = intent.getStringExtra(getString(R.string.file_key));
            }
        }

        ActionBar toolbar = getSupportActionBar();
        if(deckName != null) {
            toolbar.setTitle(deckName);
        }
//        if(!getIntent().hasExtra("noParent")) {
            toolbar.setDisplayHomeAsUpEnabled(true);
//        }
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
            Bundle args = new Bundle();
            if(deckName != null) {
                args.putString(getString(R.string.deck_name_key), deckName);
                if(fileString != null) {
                    args.putString(getString(R.string.file_key), fileString);
                }

            }
            wordListFragment.setArguments(args);
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.word_list_fragment_container, wordListFragment).commit();
        }
    }

    @Override
    public void onWordClicked(Word item, TextView sharedTextView) {


        Intent intent = new Intent(this, WordActivity.class);
        intent.putExtra(getString(R.string.deck_name_key), deckName);
        intent.putExtra(getString(R.string.word_name_key), item.getCharacter());

        intent.putExtra(TEXT_TRANSITION_NAME, ViewCompat.getTransitionName(sharedTextView));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedTextView,
                ViewCompat.getTransitionName(sharedTextView));

        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent inputIntent = getIntent();
        switch (item.getItemId()) {
            case android.R.id.home:
                if(inputIntent != null) {
                    if(inputIntent.hasExtra(getString(R.string.no_parent_key))) {
                        Intent intent = new Intent(this, DeckActivity.class);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                }
        }
        return true;
    }
}
