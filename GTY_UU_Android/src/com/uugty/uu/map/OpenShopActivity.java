package com.uugty.uu.map;



import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.login.AgreementWebActivity;

public class OpenShopActivity extends BaseActivity implements OnClickListener{

    private TopBackView titleView;
    private LinearLayout helpLin;
    private Button btn;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_openshop;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.open_shop_title);
		titleView.setTitle("我要开店");
		helpLin = (LinearLayout) findViewById(R.id.open_shop_title_lin);
		btn = (Button) findViewById(R.id.open_shop_btn);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		helpLin.setOnClickListener(this);
		btn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.open_shop_title_lin:
			intent.putExtra("agreement", "generalize");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.open_shop_btn:
			intent.setClass(this, VipPayActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
