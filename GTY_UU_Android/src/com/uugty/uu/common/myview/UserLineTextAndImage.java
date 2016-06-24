package com.uugty.uu.common.myview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;

public class UserLineTextAndImage extends RelativeLayout {

	private View view;
	private TextView leftTextView, rightTextView;
	private ImageView leftImageView;
	private View line_long,line_short;

	public UserLineTextAndImage(Context context) {
		super(context);
		init(context);
	}

	public UserLineTextAndImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UserLineTextAndImage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.user_line_text_and_image, this);
		line_long = (View) view
				.findViewById(R.id.user_line_text_long);
		line_short = (View) view
				.findViewById(R.id.user_line_text_short);
		leftTextView = (TextView) view
				.findViewById(R.id.user_line_text_and_image_left_text);
		rightTextView = (TextView) view
				.findViewById(R.id.user_line_text_and_image_rigth_text);
		leftImageView = (ImageView) view
				.findViewById(R.id.user_line_text_and_image_left_img);
	}

	public void setLeftText(String text) {
		leftTextView.setText(text);
	}
	/*
	 * 设置长短线
	 */
	public void linesl(){
		line_long.setVisibility(View.GONE);
		line_short.setVisibility(View.VISIBLE);
	}
	//设置内容
	public void setRightText(String text){
		rightTextView.setText(text);
	}
	/*
	 * 设置图标
	 */
	public void setLeftImageView(Drawable resId) {
		// TODO Auto-generated method stub
		leftImageView.setImageDrawable(resId);
	}
	
	public void setImageViewGone() {
		// TODO Auto-generated method stub
		leftImageView.setVisibility(View.GONE);
	}
	/*
	 * 修改左边字段
	 */
	public void setLedtText(String text){
		leftTextView.setText(text);
	}
	/*public void hitRel(){
		User_line_textandimg_rel.setBackgroundColor(Color.parseColor("#e5e5e5"));
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				User_line_textandimg_rel.setBackgroundColor(Color.parseColor("#ffffff"));
			}
		}, 200);
	}*/
}