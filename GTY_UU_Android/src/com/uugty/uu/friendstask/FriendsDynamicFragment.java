package com.uugty.uu.friendstask;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.myview.JoyGridView.OnTouchBlankPositionListener;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.DynamicEntity;
import com.uugty.uu.entity.DynamicEntity.Dynamic;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.ArrayList;
import java.util.List;


public class FriendsDynamicFragment extends Fragment implements
		OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
	private Context context;
	private View rootview;
	
	private ListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private FriendsAdapter adapter; // 论坛 list
	private List<Dynamic> list = new ArrayList<Dynamic>();
	private TextView Dynamic_release_text;
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
					loadData(2);
					mListView.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
					break;
				case 2:
					list.addAll(result);
					adapter.notifyDataSetChanged();
					break;
				}

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

			rootview = inflater.inflate(R.layout.friends_dynamic_layout, null);

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
		mSwipeLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.dynamic_swipe_container);
		Dynamic_release_text = (TextView) rootview.findViewById(R.id.Dynamic_release_text);
		Dynamic_release_text.setVisibility(View.VISIBLE);
		mListView = (ListView) rootview.findViewById(R.id.dynamic_listview);
		dynamic_back = (LinearLayout) rootview.findViewById(R.id.tabar_back);
		dynamic_back.setVisibility(View.GONE);
		// list footer
		TextView footView = new TextView(context);
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, dp2px(50));
		footView.setLayoutParams(params);
		mListView.addFooterView(footView, null, false);
		adapter = new FriendsAdapter(context, list);
		mListView.setAdapter(adapter);
	}

	protected void initAction() {

		Dynamic_release_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if (MyApplication.getInstance().isLogin()) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(getActivity(), PublishTalkActivity.class);
					startActivity(intent);
				} else {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage", MainActivity.class.getName());
					intent.setClass(getActivity(), LoginActivity.class);
					getActivity().startActivity(intent);
				}
			}
		});
		mSwipeLayout.setOnRefreshListener(this);
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
		loadData(1);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Bundle b = new Bundle();
				b.putSerializable("dynamic", list.get(position));
				intent.putExtras(b);
				intent.setClass(context, DynamicCommentActivity.class);
				startActivity(intent);
			}
		});
	}

	public void onRefreshData() {
		onRefresh();
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1);
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
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (startId > 1) {
			if (firstVisibleItem == (startId - 1) * 5) {
				startId++;
				loadData(2);
			}
		}
	}
	
	private void loadData(final int what) {
		// 显示等待层
		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize

		APPRestClient.post(context, ServiceCode.DYNAMIC_DETAILS, params,
				new APPResponseHandler<DynamicEntity>(DynamicEntity.class,
						context) {
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
							CustomToast.makeText(context, 0, errorMsg,
									300).show();
							mSwipeLayout.setRefreshing(false);
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

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	class FriendsAdapter extends BaseAdapter {
		private Context context;
		private List<Dynamic> ls = new ArrayList<Dynamic>();
		private String[] arry = new String[1];
		private LayoutInflater inflater;
		private FriendsImgAdapter imgadapter;

		public FriendsAdapter(Context context, List<Dynamic> ls) {
			this.context = context;
			this.ls = ls;
			inflater = LayoutInflater.from(context);
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
				view = inflater.inflate(R.layout.friends_dynamic_list_item, null);
				holder.dynamic_item_lin = (LinearLayout) view
						.findViewById(R.id.friends_dynamic_list_item_lin);
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
				holder.dynamic_photo_show = (LinearLayout) view
						.findViewById(R.id.dynamic_photo_show);
				holder.dynamic_img_grid = (JoyGridView) view
						.findViewById(R.id.dynamic_img_grid);
				holder.dynamic_position_item_text = (TextView) view
						.findViewById(R.id.dynamic_position_item_text);
				holder.right_report_pupuwindow = (ImageView) view
						.findViewById(R.id.right_report_pupuwindow);
				holder.dynamic_right_chat_img = (ImageView) view
						.findViewById(R.id.dynamic_right_chat_img);
				holder.dynamic_right_delete = (ImageView) view
						.findViewById(R.id.dynamic_right_delete);
//			holder.dynamic_right_idVeri = (ImageView) view
//					.findViewById(R.id.dynamic_title_V);
				holder.dynamic_praise_rel = (LinearLayout) view
						.findViewById(R.id.dynamic_zambia_rel);
				holder.dynamic_browse_rel = (LinearLayout) view
						.findViewById(R.id.dynamic_browse_rel);
				holder.dynamic_friend_comments_lin = (LinearLayout) view
						.findViewById(R.id.dynamic_friend_comments_lin);
				holder.dynamic_postion_item_lin = (LinearLayout) view
						.findViewById(R.id.dynamic_postion_item_lin);
				//分享路线
				holder.share_road = (LinearLayout) view
						.findViewById(R.id.dynamic_road_share_linear);
				holder.road_img = (SimpleDraweeView) view
						.findViewById(R.id.dynamic_road_image);
				holder.road_title = (TextView) view
						.findViewById(R.id.dynamic_road_title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if(null != ls.get(position).getShareRoadId()
					&& !ls.get(position).getShareRoadId().equals("0")){
				holder.share_road.setVisibility(View.VISIBLE);
				if(ls.get(position).getSaidContent().equals("")){
					holder.dynamic_content_text.setVisibility(View.GONE);
				}else{
					holder.dynamic_content_text.setVisibility(View.VISIBLE);
				}
				holder.road_img.setImageURI(Uri.parse(APPRestClient.SERVER_IP+ "images/roadlineDescribe/"
						+ ls.get(position).getShareRoadImg()));
				holder.road_title.setText(ls.get(position).getShareRoadTitle());
				holder.share_road.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent();
						i.putExtra("roadId",ls.get(position).getShareRoadId());
						i.setClass(context, FindTestViewPagerActivity.class);
						startActivity(i);
					}
				});
			}else{
				holder.share_road.setVisibility(View.GONE);
				holder.dynamic_content_text.setVisibility(View.VISIBLE);
			}

			if (null != ls.get(position).getUserAvatar() // 用户头像
					&& !ls.get(position).getUserAvatar().equals("")) {
				holder.userheand.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP+ls.get(position).getUserAvatar().substring(0, ls.get(position).getUserAvatar().indexOf("."))
								+ "_ya"
								+ ls.get(position).getUserAvatar().substring(ls.get(position).getUserAvatar().indexOf("."))));
			} else {
				holder.userheand.setImageURI(Uri.parse("res:///"
						+ R.drawable.no_default_head_img));
			}
//		if (ls.get(position).getUserIdValidate().equals("2")) {
//			holder.dynamic_right_idVeri.setVisibility(View.VISIBLE);
//		} else {
//			holder.dynamic_right_idVeri.setVisibility(View.GONE);
//		}
			holder.username.setText(ls.get(position).getUserName()); // 用户名
			holder.dynamic_createtimes
					.setText(ls
							.get(position)
							.getSaidCreateDate()
							.substring(
									0,
									ls.get(position).getSaidCreateDate()
											.indexOf("."))); // 发布时间
			holder.dynamic_content_text.setText(ls.get(position).getSaidContent()); // 内容
			// 聊天 or 删除
			if (MyApplication.getInstance().isLogin()) {
				if (ls.get(position)
						.getUserId()
						.equals(MyApplication.getInstance().getUserInfo()
								.getOBJECT().getUserId())) {
					holder.dynamic_right_chat_img.setVisibility(View.GONE);
					holder.right_report_pupuwindow.setVisibility(View.GONE);
					holder.dynamic_right_delete.setVisibility(View.VISIBLE);
				} else {
					holder.dynamic_right_chat_img.setVisibility(View.VISIBLE);
					holder.right_report_pupuwindow.setVisibility(View.VISIBLE);
					holder.dynamic_right_delete.setVisibility(View.GONE);
				}
			} else {
				holder.dynamic_right_chat_img.setVisibility(View.VISIBLE);
				holder.right_report_pupuwindow.setVisibility(View.VISIBLE);
				holder.dynamic_right_delete.setVisibility(View.GONE);
			}
			// 如果只有一张图片时，显示一张大图
			if (!ls.get(position).getSaidPhoto().equals("")) {
				holder.dynamic_photo_show.setVisibility(View.VISIBLE);
				arry = ls.get(position).getSaidPhoto().split(",");
				if (arry.length > 1) {
					holder.dynamic_img_grid.setVisibility(View.VISIBLE);
					holder.dynamic_img_gridview_oneimg.setVisibility(View.GONE);
					imgadapter = new FriendsImgAdapter(context, arry);
					holder.dynamic_img_grid.setAdapter(imgadapter);
				} else {
					holder.dynamic_img_grid.setVisibility(View.GONE);
					holder.dynamic_img_gridview_oneimg.setVisibility(View.VISIBLE);
					holder.dynamic_img_gridview_oneimg.setAspectRatio(1);
					holder.dynamic_img_gridview_oneimg.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + arry[0].substring(0, arry[0].indexOf("."))
									+ "_ya"
									+ arry[0].substring(arry[0].indexOf("."))));
				}
			} else {
				holder.dynamic_photo_show.setVisibility(View.GONE);
			}
			// 地点
			if (!ls.get(position).getSaidCity().equals("")) {
				holder.dynamic_postion_item_lin.setVisibility(View.VISIBLE);
				holder.dynamic_position_item_text.setText(ls.get(position)
						.getSaidCity());
			} else {
				holder.dynamic_postion_item_lin.setVisibility(View.GONE);
			}

			holder.dynamic_browse_text.setText(ls.get(position)
					.getSaidBrowseTimes()); // 浏览量
			holder.dynamic_comments_text.setText(ls.get(position)
					.getSaidCommentTimes()); // 评论次数
			holder.dynamic_goodtimes_text.setText(ls.get(position)
					.getSaidGoodTimes()); // 点赞次数

			// 点赞状态
			if (ls.get(position).getSaidUpvoteStatus().equals("0")) {
				Drawable drawable = context.getResources().getDrawable(
						R.drawable.dynamic_praise_img);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				holder.dynamic_praise_text.setCompoundDrawables(drawable, null,
						null, null);

				holder.dynamic_praise_text
						.setTextColor(Color.parseColor("#98999a"));
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
				holder.dynamic_praise_text
						.setTextColor(Color.parseColor("#00a1d9"));
//			holder.dynamic_praise_text.setText("已赞");
				holder.dynamic_goodtimes_text.setTextColor(Color
						.parseColor("#00a1d9"));
			}
			// 查看图片
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
						public void onItemClick(AdapterView<?> parent, View view,
												int positions, long id) {
							// TODO Auto-generated method stub
							arry = ls.get(position).getSaidPhoto().split(",");
							List<String> urls = new ArrayList<String>();
							for (int i = 0; i < arry.length; i++) {
								urls.add(arry[i]);
							}
							imageBrower(positions, (ArrayList<String>) urls);
						}
					});
			// 删除朋友圈
			holder.dynamic_right_delete.setOnClickListener(new OnClickListener() {

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
									deletefriendsaid(ls.get(position).getSaidId());
									ls.remove(position);
									notifyDataSetChanged();
									dialog.dismiss();
								}
							});

					builder1.setNegativeButton("取消",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
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
							Drawable drawable = context.getResources().getDrawable(
									R.drawable.dynamic_havepraise_img);
							// / 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							holder.dynamic_praise_text.setCompoundDrawables(
									drawable, null, null, null);
							holder.dynamic_praise_text.setTextColor(Color
									.parseColor("#00a1d9"));
