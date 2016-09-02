package com.uugty.uu.shop.guide.Interface;

import android.app.Activity;
import android.view.LayoutInflater;

import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderViewPager;

import java.util.List;

/**
 * Created by lzh on 16/9/1.
 */
public abstract class HeaderViewInterface<T> {

    protected Activity mContext;
    protected LayoutInflater mInflate;
    protected T mEntity;

    public HeaderViewInterface(Activity context) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
    }

    public boolean fillView(T t, MagicHeaderViewPager viewPager) {
        if (t == null) {
            return false;
        }
        if ((t instanceof List) && ((List) t).size() == 0) {
            return false;
        }
        this.mEntity = t;
        getView(t, viewPager);
        return true;
    }

    protected abstract void getView(T t, MagicHeaderViewPager viewPager);

}
