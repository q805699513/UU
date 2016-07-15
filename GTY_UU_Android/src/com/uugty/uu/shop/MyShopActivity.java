package com.uugty.uu.shop;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.SellEntity;
import com.uugty.uu.map.PublishServicesActivity;

public class MyShopActivity extends BaseActivity implements OnClickListener{

	private LinearLayout mServiceControl;//服务管理
	private LinearLayout mShare;//分享赚钱
	private LinearLayout mIsU;//我是小u
    private TopBackView titleView;
	private CirculHeadImage mHeadImg;//用户头像
	private TextView mName;//用户名称
	private TextView mId;//用户ID
	private TextView mRetailReward;//分销奖励
	private TextView mRetailSell;//分销销售额
	private TextView mServiceReward;//服务奖励
	private TextView mServiceSell;//服务销售额
	private Button mPublishService;//发布服务


	@Override
	protected int getContentLayout() {

		return R.layout.activity_myshop;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.open_shop_title);
		titleView.setTitle("我的小店");
		mServiceControl = (LinearLayout) findViewById(R.id.my_shop_service_control);
		mShare = (LinearLayout) findViewById(R.id.my_shop_share);
		mIsU = (LinearLayout) findViewById(R.id.my_shop_isu);
		mHeadImg = (CirculHeadImage) findViewById(R.id.my_shop_headImg);
		mName = (TextView) findViewById(R.id.my_shop_name);
		mId	= (TextView) findViewById(R.id.my_shop_id);
		mRetailReward = (TextView) findViewById(R.id.my_shop_retail);
		mRetailSell	= (TextView) findViewById(R.id.my_shop_retailSell);
		mServiceSell = (TextView) findViewById(R.id.my_shop_service_sell);
		mServiceReward = (TextView) findViewById(R.id.my_shop_service_reward);
		mPublishService = (Button) findViewById(R.id.my_shop_publish);
	}

	@Override
	protected void initAction() {
		mServiceControl.setOnClickListener(this);
		mShare.setOnClickListener(this);
		mIsU.setOnClickListener(this);
		mPublishService.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		//用户头像
		if (!MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserAvatar().equals("")) {
			mHeadImg.setCirCularImageSize(55, 55, 5);
			mHeadImg.setBackPic("drawable://"
					+ R.drawable.persion_circle_bg);
			// imageView.setCirCularImageSize(85, 85, 6);
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserAvatar().contains("images")) {
				mHeadImg.setNoHeadPic(MyApplication
						.getInstance().getUserInfo().getOBJECT()
						.getUserAvatar(), "net");
			} else {
				mHeadImg.setNoHeadPic(MyApplication
						.getInstance().getUserInfo().getOBJECT()
						.getUserAvatar(), "local");
			}
		} else {
			// 加载默认的图片
			mHeadImg.setBackPic("drawable://"
					+ R.drawable.persion_circle_bg);
			mHeadImg.setNoHeadPic("drawable://"
					+ R.drawable.no_default_head_img, "drawable");
			mHeadImg.setCirCularImageSize(55, 55, 5);
		}
		//用户名称
		if (!MyApplication.getInstance().getUserInfo().getOBJECT().getUserName().equals("")
				&& MyApplication.getInstance().getUserInfo().getOBJECT().getUserName() != null) {
			mName.setText(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserName());
		}
		//用户ID
		if (!MyApplication.getInstance().getUserInfo().getOBJECT().getUserId().equals("")
				&& MyApplication.getInstance().getUserInfo().getOBJECT().getUserId() != null) {
			mId.setText("ID: " + MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserVipId());
		}
		sendRequest();//发送网络请求获取数据
	}

	public void sendRequest(){
		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.USER_SELL, params,
				new APPResponseHandler<SellEntity>(SellEntity.class, this) {
					@Override
					public void onSuccess(SellEntity result) {
						if(result.getLIST() != null){
							for(int i = 0;i < result.getLIST().size() ; i++) {
								if ("0".equals(result.getLIST().get(i).getUserId())) {
									mRetailReward.setText(String.valueOf((float) Math.round(result.getLIST().get(i).getPayPrice()*100)/100));
									mRetailSell.setText(String.valueOf((float) Math.round(result.getLIST().get(i).getTotalPrice()*100)/100));
								} else {
									mServiceReward.setText(String.valueOf((float) Math.round(result.getLIST().get(i).getPayPrice()*100)/100));
									mServiceSell.setText(String.valueOf((float) Math.round(result.getLIST().get(i).getTotalPrice()*100)/100));
								}
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										MyShopActivity.this)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														finish();
														dialog.dismiss();
													}
												}).show();
							}
						}
					}
					@Override
					public void onFinish() {

					}
				});

	}
	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		switch(v.getId()){
			case R.id.my_shop_publish:
				i.setClass(this,PublishServicesActivity.class);
				i.putExtra("from", "framgent");
				startActivity(i);
				break;
			case R.id.my_shop_service_control:
				i.setClass(this,ShopControlActivity.class);
				startActivity(i);
				break;
			case R.id.my_shop_share:
				i.setClass(this,NotVipShopActivity.class);
				startActivity(i);
				break;
			case R.id.my_shop_isu:
				i.setClass(this,MyIsUuActivity.class);
				startActivity(i);
				break;
			default:
				break;
		}
	}
}
