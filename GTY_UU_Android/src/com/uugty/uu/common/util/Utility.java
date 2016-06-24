package com.uugty.uu.common.util;

import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		/*
		 * int itemHeight = (int) (TypedValue
		 * .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		 * MyApplication.getInstance
		 * ().getResources().getDimension(R.dimen.find_image_lin_height),
		 * MyApplication.getInstance().getResources().getDisplayMetrics()));
		 */
		float itemHeight = MyApplication.getInstance().getResources()
				.getDimension(R.dimen.find_image_lin_height); // 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		/*
		 * for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //
		 * listAdapter.getCount()返回数据项的数目 View listItem = listAdapter.getView(i,
		 * null, listView); listItem.measure(0, 0); // 计算子项View 的宽高 totalHeight
		 * += listItem.getMeasuredHeight(); // 统计所有子项的总高度 }
		 */
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = (int) (itemHeight * listAdapter.getCount() + (listView
				.getDividerHeight() * (listAdapter.getCount() - 1)));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	public static void setSwipeMenuListViewHeightBasedOnChildren(ListView listView) {

		/*
		 * int itemHeight = (int) (TypedValue
		 * .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		 * MyApplication.getInstance
		 * ().getResources().getDimension(R.dimen.find_image_lin_height),
		 * MyApplication.getInstance().getResources().getDisplayMetrics()));
		 */
		float itemHeight = MyApplication.getInstance().getResources()
				.getDimension(R.dimen.fragment2_swipeMenu); // 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		/*
		 * for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //
		 * listAdapter.getCount()返回数据项的数目 View listItem = listAdapter.getView(i,
		 * null, listView); listItem.measure(0, 0); // 计算子项View 的宽高 totalHeight
		 * += listItem.getMeasuredHeight(); // 统计所有子项的总高度 }
		 */
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = (int) (itemHeight * listAdapter.getCount() + (listView
				.getDividerHeight() * (listAdapter.getCount() - 1)));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
}