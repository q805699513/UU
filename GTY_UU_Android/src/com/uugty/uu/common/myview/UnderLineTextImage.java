package com.uugty.uu.common.myview;

import com.uugty.uu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UnderLineTextImage extends RelativeLayout {

	private View view;
	private TextView leftTextView;
	private ImageView imageView;

	public UnderLineTextImage(Context context) {
		super(context);
		init(context);
	}

	public UnderLineTextImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UnderLineTextImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.under_line_text_image, this);
		leftTextView = (TextView) view
				.findViewById(R.id.under_line_text_image_left_text);
		imageView = (ImageView) view
				.findViewById(R.id.under_line_text_image_right_iamge);

	}
	
	public void setLeftText(String leftText){
		leftTextView.setText(leftText);
	}
	
	public void setImage(int res){
		imageView.setImageResource(res);
	}
}
