package com.uugty.uu.uuchat;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.uugty.uu.R;
import com.uugty.uu.common.photoview.HackyViewPager;


public class ChatShowImageActivity extends FragmentActivity {

	private HackyViewPager mPager;
	private int pagerPosition; // 当前图片位置
	private TextView indicator;
	private List<EMMessage> listImage = new ArrayList();
	private String emMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_show_image_one);
		if (null != getIntent()) {
			this.listImage = ((List) getIntent().getSerializableExtra(
					"listImage"));
			this.emMessage = getIntent().getStringExtra("image");
		}
		if ((this.listImage != null) && (this.listImage.size() > 0)){
			 for (int i = 0; i < this.listImage.size(); i++){
				 if (((ImageMessageBody)((EMMessage)this.listImage.get(i)).getBody()).getLocalUrl().equals(this.emMessage))
			          this.pagerPosition = i; 
			 }
		}
		     
		        
		
		mPager = (HackyViewPager) findViewById(R.id.chat_image_hacky_pager);
		indicator = (TextView) findViewById(R.id.chat_image_indicator);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), listImage);
		mPager.setAdapter(mAdapter);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});

		mPager.setCurrentItem(pagerPosition);
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public List<EMMessage> fileList;
		public ImagePagerAdapter(FragmentManager fm,
				List<EMMessage> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {

			return ChatImageShowFragment.newInstance(fileList.get(position));
		}

	}
}
