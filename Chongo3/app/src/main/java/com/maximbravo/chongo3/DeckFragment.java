package com.maximbravo.chongo3;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckFragment extends Fragment implements View.OnClickListener {

    private OnListFragmentInteractionListener mListener;
    private static final String TAG = "DeckFragment";
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private ArrayList<Deck> decks = new ArrayList<>();
    private RecyclerView recyclerView;
    private View rootView;
    private DeckRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_deck_list, container, false);

        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        root = database.getReference(mCurrentUser.getUid());

        // on click listener
        FloatingActionButton addDeckButton = (FloatingActionButton) rootView.findViewById(R.id.add_deck_button);
        addDeckButton.setOnClickListener(this);

        // Read from the database
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //
                for(DataSnapshot deckSnapShot: dataSnapshot.getChildren()) {
                    String deckName = "" + deckSnapShot.getKey();
                    addDeckToLocalList(deckName);
                }
                if(recyclerView == null) {
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewAdapter = new DeckRecyclerViewAdapter(decks, mListener);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    recyclerViewAdapter.updateData(decks);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




        return rootView;
    }

    private void addDeckToLocalList(String key) {
        if (decks == null || decks.size() == 0) {
            decks = new ArrayList<>();
        }
        if (hasDeck(key)) {
            return;
        }
        decks.add(new Deck(key));
    }

    private boolean hasDeck(String key) {
        if(decks != null) {
            for(int i = 0 ; i < decks.size(); i++) {
                String currentDeckKey = decks.get(i).getName();
                if(key.equals(currentDeckKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_deck_button:
                addDeck();
                break;
        }
    }

    private void addDeck() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Deck Name:");

        final EditText nameField = new EditText(getActivity());
        builder.setView(nameField);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newDeckName = nameField.getText().toString();
                HashMap<String, Object> map = new HashMap<>();
                map.put(newDeckName, "");
                root.updateChildren(map);
            }
        });

        builder.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Deck item);
    }
}
