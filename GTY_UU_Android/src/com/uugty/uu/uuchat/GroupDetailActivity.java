package com.uugty.uu.uuchat;

import android.text.TextUtils;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.easemob.chat.EMChatManager;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.myview.UserLineTextAndImage;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.GroupChatDetailEntity;
import com.uugty.uu.entity.GroupMemberEntity;
import com.uugty.uu.entity.GroupMemberEntity.GroupMember;
import com.uugty.uu.login.LoginActivity;

public class GroupDetailActivity extends BaseActivity implements
		OnClickListener {

	private String groupId, groupName;
	private TopBackView titleView;
	private CirculHeadImage groupAvatar;
	private TextView groupNameTextView, groupContentTextView, groupNumTextView;
	private UserLineTextAndImage reportView, clearView, exitView;
	private SpotsDialog loadingDialog;
	private GridView groupGrideView;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.group_chat_detail;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			groupId = getIntent().getStringExtra("groupId");
			groupName = getIntent().getStringExtra("groupName");
		}
		titleView = (TopBackView) findViewById(R.id.group_chat_detail_title);
		groupAvatar = (CirculHeadImage) findViewById(R.id.group_chat_detail_avatar);
		groupNameTextView = (TextView) findViewById(R.id.group_chat_detail_name);
		groupContentTextView = (TextView) findViewById(R.id.group_chat_detail_content);
		groupNumTextView = (TextView) findViewById(R.id.group_chat_detail_num);
		reportView = (UserLineTextAndImage) findViewById(R.id.group_chat_detail_report);
		clearView = (UserLineTextAndImage) findViewById(R.id.group_chat_detail_clear);
		exitView = (UserLineTextAndImage) findViewById(R.id.group_chat_detail_exit);
		groupGrideView = (GridView) findViewById(R.id.group_chat_detail_grideview);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		reportView.setOnClickListener(this);
		clearView.setOnClickListener(this);
		exitView.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		titleView.setTitle(groupName);
		groupNameTextView.setText(groupName);
		reportView.setLedtText("举报");
		clearView.setLedtText("清空聊天记录");
		exitView.setLedtText("退出");
		reportView.setImageViewGone();
		clearView.setImageViewGone();
		exitView.setImageViewGone();
		getGroupDeatilRequest();
	}

	private void getGroupDeatilRequest() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}
		RequestParams params = new RequestParams();
		params.add("groupEasemobID", groupId);
		APPRestClient.post(this, ServiceCode.GROUP_DETAIL_INFO, params,
				new APPResponseHandler<GroupChatDetailEntity>(
						GroupChatDetailEntity.class, GroupDetailActivity.this) {
					@Override
					public void onSuccess(GroupChatDetailEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("GroupChatDetailEntity", result);
						msg.setData(b);
						handler.sendMessage(msg);
						getGroupMemberRequest();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getGroupDeatilRequest();
						} else {
						if (null != loadingDialog)
							loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(GroupDetailActivity.this)
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
					}}
				});

	}

	// 成员接口
	private void getGroupMemberRequest() {
		RequestParams params = new RequestParams();
		params.add("groupEasemobID", groupId);
		params.add("currentPage", "1");
		params.add("pageSize", "10");
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_MEMBERS, params,
				new APPResponseHandler<GroupMemberEntity>(
						GroupMemberEntity.class, GroupDetailActivity.this) {
					@Override
					public void onSuccess(GroupMemberEntity result) {
						Message msg = Message.obtain();
						msg.what = 2;
						Bundle b = new Bundle();
						b.putSerializable("GroupMemberEntity", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getGroupMemberRequest();
						} else {
						if (null != loadingDialog)
							loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(GroupDetailActivity.this)
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
					}}
				});

	}

	// 退群
	private void exitGroupRequest() {
		RequestParams params = new RequestParams();
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					GroupDetailActivity.class.getName());
			intent.setClass(GroupDetailActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		params.add("userId", MyApplication.getInstance().getUserInfo()
				.getOBJECT().getUserId());
		}
		params.add("groupEasemobID", groupId);
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_EXIT, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class,
						GroupDetailActivity.this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "成功退出该群", 300).show();
						EMChatManager.getInstance().clearConversation(
								groupId);
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							exitGroupRequest();
						} else {
						if (null != loadingDialog)
							loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(GroupDetailActivity.this)
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
					}}
				});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				GroupChatDetailEntity entity = (GroupChatDetailEntity) msg
						.getData().getSerializable("GroupChatDetailEntity");
				groupAvatar.setHeadPic(entity.getOBJECT().getGroupImages(), "net");
				groupContentTextView.setText(entity.getOBJECT()
						.getGroupContent());
				groupNumTextView.setText(entity.getOBJECT().getGroupUserCount()
						+ "人");
				break;
			case 2:
				loadingDialog.dismiss();
				GroupMemberEntity groupMemberEntity = (GroupMemberEntity) msg
						.getData().getSerializable("GroupMemberEntity");
				if (groupMemberEntity.getLIST().size() > 0) {
					groupGrideView.setAdapter(new GroupAdapter(
							groupMemberEntity.getLIST(),
							GroupDetailActivity.this));
				}

				break;
			}

			super.handleMessage(msg);
		};
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.group_chat_detail_clear:
			// 清空缓存
			CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
			builder.setMessage("确定删除此群的聊天记录吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							EMChatManager.getInstance().clearConversation(
									groupId);
							CustomToast.makeText(ctx, 0, "清除成功", 300).show();
							dialog.dismiss();
						}
					});

			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
			break;
		case R.id.group_chat_detail_exit:
			// 清空缓存
			CustomDialog.Builder builder1 = new CustomDialog.Builder(ctx);
			builder1.setMessage("确定退出此群吗?");
			builder1.setTitle("提示");
			builder1.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityCollector
									.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupChatListActivity");
							ActivityCollector
									.removeSpecifiedActivity("com.uugty.uu.uuchat.ChatActivity");
							exitGroupRequest();
							dialog.dismiss();

						}
					});

			builder1.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder1.create().show();
			break;
		case R.id.group_chat_detail_report:
			Intent intent =new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("saidId", groupId);
			intent.putExtra("type", "2");
			intent.setClass(ctx, ReportInterfaceActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	};
}

class GroupAdapter extends BaseAdapter {
	private List<GroupMember> list;
	private Context context;

	public GroupAdapter(List<GroupMember> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.group_chat_grideview_item, null);
			holder.img = (CirculHeadImage) view
					.findViewById(R.id.group_chat_grideview_item_image);
			holder.text = (TextView) view
					.findViewById(R.id.group_chat_grideview_item_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.img.setHeadPic(list.get(position).getUserAvatar(), "net");
		if (!TextUtils.isEmpty(list.get(position).getUserName())) {
			holder.text.setText(list.get(position).getUserName());
		} else {
			holder.text.setText("小u");
		}
		return view;

	}

	static class ViewHolder {
		CirculHeadImage img;
		TextView text;
	}
}
