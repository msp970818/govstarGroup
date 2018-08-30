package com.kaituocn.govstar.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kaituocn.govstar.schedule.ScheduleFragment;
import com.kaituocn.govstar.set.SetFragment;
import com.kaituocn.govstar.work.WorkFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
               return new ScheduleFragment();
            case 1:
                return new WorkFragment();
            case 2:
                return new SetFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
