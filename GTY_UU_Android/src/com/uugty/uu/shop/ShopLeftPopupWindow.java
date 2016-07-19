package com.uugty.uu.shop;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.utils.UIHandler;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.share.onekeyshare.OnekeyShare;
import com.uugty.uu.common.share.onekeyshare.ShareContentCustomizeCallback;
import com.uugty.uu.entity.RoadLineEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.util.LogUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class ShopLeftPopupWindow extends PopupWindow implements PlatformActionListener,Handler.Callback {
    private Activity context;
	private View mMenuView;
	private LinearLayout mEdit;
	private LinearLayout mDrop;
	private LinearLayout mDelete;
	private LinearLayout mShare;
	private LinearLayout mUp;

	private TextView mShopText;//下架-上架文字
	private ImageView mShopImg;//上架-下架图片
	private RoadLineEntity mRoadEntity;//路线信息

	private deleteListener deleteListener;//删除服务监听
	private upListener upListener;//上架监听
	private dropAndOnListener doListener;//上架监听

	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	public ShopLeftPopupWindow(Activity context,Boolean isSell) {
		super(context);
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.shop_popup_window, null);

		mEdit = (LinearLayout) mMenuView.findViewById(R.id.shop_edit);
		mDrop = (LinearLayout) mMenuView.findViewById(R.id.shop_drop);
		mDelete = (LinearLayout) mMenuView.findViewById(R.id.shop_delete);
		mShare = (LinearLayout) mMenuView.findViewById(R.id.shop_share);
		mUp = (LinearLayout) mMenuView.findViewById(R.id.shop_up);
		mShopText = (TextView) mMenuView.findViewById(R.id.shop_text);
		mShopImg = (ImageView) mMenuView.findViewById(R.id.shop_image);

		if(isSell){
			mShopText.setText("下架");
			mShopImg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lzh_drop));
			mDrop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doListener.doDrop();
					dismiss();
				}
			});
		}else{
			mShopText.setText("上架");
			mShopImg.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lzh_toshop));
			mDrop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doListener.doOn();
					dismiss();
				}
			});
		}

		initAction();//点击事件处理

		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
//		this.setAnimationStyle(R.style.popWindowAnimation);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		//this.setAnimationStyle(R.style.mystyle);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}


	public void setDeleteListener(ShopLeftPopupWindow.deleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public void setUpListener(ShopLeftPopupWindow.upListener upListener) {
		this.upListener = upListener;
	}

	public void setDoListener(dropAndOnListener doListener) {
		this.doListener = doListener;
	}

	//上架下架接口
	public interface  dropAndOnListener {
		public void doDrop();
		public void doOn();
	}

	//删除接口
	public interface  deleteListener {
		public void doDelete();
	}

	//置顶接口
	public interface  upListener {
		public void doUp();
	}


	private void initAction() {
		mEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (null != mRoadEntity) {
					RoadLineEntity entity = (RoadLineEntity) mRoadEntity;
					if(entity.getRoadlineId() != null){
						Intent intent = new Intent();
						intent.putExtra("roadLineId", entity.getRoadlineId());
						if("edit".equals(entity.getRoadlineStatus())){
							intent.putExtra("roadLineStuatus", "edit");
						}
						intent.setClass(context,
								PublishServicesActivity.class);
						context.startActivity(intent);
						dismiss();
					}
//					if (entity.getRoadlineStatus().equals("success")
//							|| entity.getRoadlineStatus().equals("failure")) {
//						Intent intent = new Intent();
//						intent.putExtra("roadLineId", entity.getRoadlineId());
//						intent.putExtra("from", "framgent");
//						intent.setClass(context,
//								PublishServicesActivity.class);
//						context.startActivity(intent);
//						dismiss();
//					} else if (entity.getRoadlineStatus().equals("edit")) {
//						// 编辑中
//						Intent intent = new Intent();
//						intent.putExtra("roadLineId", entity.getRoadlineId());
//						intent.putExtra("from", "framgent");
//						intent.putExtra("roadLineStuatus", "edit");
//						intent.setClass(context,
//								PublishServicesActivity.class);
//						context.startActivity(intent);
//						dismiss();
//					} else {
//						CustomToast.makeText(context, 0,
//								"该路线正在审核，不可编辑", 300).show();
//						dismiss();
//					}
				}
			}
		});

		mUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				upListener.doUp();
				dismiss();
			}
		});
		mDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteListener.doDelete();
				dismiss();
			}
		});
		mShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showShare(mRoadEntity);
				dismiss();
			}
		});
	}



	public void setRoadlineEntity(RoadLineEntity roadlineId) {
		this.mRoadEntity = roadlineId;
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
										BitmapDrawable d = new BitmapDrawable(context.getResources().openRawResource( + R.drawable.app_icon));
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
										BitmapDrawable d = new BitmapDrawable(context.getResources().openRawResource(+ R.drawable.app_icon));
										paramsToShare.setImageData(d.getBitmap());
									}
								}
								paramsToShare.setUrl(wxShareUrl);
								paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
				// 点击新浪微博
				if ("SinaWeibo".equals(platform.getName())) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								URL urlStr = new URL(APPRestClient.SERVER_IP
										+ "images/roadlineBackgroud/" + bgaddress);
								HttpURLConnection connection = (HttpURLConnection) urlStr.openConnection();
								int state = connection.getResponseCode();
								paramsToShare.setText(entity.getRoadlineTitle() + shareUrl);
								paramsToShare.setShareType(Platform.SHARE_WEBPAGE);// 一定要设置分享属性
								paramsToShare.setImagePath("");
								if (state == 200) {
									paramsToShare.setImageUrl(APPRestClient.SERVER_IP
											+ "images/roadlineBackgroud/" + bgaddress);
									paramsToShare.setUrl(shareUrl);
								} else {
									// 取不到图片使用默认logo
									BitmapDrawable d = new BitmapDrawable(context.getResources().openRawResource( + R.drawable.app_icon));
									LogUtils.saveFile(d.getBitmap(),"default.png");
									paramsToShare.setImagePath(LogUtils.WEIBO_PATH+"default.png");
								}
							} catch (Exception e) {
								e.printStackTrace();;
							}
						}
					}).start();

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
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_ACTION_CCALLBACK: {
				switch (msg.arg1) {
					case 1: { // 成功, successful notification
						CustomToast.makeText(context, 0, "分享成功", 300)
								.show();
					}
					break;
					case 2: { // 失败, fail notification
						CustomToast.makeText(context, 0, "分享完成", 300)
								.show();
					}
					break;
					case 3: { // 取消, cancel notification
						CustomToast.makeText(context, 0, "分享取消", 300)
								.show();
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
}
