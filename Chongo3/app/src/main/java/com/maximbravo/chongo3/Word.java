package com.maximbravo.chongo3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Maxim Bravo on 8/2/2017.
 */

class Word {
    private String character;
    private String pinyin;
    private String definition;
    private LinkedHashMap<String, String> allDetails;


    public Word(String character, LinkedHashMap<String, String> allDetails) {
        this.character = character;
        this.pinyin = allDetails.get("pinyin");
        this.definition = allDetails.get("definition");
        this.allDetails = allDetails;
    }

    public Word(String character, String pinyin, String definition) {
        this.character = character;
        this.pinyin = pinyin;
        this.definition = definition;
        this.allDetails = new LinkedHashMap<>();
        allDetails.put("pinyin", pinyin);
        allDetails.put("definition", definition);
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

    public LinkedHashMap<String, String> getAllDetails() {
        return allDetails;
    }

    public void setAllDetails(LinkedHashMap<String, String> allDetails) {
        this.allDetails = allDetails;
    }
}
