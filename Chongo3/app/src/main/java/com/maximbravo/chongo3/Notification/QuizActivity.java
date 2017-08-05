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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maximbravo.chongo3.R;
import com.maximbravo.chongo3.Word;

import java.util.ArrayList;

public class QuizActivity extends Activity implements View.OnClickListener {
    private TextView characterTextView;
    private TextView pinyinTextView;
    private TextView definitionTextView;
    private ArrayList<String> packToStudyStrings;

    private int wordInPack = 0;
    private ArrayList<Word> packToStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        Intent intent = getIntent();
        if(intent != null) {
            packToStudyStrings = intent.getStringArrayListExtra("pack");
        }

        packToStudy = new ArrayList<Word>();
        for(int i = 0; i < packToStudyStrings.size(); i++) {
            packToStudy.add(Word.fromString(packToStudyStrings.get(i)));
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;

        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (width*0.8));

        LinearLayout mainDetails = (LinearLayout) findViewById(R.id.main_details);

        mainDetails.setOnClickListener(this);

        LayoutTransition transition = mainDetails.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        startStudySession();
    }

    private void startStudySession() {
        studyWord(wordInPack);
    }

    private void studyWord(int i) {
        Word current = packToStudy.get(i);
        characterTextView = (TextView) findViewById(R.id.word);
        characterTextView.setText(current.getCharacter());
        pinyinTextView = (TextView) findViewById(R.id.pinyin);
        pinyinTextView.setText(current.getPinyin());
        definitionTextView = (TextView) findViewById(R.id.definition);
        definitionTextView.setText(current.getDefinition());
    }


    @Override
    public void onClick(View v) {
        Word currentWord = packToStudy.get(wordInPack);
        int currentBucket = Integer.parseInt(currentWord.getBucket());
        boolean moveToNext = true;
        switch (v.getId()) {
            case R.id.main_details:
                pinyinTextView.setVisibility(View.VISIBLE);
                definitionTextView.setVisibility(View.VISIBLE);
                moveToNext = false;
                break;
            case R.id.bad:
                if(currentBucket > 1) {
                    currentBucket--;
                }
                break;
            case R.id.ok:
                break;
            case R.id.good:
                currentBucket++;
                break;
        }

        if(moveToNext) {
            currentWord.setBucket(currentBucket);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            // Get current User
            FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Initialize database and root
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference root = database.getReference(mCurrentUser.getUid());

            DatabaseReference deckRoot = root.child(currentWord.getInDeck());
            currentWord.updateSelf(deckRoot);
        }
    }
}
