package com.maximbravo.chongo3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maxim Bravo on 8/2/2017.
 */

class WordRecyclerViewAdapter extends RecyclerView.Adapter<WordRecyclerViewAdapter.ViewHolder> {
    private final TabFragment.OnWordClickedListener mListener;
    private ArrayList<Word> mWords;
    private boolean mOnlyRecent;

    public WordRecyclerViewAdapter(ArrayList<Word> words, TabFragment.OnWordClickedListener listener, Boolean onlyRecent) {
        mWords = words;
        mListener = listener;
        mOnlyRecent = onlyRecent;
        if(onlyRecent) {
            mWords = getRecent(words);
        }
    }

    private ArrayList<Word> getRecent(ArrayList<Word> words) {
        //TODO: make this method only have the words that are recently studied
        //For now dont show any since not started notifications yet
        return new ArrayList<>();
    }

    @Override
    public WordRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_list_item, parent, false);
        return new WordRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WordRecyclerViewAdapter.ViewHolder holder, int position) {
        Word currentWord = mWords.get(position);
        holder.wordData = currentWord;
        holder.mCharacterView.setText(currentWord.getCharacter());
        holder.mPinyinView.setText(currentWord.getPinyin());
        holder.mDefinitionView.setText(currentWord.getDefinition());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onWordClicked(holder.wordData);
                }
            }
        });
    }

    public void updateData(ArrayList<Word> newWords) {
        if(mOnlyRecent) {
            mWords = getRecent(newWords);
        } else {
            mWords = newWords;
        }
    }
    @Override
    public int getItemCount() {
        return mWords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCharacterView;
        public final TextView mPinyinView;
        public final TextView mDefinitionView;
        public Word wordData;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCharacterView = (TextView) view.findViewById(R.id.character);
            mPinyinView = (TextView) view.findViewById(R.id.pinyin);
            mDefinitionView = (TextView) view.findViewById(R.id.definition);
        }

        @Override
        public String toString() {
            return mCharacterView.getText().toString() + ": " + mPinyinView.getText().toString();
        }
    }
}

