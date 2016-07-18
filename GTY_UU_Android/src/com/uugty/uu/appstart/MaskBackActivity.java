package com.uugty.uu.appstart;

import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class MaskBackActivity extends BaseActivity {
	private ImageView close;
	private ImageView vip;
	private LinearLayout mLinearLayout;
	private String type;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_mask_dialog;
	}

	@Override
	protected void initGui() {

		if(getIntent() != null){
			type = getIntent().getStringExtra("type");
		}
		close = (ImageView) findViewById(R.id.mask_img);
		vip = (ImageView) findViewById(R.id.mask_vip_img);
		mLinearLayout = (LinearLayout) findViewById(R.id.mask_linearlayout);
		if("1".equals(type)){
			close.setVisibility(View.VISIBLE);
			vip.setVisibility(View.GONE);
		}else{
			close.setVisibility(View.GONE);
			vip.setVisibility(View.VISIBLE);
		}
		//display对象获取屏幕的宽高
		Display dis = getWindowManager().getDefaultDisplay();
		//调整弹出框位置
		Window win = getWindow();
		win.getDecorView().setPadding(0,0,0,0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		ColorDrawable dw = new ColorDrawable(0000000000);
		win.setBackgroundDrawable(dw);
		win.setAttributes(lp);

	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	protected void initAction() {
		mLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}

	@Override
	protected void initData() {

	}

}
