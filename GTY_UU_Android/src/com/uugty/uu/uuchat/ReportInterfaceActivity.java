package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;

public class ReportInterfaceActivity extends BaseActivity implements OnClickListener{
	private TopBackView report_titile_top;
	private RelativeLayout rel1,rel2,rel3,rel4,rel5;
	private ImageView img1,img2,img3,img4,img5;
	private Button send;
	private String repotContent="";
	private String myId="";
	private String groupId="";
	private String reportType="";
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.report_interface_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					ReportInterfaceActivity.class.getName());
			intent.setClass(ReportInterfaceActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		myId=MyApplication.getInstance().getUserInfo().getOBJECT().getUserId();
		}
		if(getIntent()!=null){
		groupId=getIntent().getStringExtra("saidId");
		reportType=getIntent().getStringExtra("type");
		}
		report_titile_top=(TopBackView) findViewById(R.id.report_titile_top);
		report_titile_top.setTitle("举报");
		rel1=(RelativeLayout) findViewById(R.id.report_rel1);
		rel2=(RelativeLayout) findViewById(R.id.report_rel2);
		rel3=(RelativeLayout) findViewById(R.id.report_rel3);
		rel4=(RelativeLayout) findViewById(R.id.report_rel4);
		rel5=(RelativeLayout) findViewById(R.id.report_rel5);
		img1=(ImageView) findViewById(R.id.report_img1);
		img2=(ImageView) findViewById(R.id.report_img2);
		img3=(ImageView) findViewById(R.id.report_img3);
		img4=(ImageView) findViewById(R.id.report_img4);
		img5=(ImageView) findViewById(R.id.report_img5);
		send=(Button) findViewById(R.id.report_send_button);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		report_titile_top.setOnClickListener(this);
		rel1.setOnClickListener(this);
		rel2.setOnClickListener(this);
		rel3.setOnClickListener(this);
		rel4.setOnClickListener(this);
		rel5.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.report_rel1:
			repotContent="色情低俗";
			imgShow(1);
			break;
		case R.id.report_rel2:
			repotContent="赌博";
			imgShow(2);
			break;
		case R.id.report_rel3:
			repotContent="政治敏感";
			imgShow(3);
			break;
		case R.id.report_rel4:
			repotContent="欺诈骗我";
			imgShow(4);
			break;
		case R.id.report_rel5:
			repotContent="违法（暴力恐怖，违禁品等）";
			imgShow(5);
			break;

		case R.id.report_send_button:
			if(!repotContent.equals("")){				
				Report("",reportType);			
			}else{
				CustomToast.makeText(ctx, 0, "请选择", 300).show();
			}
			break;
		case R.id.report_titile_top:
			finish();
			break;
		}
	}
	private void imgShow(int num) {
		// TODO Auto-generated method stub
		img1.setVisibility(View.GONE);
		img2.setVisibility(View.GONE);
		img3.setVisibility(View.GONE);
		img4.setVisibility(View.GONE);
		img5.setVisibility(View.GONE);
		switch (num) {
		case 1:
			img1.setVisibility(View.VISIBLE);			
			break;
		case 2:
			img2.setVisibility(View.VISIBLE);			
			break;
		case 3:
			img3.setVisibility(View.VISIBLE);			
			break;
		case 4:
			img4.setVisibility(View.VISIBLE);			
			break;
		case 5:
			img5.setVisibility(View.VISIBLE);			
			break;
		}
	}
	// 举报
	private void Report(final String title,final String type) {
		RequestParams params = new RequestParams();
		params.add("complaintId", groupId); //举报标题
		params.add("complaintTitle", title); //举报标题
		params.add("complaintDetail", repotContent); //举报内容
		params.add("complaintUserId", myId); //举报人ID
		params.add("complaintType", type); 
		APPRestClient.post(this, ServiceCode.COMPLAINT_USER, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "举报成功", 300).show();
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							Report(title, type);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ReportInterfaceActivity.this)
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
					}
					}
					@Override
					public void onFinish() {
					}
				});
	}
}
