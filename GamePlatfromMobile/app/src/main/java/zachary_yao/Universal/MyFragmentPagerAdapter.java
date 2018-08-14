package zachary_yao.Universal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by yaozh16 on 18-8-11.
 */


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> mFragements;
    private final ArrayList<String> mTitles;

    public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments,ArrayList<String> titles) {
        super(fm);
        mTitles=titles;
        mFragements=fragments;
    }

    @Override
    public Fragment getItem(int position){
        return mFragements.get(position);
    }

    @Override
    public int getCount() {
        return mFragements.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.size() > 0)
            return mTitles.get(position);
        return null;
    }
}
