package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.UUMessage;
import com.uugty.uu.entity.UUMessage.DetailModeal;
import com.uugty.uu.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatFriendsActivity extends BaseActivity implements
		OnClickListener {

	private ListView wish_lsit;
	private List<DetailModeal> lsitmessage;
	private WishFriendAdapter adapter;
	private RelativeLayout wishFrame2;
	private Button dataBtn;
	private TopBackView topTitle;
	private String toForm, forward_msg_id;
	private SpotsDialog loadingDialog;

	//分享路线
	private String roadLineId;
	private String roadLineImg;
	private String roadLineTitle;
	private String roadLinePrice;


	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_wishtwo;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			toForm = getIntent().getStringExtra("toForm");
			forward_msg_id = getIntent().getStringExtra("forward_msg_id");
			roadLineId = getIntent().getStringExtra("roadlineId");
			roadLineImg = getIntent().getStringExtra("roadlineImg");
			roadLineTitle = getIntent().getStringExtra("roadlineTitle");
			roadLinePrice = getIntent().getStringExtra("roadlinePrice");
		}
		topTitle = (TopBackView) findViewById(R.id.chat_friends_title);
		topTitle.setTitle("我的好友");
		wish_lsit = (ListView) findViewById(R.id.activity_wish_listview_two);
		lsitmessage = new ArrayList<UUMessage.DetailModeal>();
		wishFrame2 = (RelativeLayout) findViewById(R.id.wish_second_no_data_rel);
		dataBtn = (Button) findViewById(R.id.wish_second_no_data_btn);
		dataBtn.setOnClickListener(this);
		loadData();

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		wish_lsit.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(toForm) && "ChatActivity".equals(toForm)) {

					CustomDialog.Builder builder = new CustomDialog.Builder(
							ChatFriendsActivity.this);
					builder.setMessage("确定要给"
							+ lsitmessage.get(position).getUserName() + "发送吗");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {
									dialog.dismiss();
									ChatActivity.activityInstance.finish();
									forwardInfor(new Intent(),
											lsitmessage.get(position)
													.getUserId(), lsitmessage
													.get(position)
													.getUserAvatar());
								}
							});
					builder.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();

				} else {
					Intent intent = new Intent();
					intent.putExtra("userId", lsitmessage.get(position)
							.getUserId());
					intent.putExtra("avatar", lsitmessage.get(position)
							.getUserAvatar());
					intent.putExtra("userName", lsitmessage.get(position)
							.getUserName());
					if(null != roadLineId) {
						intent.putExtra("toFrom", "friendShare");
						intent.putExtra("roadlineId", roadLineId);
						intent.putExtra("roadlineImg", roadLineImg);
						intent.putExtra("roadlineTitle", roadLineTitle);
						intent.putExtra("roadlinePrice", roadLinePrice);
					}
					intent.setClass(ChatFriendsActivity.this,
							ChatActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.wish_second_no_data_btn:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("toPage", "order");
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	protected void forwardInfor(Intent intent, String userId, String forwar_id) {
		// TODO Auto-generated method stub
		intent.putExtra("userId", userId);
		if("true".equals(getIntent().getStringExtra("isShare"))){
			intent.putExtra("toFrom","friendShare");
			intent.putExtra("roadlineId",roadLineId);
			intent.putExtra("roadlineImg",roadLineImg);
			intent.putExtra("roadlineTitle",roadLineTitle);
			intent.putExtra("roadlinePrice",roadLinePrice);
		}
		intent.putExtra("forward_msg_id", forward_msg_id);
		intent.setClass(ChatFriendsActivity.this, ChatActivity.class);
		startActivity(intent);
	}

	private void loadData() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}
		RequestParams request = new RequestParams();
		APPRestClient.post(this, ServiceCode.UU_FRIEND_LIST, request,
				new APPResponseHandler<UUMessage>(UUMessage.class, this) {
					@Override
					public void onSuccess(UUMessage result) {
						// TODO Auto-generated method stub
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("uuMessage", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						// TODO Auto-generated method stub

						// 显示dialog
						loadingDialog.dismiss();

					}}
				});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				new Handler().postDelayed(new Runnable() {
					public void run() {
						// 显示dialog
						if(!ChatFriendsActivity.this.isFinishing())
						loadingDialog.dismiss();
					}
				}, 500);
				UUMessage result = (UUMessage) msg.getData().getSerializable(
						"uuMessage");
				if (null != result && result.getLIST().size() > 0) {
					lsitmessage = result.getLIST();
					adapter = new WishFriendAdapter(ChatFriendsActivity.this,
							lsitmessage);
					wish_lsit.setAdapter(adapter);
					wishFrame2.setVisibility(View.GONE);
					wish_lsit.setVisibility(View.VISIBLE);
				} else {
					wishFrame2.setVisibility(View.VISIBLE);
					wish_lsit.setVisibility(View.GONE);
				}
				break;
			}
		};
	};
}

