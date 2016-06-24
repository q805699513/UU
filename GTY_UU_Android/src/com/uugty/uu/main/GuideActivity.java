package com.uugty.uu.main;

import java.util.ArrayList;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.FlyTxtView;

public class GuideActivity extends BaseActivity {
	private ViewPager viewPager;

	/** 装分页显示的view的数组 */
	private ArrayList<View> pageViews;
	private ImageView imageView;

	/** 将小圆点的图片用数组表示 */
	private ImageView[] imageViews;

	// 包裹滑动图片的LinearLayout
	private ViewGroup viewPics;

	// 包裹小圆点的LinearLayout
	private ViewGroup viewPoints;
	private FlyTxtView guide_flytext1,guide_flytext2,guide_flytext3,guide_flytext4,guide_flytext5;
	private LinearLayout backLin;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void initGui() {
		// 将要分页显示的View装入数组中
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		View view1 = inflater.inflate(R.layout.guide_viewpager_one, null);
		guide_flytext1 = (FlyTxtView) view1.findViewById(R.id.guide_viewpager_one_text);
		guide_flytext1.setTextSize(12);
		guide_flytext1.setTextColor(getResources().getColor(R.color.guide_animation_text_color));
		guide_flytext1.setTexts("大伙儿都说uu客是旅游界的“有步儿”，我不这么认为因为咱不光能打车还能找当地人和订特色民宿。");
		guide_flytext1.startAnimation();
		
		
		View view2 = inflater.inflate(R.layout.guide_viewpager_two, null);
		guide_flytext2 = (FlyTxtView) view2.findViewById(R.id.guide_viewpager_two_text);
		guide_flytext2.setTextSize(12);
		guide_flytext2.setTextColor(getResources().getColor(R.color.guide_animation_text_color));
		guide_flytext2.setTexts("酱紫，那些把车拿出来给你用把时间拿出来带你玩的都是小u。");
		guide_flytext2.startAnimation();
		
		View view3 = inflater.inflate(R.layout.guide_viewpager_three, null);
		guide_flytext3 = (FlyTxtView) view3.findViewById(R.id.guide_viewpager_three_text);
		guide_flytext3.setTextSize(12);
		guide_flytext3.setTextColor(getResources().getColor(R.color.guide_animation_text_color));
		guide_flytext3.setTexts("两个步骤：你就是小u，先认证身份再发布玩法汇报完毕。");
		guide_flytext3.startAnimation();
		
		View view4 = inflater.inflate(R.layout.guide_viewpager_four, null);
		guide_flytext4 = (FlyTxtView) view4.findViewById(R.id.guide_viewpager_four_text);
		guide_flytext4.setTextSize(12);
		guide_flytext4.setTextColor(getResources().getColor(R.color.guide_animation_text_color));
		guide_flytext4.setTexts("首页搜索目标城市地图移到想去城市。 嘘，我才不告诉你还可以看到小u长啥样儿。");
		guide_flytext4.startAnimation();
		
		View view5 = inflater.inflate(R.layout.guide_viewpager_five, null);
		guide_flytext5 = (FlyTxtView) view5.findViewById(R.id.guide_viewpager_five_text);
		guide_flytext5.setTextSize(12);
		guide_flytext5.setTextColor(getResources().getColor(R.color.guide_animation_text_color));
		guide_flytext5.setTexts("能约的不仅是风景，在uu客遇见你想要的旅行！");
		guide_flytext5.startAnimation();
		
		pageViews.add(view1);
		pageViews.add(view2);
		pageViews.add(view3);
		pageViews.add(view4);
		pageViews.add(view5);

		// 创建imageviews数组，大小是要显示的图片的数量
		imageViews = new ImageView[pageViews.size()];
		// 从指定的XML文件加载视图
		viewPics = (ViewGroup) inflater.inflate(R.layout.guide_user_view, null);

		// 实例化小圆点的linearLayout和viewpager
		viewPoints = (ViewGroup) viewPics.findViewById(R.id.guide_user_viewgroup);
		viewPager = (ViewPager) viewPics.findViewById(R.id.guide_user_viewpager);
		backLin = (LinearLayout) viewPics.findViewById(R.id.tabar_back);

		backLin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 添加小圆点的图片
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(GuideActivity.this);
			// 设置小圆点imageview的参数
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);
			lp.setMargins(10, 20, 12, 40);
			imageView.setLayoutParams(lp);
			// imageView.setLayoutParams(new LayoutParams(10, 10));//
			// 创建一个宽高均为20// 的布局
			// 将小圆点layout添加到数组中
			imageViews[i] = imageView;
			// 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
			if (i == 0) {
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			// 将imageviews添加到小圆点视图组
			viewPoints.addView(imageViews[i]);
		}

		// 显示滑动图片的视图
		setContentView(viewPics);

		// 设置viewpager的适配器和监听事件
		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}


	class GuidePageAdapter extends PagerAdapter {

		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(pageViews.get(position));

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pageViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			// TODO Auto-generated method stub
			((ViewPager) v).addView(pageViews.get(position));

			return pageViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			// TODO Auto-generated method stub
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
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
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			for (int i = 0; i < imageViews.length; i++) {
				if(i==0){
					guide_flytext1.startAnimation();
				}
				if(i==1){
					guide_flytext2.startAnimation();
				}
				if(i==2){
					guide_flytext3.startAnimation();
				}
				if(i==3){
					guide_flytext4.startAnimation();
				}
				if(i==4){
					guide_flytext5.startAnimation();
				}
				
				imageViews[position]
						.setBackgroundResource(R.drawable.page_indicator_focused);
				// 不是当前选中的page，其小圆点设置为未选中的状态
				if (position != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator);
				}
			}

		}
	}

}