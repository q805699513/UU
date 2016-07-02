package com.uugty.uu.friendstask;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.DynamicEntity;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.entity.DynamicEntity.Dynamic;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.uuchat.ChatActivity;

public class DynamicDetailActivity extends BaseActivity implements
		OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
		OnClickListener, OnItemClickListener {
	private TextView title;
	private ImageView send;
	private ListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private RelativeLayout invitaRel;
	private Button publistBtn;
	private DynamicAdapter adapter; // 论坛 list
	private DynamicImgAdapter imgadapter; // grid item
	private List<Dynamic> list = new ArrayList<Dynamic>();

	private SpotsDialog loadingDialog;
	private String userid = "", username, useravatar, dynamicType;
	private String myId = "";
	private LinearLayout dynamic_back;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			DynamicEntity entity = (DynamicEntity) msg.getData()
					.getSerializable("DynamicEntity");
			if (entity != null) {
				List<Dynamic> result = (List<Dynamic>) entity.getLIST();
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							loadingDialog.dismiss();
						}
					}, 500);
					loadData(2, false);
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
		// TODO Auto-generated method stub
		return R.layout.friends_dynamic_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					DynamicDetailActivity.class.getName());
			intent.setClass(DynamicDetailActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
			myId = MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId();
		}
		userid = getIntent().getStringExtra("userid");
		username = getIntent().getStringExtra("username");
		useravatar = getIntent().getStringExtra("useravatar");
		dynamicType = getIntent().getStringExtra("usertype");
		title = (TextView) findViewById(R.id.friend_dynamic_titleName);
		if (!username.equals("")) {
			if(dynamicType.equals("0")){
			title.setText(username);
			}else{
			title.setText("我的uu圈");	
			}
		} else {
			title.setText("小U");
		}
		publistBtn=(Button) findViewById(R.id.service_collect_no_data_btn);
		invitaRel=(RelativeLayout) findViewById(R.id.service_collect_no_data_rel);
		dynamic_back = (LinearLayout) findViewById(R.id.tabar_back);
		send = (ImageView) findViewById(R.id.dynamic_sendMessage_friend);
		send.setVisibility(View.GONE);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.dynamic_swipe_container);
		mListView = (ListView) findViewById(R.id.dynamic_listview);
		adapter = new DynamicAdapter(ctx, list,mSwipeLayout,invitaRel);
		mListView.setAdapter(adapter);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		publistBtn.setOnClickListener(this);
		dynamic_back.setOnClickListener(this);
		send.setOnClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		mListView.setOnScrollListener(this);
		loadData(1, true);
	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		switch (v.getId()) {
		case R.id.dynamic_sendMessage_friend:
			Intent mintent = new Intent();
			if (MyApplication.getInstance().isLogin()) {
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(userid)) {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
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
					mintent.putExtra("userId", userid);
					mintent.putExtra("useravatar", useravatar);
					mintent.putExtra("userName", username);
					mintent.setClass(this, ChatActivity.class);
					startActivity(mintent);
				}
			} else {
				// 先登录
				mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				mintent.putExtra("topage",
						DynamicDetailActivity.class.getName());
				mintent.setClass(this, LoginActivity.class);
				startActivity(mintent);

			}
			break;
		case R.id.tabar_back:
			finish();
			dynamic_back.setClickable(false);
			break;
		case R.id.service_collect_no_data_btn:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(DynamicDetailActivity.this, PublishTalkActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

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
		list.clear();
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
				loadData(2, false);
			}
		}

	}

	// 大图展示
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(ctx, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "0");
		startActivity(intent);
	}

	private void loadData(final int what, final boolean msg) {
		// 显示等待层
		if (what == 1 && msg) {
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
			}
		}
		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		params.add("userId", userid);
		APPRestClient
				.post(this, ServiceCode.DYNAMIC_DETAILS, params,
						new APPResponseHandler<DynamicEntity>(
								DynamicEntity.class, this) {
							@Override
							public void onSuccess(DynamicEntity result) {
								if (null != result.getLIST()
										&& result.getLIST().size() > 0) {
									Message msg = Message.obtain();
									msg.what = what;
									Bundle b = new Bundle();
									b.putSerializable("DynamicEntity", result);
									msg.setData(b);
									handler.sendMessage(msg);
									invitaRel.setVisibility(View.GONE);
									mSwipeLayout.setVisibility(View.VISIBLE);
								} else {
									if (mSwipeLayout != null) {
										mSwipeLayout.setRefreshing(false);
									}
									if (startId == 1) {
										invitaRel.setVisibility(View.VISIBLE);
										mSwipeLayout.setVisibility(View.GONE);
										new Handler().postDelayed(
												new Runnable() {
													public void run() {
														// 显示dialog
														loadingDialog.dismiss();
													}
												}, 500);
									}
								}

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									loadData(what, msg);
								} else {
								loadingDialog.dismiss();
								CustomToast.makeText(ctx, 0, errorMsg, 300)
										.show();
								if (errorCode == -999) {
									new AlertDialog.Builder(
											DynamicDetailActivity.this)
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

	// 点赞
	private void UpdatePraise(final String id, final String state) {
		RequestParams params = new RequestParams();
		params.add("saidId", id); // 用户ID
		params.add("saidUpvoteStatus", state); // 点赞状态

		APPRestClient.post(this, ServiceCode.UP_VOTE, params,
				new APPResponseHandler<UpVoteEntity>(UpVoteEntity.class, this) {
					@Override
					public void onSuccess(UpVoteEntity result) {

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							UpdatePraise(id, state);
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(DynamicDetailActivity.this)
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

	// 删除
	private void deletefriendsaid(final String id) {
		RequestParams params = new RequestParams();
		params.add("saidId", id); // 用户ID
		APPRestClient.post(this, ServiceCode.DELETE_FRIENDSAID, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "删除成功", 200).show();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							deletefriendsaid(id);
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(DynamicDetailActivity.this)
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

	class DynamicAdapter extends BaseAdapter {
		private Context context;
		private List<Dynamic> ls = new ArrayList<Dynamic>();
		private SwipeRefreshLayout mSwipeLayout;
		private RelativeLayout invitaRel;
		private String[] arry;

		

		public DynamicAdapter(Context context, List<Dynamic> ls,
				SwipeRefreshLayout mSwipeLayout, RelativeLayout invitaRel) {
			super();
			this.context = context;
			this.ls = ls;
			this.mSwipeLayout = mSwipeLayout;
			this.invitaRel = invitaRel;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ls.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.friends_dynamic_list_item, null);
				holder.username = (TextView) view
						.findViewById(R.id.dynamic_username_text);
				holder.dynamic_browse_text = (TextView) view
						.findViewById(R.id.dynamic_browse_text);
				holder.dynamic_content_text = (TextView) view
						.findViewById(R.id.dynamic_content_text);
				holder.dynamic_createtimes = (TextView) view
						.findViewById(R.id.dynamic_createtimes);
				holder.dynamic_goodtimes_text = (TextView) view
						.findViewById(R.id.dynamic_goodtimes_text);
				holder.dynamic_comments_text = (TextView) view
						.findViewById(R.id.dynamic_comments_text);
				holder.dynamic_praise_text = (TextView) view
						.findViewById(R.id.dynamic_zambia_text);
				holder.userheand = (SimpleDraweeView) view
						.findViewById(R.id.dynamic_avatar_img);
				holder.dynamic_img_gridview_oneimg = (SimpleDraweeView) view
						.findViewById(R.id.dynamic_img_gridview_oneimg);
				holder.dynamic_friend_comments_lin = (LinearLayout) view
						.findViewById(R.id.dynamic_friend_comments_lin);
				holder.dynamic_photo_show = (LinearLayout) view
						.findViewById(R.id.dynamic_photo_show);
				holder.dynamic_img_grid = (JoyGridView) view
						.findViewById(R.id.dynamic_img_grid);
//				holder.dynamic_title_V = (ImageView) view
//						.findViewById(R.id.dynamic_title_V);
				holder.dynamic_praise_state_img = (ImageView) view
						.findViewById(R.id.dynamic_praise_state_img);
				holder.dynamic_delete_rel = (LinearLayout) view
						.findViewById(R.id.dynamic_delete_rel);
				holder.dynamic_browse_rel = (LinearLayout) view
						.findViewById(R.id.dynamic_browse_rel);
				holder.dynamic_praise_rel = (LinearLayout) view
						.findViewById(R.id.dynamic_zambia_rel);
				holder.dynamic_right_delete = (ImageView) view
						.findViewById(R.id.dynamic_right_delete);
				// holder.dynamic_right_menu_lin=(LinearLayout)
				// view.findViewById(R.id.dynamic_right_menu_lin);
				holder.dynamic_position_item_text = (TextView) view
						.findViewById(R.id.dynamic_position_item_text);
				holder.right_report_pupuwindow = (ImageView) view
						.findViewById(R.id.right_report_pupuwindow);
				holder.dynamic_right_chat_img = (ImageView) view
						.findViewById(R.id.dynamic_right_chat_img);
				holder.dynamic_postion_item_lin = (LinearLayout) view
						.findViewById(R.id.dynamic_postion_item_lin);				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			// 聊天 or 删除
			if (MyApplication.getInstance().isLogin()) {
				if (ls.get(position)
						.getUserId()
						.equals(MyApplication.getInstance().getUserInfo()
								.getOBJECT().getUserId())
						&& dynamicType.equals("0")) {
					holder.dynamic_right_chat_img.setVisibility(View.GONE);
					holder.right_report_pupuwindow.setVisibility(View.GONE);
					holder.dynamic_right_delete.setVisibility(View.VISIBLE);
				} else if (dynamicType.equals("0")) {
					holder.dynamic_right_chat_img.setVisibility(View.VISIBLE);
					holder.right_report_pupuwindow.setVisibility(View.VISIBLE);
					holder.dynamic_right_delete.setVisibility(View.GONE);
				} else {
					holder.dynamic_right_chat_img.setVisibility(View.GONE);
					holder.right_report_pupuwindow.setVisibility(View.GONE);
					holder.dynamic_right_delete.setVisibility(View.GONE);
				}
			} else {
				holder.dynamic_right_chat_img.setVisibility(View.VISIBLE);
				holder.right_report_pupuwindow.setVisibility(View.VISIBLE);
				holder.dynamic_right_delete.setVisibility(View.GONE);
			}
			// 朋友圈 or 帖子
			if (dynamicType.equals("0")) {
				holder.dynamic_delete_rel.setVisibility(View.GONE);
				holder.dynamic_browse_rel.setVisibility(View.VISIBLE);
			} else {
				holder.dynamic_delete_rel.setVisibility(View.VISIBLE);
				holder.dynamic_browse_rel.setVisibility(View.GONE);
			}
			if (!ls.get(position).getSaidCity().equals("")) {
				holder.dynamic_postion_item_lin.setVisibility(View.VISIBLE);
				holder.dynamic_position_item_text.setText(ls.get(position)
						.getSaidCity());
			} else {
				holder.dynamic_position_item_text.setText("");
				holder.dynamic_postion_item_lin.setVisibility(View.GONE);
			}
//			holder.dynamic_title_V.setVisibility(View.VISIBLE);
			holder.username.setText(ls.get(position).getUserName()); // 用户名
			holder.dynamic_browse_text.setText(ls.get(position)
					.getSaidBrowseTimes()); // 浏览量
			holder.dynamic_content_text.setText(ls.get(position)
					.getSaidContent()); // 内容
			holder.dynamic_createtimes.setText(ls
					.get(position)
					.getSaidCreateDate()
					.substring(0,
							ls.get(position).getSaidCreateDate().indexOf("."))); // 发布时间
			holder.dynamic_goodtimes_text.setText(ls.get(position)
					.getSaidGoodTimes()); // 点赞次数
			holder.dynamic_comments_text.setText(ls.get(position)
					.getSaidCommentTimes()); // 评论次数
			// 点赞状态
			if (ls.get(position).getSaidUpvoteStatus().equals("0")) {
				Drawable drawable = context.getResources().getDrawable(
						R.drawable.dynamic_praise_img);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				holder.dynamic_praise_text.setCompoundDrawables(drawable, null,
						null, null);

				holder.dynamic_praise_text.setTextColor(Color
						.parseColor("#98999a"));
				holder.dynamic_praise_text.setText("");
				holder.dynamic_goodtimes_text.setTextColor(Color
						.parseColor("#98999a"));
			} else {

				Drawable drawable = context.getResources().getDrawable(
						R.drawable.dynamic_havepraise_img);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				holder.dynamic_praise_text.setCompoundDrawables(drawable, null,
						null, null);
				holder.dynamic_praise_text.setTextColor(Color
						.parseColor("#00a1d9"));
//				holder.dynamic_praise_text.setText("已赞");
				holder.dynamic_goodtimes_text.setTextColor(Color
						.parseColor("#00a1d9"));
			}

			if (null != ls.get(position).getUserAvatar() // 用户头像
					&& !ls.get(position).getUserAvatar().equals("")) {
				holder.userheand.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ ls.get(position).getUserAvatar()));
			} else {
				holder.userheand.setImageURI(Uri.parse("res:///"
						+ R.drawable.no_default_head_img));
			}
			holder.userheand.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("detailUserId", ls.get(position).getUserId());
					intent.setClass(ctx, PersonCenterActivity.class);
					startActivity(intent);
				}
			});
			// 聊天
			holder.dynamic_right_chat_img
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent mintent = new Intent();
							if (MyApplication.getInstance().isLogin()) {
								if (MyApplication.getInstance().getUserInfo()
										.getOBJECT().getUserId().equals(userid)) {
									// 弹出框，确认删除
									CustomDialog.Builder builder = new CustomDialog.Builder(
											context);
									builder.setMessage("这是你自己");
									builder.setTitle("提示");
									builder.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											});
									builder.create().show();
								} else {
									mintent.putExtra("userId", userid);
									mintent.putExtra("useravatar", useravatar);
									mintent.putExtra("userName", username);
									mintent.setClass(context,
											ChatActivity.class);
									startActivity(mintent);
								}
							} else {
								// 先登录
								mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								mintent.putExtra("topage",
										DynamicDetailActivity.class.getName());
								mintent.setClass(context, LoginActivity.class);
								startActivity(mintent);

							}
						}
					});
			// 弹出popupwindow
			holder.right_report_pupuwindow
					.setOnClickListener(new OnClickListener() {

						@SuppressLint("NewApi")
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (MyApplication.getInstance().isLogin()) {
								RightReportPopupWindow mLifeHallWindow = new RightReportPopupWindow(
										(Activity) context);
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
									mLifeHallWindow.showAsDropDown(v, -180,
											-50, Gravity.BOTTOM);
									mLifeHallWindow.setSaidId(ls.get(position)
											.getSaidId());
									mLifeHallWindow.setType("3");
									mLifeHallWindow.upCollect(ls.get(position)
											.getSaidId());
								} else {
									mLifeHallWindow
											.showAsDropDown(v, -128, -30);
									mLifeHallWindow.setSaidId(ls.get(position)
											.getSaidId());
									mLifeHallWindow.setType("3");
									mLifeHallWindow.upCollect(ls.get(position)
											.getSaidId());
								}
							} else {
								// 先登录
								Intent intent = new Intent();
								intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent.putExtra("topage",
										DynamicDetailActivity.class.getName());
								intent.setClass(context, LoginActivity.class);
								context.startActivity(intent);
							}
						}
					});
			// 删除帖子
			holder.dynamic_delete_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CustomDialog.Builder builder1 = new CustomDialog.Builder(
							ctx);
					builder1.setMessage("确定删除吗?");
					builder1.setTitle("提示");
					builder1.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									deletefriendsaid(ls.get(position)
											.getSaidId());
									ls.remove(position);
									adapter.notifyDataSetChanged();
									if(ls.size()<1){
									invitaRel.setVisibility(View.VISIBLE);
									mSwipeLayout.setVisibility(View.GONE);
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
			/*
			 * 图片展示
			 */
			if (ls.get(position).getSaidPhoto().equals("")) {
				holder.dynamic_photo_show.setVisibility(View.GONE);
			} else {
				holder.dynamic_photo_show.setVisibility(View.VISIBLE);
			}
			if (!ls.get(position).getSaidPhoto().equals("")) {
				arry = ls.get(position).getSaidPhoto().split(",");
				if (arry.length > 1) {
					holder.dynamic_img_grid.setVisibility(View.VISIBLE);
					holder.dynamic_img_gridview_oneimg.setVisibility(View.GONE);
					imgadapter = new DynamicImgAdapter(ctx, arry);
					holder.dynamic_img_grid.setAdapter(imgadapter);
				} else {
					holder.dynamic_img_grid.setVisibility(View.GONE);
					holder.dynamic_img_gridview_oneimg
							.setVisibility(View.VISIBLE);
					holder.dynamic_img_gridview_oneimg.setAspectRatio(1);
					holder.dynamic_img_gridview_oneimg.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + arry[0].substring(0, arry[0].indexOf("."))
									+ "_ya"
									+ arry[0].substring(arry[0].indexOf("."))));
				}
			}
			holder.dynamic_img_gridview_oneimg
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							arry = ls.get(position).getSaidPhoto().split(",");
							List<String> urls = new ArrayList<String>();
							for (int i = 0; i < arry.length; i++) {
								urls.add(arry[i]);
							}
							imageBrower(0, (ArrayList<String>) urls);
						}
					});
			// 点击大图
			holder.dynamic_img_grid
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int positions, long id) {
							// TODO Auto-generated method stub
							arry = ls.get(position).getSaidPhoto().split(",");
							List<String> urls = new ArrayList<String>();
							for (int i = 0; i < arry.length; i++) {
								urls.add(arry[i]);
							}
							imageBrower(positions, (ArrayList<String>) urls);
						}
					});
			// 评论跳转
			holder.dynamic_friend_comments_lin
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							Bundle b = new Bundle();
							b.putSerializable("dynamic", ls.get(position));
							intent.putExtras(b);
							intent.setClass(ctx, DynamicCommentActivity.class);
							startActivity(intent);
						}
					});
			// 删除朋友圈
			holder.dynamic_right_delete
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CustomDialog.Builder builder1 = new CustomDialog.Builder(
									context);
							builder1.setMessage("确定删除吗?");
							builder1.setTitle("提示");
							builder1.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											deletefriendsaid(ls.get(position)
													.getSaidId());
											ls.remove(position);
											notifyDataSetChanged();
											if(ls.size()<1){
												invitaRel.setVisibility(View.VISIBLE);
												mSwipeLayout.setVisibility(View.GONE);
												}
											dialog.dismiss();
										}
									});

							builder1.setNegativeButton(
									"取消",
									new android.content.DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});

							builder1.create().show();
						}
					});
			// 点赞
			holder.dynamic_praise_rel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (MyApplication.getInstance().isLogin()) {
						String state = "";
						int num = Integer.parseInt(ls.get(position)
								.getSaidGoodTimes());
						if (ls.get(position).getSaidUpvoteStatus().equals("0")) {
							state = "1";
							ls.get(position).setSaidUpvoteStatus("1");
							Drawable drawable = context.getResources()
									.getDrawable(
											R.drawable.dynamic_havepraise_img);
							// / 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0,
									drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							holder.dynamic_praise_text.setCompoundDrawables(
									drawable, null, null, null);
							holder.dynamic_praise_text.setTextColor(Color
									.parseColor("#00a1d9"));
//							holder.dynamic_praise_text.setText("已赞");
							holder.dynamic_goodtimes_text.setTextColor(Color
									.parseColor("#00a1d9"));
							holder.dynamic_goodtimes_text.setText(""
									+ (num + 1));
							ls.get(position).setSaidGoodTimes("" + (num + 1));
						} else {
							CustomToast.makeText(context, 0, "您已取消了赞", 300)
									.show();
							state = "0";
							ls.get(position).setSaidUpvoteStatus("0");
							Drawable drawable = context.getResources()
									.getDrawable(R.drawable.dynamic_praise_img);
							// / 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0,
									drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							holder.dynamic_praise_text.setCompoundDrawables(
									drawable, null, null, null);
							holder.dynamic_praise_text.setTextColor(Color
									.parseColor("#98999a"));
							holder.dynamic_goodtimes_text.setTextColor(Color
									.parseColor("#98999a"));
							holder.dynamic_praise_text.setText("");
							if (num > 0) {
								holder.dynamic_goodtimes_text.setText(""
										+ (num - 1));
								ls.get(position).setSaidGoodTimes(
										"" + (num - 1));
							} else {
								holder.dynamic_goodtimes_text.setText("" + num);
							}
						}
						UpdatePraise(ls.get(position).getSaidId(), state);
					} else {
						// 先登录
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.putExtra("topage",
								DynamicDetailActivity.class.getName());
						intent.setClass(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}
			});
			return view;
		}

		class ViewHolder {
			TextView username, dynamic_browse_text, dynamic_content_text,
					dynamic_createtimes, dynamic_comments_text,
					dynamic_goodtimes_text, dynamic_praise_text,
					dynamic_position_item_text;
			SimpleDraweeView userheand, dynamic_img_gridview_oneimg;

			LinearLayout dynamic_photo_show, dynamic_right_menu_lin,
					dynamic_delete_rel, dynamic_browse_rel,
					dynamic_friend_comments_lin, dynamic_praise_rel,dynamic_postion_item_lin;
			JoyGridView dynamic_img_grid;
			ImageView dynamic_title_V, dynamic_praise_state_img,
					right_report_pupuwindow, dynamic_right_chat_img,
					dynamic_right_delete;
		}
	}

	class DynamicImgAdapter extends BaseAdapter {
		private Context context;
		private String[] ls;
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.square_no_pricture)
				.showImageForEmptyUri(R.drawable.square_no_pricture)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		public DynamicImgAdapter(Context context, String[] ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ls.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ls[position];
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
				view = LayoutInflater.from(ctx).inflate(
						R.layout.dynamic_img_gridview_item, null);
				holder.img = (ImageView) view
						.findViewById(R.id.dynamic_img_gridview_img);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			ImageLoader.getInstance().displayImage(APPRestClient.SERVER_IP +
					ls[position].substring(0,ls[position].indexOf("."))+"_ya"+ls[position].substring(ls[position].indexOf(".")),
					holder.img,options);

			// holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP+ls[position]));
			return view;
		}

		class ViewHolder {
			ImageView img;
		}
	}

}
