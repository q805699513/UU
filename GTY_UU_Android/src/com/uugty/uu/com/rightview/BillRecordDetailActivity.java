package com.uugty.uu.com.rightview;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.R.drawable;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;

public class BillRecordDetailActivity extends BaseActivity{

	private TopBackView topTitle;
	private ImageView iconImage;
	private TextView detailText,priceText,rotueText,statusText,timeText,paymentText;
	private String icon,title,price,status,time,payment;
	private RelativeLayout bills_paystyles;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.bill_record_detail;
	}

	@Override
	protected void initGui() {
		if(null!=getIntent()){
			Bundle bundle = getIntent().getExtras();
			icon = bundle.getString("icon");
			title= bundle.getString("title");
			price=bundle.getString("price");
			status=bundle.getString("status");
			time=bundle.getString("time");
			payment=bundle.getString("payment");
		}
		topTitle = (TopBackView) findViewById(R.id.bill_record_detail_title);
		bills_paystyles = (RelativeLayout) findViewById(R.id.bills_paystyles);
		detailText = (TextView) findViewById(R.id.bill_record_detail_text);
		priceText= (TextView) findViewById(R.id.bill_record_detail_price);
		rotueText = (TextView) findViewById(R.id.bill_record_detail_route);
		statusText= (TextView) findViewById(R.id.bill_record_detail_status);
		timeText= (TextView) findViewById(R.id.bill_record_detail_time);
		paymentText= (TextView) findViewById(R.id.bill_record_detail_payment);
		topTitle.setTitle("账单详情");
	}

	@Override
	protected void initAction() {
		detailText.setText(title);
		if(title.equals("")){
			bills_paystyles.setVisibility(View.GONE);
		}else{
			bills_paystyles.setVisibility(View.VISIBLE);
		}
		priceText.setText(price);
		rotueText.setText(title);
		statusText.setText(chooseStauts(Integer.valueOf(status)));//1 进行中 ，2 成功完成 ，3 关闭
		timeText.setText(time);
		if(payment.equals("1"))paymentText.setText("微信支付");
		if(payment.equals("2"))paymentText.setText("钱包支付");
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	public int chooseImage(String type) {
		Class<drawable> cls = R.drawable.class;
		int imageRes = 0;
		try {
			if (type.equals("order_wx_send"))
				imageRes = cls.getDeclaredField("bill_record_payment").getInt(
						null);
			if (type.equals("order_purse_send"))
				imageRes = cls.getDeclaredField("bill_record_payment").getInt(
						null);
			if (type.equals("order_receive"))
				imageRes = cls.getDeclaredField("bill_record_order").getInt(
						null);
			if (type.equals("gratuity_wx_send"))
				imageRes = cls.getDeclaredField("bill_record_tip").getInt(
						null);
			if (type.equals("gratuity_purse_send"))
				imageRes = cls.getDeclaredField("bill_record_tip").getInt(
						null);
			if (type.equals("gratuity_receive"))
				imageRes = cls.getDeclaredField("bill_record_tip").getInt(
						null);
			if (type.equals("widthdraw"))
				imageRes = cls.getDeclaredField("bill_record_drawal").getInt(
						null);
			if (type.equals("recharge"))
				imageRes = cls.getDeclaredField("bill_record_recharge").getInt(
						null);
			if (type.equals("drawback_outcome"))
				imageRes = cls.getDeclaredField("bill_record_refund").getInt(
						null);
			if (type.equals("drawback_income"))
				imageRes = cls.getDeclaredField("bill_record_refund").getInt(
						null);
			if (type.equals("penalty"))
				imageRes = cls.getDeclaredField("bill_record_order").getInt(
						null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageRes;
	}
	
	public String chooseStauts(int num) {
		String name = "";
		switch (num) {
		case 1:
			name = "进行中";
			break;
		case 2:
			name = "成功完成";
			break;
		case 3:
			name = "关闭";
			break;
		case 4:
			name = "取消";
			break;
		default:
			break;
		}
		return name;
	}
}
