package com.example.sooraj.getfit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private final String username;
    private int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs, String username) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProgressFragment();
            case 1:
                return new StepsFragment();
            case 2:
                return new FoodFragment();
            case 3:
                return new SocialFragment();
            case 4:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
