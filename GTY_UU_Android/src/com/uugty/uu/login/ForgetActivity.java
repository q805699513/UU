package com.uugty.uu.login;

import android.text.TextUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.modeal.MessageModeal;
import com.uugty.uu.modeal.UUlogin;
import com.uugty.uu.util.Md5Util;

public class ForgetActivity extends BaseActivity implements OnClickListener {

	private final String TAG = "ForgetActivity";
	// 用户名 短信验证码 用户密码
	private String user_phone, user_coedy, user_pwd1, user_pwd2, toPage,
			jumpPage;

	// 注册按钮
	private Button regsterbtn;
	// 关闭按钮
	private ImageView pwdOneClearImage, pwdTwoClearImage;
	// loading等待
	private SpotsDialog loadingDialog;
	// 获取用户输入信息
	private EditText edit_pwd1, edit_pwd2;
	// 经纬度
	private Double geoLat, geoLng;
	private LinearLayout closeLin;
	// 提示
	private TextView remindTextView;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uu_forget;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			user_phone = getIntent().getStringExtra("username");
			user_coedy = getIntent().getStringExtra("usersms");
			toPage = getIntent().getStringExtra("toPage");
			jumpPage = getIntent().getStringExtra("jumpPage");
		}
		closeLin = (LinearLayout) findViewById(R.id.forget_pwd_back_lin);
		edit_pwd1 = (EditText) findViewById(R.id.forget_pwd_one);
		edit_pwd2 = (EditText) findViewById(R.id.forget_pwd_two);
		regsterbtn = (Button) findViewById(R.id.forget_pwd_btn);
		pwdOneClearImage = (ImageView) findViewById(R.id.forget_pwd_one_clear_image);
		pwdTwoClearImage = (ImageView) findViewById(R.id.forget_pwd_two_clear_image);
		remindTextView = (TextView) findViewById(R.id.wx_login_remind_text);

		if (!TextUtils.isEmpty(toPage) && toPage.equals("regist")) {
			regsterbtn.setText("登录");
			remindTextView.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(toPage) && toPage.equals("forgetPwd")) {
			regsterbtn.setText("确认");
			remindTextView.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(toPage) && toPage.equals("wxLogin")) {
			regsterbtn.setText("绑定手机");
			remindTextView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		regsterbtn.setOnClickListener(this);
		closeLin.setOnClickListener(this);
		pwdOneClearImage.setOnClickListener(this);
		pwdTwoClearImage.setOnClickListener(this);
		edit_pwd1.addTextChangedListener(new TextWatcher() {

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
					pwdOneClearImage.setVisibility(View.VISIBLE);
				} else {
					pwdOneClearImage.setVisibility(View.GONE);
				}

			}
		});

		edit_pwd2.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s.toString())) {
					pwdTwoClearImage.setVisibility(View.VISIBLE);
				} else {
					pwdTwoClearImage.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {

		// TODO Auto-generated method stub
		user_pwd1 = edit_pwd1.getText().toString().trim();
		user_pwd2 = edit_pwd2.getText().toString().trim();

		switch (v.getId()) {

		case R.id.forget_pwd_btn:
			// 注册
			if (checkLoginData()) {
				if (!TextUtils.isEmpty(toPage) && toPage.equals("regist")) {
					regist();
				}
				if (!TextUtils.isEmpty(toPage) && toPage.equals("forgetPwd")) {
					forgetPwd();
				}
				if (!TextUtils.isEmpty(toPage) && toPage.equals("wxLogin")) {
					bundleMoble();
				}

			}
			break;
		case R.id.forget_pwd_back_lin:
			finish();
			break;
		case R.id.forget_pwd_one_clear_image:
			edit_pwd1.setText("");
			break;
		case R.id.forget_pwd_two_clear_image:
			edit_pwd2.setText("");
			break;
		default:
			break;
		}

	}

	private void forgetPwd() {
		// 显示等待层
		RequestParams params = new RequestParams();
		params.add("userTel", user_phone); // 应用编号
		params.add("userPassword", Md5Util.MD5(user_pwd2)); // 应用编号
		params.add("veryCode", user_coedy);
		// 手机号是否被注册
		APPRestClient
				.post(this, ServiceCode.USER_FORGET_PWD, params,
						new APPResponseHandler<MessageModeal>(
								MessageModeal.class, this) {
							@Override
							public void onSuccess(MessageModeal result) {
								CustomToast.makeText(ForgetActivity.this, 0,
										"密码设置成功", 200).show();
								UU_Login();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									forgetPwd();
								} else {
									if (errorCode == -999) {
										new AlertDialog.Builder(
												ForgetActivity.this)
												.setTitle("提示")
												.setMessage("服务器连接失败！")
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
									} else {
										CustomToast.makeText(
												ForgetActivity.this, 0,
												errorMsg, 200).show();
									}
								}
							}

							@Override
							public void onFinish() {
							}
						});

	}

	// 检测输入数据准确性
	private boolean checkLoginData() {

		if (user_pwd1.equals("") || user_pwd2.equals("")) {
			CustomToast.makeText(this, 0, "密码不能为空", 200).show();
			return false;
		}
		if (user_pwd1.length() < 6) {
			CustomToast.makeText(this, 0, "密码最少6位", 200).show();
			return false;
		}
		if (user_pwd1.length() > 16) {
			CustomToast.makeText(this, 0, "密码不能超过16位", 200).show();
			return false;
		}
		if (!user_pwd1.equals(user_pwd2)) {
			CustomToast.makeText(this, 0, "密码不一致", 200).show();
			return false;
		} else {
			return true;
		}

	}

	private void regist() {
		if (loadingDialog != null) {
			loadingDialog.show();
		} else {
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}
		RequestParams params = new RequestParams();
		params.add("userTel", user_phone);
		params.add("userPassword", Md5Util.MD5(user_pwd2));
		params.add("veryCode", user_coedy);
		if (null != geoLng && !geoLng.equals("")) {
			params.add("userLastLoginLng", geoLng.toString());
			params.add("userLastLoginLat", geoLat.toString());
		} else {
			params.add("userLastLoginLng", "39.938897");
			params.add("userLastLoginLat", "116.464053");
		}
		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid
		params.add("userChannel", getAppMetaData(this));

		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.UUREGSTER_INTERFACE, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, this) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							regist();
						} else {
							CustomToast.makeText(ForgetActivity.this, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ForgetActivity.this)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
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
						if (loadingDialog != null)
							loadingDialog.dismiss();
					}
				});

	}

	private void bundleMoble() {
		// 显示等待层
		if (loadingDialog != null) {
			loadingDialog.show();
		} else {
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}
		RequestParams params = new RequestParams();
		params.put("type", "6");
		if (!TextUtils.isEmpty(toPage) && toPage.equals("wxLogin")) {
			params.put("content", user_phone + "," + Md5Util.MD5(user_pwd2)
					+ "," + user_coedy);
		}

		APPRestClient.post(this, ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						// 成功，关闭login,regster
						bundleChannel();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							bundleMoble();
						} else {
							if (loadingDialog != null)
								loadingDialog.dismiss();
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ForgetActivity.this)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
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
						if (loadingDialog != null)
							loadingDialog.dismiss();
						finish();
					}
				});
	}

	private void bundleChannel() {
		// 显示等待层
		RequestParams params = new RequestParams();
		params.put("type", "16");
		params.put("content", getAppMetaData(this));

		APPRestClient.post(this, ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						// 成功，关闭login,regster
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.login.LoginActivity");
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.login.RegsterActivity");
						refreshPersonData();

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							bundleChannel();
						} else {
							if (loadingDialog != null)
								loadingDialog.dismiss();
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ForgetActivity.this)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
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

						finish();
					}
				});
	}

	// 登录按钮
	public void UU_Login() {
		// 显示等待层
		if (loadingDialog != null) {
			loadingDialog.show();
		} else {
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}
		RequestParams params = new RequestParams();
		params.add("userTel", user_phone); // 手机�?
		params.add("userPassword", Md5Util.MD5(user_pwd2)); // 密码

		if (null != geoLng && !geoLng.equals("")) {
			params.add("userLastLoginLng", geoLng.toString()); // 经度
			params.add("userLastLoginLat", geoLat.toString()); // 纬度
		} else {
			params.add("userLastLoginLng", "39.938897"); // 经度
			params.add("userLastLoginLat", "116.464053"); // 纬度
		}
		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid

		APPRestClient.post(ctx, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.UULOGIN_INTERFACE, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, this) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							UU_Login();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ctx)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
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
						if (loadingDialog != null)
							loadingDialog.dismiss();
					}
				});

	}

	private void refreshPersonData() {

		RequestParams params = new RequestParams();

		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.FEFRESH_PERSON_DATA, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, this) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 2;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							refreshPersonData();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ctx)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
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
						if (loadingDialog != null)
							loadingDialog.dismiss();
					}
				});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 1:
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.login.RegsterActivity");
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.login.LoginActivity");
				final UUlogin objectRcvd = (UUlogin) msg.getData()
						.getSerializable("LoginData");
				SharedPreferenceUtil.getInstance(ctx).setString("userName",
						objectRcvd.getOBJECT().getUserTel());
				SharedPreferenceUtil.getInstance(ctx).setString("userPwd",
						objectRcvd.getOBJECT().getUserPassword());
				MyApplication.getInstance().setUserInfo(objectRcvd);
				MyApplication.getInstance().setLogin(true);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMChatManager.getInstance().logout();// 此方法为同步方法

				}
				EMChatManager.getInstance().login(
						objectRcvd.getOBJECT().getUserId(),
						objectRcvd.getOBJECT().getUserEasemobPassword(),
						new EMCallBack() {
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								EMGroupManager.getInstance().loadAllGroups();
								EMChatManager.getInstance()
										.loadAllConversations();
								sendCustomerServiceMsg(objectRcvd.getOBJECT()
										.getUserId());
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
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.hx_login_failed)
														+ arg1,
												Toast.LENGTH_SHORT).show();
										MyApplication.getInstance().setLogin(
												false);
									}
								});
							}
						});
				if (!TextUtils.isEmpty(jumpPage)) {
					Class c = null;
					try {
						c = Class.forName(jumpPage);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(ForgetActivity.this, c);
					startActivity(intent);
					finish();
				} else {
					intent.setClass(ForgetActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}

				break;

			case 2:
				final UUlogin refreshObjectRcvd = (UUlogin) msg.getData()
						.getSerializable("LoginData");
				MyApplication.getInstance().setUserInfo(refreshObjectRcvd);
				SharedPreferenceUtil.getInstance(ctx).setString("userName",
						refreshObjectRcvd.getOBJECT().getUserTel());
				SharedPreferenceUtil.getInstance(ctx).setString("userPwd",
						refreshObjectRcvd.getOBJECT().getUserPassword());
				MyApplication.getInstance().setLogin(true);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMChatManager.getInstance().logout();// 此方法为同步方法

				}
				EMChatManager.getInstance().login(
						refreshObjectRcvd.getOBJECT().getUserId(),
						refreshObjectRcvd.getOBJECT().getUserEasemobPassword(),
						new EMCallBack() {
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								EMGroupManager.getInstance().loadAllGroups();
								EMChatManager.getInstance()
										.loadAllConversations();
								sendCustomerServiceMsg(refreshObjectRcvd
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
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.hx_login_failed)
														+ arg1,
												Toast.LENGTH_SHORT).show();
										MyApplication.getInstance().setLogin(false);
									}
								});
							}
						});

				if (!TextUtils.isEmpty(jumpPage)) {
					Class c = null;
					try {
						c = Class.forName(jumpPage);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(ForgetActivity.this, c);
					startActivity(intent);
					finish();

				} else {
					intent.setClass(ForgetActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			}
		};
	};

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
