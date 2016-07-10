package com.uugty.uu.appstart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.helper.DemoHXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.AddJpushId;
import com.uugty.uu.entity.AppStartImageEntity;
import com.uugty.uu.entity.AppVersionCheckVo;
import com.uugty.uu.entity.TestEntity;
import com.uugty.uu.guide.ImageGuideActivity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.modeal.UUlogin;
import com.uugty.uu.util.LogUtils;

import cn.jpush.android.api.JPushInterface;


public class AppStartActivity extends BaseActivity implements
		AMapLocationListener {
	private final String TAG = "AppStartActivity";
	private AppVersionCheckVo versionCheckVo;
	// 定位
	private LocationManagerProxy mLocationManagerProxy;
	// 经纬度
	private Double geoLat, geoLng;
	private int count = 0;
	private String appVersion;
	private ImageView startImageView;
	private FrameLayout skipFramLayout;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.corners_linear_bottom)
			.showImageForEmptyUri(R.drawable.corners_linear_bottom)
			.showImageOnLoading(R.drawable.corners_linear_bottom)
			.cacheInMemory(true).cacheOnDisk(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			final Intent intent = new Intent();
			switch (msg.what) {

			case 2:
				// 检查版本
				apkVersionCheck();
				break;
			case 3:
				apkUpdate();// apk更新提示
				break;
			case 5:
				// 检查版本
				AppVersionCheckVo vo = (AppVersionCheckVo) msg.getData()
						.getSerializable("appVersionCheckVo");
				versionCheckVo = vo;
				break;
			case 4:
				UUlogin objectRcvd = (UUlogin) msg.getData().getSerializable(
						"LoginData");
				MyApplication.getInstance().setUserInfo(objectRcvd);
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					intent.setClass(AppStartActivity.this, MainActivity.class);
					handler.postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							MyApplication.getInstance().setLogin(true);
							startActivity(intent);
						}
					}, 300);

				} else {
					intent.setClass(AppStartActivity.this, MainActivity.class);
					handler.postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							startActivity(intent);
						}
					}, 300);
				}

				break;
			case 6:
				intent.setClass(AppStartActivity.this, MainActivity.class);
				handler.postDelayed(new Runnable() {
					public void run() {
						// 显示dialog
						startActivity(intent);
					}
				}, 300);
				break;
			}
			super.handleMessage(msg);
		};
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private Handler jumpHandler = new Handler();
	private Runnable jumpRunnable = new Runnable() {
		
		@Override
		public void run() {
			// 判断程序与第几次运行，如果是第一次运行则跳转到引导页面
			StartGuide();
		}
	};

	@Override
	protected int getContentLayout() {
		return R.layout.app_start;
	}

	@Override
	protected void initGui() {
		startImageView = (ImageView) findViewById(R.id.app_start_image);
		skipFramLayout = (FrameLayout) findViewById(R.id.app_start_skip_fram);
		count = SharedPreferenceUtil.getInstance(ctx).getInt("count", 0);
		appVersion = SharedPreferenceUtil.getInstance(ctx).getString(
				"appversion", MyApplication.getInstance().getApp_version());
	}

	@Override
	protected void initAction() {
//		MobclickAgent.updateOnlineConfig(this);
		String id = SharedPreferenceUtil.getInstance(ctx).getString("JPushRegistId","");
		if(!id.equals(JPushInterface.getRegistrationID(ctx))) {
			pushJpushId();
		}
		getBackgroudImageRequest();
		sendRequest();
	}

	@Override
	public void onActivityResult(int requestCode,int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 1:
					getCheck();
					break;
				default:
			}
		}
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
						SharedPreferenceUtil.getInstance(ctx).setString("JPushRegistId",JPushInterface.getRegistrationID(ctx));
						LogUtils.printLog("JPsuhRegistId",JPushInterface.getRegistrationID(ctx));
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
	protected void initData() {

	}

	private void getBackgroudImageRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.QUERY_BOOT_BACKGROUND, params,
				new APPResponseHandler<AppStartImageEntity>(
						AppStartImageEntity.class, this) {
					@Override
					public void onSuccess(AppStartImageEntity result) {
						if(null!=result.getOBJECT()){
							ImageLoader.getInstance().displayImage(
									APPRestClient.SERVER_IP
											+ result.getOBJECT().getBootImage(),
									startImageView,options);
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

	private void sendRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.USER_LOGIN_OUT, params,
				new APPResponseHandler<TestEntity>(TestEntity.class, this) {
					@Override
					public void onSuccess(TestEntity result) {
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}
					@Override
					public void onFailure(int errorCode, String errorMsg) {
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
													finish();
													ActivityCollector
															.finishAll();

												}
											}).show();
						}
					}

					@Override
					public void onFinish() {
					}
				});

	}

	private void apkVersionCheck() {
		try {
			RequestParams params = new RequestParams();
			params.add("clientVersion", MyApplication.getInstance()
					.getApp_version()); // 版本号
			params.add("osType", "android");
			params.add("uuid", MyApplication.getInstance().getUuid()); // uuid
			params.add("channel", getAppMetaData(this));
			APPRestClient.post(ctx, ServiceCode.VERSION_CHECK, params,
					new APPResponseHandler<AppVersionCheckVo>(
							AppVersionCheckVo.class, this) {
						@Override
						public void onSuccess(AppVersionCheckVo result) {
							Message msg = Message.obtain();
							msg.what = 5;
							Bundle b = new Bundle();
							b.putSerializable("appVersionCheckVo", result);
							msg.setData(b);
							handler.sendMessage(msg);
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							LogUtils.printLog(TAG, errorMsg);
							if (errorCode == 1
									&& errorMsg.equals("客户端传递的版本号没有对应的策略！")) {
								getCheck();
								return;
							}
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
														finish();
														exit();
													}
												}).show();
							}
						}

						@Override
						public void onFinish() {
							Log.d(TAG, "appVersionCheck onFinish:");
							Message msg = new Message();
							msg.what = 3;
							handler.sendMessage(msg);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.printLog(TAG, "---------版本检测失败---------");
		}

	}

	/**
	 * apk更新
	 */
	public void apkUpdate() {
		LogUtils.printLog(TAG, "---------apk更新提示........");

		if (versionCheckVo != null) {
			// apk更新逻辑
			if ("0".equals(versionCheckVo.getOBJECT().getSTRATERY())) {
				LogUtils.printLog(TAG, "---------已是最新版本........");
				// 没有更新, 获取数据字典和区域数据
				getCheck();
			} else if ("1".equals(versionCheckVo.getOBJECT().getSTRATERY())) {// 可更新
				LogUtils.printLog(TAG, "---------有可更新版本........");
//				Intent i = new Intent();
//				i.putExtra("url",versionCheckVo.getOBJECT().getREDIRECTLOCATION());
//				i.setClass(AppStartActivity
//				.this,UpgradeDialogActivity.class);
//				startActivityForResult(i,1);
				new AlertDialog.Builder(ctx)
						.setTitle("提示")
						.setMessage(
								"当前版本为:"
										+ MyApplication.getInstance()
												.getApp_version()
										+ ",最新版本为:"
										+ versionCheckVo.getOBJECT()
												.getCURRVERSION() + ",是否需要更新?")
						.setCancelable(false)
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 打开浏览器下载
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								Uri content_url = Uri.parse(versionCheckVo
										.getOBJECT().getREDIRECTLOCATION());
								intent.setData(content_url);
								startActivity(intent);
								exit();

							}
						}).setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								getCheck();
							}
						}).show();
			} else if ("2".equals(versionCheckVo.getOBJECT().getSTRATERY())) {// 强制升级
				LogUtils.printLog(TAG, "---------客户端版本太老，强制升级........");
				new AlertDialog.Builder(ctx).setTitle("提示")
						.setMessage("当前版本过旧,需升级后使用!").setCancelable(false)
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 打开浏览器下载
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
								Uri content_url = Uri.parse(versionCheckVo
										.getOBJECT().getREDIRECTLOCATION());
								intent.setData(content_url);
								startActivity(intent);
								exit();
							}
						}).show();
			}
		}
	}

	public void getCheck() {

		//跳过
		skipFramLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				skipFramLayout.setEnabled(false);
				jumpHandler.removeCallbacks(jumpRunnable);
				StartGuide();
			}
		});

		jumpHandler.postDelayed(jumpRunnable, 2000);
	}
	
	/**
	 * 判断程序与第几次运行，如果是第一次运行则跳转到引导页面
	 */
	private void StartGuide() {
		Intent intent = new Intent();
		if (count == 0) {
			intent.setClass(AppStartActivity.this,
					ImageGuideActivity.class);
			startActivity(intent);
			SharedPreferenceUtil.getInstance(ctx).setInt("count", 1);
			SharedPreferenceUtil.getInstance(ctx).setString(
					"appversion",
					MyApplication.getInstance().getApp_version());
			SharedPreferenceUtil.getInstance(ctx).setInt("myservices",
					0);
		} else if (!appVersion.equals(MyApplication.getInstance()
				.getApp_version())) {
			intent.setClass(AppStartActivity.this,
					ImageGuideActivity.class);
			startActivity(intent);
			SharedPreferenceUtil.getInstance(ctx).setInt("count", 1);
			SharedPreferenceUtil.getInstance(ctx).setString(
					"appversion",
					MyApplication.getInstance().getApp_version());
			SharedPreferenceUtil.getInstance(ctx).setInt("myservices",
					0);
		} else {// 判断用户名、密码
			String userName = SharedPreferenceUtil.getInstance(ctx)
					.getString("userName", "");
			String userPwd = SharedPreferenceUtil.getInstance(ctx)
					.getString("userPwd", "");
			if (!userName.equals("") && !userPwd.equals("")) {
				// 调用登录，跳入主页
				UU_Login(userName, userPwd);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(AppStartActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		}
	}

	// 登录按钮
	public void UU_Login(final String userName, final String userPwd) {
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
		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid

		APPRestClient.post(ctx, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.UULOGIN_INTERFACE, params, true,
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
													ActivityCollector
															.finishAll();
													dialog.dismiss();
												}
											}).show();
						} else {
							Message msg = new Message();
							msg.what = 6;
							handler.sendMessage(msg);

						}
					}

					@Override
					public void onFinish() {
					}
				});

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

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

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

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();
	}
}
