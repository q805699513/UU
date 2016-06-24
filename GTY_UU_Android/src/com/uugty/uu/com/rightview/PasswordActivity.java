package com.uugty.uu.com.rightview;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;

public class PasswordActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout layout_one, layout_two;
	private TopBackView right_backpwd;

	protected int getContentLayout() {
		// TODO Auto-generated method stub 密码管理
		return R.layout.right_pop_pwd;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		layout_one = (RelativeLayout) findViewById(R.id.linear_pwd_one);
		layout_two = (RelativeLayout) findViewById(R.id.linear_pwd_two);
		right_backpwd = (TopBackView) findViewById(R.id.right_backpwd);
		right_backpwd.setTitle("密码管理");
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		layout_one.setOnClickListener(this);
		layout_two.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.com.rightview.UPdataActivity");
		ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.com.rightview.SetPayPwdActivity");
		Intent intetn = new Intent();
		switch (v.getId()) {
		case R.id.linear_pwd_one:
			intetn.setClass(this, UpdatePasswordOld.class);
			startActivity(intetn);
			break;
		case R.id.linear_pwd_two:
			intetn.setClass(this, ForgetPayPwdActivity.class);
			startActivity(intetn);

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityCollector
					.removeSpecifiedActivity("com.uugty.uu.com.rightview.UPdataActivity");
		}
		return super.onKeyDown(keyCode, event);
	}
}
