package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.connectmanager.SMSBroadcastReceiver;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.modeal.MessageModeal;

public class ForgetPayPwdActivity extends BaseActivity implements
		OnClickListener {

	private TopBackView title;
	private TextView mobleText;
	private EditText smsEdit;
	private Button btn,commitBtn;
	private TimeCount time;
	private String sms="";
	// 短信监听广播
	private SMSBroadcastReceiver mSMSBroadcastReceiver;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.forget_pay_pwd;
	}

	@Override
	protected void initGui() {
		title = (TopBackView) findViewById(R.id.forget_pay_pwd_title);
		mobleText = (TextView) findViewById(R.id.forget_pay_pwd_mobile_text);
		btn = (Button) findViewById(R.id.forget_pay_pwd_sms_send);
		smsEdit = (EditText) findViewById(R.id.forget_pay_pwd_sms_code);
		commitBtn = (Button) findViewById(R.id.forget_pay_pwd_btn);
		title.setTitle("找回密码");
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					ForgetPayPwdActivity.class.getName());
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
		}else{
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserTelValidate().equals("1")) {
				if (!MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserTel().equals("")) {
					String mobile = MyApplication.getInstance().getUserInfo()
							.getOBJECT().getUserTel();
					mobleText.setText("请输入" + mobile.substring(0, 3) + "****"
							+ mobile.substring(7, 11)+"收到的短信校检码。");
				}
			}
			
		}

	}

	@Override
	protected void initAction() {
		btn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
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
				if(!sms.equals("")&&!sms.equals("")){
					commitBtn.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					commitBtn.setEnabled(true);
				}else{
					commitBtn.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					commitBtn.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
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
							ForgetPayPwdActivity.this
									.unregisterReceiver(mSMSBroadcastReceiver);
						}
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
		case R.id.forget_pay_pwd_sms_send:
			sendSMS();
			break;
		case R.id.forget_pay_pwd_btn:
			//跳转值设置密码
			Intent intent = new Intent();
			intent.putExtra("smsCode", smsEdit.getText().toString().trim());
			intent.putExtra("from", "ForgetPayPwdActivity");
			intent.setClass(this, SetPayPwdActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn.setText("获取验证码");
			btn.setClickable(true);

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn.setClickable(false);
			btn.setText(millisUntilFinished / 1000 + "秒");
		}
	}
	
	// 发送短信
		private void sendSMS() {
			RequestParams params = new RequestParams();
			params.put("userTel",MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserTel() );
			params.put("mobileCountryCode",MyApplication.getInstance().getUserInfo()
					.getOBJECT().getMobileCountryCode());
			params.put("type", "3");
			params.put("uuid", MyApplication.getInstance().getUuid());
			APPRestClient.post(this, ServiceCode.UUCODEY_INTERFACE, params,
					new APPResponseHandler<MessageModeal>(MessageModeal.class,this) {
						@Override
						public void onSuccess(MessageModeal result) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								sendSMS();
							} else {
							// TODO Auto-generated method stub
							CustomToast.makeText(ForgetPayPwdActivity.this, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ForgetPayPwdActivity.this)
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

		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
					time.start();
					break;
				}
			};
		};
		
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
}
