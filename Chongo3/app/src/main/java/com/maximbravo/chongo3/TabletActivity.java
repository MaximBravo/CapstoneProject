package com.maximbravo.chongo3;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TabletActivity extends AppCompatActivity
        implements DeckFragment.OnDeckClickedListener,
        WordListFragment.OnWordClickedListener {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);

        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.getStringExtra("deckName") != null) {
                inflateWordListFragment(intent.getStringExtra("deckName"));
            }
        }
        if(savedInstanceState == null) {
            DeckFragment deckFragment = new DeckFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.deck_fragment, deckFragment).commit();
        }
    }

    private void inflateWordListFragment(String deckName) {
        WordListFragment wordListFragment = new WordListFragment();

        Bundle args = new Bundle();
        args.putString("deckName", deckName);
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
       inflateWordListFragment(item.getName());
    }

    @Override
    public void onWordClicked(Word item) {

    }
}
