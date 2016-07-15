package com.uugty.uu.viewpage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {
	ArrayList<Fragment> list;  
    public TabFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {  
        super(fm);  
        this.list = list;  
          
    }  
      
      
   /* @Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		return super.instantiateItem(container, position);
	}

*/

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public int getCount() {  
        return list.size();
    }  
      
    @Override  
    public Fragment getItem(int arg0) {  
        return list.get(arg0);
    }
}
