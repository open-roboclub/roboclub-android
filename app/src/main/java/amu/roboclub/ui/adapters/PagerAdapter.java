package amu.roboclub.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mTabs;
    private Class[] fragments;

    public PagerAdapter(FragmentManager fm, Class... tabs) {
        super(fm);
        mTabs = tabs.length;
        fragments = tabs;
    }

    @Override
    public Fragment getItem(int position) {

        if (position >= mTabs || position < 0)
            return null;

        try {
            return (Fragment) fragments[position].newInstance();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public int getCount() {
        return mTabs;
    }
}
