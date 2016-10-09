package com.uugty.uu.shop.guide.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.ListViewForScrollView;
import com.uugty.uu.common.myview.PagerScrollView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.MoreLvEntity;
import com.uugty.uu.entity.MoreLvEntity.MoreListEntity;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.RoadEntity.RoadDetail;
import com.uugty.uu.entity.Util;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.shop.guide.fragment.FindViewFragment_guide_comments;
import com.uugty.uu.shop.guide.fragment.FindViewFragment_guide_explain;
import com.uugty.uu.shop.guide.fragment.FindViewFragment_guide_play;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.viewpage.adapter.MoreListAdapter;
import com.uugty.uu.viewpage.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class RoadDetailActivity extends BaseActivity implements
		OnClickListener, Callback {

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private ImageView image,phone,chat;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private float bmpW;// 横线图片宽度
	private float offset;// 图片移动的偏移量
	private PagerScrollView mScrollView;
	private ArrayList<TextView> arryTextView;
	private String roadId,avatar,user_name;
	private MoreListAdapter adapter;
	private ListViewForScrollView more_listview;
	private RoadDetail roadDetailResult;// 路线信息
	private LinearLayout shareLin;
	private String detailUserId = "", routeAddress,
			routeBackgroundImage;
	private SimpleDraweeView backgroundImage;
	private TextView routeTitleTextView, routePriceTextView,routeMarketPrice;
	private TextView moreRouteTextView,chat_text,tel_text;
	private LinearLayout find_back, moreRouteLin, chatLin;
	private LinearLayout callPhone;//联系客服
	private TextView routeCommentNum, routeTravelNum, routeBrowseNum;
	private Button orderBtn;
	private MoreLvEntity moreLista;// 更多路线
	private SpotsDialog loadingDialog;
	private RelativeLayout fatherRel;
	private boolean isFirst = true;
	private Fragment btFragment;
	private Fragment secondFragment;
	private Fragment thirdFragment;
	private String userTel;//用户的电话号码

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_guide_find_route_display;
	}

	@Override
	protected void initGui() {
		roadId = getIntent().getStringExtra("roadId");
		find_back = (LinearLayout) findViewById(R.id.tabar_back);
		mScrollView = (PagerScrollView) findViewById(R.id.activity_find_route_display_scrollview);
		more_listview = (ListViewForScrollView) findViewById(R.id.find_route_display_more_route_list);
		shareLin = (LinearLayout) findViewById(R.id.find_title_share_lin);
		backgroundImage = (SimpleDraweeView) findViewById(R.id.find_route_display_background_image);
		routeTitleTextView = (TextView) findViewById(R.id.find_route_display_route_title);
		routePriceTextView = (TextView) findViewById(R.id.find_route_display_route_price);
		moreRouteLin = (LinearLayout) findViewById(R.id.find_route_display_more_route_btn_lin);
		routeCommentNum = (TextView) findViewById(R.id.find_route_display_route_comment);
		routeBrowseNum = (TextView) findViewById(R.id.find_route_display_route_browse);
		routeTravelNum = (TextView) findViewById(R.id.find_route_display_route_travel);
		chatLin = (LinearLayout) findViewById(R.id.find_route_display_route_chat_lin);
		chat = (ImageView) findViewById(R.id.chat_image);
		chat_text = (TextView) findViewById(R.id.chat_text);
		phone = (ImageView) findViewById(R.id.tel_image);
		tel_text = (TextView) findViewById(R.id.tel_text);
		callPhone = (LinearLayout) findViewById(R.id.find_route_display_route_tel_lin);
		orderBtn = (Button) findViewById(R.id.find_route_display_route_reserve_btn);
		moreRouteTextView = (TextView) findViewById(R.id.find_detail_about_more_line);
		fatherRel = (RelativeLayout) findViewById(R.id.activity_find_route_display_father);
		fatherRel.setVisibility(View.INVISIBLE);
		mPager = (ViewPager) findViewById(R.id.viewpager);

		routeMarketPrice = (TextView) findViewById(R.id.find_route_display_market_price);

		view1 = (TextView) findViewById(R.id.tv_guid1);
		view2 = (TextView) findViewById(R.id.tv_guid2);
		view3 = (TextView) findViewById(R.id.tv_guid3);
		arryTextView = new ArrayList<TextView>();
		arryTextView.add(view1);
		arryTextView.add(view2);
		arryTextView.add(view3);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
		InitImage();
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {
		shareLin.setOnClickListener(this);
		find_back.setOnClickListener(this);
		chatLin.setOnClickListener(this);
		callPhone.setOnClickListener(this);
		orderBtn.setOnClickListener(this);
		moreRouteTextView.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		phone.setImageResource(R.drawable.route_detail_tel_image);
		tel_text.setTextColor(getResources().getColor(R.color.black));
		chat.setImageResource(R.drawable.route_detail_chat_image_noclick);
		chat_text.setTextColor(getResources().getColor(R.color.black));
		sendRouteInfoRequest();
	}

	@Override
	protected void initData() {
		find_back.setFocusable(true);
		find_back.setFocusableInTouchMode(true);
		find_back.requestFocus();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		final Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			find_back.setClickable(false);
			break;
		case R.id.find_detail_about_more_line:
			intent.putExtra("userId", detailUserId);
			intent.setClass(ctx, MoreLineActivity.class);
			startActivity(intent);
			break;
		case R.id.find_route_display_route_chat_lin:
			chat.setImageResource(R.drawable.route_detail_chat_image);
			chat_text.setTextColor(getResources().getColor(R.color.route_selected_text_color));
			// 聊天
			if (MyApplication.getInstance().isLogin()) {
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(detailUserId)) {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("这是你自己");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									chat.setImageResource(R.drawable.route_detail_chat_image_noclick);
									chat_text.setTextColor(getResources().getColor(R.color.black));
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					intent.putExtra("userId", detailUserId);
					intent.putExtra("avatar", avatar);
					intent.putExtra("userName", user_name);
					intent.setClass(this, ChatActivity.class);
					startActivity(intent);
					chat.setImageResource(R.drawable.route_detail_chat_image_noclick);
					chat_text.setTextColor(getResources().getColor(R.color.black));
				}
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage",
						FindTestViewPagerActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

			}
			break;			
		case R.id.find_route_display_route_tel_lin:
			phone.setImageResource(R.drawable.route_detail_tel_image_click);
			tel_text.setTextColor(getResources().getColor(R.color.route_selected_text_color));
			// 联系客服
			if (MyApplication.getInstance().isLogin()) {
				// 弹出框，确认删除
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("400-600-8669");
				builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						intent.setData(Uri.parse("tel:" + "400-600-8669"));//导游圈产品替换成UU客客服
						intent.setAction(Intent.ACTION_CALL);
						startActivity(intent);
						phone.setImageResource(R.drawable.route_detail_tel_image);
						tel_text.setTextColor(getResources().getColor(R.color.black));
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						phone.setImageResource(R.drawable.route_detail_tel_image);
						tel_text.setTextColor(getResources().getColor(R.color.black));
						dialog.dismiss();
					}
				});
				builder.create().show();
				
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage",
						FindTestViewPagerActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

			}
			break;
		case R.id.find_route_display_route_reserve_btn:
			// 判断登录
			if (MyApplication.getInstance().isLogin()) {

				if (!MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(detailUserId)) {
					// 下订单
					intent.putExtra("routeTitle", routeTitleTextView.getText()
							.toString());
					intent.putExtra("routePrice", routePriceTextView.getText()
							.toString());
					intent.putExtra("routeAddress", routeAddress);
					intent.putExtra("routeComments", routeCommentNum.getText()
							.toString());
					intent.putExtra("routeTravel", routeTravelNum.getText()
							.toString());
					intent.putExtra("routeBrowse", routeBrowseNum.getText()
							.toString());
					intent.putExtra("routeBackgroundImage",
							routeBackgroundImage);
					intent.putExtra("route_id", roadId);
					intent.setClass(RoadDetailActivity.this,
							GuidePayActivity.class);
					startActivity(intent);
				} else {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("不可以给自己的路线下订单");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				}

			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage",
						FindTestViewPagerActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	
	public void InitImage() {
		image = (ImageView) findViewById(R.id.cursor);

		// bmpW = BitmapFactory.decodeResource(getResources(),
		// R.drawable.fragment_chang_two).getWidth();
		// 1.计算图片与左边的距离
		// 2.bmpW=(屏宽/3-字大小)*2
		TextPaint paint = view1.getPaint();
		float measure = paint.measureText(view1.getText().toString());
		// 194
		bmpW = (float) (measure * 3.34);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;

		// imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {

		btFragment = FindViewFragment_guide_play.newInstance(roadId);
		secondFragment = FindViewFragment_guide_explain.newInstance(roadId);
		thirdFragment = FindViewFragment_guide_comments.newInstance(detailUserId);

		fragmentList.add(btFragment);
		fragmentList.add(secondFragment);
		fragmentList.add(thirdFragment);

		// 给ViewPager设置适配器
		mPager.setAdapter(new TabFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0, true);// 设置当前显示标签页为第一页
		view1.setTextColor(getResources().getColor(android.R.color.black));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
	}

	// 路线信息
	private void sendRouteInfoRequest() {
		if (isFirst) {
			if (loadingDialog != null) {
				loadingDialog.show();
			} else {
				loadingDialog = new SpotsDialog(this);
				loadingDialog.show();
			}
		}
		RequestParams params = new RequestParams();
		params.add("roadlineId", roadId);// 路线Id
		APPRestClient.postGuide(this, ServiceCode.ROAD_LINE_DETAIL_MESSAGE, params,
				new APPResponseHandler<RoadEntity>(RoadEntity.class, this) {
					@Override
					public void onSuccess(RoadEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("roadLine", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRouteInfoRequest();
						} else {
							if (null != loadingDialog)
								loadingDialog.dismiss();
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										RoadDetailActivity.this)
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

	// 更多路线
	private void sendRequestMoreLine() {
		RequestParams params = new RequestParams();
		params.add("userId", detailUserId);
		APPRestClient.postGuide(this, ServiceCode.ROAD_LINE_ALL_LISTSIMPLE, params,
				new APPResponseHandler<MoreLvEntity>(MoreLvEntity.class, this) {
					@Override
					public void onSuccess(MoreLvEntity result) {
						if (result.getLIST() == null
								|| result.getLIST().size() <= 3) {
							moreRouteLin.setVisibility(View.GONE);
						}
						Message msg = Message.obtain();
						msg.what = 4;
						Bundle b = new Bundle();
						b.putSerializable("morelist", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequestMoreLine();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										RoadDetailActivity.this)
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
								.removeSpecifiedActivity("com.uugty.guide.com.find.MoreLineActivity");
					}
				});

	}



	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				RoadEntity roadEntityResult = (RoadEntity) msg.getData()
						.getSerializable("roadLine");
				if (null != roadEntityResult.getOBJECT()) {

					roadDetailResult = roadEntityResult.getOBJECT();
					roadId = roadDetailResult.getRoadlineId();
					detailUserId = roadDetailResult.getUserId();
					userTel = roadDetailResult.getUserTel();//获取发布路线人员电话号码
					if (isFirst) {
						InitViewPager();
						sendRequestMoreLine();
					}
					backgroundImage
							.setImageURI(Uri.parse(APPRestClient.SERVER_IP
									+ "images/roadlineDescribe/"
									+ roadDetailResult.getRoadlineBackground()));
					routeBackgroundImage = APPRestClient.SERVER_IP
							+ "images/roadlineDescribe/"
							+ roadDetailResult.getRoadlineBackground();
					routeTitleTextView.setText(roadDetailResult
							.getRoadlineTitle());
					routePriceTextView.setText(roadDetailResult
							.getRoadlinePrice());
					routeCommentNum.setText(roadDetailResult.getCommentCount());
					routeTravelNum.setText(roadDetailResult.getOrderNum());
					routeBrowseNum.setText(roadDetailResult.getRoadlineViews());
					routeMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					routeMarketPrice.setText(roadDetailResult.getRoadlineMarketPrice());

					avatar = roadDetailResult.getTourAvatar();
					routeAddress = roadDetailResult.getRoadlineGoalArea();
					if (roadDetailResult.getUserName().equals("")) {
						user_name = "小u";
					} else {
						user_name = roadDetailResult.getUserName();
					}
					fatherRel.setVisibility(View.VISIBLE);
					mScrollView.smoothScrollTo(0, 20);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog

							if (null != loadingDialog)
								loadingDialog.dismiss();
						}
					}, 800);
					isFirst = false;

				}
				break;
			case 4:
				moreLista = (MoreLvEntity) msg.getData().getSerializable(
						"morelist");
				moreList(moreLista.getLIST());
				break;
			}

			super.handleMessage(msg);
		};
	};
	
	
	private void moreList(final List<MoreListEntity> result) {
		// TODO Auto-generated method stub
		adapter = new MoreListAdapter(result, this, "0");
		more_listview.setAdapter(adapter);
		more_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				if (moreLista != null && moreLista.getLIST().size() > 0) {
					intent.putExtra("roadId", moreLista.getLIST().get(position)
							.getRoadlineId());
					intent.setClass(RoadDetailActivity.this,
							RoadDetailActivity.class);
					startActivity(intent);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							finish();
						}
					}, 300);
					// overridePendingTransition(R.anim.slide_right_in,
					// R.anim.slide_left_out);
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Util.sharWXType = "";
		MyApplication.getInstance().setKilled(true);
	}

	public class txListener implements OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPager.setCurrentItem(index);
			mPager.invalidate();
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		private float one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
			for (int n = 0; n < arryTextView.size(); n++) {
				if (n == arg0) {
					arryTextView.get(n).setTextColor(
							getResources().getColor(android.R.color.black));
				} else {
					arryTextView.get(n).setTextColor(
							getResources().getColor(
									R.color.route_selected_text_color));
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

}
