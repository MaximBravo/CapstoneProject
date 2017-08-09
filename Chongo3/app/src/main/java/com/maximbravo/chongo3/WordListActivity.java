package com.maximbravo.chongo3;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class WordListActivity extends AppCompatActivity implements WordListFragment.OnWordClickedListener {

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
                sendIntent.putExtra("deckName", deckName);
                startActivity(sendIntent);
            }
        }

        String fileString = null;
        Intent intent = getIntent();
        if(intent != null) {
            deckName = intent.getStringExtra("deckName");
            if(intent.hasExtra("file")) {
                fileString = intent.getStringExtra("file");
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
                args.putString("deckName", deckName);
                if(fileString != null) {
                    args.putString("file", fileString);
                }

            }
            wordListFragment.setArguments(args);
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.word_list_fragment_container, wordListFragment).commit();
        }
    }

    @Override
    public void onWordClicked(Word item) {

        Toast.makeText(this, "Hello from the character: " + item.getCharacter(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, WordActivity.class);
        intent.putExtra("deckName", deckName);
        intent.putExtra("key", item.getCharacter());
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent inputIntent = getIntent();
        switch (item.getItemId()) {
            case android.R.id.home:
                if(inputIntent != null) {
                    if(inputIntent.hasExtra("noParent")) {
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
