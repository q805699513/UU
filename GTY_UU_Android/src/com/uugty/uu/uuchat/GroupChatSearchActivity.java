package com.uugty.uu.uuchat;

import java.util.List;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.GroupChatEntity;
import com.uugty.uu.entity.GroupChatEntity.GroupChat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class GroupChatSearchActivity extends Activity {

	private EditText searchEditText;
	private TextView cancleTextView;
	private String from;
	private List<GroupChat> groupList;
	private GroupChatHotListViewAdapter adapter;
	private ListView searchListView;
	private SpotsDialog loadingDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_serach_view);
		searchEditText = (EditText) findViewById(R.id.group_chat_search);
		cancleTextView = (TextView) findViewById(R.id.group_chat_search_cancle);
		searchListView = (ListView) findViewById(R.id.group_chat_search_listview);
		
		ActivityCollector.addActivity(this);
		if (null != getIntent()) {
			from = getIntent().getStringExtra("from");
		}
		if (null != from &&from.equals("noData")) {
			initAction();
		} else {
			getKeyboard();
			initAction();
		}

	}

	private void initAction() {
		cancleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_OK);
				finish();
			}
		});

		getHotGroupInfoRequest();
		
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("groupName", groupList.get(position).getGroupName());
				intent.putExtra("groupAvatar", groupList.get(position).getGroupImages());
				intent.putExtra("groupContent", groupList.get(position).getGroupContent());
				intent.putExtra("groupNum", groupList.get(position).getGroupUserCount());
				intent.putExtra("groupId", groupList.get(position).getGroupEasemobID());
				intent.setClass(GroupChatSearchActivity.this, GroupEntryActivity.class);
				startActivity(intent);
			}
		});
		
		
		searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				final String searchStr = searchEditText.getText().toString().trim();
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					// do something;
					if (null != searchStr && !"".equals(searchStr)) {
						// 发送请求
						// 调用接口
						searchGroupRequest(searchEditText.getText().toString().trim());
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						return true;
					}
					return false;
				}
				return false;
			}
		});
	}

	private void getKeyboard() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				searchEditText.setFocusable(true);
				searchEditText.setFocusableInTouchMode(true);
				InputMethodManager inputManager = (InputMethodManager) searchEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 300);
	}

	// 路线信息
	private void getHotGroupInfoRequest() {
		

		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_HOT, params,
				new APPResponseHandler<GroupChatEntity>(GroupChatEntity.class,
						this) {
					@Override
					public void onSuccess(GroupChatEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("GroupChatEntity", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getHotGroupInfoRequest();
						} else {
						CustomToast.makeText(GroupChatSearchActivity.this, 0,
								errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(
									GroupChatSearchActivity.this)
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
	
	// 路线信息
		private void searchGroupRequest(final String groupName) {
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
			}

			RequestParams params = new RequestParams();
			params.add("groupName", groupName);
			APPRestClient.post(this, ServiceCode.GROUP_CHAT_SEARCH, params,
					new APPResponseHandler<GroupChatEntity>(GroupChatEntity.class,
							this) {
						@Override
						public void onSuccess(GroupChatEntity result) {
							loadingDialog.dismiss();
							Message msg = Message.obtain();
							msg.what = 2;
							Bundle b = new Bundle();
							b.putSerializable("GroupChatEntity", result);
							msg.setData(b);
							handler.sendMessage(msg);
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								searchGroupRequest(groupName);
							} else {
							if (null != loadingDialog)
								loadingDialog.dismiss();
							CustomToast.makeText(GroupChatSearchActivity.this, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										GroupChatSearchActivity.this)
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

						@Override
						public void onFinish() {

						}
					});

		}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				GroupChatEntity entity = (GroupChatEntity) msg
						.getData().getSerializable("GroupChatEntity");
				if(entity.getLIST().size()>0){
					groupList = entity.getLIST();
					adapter = new GroupChatHotListViewAdapter(
							GroupChatSearchActivity.this, groupList);
					searchListView.setAdapter(adapter);
				}
				break;
			case 2:
				GroupChatEntity chatEntity = (GroupChatEntity) msg
						.getData().getSerializable("GroupChatEntity");
					groupList.clear();
					groupList.addAll(chatEntity.getLIST());
					adapter.notifyDataSetChanged();
				
				break;
			}

			super.handleMessage(msg);
		};
	};
}

class GroupChatHotListViewAdapter extends BaseAdapter {
	private List<GroupChat> ls;
	private Context context;

	// private Animation animation;
	// private Map<Integer, Boolean> isFrist;

	public GroupChatHotListViewAdapter(Context context, List<GroupChat> list) {
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
