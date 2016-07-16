package com.uugty.uu.shop;



import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.map.OpenShopActivity;
import com.uugty.uu.map.PublishServicesActivity;

public class NotVipShopActivity extends BaseActivity implements OnClickListener{

    private TopBackView titleView;
	private LinearLayout mServiceControl;//服务管理
	private LinearLayout mShare;//分享赚钱
	private Button mPublishService;//发布服务
	private TextView mOpenShop;//立即成为小u
	@Override
	protected int getContentLayout() {

		return R.layout.activity_notvipshop;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.open_shop_title);
		titleView.setTitle("我的小店");
		mServiceControl = (LinearLayout) findViewById(R.id.notvip_shop_service_control);
		mShare = (LinearLayout) findViewById(R.id.notvip_shop_share);
		mPublishService = (Button) findViewById(R.id.notvip_shop_publish);
		mOpenShop = (TextView) findViewById(R.id.notvip_shop_button);
	}

	@Override
	protected void initAction() {
		mServiceControl.setOnClickListener(this);
		mShare.setOnClickListener(this);
		mPublishService.setOnClickListener(this);
		mOpenShop.setOnClickListener(this);
	}

	@Override
	protected void initData() {
	}

	
	@Override
	public void onClick(View v) {

		Intent intent = new  Intent();
		switch (v.getId()){
			case R.id.notvip_shop_button:
				intent.setClass(this,
						OpenShopActivity.class);
				startActivity(intent);
				break;
			case  R.id.notvip_shop_publish:
				intent.setClass(this,PublishServicesActivity.class);
				intent.putExtra("from", "framgent");
				startActivity(intent);
				break;
			case R.id.notvip_shop_service_control:
				intent.setClass(this,ShopControlActivity.class);
				startActivity(intent);
				break;
			case R.id.notvip_shop_share:
				break;
			default:
				break;
		}
	}
}
