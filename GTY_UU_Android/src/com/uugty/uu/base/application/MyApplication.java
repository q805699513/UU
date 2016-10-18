package com.uugty.uu.base.application;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.analytics.MobclickAgent;
import com.uugty.uu.R;
import com.uugty.uu.com.helper.DemoHXSDKHelper;
import com.uugty.uu.common.Exception.CrashHandler;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.img.ImageLoaderConfig;
import com.uugty.uu.modeal.UUlogin;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends BaseApplication {

	private String TAG = "MyApplication";
	private static MyApplication mApplication;
	private boolean isLogin = false;
	private UUlogin userInfo;// 客户信息
	private String routeId = "";// 线路ID
	private BaseResp baseResp;// 微信登录code
	private boolean isIssueRoute = false;// 是否发布过路线
	private boolean isKilled=true;
	// private NetState mNetworkStateReceiver;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	// 账号在别处登录
	public boolean isConflict = false;
	

	// private HXSDKHelper hxsckhelper;

	public static MyApplication getInstance() {
		return mApplication;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		//解决65535问题
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		mApplication = this;
		setContext(this);
		ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
				.setDownsampleEnabled(true).build();
		Fresco.initialize(this, config);// 图片缓存初始化配置
		setAppName(getResources().getString(R.string.app_name));
		// 加载sqlite加密so库
		// SQLiteDatabase.loadLibs(this);
		// 初始化图片加载配置
		CacheFileUtil.initCreateFiles();
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfig.initImageLoader(this, CacheFileUtil.rootPath);
		}

		JPushInterface.setDebugMode(true);
		JPushInterface.init(this.getApplicationContext());
		JPushInterface.getRegistrationID(this.getApplicationContext());
		// 崩溃异常监听

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getContext());

		// 启动网络监听广播
		/*
		 * if (mNetworkStateReceiver == null) { mNetworkStateReceiver = new
		 * NetState(); // 注册网络监听 IntentFilter filter = new IntentFilter();
		 * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		 * registerReceiver(mNetworkStateReceiver, filter); }
		 */

		hxSDKHelper.onInit(this.getApplicationContext());
		// 默认session超时为1小时
		MobclickAgent.setSessionContinueMillis(3600000);

	}


	/**
	 * add by mcoy for bugID=427
	 * 
	 * @return 底部状态栏的高度
	 */
	@SuppressWarnings("unused")
	public int getNavigationBarHeight(Context context) {
		if (!hasSoftKeys((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE))) {
			return 0;
		}
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		int height = resources.getDimensionPixelSize(resourceId);
		Log.e("mcoy", "the height is " + height);
		return height;
	}

	@SuppressLint("NewApi")
	public boolean hasSoftKeys(WindowManager windowManager) {
		Display d = windowManager.getDefaultDisplay();

		DisplayMetrics realDisplayMetrics = new DisplayMetrics();
		d.getRealMetrics(realDisplayMetrics);

		int realHeight = realDisplayMetrics.heightPixels;
		int realWidth = realDisplayMetrics.widthPixels;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		d.getMetrics(displayMetrics);

		int displayHeight = displayMetrics.heightPixels;
		int displayWidth = displayMetrics.widthPixels;

		return (realWidth - displayWidth) > 0
				|| (realHeight - displayHeight) > 0;
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		// TODO Auto-generated method stub
		if(level ==TRIM_MEMORY_COMPLETE&&isKilled){
			ActivityCollector.finishAll();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
		
	}
	/**
	 * ַ 获取手机ip地址
	 * 
	 * @return
	 */
	/*
	 * public static String getPhoneIp() { try { for
	 * (Enumeration<NetworkInterface> en = NetworkInterface
	 * .getNetworkInterfaces(); en.hasMoreElements();) { NetworkInterface intf =
	 * en.nextElement(); for (Enumeration<InetAddress> enumIpAddr = intf
	 * .getInetAddresses(); enumIpAddr.hasMoreElements();) { InetAddress
	 * inetAddress = enumIpAddr.nextElement(); if
	 * (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
	 * { // if (!inetAddress.isLoopbackAddress() && inetAddress // instanceof
	 * Inet6Address) { return inetAddress.getHostAddress().toString(); } } } }
	 * catch (Exception e) { } return ""; }
	 */

	public void clearLoginData() {
		this.isLogin = false;
		this.userInfo = null;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public UUlogin getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UUlogin userInfo) {
		this.userInfo = userInfo;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public BaseResp getBaseResp() {
		return baseResp;
	}

	public void setBaseResp(BaseResp baseResp) {
		this.baseResp = baseResp;
	}

	public boolean isIssueRoute() {
		return isIssueRoute;
	}

	public void setIssueRoute(boolean isIssueRoute) {
		this.isIssueRoute = isIssueRoute;
	}

	public boolean isKilled() {
		return isKilled;
	}

	public void setKilled(boolean isKilled) {
		this.isKilled = isKilled;
	}

	public boolean isConflict() {
		return isConflict;
	}

	public void setConflict(boolean isConflict) {
		this.isConflict = isConflict;
	}

	
}
