package com.uugty.uu.setup;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class ContactUsActivity extends BaseActivity{

	private LinearLayout backLin;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.contact_us;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		backLin = (LinearLayout) findViewById(R.id.tabar_back);
	}

	@Override
	protected void initAction() {
		backLin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}



}
