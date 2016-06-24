package com.uugty.uu.com.find;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mob.tools.utils.UIHandler;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.city.customview.LabelTipsGroupView;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.CustomViewPager;
import com.uugty.uu.common.myview.ListViewForScrollView;
import com.uugty.uu.common.myview.PagerScrollView;
import com.uugty.uu.common.share.onekeyshare.OnekeyShare;
import com.uugty.uu.common.share.onekeyshare.ShareContentCustomizeCallback;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.CollectRoadLineEntity;
import com.uugty.uu.entity.MoreLvEntity;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.RoadTag;
import com.uugty.uu.entity.TagsEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.HomeTagEntity.Tags.PlayAndBuy;
import com.uugty.uu.entity.MoreLvEntity.MoreListEntity;
import com.uugty.uu.entity.RoadEntity.RoadDetail;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.order.UUPayActivity;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.util.LogUtils;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.viewpage.adapter.MoreListAdapter;
import com.uugty.uu.viewpage.adapter.TabFragmentPagerAdapter;

public class FindTestViewPagerActivity extends BaseActivity implements
		OnClickListener, PlatformActionListener, Callback {

	private CustomViewPager mPager;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private ImageView image, collectImage, idverificationImage,phone,chat;
	private TextView view1, view3;
	private ImageView mVerU;//认证小U
	private ImageView mRealName;//实名
	private ImageView mAcademic;//学历证
	private ImageView mGuide;//导游证
	private ImageView mDriver;//驾驶证
	private String verU,realName,academic,guide,driver;
	private int currIndex;// 当前页卡编号
	private float bmpW;// 横线图片宽度
	private float offset;// 图片移动的偏移量
	private PagerScrollView mScrollView;
	private ArrayList<TextView> arryTextView;
	private String roadId;
	private MoreListAdapter adapter;
	private ListViewForScrollView more_listview;
	private String shareUrl, wxShareUrl, collectId;// 分享路径 0--未收藏
	private RoadDetail roadDetailResult;// 路线信息
	private LinearLayout shareLin;
	private String detailUserId = "", user_name, avatar, routeAddress,
			routeBackgroundImage;
	private SimpleDraweeView backgroundImage;
	private TextView routeTitleTextView, routePriceTextView;
	private SimpleDraweeView avatarImage;
	private TextView routePersonNamTextView, routePersonIntroductionTextView,
			moreRouteTextView,chat_text,tel_text;
	private LinearLayout find_back, moreRouteLin, chatLin;
	private LinearLayout callPhone;//联系客服
	private LabelTipsGroupView tagLin;
	private TextView routeCommentNum, routeTravelNum, routeBrowseNum;
	private Button orderBtn;
	private MoreLvEntity moreLista;// 更多路线
	private SpotsDialog loadingDialog;
	private RelativeLayout fatherRel, persondate_rel;
	private String bgaddress = "";
	private boolean isFirst = true;
	private Fragment btFragment;
//	private Fragment secondFragment;
	private Fragment thirdFragment;
	private String[] tagsItem;
	private String userTel;//用户的电话号码
	private boolean isVisibleDetail = true;//是否显示说明
	
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	
	
	//说明相关
	private TextView roadContentTextView;
	private LinearLayout mLinearLayout,findview_fragment_explain_view_price_linear, findview_fragment_explain_view_content_linear,
			findview_fragment_explain_view_line_linear;
	private TextView findview_fragment_explain_roadlineInfo, findview_fragment_explain_roadlineStartArea,
			findview_fragment_explain_roadlineSpecialMark, findview_fragment_explain_roadlinePrice,
			findview_fragment_explain_roadlineFeeContains, findview_fragment_explain_view_price_text,
			findview_fragment_explain_view_content_text, findview_fragment_explain_view_line_text,
			findview_fragment_explain_roadlineFeeContains_text;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_find_route_display;
	}

	@Override
	protected void initGui() {
		init();//线路说明内容
		roadId = getIntent().getStringExtra("roadId");
		find_back = (LinearLayout) findViewById(R.id.tabar_back);
		mScrollView = (PagerScrollView) findViewById(R.id.activity_find_route_display_scrollview);
		more_listview = (ListViewForScrollView) findViewById(R.id.find_route_display_more_route_list);
		shareLin = (LinearLayout) findViewById(R.id.find_title_share_lin);
		backgroundImage = (SimpleDraweeView) findViewById(R.id.find_route_display_background_image);
		routeTitleTextView = (TextView) findViewById(R.id.find_route_display_route_title);
		routePriceTextView = (TextView) findViewById(R.id.find_route_display_route_price);
		avatarImage = (SimpleDraweeView) findViewById(R.id.find_route_display_route_person_avatar);
		routePersonNamTextView = (TextView) findViewById(R.id.find_route_display_route_person_name);
		routePersonIntroductionTextView = (TextView) findViewById(R.id.find_route_display_route_guide_introduction);
		moreRouteLin = (LinearLayout) findViewById(R.id.find_route_display_more_route_btn_lin);
		routeCommentNum = (TextView) findViewById(R.id.find_route_display_route_comment);
		routeBrowseNum = (TextView) findViewById(R.id.find_route_display_route_browse);
		routeTravelNum = (TextView) findViewById(R.id.find_route_display_route_travel);
		collectImage = (ImageView) findViewById(R.id.find_route_display_route_collect_image);
		tagLin = (LabelTipsGroupView) findViewById(R.id.find_route_display_route_tag_lin);
		chatLin = (LinearLayout) findViewById(R.id.find_route_display_route_chat_lin);
		chat = (ImageView) findViewById(R.id.chat_image);
		chat_text = (TextView) findViewById(R.id.chat_text);
		phone = (ImageView) findViewById(R.id.tel_image);
		tel_text = (TextView) findViewById(R.id.tel_text);
		callPhone = (LinearLayout) findViewById(R.id.find_route_display_route_tel_lin);
		orderBtn = (Button) findViewById(R.id.find_route_display_route_reserve_btn);
		moreRouteTextView = (TextView) findViewById(R.id.find_detail_about_more_line);
		persondate_rel = (RelativeLayout) findViewById(R.id.persondate_rel);
		fatherRel = (RelativeLayout) findViewById(R.id.activity_find_route_display_father);
		idverificationImage = (ImageView) findViewById(R.id.find_route_display_route_id_verification);
		fatherRel.setVisibility(View.INVISIBLE);
		mPager = (CustomViewPager) findViewById(R.id.viewpager);

		//导游认证信息
		mVerU = (ImageView) findViewById(R.id.find_rote_img_ver);
		mAcademic = (ImageView) findViewById(R.id.find_rote_img_academic);
		mRealName = (ImageView) findViewById(R.id.find_rote_img_realname);
		mGuide = (ImageView) findViewById(R.id.find_rote_img_guide);
		mDriver = (ImageView) findViewById(R.id.find_rote_img_drive);
		
		view1 = (TextView) findViewById(R.id.tv_guid1);
//		view2 = (TextView) findViewById(R.id.tv_guid2);
		view3 = (TextView) findViewById(R.id.tv_guid3);
		arryTextView = new ArrayList<TextView>();
		arryTextView.add(view1);
//		arryTextView.add(view2);
		arryTextView.add(view3);

		view1.setOnClickListener(new txListener(0));
//		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(1));
		InitImage();
		MyApplication.getInstance().setKilled(false);
	}

	private void init() {
		mLinearLayout = (LinearLayout) findViewById(R.id.findview_fragment_linearlayout);
		findview_fragment_explain_roadlineFeeContains_text = (TextView) findViewById(R.id.findview_fragment_explain_roadlineFeeContains_text);
		roadContentTextView = (TextView) findViewById(R.id.find_detail_road_content);
		findview_fragment_explain_view_price_linear = (LinearLayout) findViewById(R.id.findview_fragment_explain_view_price_linear);
		findview_fragment_explain_view_content_linear = (LinearLayout) findViewById(R.id.findview_fragment_explain_view_content_linear);
		findview_fragment_explain_view_line_linear = (LinearLayout) findViewById(R.id.findview_fragment_explain_view_line_linear);
		findview_fragment_explain_roadlineInfo = (TextView) findViewById(R.id.findview_fragment_explain_roadlineInfo);
		findview_fragment_explain_roadlineStartArea = (TextView) findViewById(R.id.findview_fragment_explain_roadlineStartArea);
		findview_fragment_explain_view_price_text = (TextView) findViewById(R.id.findview_fragment_explain_view_price_text);
		findview_fragment_explain_roadlineSpecialMark = (TextView) findViewById(R.id.findview_fragment_explain_roadlineSpecialMark);
		findview_fragment_explain_roadlinePrice = (TextView) findViewById(R.id.findview_fragment_explain_roadlinePrice);
		findview_fragment_explain_roadlineFeeContains = (TextView) findViewById(R.id.findview_fragment_explain_roadlineFeeContains);
		findview_fragment_explain_view_content_text = (TextView) findViewById(R.id.findview_fragment_explain_view_content_text);
		findview_fragment_explain_view_line_text = (TextView) findViewById(R.id.findview_fragment_explain_view_line_text);
	}
	
	@Override
	protected void initAction() {
		shareLin.setOnClickListener(this);
		find_back.setOnClickListener(this);
		collectImage.setOnClickListener(this);
		chatLin.setOnClickListener(this);
		callPhone.setOnClickListener(this);
		orderBtn.setOnClickListener(this);
		moreRouteTextView.setOnClickListener(this);
		persondate_rel.setOnClickListener(this);
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
		// TODO Auto-generated method stub
//		if(MyApplication.getInstance().getUserInfo().getOBJECT().getUserIsPromoter().equals(other)){
//			
//		}
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
		case R.id.find_title_share_lin:
			if (MyApplication.getInstance().isLogin()) {
				showShare();
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage",
						FindTestViewPagerActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.tabar_back:
			finish();
			find_back.setClickable(false);
			break;
		case R.id.find_detail_about_more_line:
			intent.putExtra("userId", detailUserId);
			intent.setClass(ctx, MoreLineActivity.class);
			startActivity(intent);
			break;
		case R.id.find_route_display_route_collect_image:
			// 判断登录
			if (MyApplication.getInstance().isLogin()) {
				if (!collectId.equals("0")) {
					sendCollectCancleRequest();
				} else {
					if (!MyApplication.getInstance().getUserInfo().getOBJECT()
							.getUserId().equals(detailUserId)) {
						// 收藏
						sendCollectRequest("");
					} else {
						// 自己不可收藏自己的路线
						CustomDialog.Builder builder = new CustomDialog.Builder(
								this);
						builder.setMessage("不可以收藏自己的路线");
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

				}

			} else {
				intent.putExtra("topage",
						FindTestViewPagerActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);
			}

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
				builder.setMessage(userTel);
				builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						intent.setData(Uri.parse("tel:" + userTel));
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
					intent.setClass(FindTestViewPagerActivity.this,
							UUPayActivity.class);
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
		case R.id.persondate_rel:
			if (MyApplication.getInstance().isLogin()) {
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.com.find.PersonCenterActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("detailUserId", detailUserId);
				intent.setClass(this, PersonCenterActivity.class);
				startActivity(intent);
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
		// 2.bmpW=(屏宽/3-字大小)*2  合并说明板块之后减1
		TextPaint paint = view1.getPaint();
		float measure = paint.measureText(view1.getText().toString());
		// 194
		bmpW = (float) (measure * 4.45);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2 ;

		// imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {

		btFragment = FindViewFragment_play.newInstance(roadId);
		//合并说明和玩法板块
//		secondFragment = FindViewFragment_explain.newInstance(roadId);
		thirdFragment = FindViewFragment_comments.newInstance(detailUserId);

		fragmentList.add(btFragment);
//		fragmentList.add(secondFragment);
		fragmentList.add(thirdFragment);

		// 给ViewPager设置适配器
		mPager.setAdapter(new TabFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0, true);// 设置当前显示标签页为第一页
		view1.setTextColor(getResources().getColor(android.R.color.black));
		mLinearLayout.setVisibility(View.VISIBLE);
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
		APPRestClient.post(this, ServiceCode.ROAD_LINE_DETAIL_MESSAGE, params,
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
										FindTestViewPagerActivity.this)
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
		APPRestClient.post(this, ServiceCode.ROAD_LINE_ALL_LISTSIMPLE, params,
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
										FindTestViewPagerActivity.this)
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
								.removeSpecifiedActivity("com.uugty.uu.com.find.MoreLineActivity");
					}
				});

	}

	// 取消收藏
	private void sendCollectCancleRequest() {
		RequestParams params = new RequestParams();
		params.add("collectId", collectId);// 路线Id
		APPRestClient.post(this, ServiceCode.COLLECT_CANCEL_ROAD_LINE, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendCollectCancleRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										FindTestViewPagerActivity.this)
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

	private void sendCollectRequest(final String content) {
		RequestParams params = new RequestParams();
		params.add("collectRoadlineId", roadId);// 路线Id
		params.add("collectTitle", content);// 路线Id
		APPRestClient.post(this, ServiceCode.COLLECT_ROAD_LINE, params,
				new APPResponseHandler<CollectRoadLineEntity>(
						CollectRoadLineEntity.class, this) {
					@Override
					public void onSuccess(CollectRoadLineEntity result) {
						Message msg = Message.obtain();
						msg.what = 3;
						Bundle b = new Bundle();
						b.putSerializable("collectRoadLine", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendCollectRequest(content);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										FindTestViewPagerActivity.this)
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
					verU = roadEntityResult.getOBJECT().getUserIsPromoter();//是否为会员
					realName = roadEntityResult.getOBJECT().getUserIdValidate();//身份证是否验证
					academic = roadEntityResult.getOBJECT().getUserCertificateValidate();//学历证是否验证
					guide = roadEntityResult.getOBJECT().getUserTourValidate();//导游证是否验证
					driver = roadEntityResult.getOBJECT().getUserCarValidate();//驾驶证是否验证
					
					if(isVisibleDetail){
						rodeDetail(roadEntityResult);//路线说明内容添加
					}
					if(null != verU && verU.equals("1")){
						mVerU.setVisibility(View.VISIBLE);
					}else{
						mVerU.setVisibility(View.GONE);
					}
					if(null != realName && realName.equals("2")){
						mRealName.setVisibility(View.VISIBLE);
					}else{
						mRealName.setVisibility(View.GONE);
					}
					if(null != academic && academic.equals("2")){
						mAcademic.setVisibility(View.VISIBLE);
					}else{
						mAcademic.setVisibility(View.GONE);
					}
					if(null != guide && guide.equals("2")){
						mGuide.setVisibility(View.VISIBLE);
					}else{
						mGuide.setVisibility(View.GONE);
					}
					if(null != driver && driver.equals("2")){
						mDriver.setVisibility(View.VISIBLE);
					}else{
						mDriver.setVisibility(View.GONE);
					}
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
					collectId = roadDetailResult.getCollectId();
					if (!roadDetailResult.getCollectId().equals("0")) {
						collectImage
								.setImageResource(R.drawable.home_page_collected_img);
					} else {
						collectImage
								.setImageResource(R.drawable.home_page_default_collect_img);
					}
					if (null != roadDetailResult.getTourAvatar()
							&& !roadDetailResult.getTourAvatar().equals("")) {
						avatarImage.setImageURI(Uri
								.parse(APPRestClient.SERVER_IP
										+ roadDetailResult.getTourAvatar()));
					} else {
						avatarImage.setImageURI(Uri.parse("res///"
								+ R.drawable.no_default_head_img));
					}
					avatar = roadDetailResult.getTourAvatar();
					routeAddress = roadDetailResult.getRoadlineGoalArea();
					if (roadDetailResult.getUserName().equals("")) {
						user_name = "小u";
					} else {
						user_name = roadDetailResult.getUserName();
					}
					routePersonNamTextView.setText(user_name);
					if (roadDetailResult.getUserIdValidate().equals("2")) {
						idverificationImage.setVisibility(View.VISIBLE);
					} else {
						idverificationImage.setVisibility(View.GONE);
					}
					if (!TextUtils.isEmpty(roadDetailResult
							.getUserDescription())) {
						routePersonIntroductionTextView
								.setVisibility(View.VISIBLE);
						routePersonIntroductionTextView
								.setText(roadDetailResult.getUserDescription());
					} else {
						routePersonIntroductionTextView
								.setVisibility(View.GONE);
					}
					// 标签
					List<PlayAndBuy> tags = roadDetailResult.getTags();
					List<String> tagsls = new ArrayList<String>();
					for (int i = 0; i < tags.size(); i++) {
						tagsls.add(tags.get(i).getTagName());
					}
					int size = tagsls.size();
					tagsItem = tagsls.toArray(new String[size]);
					tagLin.removeAllViews();
					if (tagsItem.length > 0) {
						tagLin.setVisibility(View.VISIBLE);
						tagLin.initViews(tagsItem);
					} else {
						tagLin.setVisibility(View.INVISIBLE);
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
			case 2:
				// 改变图标
				collectImage
						.setImageResource(R.drawable.home_page_default_collect_img);
				collectId = "0";
				break;
			case 3:
				CollectRoadLineEntity entity = (CollectRoadLineEntity) msg
						.getData().getSerializable("collectRoadLine");
				// 改变图标
				collectImage
						.setImageResource(R.drawable.home_page_collected_img);
				collectId = entity.getOBJECT().getCollectId();
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
					intent.setClass(FindTestViewPagerActivity.this,
							FindTestViewPagerActivity.class);
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

	// 分享
	private void showShare() {
		Util.sharWXType = "share";
		shareUrl = "http://www.uugty.com/uuapplication/wxprojectbendi/html/fx.html?roadlineId=" + roadId;
		if(null!=MyApplication.getInstance().getUserInfo())
		wxShareUrl ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf6c597932d583ce0&redirect_uri=http://www.uugty.com/uuapplication/wxRedictUrl.do?url=http://www.uugty.com/uuapplication/wxprojectbendi/html/join_uu_roaddetail_load.html?promoteUserId="
				+MyApplication.getInstance().getUserInfo().getOBJECT().getUserId()+"??roadlineId="+roadId+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		if (roadDetailResult.getRoadlineBackground().contains(".")) {
			bgaddress = (roadDetailResult.getRoadlineBackground())
					.substring(0, (roadDetailResult.getRoadlineBackground())
							.lastIndexOf("."))
					+ "_ya.png";
		} else {
			bgaddress = roadDetailResult.getRoadlineBackground() + "_ya.png";
		}
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(roadDetailResult.getUserName());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shareUrl);
		// text是分享文本，所有平台都需要这个字段
		// oks.setText("我是分享文本http://www.baidu.com"); //新浪微博链接
		oks.setText(roadDetailResult.getRoadlineTitle());
		oks.setImageUrl(APPRestClient.SERVER_IP + "images/roadlineBackgroud/"
				+ bgaddress);

		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");//
		// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(wxShareUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
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
					// 由于Onekeyshare没有关于应用分享的参数如setShareType等，我们需要通过自定义 分享来实现
					// 比如下面设置了setTitle,可以覆盖oks.setTitle里面的title值
					paramsToShare.setTitle(roadDetailResult.getUserName());
					paramsToShare.setText(roadDetailResult.getRoadlineTitle());
					
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
				// 点击微信好友
				if ("WechatMoments".equals(platform.getName())) {
					// 微信分享应用 ,此功能需要微信绕开审核，需要使用项目中的wechatdemo.keystore进行签名打包
					// 由于Onekeyshare没有关于应用分享的参数如setShareType等，我们需要通过自定义 分享来实现
					// 比如下面设置了setTitle,可以覆盖oks.setTitle里面的title值
					paramsToShare.setTitle(roadDetailResult.getRoadlineTitle());
					paramsToShare.setText(roadDetailResult.getRoadlineTitle());
					// 取不到图片使用默认logo
//					if (paramsToShare.getImageData() == null) {
//						BitmapDrawable d = new BitmapDrawable(getResources().openRawResource(R.drawable.app_icon));
//						paramsToShare.setImageData(d.getBitmap());
//					}
//					paramsToShare.setImageUrl(APPRestClient.SERVER_IP
//							+ "images/roadlineBackgroud/" + bgaddress);
					
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
				// 点击新浪微博
				if ("SinaWeibo".equals(platform.getName())) {
					 paramsToShare.setText(roadDetailResult.getRoadlineTitle() + shareUrl);
					 paramsToShare.setShareType(Platform.SHARE_WEBPAGE);// 一定要设置分享属性
					 paramsToShare.setImagePath("");
					 paramsToShare.setImageUrl(APPRestClient.SERVER_IP
								+ "images/roadlineBackgroud/" + bgaddress);

					 // 限制微博分享的文字不能超过20
//					 if (paramsToShare.getComment().length() > 20) {
//					  
//					  Toast.makeText(FindTestViewPagerActivity.this,
//					  "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//					  
//					  } 
				}
			}
		});
		// 启动分享GUI
		oks.show(this);
	}

	public class txListener implements View.OnClickListener {
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
	
	/**
	 * 路线说明内容移植到此
	 * @param roadEntityResult 
	 */
	private void rodeDetail(RoadEntity roadEntityResult) {
		if (!roadEntityResult.getOBJECT().getRoadlineSpecialMark()
				.equals("")) {
			findview_fragment_explain_view_line_linear
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_line_text
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_price_text
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_price_linear
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_content_text
					.setVisibility(View.GONE);
			findview_fragment_explain_view_content_linear
					.setVisibility(View.GONE);

			findview_fragment_explain_roadlineInfo
					.setText(roadEntityResult.getOBJECT()
							.getRoadlineInfo());
			findview_fragment_explain_roadlineStartArea
					.setText(roadEntityResult.getOBJECT()
							.getRoadlineStartArea());
			findview_fragment_explain_roadlineSpecialMark
					.setText(roadEntityResult.getOBJECT()
							.getRoadlineSpecialMark());
			findview_fragment_explain_roadlinePrice
					.setText(roadEntityResult.getOBJECT()
							.getRoadlinePrice());
			findview_fragment_explain_roadlineFeeContains
					.setText(roadEntityResult.getOBJECT()
							.getRoadlineFeeContains());
		} else if (roadEntityResult.getOBJECT().getRoadlineDays()
				.equals("1")) {
			findview_fragment_explain_view_line_linear
					.setVisibility(View.GONE);
			findview_fragment_explain_view_line_text
					.setVisibility(View.GONE);
			findview_fragment_explain_view_price_text
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_price_linear
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_content_text
					.setVisibility(View.GONE);
			findview_fragment_explain_view_content_linear
					.setVisibility(View.GONE);
			findview_fragment_explain_roadlinePrice
					.setText(roadEntityResult.getOBJECT()
							.getRoadlinePrice());
			if (roadEntityResult.getOBJECT()
					.getRoadlineFeeContains().equals("")) {
				findview_fragment_explain_roadlineFeeContains_text.setVisibility(View.GONE);
				findview_fragment_explain_roadlineFeeContains
						.setVisibility(View.GONE);
			} else {
				findview_fragment_explain_roadlineFeeContains_text.setVisibility(View.VISIBLE);
				findview_fragment_explain_roadlineFeeContains
						.setVisibility(View.VISIBLE);
				findview_fragment_explain_roadlineFeeContains
						.setText(roadEntityResult.getOBJECT()
								.getRoadlineFeeContains());
			}
		} else {
			findview_fragment_explain_view_line_linear
					.setVisibility(View.GONE);
			findview_fragment_explain_view_line_text
					.setVisibility(View.GONE);
			findview_fragment_explain_view_price_text
					.setVisibility(View.GONE);
			findview_fragment_explain_view_price_linear
					.setVisibility(View.GONE);
			findview_fragment_explain_view_content_text
					.setVisibility(View.VISIBLE);
			findview_fragment_explain_view_content_linear
					.setVisibility(View.VISIBLE);

			roadContentTextView.setText(roadEntityResult
					.getOBJECT().getRoadlineContent());
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
					if(arg0 == 0){
						isVisibleDetail = true;
						mLinearLayout.setVisibility(View.VISIBLE);
					}else{
						mLinearLayout.setVisibility(View.GONE);
						isVisibleDetail = false;
					}
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
