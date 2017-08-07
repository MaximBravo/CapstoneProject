package com.maximbravo.chongo3;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class WordListActivity extends AppCompatActivity implements WordListFragment.OnWordClickedListener {

    private String deckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);


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
}
