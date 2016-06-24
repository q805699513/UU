package com.uugty.uu.main;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.calendar.CalendarCard;
import com.uugty.uu.common.myview.calendar.CalendarCard.OnCellClickListener;
import com.uugty.uu.common.myview.calendar.CalendarViewAdapter;
import com.uugty.uu.common.myview.calendar.CustomDate;

public class OrderDateActivty extends BaseActivity implements OnClickListener,
		OnCellClickListener {
	private ViewPager mViewPager;
	private int mCurrentIndex = 0;
	private CalendarCard[] mShowViews;
	private CalendarViewAdapter<CalendarCard> adapter;
	private SildeDirection mDirection = SildeDirection.NO_SILDE;

	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}

	private ImageButton preImgBtn, nextImgBtn;
	private TextView monthText;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.order_date_choose;
	}

	@Override
	protected void initGui() {
		mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
		preImgBtn = (ImageButton) this.findViewById(R.id.btnPreMonth);
		nextImgBtn = (ImageButton) this.findViewById(R.id.btnNextMonth);
		monthText = (TextView) this.findViewById(R.id.tvCurrentMonth);
		preImgBtn.setOnClickListener(this);
		nextImgBtn.setOnClickListener(this);

		CalendarCard[] views = new CalendarCard[3];
		for (int i = 0; i < 3; i++) {
			views[i] = new CalendarCard(this, this);
		}
		adapter = new CalendarViewAdapter<>(views);
		setViewPager();

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	private void setViewPager() {
		mViewPager.setAdapter(adapter);
		// mViewPager.setCurrentItem(498);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				measureDirection(position);
				updateCalendarView(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.btnPreMonth:
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
			break;
		case R.id.btnNextMonth:
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
			break;
		default:
			break;
		}
	}

	@Override
	public void clickDate(CustomDate date) {
		Intent mIntent = new Intent();
		String month = "";
		String day = "";
		if (date.getMonth() < 10) {
			month = "0" + String.valueOf(date.getMonth());
		} else {
			month = String.valueOf(date.getMonth());
		}
		if (date.getDay() < 10) {
			day = "0" + String.valueOf(date.getDay());
		} else {
			day = String.valueOf(date.getDay());
		}
		String choose_date = date.getYear() + "-" + month + "-" + day;
		mIntent.putExtra("choose_date", choose_date);
		// 设置结果，并进行传送
		setResult(RESULT_OK, mIntent);
		finish();
	}

	@Override
	public void changeDate(CustomDate date) {
		monthText.setText(date.getYear() + "年" + date.month + "月");
	}

	/**
	 * 计算方向
	 * 
	 * @param arg0
	 */
	private void measureDirection(int arg0) {
		if (arg0 >= mCurrentIndex) {
			mDirection = SildeDirection.RIGHT;

		} else if (arg0 < mCurrentIndex) {
			mDirection = SildeDirection.LEFT;
		}
		mCurrentIndex = arg0;
	}

	// 更新日历视图
	private void updateCalendarView(int arg0) {
		mShowViews = adapter.getAllItems();
		if (mDirection == SildeDirection.RIGHT) {
			mShowViews[arg0 % mShowViews.length].rightSlide();
		} else if (mDirection == SildeDirection.LEFT) {
			mShowViews[arg0 % mShowViews.length].leftSlide();
		}
		mDirection = SildeDirection.NO_SILDE;
	}

}
