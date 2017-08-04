package com.maximbravo.chongo3;

/**
 * Created by Maxim Bravo on 8/2/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TabFragment";
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private ArrayList<Word> words = new ArrayList<>();
    private RecyclerView recyclerView;
    private View rootView;
    private WordRecyclerViewAdapter recyclerViewAdapter;
    private int mTabNumber;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private OnWordClickedListener mListener;
    private String mFileString;
    private String currentDeck;

    public TabFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TabFragment newInstance(int sectionNumber, String deckName, String fileString) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("deckName", deckName);
        args.putString("file", fileString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab, container, false);

        mTabNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        mFileString = getArguments().getString("file");


        currentDeck = getArguments().getString("deckName");
        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRoot = database.getReference(mCurrentUser.getUid());

        root = userRoot.child(currentDeck);


        // Read from the database
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //
                for(DataSnapshot deckSnapShot: dataSnapshot.getChildren()) {
                    String character = (String) deckSnapShot.getKey();
                    LinkedHashMap<String, String> allDetails = new LinkedHashMap<String, String>();
                    for (DataSnapshot detailSnapShot : deckSnapShot.getChildren()) {
                        allDetails.put(detailSnapShot.getKey(), "" + detailSnapShot.getValue());
                    }

                    addWordToList(character, allDetails);
                }

                if (recyclerView == null) {
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.word_list);
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    if(mTabNumber == 0) {
                        recyclerViewAdapter = new WordRecyclerViewAdapter(words, mListener, true);
                    } else {
                        recyclerViewAdapter = new WordRecyclerViewAdapter(words, mListener, false);
                    }
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    recyclerViewAdapter.updateData(words);
                    recyclerViewAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        if(mFileString != null && mFileString.length() != 0) {
            addWordsFromFile();
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.add_word_button);
        if(mTabNumber == 1) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWord();
                }
            });
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }



        return rootView;
    }

    private void addWordsFromFile() {
        StringBuilder fileStringBuilder = new StringBuilder(mFileString);
        boolean inQuotes = false;
        for(int i = 0; i < mFileString.length(); i++) {
            char current = mFileString.charAt(i);
            if(inQuotes) {
                boolean replace = false;
                char replacer = current;
                switch(current) {
                    case ',':
                        replacer = '#';
                        replace = true;
                        break;
                    case '\n':
                        replacer = ' ';
                        replace = true;
                        break;
                    case '"':
                        inQuotes = false;
                        break;
                }
                if(replace) {
                    fileStringBuilder.setCharAt(i, replacer);
                }
            } else {
                switch (current) {
                    case '"':
                        inQuotes = true;
                        break;
                    case '\n':
                        fileStringBuilder.setCharAt(i, ',');
                        break;
                }
            }
        }

        String[] parsedFileParts = fileStringBuilder.toString().split(",");

        int columns = 8;
        for(int i = columns+1; i < parsedFileParts.length; i+= columns) {
            String simplified = parsedFileParts[i];
            String traditional = parsedFileParts[i+1];
            String pinyin = parsedFileParts[i+2];
            String definition = parsedFileParts[i+3];

            definition = definition.replace('#', ',');
            char firstChar = definition.charAt(0);
            if(firstChar == '"') {
                definition = definition.substring(1, definition.length()-1);
            }
            addWordToFirebase(simplified, pinyin, definition);
        }
    }


    private void addWordToList(String character, LinkedHashMap<String, String> allDetails) {
        if (words == null || words.size() == 0) {
            words = new ArrayList<Word>();
        }
        if (hasWord(character)) {
            return;
        }
        words.add(new Word(character, allDetails));
    }

    private boolean hasWord(String character) {
        if(words != null) {
            for(int i = 0 ; i < words.size(); i++) {
                String currentCharacter = words.get(i).getCharacter();
                if(character.equals(currentCharacter)) {
                    return true;
                }
            }
        }
        return false;
    }



    private void addWord() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Fill In details");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText characterField = new EditText(getActivity());
        characterField.setHint("子");
        linearLayout.addView(characterField);

        final EditText pinyinField = new EditText(getActivity());
        pinyinField.setHint("zǐ");
        linearLayout.addView(pinyinField);

        final EditText definitionField = new EditText(getActivity());
        definitionField.setHint("son; child; person");
        linearLayout.addView(definitionField);

        builder.setView(linearLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String characterString = characterField.getText().toString();
                String pinyinString = pinyinField.getText().toString();
                String definitionString = definitionField.getText().toString();
                if(characterString.length() == 0 ||
                        pinyinString.length() == 0 ||
                        definitionString.length() == 0) {
                    Toast.makeText(getActivity(), "You need to fill in all fields.", Toast.LENGTH_LONG).show();
                } else {
                    addWordToFirebase(characterString, pinyinString, definitionString);
                }
            }
        });
        builder.setNegativeButton("Import From .csv", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), FileExtractor.class);
                intent.putExtra("deckName", currentDeck);
                startActivity(intent);
            }
        });

        builder.show();
    }

    public void addWordToFirebase(String character, String pinyin, String definition) {
        Word newWord = new Word(character, pinyin, definition);
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(character, null);
        root.updateChildren(map);
        DatabaseReference characterRoot = root.child(character);
        LinkedHashMap<String, Object> detailsMap = new LinkedHashMap<String, Object>();
        detailsMap.putAll(newWord.getAllDetails());
        characterRoot.updateChildren(detailsMap);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWordClickedListener) {
            mListener = (OnWordClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGridFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_word_button:
                addWord();
                break;
        }
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
    public interface OnWordClickedListener {
        // TODO: Update argument type and name
        void onWordClicked(Word item);
    }
}