class WishFriendAdapter extends BaseAdapter {
	private List<DetailModeal> list;
	private Context context;
	private int mFocus = 0;

	public WishFriendAdapter(Context context, List<DetailModeal> list) {
		this.list = list;
		this.context = context;
	}

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewManger viewmanger = null;
		if (convertView == null) {
			viewmanger = new ViewManger();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.wish_mangertwo, null);
			viewmanger.wishimg = (SimpleDraweeView) convertView
					.findViewById(R.id.wish_chat_image);
			viewmanger.wishtitle = (TextView) convertView
					.findViewById(R.id.wish_chat_name);
			viewmanger.wishcityname = (TextView) convertView
					.findViewById(R.id.wish_city_name);
			viewmanger.wish_mangertwo_linearla = (LinearLayout) convertView
					.findViewById(R.id.wish_mangertwo_linearla);
			viewmanger.isFocus = (TextView) convertView
					.findViewById(R.id.friend_isfocus);
			convertView.setTag(viewmanger);
		} else {
			viewmanger = (ViewManger) convertView.getTag();
		}
		viewmanger.wish_mangertwo_linearla
				.setBackgroundResource(R.drawable.list_item_bg);
		viewmanger.wishimg.setImageURI(Uri.parse(APPRestClient.SERVER_IP + list.get(position).getUserAvatar()));
		if (list.get(position).getUserName().equals("")
				|| list.get(position).getUserName().equals(null)) {
			viewmanger.wishtitle.setText("小u");
		} else {
			viewmanger.wishtitle.setText(list.get(position).getUserName());
		}
		if (list.get(position).getUserCity().equals("")
				|| list.get(position).getUserCity().equals(null)) {
		} else {
			viewmanger.wishcityname.setText(list.get(position).getUserCity());
		}
		viewmanger.isFocus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(mFocus == 0){
					mFocus = 1;
					friendsRequest(v,false,list.get(position).getUserId());
				}else{
					mFocus = 0;
					friendsRequest(v,true,list.get(position).getUserId());
				}
			}
		});

		return convertView;
	}

	private void friendsRequest(final View view, final boolean isFocus, final String detailUserId) {
		String post="";
		RequestParams params = new RequestParams();
		params.add("friendId", detailUserId);
		if(isFocus){
			post = ServiceCode.ADD_FRIENDS;
		}else{
			post = ServiceCode.DELETE_FRIENDS;
		}
		APPRestClient.post(context, post, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, context) {
					@Override
					public void onSuccess(BaseEntity result) {
						if(isFocus) {
							view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lzh_friend_close));
							CustomToast.makeText(context, 0, "添加关注成功", 300).show();
						}else{
							view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lzh_friend_focus));
							CustomToast.makeText(context, 0, "取消关注成功", 300).show();
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							friendsRequest(view,isFocus,detailUserId);
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
										.setTitle("提示")
										.setMessage("网络拥堵,请稍后重试！")
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

	static class ViewManger {
		SimpleDraweeView wishimg;
		TextView wishtitle;
		TextView wishcityname;
		TextView isFocus;
		LinearLayout wish_mangertwo_linearla;
	}
}
