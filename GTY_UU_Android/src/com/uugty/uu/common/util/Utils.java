package com.uugty.uu.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author SunnyCoffee
 * @Date 2014-1-28
 * @version 1.0
 * @Desc 工具�?
 */

public class Utils {
	private static long lastClickTime;
	
	public synchronized static boolean isFastClick() {
	        long time = System.currentTimeMillis();   
	        if ( time - lastClickTime < 500) {   
	            return true;   
	        }   
	        lastClickTime = time;   
	        return false;   
	    }
	//使用
	/*@Override
	public void onClick(View v) {
	    if (Utils.isFastClick()) {
	        return ;
	    }
	}
	*/

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}
}
