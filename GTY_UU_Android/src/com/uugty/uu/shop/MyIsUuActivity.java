package com.uugty.uu.shop;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.ThreeCountEntity;

public class MyIsUuActivity extends BaseActivity implements OnClickListener{

    private TopBackView titleView;
	private TextView mFrist;
	private TextView mSecond;
	private TextView mThree;
	@Override
	protected int getContentLayout() {

		return R.layout.activity_isu;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.collect_titile_top);
		titleView.setTitle("我是小u");
		mFrist = (TextView) findViewById(R.id.isu_first);
		mSecond = (TextView) findViewById(R.id.isu_second);
		mThree = (TextView) findViewById(R.id.isu_three);
	}

	@Override
	protected void initAction() {
		mFrist.setOnClickListener(this);
		mSecond.setOnClickListener(this);
		mThree.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		sendRequest();//发送网络请求获取数据
	}

	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()){
			case R.id.isu_first:
				intent.putExtra("level","1");
				intent.setClass(this,ThreeUuActivity.class);
				startActivity(intent);
				break;
			case R.id.isu_second:
				intent.putExtra("level","2");
				intent.setClass(this,ThreeUuActivity.class);
				startActivity(intent);
				break;
			case R.id.isu_three:
				intent.putExtra("level","3");
				intent.setClass(this,ThreeUuActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}

	}

	public void sendRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.THREE_COUNT, params,
				new APPResponseHandler<ThreeCountEntity>(ThreeCountEntity.class, this) {
					@Override
					public void onSuccess(ThreeCountEntity result) {
						if(result.getLIST().size() > 0){
							for(int i = 0;i < result.getLIST().size(); i++){
								if(1 == result.getLIST().get(i).getPayLevel()){
									mFrist.setText("一级小u (" + String.valueOf(result.getLIST().get(i).getNum()) + "人)");
								}else if(2 == result.getLIST().get(i).getPayLevel()){
									mSecond.setText("二级小u (" + String.valueOf(result.getLIST().get(i).getNum())  + "人)");
								}else if(3 == result.getLIST().get(i).getPayLevel()){
									mThree.setText("三级小u (" + String.valueOf(result.getLIST().get(i).getNum())  + "人)");
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
										MyIsUuActivity.this)
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
}
