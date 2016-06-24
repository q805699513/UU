package com.uugty.uu.common.util;

import java.util.Map;

import com.uugty.uu.util.LogUtils;
import android.content.Context;
import android.content.SharedPreferences;

//sharedPreference工具类
public class SharedPreferenceUtil {

	private static String SP_NAME = "uu";//SP文件名
	private SharedPreferences mSharedStore;
	private SharedPreferences.Editor editor;
	private static SharedPreferenceUtil instance;
	
	public static SharedPreferenceUtil getInstance(Context context){
		if(instance == null){
			synchronized(SharedPreferenceUtil.class){
				if(instance == null){
					instance = new SharedPreferenceUtil(context);
				}
			}
		}
		return instance;
	}
	
	
	//构造函数
	private SharedPreferenceUtil(Context context){
		init(context);
	}
	
	//初始化
	private void init(Context context){
		if(context !=null){
			mSharedStore = context.getSharedPreferences(SP_NAME, context.MODE_PRIVATE);
			editor = mSharedStore.edit();
		}
	}
	
	//setString
	public void setString(String key,String value){
		editor.putString(key, value);
		editor.commit();
	}
	
	//getString
	public String getString(String key,String delValue){
		return mSharedStore.getString(key, delValue);
	}
	
	//setInt
	public void setInt(String key,int value){
		editor.putInt(key, value);
		editor.commit();
	}
	
	//getInt
	public int getInt(String key,int delValue){
		return mSharedStore.getInt(key, delValue);
	}
	
	/**
	 * <p>
	 * 根据键名提取键值。
	 * </p>
	 * 
	 * @param key
	 *            键
	 * @param obj
	 *            支持类型 String\Integer\Boolean\Float\Long\null
	 * @return 是否存储成功 true\false
	 */
	public boolean putObject(String key, Object obj) {

		try {
			if (obj == null) {
				editor.putString(key, null);
			} else {
				if (obj instanceof String) {
					editor.putString(key, (String) obj);
				} else if (obj instanceof Integer) {
					editor.putInt(key, ((Integer) obj).intValue());
				} else if (obj instanceof Boolean) {
					editor.putBoolean(key, ((Boolean) obj).booleanValue());
				} else if (obj instanceof Float) {
					editor.putFloat(key, ((Float) obj).floatValue());
				} else if (obj instanceof Long) {
					editor.putLong(key, ((Long) obj).longValue());
				}
			}
			return editor.commit();
		} catch (Exception e) {
			LogUtils.printException(e);
		}
		return false;
	}
	
	/**
	 * 获取键值
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public Object getObject(String key) {

		Object obj = null;
		try {
			if (mSharedStore.contains(key)) {
				Map<String, ?> map = mSharedStore.getAll();
				if (map != null && !map.isEmpty()) {
					obj = map.get(key);
				}
			}
		} catch (Exception e) {
			LogUtils.printException(e);
		}
		return obj;
	}

	/**
	 * 存储boolean 类型键值对
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 是否存储成功
	 */
	public boolean putBoolean(String key, boolean value) {
		try {
			editor.putBoolean(key, value);
			return editor.commit();
		} catch (Exception e) {
			LogUtils.printException(e);
		}
		return false;
	}
	
	/**
	 * <p>
	 * 删除存储值。给定键后将删除此键对应的值。
	 * </p>
	 * 
	 * @param key 给定的键
	 */
	public boolean remove(String key) {
		try {
			editor.remove(key);
			return editor.commit();
		} catch (Exception e) {
			LogUtils.printException(e);
		}
		return false;
	}
	
	/**
	 * 删除数据库
	 * 
	 * @throws Exception
	 */
	public final void deleteDB() throws Exception {
		mSharedStore.edit().remove(SP_NAME);
	}
	
}
