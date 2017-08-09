package com.maximbravo.chongo3.Notification;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maximbravo.chongo3.R;
import com.maximbravo.chongo3.Word;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.word) TextView characterTextView;
    @BindView(R.id.pinyin) TextView pinyinTextView;
    @BindView(R.id.definition) TextView definitionTextView;
    private ArrayList<String> packToStudyStrings;

    private int wordInPack = 0;
    private ArrayList<Word> packToStudy;
    private DatabaseReference root;
    private FirebaseDatabase database;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("pack")) {
            packToStudyStrings = intent.getStringArrayListExtra("pack");
            packToStudy = new ArrayList<Word>();
            for(int i = 0; i < packToStudyStrings.size(); i++) {
                packToStudy.add(Word.fromString(packToStudyStrings.get(i)));
            }
            startStudySession();
        } else {
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Initialize database and root
            database = FirebaseDatabase.getInstance();
            root = database.getReference(mCurrentUser.getUid());

            packToStudy = new ArrayList<Word>();
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot deck : dataSnapshot.getChildren()) {
                        for(DataSnapshot word : deck.getChildren()) {
                            String currentCharacter = word.getKey();
                            HashMap<String, String> allDetails = new HashMap<String, String>();
                            for(DataSnapshot details : word.getChildren()) {
                                allDetails.put(details.getKey(), (String) details.getValue());
                            }
                            Word toStudy = new Word(currentCharacter, allDetails);
                            packToStudy.add(toStudy);
                            startStudySession();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int) (dm.widthPixels*0.8);

        int height = (int) (dm.heightPixels*0.8);

        int smallSide = width < height ? width : height ;
        getWindow().setLayout(smallSide, smallSide);

        LinearLayout mainDetails = (LinearLayout) findViewById(R.id.main_details);

        mainDetails.setOnClickListener(this);
        findViewById(R.id.good).setOnClickListener(this);
        findViewById(R.id.bad).setOnClickListener(this);
        findViewById(R.id.ok).setOnClickListener(this);

        LayoutTransition transition = mainDetails.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);



        if(savedInstanceState != null) {
            wordInPack = savedInstanceState.getInt("wordInPack");
        }

    }

    private void startStudySession() {
        studyWord(wordInPack);
    }

    private void studyWord(int i) {
        Word current = packToStudy.get(i);
        pinyinTextView.setVisibility(View.GONE);
        definitionTextView.setVisibility(View.GONE);
        characterTextView.setText(current.getCharacter());
    }

    private void showText(Word current) {
        pinyinTextView.setText(current.getPinyin());
        definitionTextView.setText(current.getDefinition());
        pinyinTextView.setVisibility(View.VISIBLE);
        definitionTextView.setVisibility(View.VISIBLE);
    }

    private boolean done = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("wordInPack", wordInPack);
    }

    @Override
    public void onClick(View v) {
        boolean moveToNext = true;
        if(done) {
            packToStudy = null;
            finish();
        } else {
            Word currentWord = packToStudy.get(wordInPack);
            int currentBucket = Integer.parseInt(currentWord.getBucket());

            switch (v.getId()) {
                case R.id.main_details:
                    showText(currentWord);
                    moveToNext = false;
                    break;
                case R.id.bad:
                    pinyinTextView.setVisibility(View.GONE);
                    definitionTextView.setVisibility(View.GONE);
                    if (currentBucket > 1) {
                        currentBucket--;
                    }
                    break;
                case R.id.ok:
                    pinyinTextView.setVisibility(View.GONE);
                    definitionTextView.setVisibility(View.GONE);
                    break;
                case R.id.good:
                    pinyinTextView.setVisibility(View.GONE);
                    definitionTextView.setVisibility(View.GONE);
                    currentBucket++;
                    break;
            }

            if(moveToNext && !done) {
                currentWord.setBucket(currentBucket);
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                // Get current User
                FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Initialize database and root
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference root = database.getReference(mCurrentUser.getUid());

                DatabaseReference deckRoot = root.child(currentWord.getInDeck());
                currentWord.updateSelf(deckRoot);

                wordInPack++;
                if(wordInPack >= packToStudy.size()) {
                    finishTest();
                } else {
                    studyWord(wordInPack);
                }
            }
        }


    }

    private void finishTest() {
        characterTextView = (TextView) findViewById(R.id.word);
        characterTextView.setText("Your Done!");
        pinyinTextView = (TextView) findViewById(R.id.pinyin);
        pinyinTextView.setVisibility(View.GONE);
        pinyinTextView.setText("");
        definitionTextView = (TextView) findViewById(R.id.definition);
        definitionTextView.setVisibility(View.GONE);
        definitionTextView.setText("");
        done = true;
    }
}
