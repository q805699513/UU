package com.uugty.uu.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.helper.DemoHXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.connectmanager.SMSBroadcastReceiver;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.PhoneInfo;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.modeal.MessageModeal;
import com.uugty.uu.modeal.UUlogin;

public class RegsterActivity extends BaseActivity implements OnClickListener {

	// 用户名 短信验证码 用户密码
	private String user_phone, user_coedy, userPassword;
	// 发送验证码按钮
	private TextView sendMessagebtn, registText;
	// 注册按钮
	private Button regsterbtn;
	// uu 协议
	private TextView agreement;
	private TimeCount time;
	private RelativeLayout argmentRel;
	private LinearLayout closeLin;
	private TextView mPhoneCode;
	private String phoneCode = "86";

	//选择国家和地区
	private LinearLayout mSelectCountry;
	private TextView mCountryName;

	// 获取用户输入信息
	private EditText edit_phone, edit_codey;

	private ImageView usernameClear;
	// 短信监听广播
	private SMSBroadcastReceiver mSMSBroadcastReceiver;
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private String toPage,jumpPage;
	private SpotsDialog loadingDialog;
	public final static int REQUEST_STARTING = 1;//地区返回

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uu_regsit;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			toPage = getIntent().getStringExtra("toPage");
			jumpPage = getIntent().getStringExtra("jumpPage");
		}
		mPhoneCode = (TextView) findViewById(R.id.regist_phonecode);
		mSelectCountry = (LinearLayout) findViewById(R.id.regist_country);
		mCountryName = (TextView) findViewById(R.id.country_name);
		edit_phone = (EditText) findViewById(R.id.regist_edit_phone);
		usernameClear = (ImageView) findViewById(R.id.regist_username_clear_image);
		sendMessagebtn = (TextView) findViewById(R.id.send_messagebtn);
		closeLin = (LinearLayout) findViewById(R.id.regist_back_lin);
		edit_codey = (EditText) findViewById(R.id.edit_yan);
		registText = (TextView) findViewById(R.id.regist_text);

		regsterbtn = (Button) findViewById(R.id.regster_btn);
		regsterbtn.setClickable(false);
		agreement = (TextView) findViewById(R.id.regist_agreement_text);
		argmentRel = (RelativeLayout) findViewById(R.id.regsit_argment_rel);
		if (!TextUtils.isEmpty(toPage) && toPage.equals("forgetPwd")) {
			registText.setText("忘记密码");
			argmentRel.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(toPage) && toPage.equals("wxLogin")) {
			registText.setText("验证手机");
			argmentRel.setVisibility(View.GONE);
			regsterbtn.setText("验证");
		}

		user_phone = new PhoneInfo(this).getNativePhoneNumber();
		if(!TextUtils.isEmpty(user_phone)){
			if(user_phone.contains("+86")){
				edit_phone.setText(user_phone.substring(3, user_phone.length()));
			}else if(user_phone.contains("00000000000")){
				edit_phone.setText("");
			}else{
				edit_phone.setText(user_phone);
			}
			
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		// initLocation();
		sendMessagebtn.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
				user_phone = edit_phone.getText().toString().trim();
				if (user_phone.equals("")) {

					CustomToast.makeText(ctx, 0, "手机号为空", 200).show();
				}else {
					time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
					time.start();
					sendSMS();
				}
			}
		});
		regsterbtn.setOnClickListener(this);
		closeLin.setOnClickListener(this);
		agreement.setOnClickListener(this);
		usernameClear.setOnClickListener(this);

		mSelectCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(ctx, CountryActivity.class);
				startActivityForResult(intent, REQUEST_STARTING);
			}
		});

		edit_phone.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s.toString())) {
					usernameClear.setVisibility(View.VISIBLE);
					regsterbtn.setClickable(true);
					regsterbtn.setBackgroundColor(getResources().getColor(R.color.regist_txt));
				} else {
					usernameClear.setVisibility(View.GONE);
					regsterbtn.setClickable(false);
					regsterbtn.setBackgroundColor(getResources().getColor(R.color.regist_notxt));
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_STARTING:
					String chageCity = data.getStringExtra("chageCity");
					phoneCode = data.getStringExtra("code");
					mPhoneCode.setText("+"+phoneCode);
					mCountryName.setText(chageCity);
					break;

			}
		}
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
							edit_codey.setText(message);
							RegsterActivity.this
									.unregisterReceiver(mSMSBroadcastReceiver);
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
						}
					}
				});
	}

	// 验证码

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			sendMessagebtn.setText("获取验证码");
			sendMessagebtn.setClickable(true);
