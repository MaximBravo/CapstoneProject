package com.maximbravo.chongo3;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class WordActivity extends AppCompatActivity {

    private String deckName;
    private WordFragment wordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        android.support.v7.app.ActionBar toolbar = this.getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle("");
        }
        String wordName = null;
        Intent intent = getIntent();
        if(intent != null) {
            deckName = intent.getStringExtra("deckName");
            wordName = intent.getStringExtra("key");
        }
        toolbar.setDisplayHomeAsUpEnabled(true);

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
        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, WordListActivity.class);
            intent.putExtra("deckName", deckName);
            startActivity(intent);
        }
        return true;
    }
}
