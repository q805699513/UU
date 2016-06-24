package com.uugty.uu.common.myview;

import com.uugty.uu.R;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.util.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBackView extends RelativeLayout {

	private View view;
	private TextView titleView;
	private LinearLayout topBackImage;
	private ImageView img;
	public TopBackView(Context context) {
		super(context);
		init(context);
	}

	public TopBackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TopBackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.top_back, this);
		
		titleView = (TextView) view.findViewById(R.id.top_back_text);
		topBackImage = (LinearLayout) view.findViewById(R.id.tabar_back_title);
		img=(ImageView) view.findViewById(R.id.tabar_back_img);
		topBackImage.setOnClickListener(new NoDoubleClickListener() {
			
			@Override
			public void onNoDoubleClick(View v) {
				img.setImageDrawable(getResources().getDrawable(R.drawable.phone_back_hit));
				if(((Activity)getContext()).getClass().getName().equals("com.uugty.uu.person.PersonValidResultActivity")){
					closeActivity("com.uugty.uu.person.PersonPhotoVeriActivity");
				}else if(((Activity)getContext()).getClass().getName().equals("com.uugty.uu.com.rightview.PasswordActivity")){
					closeActivity("com.uugty.uu.com.rightview.UPdataActivity");
				}
				img.setEnabled(false);
				((Activity)getContext()).finish();
			}
		});
	}
	public void setTitle(String title){
		titleView.setText(title);
	}
	
	public void closeActivity(String activityName){
		ActivityCollector
		.removeSpecifiedActivity(activityName);
	}
	
}
