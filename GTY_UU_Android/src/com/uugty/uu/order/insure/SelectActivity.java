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

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;
import com.uugty.uu.order.UUOrederPayActivity;
import com.uugty.uu.order.UUPayActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends BaseActivity{
	
	private Button mButton;//确定
	private Drawable noclick,click;
	
	private String isNotRec = "1";//是否选择暂不使用
	private String from = "";//从哪个页面跳转
	private String DiscountId; //代金券id
	private String UserId;//用户和代金券关联表主键id
	private String discountMoney = "0";//代金券金额
	private List<DiscountEntity> ls = new ArrayList<DiscountEntity>();

	@Override
	protected int getContentLayout() {
		return R.layout.activity_insure_selected;
	}

	@Override
	protected void initGui() {
		
		if (null != getIntent()) {
			ls = (List<DiscountEntity>) getIntent().getSerializableExtra("list");
			from = getIntent().getStringExtra("from");
		}
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
		 
	}
	

	@Override
	protected void initAction() {
		Resources res = ctx.getResources();
		noclick = res.getDrawable(R.drawable.pay_noclick);
		click = res.getDrawable(R.drawable.pay_click);
		
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("id", DiscountId);
				intent.putExtra("userId", UserId);
				intent.putExtra("notRec", isNotRec);
				intent.putExtra("discountMoney", discountMoney);
				if("write".equals(from)){
					intent.setClass(SelectActivity.this, UUPayActivity.class);
				}
				if("modify".equals(from)){
					intent.setClass(SelectActivity.this, UUOrederPayActivity.class);
				}
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		isNotRec = "1";
	}

}
