package com.maximbravo.chongo3;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maximbravo.chongo3.DeckFragment.OnDeckClickedListener;
import java.util.ArrayList;

public class DeckRecyclerViewAdapter extends RecyclerView.Adapter<DeckRecyclerViewAdapter.ViewHolder> {

    private final OnDeckClickedListener mListener;
    @NonNull private ArrayList<Deck> mDecks;

    public DeckRecyclerViewAdapter(@NonNull ArrayList<Deck> decks, OnDeckClickedListener listener, TextView emptyView) {
        mDecks = decks;
        mListener = listener;
        if(decks.size() > 0) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deck_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Deck currentDeck = mDecks.get(position);
        holder.deckData = currentDeck;
        holder.mNameView.setText(currentDeck.getName());
        holder.mCardCountView.setText("");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onDeckClicked(holder.deckData);
                }
            }
        });
    }

    public void updateData(ArrayList<Deck> newDecks) {
        mDecks = newDecks;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mDecks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mCardCountView;
        public Deck deckData;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mCardCountView = (TextView) view.findViewById(R.id.card_count);
        }

        @Override
        public String toString() {
            return mNameView.getText().toString() + ": " + mCardCountView.getText().toString();
        }
    }
}
