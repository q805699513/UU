package com.uugty.uu.common.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;

public class UnderLineTextAndImage extends RelativeLayout {

	private View view;
	private TextView leftTextView, rightTextView,textRight;
	private ImageView rigthImageView;
	// 横线
	private View lineView;

	public UnderLineTextAndImage(Context context) {
		super(context);
		init(context);
	}

	public UnderLineTextAndImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UnderLineTextAndImage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.under_line_text_and_image, this);
		leftTextView = (TextView) view
				.findViewById(R.id.under_line_text_and_image_left_text);
		rightTextView = (TextView) view
				.findViewById(R.id.under_line_text_and_image_rigth_text);
		textRight = (TextView) view
				.findViewById(R.id.under_line_text_and_image_rigth_textRight);
		rigthImageView = (ImageView) view
				.findViewById(R.id.under_line_text_and_image_rigth_image);
		lineView = findViewById(R.id.under_line_text_and_image_line);
	}

	public void setLeftText(String text) {
		leftTextView.setText(text);
	}
	
	/*
	 * 隐藏下划线
	 */
	public void hideLine(){
		lineView.setVisibility(View.GONE);
	}
	
	/*
	 * 设置内容
	 */
	public void setRightText(String text){
		rightTextView.setText(text);
	}	
	/*
	 * 隐藏图片 显示内容
	 */
	public void setRightTextRight(String text){
		rigthImageView.setVisibility(View.GONE);
		textRight.setVisibility(View.VISIBLE);
		textRight.setText(text);
		
		
	}	
	public void setLedtText(String text){
		leftTextView.setText(text);
	}
	public void setTextBG(){
		rightTextView.setBackgroundResource(R.drawable.person_lable_bg_color);
	}
}