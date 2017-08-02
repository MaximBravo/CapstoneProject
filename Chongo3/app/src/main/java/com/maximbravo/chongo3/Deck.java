package com.maximbravo.chongo3;

/**
 * Created by Maxim Bravo on 8/1/2017.
 */

public class Deck {
    private String mName;
    private String mCardCount;
    public Deck (String name, String cardCount) {
        mName = name;
        mCardCount = cardCount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCardCount() {
        return mCardCount;
    }

    public void setCardCount(String mCardCount) {
        this.mCardCount = mCardCount;
    }
}
