package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.uugty.uu.R;
import com.uugty.uu.appstart.Constant;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.connectmanager.SMSBroadcastReceiver;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.modeal.MessageModeal;

public class RightForgetActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout imageView;
	private EditText mobileEdit, smsEdit;
	private Button smsBtn, commitBtn;
	private TimeCount time;
	private SpotsDialog loadingDialog;
	private String mobile = "", sms = "";
	// 短信监听广播
	private SMSBroadcastReceiver mSMSBroadcastReceiver;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.right_forget;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		imageView = (LinearLayout) findViewById(R.id.tabar_back);
		mobileEdit = (EditText) findViewById(R.id.invilide_phone_num);
		smsEdit = (EditText) findViewById(R.id.invilide_sms_code);
		smsBtn = (Button) findViewById(R.id.invilide_sms_send);
		commitBtn = (Button) findViewById(R.id.invilide_btn);
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
	}

	@Override
	protected void initAction() {
		imageView.setOnClickListener(this);
		smsBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				smsBtn.setEnabled(false);
				sendSMS();
				time.start();
				
			}
		});
		commitBtn.setOnClickListener(this);

		mobileEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				mobile = s.toString();
				if (null == mobile)
					mobile = "";
				if (!mobile.equals("")) {
					smsBtn.setTextColor(getResources().getColor(
							R.color.select_color));
					smsBtn.setEnabled(true);
				} else {
					smsBtn.setTextColor(getResources().getColor(R.color.gray));
					smsBtn.setEnabled(false);
				}
				if (!mobile.equals("") && !sms.equals("")) {
					commitBtn
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					commitBtn.setEnabled(true);
				} else {
					commitBtn
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					commitBtn.setEnabled(false);
				}
			}
		});

		smsEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				sms = s.toString();
				if (null == sms)
					sms = "";
				if (!mobile.equals("") && !sms.equals("")) {
					commitBtn
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					commitBtn.setEnabled(true);
				} else {
					commitBtn
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					commitBtn.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// 生成广播处理
		mSMSBroadcastReceiver = new SMSBroadcastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		// 注册广播
		this.registerReceiver(mSMSBroadcastReceiver, intentFilter);

		mSMSBroadcastReceiver
				.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
					@Override
					public void onReceived(String message) {

						if (message != null) {
							smsEdit.setText(message);
							RightForgetActivity.this
									.unregisterReceiver(mSMSBroadcastReceiver);
						}
					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imageView.setClickable(true);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			imageView.setClickable(false);
			break;
		case R.id.invilide_btn:
			Commit();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 注销短信监听广播
		try {
			if (null != mSMSBroadcastReceiver) {

				this.unregisterReceiver(mSMSBroadcastReceiver);
			}
		} catch (Exception e) {
		}
		super.onDestroy();

	}
	// 验证码

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			smsBtn.setText("获取验证码");
			smsBtn.setEnabled(true);

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			smsBtn.setEnabled(false);
			smsBtn.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	// 发送短信
	private void sendSMS() {
		RequestParams params = new RequestParams();
		params.put("userTel", mobile);
		params.put("type", "2");
		params.put("uuid", Constant.UUID);
		APPRestClient
				.post(this, ServiceCode.UUCODEY_INTERFACE, params,
						new APPResponseHandler<MessageModeal>(
								MessageModeal.class, this) {
							@Override
							public void onSuccess(MessageModeal result) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									sendSMS();
								} else {
								// TODO Auto-generated method stub
								CustomToast.makeText(RightForgetActivity.this,
										0, errorMsg, 300).show();
								smsBtn.setEnabled(true);
								if (errorCode == -999) {
									new AlertDialog.Builder(
											RightForgetActivity.this)
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

								// Toast.makeText(ctx, "onFinish",
								// Toast.LENGTH_SHORT).show();

							}
						});
	}

	// 提交按钮
	private void Commit() {

		// 显示等待层
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.put("type", "6");
		params.put("content", mobileEdit.getText().toString().trim() + ","
				+ smsEdit.getText().toString().trim());
		APPRestClient.post(this, ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						loadingDialog.dismiss();
						Intent intent = new Intent();
						intent.setClass(RightForgetActivity.this,
								SetPayPwdActivity.class);
						startActivity(intent);
						if(null==MyApplication.getInstance().getUserInfo()){
							intent.putExtra("topage",
									RightForgetActivity.class.getName());
							intent.setClass(RightForgetActivity.this, LoginActivity.class);
							startActivity(intent);
						}else{
						MyApplication.getInstance().getUserInfo().getOBJECT()
								.setUserTelValidate("1");
						MyApplication
								.getInstance()
								.getUserInfo()
								.getOBJECT()
								.setUserTel(
										mobileEdit.getText().toString().trim());
						}
						new Handler().postDelayed(new Runnable() {
							public void run() {
								// 显示dialog
								finish();
							}
						}, 500);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							Commit();
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(RightForgetActivity.this)
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
