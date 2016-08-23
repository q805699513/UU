package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
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
import com.uugty.uu.guide.LeadPageActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class ConsultActivity extends BaseActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,
		OnItemClickListener, OnScrollListener {

	private List<Consult> list = new ArrayList<Consult>();
	private ListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private ConsultAdapter adapter;
	private String city = "北京";
	private ImageView MapImageView,backImg;
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

	@Override
	protected int getContentLayout() {
		return R.layout.activity_consult_layout;
	}


	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent().getStringExtra("city")) {
			city = getIntent().getStringExtra("city");
		}
		mListView = (ListView) findViewById(R.id.consult_listview);
		MapImageView=(ImageView) findViewById(R.id.consult_test);
		backImg = (ImageView) findViewById(R.id.consult_back);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.consult_swipe_container);
		adapter = new ConsultAdapter(this, list);
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

	@Override
	protected void initAction() {
		//引导页
				int myservices = SharedPreferenceUtil.getInstance(this)
						.getInt("myconsult", 0);
				if (myservices == 0) {
					Intent intent = new Intent();
					intent.putExtra("type", "咨询");
					intent.setClass(this,
							LeadPageActivity.class);
					startActivity(intent);
					SharedPreferenceUtil.getInstance(this).setInt("myconsult", 1);
				}
		backImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		MapImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(MyApplication.getInstance().isLogin()){
					intent.setClass(ConsultActivity.this, MapActivity.class);
					startActivity(intent);
				}else{
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage", ConsultActivity.class.getName());
					intent.setClass(ConsultActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				
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
	protected void initData() {
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

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
				.post(this, ServiceCode.QUERY_CONSULT_USERLIST, params,
						new APPResponseHandler<ConsultEntity>(
								ConsultEntity.class, this) {
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
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									mSwipeLayout.setRefreshing(false);
									mSwipeLayout.setVisibility(View.GONE);
									if (errorCode == -999) {
										new AlertDialog.Builder(
												ctx)
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
						ctx);
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
				mintent.setClass(ConsultActivity.this, ChatActivity.class);
				startActivity(mintent);
			}
		} else {
			// 先登录
			mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			mintent.putExtra("topage", ConsultActivity.class.getName());
			mintent.setClass(ConsultActivity.this, LoginActivity.class);
			startActivity(mintent);

		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
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


