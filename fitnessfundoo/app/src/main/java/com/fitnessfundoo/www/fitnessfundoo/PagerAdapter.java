package com.fitnessfundoo.www.fitnessfundoo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fitnessfundoo.R;


/**
 * Created by Anubhav on 13-01-2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        //this.mNumOfTabs = NumOfTabs;
    }



    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                //GymFragment tab1 = new SportsEventsFragment();
                return new GymFragment();
            case 1:
                //GymsFragment tab2 = new GymsFragment();
                return new SwimmimgPoolFragment();
            case 2:
                //SportsClubFragment tab3 = new SportsClubFragment();
                return new SportsClubFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.gym);
            case 1:
                return mContext.getString(R.string.pool);
            case 2:
                return mContext.getString(R.string.club);
            default:
                return null;
        }
    }

}
