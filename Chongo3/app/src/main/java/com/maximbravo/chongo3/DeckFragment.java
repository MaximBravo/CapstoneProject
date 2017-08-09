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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DeckFragment extends Fragment {

    private OnDeckClickedListener mListener;
    private static final String TAG = "DeckFragment";
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private ArrayList<Deck> decks = new ArrayList<>();
    private RecyclerView recyclerView;
    private View rootView;
    private DeckRecyclerViewAdapter recyclerViewAdapter;
    private ValueEventListener valueEventListener;
    private boolean isTablet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_deck_list, container, false);
        isTablet = getResources().getBoolean(R.bool.isTablet);

        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        root = database.getReference(mCurrentUser.getUid());

        // on click listener
        FloatingActionButton addDeckButton = (FloatingActionButton) rootView.findViewById(R.id.add_deck_button);
        if(!isTablet) {
            addDeckButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDeck();
                }
            });
        } else {
            addDeckButton.setVisibility(View.GONE);
        }
        // Read from the database
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //
                for (DataSnapshot deckSnapShot : dataSnapshot.getChildren()) {
                    String deckName = "" + deckSnapShot.getKey();
                    addDeckToLocalList(deckName);

                }
                if (recyclerView == null) {
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewAdapter = new DeckRecyclerViewAdapter(decks, mListener, (TextView) rootView.findViewById(R.id.empty_view));
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    recyclerViewAdapter.updateData(decks, (TextView) rootView.findViewById(R.id.empty_view));

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };




        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        root.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //root.addListenerForSingleValueEvent(valueEventListener);
        root.addValueEventListener(valueEventListener);
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

    public void addDeck() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Deck Name:");

        final EditText nameField = new EditText(getActivity());
        builder.setView(nameField);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addDeckToFirebase(nameField.getText().toString());
            }
        });

        builder.show();
    }

    private void addDeckToFirebase(String newDeckName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(newDeckName, "");
        root.updateChildren(map);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeckClickedListener) {
            mListener = (OnDeckClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeckClickedListener");
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
    public interface OnDeckClickedListener {
        // TODO: Update argument type and name
        void onDeckClicked(Deck item);
    }
}
