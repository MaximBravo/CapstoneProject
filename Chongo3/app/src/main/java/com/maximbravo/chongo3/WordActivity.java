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
            deckName = intent.getStringExtra("deckName");
            wordName = intent.getStringExtra("key");
        }
       // if(!getIntent().hasExtra("noParent")) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        //}

        if(isTablet) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Intent sendIntent = new Intent(this, TabletActivity.class);
                sendIntent.putExtra("deckName", deckName);
                sendIntent.putExtra("key", wordName);
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
            bundle.putString("deckName", deckName);
            bundle.putString("key", wordName);
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
                    if(inputIntent.hasExtra("noParent")) {
                        Intent intent = new Intent(this, WordListActivity.class);
                        intent.putExtra("deckName", deckName);
                        intent.putExtra("noParent", true);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                }
        }
        return true;
    }
}
