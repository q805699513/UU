package com.uugty.uu.shop;



import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.utils.UIHandler;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.share.onekeyshare.OnekeyShare;
import com.uugty.uu.common.share.onekeyshare.ShareContentCustomizeCallback;
import com.uugty.uu.entity.RoadLineEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.map.OpenShopActivity;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.util.LogUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class NotVipShopActivity extends BaseActivity implements OnClickListener
		,
		PlatformActionListener,Handler.Callback {

    private TopBackView titleView;
	private LinearLayout mServiceControl;//服务管理
	private LinearLayout mShare;//分享赚钱
	private Button mPublishService;//发布服务
	private TextView mOpenShop;//立即成为小u

	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;
	@Override
	protected int getContentLayout() {

		return R.layout.activity_notvipshop;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.open_shop_title);
		titleView.setTitle("我的小店");
		mServiceControl = (LinearLayout) findViewById(R.id.notvip_shop_service_control);
		mShare = (LinearLayout) findViewById(R.id.notvip_shop_share);
		mPublishService = (Button) findViewById(R.id.notvip_shop_publish);
		mOpenShop = (TextView) findViewById(R.id.notvip_shop_button);
	}

	@Override
	protected void initAction() {
		mServiceControl.setOnClickListener(this);
		mShare.setOnClickListener(this);
		mPublishService.setOnClickListener(this);
		mOpenShop.setOnClickListener(this);
	}

	@Override
	protected void initData() {
	}

	
	@Override
	public void onClick(View v) {

		Intent intent = new  Intent();
		switch (v.getId()){
			case R.id.notvip_shop_button:
				intent.setClass(this,
						OpenShopActivity.class);
				startActivity(intent);
				break;
			case  R.id.notvip_shop_publish:
				intent.setClass(this,PublishServicesActivity.class);
				intent.putExtra("from", "framgent");
				startActivity(intent);
				break;
			case R.id.notvip_shop_service_control:
				intent.setClass(this,ShopControlActivity.class);
				startActivity(intent);
				break;
			case R.id.notvip_shop_share:
				RoadLineEntity mRoadEntity = new RoadLineEntity();
				showShare(mRoadEntity);
				break;
			default:
				break;
		}
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
		ShareSDK.initSDK(ctx);
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
									BitmapDrawable d = new BitmapDrawable(getResources().openRawResource( + R.drawable.app_icon));
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
		oks.show(ctx);
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
						CustomToast.makeText(ctx, 0, "分享成功", 300)
								.show();
					}
					break;
					case 2: { // 失败, fail notification
						CustomToast.makeText(ctx, 0, "分享完成", 300)
								.show();
					}
					break;
					case 3: { // 取消, cancel notification
						CustomToast.makeText(ctx, 0, "分享取消", 300)
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
