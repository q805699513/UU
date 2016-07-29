package com.uugty.uu.com.rightview;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.order.UUOrderActivity;

public class PriceDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView priceText, payTypeText, payTimeText;
	private String price, payType;
	private ImageView title;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.price_detail;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			price = getIntent().getStringExtra("price");
			payType = getIntent().getStringExtra("payType");
		}
		title = (ImageView) findViewById(R.id.price_detail_title_image);
		priceText = (TextView) findViewById(R.id.pay_detail_price_text);
		payTypeText = (TextView) findViewById(R.id.pay_detail_type);
		payTimeText = (TextView) findViewById(R.id.pay_detail_pay_time);
		priceText.setText(price);
		if (payType.equals("1")) {
			payTypeText.setText("钱包支付");
		} else if(payType.equals("2")){
			payTypeText.setText("微信支付");
		}else{
			payTypeText.setText("支付宝支付");
		}
		payTimeText.setText(DateUtil.currentTime("yyyy-MM-dd HH:mm"));

	}

	@Override
	protected void initAction() {
		title.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
       ActivityCollector
		.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatPaypriceActivity");
	}
	@Override
	public void onNoDoubleClick(View v) {
		ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.order.UUPaypriceActivity");
		Intent intent = new Intent();
		intent.putExtra("from", "priceDetail");
		intent.setClass(this, UUOrderActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityCollector
					.removeSpecifiedActivity("com.uugty.uu.order.UUPaypriceActivity");
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatPaypriceActivity");
			Intent intent = new Intent();
			intent.putExtra("from", "priceDetail");
			intent.setClass(this, UUOrderActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

}
