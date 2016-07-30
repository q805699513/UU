package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;

public class GroupEntryActivity extends BaseActivity {

	private TopBackView titleView;
	private String groupName,groupAvatar,groupContent,groupNum,groupId;
	private CirculHeadImage avatar;
	private TextView groupNameText,groupContentText,groupDetailText,groupNumText;
	private Button btn;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.group_entry;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null!=getIntent()){
			groupName = getIntent().getStringExtra("groupName");
			groupAvatar = getIntent().getStringExtra("groupAvatar");
			groupContent = getIntent().getStringExtra("groupContent");
			groupNum= getIntent().getStringExtra("groupNum");
			groupId =getIntent().getStringExtra("groupId");
		}
		titleView = (TopBackView) findViewById(R.id.group_entry_title);
		avatar = (CirculHeadImage) findViewById(R.id.group_entry_avatar_image);
		groupNameText = (TextView) findViewById(R.id.group_entry_name);
		groupContentText = (TextView) findViewById(R.id.group_entry_content);
		groupDetailText=(TextView) findViewById(R.id.group_entry_detail_content);
		groupNumText = (TextView) findViewById(R.id.group_entry_num);
		btn = (Button) findViewById(R.id.group_entry_apply_btn);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getApplyEnterGroup();
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		titleView.setTitle(groupName);
		avatar.setHeadPic(groupAvatar, "net");
		groupNameText.setText(groupName);
		groupContentText.setText(groupContent);
		groupDetailText.setText(groupContent);
		groupNumText.setText(groupNum+"人");
	}

	
	//申请入群
	private void getApplyEnterGroup() {
		RequestParams params = new RequestParams();
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					GroupEntryActivity.class.getName());
			intent.setClass(GroupEntryActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		params.add("userId", MyApplication.getInstance().getUserInfo().getOBJECT().getUserId());
		}
		params.add("groupEasemobID",groupId);
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_ADD, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class,
						this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(GroupEntryActivity.this, 0,
								"申请成功", 300).show();
						ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupChatSearchActivity");
						ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupChatListActivity");
						Intent intent = new Intent();
						intent.setClass(GroupEntryActivity.this, GroupChatListActivity.class);
						startActivity(intent);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getApplyEnterGroup();
						} else {
						CustomToast.makeText(GroupEntryActivity.this, 0,
								errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(
									GroupEntryActivity.this)
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
