package com.maximbravo.chongo3;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuizActivity extends Activity implements View.OnClickListener {
    private TextView characterTextView;
    private TextView pinyinTextView;
    private TextView definitionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;

        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (width*0.8));

        LinearLayout mainDetails = (LinearLayout) findViewById(R.id.main_details);

        characterTextView = (TextView) findViewById(R.id.word);
        characterTextView.setText("我的");
        pinyinTextView = (TextView) findViewById(R.id.pinyin);
        pinyinTextView.setText("wǒ de");
        definitionTextView = (TextView) findViewById(R.id.definition);
        definitionTextView.setText("Mine, my");

        mainDetails.setOnClickListener(this);

        LayoutTransition transition = mainDetails.getLayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_details:
                pinyinTextView.setVisibility(View.VISIBLE);
                definitionTextView.setVisibility(View.VISIBLE);
                break;
            case R.id.bad:
                break;
            case R.id.ok:
                break;
            case R.id.good:
                break;
        }
    }
}