//						holder.dynamic_praise_text.setText("已赞");
							holder.dynamic_goodtimes_text.setTextColor(Color
									.parseColor("#00a1d9"));
							holder.dynamic_goodtimes_text.setText("" + (num + 1));
							ls.get(position).setSaidGoodTimes("" + (num + 1));
						} else {

							CustomToast.makeText(context, 0, "您已取消了赞", 300).show();
							state = "0";
							ls.get(position).setSaidUpvoteStatus("0");
							Drawable drawable = context.getResources().getDrawable(
									R.drawable.dynamic_praise_img);
							// / 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(),
									drawable.getMinimumHeight());
							holder.dynamic_praise_text.setCompoundDrawables(
									drawable, null, null, null);

							holder.dynamic_praise_text.setTextColor(Color
									.parseColor("#98999a"));
							holder.dynamic_praise_text.setText("");
							holder.dynamic_goodtimes_text.setTextColor(Color
									.parseColor("#98999a"));
							if (num > 0) {
								holder.dynamic_goodtimes_text.setText(""
										+ (num - 1));
								ls.get(position).setSaidGoodTimes("" + (num - 1));
							} else {
								holder.dynamic_goodtimes_text.setText("" + num);
							}
						}
						UpdatePraise(ls.get(position).getSaidId(), state);
					} else {
						// 先登录
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.putExtra("topage", MainActivity.class.getName());
						intent.setClass(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}
			});

			// 跳转个人展示页
			holder.userheand.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("detailUserId", ls.get(position).getUserId());
					intent.setClass(context, PersonCenterActivity.class);
					context.startActivity(intent);
