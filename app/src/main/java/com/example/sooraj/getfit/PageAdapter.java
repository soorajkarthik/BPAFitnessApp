package com.example.sooraj.getfit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    /**
     * Fields
     */
    private int numOfTabs;

    /**
     * Constructor
     * @param fragmentManager FragmentManager used by the current activity
     * @param numOfTabs number of tabs in TabLayout
     */
    public PageAdapter(FragmentManager fragmentManager, int numOfTabs) {

        super(fragmentManager);
        this.numOfTabs = numOfTabs;
    }

    /**
     * @param position current position of ViewPager that PageAdapter is attached to
     * @return Fragment based on position
     */
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

    /**
     * @return number of tabs in TabLayout
     */
    @Override
    public int getCount() {

        return numOfTabs;
    }
}
