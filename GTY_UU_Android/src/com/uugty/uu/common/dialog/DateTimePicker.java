package com.uugty.uu.common.dialog;

import java.util.Calendar;

import com.uugty.uu.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class DateTimePicker extends FrameLayout {
	private NumberPicker mDateSpinner;
	private NumberPicker mHourSpinner;
	private NumberPicker mMinuteSpinner;
	
	private Context mContext;
	private int mHour, mMinute,day;
	private String[] mDateDisplayValues = new String[10];
	private String[] mMinuteValues = new String[4];
	private OnDateTimeChangedListener mOnDateTimeChangedListener;

	@SuppressLint("NewApi")
	public DateTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	@SuppressLint("NewApi")
	public DateTimePicker(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs,defStyle);
		mContext = context;
		init();
	}

	@SuppressLint("NewApi")
	public DateTimePicker(Context context) {
		super(context);
		mContext = context;
		init();

	}

	@SuppressLint("NewApi")
	private void init() {
		/*
		 * 鐛插彇绯荤当鏅傞枔
		 */
		Calendar c = Calendar.getInstance();
		
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = (c.get(Calendar.MINUTE))/15;
		day = 1;
		/**
		 * 鍔犺浇甯冨眬
		 */
		// LayoutInflater inflater = LayoutInflater.from(context);
		// inflater.inflate(R.layout.datedialog, this);
		//inflate(mContext, R.layout.datedialog, this);
		LayoutInflater.from(mContext).inflate(R.layout.dialog_date, this, true);
		/**
		 * 鍒濆鍖栨帶浠�
		 */
		mDateSpinner = (NumberPicker) this.findViewById(R.id.np_date);
		mDateSpinner.setMinValue(1);
		mDateSpinner.setMaxValue(10);
		updateDateControl();
		mDateSpinner.setValue(day);
		mDateSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);

		mHourSpinner = (NumberPicker) this.findViewById(R.id.np_hour);
		mHourSpinner.setMaxValue(23);
		mHourSpinner.setMinValue(0);
		mHourSpinner.setValue(mHour);
		mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);
		mHourSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		mMinuteSpinner = (NumberPicker) this.findViewById(R.id.np_minute);
		mMinuteSpinner.setMaxValue(3);
		mMinuteSpinner.setMinValue(0);
		updateMinuteControl();
		mMinuteSpinner.setValue(mMinute);
		mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);
		mMinuteSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
	}

	/**
	 * 
	 * 鎺т欢鐩戝惉鍣�
	 */
	@SuppressLint("NewApi")
	private NumberPicker.OnValueChangeListener mOnDateChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			
			/**
			 * 鏇存柊鏃ユ湡
			 */
			day = mDateSpinner.getValue();
			onDateTimeChanged();
		}
	};

	@SuppressLint("NewApi")
	private NumberPicker.OnValueChangeListener mOnHourChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mHour = mHourSpinner.getValue();
			onDateTimeChanged();
		}
	};

	@SuppressLint("NewApi")
	private NumberPicker.OnValueChangeListener mOnMinuteChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mMinute = mMinuteSpinner.getValue();
			onDateTimeChanged();
		}
	};

	@SuppressLint("NewApi")
	private void updateDateControl() {
		/**
		 * 鏄熸湡鍑犵畻娉�
		 */
		
		mDateSpinner.setDisplayedValues(null);
		for (int i = 0; i < 10; ++i) {
			
			mDateDisplayValues[i] = "第"+(i+1)+"天";
		}
		mDateSpinner.setDisplayedValues(mDateDisplayValues);
		
		mDateSpinner.invalidate();
	}
	@SuppressLint("NewApi")
	private void updateMinuteControl() {
		/**
		 * 鏄熸湡鍑犵畻娉�
		 */
		
		mMinuteSpinner.setDisplayedValues(null);
		mMinuteValues[0] = "00";
		for (int i = 1; i < 4; ++i) {
			
			mMinuteValues[i] = ""+(i*15);
			
		}
		mMinuteSpinner.setDisplayedValues(mMinuteValues);
		mMinuteSpinner.setWrapSelectorWheel(true);
		mMinuteSpinner.invalidate();
	}

	/*
	 * 鎺ュ彛鍥炶皟 鍙傛暟鏄綋鍓嶇殑View 骞存湀鏃ュ皬鏃跺垎閽�
	 */
	public interface OnDateTimeChangedListener {
		void onDateTimeChanged(DateTimePicker view, 
				int day, int hour, int minute);
	}

	/*
	 * 瀵瑰鐨勫叕寮�鏂规硶
	 */
	public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
		mOnDateTimeChangedListener = callback;
	}

	private void onDateTimeChanged() {
		if (mOnDateTimeChangedListener != null) {
		
			mOnDateTimeChangedListener.onDateTimeChanged(this,
					day, mHour, mMinute);
		}
	}
}