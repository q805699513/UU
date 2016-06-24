package com.uugty.uu.map;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.text.TextUtils;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mob.tools.utils.UIHandler;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.myview.WaveView;
import com.uugty.uu.common.share.onekeyshare.OnekeyShare;
import com.uugty.uu.common.share.onekeyshare.ShareContentCustomizeCallback;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.db.service.RoadLineService;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.GuideRouteEntity;
import com.uugty.uu.entity.RoadLineEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.VipEntity;

public class MyServicesActivity extends BaseActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,PlatformActionListener, Callback {

	private TopBackView titleView;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefresh;
	private MyServicesAdapter adapter;
	private List<RoadLineEntity> routeList = new ArrayList<RoadLineEntity>();
	private WaveView waveView;
	private Button btn;
	private FloatingActionButton addRouteImage;
	private static final int INITIAL_DELAY_MILLIS = 500;
	private RoadLineService roadLineService;
	private ArrayList<RoadLineEntity> dbRoadLineList;
	private View headerView;
	private boolean isVip = false;// 是否为vip true是，false
	
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_services;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.my_services_title);
		titleView.setTitle("我的小店");
		mListView = (ListView) findViewById(R.id.my_services_listview);
		mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.my_services_swipe_container);
		waveView = (WaveView) findViewById(R.id.my_services_no_data_rel);
		btn = (Button) findViewById(R.id.my_services_no_data_release_btn);
		addRouteImage = (FloatingActionButton) findViewById(R.id.my_floatactionBtn);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeRefresh.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		mSwipeRefresh.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeRefresh.setRefreshing(true);
			}
		});

		roadLineService = new RoadLineService(this);
		MyApplication.getInstance().setKilled(false);
		headerView = LayoutInflater.from(this).inflate(
				R.layout.my_services_listview_header, null);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		mSwipeRefresh.setOnRefreshListener(this);
		btn.setOnClickListener(this);
		addRouteImage.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Object object = parent.getAdapter().getItem(position);
				if (null != object) {
					RoadLineEntity entity = (RoadLineEntity) object;
					if (entity.getRoadlineStatus().equals("success")
							|| entity.getRoadlineStatus().equals("failure")) {
						Intent intent = new Intent();
						intent.putExtra("roadLineId", entity.getRoadlineId());
						intent.setClass(MyServicesActivity.this,
								PublishServicesActivity.class);
						startActivity(intent);
					} else if (entity.getRoadlineStatus().equals("edit")) {
						// 编辑中
						Intent intent = new Intent();
						intent.putExtra("roadLineId", entity.getRoadlineId());
						intent.putExtra("roadLineStuatus", "edit");
						intent.setClass(MyServicesActivity.this,
								PublishServicesActivity.class);
						startActivity(intent);
					} else {
						CustomToast.makeText(MyServicesActivity.this, 0,
								"该路线正在审核，不可编辑", 300).show();
					}
				}
			}
		});
		headerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MyServicesActivity.this, OpenShopActivity.class);
				Util.vipBack="myservices";
				startActivity(intent);
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// 查询数据库
		dbRoadLineList = roadLineService.queryAllRoadLine(MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId());
		// 查询是否为vip
		queryIsVip();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addRouteImage.setEnabled(true);

	}

	private void loadData(final int what) {
		routeList.clear();
		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.ROAD_LINE_LIST, params,
				new APPResponseHandler<GuideRouteEntity>(
						GuideRouteEntity.class, this) {
					@Override
					public void onSuccess(GuideRouteEntity result) {
						if (result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("guideRouteEntity",
									(Serializable) result.getLIST());
							msg.setData(b);
							handler.sendMessage(msg);
						} else if (result.getLIST().size() == 0
								&& dbRoadLineList.size() > 0) {
							Message message = new Message();
							message.what = 3;
							handler.sendMessage(message);
						} else {
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(1);
						} else {
							CustomToast.makeText(MyServicesActivity.this, 0,
									errorMsg, 300).show();
							mSwipeRefresh.setRefreshing(false);
							if (errorCode == -999) {
								new AlertDialog.Builder(MyServicesActivity.this)
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
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.map.PublishServicesActivity");
					}
				});
	}

	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mListView.setVisibility(View.VISIBLE);
				waveView.setVisibility(View.GONE);
				mSwipeRefresh.setVisibility(View.VISIBLE);
				mSwipeRefresh.setRefreshing(false);
				routeList = (List<RoadLineEntity>) msg.getData()
						.getSerializable("guideRouteEntity");
				routeList.addAll(dbRoadLineList);
				adapter = new MyServicesAdapter(MyServicesActivity.this,
						routeList);
				SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
						adapter);
				swingBottomInAnimationAdapter.setAbsListView(mListView);
				assert swingBottomInAnimationAdapter.getViewAnimator() != null;
				swingBottomInAnimationAdapter.getViewAnimator()
						.setInitialDelayMillis(INITIAL_DELAY_MILLIS);
				mListView.setAdapter(swingBottomInAnimationAdapter);
				break;
			case 2:
				mSwipeRefresh.setRefreshing(false);
				mListView.setVisibility(View.GONE);
				mSwipeRefresh.setVisibility(View.GONE);
				waveView.setVisibility(View.VISIBLE);
				break;
			case 3:
				mListView.setVisibility(View.VISIBLE);
				waveView.setVisibility(View.GONE);
				mSwipeRefresh.setVisibility(View.VISIBLE);
				mSwipeRefresh.setRefreshing(false);
				routeList.addAll(dbRoadLineList);
				adapter = new MyServicesAdapter(MyServicesActivity.this,
						routeList);
				SwingBottomInAnimationAdapter swingBottomInAnimationAdapter1 = new SwingBottomInAnimationAdapter(
						adapter);
				swingBottomInAnimationAdapter1.setAbsListView(mListView);
				assert swingBottomInAnimationAdapter1.getViewAnimator() != null;
				swingBottomInAnimationAdapter1.getViewAnimator()
						.setInitialDelayMillis(INITIAL_DELAY_MILLIS);
				mListView.setAdapter(swingBottomInAnimationAdapter1);
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		dbRoadLineList = roadLineService.queryAllRoadLine(MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId());
		loadData(1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_floatactionBtn:
			// 调用路线发布权限接口
			addRouteImage.setEnabled(false);
			setPermissionRequest();
			break;
		case R.id.my_services_no_data_release_btn:
			setPermissionRequest();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		mSwipeRefresh.setRefreshing(true);
		mListView.setVisibility(View.GONE);
		dbRoadLineList = roadLineService.queryAllRoadLine(MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId());
		queryIsVip();
		//loadData(1);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);
	}

	private void setPermissionRequest() {

		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.ROAD_LINE_PUBLISH_PERMISSION,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						this) {
					@Override
					public void onSuccess(BaseEntity result) {
						// 继续往下走
						Intent intent = new Intent();
						if (isVip) {
							intent.setClass(MyServicesActivity.this,
									PublishServicesActivity.class);
						} else {
							intent.setClass(MyServicesActivity.this,
									OpenShopActivity.class);
							Util.vipBack="myservices";
						}
						startActivity(intent);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							setPermissionRequest();
						} else {
							CustomToast.makeText(MyServicesActivity.this, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(MyServicesActivity.this)
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
							} else {
								new AlertDialog.Builder(MyServicesActivity.this)
										.setTitle("提示")
										.setMessage(errorMsg)
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

	private void queryIsVip() {
		RequestParams params = new RequestParams();
		APPRestClient.post(this, APPRestClient.BASE_URL
				+ ServiceCode.QUERY_IS_VIP, params, true,
				new APPResponseHandler<VipEntity>(VipEntity.class, this) {
					@Override
					public void onSuccess(VipEntity result) {
						if (null != result.getOBJECT()) {
							if (result.getOBJECT().getUserIsPromoter()
									.equals("1")) {
								isVip = true;
							} else {
								mListView.removeHeaderView(headerView);
								mListView.addHeaderView(headerView);
							}
						}
						loadData(1);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							queryIsVip();
						} else {
							CustomToast.makeText(MyServicesActivity.this, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(MyServicesActivity.this)
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

	class MyServicesAdapter extends BaseAdapter implements
			PlatformActionListener {
		private Context context;
		private List<RoadLineEntity> routeList1;
		private LayoutInflater inflater;

		public MyServicesAdapter(Context context, List<RoadLineEntity> routeList) {
			this.context = context;
			this.routeList1 = routeList;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return routeList1.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return routeList1.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.my_services_listview_item, null);
				holder.imageView = (SimpleDraweeView) convertView
						.findViewById(R.id.my_services_listview_item_image);
				holder.titleText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_name);
				holder.statusText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_status);
				holder.timeText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_time);
				holder.priceText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_price);
				holder.deleteImage = (ImageView) convertView
						.findViewById(R.id.my_services_delete);
				holder.shareImage = (ImageView) convertView
						.findViewById(R.id.my_services_share);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.setBackgroundResource(R.drawable.list_item_bg);
			holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ "images/roadlineDescribe/"
					+ routeList1.get(position).getRoadlineBackground()));
			if (TextUtils.isEmpty(routeList1.get(position).getRoadlineTitle())) {
				holder.titleText.setText("还没有填写标题");
			} else {
				holder.titleText.setText(routeList1.get(position)
						.getRoadlineTitle());
			}

			if (!routeList1.get(position).getRoadlineStatus().equals("")) {
				if (routeList1.get(position).getRoadlineStatus()
						.equals("review")) {
					holder.statusText.setText("正在提交");
					holder.deleteImage.setVisibility(View.GONE);
					holder.shareImage.setVisibility(View.GONE);
				}
				if (routeList1.get(position).getRoadlineStatus()
						.equals("success")) {
					holder.statusText.setText("已发布");
					holder.deleteImage.setVisibility(View.VISIBLE);
					holder.shareImage.setVisibility(View.VISIBLE);
				}
				if (routeList1.get(position).getRoadlineStatus()
						.equals("failure")) {
					holder.statusText.setText("已下架");
					holder.deleteImage.setVisibility(View.VISIBLE);
					holder.shareImage.setVisibility(View.GONE);
				}
				if (routeList1.get(position).getRoadlineStatus().equals("edit")) {
					holder.statusText.setText("编辑中");
					holder.deleteImage.setVisibility(View.VISIBLE);
					holder.shareImage.setVisibility(View.GONE);
				}
			}
			holder.timeText.setText(routeList1.get(position)
					.getRoadlineCreateDate());
			if (TextUtils.isEmpty(routeList1.get(position).getRoadlinePrice())) {
				holder.priceText.setText("0.00");
			} else {
				holder.priceText.setText(routeList1.get(position)
						.getRoadlinePrice());
			}
			holder.shareImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showShare(routeList1.get(position));
				}
			});
			holder.deleteImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CustomDialog.Builder builder1 = new CustomDialog.Builder(
							context);
					builder1.setMessage("确定删除吗?");
					builder1.setTitle("提示");
					builder1.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (routeList1.get(position)
											.getRoadlineStatus().equals("edit")) {
										// 删除数据库
										roadLineService.deleteData(routeList1
												.get(position).getRoadlineId());
										routeList1.remove(position);
										notifyDataSetChanged();
										if (routeList1.size() == 0) {
											onRefresh();
										}
									} else {
										deleteServices(routeList1.get(position)
												.getRoadlineId(), position);
									}

									dialog.dismiss();
								}
							});

					builder1.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder1.create().show();

				}
			});
			return convertView;
		}

		private void deleteServices(final String roadLineId, final int position) {
			RequestParams params = new RequestParams();
			params.add("roadlineId", roadLineId); // 用户ID

			APPRestClient.post(context, ServiceCode.ROAD_LINE_DELETE, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class,
							context) {
						@Override
						public void onSuccess(BaseEntity result) {
							routeList1.remove(position);
							notifyDataSetChanged();
							if (routeList1.size() == 0) {
								onRefresh();
							}
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								deleteServices(roadLineId, position);
							} else {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
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
							}
						}

						@Override
						public void onFinish() {
						}
					});
		}

		class ViewHolder {
			SimpleDraweeView imageView;
			TextView titleText, statusText, timeText, priceText;
			ImageView deleteImage, shareImage;
		}

		String bgaddress = "";
		String shareUrl = "";
		String wxShareUrl = "";

		// 分享
		private void showShare(final RoadLineEntity entity) {
			Util.sharWXType = "share";
			shareUrl = "http://www.uugty.com/uuapplication/wxprojectbendi/html/fx.html?roadlineId="
					+ entity.getRoadlineId();
			if (null != MyApplication.getInstance().getUserInfo())
				wxShareUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf6c597932d583ce0&redirect_uri=http://www.uugty.com/uuapplication/wxRedictUrl.do?url=http://www.uugty.com/uuapplication/wxprojectbendi/html/join_uu_roaddetail_load.html?promoteUserId="
						+ MyApplication.getInstance().getUserInfo().getOBJECT()
								.getUserId()
						+ "??roadlineId="
						+ entity.getRoadlineId()
						+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";

			if (entity.getRoadlineBackground().contains(".")) {
				bgaddress = (entity.getRoadlineBackground()).substring(0,
						(entity.getRoadlineBackground()).lastIndexOf("."))
						+ "_ya.png";
			} else {
				bgaddress = entity.getRoadlineBackground() + "_ya.png";
			}
			ShareSDK.initSDK(context);
			OnekeyShare oks = new OnekeyShare();
			// 关闭sso授权
			oks.disableSSOWhenAuthorize();

			// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
			// oks.setNotification(R.drawable.ic_launcher,
			// getString(R.string.app_name));
			// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
			oks.setTitle(MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserName());
			// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			oks.setTitleUrl(shareUrl);
			// text是分享文本，所有平台都需要这个字段
			// oks.setText("我是分享文本http://www.baidu.com"); //新浪微博链接
			oks.setText(entity.getRoadlineTitle());
			oks.setImageUrl(APPRestClient.SERVER_IP
					+ "images/roadlineBackgroud/" + bgaddress);

			// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
			// oks.setImagePath("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");//
			// 确保SDcard下面存在此张图片
			// url仅在微信（包括好友和朋友圈）中使用
			oks.setUrl(wxShareUrl);
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			// site是分享此内容的网站名称，仅在QQ空间使用
			oks.setSite(context.getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
			oks.setSiteUrl(shareUrl);
			oks.setSilent(true); // 隐藏编辑页面
			oks.setCallback(this);
			// 此处为本demo关键为一键分享折子定义分享回调函数 shareContentCustomuzeCallback
			// 自定义平台可以通过判断不同的平台来实现不同平台间的不同操作
			oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
				// 自定义分享的回调想要函数
				@Override
				public void onShare(Platform platform,
						final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
					// 点击微信好友
					if ("Wechat".equals(platform.getName())) {
						// 微信分享应用 ,此功能需要微信绕开审核，需要使用项目中的wechatdemo.keystore进行签名打包
						// 由于Onekeyshare没有关于应用分享的参数如setShareType等，我们需要通过自定义
						// 分享来实现
						// 比如下面设置了setTitle,可以覆盖oks.setTitle里面的title值
						paramsToShare.setTitle(MyApplication.getInstance()
								.getUserInfo().getOBJECT().getUserName());
						paramsToShare.setText(entity.getRoadlineTitle());

//						paramsToShare.setImageUrl(APPRestClient.SERVER_IP
//								+ "images/roadlineBackgroud/" + bgaddress);
//						
//						//取不到图片使用默认logo
//						if(paramsToShare.getImageData() == null){
//							BitmapDrawable d = new BitmapDrawable(getResources().openRawResource(R.drawable.app_icon));
//							paramsToShare.setImageData(d.getBitmap());
//						}
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									URL urlStr = new URL(APPRestClient.SERVER_IP
											+ "images/roadlineBackgroud/" + bgaddress);
									HttpURLConnection connection = (HttpURLConnection) urlStr.openConnection();  
									int state = connection.getResponseCode();  
									if (state == 200) {  
										paramsToShare.setImageUrl(APPRestClient.SERVER_IP
												+ "images/roadlineBackgroud/" + bgaddress);
									} else {
										// 取不到图片使用默认logo
										if (paramsToShare.getImageData() == null) {
											BitmapDrawable d = new BitmapDrawable(getResources().openRawResource( + R.drawable.app_icon));
											paramsToShare.setImageData(d.getBitmap());
										}
									}
									paramsToShare.setUrl(wxShareUrl);
									paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
								} catch (Exception e) {
									e.printStackTrace();;
								}  
							}
						}).start();

					}
					// 点击朋友圈
					if ("WechatMoments".equals(platform.getName())) {
						// 微信分享应用 ,此功能需要微信绕开审核，需要使用项目中的wechatdemo.keystore进行签名打包
						// 由于Onekeyshare没有关于应用分享的参数如setShareType等，我们需要通过自定义
						// 分享来实现
						// 比如下面设置了setTitle,可以覆盖oks.setTitle里面的title值
						paramsToShare.setTitle(entity.getRoadlineTitle());
						paramsToShare.setText(entity.getRoadlineTitle());
//						paramsToShare.setImageUrl(APPRestClient.SERVER_IP
//								+ "images/roadlineBackgroud/" + bgaddress);
//						
//						//取不到图片使用默认logo
//						if(paramsToShare.getImageData() == null){
//							BitmapDrawable d = new BitmapDrawable(getResources().openRawResource(R.drawable.app_icon));
//							paramsToShare.setImageData(d.getBitmap());
//						}

						new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									URL urlStr = new URL(APPRestClient.SERVER_IP
											+ "images/roadlineBackgroud/" + bgaddress);
									HttpURLConnection connection = (HttpURLConnection) urlStr.openConnection();  
									int state = connection.getResponseCode();  
									if (state == 200) {  
										paramsToShare.setImageUrl(APPRestClient.SERVER_IP
												+ "images/roadlineBackgroud/" + bgaddress);
									} else {
										// 取不到图片使用默认logo
										if (paramsToShare.getImageData() == null) {
											BitmapDrawable d = new BitmapDrawable(getResources().openRawResource(+ R.drawable.app_icon));
											paramsToShare.setImageData(d.getBitmap());
										}
									}
									paramsToShare.setUrl(wxShareUrl);
									paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
								} catch (Exception e) {
									e.printStackTrace();;
								}  
							}
						}).start();
					}
					// 点击新浪微博
					if ("SinaWeibo".equals(platform.getName())) {
						paramsToShare.setText(entity.getRoadlineTitle() + shareUrl);
						paramsToShare.setShareType(Platform.SHARE_WEBPAGE);// 一定要设置分享属性
						paramsToShare.setImagePath("");
						paramsToShare.setImageUrl(APPRestClient.SERVER_IP
								+ "images/roadlineBackgroud/" + bgaddress);

						 // 限制微博分享的文字不能超过20
//						 if (paramsToShare.getComment().length() > 20) {
//						  
//						  Toast.makeText(FindTestViewPagerActivity.this,
//						  "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//						  
//						  } 
					}
				}
			});
			// 启动分享GUI
			oks.show(context);
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			// TODO Auto-generated method stub

		}
	}

	//设置监听http://sharesdk.cn/androidDoc/cn/sharesdk/framework/PlatformActionListener.html 

    //监听是子线程，不能Toast，要用handler处理，不要犯这么二的错误
	//Setting listener, http://sharesdk.cn/androidDoc/cn/sharesdk/framework/PlatformActionListener.html 

	//The listener is the child-thread that can not handle ui
	@Override
	public void onCancel(Platform platform, int action) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);		
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case MSG_TOAST: {
			String text = String.valueOf(msg.obj);
			Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
		}
		break;
		case MSG_ACTION_CCALLBACK: {
			switch (msg.arg1) {
				case 1: { // 成功, successful notification
					showNotification(2000, "分享成功");
				}
				break;
				case 2: { // 失败, fail notification
					showNotification(2000, "分享完成");
				}
				break;
				case 3: { // 取消, cancel notification
					showNotification(2000, "取消分享");
				}
				break;
			}
		}
		break;
		case MSG_CANCEL_NOTIFY: {
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null) {
				nm.cancel(msg.arg1);
			}
		}
		break;
	}
	return false;
	}

	// 在状态栏提示分享操作,the notification on the status bar
	private void showNotification(long cancelTime, String text) {
		try {
			Context app = getApplicationContext();
			NotificationManager nm = (NotificationManager) app
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);

			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.app_icon, text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(), 0);
			notification.setLatestEventInfo(app, "uu客", text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);

			if (cancelTime > 0) {
				Message msg = new Message();
				msg.what = MSG_CANCEL_NOTIFY;
				msg.obj = nm;
				msg.arg1 = id;
				UIHandler.sendMessageDelayed(msg, cancelTime, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
