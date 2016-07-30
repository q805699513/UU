package com.uugty.uu.order;

import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class InsureOrderDetailActivity extends BaseActivity{

	private TextView mTopic;
	private TextView mPeerPrice;
	private TextView mInsure;
	private TextView mDiscount;
	private TextView mRealPrice;
	private TextView mOrderPrice;
	private TextView mClick;

	@Override
	protected int getContentLayout() {
		return R.layout.activity_insure_orderdetail;
	}

	@Override
	protected void initGui() {

		//display对象获取屏幕的宽高
		Display dis = getWindowManager().getDefaultDisplay();
		//调整弹出框位置
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		win.setAttributes(lp);

		mTopic = (TextView) findViewById(R.id.insure_ordertopic);
		mPeerPrice = (TextView) findViewById(R.id.insure_order_perprice);
		mInsure = (TextView) findViewById(R.id.insure_order_baoxian);
		mDiscount = (TextView) findViewById(R.id.insure_order_daijinquang);
		mRealPrice = (TextView) findViewById(R.id.insure_realprice);
		mOrderPrice = (TextView) findViewById(R.id.insure_order_price);
		mClick = (TextView) findViewById(R.id.order_insure_detail);

	}
	

	@Override
	protected void initAction() {
		mClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		if(getIntent() != null){
			mTopic.setText(getIntent().getStringExtra("topic"));
			mPeerPrice.setText(getIntent().getStringExtra("perPrice"));
			if((getIntent().getStringExtra("insure").contains("1"))) {
				mInsure.setText(" + ￥" + "5" + "x" + getIntent().getStringExtra("insureNum"));
			}else if((getIntent().getStringExtra("insure").contains("2"))) {
				mInsure.setText(" + ￥" + "10" + "x" + getIntent().getStringExtra("insureNum"));
			}else if((getIntent().getStringExtra("insure").contains("3"))) {
				mInsure.setText(" + ￥" + "15" + "x" + getIntent().getStringExtra("insureNum"));
			}
			if(null != getIntent().getStringExtra("discount") && !"".equals(getIntent().getStringExtra("discount"))){
				mDiscount.setText(" - ￥" + getIntent().getStringExtra("discount"));
			}
			mRealPrice.setText(" ￥" + getIntent().getStringExtra("realPrice"));
			mOrderPrice.setText(getIntent().getStringExtra("realPrice"));

		}

	}
}
