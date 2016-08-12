package com.uugty.uu.friendstask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.myview.ListViewForScrollView;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.DynamicCommentsEntity;
import com.uugty.uu.entity.DynamicCommentsEntity.DynamicComments;
import com.uugty.uu.entity.DynamicEntity.Dynamic;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.entity.UpVotersTitleEntity;
import com.uugty.uu.entity.UpVotersTitleEntity.UpvotersTitle;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.util.LogUtils;
import com.uugty.uu.uuchat.ReportInterfaceActivity;

import java.util.ArrayList;
import java.util.List;

public class DynamicCommentActivity extends BaseActivity implements
		OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
		OnClickListener, OnLayoutChangeListener {
	private TextView username, dynamic_browse_text, dynamic_content_text,
			dynamic_createtimes, dynamic_goodtimes_text, dynamic_praise_text,road_title,
			dynamic_position_item_text;
	private SimpleDraweeView userheand, dynamic_img_gridview_oneimg,road_img;
	private RelativeLayout dynamic_report_rel, dynamic_praise_rel,
			dynamic_position_rel;
	private LinearLayout dynamic_photo_show,share_road;
	private JoyGridView dynamic_img_grid;
	private Dynamic dynamic;
	private String[] arry;
	private DynamicImgAdapter imgadapter; // grid item
	private ImageView dynamic_praise_state_img;
	private ListViewForScrollView listView;
	private SwipeRefreshLayout mSwipeLayout;
	private Button dynamic_comments_send;
	private EditText dynamic_comments_edit;
	private int startId = 1;// 起始页页
	private List<DynamicComments> list = new ArrayList<DynamicComments>();
	private CommentsAdapter ComAdapter;
	private TopBackView titleView;
	private JoyGridView dynamic_gridview_title;
	private List<UpvotersTitle> Uplist = new ArrayList<UpvotersTitle>();
	private UpGridAdapter gridAdapter;
	private String replayCommentId;
	private String replayUserId;
	private String replayedUserId;
	private String replayContent;
	private String parentId;
	private String type = "1";
	// Activity最外层的Layout视图
	private RelativeLayout activityRootView;
	// 屏幕高度
	private int screenHeight = 0;
	// 软件盘弹起后所占高度阀值
	private int keyHeight = 0;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			DynamicCommentsEntity entity = (DynamicCommentsEntity) msg
					.getData().getSerializable("DynamicCommentsEntity");
			if (entity != null) {
				List<DynamicComments> result = (List<DynamicComments>) entity
						.getLIST();
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					// startId++;
					// loadData(2, false);
					break;
				// case 2:
				// list.addAll(result);
				// break;
				}
				ComAdapter.notifyDataSetChanged();
			} else {
				list.clear();
				ComAdapter.notifyDataSetChanged();
			}

		};
	};

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.dynamic_comments_item_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		// 获取屏幕高度
		screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		// 阀值设置为屏幕高度的1/3
		keyHeight = screenHeight / 3;

		if (null == MyApplication.getInstance().getUserInfo()) {
			Intent intent = new Intent();
			intent.putExtra("topage", DynamicCommentActivity.class.getName());
			intent.setClass(DynamicCommentActivity.this, LoginActivity.class);
			startActivity(intent);
		} else {
			replayUserId = MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserId();
		}
		Bundle bundle = getIntent().getExtras();
		dynamic = (Dynamic) bundle.getSerializable("dynamic");
		// 浏览量+
		Browse(dynamic.getSaidId());
		// 点赞头像
		Upvoters(dynamic.getSaidId());
		activityRootView = (RelativeLayout) findViewById(R.id.dynamic_comment_relative);
		username = (TextView) findViewById(R.id.dynamic_username_text);
		dynamic_browse_text = (TextView) findViewById(R.id.dynamic_browse_text);
		titleView = (TopBackView) findViewById(R.id.dynamic_comments_title);
		dynamic_content_text = (TextView) findViewById(R.id.dynamic_content_text);
		dynamic_createtimes = (TextView) findViewById(R.id.dynamic_createtimes);
		dynamic_goodtimes_text = (TextView) findViewById(R.id.dynamic_goodtimes_text);
		dynamic_praise_text = (TextView) findViewById(R.id.dynamic_praise_text);
		userheand = (SimpleDraweeView) findViewById(R.id.dynamic_avatar_img);
		dynamic_img_gridview_oneimg = (SimpleDraweeView) findViewById(R.id.dynamic_img_gridview_oneimg);
		dynamic_report_rel = (RelativeLayout) findViewById(R.id.dynamic_report_rel);
		dynamic_praise_rel = (RelativeLayout) findViewById(R.id.dynamic_praise_rel);
		dynamic_photo_show = (LinearLayout) findViewById(R.id.dynamic_photo_show);
		dynamic_img_grid = (JoyGridView) findViewById(R.id.dynamic_img_grid);
		dynamic_praise_state_img = (ImageView) findViewById(R.id.dynamic_praise_state_img);
		dynamic_comments_send = (Button) findViewById(R.id.dynamic_comments_send);
		dynamic_comments_edit = (EditText) findViewById(R.id.dynamic_comments_edit);
		dynamic_position_rel = (RelativeLayout) findViewById(R.id.dynamic_position_rel);
		dynamic_position_item_text = (TextView) findViewById(R.id.dynamic_position_item_text);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.dynamic_swipe_container);
		listView = (ListViewForScrollView) findViewById(R.id.dynamic_comments_listview);
		dynamic_gridview_title = (JoyGridView) findViewById(R.id.dynamic_gridview_title);

		//分享路线
		share_road = (LinearLayout) findViewById(R.id.dynamic_road_share_linear);
		road_img = (SimpleDraweeView) findViewById(R.id.dynamic_road_image);
		road_title = (TextView) findViewById(R.id.dynamic_road_title);

		ComAdapter = new CommentsAdapter(ctx, list);
		listView.setAdapter(ComAdapter);
	}

	private void Upvoters(final String id) {
		RequestParams params = new RequestParams();
		params.add("saidId", id); // 用户ID
		APPRestClient.post(this, ServiceCode.QUERY_UPVOTERS, params,
				new APPResponseHandler<UpVotersTitleEntity>(
						UpVotersTitleEntity.class, this) {
					@Override
					public void onSuccess(UpVotersTitleEntity result) {
						Uplist = result.getLIST();
						gridAdapter = new UpGridAdapter(ctx, Uplist);
						dynamic_gridview_title.setAdapter(gridAdapter);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							Upvoters(id);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
					}
				});
	}

	// 浏览量 +
	private void Browse(final String id) {
		RequestParams params = new RequestParams();
		params.add("saidId", id); // 用户ID
		APPRestClient.post(this, ServiceCode.VIEW_DETAILS, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							Browse(id);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
						Upvoters(dynamic.getSaidId());
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							UpdatePraise(id, state);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
					}
				});
	}

	// 评论
	private void SendComments(final String content) {
		RequestParams params = new RequestParams();
		params.add("saidId", dynamic.getSaidId()); // 评论ID
		params.add("commentContent", content); // 评论内容
		APPRestClient.post(this, ServiceCode.COMMENT_FRIENDSAID, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "评论成功", 300).show();
						dynamic_comments_edit.setText("");
						onRefresh();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							SendComments(content);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
					}
				});
	}

	// 评论回复
	private void SendCommentsDetail() {
		RequestParams params = new RequestParams();
		params.add("replayCommentId", replayCommentId); // 评论ID
		params.add("replayUserId", replayUserId); // 回复用户ID
		params.add("replayedUserId", replayedUserId); // 被回复用户ID
		params.add("replayContent", replayContent); // 回复内容
		params.add("parentId", parentId); // 父ID
		params.add("saidId", dynamic.getSaidId()); // 父ID
		LogUtils.printLog("阿斯蒂芬", "1==" + replayCommentId + "2=" + replayUserId + "3="
				+ replayedUserId + "4=" + replayContent + "5=" + parentId
				+ "6=" + dynamic.getSaidId());
		APPRestClient.post(this, ServiceCode.ADD_FRIENDREPLAY, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "回复成功", 300).show();
						replayCommentId = "";
						replayedUserId = "";
						replayContent = "";
						parentId = "";
						type = "1";
						dynamic_comments_edit.setHint("");
						dynamic_comments_edit.setText("");
						onRefresh();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							SendCommentsDetail();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
					}
				});
	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		switch (v.getId()) {
		case R.id.dynamic_avatar_img:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("detailUserId", dynamic.getUserId());
			intent.setClass(ctx, PersonCenterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		dynamic_gridview_title
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.putExtra("detailUserId", Uplist.get(position)
								.getUserId());
						intent.setClass(ctx, PersonCenterActivity.class);
						startActivity(intent);
					}
				});
		userheand.setOnClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		listView.setOnScrollListener(this);
		loadData(1, true);

		dynamic_comments_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (MyApplication.getInstance().isLogin()) {
					if (type.equals("1")) {
						if (!dynamic_comments_edit.getText().toString()
								.equals("")) {
							SendComments(dynamic_comments_edit.getText()
									.toString());
						} else {
							CustomToast.makeText(ctx, 0, "评论不能为空", 300).show();
						}
					} else {
						if (!dynamic_comments_edit.getText().toString()
								.equals("")) {
							replayContent = dynamic_comments_edit.getText()
									.toString();
							SendCommentsDetail();
						} else {
							CustomToast.makeText(ctx, 0, "回复不能为空", 300).show();
						}
					}
				} else {
					// 先登录
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage",
							DynamicCommentActivity.class.getName());
					intent.setClass(ctx, LoginActivity.class);
					ctx.startActivity(intent);
				}

			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		if (!dynamic.getSaidCity().equals("")) {
			dynamic_position_rel.setVisibility(View.VISIBLE);
			dynamic_position_item_text.setText(dynamic.getSaidCity());
		} else {
			dynamic_position_item_text.setText("");
			dynamic_position_rel.setVisibility(View.GONE);
		}
		titleView.setTitle(dynamic.getUserName());
		username.setText(dynamic.getUserName()); // 用户名
		dynamic_browse_text.setText(dynamic.getSaidBrowseTimes()); // 浏览量
		dynamic_content_text.setText(dynamic.getSaidContent()); // 内容
		dynamic_createtimes.setText(dynamic.getSaidCreateDate().substring(0,
				dynamic.getSaidCreateDate().indexOf("."))); // 发布时间
		dynamic_goodtimes_text.setText(dynamic.getSaidGoodTimes()); // 点赞次数
		// 点赞状态
		if (dynamic.getSaidUpvoteStatus().equals("0")) {

			dynamic_praise_state_img.setImageDrawable(getResources()
					.getDrawable(R.drawable.dynamic_praise_img));
			dynamic_praise_text.setTextColor(Color.parseColor("#98999a"));
			dynamic_goodtimes_text.setTextColor(Color.parseColor("#98999a"));
			dynamic_praise_text.setText("");
		} else {
			dynamic_praise_state_img.setImageDrawable(getResources()
					.getDrawable(R.drawable.dynamic_havepraise_img));
			dynamic_praise_text.setTextColor(Color.parseColor("#00a1d9"));
			dynamic_goodtimes_text.setTextColor(Color.parseColor("#00a1d9"));
//			dynamic_praise_text.setText("已赞");
		}

		if (null != dynamic.getUserAvatar() // 用户头像
				&& !dynamic.getUserAvatar().equals("")) {
			userheand.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ dynamic.getUserAvatar()));
		} else {
			userheand.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}

		if(null != dynamic.getShareRoadId()
				&& !dynamic.getShareRoadId().equals("0")){
			share_road.setVisibility(View.VISIBLE);
			if(dynamic.getSaidContent().equals("")){
				dynamic_content_text.setVisibility(View.GONE);
			}else{
				dynamic_content_text.setVisibility(View.VISIBLE);
			}
			road_img.setImageURI(Uri.parse(APPRestClient.SERVER_IP+ "images/roadlineDescribe/"
					+ dynamic.getShareRoadImg()));
			road_title.setText(dynamic.getShareRoadTitle());
			share_road.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra("roadId",dynamic.getShareRoadId());
					i.setClass(ctx, FindTestViewPagerActivity.class);
					startActivity(i);
				}
			});
		}else{
			share_road.setVisibility(View.GONE);
		}
		/*
		 * 图片展示
		 */
		if (dynamic.getSaidPhoto().equals("")) {
			dynamic_photo_show.setVisibility(View.GONE);
		} else {
			dynamic_photo_show.setVisibility(View.VISIBLE);
		}
		if (!dynamic.getSaidPhoto().equals("")) {
			arry = dynamic.getSaidPhoto().split(",");
			if (arry.length > 1) {
				dynamic_img_grid.setVisibility(View.VISIBLE);
				dynamic_img_gridview_oneimg.setVisibility(View.GONE);
				imgadapter = new DynamicImgAdapter(ctx, arry);
				dynamic_img_grid.setAdapter(imgadapter);
			} else {
				dynamic_img_grid.setVisibility(View.GONE);
				dynamic_img_gridview_oneimg.setVisibility(View.VISIBLE);
				dynamic_img_gridview_oneimg.setAspectRatio(1);
				dynamic_img_gridview_oneimg.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP
								+ arry[0].substring(0, arry[0].indexOf("."))
								+ "_ya"
								+ arry[0].substring(arry[0].indexOf("."))));
			}
		}
		dynamic_img_gridview_oneimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<String> urls = new ArrayList<String>();
				for (int i = 0; i < arry.length; i++) {
					urls.add(arry[i]);
				}
				imageBrower(0, (ArrayList<String>) urls);
			}
		});
		// 点击大图
		dynamic_img_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int positions, long id) {
				// TODO Auto-generated method stub
				arry = dynamic.getSaidPhoto().split(",");
				List<String> urls = new ArrayList<String>();
				for (int i = 0; i < arry.length; i++) {
					urls.add(arry[i]);
				}
				imageBrower(positions, (ArrayList<String>) urls);
			}
		});
		// 点赞
		dynamic_praise_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MyApplication.getInstance().isLogin()) {
					String state = "";
					int num = Integer.parseInt(dynamic.getSaidGoodTimes());
					if (dynamic.getSaidUpvoteStatus().equals("0")) {
						state = "1";
						dynamic.setSaidUpvoteStatus("1");
						dynamic_praise_state_img
								.setImageDrawable(getResources().getDrawable(
										R.drawable.dynamic_havepraise_img));
						dynamic_praise_text.setTextColor(Color
								.parseColor("#00a1d9"));
						dynamic_goodtimes_text.setTextColor(Color
								.parseColor("#00a1d9"));
//						dynamic_praise_text.setText("已赞");
						dynamic_goodtimes_text.setText("" + (num + 1));
						dynamic.setSaidGoodTimes("" + (num + 1));
					} else {
						CustomToast.makeText(ctx, 0, "您已取消了赞", 300).show();
						state = "0";
						dynamic.setSaidUpvoteStatus("0");
						dynamic_praise_state_img
								.setImageDrawable(getResources().getDrawable(
										R.drawable.dynamic_praise_img));
						dynamic_praise_text.setTextColor(Color
								.parseColor("#98999a"));
						dynamic_goodtimes_text.setTextColor(Color
								.parseColor("#98999a"));
						dynamic_praise_text.setText("");
						if (num > 0) {
							dynamic_goodtimes_text.setText("" + (num - 1));
							dynamic.setSaidGoodTimes("" + (num - 1));
						} else {
							dynamic_goodtimes_text.setText("" + num);
						}
					}
					UpdatePraise(dynamic.getSaidId(), state);
				} else {
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage",
							DynamicCommentActivity.class.getName());
					intent.setClass(ctx, LoginActivity.class);
					ctx.startActivity(intent);
				}
			}
		});
		// 举报
		dynamic_report_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if (MyApplication.getInstance().isLogin()) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("saidId", dynamic.getSaidId());
					intent.putExtra("type", "3");
					intent.setClass(ctx, ReportInterfaceActivity.class);
					startActivity(intent);
				} else {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage",
							DynamicCommentActivity.class.getName());
					intent.setClass(ctx, LoginActivity.class);
					ctx.startActivity(intent);
				}
			}
		});
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

	// 加载列表数据
	private void loadData(final int what, final boolean msg) {
		RequestParams params = new RequestParams();
		params.add("saidId", dynamic.getSaidId()); //
		// params.add("currentPage", String.valueOf(startId)); // 当前页数
		// params.add("pageSize", "5"); // pageSize

		APPRestClient.post(this, ServiceCode.COMMENT_DETAILS, params,
				new APPResponseHandler<DynamicCommentsEntity>(
						DynamicCommentsEntity.class, this) {
					@Override
					public void onSuccess(DynamicCommentsEntity result) {
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("DynamicCommentsEntity", result);
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
							loadData(what, msg);
						} else {
							mSwipeLayout.setRefreshing(false);
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										DynamicCommentActivity.this)
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
					}
				});
	}

	// 图片展示
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
			ImageLoader
					.getInstance()
					.displayImage(
							APPRestClient.SERVER_IP
									+ ls[position].substring(0,
											ls[position].indexOf("."))
									+ "_ya"
									+ ls[position].substring(ls[position]
											.indexOf(".")), holder.img, options);
			// holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP+ls[position]));
			return view;
		}

		class ViewHolder {
			ImageView img;
		}
	}

	// 评论列表adapter
	class CommentsAdapter extends BaseAdapter {
		private Context context;
		private List<DynamicComments> ls;
		private SpannableString ss;
		private String replyNickName;
		private String commentNickName;
		private String replyContentStr;

		public CommentsAdapter(Context context, List<DynamicComments> ls) {
			super();
			this.context = context;
			this.ls = ls;
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
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.dynamic_comments_list_item, null);
				holder.dynamic_comments_lin = (LinearLayout) view
						.findViewById(R.id.dynamic_comments_lin);
				holder.dynamic_reply_text = (TextView) view
						.findViewById(R.id.dynamic_reply_text);
				holder.userHead = (SimpleDraweeView) view
						.findViewById(R.id.dynamic_avatar_img);
				holder.dynamic_username_text = (TextView) view
						.findViewById(R.id.dynamic_username_text);
				holder.dynamic_createtimes = (TextView) view
						.findViewById(R.id.dynamic_createtimes);
				holder.dynamic_content_text = (TextView) view
						.findViewById(R.id.dynamic_content_text);

				holder.view1 = view.findViewById(R.id.view1);
				holder.view2 = view.findViewById(R.id.view2);
			
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (null != ls.get(position).getUserAvatar() // 用户头像
					&& !ls.get(position).getUserAvatar().equals("")) {
				holder.userHead.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ ls.get(position).getUserAvatar()));
			} else {
				holder.userHead.setImageURI(Uri.parse("res:///"
						+ R.drawable.no_default_head_img));
			}
			//判断是否为回复评论
			
