package com.uugty.uu.person;

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
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;

public class TouristFillActivity extends BaseActivity implements OnClickListener{
	private TopBackView titleView;
	private TextView tourist_confirm_text;
	private EmojiEdite tourist_realID_edt,tourist_realName_edt;
	private String name,Id_card;
	private String getname,getId_card,gettoristId;
	private String touristType="";//0添加  1 修改
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.touristdata_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(getIntent()!=null){
			touristType=getIntent().getStringExtra("touristType");
			if(touristType.equals("1")){
				getname=getIntent().getStringExtra("name");
				getId_card=getIntent().getStringExtra("id_card");
				gettoristId=getIntent().getStringExtra("toristId");				
			}
		}
		titleView = (TopBackView) findViewById(R.id.touristback_title);
		if(touristType.equals("0")){
			titleView.setTitle("添加");
		}else{
			titleView.setTitle("修改");			
		}
		tourist_confirm_text=(TextView) findViewById(R.id.tourist_confirm_text);
		tourist_realName_edt=(EmojiEdite) findViewById(R.id.tourist_realName_edt);
		tourist_realID_edt=(EmojiEdite) findViewById(R.id.tourist_realID_edt);
		if(touristType.equals("1")){
			tourist_realName_edt.setText(getname);
			tourist_realID_edt.setText(getId_card);
		}
		
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		tourist_confirm_text.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		switch (v.getId()) {
		case R.id.tourist_confirm_text:
			name=tourist_realName_edt.getText().toString().trim();
			Id_card=tourist_realID_edt.getText().toString().trim();
			if(name.equals("")){
				CustomToast.makeText(ctx, 0, "姓名未填写", 300).show();
			}else if(Id_card.equals("")){
				CustomToast.makeText(ctx, 0, "身份证未填写", 300).show();
			}else if(Id_card.length()<15||Id_card.length()>18){
				CustomToast.makeText(ctx, 0, "身份证号输入错误", 300).show();
			}else{
				if(touristType.equals("0")){
					Report(name, Id_card);
				}else{
					UpDate(name, Id_card, gettoristId);
				}
			}
			break;

		default:
			break;
		}
	}
	// 添加
		private void Report(final String name,final String Idcard) {
			RequestParams params = new RequestParams();
			params.add("contactName", name); //姓名
			params.add("contactIDCard", Idcard); //身份证
			APPRestClient.post(this, ServiceCode.ADD_CONTACT, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
						@Override
						public void onSuccess(BaseEntity result) {
							Intent intent=new Intent();
							setResult(RESULT_OK, intent);
							finish();
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								Report(name, Idcard);
							} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(TouristFillActivity.this)
										.setTitle("提示")
										.setMessage("网络拥堵,请稍后重试！")
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
						}}

						@Override
						public void onFinish() {
						}
					});
		}
		// 修改
				private void UpDate(final String name,final String Idcard,final String contactId) {
					RequestParams params = new RequestParams();
					params.add("contactId", contactId); //修改ID
					params.add("contactName", name); //姓名
					params.add("contactIDCard", Idcard); //身份证
					APPRestClient.post(this, ServiceCode.MODIFY_CONTACT, params,
							new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
								@Override
								public void onSuccess(BaseEntity result) {
									Intent intent=new Intent();
									setResult(RESULT_OK, intent);
									finish();
								}

								@Override
								public void onFailure(int errorCode, String errorMsg) {
									if (errorCode == 3) {
										Report(name, Idcard);
									} else {
									CustomToast.makeText(ctx, 0, errorMsg, 300).show();
									if (errorCode == -999) {
										new AlertDialog.Builder(TouristFillActivity.this)
												.setTitle("提示")
												.setMessage("网络拥堵,请稍后重试！")
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
								}}

								@Override
								public void onFinish() {
								}
							});
				}
}
