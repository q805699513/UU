package com.uugty.uu.discount.c;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.util.ScreenUtils;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.discount.v.AlreadyExpiredFragment;
import com.uugty.uu.discount.v.AlreadyUseFragment;
import com.uugty.uu.discount.v.WaitReceiveFragment;
import com.uugty.uu.discount.v.WaitUseFragment;

public class MyDiscountActivity extends BaseActivity implements
		OnCheckedChangeListener, OnClickListener {
	private RadioGroup group;
	private RadioButton mWaitReceiver;
	private RadioButton mWaitUse;
	private RadioButton mAlreadyExpired;
	private RadioButton mAlreadyUse;
	
	private Fragment WaitReceiveFragment;// 代领取
	private Fragment WaitUseFragment;// 未使用
	private Fragment AlreadyExpired;// 已过期
	private Fragment AlreadyUse;// 已使用
	
	private TextView mUseOrder;//使用规则
	private int width;
	private LinearLayout backView;

	@Override
	protected int getContentLayout() {
		return R.layout.activity_discount;
	}

	@Override
	protected void initGui() {
		width = ScreenUtils.getScreenWidth(this);
		group = (RadioGroup) findViewById(R.id.discount_group);//radioButton组
		mWaitReceiver = (RadioButton) findViewById(R.id.my_discount_wait_receive);//待领取
		mWaitUse = (RadioButton) findViewById(R.id.my_discount_wait_use);//未使用
		mAlreadyExpired = (RadioButton) findViewById(R.id.my_discount_already_expired);//已过期
		mAlreadyUse = (RadioButton) findViewById(R.id.my_discount_already_use);//已使用
		mUseOrder = (TextView) findViewById(R.id.my_discount_use);//使用规则
		
		backView = (LinearLayout) findViewById(R.id.tabar_back);
	}

	@Override
	protected void initAction() {

		group.setOnCheckedChangeListener(this);
		backView.setOnClickListener(this);
		mWaitReceiver.setOnClickListener(this);
		mWaitUse.setOnClickListener(this);
		mAlreadyExpired.setOnClickListener(this);
		mAlreadyUse.setOnClickListener(this);
		mUseOrder.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		SharedPreferenceUtil.getInstance(ctx).setString("showDiscountHint","0");
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				((RadioButton) group.findViewById(R.id.my_discount_wait_receive))
						.setChecked(true);
			}
		}, 200);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		hideFragments(transaction);
		Resources res = this.getResources();
		Drawable fragment_img = res
				.getDrawable(R.drawable.corners_linear_blue);
		switch (checkedId) {
		case R.id.my_discount_wait_receive:
			fragment_img.setBounds(1, 1, mWaitReceiver.getWidth() / 3 * 2,
					5);
			mWaitReceiver.setCompoundDrawables(null, null, null,
					fragment_img);
			mWaitReceiver.setTextColor(getResources().getColor(
					R.color.discount_select_color));
			mWaitUse.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyExpired
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mAlreadyExpired.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyUse.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			mAlreadyUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (WaitReceiveFragment == null) {
				WaitReceiveFragment = new WaitReceiveFragment();
				transaction.add(R.id.my_discount_list_layout, WaitReceiveFragment);
			} else {
				transaction.show(WaitReceiveFragment);
			}
			transaction.commit();
			break;
		case R.id.my_discount_wait_use:
			transaction.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_left_out);
			fragment_img.setBounds(1, 1, mWaitUse.getWidth() / 3 * 2,
					5);
			mWaitUse.setCompoundDrawables(null, null, null,
					fragment_img);
			mWaitUse.setTextColor(getResources().getColor(
					R.color.discount_select_color));
			mWaitReceiver.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitReceiver.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyExpired
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mAlreadyExpired.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyUse.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			mAlreadyUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (WaitUseFragment == null) {
				WaitUseFragment = new WaitUseFragment();
				transaction.add(R.id.my_discount_list_layout, WaitUseFragment);
			} else {
				transaction.show(WaitUseFragment);
			}
			transaction.commit();
			break;
		case R.id.my_discount_already_expired:
			transaction.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_left_out);
			fragment_img.setBounds(1, 1,
					mAlreadyExpired.getWidth() / 3 * 2, 5);
			mAlreadyExpired.setCompoundDrawables(null, null, null,
					fragment_img);
			mAlreadyExpired.setTextColor(getResources().getColor(
					R.color.discount_select_color));
			mWaitReceiver.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitReceiver.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mWaitUse
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyUse.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			mAlreadyUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (AlreadyExpired == null) {
				AlreadyExpired = new AlreadyExpiredFragment();
				transaction.add(R.id.my_discount_list_layout, AlreadyExpired);
			} else {
				transaction.show(AlreadyExpired);
			}
			transaction.commit();
			break;
		case R.id.my_discount_already_use:
			transaction.setCustomAnimations(R.anim.slide_left_in,
					R.anim.slide_right_in);
			fragment_img
					.setBounds(1, 1, mAlreadyUse.getWidth() / 3 * 2, 5);
			mAlreadyUse.setCompoundDrawables(null, null, null,
					fragment_img);
			mAlreadyUse.setTextColor(getResources().getColor(
					R.color.discount_select_color));
			mWaitReceiver.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitReceiver.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mWaitUse
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mWaitUse.setTextColor(getResources().getColor(
					R.color.base_text_color));
			mAlreadyExpired.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			mAlreadyExpired.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (AlreadyUse == null) {
				AlreadyUse = new AlreadyUseFragment();
				transaction.add(R.id.my_discount_list_layout, AlreadyUse);
			} else {
				transaction.show(AlreadyUse);
			}
			transaction.commit();
			break;
		}

	}

	private void hideFragments(FragmentTransaction transaction) {
		if(WaitReceiveFragment != null){
			transaction.hide(WaitReceiveFragment);
		}
		if (WaitUseFragment != null) {
			transaction.hide(WaitUseFragment);
		}
		if (AlreadyExpired != null) {
			transaction.hide(AlreadyExpired);
		}
		if (AlreadyUse != null) {
			transaction.hide(AlreadyUse);
		}
	}

	@Override
	public void onNoDoubleClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tabar_back:
			finish();
			break;
		case R.id.my_discount_use:
			Intent intent = new Intent();
			intent.setClass(this, OrderUseActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
