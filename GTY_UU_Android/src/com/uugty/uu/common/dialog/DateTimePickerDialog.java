package com.uugty.uu.common.dialog;

import java.util.Calendar;
import java.util.Date;

import com.uugty.uu.common.dialog.DateTimePicker.OnDateTimeChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;
import android.content.DialogInterface.OnClickListener;;

public class DateTimePickerDialog extends AlertDialog implements
		OnClickListener {
	private DateTimePicker mDateTimePicker;
	
	private OnDateTimeSetListener mOnDateTimeSetListener;
	private int daySelect,hourSelect,minuteSelect;
	@SuppressWarnings("deprecation")
	public DateTimePickerDialog(Context context, long date) {
		super(context);
		daySelect = 1;
		Date d = new Date();
		hourSelect = d.getHours();
		minuteSelect = (d.getMinutes())/15;
		mDateTimePicker = new DateTimePicker(context);
		setView(mDateTimePicker);
		/*
		 * 瀹炵幇鎺ュ彛锛屽疄鐜伴噷闈㈢殑鏂规硶
		 */
		mDateTimePicker
				.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {
					@Override
					public void onDateTimeChanged(DateTimePicker view,
							int day, int hour, int minute) {
						
						daySelect = day;
						hourSelect = hour;
						minuteSelect = minute;
						/**
						 * 鏇存柊鏃ユ湡
						 */
						
					}
				});

		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);
		
		
	}

	/*
	 * 鎺ュ彛鍥炶鎺т欢 绉掓暟
	 */
	public interface OnDateTimeSetListener {
		void OnDateTimeSet(AlertDialog dialog, String date);
	}

	/**
	 * 鏇存柊瀵硅瘽妗嗘棩鏈�
	 * 
	 * @param date
	 */
	

	/*
	 * 瀵瑰鍏紑鏂规硶璁〢ctivity瀹炵幇
	 */
	public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
		mOnDateTimeSetListener = callBack;
	}

	

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mOnDateTimeSetListener != null) {
			mOnDateTimeSetListener.OnDateTimeSet(this,daySelect+":"+hourSelect+":"+(minuteSelect*15));
		}	}
}
