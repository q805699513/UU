package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.ConsultEntity;
import com.uugty.uu.entity.ConsultEntity.Consult;
import com.uugty.uu.friendstask.FriendsDynamicActivity;
import com.uugty.uu.guide.LeadPageActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.util.UUConfig;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class ConsultFragment extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,
		OnItemClickListener, OnScrollListener {
	private View rootview;
	private Context context;
	private List<Consult> list = new ArrayList<Consult>();
	private ListView mListView;
	private TextView mFriend;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private ConsultAdapter adapter;
	private String city = "北京";
	private ImageView MapImageView;
	private int isFirst=0;//是否第一次加载
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ConsultEntity entity = (ConsultEntity) msg.getData()
					.getSerializable("ConsultEntity");
			if (entity != null) {
				List<Consult> result = (List<Consult>) entity.getLIST();
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					loadData(2);
					break;
				case 2:
					list.addAll(result);
					break;
				}
				adapter.notifyDataSetChanged();
			} else {
				list.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.context = getActivity();
		if (rootview == null) {

			rootview = inflater.inflate(R.layout.activity_consult_layout, null);

		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootview.getParent();

		if (parent != null) {
			parent.removeView(rootview);
		}
		return rootview;
	}

	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initGui();
		initAction();
	}


	protected void initGui() {
		// TODO Auto-generated method stub
		mListView = (ListView) rootview.findViewById(R.id.consult_listview);
		MapImageView=(ImageView) rootview.findViewById(R.id.consult_test);
		mFriend = (TextView) rootview.findViewById(R.id.consult_friend);
		mSwipeLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.consult_swipe_container);
		adapter = new ConsultAdapter(context, list);
		mListView.setAdapter(adapter);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 显示dialog
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.appstart.AppStartActivity");
				ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.guide.ImageGuideActivity");
			}
		}, 1000);
	}

	protected void initAction() {
		//引导页
				int myservices = SharedPreferenceUtil.getInstance(context)
						.getInt("myconsult", 0);
				if (myservices == 0) {
					Intent intent = new Intent();
					intent.putExtra("type", "咨询");
					intent.setClass(context,
							LeadPageActivity.class);
					startActivity(intent);
					SharedPreferenceUtil.getInstance(context).setInt("myconsult", 1);
				} 
		
		MapImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(MyApplication.getInstance().isLogin()){
					intent.setClass(context, MapActivity.class);
					startActivity(intent);
				}else{
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//					intent.putExtra("topage", ConsultFragment.class.getName());
					intent.setClass(context, LoginActivity.class);
					startActivity(intent);
				}
				
			}
		});
		
		mFriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, FriendsDynamicActivity.class);
				context.startActivity(intent);
			}
		});
		mListView.setOnItemClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		mListView.setOnScrollListener(this);
		mSwipeLayout.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(true);
			}
		});
		loadData(1);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (startId > 1) {
			if (firstVisibleItem == (startId - 1) * 5) {
				startId++;
				loadData(2);
			}
		}
	}

	private void loadData(final int what) {
		RequestParams params = new RequestParams();
		params.add("userCity", city); // 城市
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "10"); // pageSize
		APPRestClient
				.post(context, ServiceCode.QUERY_CONSULT_USERLIST, params,
						new APPResponseHandler<ConsultEntity>(
								ConsultEntity.class, context) {
							@Override
							public void onSuccess(ConsultEntity result) {
								if (null != result.getLIST()
										&& result.getLIST().size() > 0) {
									Message msg = Message.obtain();
									msg.what = what;
									Bundle b = new Bundle();
									b.putSerializable("ConsultEntity", result);
									msg.setData(b);
									handler.sendMessage(msg);
								} else {
									if (mSwipeLayout != null) {
										mSwipeLayout.setRefreshing(false);
									}
								}

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									loadData(what);
								} else {
									CustomToast.makeText(context, 0, errorMsg, 300)
											.show();
									mSwipeLayout.setRefreshing(false);
									mSwipeLayout.setVisibility(View.GONE);
									if (errorCode == -999) {
										new AlertDialog.Builder(
												context)
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent mintent = new Intent();
		// 聊天
		if (MyApplication.getInstance().isLogin()) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId().equals(list.get(position).getUserId())) {
				CustomDialog.Builder builder = new CustomDialog.Builder(
						context);
				builder.setMessage("这是你自己");
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else {
				mintent.putExtra("userId", list.get(position).getUserId());
				mintent.putExtra("avatar", list.get(position).getUserAvatar());
				mintent.putExtra("userName", list.get(position).getUserName());
				mintent.setClass(context, ChatActivity.class);
				startActivity(mintent);
			}
		} else {
			// 先登录
			mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			mintent.putExtra("topage", ConsultFragment.class.getName());
			mintent.setClass(context, LoginActivity.class);
			startActivity(mintent);

		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (UUConfig.INSTANCE.getmCtity() != null){
			city = UUConfig.INSTANCE.getmCtity();
		}
		if(isFirst != 0|| !"北京".equals(city)){
			isFirst = 1;
			onRefresh();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}

class ConsultAdapter extends BaseAdapter {
	private Context context;
	private List<Consult> ls;
	private LayoutInflater layoutInflater;

	public ConsultAdapter(Context context, List<Consult> ls) {
		super();
		this.context = context;
		this.ls = ls;
		this.layoutInflater=LayoutInflater.from(context);
	}

	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ls.get(position).getUserAvatar();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (view == null) {
			holder = new ViewHolder();
			view = layoutInflater.inflate(
					R.layout.consult_list_item_layout, null);
			holder.consult_person_avatar = (SimpleDraweeView) view
					.findViewById(R.id.consult_person_avatar);
			holder.consult_person_name = (TextView) view
					.findViewById(R.id.consult_person_name);
			holder.consult_person_state = (TextView) view
					.findViewById(R.id.consult_person_state);
			holder.consult_person_distance = (TextView) view
					.findViewById(R.id.consult_person_distance);
			holder.consult_person_truename = (TextView) view
					.findViewById(R.id.consult_person_truename);
			holder.consult_person_education = (TextView) view
					.findViewById(R.id.consult_person_education);
			holder.consult_person_drive = (TextView) view
					.findViewById(R.id.consult_person_drive);
			holder.consult_person_guide = (TextView) view
					.findViewById(R.id.consult_person_guide);
			holder.consult_line = (View) view.findViewById(R.id.consult_line);
			holder.consult_up_num = (TextView) view
					.findViewById(R.id.consult_up_num);
			holder.consult_upstate_img = (ImageView) view
					.findViewById(R.id.consult_upstate_img);
			holder.consult_tag_lin = (TextView) view
					.findViewById(R.id.consult_tag_lin);
			holder.simpleImage = (TextView) view.findViewById(R.id.framlayout_tv);
			holder.is_veru = (TextView) view.findViewById(R.id.consult_person_veru);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// 头像
		if (null != ls.get(position).getUserAvatar()
				&& !ls.get(position).getUserAvatar().equals("")) {
			holder.consult_person_avatar.setImageURI(Uri
					.parse(APPRestClient.SERVER_IP
							+ ls.get(position)
									.getUserAvatar()
									.substring(
											0,
											ls.get(position).getUserAvatar()
													.indexOf("."))
							+ "_ya"
							+ ls.get(position)
									.getUserAvatar()
									.substring(
											ls.get(position).getUserAvatar()
													.indexOf("."))));
			
		} else {
			holder.consult_person_avatar.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}
		// 灰色阴影
		if (ls.get(position).getIsOnline().equals("1")) {
			holder.simpleImage.setVisibility(View.GONE);
		} else {
			holder.simpleImage.setVisibility(View.VISIBLE);
		}
		//是否会员
		if (ls.get(position).getUserIsPromoter().equals("1")) {
			holder.is_veru.setVisibility(View.VISIBLE);
		} else {
			holder.is_veru.setVisibility(View.GONE);
		}
		// 姓名
		if(!ls.get(position).getUserName().equals("")){
			holder.consult_person_name.setText(ls.get(position).getUserName());
		}else{
			holder.consult_person_name.setText("小u");
		}
		// 实名
		if (ls.get(position).getUserIdValidate().equals("2")) {
			holder.consult_person_truename.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_truename.setVisibility(View.GONE);
		}
		// 导游
		if (ls.get(position).getUserTourValidate().equals("2")) {
			holder.consult_person_guide.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_guide.setVisibility(View.GONE);
		}
		// 驾驶
		if (ls.get(position).getUserCarValidate().equals("2")) {
			holder.consult_person_drive.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_drive.setVisibility(View.GONE);
		}
		// 学历
		if (ls.get(position).getUserCertificateValidate().equals("2")) {
			holder.consult_person_education.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_education.setVisibility(View.GONE);
		}
		holder.consult_person_distance.setVisibility(View.GONE);
		holder.consult_line.setVisibility(View.GONE);
		// 是否在线
		if (ls.get(position).getIsOnline().equals("1")) {
			holder.consult_person_state.setText("在线");
		} else if(ls.get(position).getIsOnline().equals("0")){
			holder.consult_person_state.setText("离线");
			//灰色头像尝试
//			Drawable drawable = holder.consult_person_avatar.getDrawable();
//			drawable.mutate();
//			drawable.clearColorFilter();
//			drawable.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
//			holder.consult_person_avatar.setBackground(drawable);
		}
		// 好评数
		if (ls.get(position).getFavourableCommentNum().equals("0")) {
			holder.consult_up_num.setText("暂无好评");
			holder.consult_upstate_img
					.setImageResource(R.drawable.consult_no_upstate);
		} else {
			holder.consult_up_num.setText(ls.get(position)
					.getFavourableCommentNum() + "人好评");
			holder.consult_upstate_img
					.setImageResource(R.drawable.consult_upstate);
		}
		// 标签
		if (!ls.get(position).getMarkContent().equals("")) {
			String label = ls.get(position).getMarkContent().replace(",", "  ");
			holder.consult_tag_lin.setText(label);
		} else {
			holder.consult_tag_lin.setText("暂无标签");
		}

		holder.consult_person_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("detailUserId", ls.get(position)
						.getUserId());
				intent.setClass(context, PersonCenterActivity.class);
				context.startActivity(intent);
			}
		});
		return view;
	}
	
	public final static float[] BT_SELECTED =  new   float [] {         
			    0.308f,  0.609f,  0.082f,  0 ,  0 ,       
			    0.308f,  0.609f,  0.082f,  0 ,  0 ,  
			    0.308f,  0.609f,  0.082f,  0 ,  0 ,  
			    0 ,  0 ,  0 ,  1 ,  0   
	};

	static class ViewHolder {
		private SimpleDraweeView consult_person_avatar;
		private TextView consult_person_name, consult_person_distance,
				consult_person_state, consult_person_truename,
				consult_person_education, consult_person_drive,
				consult_person_guide, consult_up_num,simpleImage,is_veru;
		private View consult_line;
		private ImageView consult_upstate_img;
		private TextView consult_tag_lin;
	}

}