//			sendMessagebtn.setTextColor(getResources().getColor(
//					R.color.login_text_color));

		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			sendMessagebtn.setClickable(false);
			sendMessagebtn.setText(millisUntilFinished / 1000 + "秒");
			sendMessagebtn.setTextColor(getResources().getColor(R.color.black));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {

		// TODO Auto-generated method stub
		user_phone = edit_phone.getText().toString().trim();
		user_coedy = edit_codey.getText().toString().trim();
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.regster_btn:
			// 跳转到下一个界面
			if (checkLoginData()) {
				if (toPage.equals("wxLogin")) {
					if (!TextUtils.isEmpty(userPassword)) {
						bundleMoble();

					} else {
						intent.putExtra("username", user_phone);
						intent.putExtra("usersms", user_coedy);
						intent.putExtra("toPage", toPage);
						intent.putExtra("jumpPage", jumpPage);
						intent.putExtra("code",phoneCode);
						intent.setClass(this, ForgetActivity.class);
						startActivity(intent);
					}
				} else {
					intent.putExtra("username", user_phone);
					intent.putExtra("usersms", user_coedy);
					intent.putExtra("toPage", toPage);
					intent.putExtra("code",phoneCode);
					if(null!=jumpPage)
					intent.putExtra("jumpPage", jumpPage);
					intent.setClass(this, ForgetActivity.class);
					startActivity(intent);
				}
			}

			break;
		case R.id.regist_agreement_text:
			// 协议
			intent.putExtra("agreement", "regist");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;

		case R.id.regist_back_lin:
			finish();
			break;
		case R.id.regist_username_clear_image:
			edit_phone.setText("");
			break;

		default:
			break;
		}
	}

	private void sendSMS() {
		//
		RequestParams params = new RequestParams();
		params.put("userTel", user_phone);
		params.put("mobileCountryCode",phoneCode);
		params.put("uuid", MyApplication.getInstance().getUuid());
		if (!TextUtils.isEmpty(toPage)) {
			if (toPage.equals("regist")) {
				params.put("type", "0");
			} else if (toPage.equals("forgetPwd")) {
				params.put("type", "1");
			} else if (toPage.equals("wxLogin")) {
				params.put("type", "2");
			}
		}

		APPRestClient.post(RegsterActivity.this, ServiceCode.UUCODEY_INTERFACE,
				params, new APPResponseHandler<MessageModeal>(
						MessageModeal.class, this) {
					@Override
					public void onSuccess(MessageModeal result) {
						// TODO Auto-generated method stub
						CustomToast.makeText(RegsterActivity.this, 0, "发送短信成功",
								200).show();
						if (toPage.equals("wxLogin")) {
							if (!TextUtils.isEmpty(result.getOBJECT()
									.getUserPassword())) {
								userPassword = result.getOBJECT()
										.getUserPassword();
							}

						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendSMS();
						} else {
						// TODO Auto-generated method stub
						CustomToast.makeText(RegsterActivity.this, 0, errorMsg,
								300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(RegsterActivity.this)
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

	// 检测输入数据准确性
	private boolean checkLoginData() {
		if (user_phone.equals("")) {
			CustomToast.makeText(this, 0, "手机号为空", 200).show();
			return false;
		}
		if (user_coedy.length() < 6) {
			CustomToast.makeText(this, 0, "验证码为6位", 200).show();
			return false;
		}

		if (user_coedy.equals("")) {
			CustomToast.makeText(this, 0, "验证码不能为空", 200).show();
			return false;
		} else {
			return true;
		}
	}

	// 检测输入数据准确性
	private boolean checkAutoLoginData() {
		user_phone = edit_phone.getText().toString().trim();
		user_coedy = edit_codey.getText().toString().trim();
		if (user_phone.equals("")) {
			return false;
		}
		if (user_coedy.length() < 6) {
			return false;
		}

		if (user_coedy.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 2:
				if (checkAutoLoginData()) {
					if (toPage.equals("wxLogin")) {
						if (!TextUtils.isEmpty(userPassword)) {
							bundleMoble();
						} else {
							intent.putExtra("username", user_phone);
							intent.putExtra("usersms", user_coedy);
							intent.putExtra("toPage",  toPage);
							intent.putExtra("jumpPage", jumpPage);
							intent.setClass(RegsterActivity.this,
									ForgetActivity.class);
							startActivity(intent);
						}

					} else {
						intent.putExtra("username", user_phone);
						intent.putExtra("usersms", user_coedy);
						intent.putExtra("toPage", toPage);
						if(null!=jumpPage)
							intent.putExtra("jumpPage", jumpPage);
						intent.setClass(RegsterActivity.this,
								ForgetActivity.class);
						startActivity(intent);
					}
				}
				break;
			case 3:
				//关闭login界面
				ActivityCollector.removeSpecifiedActivity("com.uugty.uu.login.LoginActivity");
				final UUlogin objectRcvd = (UUlogin) msg.getData().getSerializable(
						"LoginData");
				SharedPreferenceUtil.getInstance(ctx).setString("userName",
						objectRcvd.getOBJECT().getUserTel());
				SharedPreferenceUtil.getInstance(ctx).setString("userPwd",
						objectRcvd.getOBJECT().getUserPassword());
				MyApplication.getInstance().setUserInfo(objectRcvd);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					sendCustomerServiceMsg(objectRcvd.getOBJECT().getUserId());
				}
				else {
					EMChatManager.getInstance().login(
							objectRcvd.getOBJECT().getUserId(),
							objectRcvd.getOBJECT().getUserEasemobPassword(),
							new EMCallBack() {
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									EMGroupManager.getInstance()
											.loadAllGroups();
									EMChatManager.getInstance()
											.loadAllConversations();
									sendCustomerServiceMsg(objectRcvd
											.getOBJECT().getUserId());
								}

								@Override
								public void onProgress(int arg0, String arg1) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onError(int arg0, final String arg1) {
									// TODO Auto-generated method stub
									runOnUiThread(new Runnable() {
										public void run() {
											CustomToast
													.makeText(
															RegsterActivity.this,
															getString(R.string.hx_login_failed)
																	+ arg1,
															Toast.LENGTH_SHORT)
													.show();
											MyApplication.getInstance().setLogin(false);
										}
									});
								}
							});
				}
				if (!TextUtils.isEmpty(jumpPage)) {
					Class c = null;
					try {
						c = Class.forName(jumpPage);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(RegsterActivity.this, c);

				} else {
					intent.setClass(RegsterActivity.this, MainActivity.class);
				}
				startActivity(intent);
				finish();
				break;
			}
		};
	};

	// 登录按钮
	public void UU_Login(final String userTel,final String userPassword) {
		// 显示等待层
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.add("userTel", userTel); // 手机�?
		params.add("userPassword", userPassword); // 密码
		params.add("userLastLoginLng", "39.938897"); // 经度
		params.add("userLastLoginLat", "116.464053"); // 纬度

		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid

		APPRestClient.post(ctx, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.UULOGIN_INTERFACE, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, this) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 3;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							UU_Login(userTel, userPassword);
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ctx)
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

	private void bundleMoble() {
		RequestParams params = new RequestParams();
		params.put("type", "6");
		if (!TextUtils.isEmpty(toPage) && toPage.equals("wxLogin")) {
			params.put("content", user_phone + "," + userPassword + ","
					+ user_coedy);
		}

		APPRestClient.post(this, ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						UU_Login(user_phone, userPassword);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							bundleMoble();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 200).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(RegsterActivity.this)
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
					}
					}
					@Override
					public void onFinish() {
					}
				});
	}
	
	// 给客服发消息
		private void sendCustomerServiceMsg(String userId) {
			// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(userId);
			// 创建一条文本消息
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			// 设置消息body
			TextMessageBody txtBody = new TextMessageBody(
					"the_client_visit_customer");
			message.addBody(txtBody);
			// 设置接收人
			message.setReceipt("client_customer");
			// 把消息加入到此会话对象中
			conversation.addMessage(message);
			// 发送消息
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					EMConversation clientConversation = EMChatManager.getInstance()
							.getConversation("client_customer");
					clientConversation.removeMessage(message.getMsgId());

				}
			});
		}
}
