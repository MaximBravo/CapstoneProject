package com.maximbravo.chongo3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Fragment[] fragments = new Fragment[2];
    private String fileString;
    private String deckName;

    public SectionsPagerAdapter(FragmentManager fm, String deckName, String fileString) {
        super(fm);
        this.deckName = deckName;
        this.fileString = fileString;
        fragments[0] = TabFragment.newInstance(0, deckName, fileString);
        fragments[1] = TabFragment.newInstance(1, deckName, fileString);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a TabFragment (defined as a static inner class below).
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Recent";
            case 1:
                return "Word List";
        }
        return null;
    }


}
