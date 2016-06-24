package com.uugty.uu.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @ClassName: DateUtil
 * @Description: 格式化日期信息的对象
 * @author ganliang
 * @date 2015年6月6日 下午3:53:12
 */
public final class DateUtil {

	/**
	 * 根据指定的格式格式化日期对象
	 * 
	 * @param date
	 *            日期对象
	 * @param foramt
	 *            要格式的格式
	 * @return
	 */
	public static String dateFormat(Date date, String foramt) {
		String result = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(foramt);
			result = df.format(date);
		}
		return result;
	}

	public static Date dateFormat(String date, String format) {
		Date result = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			result = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 当前时间戳转为日期
	 * 
	 * @param time
	 * @return 2012-11-11 格式
	 */
	public static String currentTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(time));
	}

	/**
	 * 
	 * @param time
	 * @param patten
	 * @return
	 */
	public static String currentTime(long time, String patten) {
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(new Date(time));
	}
	
	public static String currentTime(String patten) {
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(new Date());
	}
	
	//比较时间是否大于今天
	public static boolean comperaBeforeTime(String time){
		//结果是当天的返回false
		boolean flag = false;
		java.util.Date nowdate=new java.util.Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		try {
			Date d = sdf.parse(time);
			flag = d.before(nowdate);
			if(d.equals(nowdate)) flag=true;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

}