package com.maximbravo.chongo3;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TabletActivity extends AppCompatActivity
        implements DeckFragment.OnDeckClickedListener,
        WordListFragment.OnWordClickedListener {

    private FragmentManager fragmentManager;
    private String currentDeckName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra("deckName") != null) {
                currentDeckName = intent.getStringExtra("deckName");
                inflateWordListFragment();
            }
        }
        if(savedInstanceState == null) {
            DeckFragment deckFragment = new DeckFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.deck_fragment, deckFragment).commit();
        } else {
            currentDeckName = savedInstanceState.getString("deckName");
        }
    }

    private void inflateWordListFragment() {
        WordListFragment wordListFragment = new WordListFragment();

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
