package com.maximbravo.chongo3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private String deckName;

    public SectionsPagerAdapter(FragmentManager fm, String deckName) {
        super(fm);
        this.deckName = deckName;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a TabFragment (defined as a static inner class below).
        return TabFragment.newInstance(position, deckName);
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
