package com.uugty.uu.viewpage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
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
    public int getCount() {  
        return list.size();  
    }  
      
    @Override  
    public Fragment getItem(int arg0) {  
        return list.get(arg0);  
    }
}