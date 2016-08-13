package com.uugty.uu.com.rightview.alipaywallet;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class WithDrawDetailActivity extends BaseActivity implements OnClickListener {

	private ImageView right_back_add;

	protected int getContentLayout() {

		return R.layout.detail_withdrawcash;
	}

	@Override
	protected void initGui() {

		right_back_add = (ImageView) findViewById(R.id.right_view_withdrawcash_back);

	}

	@Override
	protected void initAction() {
		right_back_add.setOnClickListener(this);
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
		case R.id.right_view_withdrawcash_back:
			finish();
			right_back_add.setClickable(false);
			break;
		default:
			break;
		}
	}
}
