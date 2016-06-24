package com.uugty.uu.common.myview;


import com.uugty.uu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UnderLineText extends RelativeLayout{

	private View view;
    private TextView leftTextView,rightTextView;
	public UnderLineText(Context context){
		super(context);
		init(context);
	}
	
	public UnderLineText(Context context,AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	public UnderLineText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.under_line_text,this);
		leftTextView = (TextView) view
				.findViewById(R.id.under_line_left_text);
		rightTextView = (TextView) view
				.findViewById(R.id.under_line_rigth_text);
		
	}
	
	public void setText(String leftText,String rightText){
		leftTextView.setText(leftText);
		rightTextView.setText(rightText);
	}
}
