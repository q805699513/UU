package com.uugty.uu.base.application;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application{

	//全局上下文
	private Context context;
	
	//全局BaseApplication的唯一實例
	private static BaseApplication instance;
	
	//app名字
	private String appName;

	public Context getContext() {
		if(context == null){
			context = this;
		}
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static  BaseApplication getInstance() {
		if (instance == null) {  
	        synchronized (BaseApplication.class) {  
		        if (instance == null) {  
		        	instance = new BaseApplication();  
		        }  
	        }  
	    }  
		return instance;
	}

	public static void setInstance(BaseApplication instance) {
		BaseApplication.instance = instance;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
}
