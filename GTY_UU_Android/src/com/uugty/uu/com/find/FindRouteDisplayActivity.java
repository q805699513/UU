/*package com.uugty.uu.com.find;


import java.util.ArrayList;
import java.util.List;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.CustomViewPager;
import com.uugty.uu.common.myview.PagerScrollView;
import com.uugty.uu.viewpage.adapter.TabFragmentPagerAdapter;

public class FindRouteDisplayActivity extends BaseActivity{

	private PagerScrollView mScrollView;
	private TabLayout tabs;
	private ViewPager viewpager;
	private List<Fragment> fragments=new ArrayList<Fragment>();
	private List<String> titles = new ArrayList<String>();
	private String roadId;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_find_route_display;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			roadId = getIntent().getStringExtra("roadId");
		}
		mScrollView = (PagerScrollView) findViewById(R.id.activity_find_route_display_scrollview);
		tabs = (TabLayout) findViewById(R.id.tabs);
		viewpager = (CustomViewPager) findViewById(R.id.viewPager);
		titles.add("玩法");
		titles.add("说明");
		titles.add("评价");
		FindViewFragment_play play = new FindViewFragment_play();
		FindViewFragment_play play1 = new FindViewFragment_play();
		FindViewFragment_play play2 = new FindViewFragment_play();

		fragments.add(play);
	
		fragments.add(play1);
		fragments.add(play2);
		 TabFragmentPagerAdapter adapter=new TabFragmentPagerAdapter(
	                getSupportFragmentManager(),fragments,titles);
		 //tabs.setTabsFromPagerAdapter(adapter);
		 viewpager.setAdapter(adapter);
		 viewpager.setOffscreenPageLimit(3); 
		 tabs.setupWithViewPager(viewpager);
		 tabs.setTabMode(TabLayout.MODE_FIXED);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            	viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
		
		final TabLayout.TabLayoutOnPageChangeListener listener =
                new TabLayout.TabLayoutOnPageChangeListener(tabs);
		viewpager.addOnPageChangeListener(listener);
		
		viewpager.setOnTouchListener(new View.OnTouchListener() {  
			  

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);  
				return false;
			}  
	    });  
	  
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {  
	  
	        @Override  
	        public void onPageSelected(int arg0) {  
	        }  
	  
	        @Override  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {  
	        	viewpager.getParent().requestDisallowInterceptTouchEvent(true);  
	        }   
	        @Override   
	        public void onPageScrollStateChanged(int arg0) {  
	        }   
	    });  
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
	}

}
*/