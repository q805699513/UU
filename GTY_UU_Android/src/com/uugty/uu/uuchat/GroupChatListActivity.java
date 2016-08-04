package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.GroupChatEntity;
import com.uugty.uu.entity.GroupChatEntity.GroupChat;
import com.uugty.uu.login.LoginActivity;

import java.util.List;

public class GroupChatListActivity extends BaseActivity implements
		OnClickListener {

	private TopBackView groupTitle;
	private SpotsDialog loadingDialog;
	private LinearLayout noDataLin, guideChatLin, fatherLin, searchLin;
	private Button noDataBtn;
	private TextView searchView,createView;
	private float y;
	private ListView groupChatListView;
	private GroupChatListViewAdapter adapter;
	private List<GroupChat> groupList;

	//分享路线
	private String roadLineId;
	private String roadLineImg;
	private String roadLineTitle;
	private String roadLinePrice;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.group_chat_view;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			roadLineId = getIntent().getStringExtra("roadlineId");
			roadLineImg = getIntent().getStringExtra("roadlineImg");
			roadLineTitle = getIntent().getStringExtra("roadlineTitle");
			roadLinePrice = getIntent().getStringExtra("roadlinePrice");
		}
		groupTitle = (TopBackView) findViewById(R.id.group_chat_title);
		groupTitle.setTitle("群聊");
		noDataLin = (LinearLayout) findViewById(R.id.guide_chate_no_data_lin);
		guideChatLin = (LinearLayout) findViewById(R.id.group_chat_list_lin);
		noDataBtn = (Button) findViewById(R.id.group_chat_no_data_btn);
		searchView = (TextView) findViewById(R.id.group_chat_search);
		fatherLin = (LinearLayout) findViewById(R.id.group_chat_view);
		searchLin = (LinearLayout) findViewById(R.id.group_chat_search_lin);
		groupChatListView = (ListView) findViewById(R.id.group_chat_list);
		createView = (TextView) findViewById(R.id.group_chat_list_create);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		noDataBtn.setOnClickListener(this);
		searchView.setOnClickListener(this);
		createView.setOnClickListener(this);
		groupChatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GroupChatListActivity.this,
						ChatActivity.class);
				// it is group chat
				intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				intent.putExtra("userId", groupList.get(position)
						.getGroupEasemobID());
				if(null != roadLineId) {
					intent.putExtra("toFrom", "friendShare");
					intent.putExtra("roadlineId", roadLineId);
					intent.putExtra("roadlineImg", roadLineImg);
					intent.putExtra("roadlineTitle", roadLineTitle);
					intent.putExtra("roadlinePrice", roadLinePrice);
				}
				startActivity(intent);
			}

		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		roadLineId = null;
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		getGrouupInfoRequest();
	}

	// 路线信息
	private void getGrouupInfoRequest() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					GroupChatListActivity.class.getName());
			intent.setClass(GroupChatListActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		params.add("userId", MyApplication.getInstance().getUserInfo()
				.getOBJECT().getUserId());
		}
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_LIST, params,
				new APPResponseHandler<GroupChatEntity>(GroupChatEntity.class,
						this) {
					@Override
					public void onSuccess(GroupChatEntity result) {
						loadingDialog.dismiss();
						if (result.getLIST().size() > 0) {
							groupList = result.getLIST();
							adapter = new GroupChatListViewAdapter(
									GroupChatListActivity.this, groupList);
							groupChatListView.setAdapter(adapter);
							noDataLin.setVisibility(View.GONE);
							guideChatLin.setVisibility(View.VISIBLE);

						} else {
							noDataLin.setVisibility(View.VISIBLE);
							guideChatLin.setVisibility(View.GONE);
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getGrouupInfoRequest();
						} else {
						if (null != loadingDialog)
							loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(GroupChatListActivity.this)
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
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				});

	}
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.group_chat_no_data_btn:
			Intent intent = new Intent();
			intent.putExtra("from", "noData");
			intent.setClass(this, GroupChatSearchActivity.class);
			startActivity(intent);
			break;
		case R.id.group_chat_search:
			y = searchLin.getY() + dp2px(50);
			TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -y);
			animation.setDuration(500);
			animation.setFillAfter(true);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					Intent intent = new Intent();
					intent.setClass(GroupChatListActivity.this,
							GroupChatSearchActivity.class);
					startActivityForResult(intent, 100);
					overridePendingTransition(R.anim.group_chat_search_out,
							R.anim.group_chat_search_in);
				}
			});
			fatherLin.startAnimation(animation);
			break;
		case R.id.group_chat_list_create:
			Intent createIntent = new Intent();
			createIntent.setClass(this, GroupChatCreateActivity.class);
			startActivity(createIntent);
			createView.setEnabled(false);
			break;
			
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		createView.setEnabled(true);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, -y, 0);
		animation.setDuration(500);
		animation.setFillAfter(true);
		fatherLin.startAnimation(animation);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				new Handler().postDelayed(new Runnable() {
					public void run() {
						// 显示dialog
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupEntryActivity");
						ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupChatCreateActivity");
					}
				}, 300);

				break;
			}

			super.handleMessage(msg);
		};
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}

class GroupChatListViewAdapter extends BaseAdapter {
	private List<GroupChat> ls;
	private Context context;

	// private Animation animation;
	// private Map<Integer, Boolean> isFrist;

	public GroupChatListViewAdapter(Context context, List<GroupChat> list) {
		this.ls = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return ls.size();
	}

	@Override
	public Object getItem(int position) {
		return ls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.group_chat_listview_item, null);
			holder.user_image = (CirculHeadImage) convertView
					.findViewById(R.id.group_chat_avatar_image);
			holder.group_name = (TextView) convertView
					.findViewById(R.id.group_chat_name);
			holder.end_content = (TextView) convertView
					.findViewById(R.id.group_chat_content);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setBackgroundResource(R.drawable.list_item_bg);
		holder.user_image.setHeadPic(ls.get(position).getGroupImages(), "net");
		holder.group_name.setText(ls.get(position).getGroupName());
		holder.end_content.setText(ls.get(position).getGroupContent());
		// 最后一条消息，时间，未读消息

		return convertView;
	}

	static class ViewHolder {
		CirculHeadImage user_image;// 用户头像
		TextView group_name;// 群名称
		TextView end_content;// 最后一条消息展示
	}

}
