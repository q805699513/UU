package com.uugty.uu.order.insure;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class SelectActivity extends BaseActivity{
	
	private Button mButton;//确定
	private Drawable noclick,click;
	private String mType = "";//保险类型
	private LinearLayout mInsureLinear1;
	private LinearLayout mInsureLinear2;
	private LinearLayout mInsureLinear3;
	private LinearLayout mInsureLinear4;
	private TextView mInsureImg1;
	private TextView mInsureImg2;
	private TextView mInsureImg3;
	private TextView mInsureImg4;
	private int linear1 = 0;
	private int linear2 = 0;
	private int linear3 = 0;
	private int linear4 = 0;

	@Override
	protected int getContentLayout() {
		return R.layout.activity_insure_selected;
	}

	@Override
	protected void initGui() {

		Resources res = ctx.getResources();
		noclick = res.getDrawable(R.drawable.pay_noclick);
		click = res.getDrawable(R.drawable.pay_click);
		
		//display对象获取屏幕的宽高
		Display dis = getWindowManager().getDefaultDisplay();
		//调整弹出框位置
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		win.setAttributes(lp);
		
		mButton = (Button) findViewById(R.id.insure_selected_commit);
		mInsureLinear1 = (LinearLayout) findViewById(R.id.insure_linear1);
		mInsureLinear2 = (LinearLayout) findViewById(R.id.insure_linear2);
		mInsureLinear3 = (LinearLayout) findViewById(R.id.insure_linear3);
		mInsureLinear4 = (LinearLayout) findViewById(R.id.insure_linear4);

		mInsureImg1 = (TextView) findViewById(R.id.insure_img1);
		mInsureImg2 = (TextView) findViewById(R.id.insure_img2);
		mInsureImg3 = (TextView) findViewById(R.id.insure_img3);
		mInsureImg4 = (TextView) findViewById(R.id.insure_img4);

	}
	

	@Override
	protected void initAction() {
		Resources res = ctx.getResources();
		noclick = res.getDrawable(R.drawable.pay_noclick);
		click = res.getDrawable(R.drawable.pay_click);
		mInsureLinear1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearClick();
				if(linear1 == 0){
					linear1 = 1;
					mType = "1";
					mInsureImg1.setBackgroundDrawable(click);
				}else{
					linear1 = 0;
					mInsureImg1.setBackgroundDrawable(noclick);
				}
			}
		});
		mInsureLinear2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearClick();
				if(linear2 == 0){
					linear2 = 1;
					mType = "2";
					mInsureImg2.setBackgroundDrawable(click);
				}else{
					linear2 = 0;
					mInsureImg2.setBackgroundDrawable(noclick);
				}
			}
		});
		mInsureLinear3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearClick();
				if(linear3 == 0){
					linear3 = 1;
					mType = "3";
					mInsureImg3.setBackgroundDrawable(click);
				}else{
					linear3 = 0;
					mInsureImg3.setBackgroundDrawable(noclick);
				}
			}
		});
		mInsureLinear4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearClick();
				if(linear4 == 0){
					linear4 = 1;
					mType = "0";
					mInsureImg4.setBackgroundDrawable(click);
				}else{
					linear4 = 0;
					mInsureImg4.setBackgroundDrawable(noclick);
				}
			}
		});
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("type", mType);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}

	private void clearClick(){
		mInsureImg1.setBackgroundDrawable(noclick);
		mInsureImg2.setBackgroundDrawable(noclick);
		mInsureImg3.setBackgroundDrawable(noclick);
		mInsureImg4.setBackgroundDrawable(noclick);
	}

	@Override
	protected void initData() {

	}
}
