package com.uugty.uu.viewpage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.uugty.uu.mhvp.core.magic.viewpager.InnerScrollerContainer;
import com.uugty.uu.mhvp.core.magic.viewpager.OuterPagerAdapter;
import com.uugty.uu.mhvp.core.magic.viewpager.OuterScroller;

import java.util.ArrayList;


public class PersonCenterPagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter{


    /****    OuterPagerAdapter methods   ****/
    private OuterScroller mOuterScroller;
    private ArrayList<Fragment> list;  

    @Override
    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO: Make sure to put codes below in your PagerAdapter's instantiateItem()
        // cuz Fragment has some weird life cycle.
        InnerScrollerContainer fragment =
                (InnerScrollerContainer) super.instantiateItem(container, position);

        if (null != mOuterScroller) {
            fragment.setOuterScroller(mOuterScroller, position);
        }
        return fragment;
    }
    /****  OuterPagerAdapter methods End   ****/


    /************************ Test data *********************/
    public PersonCenterPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    protected CharSequence[] mTitles = {"我的小店", "uu圈", "关于我"/*, "page 4", "page 5", "page 6"*/};

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public final Fragment getItem(int position) {
    	 return list.get(position);  
    }
}
