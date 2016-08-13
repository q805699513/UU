package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BoundBankEntity;
import com.uugty.uu.entity.UserPurseEntity;

public class MyPriceActivity extends BaseActivity implements OnClickListener {

	private TextView txt_price;// 金额
	private LinearLayout price_back;
	private RelativeLayout price_more;
	private Button price_recharge, price_withdraw_cash;// 充值 提现

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_price;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub

		price_back = (LinearLayout) findViewById(R.id.tabar_back);
		price_recharge = (Button) findViewById(R.id.price_recharge);
		price_withdraw_cash = (Button) findViewById(R.id.price_withdraw_cash);
		price_more = (RelativeLayout) findViewById(R.id.container_more);
		txt_price = (TextView) findViewById(R.id.price_number);

	}

	@Override
	protected void initAction() {

		price_back.setOnClickListener(this);
		price_recharge.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View view) {
				Intent intent = new Intent();
				intent.setClass(MyPriceActivity.this, RechargeActivity.class);
				startActivity(intent);
			}
		});
		price_withdraw_cash.setOnClickListener(this);
		price_more.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		price_back.setClickable(true);
		// 调用接口
		sendRequest();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {

		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			price_back.setClickable(false);
			break;
		// 提现
		case R.id.price_withdraw_cash:
			Intent intent = new Intent();
			intent.setClass(MyPriceActivity.this,
					WithdrawcashActivity.class);
			startActivity(intent);
//			getCardListRequest();
			break;
		case R.id.container_more:
			RightMenuPopupWindow mLifeHallWindow = new RightMenuPopupWindow(
					MyPriceActivity.this);
			mLifeHallWindow.showAtLocation(MyPriceActivity.this
					.findViewById(R.id.container_more), Gravity.TOP
					| Gravity.RIGHT, (int) (TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 15, getResources()
							.getDisplayMetrics())), (int) (TypedValue
					.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75,
							getResources().getDisplayMetrics())));
			break;
		default:
			break;
		}

	}

	private void sendRequest() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_PURSE, params,
				new APPResponseHandler<UserPurseEntity>(UserPurseEntity.class,
						this) {
					@Override
					public void onSuccess(UserPurseEntity result) {
						if (result != null) {
							txt_price
									.setText(result.getOBJECT().getUserPurse());
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(MyPriceActivity.this)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).show();
						}
					}}

					@Override
					public void onFinish() {
						ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.com.rightview.RechargeActivity");
					}
				});
	}

	private void getCardListRequest() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_BOUND_BANK_CARDLIST, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,
						this) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						Intent intent = new Intent();
						if (result.getLIST().size() > 0) {
							// 跳转银行卡列表
//							intent.putExtra("type", "cash");
							intent.putExtra("list","1");
						}else {
							intent.putExtra("list","0");
						}

						intent.setClass(MyPriceActivity.this,
								WithdrawcashActivity.class);
						startActivity(intent);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getCardListRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(MyPriceActivity.this)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											}).show();
						}
					}}

					@Override
					public void onFinish() {

					}
				});

	}
}
