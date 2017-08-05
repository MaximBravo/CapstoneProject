package com.maximbravo.chongo3;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Maxim Bravo on 8/2/2017.
 */

public class Word {
    private String bucket;
    private String character;
    private String pinyin;
    private String definition;
    private String inDeck;
    private String rounds;
    private HashMap<String, String> allDetails;


    public Word(String character, HashMap<String, String> allDetails) {
        this.character = character;
        this.pinyin = allDetails.get("pinyin");
        this.definition = allDetails.get("definition");
        this.inDeck = allDetails.get("deck");
        this.rounds = allDetails.get("rounds");
        this.bucket = allDetails.get("bucket");
        this.allDetails = allDetails;
    }

    public Word(String character, String pinyin, String definition, String deck) {
        this.character = character;
        this.pinyin = pinyin;
        this.definition = definition;
        this.allDetails = new LinkedHashMap<>();
        allDetails.put("pinyin", pinyin);
        allDetails.put("definition", definition);
        allDetails.put("deck", deck);

        Random rand = new Random();
        int randomNumber = rand.nextInt(5) + 1;
        allDetails.put("rounds", "" + randomNumber);
        allDetails.put("bucket", "1");
    }

    public String getInDeck() {
        return inDeck;
    }

    public String getBucket() {
        return bucket;
    }

    public String getRounds() {
        return rounds;
    }

    public Map<String, String> getHistory() {
        if(allDetails == null) {
            return null;
        }
        Map<String, String> history = new HashMap<>();
        for(String key : allDetails.keySet()) {
            if(key.equals("pinyin") ||
                    key.equals("definition") ||
                    key.equals("bucket") ||
                    key.equals("deck") ||
                    key.equals("rounds")) {
                continue;
            }
            history.put(key, allDetails.get(key));
        }
        return history;
    }
    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public HashMap<String, String> getAllDetails() {
        return allDetails;
    }

    public void setAllDetails(LinkedHashMap<String, String> allDetails) {
        this.allDetails = allDetails;
    }

    public void setBucket(int bucket) {
        this.bucket = "" + bucket;
        this.rounds = "" + getCorrespondingRounds(bucket);

    }

    private int getCorrespondingRounds(int bucket) {
        switch (bucket) {
            case 1:
                return 1;
            case 2:
                return 6;
            case 3:
                return 36;
            case 4:
                return 216;
            default:
                return 1296;
        }
    }

    private final static String divider = "~";
    public String toString() {
        String toReturn = "" +
                character + divider +
                pinyin + divider +
                definition + divider +
                inDeck + divider +
                rounds + divider +
                bucket + divider +
                getStringFromHistory(getHistory());
        return toReturn;
    }

    public static Word fromString(String wordString) {
        String[] parts = wordString.split(divider);
        String character = parts[0];
        String pinyin = parts[1];
        String definition = parts[2];
        String inDeck = parts[3];
        String rounds = parts[4];
        String bucket = parts[5];

        HashMap<String, String> history = new HashMap<>();
        for(int i = 6; i < parts.length; i+=2) {
            String key = parts[i];
            String value = parts[i+1];
            history.put(key, value);
        }

        Word newWord = new Word(character, pinyin, definition, inDeck, rounds, bucket, history);
        return newWord;
    }

    public Word(String character, String pinyin, String definition, String inDeck, String rounds, String bucket, HashMap<String, String> history) {
        this.character = character;
        this.pinyin = pinyin;
        this.definition = definition;
        this.inDeck = inDeck;
        this.rounds = rounds;
        this.bucket = bucket;
        HashMap<String, String> allDetails = new HashMap<>();
        allDetails.put("pinyin", pinyin);
        allDetails.put("definition", definition);
        allDetails.put("deck", inDeck);
        allDetails.put("bucket", bucket);
        allDetails.putAll(history);
        this.allDetails = allDetails;
    }
    private String getStringFromHistory(Map<String, String> history) {
        StringBuilder historyString = new StringBuilder();
        for(String key : history.keySet()) {
            historyString.append(key);
            historyString.append(divider);
            historyString.append(history.get(key));
        }
        return historyString.toString();
    }

    public void updateSelf(DatabaseReference root) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(character, null);
        root.updateChildren(map);
        DatabaseReference characterRoot = root.child(character);
        HashMap<String, Object> detailsMap = new HashMap<String, Object>();
        detailsMap.putAll(allDetails);
        characterRoot.updateChildren(detailsMap);
    }
}
