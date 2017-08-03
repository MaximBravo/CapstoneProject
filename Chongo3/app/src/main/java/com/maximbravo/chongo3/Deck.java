package com.maximbravo.chongo3;

/**
 * Created by Maxim Bravo on 8/1/2017.
 */

public class Deck {
    private String mName;

    public Deck(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
