package com.sakagames.myseriesdatabases.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sakagames.myseriesdatabases.database.StatusWatch;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by robin on 19/08/17.
 */

public class MyTabsPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_FRAGMENTS = 6;
    private List<Fragment> mFragments;

    public MyTabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mFragments = new ArrayList<>();
        init();
    }

    public void init() {
        mFragments.clear();
        mFragments.add(TabFragment.newInstance(null));
        for (StatusWatch status : StatusWatch.values()) {
            mFragments.add(TabFragment.newInstance(status));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
        /*Fragment fragment = null;
        switch (position) {
            case 0 :
                fragment = TabFragment.newInstance(null);
                break;
            case 1 :
                fragment = TabFragment.newInstance(StatusWatch.WATCHING);
                break;
            case 2 :
                fragment = TabFragment.newInstance(StatusWatch.WATCHED);
                break;
            case 3 :
                fragment = TabFragment.newInstance(StatusWatch.STALLED);
                break;
            case 4 :
                fragment = TabFragment.newInstance(StatusWatch.DROPPED);
                break;
            case 5 :
                fragment = TabFragment.newInstance(StatusWatch.WANT_TO_WATCH);
                break;
        }
        return fragment;*/
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
