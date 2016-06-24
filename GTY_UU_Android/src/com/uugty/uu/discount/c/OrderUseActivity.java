package com.uugty.uu.discount.c;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class OrderUseActivity extends BaseActivity implements OnClickListener {

	private LinearLayout imag_back;

	protected int getContentLayout() {
		return R.layout.activity_use_order;
	}

	@Override
	protected void initGui() {
		imag_back = (LinearLayout) findViewById(R.id.tabar_back);
	}

	@Override
	protected void initAction() {
		imag_back.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		imag_back.setClickable(true);
	}

	@Override
	public void onClick(View v) {
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			break;
		default:
			break;
		}
	}

}
