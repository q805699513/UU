package com.uugty.uu.shop.guide.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.uugty.uu.mhvp.core.magic.viewpager.InnerScrollerContainer;
import com.uugty.uu.mhvp.core.magic.viewpager.OuterPagerAdapter;
import com.uugty.uu.mhvp.core.magic.viewpager.OuterScroller;

import java.util.ArrayList;

/**
 * Created by lzh on 16/9/1.
 */
public class GuideThemePagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter{


    private OuterScroller mOuterScroller;
    private ArrayList<Fragment> list;
    protected CharSequence[] mTitles ;

    @Override
    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        InnerScrollerContainer fragment =
                (InnerScrollerContainer) super.instantiateItem(container, position);

        if (null != mOuterScroller) {
            fragment.setOuterScroller(mOuterScroller, position);
        }
        return fragment;
    }


    public GuideThemePagerAdapter(FragmentManager fm, ArrayList<Fragment> list, ArrayList<String> title) {
        super(fm);
        this.list = list;
        this.mTitles = title.toArray(new CharSequence[title.size()]);
    }


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
