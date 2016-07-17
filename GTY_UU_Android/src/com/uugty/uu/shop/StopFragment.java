package com.uugty.uu.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.WaveView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.db.service.RoadLineService;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.GuideRouteEntity;
import com.uugty.uu.entity.RoadLineEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.VipEntity;
import com.uugty.uu.map.OpenShopActivity;
import com.uugty.uu.map.PublishServicesActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopFragment extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

	private View view;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefresh;
	private MyServicesAdapter adapter;
	private List<RoadLineEntity> routeList = new ArrayList<RoadLineEntity>();
	private WaveView waveView;
	private Button btn;
	private static final int INITIAL_DELAY_MILLIS = 500;
	private RoadLineService roadLineService;
	private ArrayList<RoadLineEntity> dbRoadLineList;
	private View headerView;
	private boolean isVip = false;// 是否为vip true是，false
	private int startId = 1;// 起始页页
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.activity_my_services, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initGui();
		initAction();
		initData();

	}

	protected void initGui() {

		mListView = (ListView) view.findViewById(R.id.my_services_listview);
		mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.my_services_swipe_container);
		waveView = (WaveView) view.findViewById(R.id.my_services_no_data_rel);
		btn = (Button) view.findViewById(R.id.my_services_no_data_release_btn);
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

		roadLineService = new RoadLineService(getActivity());
		MyApplication.getInstance().setKilled(false);
		headerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.my_services_listview_header, null);
	}

	protected void initAction() {
		// TODO Auto-generated method stub
		mSwipeRefresh.setOnRefreshListener(this);
		btn.setOnClickListener(this);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
									final int position, long id) {
				// TODO Auto-generated method stub

				Object object = parent.getAdapter().getItem(position);
				if (null != object) {
					final RoadLineEntity entity = (RoadLineEntity) object;
					if(entity.getRoadlineId() != null){
						Intent intent = new Intent();
						if("edit".equals(entity.getRoadlineStatus()) && roadLineService.selectRoutLine(entity.getRoadlineId()) != null) {
							CustomDialog.Builder builder1 = new CustomDialog.Builder(
									getActivity());
							builder1.setMessage("草稿箱里有内容未发布是否继续编辑草稿");
							builder1.setRelationShip(true);
							builder1.setPositiveButton("继续编辑",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int which) {
											// 编辑中
											Intent intent = new Intent();
											intent.putExtra("roadLineId", entity.getRoadlineId());
											intent.putExtra("roadLineStuatus", "edit");
											intent.setClass(getActivity(),
													PublishServicesActivity.class);
											startActivity(intent);
											dialog.dismiss();
										}
									});

							builder1.setNegativeButton(
									"发表新内容",
									new android.content.DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int which) {
											roadLineService.deleteData(entity.getRoadlineId());
											Intent intent = new Intent();
											intent.setClass(getActivity(),
													PublishServicesActivity.class);
											startActivity(intent);
											adapter.routeList1.remove(position);
											adapter.notifyDataSetChanged();
											dialog.dismiss();
										}
									});

							builder1.create().show();

						}else{
							intent.putExtra("roadId", entity.getRoadlineId());
							intent.setClass(getActivity(),
									FindTestViewPagerActivity.class);
							startActivity(intent);
						}
					}

