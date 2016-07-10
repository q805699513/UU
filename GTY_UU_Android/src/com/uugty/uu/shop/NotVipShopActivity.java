package com.uugty.uu.shop;



import android.view.View;
import android.view.View.OnClickListener;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;

public class NotVipShopActivity extends BaseActivity implements OnClickListener{

    private TopBackView titleView;
	@Override
	protected int getContentLayout() {
		return R.layout.activity_notvipshop;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.open_shop_title);
		titleView.setTitle("我的小店");

	}

	@Override
	protected void initAction() {
	}

	@Override
	protected void initData() {

	}

	
	@Override
	public void onClick(View v) {

	}
}
