package com.uugty.uu.com.rightview.alipaywallet;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.com.rightview.ChooseBankActivity;

public class AddWithDrawActivity extends BaseActivity implements OnClickListener {

	private LinearLayout right_back_add;
	private LinearLayout alipay_account;
	private LinearLayout bank_account;

	protected int getContentLayout() {

		return R.layout.add_withdraw_account;
	}

	@Override
	protected void initGui() {

		right_back_add = (LinearLayout) findViewById(R.id.tabar_back);
		alipay_account = (LinearLayout) findViewById(R.id.add_alipay_account);
		bank_account = (LinearLayout) findViewById(R.id.add_bank_account);

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		right_back_add.setOnClickListener(this);
		alipay_account.setOnClickListener(this);
		bank_account.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		right_back_add.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		Intent  i = new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			right_back_add.setClickable(false);
			break;
		case R.id.add_alipay_account:
			i.setClass(this,AddAliPayActivity.class);
			startActivity(i);
			break;
		case R.id.add_bank_account:
			i.setClass(this, ChooseBankActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}
	}
}