//			if (ls.get(position).getReplayedUserName().equals("0")) {
				holder.dynamic_comments_lin.setVisibility(View.VISIBLE);
				holder.dynamic_reply_text.setVisibility(View.GONE);
				holder.view1.setVisibility(View.VISIBLE);
				holder.view2.setVisibility(View.GONE);
//			} else {
//				holder.dynamic_comments_lin.setVisibility(View.GONE);
//				holder.dynamic_reply_text.setVisibility(View.VISIBLE);
//				holder.view2.setVisibility(View.VISIBLE);
//				holder.view1.setVisibility(View.GONE);
//			}
				//回复评论 名称  内容
		/*	
			if (!ls.get(position).getUserName().equals("")) {				
				replyNickName=ls.get(position).getUserName();
			} else {
				replyNickName="小u";
			}

			if (!ls.get(position).getReplayedUserName().equals("")) {
				commentNickName=ls
						.get(position).getReplayedUserName();
			} else {
				commentNickName="小u";
			}
			replyContentStr=ls.get(position).getCommentContent();
			
			ss = new SpannableString(replyNickName+"回复"+commentNickName
					+"："+replyContentStr);
			ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.select_one)),0, 
					replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.select_one)),replyNickName.length()+2, 
					replyNickName.length()+commentNickName.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//为回复的人昵称添加点击事件
			ss.setSpan(new TextSpanClick(false,position), 0, 
					replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//为评论的人的添加点击事件
			ss.setSpan(new TextSpanClick(true,position),replyNickName.length()+2,
			replyNickName.length()+commentNickName.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.dynamic_reply_text.setText(ss);
			//添加点击事件时，必须设置
			holder.dynamic_reply_text.setMovementMethod(LinkMovementMethod.getInstance());
			**/

			holder.dynamic_username_text
					.setText(ls.get(position).getUserName());
			holder.dynamic_createtimes.setText(ls
					.get(position)
					.getCommentCreateDate()
					.substring(
							0,
							ls.get(position).getCommentCreateDate()
									.indexOf("."))); // 发布时间
			holder.dynamic_content_text.setText(ls.get(position)
					.getCommentContent());
			holder.userHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("detailUserId", ls.get(position)
							.getCommentUserId());
					intent.setClass(context, PersonCenterActivity.class);
					startActivity(intent);
				}
			});
			//点击回复
			/*holder.dynamic_content_text
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									dynamic_comments_edit.setFocusable(true);
									dynamic_comments_edit
											.setFocusableInTouchMode(true);
									InputMethodManager inputManager = (InputMethodManager) dynamic_comments_edit
											.getContext()
											.getSystemService(
													Context.INPUT_METHOD_SERVICE);
									inputManager.toggleSoftInput(0,
											InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}, 100);
							dynamic_comments_edit.setHint("回复："
									+ ls.get(position).getReplayedUserName());
							replayCommentId = ls.get(position).getCommentId();
							replayedUserId = ls.get(position)
									.getCommentUserId();
							parentId = ls.get(position).getCommentId();
							type = "2";
						}
					});
			 	**/
		/*	
			holder.dynamic_reply_text.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									dynamic_comments_edit.setFocusable(true);
									dynamic_comments_edit
											.setFocusableInTouchMode(true);
									InputMethodManager inputManager = (InputMethodManager) dynamic_comments_edit
											.getContext()
											.getSystemService(
													Context.INPUT_METHOD_SERVICE);
									inputManager.toggleSoftInput(0,
											InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}, 300);
							dynamic_comments_edit.setHint("回复："
									+ ls.get(position).getReplayedUserName());
							replayCommentId = ls.get(position).getCommentId();
							replayedUserId = ls.get(position)
									.getCommentUserId();
							parentId = ls.get(position).getParentId();
							type = "2";
						}
					});**/
			return view;
		}

		class ViewHolder {
			SimpleDraweeView userHead;
			TextView dynamic_username_text, dynamic_createtimes,
					dynamic_content_text;
			LinearLayout dynamic_comments_lin;
			TextView dynamic_reply_text;
			View view1, view2;
		}
	}

	/*
	private final class TextSpanClick extends ClickableSpan{
		private boolean status;
		private int postion;
		public TextSpanClick(boolean status,int postion){
			this.status = status;
			this.postion=postion;
		}
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);//取消下划线
		}
		@Override
		public void onClick(View v) {
			Intent intent =new Intent();
			list.get(postion).getCommentUserId();
			String msgStr = "";
			if(status){
				msgStr = "我是回复的人";			
			}else{
				msgStr = "我是评论的人";
			}
			Toast.makeText(DynamicCommentActivity.this, msgStr, Toast.LENGTH_SHORT).show();
		}
	}**/
	class UpGridAdapter extends BaseAdapter {
		private Context context;
		private List<UpvotersTitle> ls = new ArrayList<UpvotersTitle>();

		public UpGridAdapter(Context context, List<UpvotersTitle> ls) {
			super();
			this.context = context;
			this.ls = ls;
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
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.upvoters_grid_item_layout, null);
				holder.title = (SimpleDraweeView) view
						.findViewById(R.id.upvoters_grid_item_title_img);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (null != ls.get(position).getUserAvatar() // 用户头像
					&& !ls.get(position).getUserAvatar().equals("")) {
				holder.title.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ ls.get(position).getUserAvatar()));
			} else {
				holder.title.setImageURI(Uri.parse("res:///"
						+ R.drawable.no_default_head_img));
			}
			return view;
		}

		class ViewHolder {
			SimpleDraweeView title;
		}
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
				// loadData(2, false);
			}
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
		// 点赞头像
		Upvoters(dynamic.getSaidId());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 添加layout大小发生改变监听器
		activityRootView.addOnLayoutChangeListener(this);
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		// TODO Auto-generated method stub
		// old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值

		// System.out.println(oldLeft + " " + oldTop +" " + oldRight + " " +
		// oldBottom);
		// System.out.println(left + " " + top +" " + right + " " + bottom);

		// 现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
		if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
			//键盘弹起
		} else if (oldBottom != 0 && bottom != 0
				&& (bottom - oldBottom > keyHeight)) {
			//键盘关闭
			replayCommentId = "";
			replayedUserId = "";
			replayContent = "";
			parentId = "";
			type = "1";
			dynamic_comments_edit.setHint("");
			dynamic_comments_edit.setText("");
		}
	}
}
