package com.maximbravo.chongo3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Fragment[] fragments = new Fragment[1];
    private String fileString;
    private String deckName;

    public SectionsPagerAdapter(FragmentManager fm, String deckName, String fileString) {
        super(fm);
        this.deckName = deckName;
        this.fileString = fileString;
        fragments[0] = WordListFragment.newInstance(0, deckName, fileString);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a WordListFragment (defined as a static inner class below).
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Word List";
    }


}