//				String userid = ls.get(position).getUserId();
//				String username = ls.get(position).getUserName();
//				String useravatar = ls.get(position).getUserAvatar();
//				Intent intent = new Intent();
//				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				intent.putExtra("userid", userid);
//				intent.putExtra("username", username);
//				intent.putExtra("useravatar", useravatar);
//				intent.putExtra("usertype", "0");
//				intent.setClass(context, DynamicDetailActivity.class);
//				context.startActivity(intent);
				}
			});

			// 评论跳转
			holder.dynamic_friend_comments_lin
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							if (MyApplication.getInstance().isLogin()) {
								intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								Bundle b = new Bundle();
								b.putSerializable("dynamic", ls.get(position));
								intent.putExtras(b);
								intent.setClass(context, DynamicCommentActivity.class);
								context.startActivity(intent);
							} else {
								//					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent.putExtra("topage", FriendsDynamicFragment.class.getName());
								intent.setClass(context, LoginActivity.class);
								context.startActivity(intent);
							}

						}
					});

			// 聊天
			holder.dynamic_right_chat_img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent mintent = new Intent();
					if (MyApplication.getInstance().isLogin()) {

						mintent.putExtra("userId", ls.get(position).getUserId());
						mintent.putExtra("useravatar", ls.get(position)
								.getUserAvatar());
						mintent.putExtra("userName", ls.get(position).getUserName());
						mintent.setClass(context, ChatActivity.class);
						context.startActivity(mintent);
					} else {
						// 先登录
						mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						mintent.putExtra("topage", MainActivity.class.getName());
						mintent.setClass(context, LoginActivity.class);
						context.startActivity(mintent);
					}
				}
			});
			// 弹出popupwindow
			holder.right_report_pupuwindow
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (MyApplication.getInstance().isLogin()) {
								RightReportPopupWindow mLifeHallWindow = new RightReportPopupWindow(
										(Activity) context);
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
									mLifeHallWindow.showAsDropDown(v, -180, -50,
											Gravity.BOTTOM);
									mLifeHallWindow.setSaidId(ls.get(position)
											.getSaidId());
									mLifeHallWindow.setType("3");
									mLifeHallWindow.upCollect(ls.get(position)
											.getSaidId());
								} else {
									mLifeHallWindow.showAsDropDown(v, -128, -30);
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
										MainActivity.class.getName());
								intent.setClass(context, LoginActivity.class);
								context.startActivity(intent);
							}
						}
					});

			holder.dynamic_img_grid
					.setOnTouchBlankPositionListener(new OnTouchBlankPositionListener() {

						@Override
						public boolean onTouchBlankPosition() {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							Bundle b = new Bundle();
							b.putSerializable("dynamic", ls.get(position));
							intent.putExtras(b);
							intent.setClass(context, DynamicCommentActivity.class);
							context.startActivity(intent);
							return true;
						}
					});
			return view;
		}

		// 大图展示
		protected void imageBrower(int position, ArrayList<String> urls2) {
			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			intent.putExtra(ImagePagerActivity.FLAG, "0");
			context.startActivity(intent);
		}

		// 点赞
		private void UpdatePraise(final String id, final String state) {
			RequestParams params = new RequestParams();
			params.add("saidId", id); // 用户ID
			params.add("saidUpvoteStatus", state); // 点赞状态

			APPRestClient.post(context, ServiceCode.UP_VOTE, params,
					new APPResponseHandler<UpVoteEntity>(UpVoteEntity.class,
							context) {
						@Override
						public void onSuccess(UpVoteEntity result) {

						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								UpdatePraise(id, state);
							} else {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
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

		// 删除
		private void deletefriendsaid(final String saidId) {
			RequestParams params = new RequestParams();
			params.add("saidId", saidId); // 用户ID
			APPRestClient.post(context, ServiceCode.DELETE_FRIENDSAID, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class, context) {
						@Override
						public void onSuccess(BaseEntity result) {
							CustomToast.makeText(context, 0, "删除成功", 200).show();
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								deletefriendsaid(saidId);
							} else {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
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

		class ViewHolder {
			TextView username, dynamic_browse_text, dynamic_content_text,
					dynamic_createtimes, dynamic_comments_text,
					dynamic_goodtimes_text, dynamic_praise_text,
					dynamic_position_item_text,road_title;
			SimpleDraweeView userheand, dynamic_img_gridview_oneimg,road_img;
			LinearLayout dynamic_photo_show, dynamic_praise_rel,
					dynamic_friend_comments_lin, dynamic_item_lin,
					dynamic_browse_rel, dynamic_postion_item_lin,share_road;
			JoyGridView dynamic_img_grid;
			ImageView right_report_pupuwindow, dynamic_right_chat_img,
					dynamic_right_delete, dynamic_right_idVeri;

		}

	}
	class FriendsImgAdapter extends BaseAdapter {

		private String[] ls;
		private LayoutInflater inflater;

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.uu_default_image_one)
				.showImageForEmptyUri(R.drawable.square_no_pricture)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		public FriendsImgAdapter(Context context, String[] ls) {
			super();
			this.ls = ls;
			inflater = LayoutInflater.from(context);
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
				view = inflater.inflate(R.layout.dynamic_img_gridview_item, null);
				holder.img = (ImageView) view
						.findViewById(R.id.dynamic_img_gridview_img);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			ImageLoader.getInstance().displayImage(
					APPRestClient.SERVER_IP
							+ ls[position].substring(0, ls[position].indexOf("."))
							+ "_ya"
							+ ls[position].substring(ls[position].indexOf(".")),
					holder.img, options);
		/*
		 * holder.img.setImageURI(Uri .parse(APPRestClient.SERVER_IP +
		 * ls[position
		 * ].substring(0,ls[position].indexOf("."))+"_ya"+ls[position]
		 * .substring(ls[position].indexOf("."))));
		 */
			return view;
		}

		class ViewHolder {
			ImageView img;
		}

	}

}
