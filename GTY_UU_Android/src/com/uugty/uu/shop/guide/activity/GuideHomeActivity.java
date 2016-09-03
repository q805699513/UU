package com.uugty.uu.shop.guide.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.SearchPopuWindow;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.HomePageRecommendEntity;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderUtils;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderViewPager;
import com.uugty.uu.mhvp.core.magic.viewpager.PagerSlidingTabStrip;
import com.uugty.uu.shop.guide.adapter.GuideThemePagerAdapter;
import com.uugty.uu.shop.guide.fragment.CommondFragment;
import com.uugty.uu.shop.guide.view.GuideHeaderAdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lzh on 16/9/1.
 */
public class GuideHomeActivity extends BaseActivity implements
		OnClickListener {

	@Bind(R.id.guide_parent) LinearLayout mGuideParent;//根布局
	@Bind(R.id.location_text) TextView location_text;//地区
	@Bind(R.id.home_page_search_text) TextView search_text;
	@Bind(R.id.home_page_release_lin) LinearLayout release_lin;
	@Bind(R.id.location_linear) LinearLayout location_linear;

	private MagicHeaderViewPager mMagicHeaderViewPager;//主题Tab
	private GuideThemePagerAdapter mTabPagerAdapter;
	private GuideHeaderAdView mHeadView;//广告头
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private ArrayList<String> mTitleList = new ArrayList<String>();
	//脑残逻辑(接口前三个为广告栏,之后的为主题)而定义的数组
	private ArrayList<HomePageRecommendEntity.HomePageRecommend> mThemeIdList = new ArrayList<HomePageRecommendEntity.HomePageRecommend>();
	private List<HomePageRecommendEntity.HomePageRecommend> recommendList = new ArrayList<HomePageRecommendEntity.HomePageRecommend>();//请求返回的list
	private String themeCity = "北京市";//默认城市

	@Override
	protected int getContentLayout() {
		return R.layout.activity_guidehome_layout;
	}

	@Override
	protected void initGui() {
		ButterKnife.bind(this);

		mMagicHeaderViewPager = new MagicHeaderViewPager(this) {
			@Override
			protected void initTabsArea(LinearLayout container) {
				ViewGroup tabsArea = (ViewGroup) LayoutInflater.from(
						GuideHomeActivity.this).inflate(
						R.layout.guide_layout_tabs, null);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						MagicHeaderUtils.dp2px(GuideHomeActivity.this, 41));
				container.addView(tabsArea, lp);

				//Tab
				PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) tabsArea
						.findViewById(R.id.guide_tab);
				pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);

				//告诉magicHeaderViewPager who is stable area and tabs.
				setTabsArea(tabsArea);
				setPagerSlidingTabStrip(pagerSlidingTabStrip);
			}
		};
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mGuideParent.addView(mMagicHeaderViewPager, lp);
		//实例化头部广告栏
		mHeadView = new GuideHeaderAdView(this);

	}

	@Override
	protected void initAction() {
		location_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideHomeActivity.this, CityLocationActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
		search_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("newCity",themeCity);
				intent.setClass(ctx, SearchPopuWindow.class);
				startActivity(intent);
			}
		});

		release_lin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mMagicHeaderViewPager
				.setTabOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
											   float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageSelected(int position) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	@Override
	protected void initData() {
		getThemeRecommend();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHeadView != null) {
			mHeadView.stopADRotate();
		}
	}

	public void initViewPager() {
		if (fragmentList.size() == 0) {
			//循环创建fragment
			for (int i = 0; i < mTitleList.size(); i++) {
				CommondFragment fragment = new CommondFragment();//根据不同标签加载数据
				Bundle bundle = new Bundle();
				bundle.putString("theme", mTitleList.get(i));
				bundle.putString("city",themeCity);
				bundle.putString("themeId",mThemeIdList.get(i).getRoadlineThemeId());
				fragment.setArguments(bundle);
				fragmentList.add(fragment);
			}
		}
		mTabPagerAdapter = new GuideThemePagerAdapter(getSupportFragmentManager(), fragmentList,mTitleList);
		mMagicHeaderViewPager.setPagerAdapter(mTabPagerAdapter);
	}

	private void getThemeRecommend() {

		RequestParams params = new RequestParams();
		params.add("roadlineThemeArea", themeCity); // 地点
		APPRestClient.postGuide(this, ServiceCode.HOME_PAGE_RECOMMEND, params,
				new APPResponseHandler<HomePageRecommendEntity>(
						HomePageRecommendEntity.class, this) {
					@Override
					public void onSuccess(HomePageRecommendEntity result) {
						if (result.getLIST().size() > 0) {
							if (recommendList.size() > 0){
								recommendList.clear();
								mHeadView.removeADview();
							}
							if(mTitleList.size() > 0){
								mTitleList.clear();
							}
							if(mThemeIdList.size() > 0){
								mThemeIdList.clear();
							}
							mTitleList.add("推荐");//推荐是固定的,放在第一个mTitleList.add(0,"推荐"),arraylist不建议做插入操作
							mThemeIdList.add(result.getLIST().get(0));//为了和推荐对齐,随便加的一个值
							for(int i=0 ;i <result.getLIST().size() ;i++){
								//前三个为广告,之后的为主题标签
								if(i > 2) {
									mTitleList.add(result.getLIST().get(i).getRoadlineThemeTitle());
									mThemeIdList.add(result.getLIST().get(i));
								}else{
									recommendList.add(result.getLIST().get(i));
								}
							}
							//添加广告数据到view头部
							mHeadView.fillView(recommendList, mMagicHeaderViewPager);
							//初始化Tab对应的viewpager
							initViewPager();
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(ctx, 0, errorMsg, 300)
								.show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ctx)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ImageLoader.getInstance()
															.clearMemoryCache(); // 清除内存缓存
													MyApplication.getInstance()
															.clearLoginData();
													ActivityCollector
															.finishAll();
													dialog.dismiss();
												}
											}).show();
						}
					}

					@Override
					public void onFinish() {
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			switch (requestCode) {
				case 1000:
					themeCity = data.getStringExtra("themeCity");
					location_text.setText(themeCity);
					getThemeRecommend();
					break;
			}
		}

	}
}
