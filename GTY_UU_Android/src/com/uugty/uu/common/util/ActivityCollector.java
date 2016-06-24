package com.uugty.uu.common.util;

/*
 管理Activity
 * */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;

public class ActivityCollector {

	public static List<Activity> activites = new ArrayList<Activity>();

	public static void addActivity(Activity activity) {
		activites.add(activity);
	}

	public static void removeActivity(Activity activity) {
		activites.remove(activity);
		activity.finish();
		activity = null;
	}

	//删除指定activity
	public static void removeSpecifiedActivity(String className) {
		Iterator<Activity> it = activites.iterator();
		while (it.hasNext()) {
			Activity activity = it.next();

			if (activity.getClass().getName().equals(className)) {
				activity.finish();
			}

		}
	}

	@SuppressLint("NewApi")
	public static void finishAll() {
		Iterator<Activity> it = activites.iterator();
		while (it.hasNext()) {
			Activity activity = it.next();

			if (activity == null) {
				continue;
			}
			if (activity != null) {
				try {
					activity.finish();
					activity = null;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		activites.clear();
	}

	@SuppressLint("NewApi")
	public static void finishAllExcept(Activity activity1) {
		Iterator<Activity> it = activites.iterator();
		while (it.hasNext()) {
			Activity activity = it.next();

			if (activity == null || activity1.equals(activity)) {
				continue;
			}
			if (activity != null) {
				try {
					activity.finish();
					activity = null;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		activites.clear();
	}
	
	public static Activity getTopActivity(){
		if(activites.size()>0){
			return activites.get(activites.size()-1);
		}
		return null;
	}
	
	//判断activity存在
	public static boolean isActivityExistence(String activityName){
		boolean isExist=false;
		Iterator<Activity> it = activites.iterator();
		while (it.hasNext()) {
			Activity activity = it.next();

			if (activity.getClass().getName().equals(activityName)) {
				isExist = true;
				break;
			}

		}
		return isExist;
	}
}
