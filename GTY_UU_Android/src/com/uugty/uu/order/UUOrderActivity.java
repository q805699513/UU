package com.uugty.uu.order;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.util.ScreenUtils;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.OrderDiscountNumEntity;

public class UUOrderActivity extends BaseActivity implements
		OnCheckedChangeListener, OnClickListener {
	private RadioButton completeRadioBtn, ongoingRadioBtn,
			order_list_Aftermarket, order_list_Confirm, order_list_Failure;
	private RadioGroup group;
	// private TopBackView backView;
	private Fragment UUfinshFragment;// 代接受
	private Fragment UUcarryFragment;// 代付款
	private Fragment UUComfirmFragment;// 已接受
	private Fragment UUAftermarketFragment;// 售后
	private Fragment UUFailureFragment;// 失效
	private int width;
	private LinearLayout backView;
	private Handler mHandler, finshHandler, comfirHandler, afterHandler,
			FaiHandler;
	private String frag = "1", getid;
	private TextView order_my_buy, order_my_receive;
	private String role;
	private TextView finish_text,carry_text,confirm_text,after_text;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_orderfrom;
	}

	@Override
	protected void initGui() {
		getid = getIntent().getStringExtra("from");
		width = ScreenUtils.getScreenWidth(this);
		carry_text = (TextView) findViewById(R.id.order_list_ongoing_hint);
		finish_text = (TextView) findViewById(R.id.order_list_complete_hint);
		confirm_text = (TextView) findViewById(R.id.order_list_Confirm_hint);
		after_text = (TextView) findViewById(R.id.order_list_Aftermarket_hint);


		order_list_Confirm = (RadioButton) findViewById(R.id.order_list_Confirm);
		order_list_Failure = (RadioButton) findViewById(R.id.order_list_Failure);
		order_list_Aftermarket = (RadioButton) findViewById(R.id.order_list_Aftermarket);
		completeRadioBtn = (RadioButton) findViewById(R.id.order_list_complete);
		ongoingRadioBtn = (RadioButton) findViewById(R.id.order_list_ongoing);
		order_my_receive = (TextView) findViewById(R.id.order_my_receive);
		order_my_buy = (TextView) findViewById(R.id.order_my_buy);
		order_my_buy.setText("我购买的");
		order_my_receive.setText("我出售的");
		group = (RadioGroup) findViewById(R.id.group);
		backView = (LinearLayout) findViewById(R.id.tabar_back);
		if (getid.equals("receive")) {
			SharedPreferenceUtil.getInstance(ctx).setString("showReceiverHint","0");
			myreceive();
		} else {
			SharedPreferenceUtil.getInstance(ctx).setString("showBuyHint","0");
			mybought();
		}
		/*
		 * ongoingRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
		 * R.drawable.fragment_chang_two);
		 */
		// ft = getSupportFragmentManager().beginTransaction();
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	}

	@Override
	protected void initAction() {

		group.setOnCheckedChangeListener(this);
		backView.setOnClickListener(this);
		order_my_buy.setOnClickListener(this);
		order_my_receive.setOnClickListener(this);
		order_list_Aftermarket.setOnClickListener(this);
		order_list_Failure.setOnClickListener(this);
		order_list_Confirm.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// ft.replace(R.id.layout, new UUCarryFragment()).commit();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((RadioButton) group.findViewById(R.id.order_list_ongoing))
						.setChecked(true);
			}
		}, 200);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sendHintRequest();
	}

	private void sendHintRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.ORDER_DISCOUNT_NUM, params,
				new APPResponseHandler<OrderDiscountNumEntity>(
						OrderDiscountNumEntity.class, this) {
					@Override
					public void onSuccess(OrderDiscountNumEntity result) {
						if(result.getOBJECT() != null){
							if("1".equals(getRole())) {
								if (result.getOBJECT().getUkCreateNum() > 0) {
									carry_text.setText(String.valueOf(result.getOBJECT().getUkCreateNum()));
									carry_text.setVisibility(View.VISIBLE);
								} else {
									carry_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getUkPaymentNum() > 0) {
									finish_text.setText(String.valueOf(result.getOBJECT().getUkPaymentNum()));
									finish_text.setVisibility(View.VISIBLE);
								} else {
									finish_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getUkAgreeNum() > 0) {
									confirm_text.setText(String.valueOf(result.getOBJECT().getUkAgreeNum()));
									confirm_text.setVisibility(View.VISIBLE);
								} else {
									confirm_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getUkDrawbackNum() > 0) {
									after_text.setText(String.valueOf(result.getOBJECT().getUkDrawbackNum()));
									after_text.setVisibility(View.VISIBLE);
								} else {
									after_text.setVisibility(View.GONE);
								}
							}else if("2".equals(getRole())){
								if (result.getOBJECT().getXiaouCreateNum() > 0) {
									carry_text.setText(String.valueOf(result.getOBJECT().getXiaouCreateNum()));
									carry_text.setVisibility(View.VISIBLE);
								} else {
									carry_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getXiaouPaymentNum() > 0) {
									finish_text.setText(String.valueOf(result.getOBJECT().getXiaouPaymentNum()));
									finish_text.setVisibility(View.VISIBLE);
								} else {
									finish_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getXiaouAgreeNum() > 0) {
									confirm_text.setText(String.valueOf(result.getOBJECT().getXiaouAgreeNum()));
									confirm_text.setVisibility(View.VISIBLE);
								} else {
									confirm_text.setVisibility(View.GONE);
								}
								if (result.getOBJECT().getXiaouDrawbackNum() > 0) {
									after_text.setText(String.valueOf(result.getOBJECT().getXiaouDrawbackNum()));
									after_text.setVisibility(View.VISIBLE);
								} else {
									after_text.setVisibility(View.GONE);
								}
							}

						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {
					}
				});
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		hideFragments(transaction);
		Resources res = this.getResources();
		Drawable fragment_img = res
				.getDrawable(R.drawable.order_fragment_chang_image);
		switch (checkedId) {
		case R.id.order_list_Confirm:
			frag = "3";
			fragment_img.setBounds(1, 1, order_list_Confirm.getWidth() / 3 * 2,
					5);
			order_list_Confirm.setCompoundDrawables(null, null, null,
					fragment_img);
			order_list_Confirm.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			ongoingRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			ongoingRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			completeRadioBtn
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			completeRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Aftermarket.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			order_list_Aftermarket.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Failure.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Failure.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (UUComfirmFragment == null) {
				UUComfirmFragment = new UUComfirmFragment();
				transaction.add(R.id.order_list_layout, UUComfirmFragment);
			} else {
				transaction.show(UUComfirmFragment);
				comfirHandler.sendMessage(comfirHandler.obtainMessage(1));
			}
			transaction.commit();
			break;
		case R.id.order_list_Failure:
			frag = "5";
			transaction.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_left_out);
			fragment_img.setBounds(1, 1, order_list_Failure.getWidth() / 3 * 2,
					5);
			order_list_Failure.setCompoundDrawables(null, null, null,
					fragment_img);
			order_list_Failure.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			ongoingRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			ongoingRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			completeRadioBtn
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			completeRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Aftermarket.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			order_list_Aftermarket.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Confirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Confirm.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (UUFailureFragment == null) {
				UUFailureFragment = new UUFailureFragment();
				transaction.add(R.id.order_list_layout, UUFailureFragment);
			} else {
				transaction.show(UUFailureFragment);
				FaiHandler.sendMessage(FaiHandler.obtainMessage(1));
			}
			transaction.commit();
			break;
		case R.id.order_list_Aftermarket:
			frag = "4";
			transaction.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_left_out);
			fragment_img.setBounds(1, 1,
					order_list_Aftermarket.getWidth() / 3 * 2, 5);
			order_list_Aftermarket.setCompoundDrawables(null, null, null,
					fragment_img);
			order_list_Aftermarket.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			ongoingRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			ongoingRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			completeRadioBtn
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			completeRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Confirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Confirm.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Failure.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Failure.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (UUAftermarketFragment == null) {
				UUAftermarketFragment = new UUAftermarketFragment();
				transaction.add(R.id.order_list_layout, UUAftermarketFragment);
			} else {
				transaction.show(UUAftermarketFragment);
				afterHandler.sendMessage(afterHandler.obtainMessage(1));
			}
			transaction.commit();
			break;
		case R.id.order_list_complete:
			frag = "2";
			transaction.setCustomAnimations(R.anim.slide_left_in,
					R.anim.slide_right_in);
			// animtion.replace(R.id.layout, new UUFinshFragment()).commit();
			fragment_img
					.setBounds(1, 1, completeRadioBtn.getWidth() / 3 * 2, 5);
			completeRadioBtn.setCompoundDrawables(null, null, null,
					fragment_img);
			completeRadioBtn.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			ongoingRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			ongoingRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Aftermarket.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			order_list_Aftermarket.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Confirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Confirm.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Failure.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Failure.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (UUfinshFragment == null) {
				UUfinshFragment = new UUFinshFragment();
				transaction.add(R.id.order_list_layout, UUfinshFragment);
			} else {
				transaction.show(UUfinshFragment);
				finshHandler.sendMessage(finshHandler.obtainMessage(1));
			}
			transaction.commit();
			break;
		case R.id.order_list_ongoing:
			frag = "1";
			transaction.setCustomAnimations(R.anim.slide_left_in,
					R.anim.slide_right_in);
			fragment_img.setBounds(1, 1, ongoingRadioBtn.getWidth() / 3 * 2, 5);
			ongoingRadioBtn
					.setCompoundDrawables(null, null, null, fragment_img);
			ongoingRadioBtn.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			completeRadioBtn
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			completeRadioBtn.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Aftermarket.setCompoundDrawablesWithIntrinsicBounds(0,
					0, 0, 0);
			order_list_Aftermarket.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Confirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Confirm.setTextColor(getResources().getColor(
					R.color.base_text_color));
			order_list_Failure.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					0);
			order_list_Failure.setTextColor(getResources().getColor(
					R.color.base_text_color));
			if (UUcarryFragment == null) {
				UUcarryFragment = new UUCarryFragment();
				transaction.add(R.id.order_list_layout, UUcarryFragment);
			} else {
				transaction.show(UUcarryFragment);
				mHandler.sendMessage(mHandler.obtainMessage(1));
			}
			transaction.commit();
			break;
		}

	}

	private void hideFragments(FragmentTransaction transaction) {
		if (UUfinshFragment != null) {
			transaction.hide(UUfinshFragment);
		}
		if (UUcarryFragment != null) {
			transaction.hide(UUcarryFragment);
		}
		if (UUComfirmFragment != null) {
			transaction.hide(UUComfirmFragment);
		}
		if (UUAftermarketFragment != null) {
			transaction.hide(UUAftermarketFragment);
		}
		if (UUFailureFragment != null) {
			transaction.hide(UUFailureFragment);
		}
	}

	@Override
	public void onNoDoubleClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tabar_back:
			finish();
			backView.setClickable(false);
			break;
		case R.id.order_my_receive:
			myreceive();
			sendHintRequest();
			if (frag.equals("1")) {
				Message msg = new Message();
				msg.what = 11;
				mHandler.sendMessage(msg);
			} else if (frag.equals("2")) {
				Message fmsg = new Message();
				fmsg.what = 22;
				finshHandler.sendMessage(fmsg);
			} else if (frag.equals("3")) {
				Message fmsg = new Message();
				fmsg.what = 33;
				comfirHandler.sendMessage(fmsg);
			} else if (frag.equals("4")) {
				Message fmsg = new Message();
				fmsg.what = 44;
				afterHandler.sendMessage(fmsg);
			} else if (frag.equals("5")) {
				Message fmsg = new Message();
				fmsg.what = 55;
				FaiHandler.sendMessage(fmsg);
			}
			break;
		case R.id.order_my_buy:
			mybought();
			sendHintRequest();
			if (frag.equals("1")) {
				Message msg = new Message();
				msg.what = 11;
				mHandler.sendMessage(msg);
			} else if (frag.equals("2")) {
				Message fmsg = new Message();
				fmsg.what = 22;
				finshHandler.sendMessage(fmsg);
			} else if (frag.equals("3")) {
				Message comfirmsg = new Message();
				comfirmsg.what = 33;
				comfirHandler.sendMessage(comfirmsg);
			} else if (frag.equals("4")) {
				Message aftermsg = new Message();
				aftermsg.what = 44;
				afterHandler.sendMessage(aftermsg);
			} else if (frag.equals("5")) {
				Message failmsg = new Message();
				failmsg.what = 55;
				FaiHandler.sendMessage(failmsg);
			}
			break;

		default:
			break;
		}
	}

	// 向待付款发送消息 切换模式和刷新数据
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	// 向待接收发送消息 切换模式和刷新数据
	public void setFinshHandler(Handler handler) {
		finshHandler = handler;
	}

	// 向待确认发送消息 切换模式和刷新数据
	public void setcomfirHandler(Handler handler) {
		comfirHandler = handler;
	}

	// 向售后发送消息 切换模式和刷新数据
	public void setafterHandler(Handler handler) {
		afterHandler = handler;
	}

	// 向已失效发送消息 切换模式和刷新数据
	public void setfailHandler(Handler handler) {
		FaiHandler = handler;
	}

	// 我接收的
	private void myreceive() {
		order_my_receive.setTextColor(Color.parseColor("#f15353"));
		order_my_buy.setTextColor(Color.parseColor("#000000"));
		setRole("2");
	}

	// 我购买的
	private void mybought() {
		// TODO Auto-generated method stub
		order_my_buy.setTextColor(Color.parseColor("#f15353"));
		order_my_receive.setTextColor(Color.parseColor("#000000"));
		setRole("1");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/* 在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment */
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1000:
				if (null != UUComfirmFragment)
					UUComfirmFragment.onActivityResult(requestCode, resultCode,
							data);
				break;

			default:
				break;
			}
		}

	}

}
