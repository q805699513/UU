package com.uugty.uu.map;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.Util;
import com.uugty.uu.main.MainActivity;

public class VipPaySucessActivity extends BaseActivity implements
		OnClickListener {

	private Button btn;
	private LinearLayout backLin;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_vip_center;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		backLin = (LinearLayout) findViewById(R.id.vip_pay_sucess_back);
		btn = (Button) findViewById(R.id.vip_pay_sucess_btn);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		btn.setOnClickListener(this);
		backLin.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		ActivityCollector
		.removeSpecifiedActivity("com.uugty.uu.map.VipPayActivity");
		switch (v.getId()) {
		case R.id.vip_pay_sucess_btn:
			
			if (Util.vipBack.equals("main")) {
				MainActivity.Sb = R.id.dialog_rel;
				intent.setClass(VipPaySucessActivity.this, MainActivity.class);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(VipPaySucessActivity.this,
						MyServicesActivity.class);
			}
			startActivity(intent);
			finish();
			break;
		case R.id.vip_pay_sucess_back:
			if (Util.vipBack.equals("main")) {
				MainActivity.Sb = R.id.dialog_rel;
				intent.setClass(VipPaySucessActivity.this, MainActivity.class);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(VipPaySucessActivity.this,
						MyServicesActivity.class);
			}
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.map.VipPayActivity");
			if (Util.vipBack.equals("main")) {
				MainActivity.Sb = R.id.dialog_rel;
				intent.setClass(VipPaySucessActivity.this, MainActivity.class);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(VipPaySucessActivity.this,
						MyServicesActivity.class);
			}
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