//					if ("success".equals(entity.getRoadlineStatus())
//							|| "failure".equals(entity.getRoadlineStatus())) {
//						Intent intent = new Intent();
//						intent.putExtra("roadId", entity.getRoadlineId());
//						intent.setClass(getActivity(),
//								FindTestViewPagerActivity.class);
//						startActivity(intent);
//					} else if ("edit".equals(entity.getRoadlineStatus())) {
//						// 编辑中
//						CustomDialog.Builder builder1 = new CustomDialog.Builder(
//								getActivity());
//						builder1.setMessage("草稿箱里有内容未发布是否继续编辑草稿");
//						builder1.setRelationShip(true);
//						builder1.setPositiveButton("继续编辑",
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//														int which) {
//										Intent intent = new Intent();
//										intent.putExtra("roadLineId", entity.getRoadlineId());
//										intent.putExtra("roadLineStuatus", "edit");
//										intent.setClass(getActivity(),
//												PublishServicesActivity.class);
//										startActivity(intent);
//									}
//								});
//
//						builder1.setNegativeButton(
//								"发表新内容",
//								new android.content.DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//														int which) {
//										roadLineService.deleteData(entity.getRoadlineId());
//										onRefresh();
//										Intent intent = new Intent();
//										intent.setClass(getActivity(),
//												PublishServicesActivity.class);
//										startActivity(intent);
//										dialog.dismiss();
//									}
//								});
//
//						builder1.create().show();
//
//					} else {
//						CustomToast.makeText(getActivity(), 0,
//								"该路线正在审核", 300).show();
//					}

				}
			}
		});
		headerView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), OpenShopActivity.class);
				Util.vipBack="myservices";
				startActivity(intent);
			}
		});
	}

	protected void initData() {
		// TODO Auto-generated method stub
		// 查询数据库
		dbRoadLineList = roadLineService.queryAllRoadLine(MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId());
		// 查询是否为vip
		queryIsVip();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);
	}

	private void loadData(final int what) {
		routeList.clear();
		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "50"); // pageSize
		APPRestClient.post(getActivity(), ServiceCode.ROAD_DROP_LIST, params,
				new APPResponseHandler<GuideRouteEntity>(GuideRouteEntity.class, getActivity()) {
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
							message.what = 4;
							handler.sendMessage(message);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(1);
						} else {
							CustomToast.makeText(getActivity(), 0,
									errorMsg, 300).show();
							mSwipeRefresh.setRefreshing(false);
							if (errorCode == -999) {
								new AlertDialog.Builder(getActivity())
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

	private void setPermissionRequest() {

		RequestParams params = new RequestParams();
		APPRestClient.post(getActivity(), ServiceCode.ROAD_LINE_PUBLISH_PERMISSION,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(BaseEntity result) {
						// 继续往下走
						Intent intent = new Intent();
						if (isVip) {
							intent.setClass(getActivity(),
									PublishServicesActivity.class);
						} else {
							intent.setClass(getActivity(),
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
							CustomToast.makeText(getActivity(), 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(getActivity())
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
								new AlertDialog.Builder(getActivity())
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
		APPRestClient.post(getActivity(), APPRestClient.BASE_URL
						+ ServiceCode.QUERY_IS_VIP, params, true,
				new APPResponseHandler<VipEntity>(VipEntity.class, getActivity()) {
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
							CustomToast.makeText(getActivity(), 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(getActivity())
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.my_services_no_data_release_btn:
				setPermissionRequest();
				break;
			default:
				break;
		}
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
					adapter = new MyServicesAdapter(getActivity(),
							routeList);
					SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
							adapter);
					swingBottomInAnimationAdapter.setAbsListView(mListView);
					assert swingBottomInAnimationAdapter.getViewAnimator() != null;
					swingBottomInAnimationAdapter.getViewAnimator()
							.setInitialDelayMillis(INITIAL_DELAY_MILLIS);
					mListView.setAdapter(swingBottomInAnimationAdapter);
					break;
				case 4:
					mSwipeRefresh.setRefreshing(false);
//					mListView.setVisibility(View.GONE);
//					mSwipeRefresh.setVisibility(View.GONE);
					waveView.setVisibility(View.VISIBLE);
					break;
				case 3:
					mListView.setVisibility(View.VISIBLE);
					waveView.setVisibility(View.GONE);
					mSwipeRefresh.setVisibility(View.VISIBLE);
					mSwipeRefresh.setRefreshing(false);
					routeList.addAll(dbRoadLineList);
					adapter = new MyServicesAdapter(getActivity(),
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
		dbRoadLineList = roadLineService.queryAllRoadLine(MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId());
		loadData(1);
	}


	class MyServicesAdapter extends BaseAdapter {
		private Context context;
		private List<RoadLineEntity> routeList1;
		private LayoutInflater inflater;
		private Boolean isClick = false;

		public MyServicesAdapter(Context context, List<RoadLineEntity> routeList) {
			this.context = context;
			this.routeList1 = routeList;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return routeList1.size();
		}

		@Override
		public Object getItem(int position) {
			return routeList1.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.my_services_listview_item, null);
				holder.imageView = (SimpleDraweeView) convertView
						.findViewById(R.id.my_services_listview_item_image);
				holder.titleText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_name);
				holder.countText = (TextView) convertView
						.findViewById(R.id.service_sellcount);
				holder.popwindowText = (TextView) convertView
						.findViewById(R.id.service_popwindow);
				holder.linear = (LinearLayout) convertView
						.findViewById(R.id.service_popwindow_linear);
				holder.priceText = (TextView) convertView
						.findViewById(R.id.my_services_listview_item_route_price);
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
			holder.countText.setText(routeList1.get(position).getOrderCount());
			holder.linear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isClick) {
						isClick = true;
						holder.popwindowText.setBackgroundDrawable(getResources().getDrawable(R.drawable.lzh_popwindowonclick));
					} else {
						isClick = false;
						holder.popwindowText.setBackgroundDrawable(getResources().getDrawable(R.drawable.lzh_popwindowclick));
					}
					ShopLeftPopupWindow mLifeHallWindow = new ShopLeftPopupWindow(
							(Activity) context,false);
					mLifeHallWindow.setRoadlineEntity(routeList1.get(position));
					mLifeHallWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							isClick = false;
							holder.popwindowText.setBackgroundDrawable(getResources().getDrawable(R.drawable.lzh_popwindowclick));
						}
					});
					mLifeHallWindow.setDoListener(new ShopLeftPopupWindow.dropAndOnListener() {
						@Override
						public void doDrop() {

						}

						@Override
						public void doOn() {
							CustomDialog.Builder builder1 = new CustomDialog.Builder(
									context);
							builder1.setMessage("确定上架该商品?");
							builder1.setPositiveButton("上架",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int which) {
											sendTopRequest(routeList1.get(position).getRoadlineId(),position);
											dialog.dismiss();
										}
									});

							builder1.setNegativeButton(
									"取消",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
															int which) {
											dialog.dismiss();
										}
									});

							builder1.create().show();
						}
					});

					mLifeHallWindow.setUpListener(new ShopLeftPopupWindow.upListener() {
						@Override
						public void doUp() {
							sendUpRequest(routeList1.get(position).getRoadlineId());
						}
					});
					mLifeHallWindow.setDeleteListener(new ShopLeftPopupWindow.deleteListener() {

						@Override
						public void doDelete() {
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
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						mLifeHallWindow.showAsDropDown(v, dp2px(-270), dp2px(-64), Gravity.LEFT);
					} else {
						mLifeHallWindow.showAsDropDown(v, dp2px(-270), dp2px(-64));
					}
				}
			});

			if (TextUtils.isEmpty(routeList1.get(position).getRoadlinePrice())) {
				holder.priceText.setText("0.00");
			} else {
				holder.priceText.setText(routeList1.get(position)
						.getRoadlinePrice());
			}
			return convertView;
		}

		/*
		路线上架接口
	 	*/
		public void sendTopRequest(final String roadLineId,final int position) {
			RequestParams params = new RequestParams();
			params.add("roadlineId", roadLineId); //
			params.add("roadlineStatus","success");
			APPRestClient.post(context, ServiceCode.ROAD_DROP, params,
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
								sendTopRequest(roadLineId,position);
							} else {
								CustomToast.makeText(context, 0, errorMsg, 300).show();
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
							}}

						@Override
						public void onFinish() {
						}
					});
		}
		/*
		路线置顶接口
	 	*/
		public void sendUpRequest(final String roadLineId) {
			RequestParams params = new RequestParams();
			params.add("roadlineId", roadLineId); //
			APPRestClient.post(context, ServiceCode.ROAD_TOP, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class,
							context) {
						@Override
						public void onSuccess(BaseEntity result) {
							onRefresh();
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								sendUpRequest(roadLineId);
							} else {
								CustomToast.makeText(context, 0, errorMsg, 300).show();
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
							}}

						@Override
						public void onFinish() {
						}
					});
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
			TextView titleText, countText, priceText, popwindowText;
			LinearLayout linear;
		}
	}

}
