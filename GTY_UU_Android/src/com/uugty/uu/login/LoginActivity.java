package com.uugty.uu.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
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
import com.uugty.uu.common.util.PhoneInfo;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.AddJpushId;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.modeal.UUlogin;
import com.uugty.uu.util.LogUtils;
import com.uugty.uu.util.Md5Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity implements OnClickListener,
		AMapLocationListener {

	private static final String TAG = "LoginActivity";
	// uu平台登录　
	private Button uu_loginbtn, wx_loginbtn;

	// 忘记密码
	private Button uu_forgetbtn, uu_regsterbtn;
	// 用来存用户名跟密码
	private String user_naem = "", user_pwd;
	//
	private EditText edit_name, edit_pwd;
	private TextView skipText;
	private ImageView usernameClear, pwdClear;
	private SpotsDialog loadingDialog;
	// 从何处进入到登录界面
	private String topage;
	// 定位
	private LocationManagerProxy mLocationManagerProxy;
	// 经纬度
	private Double geoLat, geoLng;
	public static IWXAPI WXapi;
	private String weixinCode;
	private static final String APP_ID = "wx7f1866a885330eb2";
	private String mobileNo;
	private boolean change = false;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uu_login;
	}

	@Override
	protected void initGui() {
		Intent topageIntent = getIntent();
		topage = topageIntent.getStringExtra("topage");
		String offline = getIntent().getStringExtra("offline");
		edit_name = (EditText) findViewById(R.id.uu_phone);
		edit_pwd = (EditText) findViewById(R.id.uu_pwd);
		wx_loginbtn = (Button) findViewById(R.id.uu_wechat_btn);
		uu_loginbtn = (Button) findViewById(R.id.uu_login_btn);
		uu_forgetbtn = (Button) findViewById(R.id.uu_forget_btn);
		uu_regsterbtn = (Button) findViewById(R.id.uu_regster_btn);
		skipText = (TextView) findViewById(R.id.login_skip);
		usernameClear = (ImageView) findViewById(R.id.login_username_clear_image);
		pwdClear = (ImageView) findViewById(R.id.login_pwd_clear_image);
		// initLocation();
		mobileNo = new PhoneInfo(this).getNativePhoneNumber();
		if (!TextUtils.isEmpty(mobileNo)) {
			if (mobileNo.contains("+86")) {
				edit_name.setText(mobileNo.substring(3, mobileNo.length()));
			} else if (mobileNo.contains("00000000000")) {
				edit_name.setText("");
			} else {
				edit_name.setText(mobileNo);
			}

		}
		MyApplication.getInstance().setKilled(false);

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		uu_loginbtn.setOnClickListener(this);
		uu_forgetbtn.setOnClickListener(this);
		wx_loginbtn.setOnClickListener(this);
		uu_regsterbtn.setOnClickListener(this);
		skipText.setOnClickListener(this);
		usernameClear.setOnClickListener(this);
		pwdClear.setOnClickListener(this);

		edit_name.addTextChangedListener(new TextWatcher() {

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
				} else {
					usernameClear.setVisibility(View.GONE);
				}

			}
		});

		edit_pwd.addTextChangedListener(new TextWatcher() {

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
					pwdClear.setVisibility(View.VISIBLE);
				} else {
					pwdClear.setVisibility(View.GONE);
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
		Intent intent = new Intent();
		user_naem = edit_name.getText().toString().trim();
		user_pwd = edit_pwd.getText().toString().trim();

		switch (v.getId()) {
		case R.id.uu_login_btn:
			if (checkLoginData()) {
				UU_Login();
			}
			// Toast.makeText(LoginActivity.this, "登录",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.uu_forget_btn:
			intent.putExtra("toPage", "forgetPwd");
			if (null != topage) {
				intent.putExtra("jumpPage", topage);
			}
			intent.setClass(this, RegsterActivity.class);
			startActivity(intent);
			break;
		case R.id.uu_regster_btn:
			intent.putExtra("toPage", "regist");
			if (null != topage) {
				intent.putExtra("jumpPage", topage);
			}
			intent.setClass(this, RegsterActivity.class);
			startActivity(intent);
			break;
		case R.id.login_skip:
			MainActivity.Sb = R.id.main_rel;
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.uu_wechat_btn:
			WXLogin();
			break;
		case R.id.login_username_clear_image:
			edit_name.setText("");
			break;
		case R.id.login_pwd_clear_image:
			edit_pwd.setText("");
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String offline = getIntent().getStringExtra("offline");
			if (null != offline && offline.equals("offline")) {
				exitClient();
			} else if (ActivityCollector.activites.size() == 2) {
				if (MainActivity.Sb == R.id.message_rel) {
					MainActivity.Sb = R.id.main_rel;
					finish();
				} else {
					finish();
				}
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);
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
		params.add("userTel", user_naem); // 手机�?
		params.add("userPassword", Md5Util.MD5(user_pwd)); // 密码

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
						String id = SharedPreferenceUtil.getInstance(ctx).getString("JPushLoginRegistId","");
						if(!id.equals(JPushInterface.getRegistrationID(ctx))) {
							pushJpushId();//上传极光RegistID
						}
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
							edit_pwd.setText("");
						}
					}

					@Override
					public void onFinish() {
						loadingDialog.dismiss();

					}
				});

	}

	private void pushJpushId() {
		RequestParams params = new RequestParams();
		params.add("registrationID", JPushInterface.getRegistrationID(ctx));
		params.add("clientVersion", MyApplication.getInstance()
				.getApp_version()); // 版本号
		params.add("type", "android");
		APPRestClient.post(ctx, ServiceCode.PUSH_ID, params,
				new APPResponseHandler<AddJpushId>(
						AddJpushId.class, this) {
					@Override
					public void onSuccess(AddJpushId result) {
						SharedPreferenceUtil.getInstance(ctx).setString("JPushLoginRegistId",JPushInterface.getRegistrationID(ctx));
						LogUtils.printLog("JPushLoginRegistId",JPushInterface.getRegistrationID(ctx));
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {
					}
				});
	}

	/**
	 * 登录微信
	 */
	private void WXLogin() {
		// 不存在
		WXapi = WXAPIFactory.createWXAPI(this, APP_ID, true);
		if (WXapi.isWXAppInstalled()) {
			if (loadingDialog != null) {
				loadingDialog.show();
			} else {
				loadingDialog = new SpotsDialog(this);
				loadingDialog.show();
			}
			WXapi.registerApp(APP_ID);
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "wechat_sdk_demo";
			WXapi.sendReq(req);
		} else {
			// 提示用户安装微信客户端
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setIcon(R.drawable.exit_remind)
					.setMessage("未安装微信客户端,无法使用该功!")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}

	}

	@Override
	protected void onResume() {
		/*
		 * resp是你保存在全局变量中的
		 */
		super.onResume();
		
		if (loadingDialog != null)
			loadingDialog.dismiss();
		if (null != MyApplication.getInstance().getBaseResp()) {

			if (MyApplication.getInstance().getBaseResp().getType() == ConstantsAPI.COMMAND_SENDAUTH) {
				// code返回
				weixinCode = ((SendAuth.Resp) MyApplication.getInstance()
						.getBaseResp()).code;
				getAccessToken(weixinCode);
			}
		}
		MyApplication.getInstance().isConflict=false;
	}

	/**
	 * 获取access_token
	 */
	public void getAccessToken(final String code) {
		// https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
		// 调用接口
		new Thread() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				@SuppressWarnings("deprecation")
				HttpGet getMethod = new HttpGet(
						"https://api.weixin.qq.com/sns/oauth2/access_token?appid="
								+ APP_ID
								+ "&secret=6e360ffc74d63c28f155ac675ccfd8d4"
								+ "&code=" + code
								+ "&grant_type=authorization_code");
				try {
					HttpResponse response = httpClient.execute(getMethod);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream is = response.getEntity().getContent();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(is));
						String str = "";
						StringBuffer sb = new StringBuffer();
						while ((str = br.readLine()) != null) {
							sb.append(str);
						}
						is.close();
						String josn = sb.toString();
						JSONObject json1 = new JSONObject(josn);
						String access_token = (String) json1
								.get("access_token");
						String refresh_token = (String) json1
								.get("refresh_token");
						String openid = (String) json1.get("openid");
						// 将access_token、refresh_token放入SharedPreference文件
						/*
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "access_token", access_token);
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "refresh_token", refresh_token);
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "open_id", openid);
						 */
						// 刚刚获取的token,不存在token超时问题,直接获取用户信息
						// 调用微信登录接口
						Message msg = new Message();
						msg.what = 1;
						Bundle bundle = new Bundle();
						bundle.putString("token", access_token);
						bundle.putString("openId", openid);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 执行GET方法
			}

		}.start();
	}

	/**
	 * 刷新token
	 */
	public void getRefreshToken() {
		// https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
		// 调用接口
		new Thread() {
			@Override
			public void run() {
				@SuppressWarnings("deprecation")
				HttpClient httpClient = new DefaultHttpClient();
				String request_refresh_token = SharedPreferenceUtil
						.getInstance(ctx).getString("refresh_token", "");
				HttpGet getMethod = new HttpGet(
						"https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="
								+ APP_ID
								+ "&grant_type=refresh_token&refresh_token="
								+ request_refresh_token);
				try {
					HttpResponse response = httpClient.execute(getMethod);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream is = response.getEntity().getContent();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(is));
						String str = "";
						StringBuffer sb = new StringBuffer();
						while ((str = br.readLine()) != null) {
							sb.append(str);
						}
						is.close();
						String josn = sb.toString();
						JSONObject json1 = new JSONObject(josn);
						String errcode = "";
						if (josn.contains("errcode")) {
							errcode = (String) json1.get("errcode");
						}
						if (errcode.equals("42002")) {
							// 重新授权
							SharedPreferenceUtil.getInstance(ctx).setString(
									"access_token", "");
							SharedPreferenceUtil.getInstance(ctx).setString(
									"refresh_token", "");
							WXLogin();
						} else {
							String access_token = (String) json1
									.get("access_token");
							String refresh_token = (String) json1
									.get("refresh_token");
							String openid = (String) json1.get("openid");
							// 将access_token、refresh_token放入SharedPreference文件
							SharedPreferenceUtil.getInstance(ctx).setString(
									"access_token", access_token);
							SharedPreferenceUtil.getInstance(ctx).setString(
									"refresh_token", refresh_token);
							SharedPreferenceUtil.getInstance(ctx).setString(
									"open_id", openid);
							// 调用微信登录接口
							Message msg = new Message();
							msg.what = 1;
							Bundle bundle = new Bundle();
							bundle.putString("token", access_token);
							bundle.putString("openId", openid);
							msg.setData(bundle);
							handler.sendMessage(msg);
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 执行GET方法
			}

		}.start();
	}

	public void getWXUserInfo(final String access_token, final String openid) {
		// 显示等待层
		if (null != loadingDialog && !this.isFinishing()) {
			loadingDialog.show();
		} else {
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}

		RequestParams params = new RequestParams();

		params.add("access_token", access_token); // 手机�?
		params.add("openid", openid); // 密码
		if (null != geoLng && !geoLng.equals("")) {
			params.add("userLastLoginLng", geoLng.toString()); // 经度
			params.add("userLastLoginLat", geoLat.toString()); // 纬度
		} else {
			params.add("userLastLoginLng", "39.938897"); // 经度
			params.add("userLastLoginLat", "116.464053"); // 纬度
		}
		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid

		APPRestClient.post(ctx, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.USER_WECHAR_LOGIN, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, this) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 4;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getWXUserInfo(access_token, openid);
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
							edit_pwd.setText("");
						}
					}

					@Override
					public void onFinish() {
                       if(null != loadingDialog)
                    	   loadingDialog.dismiss();
					}
				});

	}

	// 输入提示
	private boolean checkLoginData() {

		if (user_naem.equals("")) {
			CustomToast.makeText(ctx, 0, "请输入手机号", 200).show();
			return false;
		}
		if (user_pwd.equals("")) {
			CustomToast.makeText(ctx, 0, "密码为空", 200).show();
			return false;
		}

		if (user_pwd.length() < 6) {
			CustomToast.makeText(ctx, 0, "密码不能小于6位", 200).show();
			return false;
		}
		if (user_pwd.length() > 16) {
			CustomToast.makeText(ctx, 0, "密码不能大于16位", 200).show();
			return false;
		}
		if (!user_naem.equals("") && user_naem.length() != 11) {
			CustomToast.makeText(ctx, 0, "手机号输入错误", 200).show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 初始化定?
	 */
	private void initLocation() {

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请?
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间??1，则定位只定一?
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		mLocationManagerProxy.setGpsEnable(false);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/**
	 * 定位成功后回调函?
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			geoLat = amapLocation.getLatitude();
			geoLng = amapLocation.getLongitude();

		}
	}

	private void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destory();
		}
		mLocationManagerProxy = null;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				String acces_token = msg.getData().getString("token");
				String openId = msg.getData().getString("openId");
				getWXUserInfo(acces_token, openId);
				break;
			case 2: // 手机登录
				Intent intent = new Intent();
				final UUlogin mobileObjectRcvd = (UUlogin) msg.getData()
						.getSerializable("LoginData");
				SharedPreferenceUtil.getInstance(ctx).setString("userName",
						mobileObjectRcvd.getOBJECT().getUserTel());
				SharedPreferenceUtil.getInstance(ctx).setString("userPwd",
						mobileObjectRcvd.getOBJECT().getUserPassword());
				MyApplication.getInstance().setLogin(true);
				MyApplication.getInstance().setUserInfo(mobileObjectRcvd);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					sendCustomerServiceMsg(mobileObjectRcvd.getOBJECT()
							.getUserId());
				} else {
					EMChatManager.getInstance().login(
							mobileObjectRcvd.getOBJECT().getUserId(),
							mobileObjectRcvd.getOBJECT()
									.getUserEasemobPassword(),
							new EMCallBack() {
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									EMGroupManager.getInstance()
											.loadAllGroups();
									EMChatManager.getInstance()
											.loadAllConversations();
									sendCustomerServiceMsg(mobileObjectRcvd
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
															LoginActivity.this,
															getString(R.string.hx_login_failed)
																	+ arg1,
															Toast.LENGTH_SHORT)
													.show();
											MyApplication.getInstance()
													.setLogin(false);
										}
									});
								}
							});
				}

				if (null != topage) {
					Class c = null;
					try {
						c = Class.forName(topage);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(LoginActivity.this, c);
					startActivity(intent);
					finish();
				} else {
					intent.setClass(ctx, MainActivity.class);
					startActivity(intent);
					finish();
				}

				break;
			case 4: // 微信登录
				final UUlogin objectRcvd = (UUlogin) msg.getData()
						.getSerializable("LoginData");
				SharedPreferenceUtil.getInstance(ctx).setString("userName",
						objectRcvd.getOBJECT().getUserTel());
				SharedPreferenceUtil.getInstance(ctx).setString("userPwd",
						objectRcvd.getOBJECT().getUserPassword());
				MyApplication.getInstance().setLogin(false);
				Intent intent1 = new Intent();
				if (null == objectRcvd.getOBJECT().getUserTel()
						|| objectRcvd.getOBJECT().getUserTel().equals("")) {
					intent1.putExtra("toPage", "wxLogin");
					intent1.putExtra("jumpPage", topage);
					intent1.setClass(ctx, RegsterActivity.class);
					startActivity(intent1);
				} else {
					if (DemoHXSDKHelper.getInstance().isLogined()) {
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						sendCustomerServiceMsg(objectRcvd.getOBJECT()
								.getUserId());
					} else {
						EMChatManager.getInstance()
								.login(objectRcvd.getOBJECT().getUserId(),
										objectRcvd.getOBJECT()
												.getUserEasemobPassword(),
										new EMCallBack() {
											@Override
											public void onSuccess() {
												// TODO Auto-generated method
												// stub
												EMGroupManager.getInstance()
														.loadAllGroups();
												EMChatManager.getInstance()
														.loadAllConversations();
												sendCustomerServiceMsg(objectRcvd
														.getOBJECT()
														.getUserId());
											}

											@Override
											public void onProgress(int arg0,
													String arg1) {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onError(int arg0,
													final String arg1) {
												// TODO Auto-generated method
												// stub
												runOnUiThread(new Runnable() {
													public void run() {
														CustomToast
																.makeText(
																		LoginActivity.this,
																		getString(R.string.hx_login_failed)
																				+ arg1,
																		Toast.LENGTH_SHORT)
																.show();
														MyApplication
																.getInstance()
																.setLogin(false);
													}
												});
											}
										});
					}
					if (null != topage) {
						Class c = null;
						try {
							c = Class.forName(topage);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						intent1.setClass(LoginActivity.this, c);
					} else {
						intent1.setClass(LoginActivity.this, MainActivity.class);
					}
					MyApplication.getInstance().setUserInfo(objectRcvd);
					MyApplication.getInstance().setLogin(true);
					intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent1);
					finish();
				}
				break;
			case 3:
				getRefreshToken();// apk更新提示
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
