package com.uugty.uu.friendstask;

import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.IsCollecteEntity;
import com.uugty.uu.entity.IsCollecteEntity.IsCollct;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.uuchat.ReportInterfaceActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class RightReportPopupWindow extends PopupWindow {
    private Activity context;
	private View mMenuView;
	private TextView txt_one,txt_two;
	private String saidId,type,IsCollect;
	
	
	public String getIsCollect() {
		return IsCollect;
	}

	public void setIsCollect(String isCollect) {
		IsCollect = isCollect;
	}

	public String getSaidId() {
		return saidId;
	}

	public void setSaidId(String saidId) {
		this.saidId = saidId;		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RightReportPopupWindow(Activity context) {
		super(context);
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.right_report_popup_window, null);

		 txt_one=(TextView) mMenuView.findViewById(R.id.right_dynamic_report_rel);//举报
		 txt_two=(TextView) mMenuView.findViewById(R.id.right_dynamic_collect_rel);//收藏
		txt_one.setOnClickListener(new Report());
		txt_two.setOnClickListener(new Collect());
//		if(getIsCollect().equals("0")){
//			txt_two.setText("收藏");
//		}else{
//			txt_two.setText("取消收藏");			
//		}
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth((int) (w /3.5));
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		//this.setAnimationStyle(R.style.mystyle);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}
	
	//举报
		private class Report implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				if (MyApplication.getInstance().isLogin()) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("saidId", getSaidId());
					intent.putExtra("type", getType());
					intent.setClass(context, ReportInterfaceActivity.class);
					context.startActivity(intent);
				}else{
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage", MainActivity.class.getName());
					intent.setClass(context, LoginActivity.class);
					context.startActivity(intent);
				}
				dismiss();
			}
			
		}
		//收藏
		private class Collect implements OnClickListener{

			@Override
			public void onClick(View v) {
				IsCollect(saidId);
				dismiss();
			}
			
		}
		public void upCollect(String id){
			queryCollected(id);		
		}
		// 是否收藏
		public void IsCollect(final String saidId) {
			RequestParams params = new RequestParams();
			params.add("saidId", saidId); // 
			APPRestClient.post(context, ServiceCode.COLLECT_FROENDSAID, params,
					new APPResponseHandler<IsCollecteEntity>(IsCollecteEntity.class,
							context) {
						@Override
						public void onSuccess(IsCollecteEntity result) {
							if(result.getOBJECT().getSaidIsCollect().equals("0")){
								CustomToast.makeText(context, 0, "取消收藏", 300).show();
							}else{
								CustomToast.makeText(context, 0, "收藏成功", 300).show();
							}
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								IsCollect(saidId);
							} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														onFinish();
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
		// 查询状态
		private void queryCollected(final String id) {
			RequestParams params = new RequestParams();
			params.add("saidId", id);
			APPRestClient.post(context, ServiceCode.QUERY_COLLECT_FRIENDSAID, params,
					new APPResponseHandler<IsCollecteEntity>(IsCollecteEntity.class,
							context) {
						@Override
						public void onSuccess(IsCollecteEntity result) {
							if(result.getOBJECT().getSaidIsCollect().equals("0")){
								txt_two.setText("收藏");	
							}else{
								txt_two.setText("取消收藏");
							}							
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								queryCollected(id);
							} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														onFinish();
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
