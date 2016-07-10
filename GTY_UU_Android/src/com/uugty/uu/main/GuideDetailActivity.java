package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.SearchPopuWindow;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.CollectRoadLineEntity;
import com.uugty.uu.entity.GuideEntity;
import com.uugty.uu.entity.GuideEntity.GuideDetail;
import com.uugty.uu.guide.LeadPageActivity;

import java.util.ArrayList;
import java.util.List;


public class GuideDetailActivity extends BaseActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,
		OnItemClickListener, OnScrollListener {

	private ListView mListView;
	private int startId = 1;// 起始页页
	private TextView titleText;
	private ImageButton screenText;
	private String title, content;
	private ImageView backImage;
	private SwipeRefreshLayout mSwipeLayout;
	private List<GuideDetail> guideDetailList = new ArrayList<GuideDetail>();
	private GuideDetailAdapter adapter;
	private String online ="",sex = "", car = "", guide = "", sort = "",markContent;
	// 空页面
	private RelativeLayout guideDetailRel;
	private Button guideNodataBtn;
	private static final int INITIAL_DELAY_MILLIS = 500;
	private DrawerLayout mDrawerLayout;
	private RadioGroup onlineGroup,sexGroup, carGroup, guideGroup, sortGroup;
	private TextView commitBtn, clear;
	private String type="";

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
					"GuideEntity");
			if (entity != null) {
				List<GuideDetail> result = (List<GuideDetail>) entity.getLIST();
				switch (msg.what) {
				case 1:
					guideDetailList.clear();
					guideDetailList.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					mListView.setVisibility(View.VISIBLE);
					loadData(2, false);
					break;
				case 2:
					guideDetailList.addAll(result);
					break;
				}
				adapter.notifyDataSetChanged();
			} else {
				guideDetailList.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};
	Handler selecthandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				onlineGroup.clearCheck();
				sexGroup.clearCheck();
				carGroup.clearCheck();
				guideGroup.clearCheck();
				sortGroup.clearCheck();
				online ="";
				sex = "";
				car = "";
				guide = "";
				sort = "";
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.guide_detail;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			title = getIntent().getStringExtra("city");
			content = getIntent().getStringExtra("content");
			markContent = getIntent().getStringExtra("markContent");			
		}
		if(null!=getIntent().getStringExtra("type")){
			type = getIntent().getStringExtra("type");
		}
		mListView = (ListView) findViewById(R.id.guide_detail_listview);
		mListView.setVisibility(View.INVISIBLE);
		titleText = (TextView) findViewById(R.id.guide_detail_title);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.guide_swipe_container);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
		// 筛选按钮
		screenText = (ImageButton) findViewById(R.id.guide_detail_screen);

		backImage = (ImageView) findViewById(R.id.guide_detail_right_backpwd);
		guideDetailRel = (RelativeLayout) findViewById(R.id.guide_detail_no_data_rel);
		guideNodataBtn = (Button) findViewById(R.id.guide_detail_no_data_release_btn);
		titleText.setText(title);

		onlineGroup = (RadioGroup) findViewById(R.id.guide_screen_online);
		sexGroup = (RadioGroup) findViewById(R.id.guide_screen_sex);
		carGroup = (RadioGroup) findViewById(R.id.guide_screen_sex_car);
		guideGroup = (RadioGroup) findViewById(R.id.guide_screen_sex_car_guide);
		sortGroup = (RadioGroup) findViewById(R.id.guide_screen_sex_car_sort);
		commitBtn = (TextView) findViewById(R.id.guide_screen_btn);
		clear = (TextView) findViewById(R.id.guide_screen_clear_btn);

		adapter = new GuideDetailAdapter(this, guideDetailList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
				INITIAL_DELAY_MILLIS);

		mListView.setAdapter(swingBottomInAnimationAdapter);
	}

	@Override
	protected void initAction() {
		if(type.equals("体验")){
			//引导页
			int myservices = SharedPreferenceUtil.getInstance(ctx)
					.getInt("myexperience", 0);
			if (myservices == 0) {
				Intent intent = new Intent();
				intent.putExtra("type", type);
				intent.setClass(GuideDetailActivity.this,
						LeadPageActivity.class);
				startActivity(intent);
				SharedPreferenceUtil.getInstance(ctx).setInt("myexperience", 1);
			} 
		}else if(type.equals("特色")){
			//引导页
			int myservices = SharedPreferenceUtil.getInstance(ctx)
					.getInt("mycharacteristic", 0);
			if (myservices == 0) {
				Intent intent = new Intent();
				intent.putExtra("type", type);
				intent.setClass(GuideDetailActivity.this,
						LeadPageActivity.class);
				startActivity(intent);
				SharedPreferenceUtil.getInstance(ctx).setInt("mycharacteristic", 1);
			} 
		}
		
		backImage.setOnClickListener(this);
		screenText.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		guideNodataBtn.setOnClickListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
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
		loadData(1, true);

		onlineGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				// int radioButtonId = group.getCheckedRadioButtonId();
				if (checkedId != -1) {
					RadioButton rb = (RadioButton) GuideDetailActivity.this
							.findViewById(checkedId);
					online = (String) rb.getTag();
				}

			}
		});
		
		
		sexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				// int radioButtonId = group.getCheckedRadioButtonId();
				if (checkedId != -1) {
					RadioButton rb = (RadioButton) GuideDetailActivity.this
							.findViewById(checkedId);
					sex = (String) rb.getTag();
				}

			}
		});
		carGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId != -1) {
					RadioButton rb = (RadioButton) GuideDetailActivity.this
							.findViewById(checkedId);
					car = (String) rb.getTag();
				}
			}
		});
		guideGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1) {
					RadioButton rb = (RadioButton) GuideDetailActivity.this
							.findViewById(checkedId);
					guide = (String) rb.getTag();
				}

			}
		});
		sortGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId != -1) {
					RadioButton rb = (RadioButton) GuideDetailActivity.this
							.findViewById(checkedId);
					sort = (String) rb.getTag();
				}
			}
		});

		commitBtn.setOnClickListener(this);
		clear.setOnClickListener(this);
		mDrawerLayout
				.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);
						mListView.setEnabled(true);
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						mListView.setEnabled(false);
					}
				});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ActivityCollector
		.removeSpecifiedActivity("com.uugty.uu.common.myview.SearchPopuWindow");
		Intent intent = new Intent();
		intent.putExtra("roadId", guideDetailList.get(position).getRoadlineId());
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setClass(this, FindTestViewPagerActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
				mDrawerLayout.closeDrawers();
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.guide_detail_right_backpwd:
			finish();
			break;
		case R.id.guide_detail_screen:
			if (mSwipeLayout.isRefreshing()) {
				mSwipeLayout.setRefreshing(false);
			}
			// 弹出侧拉栏
			if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
				mDrawerLayout.closeDrawers();
			} else {
				mDrawerLayout.openDrawer(GravityCompat.END);
			}
			break;
		case R.id.guide_detail_no_data_release_btn:
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(this, SearchPopuWindow.class);
			startActivity(intent);
			break;
		case R.id.guide_screen_btn:
			startId = 1;
			loadData(1, false);
			mDrawerLayout.closeDrawers();
			break;
		case R.id.guide_screen_clear_btn:
			selecthandler.sendMessage(selecthandler.obtainMessage(1));
			break;
		default:
			break;
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(getIntent()!=null){
		title=intent.getStringExtra("city");
		content=intent.getStringExtra("content");
		titleText.setText(title);
		loadData(1, false);
		}
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		guideDetailList.clear();
	}

	private void loadData(final int what, final boolean msg) {
		RequestParams params = new RequestParams();
		params.add("markSearchType", content); // 当前页数
		params.add("markTitle", title); // pageSize
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		params.add("isOnline", online); // 性别
		params.add("userSex", sex); // 性别
		params.add("userTourValidate", guide); // 用户的旅游证
		params.add("userCarValidate", car); // 用户的车

		params.add("sort", sort); // 排序
		params.add("markContent", markContent); //城市
		

		APPRestClient.post(this, ServiceCode.ROAD_LINE_SEARCH, params,
				new APPResponseHandler<GuideEntity>(GuideEntity.class, this) {
					@Override
					public void onSuccess(GuideEntity result) {
						/*
						 * Message msg = handler.obtainMessage(); msg.what =
						 * what; msg.obj = result.getLIST();
						 * handler.sendMessage(msg);
						 */
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("GuideEntity", result);
							msg.setData(b);
							handler.sendMessage(msg);
							mSwipeLayout.setVisibility(View.VISIBLE);
							guideDetailRel.setVisibility(View.GONE);
						} else {
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							if (startId == 1) {
								mSwipeLayout.setVisibility(View.GONE);
								guideDetailRel.setVisibility(View.VISIBLE);
							}
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						mSwipeLayout.setRefreshing(false);
						mSwipeLayout.setVisibility(View.GONE);
						if (errorCode == -999) {
							new AlertDialog.Builder(GuideDetailActivity.this)
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (startId > 1) {
			if (firstVisibleItem == (startId - 1) * 5) {
				startId++;
				loadData(2, false);
			}
		}
	}	
}

class GuideDetailAdapter extends BaseAdapter {
	private List<GuideDetail> ls;
	private Context context;

	public GuideDetailAdapter(Context context, List<GuideDetail> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.guide_detail_listview_item, null);
			holder.imageView = (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_item_image);
			holder.headImage = (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_item_headImage);
			holder.priceText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_price);
			holder.titleText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_title);
			holder.orderNumText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_num_text);
			holder.lookNumText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_look_num_text);
			holder.newImageView = (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_new_route_image);
			holder.onlineImageView =  (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_online_route_image);
			holder.collect_image =  (ImageView) convertView
					.findViewById(R.id.find_route_display_route_collect_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.collect_image.setVisibility(View.VISIBLE);
		if(MyApplication.getInstance().isLogin()){
			if(!ls.get(position).getCollectId().equals("0")){
				holder.collect_image.setImageResource(R.drawable.home_page_collected_img);									
			}else{					
				holder.collect_image.setImageResource(R.drawable.home_page_default_collect_img);					
			}
		}else{			
		holder.collect_image.setImageResource(R.drawable.home_page_default_collect_img);
		}
		if (!ls.get(position).getRoadlineBackground().equals("")) {
			if (ls.get(position).getRoadlineBackground().contains("images")) {
				holder.imageView.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP
								+ ls.get(position).getRoadlineBackground()));
			} else {
				holder.imageView.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP + "images/roadlineDescribe/"
								+ (ls.get(position).getRoadlineBackground())));
				
			}
		} else {
			// 加载默认图片
			holder.imageView.setImageURI(Uri.parse("res:///"
					+ R.drawable.uu_default_image_one));
		}
		holder.priceText.setText(ls.get(position).getRoadlinePrice());
		if (!TextUtils.isEmpty(ls.get(position).getIsNew())
				&& ls.get(position).getIsNew().equals("1")) {
			holder.newImageView.setVisibility(View.VISIBLE);
		} else {
			holder.newImageView.setVisibility(View.GONE);
		}if(!TextUtils.isEmpty(ls.get(position).getIsOnline())&& ls.get(position).getIsOnline().equals("1")){
			holder.onlineImageView.setVisibility(View.VISIBLE);
		}else {
			holder.onlineImageView.setVisibility(View.GONE);
		}
		holder.titleText.setText(ls.get(position).getRoadlineTitle());
		holder.orderNumText.setText(ls.get(position).getOrderCount());
		if (null != ls.get(position).getLineNum()
				&& !ls.get(position).getLineNum().equals("")) {
			holder.lookNumText.setText(ls.get(position).getLineNum());
		} else {
			holder.lookNumText.setText("0");
		}
		if (null != ls.get(position).getUserAvatar()
				&& !ls.get(position).getUserAvatar().equals("")) {
			holder.headImage.setImageURI(Uri
					.parse(APPRestClient.SERVER_IP + ls.get(position).getUserAvatar()));
			holder.headImage.setImageURI(Uri
					.parse(APPRestClient.SERVER_IP+ls.get(position).getUserAvatar().substring(0, ls.get(position).getUserAvatar().indexOf("."))
							+ "_ya"
							+ ls.get(position).getUserAvatar().substring(ls.get(position).getUserAvatar().indexOf("."))));
		} else {
			holder.headImage.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}
		return convertView;
	}
	// 取消收藏
			private void sendCollectCancleRequest(final String id) {
				RequestParams params = new RequestParams();
				params.add("collectId", id);// 路线Id
				APPRestClient.post(context, ServiceCode.COLLECT_CANCEL_ROAD_LINE, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class, context) {
							@Override
							public void onSuccess(BaseEntity result) {
								notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									sendCollectCancleRequest(id);
								} else {
								CustomToast.makeText(context, 0, errorMsg, 300).show();
								if (errorCode == -999) {
									new AlertDialog.Builder(
											context)
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
							}}

							@Override
							public void onFinish() {

							}
						});

			}
		private void sendCollectRequest(final String id,final int position) {
			RequestParams params = new RequestParams();
			params.add("collectRoadlineId", id);// 路线Id
			params.add("collectTitle", "");// 路线Id
			APPRestClient.post(context, ServiceCode.COLLECT_ROAD_LINE, params,
					new APPResponseHandler<CollectRoadLineEntity>(
							CollectRoadLineEntity.class, context) {
						@Override
						public void onSuccess(CollectRoadLineEntity result) {
							ls.get(position).setCollectId(result.getOBJECT().getCollectId());
							notifyDataSetChanged();
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								sendCollectRequest(id, position);
							} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										context)
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
	static class ViewHolder {
		SimpleDraweeView imageView, newImageView,onlineImageView,headImage;
		TextView priceText, titleText, orderNumText, lookNumText;
		ImageView collect_image;
	}

}
