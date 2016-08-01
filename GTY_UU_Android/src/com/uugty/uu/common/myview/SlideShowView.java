package com.uugty.uu.common.myview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.entity.HomePageRecommendEntity.HomePageRecommend;
import com.uugty.uu.main.RecomendActivity;
import com.uugty.uu.main.RecomendWebActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlideShowView extends FrameLayout {

	// 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnFail(R.drawable.uu_default_image_one)
			.showImageForEmptyUri(R.drawable.square_no_pricture)
			.showImageOnLoading(R.drawable.square_no_pricture)
			.cacheInMemory(true).cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	// 轮播图图片数量
	private final static int IMAGE_COUNT = 3;
	// 自动轮播的时间间隔
	private final static int TIME_INTERVAL = 5;

	// 自动轮播启用开关
	private boolean isAutoPlay = true;
	private SwipeRefreshLayout mSwipeLayout;

	// 自定义轮播图的资源
	// private String[] imageUrls;
	private List<HomePageRecommend> imageUrls;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;

	private DecoratorViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;
	private MyPagerAdapter adapter;
	LinearLayout dotLayout;

	public void setIsAutoPlay(boolean isAutoPlay) {
		this.isAutoPlay = isAutoPlay;
	}
	// Handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);
		}

	};

	public SlideShowView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		initData();
		
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);

		dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
		viewPager = (DecoratorViewPager) findViewById(R.id.sildeshow_viewPager);
	}

	public void setSwipeRefreshLayout(SwipeRefreshLayout swipe) {
		this.mSwipeLayout = swipe;

	}

	/**
	 * 开始轮播图切换
	 */
	public void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 2, TIME_INTERVAL,
				TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	public void stopPlay() {
		if(scheduledExecutorService != null){
			scheduledExecutorService.shutdown();
			scheduledExecutorService = null;
		}
	}

	/**
	 * 初始化相关Data
	 */
	private void initData() {
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();
		// 一步任务获取图片
//		 new GetListTask().execute("");
	}

	public void setSource(List<HomePageRecommend> ls) {
		if (null != this.imageUrls)
			this.imageUrls.clear();
		if (null != this.imageViewsList)
			this.imageViewsList.clear();
		if (null != this.dotViewsList)
			this.dotViewsList.clear();

		this.imageUrls = ls;
		initUI(context);
		if (isAutoPlay) {
			startPlay();
		}
	}

	/**
	 * 初始化Views等UI
	 */
	private void initUI(Context context) {
		if (imageUrls == null || imageUrls.size() == 0)
			return;
		dotLayout.removeAllViews();

		// 热点个数与图片特殊相等
		for (int i = 0; i < 3; i++) {
			ImageView view = new ImageView(context);
			if (imageUrls.size() > i) {
				view.setTag(imageUrls.get(i));

				if (i == 0)// 给一个默认图
				 view.setBackgroundResource(R.drawable.page_indicator);

				ImageLoader.getInstance().displayImage(
						APPRestClient.SERVER_IP + imageUrls.get(i).getRoadlineThemeImage(), view, options);
				view.setScaleType(ScaleType.FIT_XY);
				imageViewsList.add(view);
				ImageView dotView = new ImageView(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				if (i == 0) {
					dotView.setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					dotView.setBackgroundResource(R.drawable.page_indicator);
				}
				params.leftMargin = 4;
				params.rightMargin = 4;
				dotLayout.addView(dotView, params);
				dotViewsList.add(dotView);
			}
		}
		/*
		 * if(null!=viewPager){ adapter.notifyDataSetChanged(); }else{}
		 */

		// viewPager.setFocusable(true);
		adapter = new MyPagerAdapter(imageViewsList, imageUrls);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}

	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {
		private List<ImageView> imageList;
		private List<HomePageRecommend> recommendList;

		public MyPagerAdapter(List<ImageView> imageViewsList,
				List<HomePageRecommend> recommendList) {
			this.imageList = imageViewsList;
			this.recommendList = recommendList;
		}

		private int mChildCount = 0;

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			 ((ViewPager)container).removeView((View)object);
//			((ViewPager) container).removeView(imageList.get(position));
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			// ImageView imageView = imageViewsList.get(position);
			imageList.get(position).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					if (!TextUtils.isEmpty(recommendList.get(position)
							.getRoadlineThemeUrl())) {
						// 跳转到webView
						intent.putExtra("roadlineThemeTitle", recommendList
								.get(position).getRoadlineThemeTitle());
						intent.putExtra("roadlineThemeUrl",
								recommendList.get(position)
										.getRoadlineThemeUrl());
						intent.setClass(context, RecomendWebActivity.class);
						context.startActivity(intent);
					} else {
						// 跳转到路线
						intent.putExtra("roadlineThemeTitle", recommendList
								.get(position).getRoadlineThemeTitle());
						intent.putExtra("roadlineThemeId",
								recommendList.get(position)
										.getRoadlineThemeId());
						intent.setClass(context, RecomendActivity.class);
						context.startActivity(intent);
					}
				}
			});
			((ViewPager) container).addView(imageList.get(position));
			return imageList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {


		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			mSwipeLayout.setEnabled(false);
			switch (arg0) {
			case 1:// 正在滑动
				isAutoPlay = false;
				break;
			case 2:// 滑动完毕
				isAutoPlay = true;
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				mSwipeLayout.setEnabled(true);
				if (viewPager.getCurrentItem() == viewPager.getAdapter()
						.getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager
							.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pos) {
			// TODO Auto-generated method stub

			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++) {
				if (i == pos) {
					((View) dotViewsList.get(pos))
							.setBackgroundResource(R.drawable.page_indicator_focused);
				} else {
					((View) dotViewsList.get(i))
							.setBackgroundResource(R.drawable.page_indicator);
				}
			}
		}

	}

	/**
	 * 执行轮播图切换任务
	 * 
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViewsList.size();
				handler.sendEmptyMessage(0);
			}
		}

	}

	@Override
	protected void onDisplayHint(int hint) {
		// TODO Auto-generated method stub
		super.onDisplayHint(hint);
		destoryBitmaps();
		stopPlay();
	}
	/**
	 * 销毁ImageView资源，回收内存
	 * 
	 */
	private void destoryBitmaps() {

		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				// 解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}

	/**
	 * 异步任务,获取数据
	 * 
	 */
	
//	 class GetListTask extends AsyncTask<String, Integer, Boolean> {
//	  
//	  @Override 
//	  protected Boolean doInBackground(String... params) { 
//		  try {//这里一般调用服务端接口获取一组轮播图片
//	  
//			  return true; 
//		  }catch (Exception e) { 
//			  e.printStackTrace(); 
//			  return false; 
//		  } 
//	}
//	  
//	  @Override
//	  protected void onPostExecute(Boolean result) {
//	     super.onPostExecute(result); 
//	     if (result) { 
//	    	 initUI(context); 
//	    	 } 
//	     } 
//	  }
	 

}
