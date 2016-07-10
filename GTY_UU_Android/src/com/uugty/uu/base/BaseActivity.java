package com.uugty.uu.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.helper.HXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.TestEntity;
import com.uugty.uu.modeal.UUlogin;

import java.util.Calendar;

//此去存在问题，应该继承FragmentActivity,这样activity、和使用FragmentActivity可以继承同一个基类
@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity {
	private final static String TAG = "BaseActivity";
	protected Activity ctx;
	protected MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		// 去除状态栏、标题栏

		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 * requestWindowFeature(Window.FEATURE_NO_TITLE);
		 */

		// getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			/*
			 * window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			 * window.getDecorView().setSystemUiVisibility(View.
			 * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
			 * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
			 * View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.setStatusBarColor(Color.GRAY);
//			window.setNavigationBarColor(Color.TRANSPARENT);
		}
		if (null != savedInstanceState)

		{
			/*
			 * UUlogin loginData = (UUlogin) savedInstanceState
			 * .getSerializable("loginData");
			 */
			UUlogin loginData = (UUlogin) savedInstanceState
					.getSerializable("loginData");
			MyApplication.getInstance().setUserInfo(loginData);
		}
		ActivityCollector.addActivity(this);

		ctx = this;
		app = MyApplication.getInstance();
		app.setContext(ctx);
		if (getContentLayout() != 0) {
			setContentView(getContentLayout());
			getWindow().setBackgroundDrawable(null);
		}

		// 设置可以滑动的区域，推荐用屏幕像素的一半来指定
//		 MobclickAgent.setAutoLocation(true);
		 MobclickAgent.setSessionContinueMillis(1000);

		/*
		 * int screenWidth =
		 * getWindowManager().getDefaultDisplay().getWidth();//真实分辨率 宽 int
		 * screenHeight =
		 * getWindowManager().getDefaultDisplay().getHeight();//真实分辨率 高
		 * 
		 * DisplayMetrics dm = new DisplayMetrics(); dm =
		 * getResources().getDisplayMetrics(); int densityDPI = dm.densityDpi;
		 * // 屏幕密度（每寸像素：120(ldpi)/160(mdpi)/213(tvdpi)/240(hdpi)/320(xhdpi)）
		 * Toast.makeText(this,
		 * "真实分辨率："+screenWidth+"*"+screenHeight+"  每英寸:"+densityDPI,
		 * Toast.LENGTH_LONG).show();
		 */
		// 反向控制
		/*
		 * if(null==MyApplication.getInstance().getUserInfo()){ String className
		 * = ctx.getClass().getName();
		 * if(!className.equals("com.uugty.uu.appstart.AppStartActivity"
		 * )&&!className.equals("com.uugty.uu.login.LoginActivity")){ Intent
		 * intent = new Intent(); intent.setClass(ctx, LoginActivity.class);
		 * startActivity(intent); return; } }
		 */
		initGui();
		initAction();
		initData();
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	/**
	 * 获取UI id
	 * 
	 * @Description
	 * @return
	 */
	protected abstract int getContentLayout();

	/**
	 * 初始化UI
	 * 
	 * @Description
	 */
	protected abstract void initGui();

	/**
	 * 初始化事件
	 * 
	 */
	protected abstract void initAction();

	/**
	 * 初始化数据 如果activity被杀死，可在里面 初始化数据
	 * 
	 * @Description
	 */
	protected abstract void initData();

	private long noDoublelastClickTime = 0;

	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - noDoublelastClickTime > 1000) {
			noDoublelastClickTime = currentTime;
			onNoDoubleClick(v);
		}
	}

	public void onNoDoubleClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putSerializable("loginData", MyApplication.getInstance()
				.getUserInfo());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		overridePendingTransition(R.anim.slide_left_out, R.anim.slide_right_out);
		// 杀掉请求进程
		APPRestClient.cancelRequests(ctx);
		ActivityCollector.removeActivity(this);
		Log.i("BaseActivity", "ActivityCollector size:"
				+ ActivityCollector.activites.size());
	}

	protected void exit() {
		ActivityCollector.finishAll();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
		HXSDKHelper.getInstance().getNotifier().reset();
		MobclickAgent.onResume(ctx);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(ctx);
	}

	/**
	 * 获取application中指定的meta-data
	 * 
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx) {
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager
						.getApplicationInfo(ctx.getPackageName(),
								PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString("UMENG_CHANNEL");
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}

	public static void exitClient() {
		ImageLoader.getInstance().clearMemoryCache(); // 清除内存缓存
		/* ImageLoader.getInstance().clearDiskCache(); */// 清除本地缓存
		MyApplication.getInstance().clearLoginData();
		// 关闭所有Activity
		ActivityCollector.finishAll();
		// System.exit(0);
	}
	
	/** 
     * 在API16以前使用setBackgroundDrawable，在API16以后使用setBackground 
     * API16<---->4.1 
     * @param view 
     * @param drawable 
     */  
    public void setBackgroundOfVersion(View view, Drawable drawable) {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  
            //Android系统大于等于API16，使用setBackground  
            view.setBackground(drawable);  
        } else {  
            //Android系统小于API16，使用setBackground  
            view.setBackgroundDrawable(drawable);  
        }  
    }
    
    public void sendLogoutRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.USER_LOGIN_OUT, params,
				new APPResponseHandler<TestEntity>(TestEntity.class, ctx) {
					@Override
					public void onSuccess(TestEntity result) {
						SharedPreferenceUtil.getInstance(ctx).setString("JPushRegistId", "null");
						SharedPreferenceUtil.getInstance(ctx).setString("JPushLoginRegistId", "null");

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {
					}
				});

	}
}
