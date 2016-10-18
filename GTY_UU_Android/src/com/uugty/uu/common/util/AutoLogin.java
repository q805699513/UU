package com.uugty.uu.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.uugty.uu.appstart.Constant;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.helper.DemoHXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.modeal.UUlogin;

public class AutoLogin {

	private static AutoLogin instance;
	private Context context;
	private String userName;
	private String userPwd;
	// 经纬度，伪定位
	private Double geoLat;
	private Double geoLng;
	private final static String TAG = "AutoLogin";

	public static synchronized AutoLogin getInstance(Context context) {
		if (instance == null) {
			instance = new AutoLogin(context);
		}
		return instance;
	}

	// 构造函数
	private AutoLogin(Context context) {
		this.context = context;
	}

	// 判断是否可以自动登录
	public boolean autoLoginAlbe() {
		userName = SharedPreferenceUtil.getInstance(context).getString(
				"userName", "");
		userPwd = SharedPreferenceUtil.getInstance(context).getString(
				"userPwd", "");
		if (!userName.equals("") && !userPwd.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public void login() {

		// 显示等待层

		RequestParams params = new RequestParams();
		params.add("userTel", userName); // 手机�?
		params.add("userPassword", userPwd); // 密码

		if (null != geoLng && !geoLng.equals("")) {
			params.add("userLastLoginLng", geoLng.toString()); // 经度
			params.add("userLastLoginLat", geoLat.toString()); // 纬度
		} else {
			params.add("userLastLoginLng", "39.938897"); // 经度
			params.add("userLastLoginLat", "116.464053"); // 纬度
		}
		params.add("uuid", Constant.UUID); // uuid

		APPRestClient.post(context, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.UULOGIN_INTERFACE, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, context) {
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
						CustomToast.makeText(context, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ActivityCollector
															.finishAll();
													dialog.dismiss();
												}
											}).show();
						} else {
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);

						}
					}

					@Override
					public void onFinish() {
					}
				});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 1:
				UUlogin objectRcvd = (UUlogin) msg.getData().getSerializable(
						"LoginData");
				MyApplication.getInstance().setLogin(true);
				MyApplication.getInstance().setUserInfo(objectRcvd);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
				} else {
					intent.setClass(context, LoginActivity.class);
					context.startActivity(intent);
				}
				break;
			case 2:
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
				break;
			}
			super.handleMessage(msg);
		};
	};
}
