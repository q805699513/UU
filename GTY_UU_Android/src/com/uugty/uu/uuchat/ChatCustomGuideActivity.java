package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.myview.UnderLineTextAndImage;
import com.uugty.uu.entity.ChatCustomEntity;


public class ChatCustomGuideActivity extends BaseActivity implements
		OnClickListener {

	private TopBackView titleView;
	private UnderLineTextAndImage chat_custom_guide_name,
			chat_custom_guide_phone, chat_custom_guide_starting,
			chat_custom_guide_destination, chat_custom_guide_starting_time,
			chat_custom_guide_travel_days, chat_custom_guide_people_nums,
			chat_custom_guide_price_num, chat_custom_guide_label;
	private TextView chat_costom_guide_need_edt;
	private String customId = "",customUserId="";
	private LinearLayout contactLin;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_chatcustom_guide;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (getIntent() != null) {
			customId = getIntent().getStringExtra("customId");
			customUserId = getIntent().getStringExtra("customUserId");
		}
		titleView = (TopBackView) findViewById(R.id.chat_custom_guide_title);
		titleView.setTitle("定制需求");
		chat_custom_guide_name = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_name);
		chat_custom_guide_phone = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_phone);
		chat_custom_guide_starting = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_starting);
		chat_custom_guide_destination = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_destination);
		chat_custom_guide_starting_time = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_starting_time);
		chat_custom_guide_travel_days = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_travel_days);
		chat_custom_guide_people_nums = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_people_nums);
		chat_custom_guide_price_num = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_price_num);
		chat_custom_guide_label = (UnderLineTextAndImage) findViewById(R.id.chat_custom_guide_label);
		chat_costom_guide_need_edt = (TextView) findViewById(R.id.chat_costom_guide_need_edt);
		contactLin = (LinearLayout) findViewById(R.id.chat_custom_contact_lin);
		demandDetail(customId);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		chat_custom_guide_name.setLeftText("姓名");
		chat_custom_guide_name.hideLine();
		chat_custom_guide_phone.setLeftText("手机号");
		chat_custom_guide_phone.hideLine();
		chat_custom_guide_starting.setLeftText("出发地");
		chat_custom_guide_starting.hideLine();
		chat_custom_guide_destination.setLeftText("目的地");
		chat_custom_guide_destination.hideLine();
		chat_custom_guide_starting_time.setLeftText("出发时间");
		chat_custom_guide_starting_time.hideLine();
		chat_custom_guide_travel_days.setLeftText("旅游时间");
		chat_custom_guide_travel_days.hideLine();
		chat_custom_guide_people_nums.setLeftText("出行人数");
		chat_custom_guide_people_nums.hideLine();
		chat_custom_guide_price_num.setLeftText("预算");
		chat_custom_guide_price_num.hideLine();
		chat_custom_guide_label.setLeftText("标签");
		chat_custom_guide_label.hideLine();
		contactLin.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
      if(!TextUtils.isEmpty(customUserId)&&customUserId.equals("requirementpublisher")){
    	  contactLin.setVisibility(View.VISIBLE);
      }else{
    	  contactLin.setVisibility(View.GONE);
      }
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ChatCustomEntity result = (ChatCustomEntity) msg.getData()
						.getSerializable("demandDtail");
				if (result != null) {
					customUserId = result.getOBJECT().getCustomUserId();
					chat_custom_guide_name.setRightTextRight(result.getOBJECT()
							.getCustomRealName());
					chat_custom_guide_phone
							.setRightTextRight(replaceSubString(result
									.getOBJECT().getCustomTel()));
					chat_custom_guide_starting.setRightTextRight(result
							.getOBJECT().getCustomPlaceOfDeparture());
					chat_custom_guide_destination.setRightTextRight(result
							.getOBJECT().getCustomDestination());
					chat_custom_guide_starting_time.setRightTextRight(result
							.getOBJECT().getCustomStartingTime());
					chat_custom_guide_travel_days.setRightTextRight(result
							.getOBJECT().getCustomTravelTime() + "天");
					chat_custom_guide_people_nums.setRightTextRight(result
							.getOBJECT().getCustomTravelNum() + "人");
					chat_custom_guide_price_num.setRightTextRight("￥"
							+ result.getOBJECT().getCustomBudget() + "元");
					chat_custom_guide_label.setRightTextRight(result
							.getOBJECT().getCustomMark());
					if (!result.getOBJECT().getCustomDemandContent().equals("")) {
						chat_costom_guide_need_edt.setText(result.getOBJECT()
								.getCustomDemandContent());
					} else {
						chat_costom_guide_need_edt.setText("他没有填写其他需求");
					}
				}
				break;

			default:
				break;
			}
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_custom_contact_lin:
			//跳转聊天
			ChatActivity.activityInstance.finish();
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("userId", customUserId);
			startActivity(intent);
			break;

		default:
			break;
		}
	};

	// 打码
	public String replaceSubString(String str) {
		if (str != null && str.length() >= 11) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (i > 2 && i <= 6) {
					sb.append('*');
				} else {
					sb.append(c);
				}
			}
			str = sb.toString();
		}
		return str;
	}

	// 需求详情
	private void demandDetail(final String customId) {
		RequestParams params = new RequestParams();
		params.add("customId", customId);
		APPRestClient.post(this, ServiceCode.QUERY_TRAVEL_CUSTOM, params,
				new APPResponseHandler<ChatCustomEntity>(
						ChatCustomEntity.class, this) {
					@Override
					public void onSuccess(ChatCustomEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("demandDtail", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							demandDetail(customId);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										ChatCustomGuideActivity.this)
										.setTitle("提示")
										.setMessage("服务器连接失败！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
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
