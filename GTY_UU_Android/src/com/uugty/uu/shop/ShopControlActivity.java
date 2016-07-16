package com.uugty.uu.shop;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.map.PublishServicesActivity;

public class ShopControlActivity extends BaseActivity implements OnCheckedChangeListener {

    private TopBackView titleView;
	private RadioButton mSellRadioBtn, mStopRadioBtn;
	private RadioGroup group;
	private SellFragment mSellFragment;
	private StopFragment mStopFragment;
	private TextView mPublishText;
	@Override
	protected int getContentLayout() {
		return R.layout.activity_shopcontrol;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.shop_top);
		titleView.setTitle("服务管理");
		mSellRadioBtn=(RadioButton) findViewById(R.id.SellRadioBtn);
		mStopRadioBtn=(RadioButton) findViewById(R.id.StopRadioBtn);
		group = (RadioGroup) findViewById(R.id.shop_group);
		mPublishText = (TextView) findViewById(R.id.shop_control_publish);

	}

	@Override
	protected void initAction() {

		group.setOnCheckedChangeListener(this);
		mPublishText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(ShopControlActivity.this,PublishServicesActivity.class);
				i.putExtra("from", "framgent");
				startActivity(i);
			}
		});
	}

	@Override
	protected void initData() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				((RadioButton) group.findViewById(R.id.SellRadioBtn))
						.setChecked(true);
			}
		}, 200);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		hideFragments(transaction);
		Resources res = this.getResources();
		Drawable fragment_img = res
				.getDrawable(R.drawable.order_fragment_chang_image);
		switch (checkedId) {
			case R.id.SellRadioBtn:
				fragment_img.setBounds(1, 1, mSellRadioBtn.getWidth() / 3 * 2,
						5);
				mSellRadioBtn.setCompoundDrawables(null, null, null,
						fragment_img);
				mSellRadioBtn.setTextColor(getResources().getColor(
						R.color.order_status_text_color));
				mStopRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				mStopRadioBtn.setTextColor(Color.parseColor("#666666"));
				if (mSellFragment == null) {
					mSellFragment = new SellFragment();
					transaction.add(R.id.shop_control_layout, mSellFragment);
				} else {
					transaction.show(mSellFragment);
				}
				transaction.commit();
				break;
			case R.id.StopRadioBtn:
				fragment_img.setBounds(1, 1, mSellRadioBtn.getWidth() / 3 * 2,
						5);
				mStopRadioBtn.setCompoundDrawables(null, null, null,
						fragment_img);
				mStopRadioBtn.setTextColor(getResources().getColor(
						R.color.order_status_text_color));
				mSellRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				mSellRadioBtn.setTextColor(Color.parseColor("#666666"));
				if (mStopFragment == null) {
					mStopFragment = new StopFragment();
					transaction.add(R.id.shop_control_layout, mStopFragment);
				} else {
					transaction.show(mStopFragment);
				}
				transaction.commit();
				break;

			default:
				break;
		}
	}
	private void hideFragments(FragmentTransaction transaction) {
		if (mSellFragment != null) {
			transaction.hide(mSellFragment);
		}
		if (mStopFragment != null) {
			transaction.hide(mStopFragment);
		}
	}

}
