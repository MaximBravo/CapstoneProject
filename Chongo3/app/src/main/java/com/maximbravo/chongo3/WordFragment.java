package com.maximbravo.chongo3;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

/**
 * Created by Maxim Bravo on 8/4/2017.
 */

public class WordFragment extends Fragment {

    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference userRoot;
    private String mWordName;
    private String mDeckName;
    private DatabaseReference deckRoot;
    private DatabaseReference root;
    private Word currentWord;
    private ValueEventListener valueEventListener;
    private String transitionName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_word, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            mDeckName = bundle.getString(getString(R.string.deck_name_key));
            mWordName = bundle.getString(getString(R.string.word_name_key));
            if(bundle.getString(WordListActivity.TEXT_TRANSITION_NAME) != null) {
                transitionName = bundle.getString(WordListActivity.TEXT_TRANSITION_NAME);
                getActivity().supportPostponeEnterTransition();
            }
        }
        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        userRoot = database.getReference(mCurrentUser.getUid());
        deckRoot = userRoot.child(mDeckName);
        root = deckRoot.child(mWordName);

        valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedHashMap<String, String> allDetails = new LinkedHashMap<String, String>();
                for (DataSnapshot detail : dataSnapshot.getChildren()) {
                    allDetails.put(detail.getKey(), "" + detail.getValue());
                }
                currentWord = new Word(getContext(), mWordName, allDetails);

                TextView wordTextView = (TextView) rootView.findViewById(R.id.word);

                TextView pinyinTextView = (TextView) rootView.findViewById(R.id.pinyin);
                TextView definitionTextView = (TextView) rootView.findViewById(R.id.definition);
                pinyinTextView.setText(currentWord.getPinyin());
                pinyinTextView.setContentDescription(currentWord.getPinyin());
                definitionTextView.setText(currentWord.getDefinition());
                definitionTextView.setContentDescription(currentWord.getDefinition());
                if(transitionName != null) {
                    wordTextView.setTransitionName(transitionName);
                    wordTextView.setText(currentWord.getCharacter());
                    wordTextView.setContentDescription(currentWord.getCharacter());
                    getActivity().supportStartPostponedEnterTransition();
                } else {
                    wordTextView.setText(currentWord.getCharacter());
                    wordTextView.setContentDescription(currentWord.getCharacter());
                }

//                ListView history = (ListView) rootView.findViewById(R.id.history);
//                history.setAdapter(new HistoryAdapter(getActivity(), currentWord.getHistory()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        root.addValueEventListener(valueEventListener);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        root.removeEventListener(valueEventListener);
    }

}
