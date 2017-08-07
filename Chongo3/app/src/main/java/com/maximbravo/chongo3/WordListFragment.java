package com.maximbravo.chongo3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.LinkedHashMap;

public class WordListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "WordListFragment";
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase database;
    private DatabaseReference root;
    @NonNull
    private ArrayList<Word> words = new ArrayList<>();
    private RecyclerView recyclerView;
    private View rootView;
    private WordRecyclerViewAdapter recyclerViewAdapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private WordListFragment.OnWordClickedListener mListener;
    private String mFileString;
    private String currentDeck;
    private static boolean clearFile = false;
    public static boolean running = false;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //
            for (DataSnapshot wordSnapshot : dataSnapshot.getChildren()) {
                String character = (String) wordSnapshot.getKey();
                LinkedHashMap<String, String> allDetails = new LinkedHashMap<String, String>();
                for (DataSnapshot detailSnapShot : wordSnapshot.getChildren()) {
                    allDetails.put(detailSnapShot.getKey(), "" + detailSnapShot.getValue());
                }

                if (!hasWord(character)) {

                    words.add(new Word(character, allDetails));
                    Log.i("addWordToList", "***added " + character + " to local list");
                }
            }

            if (recyclerView == null) {
                recyclerView = (RecyclerView) rootView.findViewById(R.id.word_list);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recyclerViewAdapter = new WordRecyclerViewAdapter(words, mListener, false);
                recyclerView.setAdapter(recyclerViewAdapter);
            } else {
                recyclerViewAdapter.updateData(words);
                recyclerViewAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private DatabaseReference userRoot;
    private String deckName;
    private String fileString;

    public WordListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WordListFragment newInstance(int sectionNumber, String deckName, String fileString) {
        WordListFragment fragment = new WordListFragment();
        Bundle args = new Bundle();
        args.putString("deckName", deckName);
        args.putString("file", fileString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tab, container, false);


        mFileString = getArguments().getString("file");

        if (clearFile) {
            getArguments().putString("file", "");
            clearFile = false;
        }

        currentDeck = getArguments().getString("deckName");
        // Get current User
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize database and root
        database = FirebaseDatabase.getInstance();
        userRoot = database.getReference(mCurrentUser.getUid());

        root = userRoot.child(currentDeck);

        root.addListenerForSingleValueEvent(valueEventListener);

        if (mFileString != null && mFileString.length() != 0 && !running) {
            new WordListFragment.LoadWordsFromFile().execute(mFileString);
            running = true;
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.add_word_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord();
            }
        });

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
        root.addValueEventListener(valueEventListener);
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    class LoadWordsFromFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.i("LoadWordsFromFile", "***doInBackground");
            String fileString = params[0];
            addWordsFromFile(fileString);
            running = false;
            clearFile = true;
            return null;
        }
    }

    private void addWordsFromFile(String fileString) {
        StringBuilder fileStringBuilder = new StringBuilder(fileString);
        boolean inQuotes = false;
        for (int i = 0; i < fileString.length(); i++) {
            char current = fileString.charAt(i);
            if (inQuotes) {
                boolean replace = false;
                char replacer = current;
                switch (current) {
                    case ',':
                        replacer = '~';
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
                if (replace) {
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

        HashMap<String, HashMap<String, String>> words = new HashMap<>();
        int columns = 8;
        for (int i = columns + 1; i < parsedFileParts.length; i += columns) {
            String simplified = parsedFileParts[i];
            String traditional = parsedFileParts[i + 1];
            String pinyin = parsedFileParts[i + 2];
            String definition = parsedFileParts[i + 3];

            definition = definition.replace('~', ',');
            char firstChar = definition.charAt(0);
            if (firstChar == '"') {
                definition = definition.substring(1, definition.length() - 1);
            }

            HashMap<String, String> details = new HashMap<>();
            Word newWord = new Word(simplified, pinyin, definition, currentDeck);
            details.putAll(newWord.getAllDetails());
            words.put(simplified, details);


        }
        HashMap<String, Object> deckMap = new HashMap<>();
        deckMap.put(currentDeck, words);
        userRoot.updateChildren(deckMap);

        Log.i("addWordsFromFile", "***Finished method");
    }


    private boolean hasWord(String character) {
        if (words != null) {
            for (int i = 0; i < words.size(); i++) {
                String currentCharacter = words.get(i).getCharacter();
                if (character.equals(currentCharacter)) {
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
                if (characterString.length() == 0 ||
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
        Word newWord = new Word(character, pinyin, definition, currentDeck);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(character, null);
        root.updateChildren(map);
        DatabaseReference characterRoot = root.child(character);
        HashMap<String, Object> detailsMap = new HashMap<String, Object>();
        detailsMap.putAll(newWord.getAllDetails());
        characterRoot.updateChildren(detailsMap);
        Log.i("addWordToFirebase", "***Added: " + character);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WordListFragment.OnWordClickedListener) {
            mListener = (WordListFragment.OnWordClickedListener) context;
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

    public void setFileString(String fileString) {
        this.fileString = fileString;
    }
}
