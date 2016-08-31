package com.uugty.uu.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.umeng.analytics.MobclickAgent;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.helper.DemoHXSDKHelper;
import com.uugty.uu.com.helper.HXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.AutoLogin;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.TestEntity;
import com.uugty.uu.friendstask.FriendsDynamicFragment;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.popwindow.MoreWindow;
import com.uugty.uu.util.UUConfig;

import java.util.List;


public class MainActivity extends BaseActivity implements OnClickListener,
		EMEventListener {

	private LinearLayout mainRel, messageRel, mapRel, dialogRel;
	private ImageView home_main_image, home_message_image, home_map_image,
			home_me_image;
	public static int Sb;
	private Fragement4 fragment4;
	private Fragment1 fragment1;
	private Fragement2 fragment2;
	private FriendsDynamicFragment lablefragment;
	private String toPage = "";
	private String fromPage = "";
	private String city=""; //城市
	private String publish;
	// 未显示的消息条数
	private TextView unReadMsgText;
	private TextView radio_textview1, radio_textview2, radio_textview3,
			radio_textview4;
	private ImageView mImageView;
	private FragmentTransaction transaction;
	private Handler mHandler;
	private MoreWindow mMoreWindow;
	// 向主页发送消息 改变地点
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.main;
	}

	@Override
	protected void initGui() {
		mainRel = (LinearLayout) findViewById(R.id.main_rel);
		messageRel = (LinearLayout) findViewById(R.id.message_rel);
		mapRel = (LinearLayout) findViewById(R.id.map_rel);
		dialogRel = (LinearLayout) findViewById(R.id.dialog_rel);
		home_main_image = (ImageView) findViewById(R.id.radio_button1);
		home_message_image = (ImageView) findViewById(R.id.radio_button2);
		home_map_image = (ImageView) findViewById(R.id.radio_button3);
		home_me_image = (ImageView) findViewById(R.id.radio_button4);
		mImageView = (ImageView) findViewById(R.id.home_bottom_img);
		radio_textview1 = (TextView) findViewById(R.id.radio_textview1);
		radio_textview2 = (TextView) findViewById(R.id.radio_textview2);
		radio_textview3 = (TextView) findViewById(R.id.radio_textview3);
		radio_textview4 = (TextView) findViewById(R.id.radio_textview4);
		unReadMsgText = (TextView) findViewById(R.id.unread_msg_number);
		fromPage = getIntent().getStringExtra("fromPage");
	}

	@Override
	protected void initAction() {
		mainRel.setOnClickListener(this);
		messageRel.setOnClickListener(this);
		mapRel.setOnClickListener(this);
		dialogRel.setOnClickListener(this);
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(MyApplication.getInstance().isLogin()) {
					showMoreWindow(v);
				}else {
					Intent intent = new Intent();
					intent.putExtra("topage", MainActivity.class.getName());
					intent.setClass(MainActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		// setContactListener监听联系人的变化等
		Sb = R.id.main_rel;
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			EMContactManager.getInstance().setContactListener(
					new MyContactListener());
			EMChat.getInstance().setAppInited();
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// 显示dialog
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.appstart.AppStartActivity");
			}
		}, 1000);
	}

	private void showMoreWindow(View view) {
		if (null == mMoreWindow) {
			mMoreWindow = new MoreWindow(this);
			mMoreWindow.init();
		}

		mMoreWindow.showMoreWindow(view,100);
	}
	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}
	
	//解决fragment 重叠问题
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	}
	

	private void sendRequest() {
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.USER_LOGIN_OUT, params,
				new APPResponseHandler<TestEntity>(TestEntity.class, this) {
					@Override
					public void onSuccess(TestEntity result) {
						SharedPreferenceUtil.getInstance(ctx).setString("JPushRegistId", "null");
						SharedPreferenceUtil.getInstance(ctx).setString("JPushLoginRegistId", "null");
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {

					}
				});

	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - exitTime > 3000) {

				CustomToast.makeText(this, 0, "再按一次退出程序", Toast.LENGTH_SHORT)
						.show();

				exitTime = System.currentTimeMillis();
			} else {
//				sendRequest();
				MobclickAgent.onKillProcess(this);
				exitClient();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.login.ForgetActivity");
		unReadMsgText.setVisibility(View.INVISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					if (!MyApplication.getInstance().isConflict) {
						updateUnreadLabel();
						EMChatManager.getInstance().activityResumed();
					}
					EMChatManager
							.getInstance()
							.registerEventListener(
									MainActivity.this,
									new EMNotifierEvent.Event[] {
											EMNotifierEvent.Event.EventNewMessage,
											EMNotifierEvent.Event.EventOfflineMessage,
											EMNotifierEvent.Event.EventConversationListChanged });
				}
			}
		}, 800);
        if(null!=publish&&publish.equals("publish")){
			mapRel.performClick();
        	lablefragment.onRefresh();
        }
		if (null != toPage && toPage.equals("order")) {
			mainRel.performClick();
			toPage = "";
		} else if (!TextUtils.isEmpty(fromPage) && fromPage.equals("hx")) {
			messageRel.performClick();
			fromPage = "";
		} else {
			findViewById(Sb).performClick();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		city=intent.getStringExtra("location");//搜索城市的地址
		UUConfig.INSTANCE.setmCtity(city);
		toPage = intent.getStringExtra("toPage");
		publish =intent.getStringExtra("publish");
		ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.login.LoginActivity");
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (conversation.getType() == EMConversationType.ChatRoom)
				chatroomUnreadMsgCount = chatroomUnreadMsgCount
						+ conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal - chatroomUnreadMsgCount;
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unReadMsgText.setText(String.valueOf(count));
			unReadMsgText.setVisibility(View.VISIBLE);
		} else {
			unReadMsgText.setVisibility(View.INVISIBLE);
			/*
			 * if (fragment2 != null) { fragment2.refreshNoMessage(); }
			 */
		}
	}

	@Override
	public void onClick(View v) {
		transaction = getSupportFragmentManager().beginTransaction();
		hideFragments(transaction);
		switch (v.getId()) {
		case R.id.main_rel:
			Sb = R.id.main_rel;
			// 代码写傻逼了
			home_main_image.setImageResource(R.drawable.find_two);
			home_message_image.setImageResource(R.drawable.message_one);
			home_map_image.setImageResource(R.drawable.map_one);
			home_me_image.setImageResource(R.drawable.user_one);

			radio_textview1.setTextColor(Color.parseColor("#00a1d9"));
			radio_textview2.setTextColor(Color.parseColor("#98999a"));
			radio_textview3.setTextColor(Color.parseColor("#98999a"));
			radio_textview4.setTextColor(Color.parseColor("#98999a"));
			if (fragment1 == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				fragment1 = new Fragment1();
				transaction.add(R.id.home_layout, fragment1);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragment1);
				if(city!=null&&!city.equals("")){
				Message msg = new Message();
				msg.obj=city;
				msg.what = 1;
				mHandler.sendMessage(msg);
				}
			}

			transaction.commitAllowingStateLoss();
			break;
		case R.id.message_rel:
			
			home_main_image.setImageResource(R.drawable.find_one);
			home_message_image.setImageResource(R.drawable.message_two);
			home_map_image.setImageResource(R.drawable.map_one);
			home_me_image.setImageResource(R.drawable.user_one);

			radio_textview1.setTextColor(Color.parseColor("#98999a"));
			radio_textview2.setTextColor(Color.parseColor("#00a1d9"));
			radio_textview3.setTextColor(Color.parseColor("#98999a"));
			radio_textview4.setTextColor(Color.parseColor("#98999a"));
			
			if (MyApplication.getInstance().isLogin()) {
				Sb = R.id.message_rel;
				if (fragment2 == null) {
					fragment2 = new Fragement2();
					transaction.add(R.id.home_layout, fragment2);
				} else {
					transaction.show(fragment2);
				}
			} else if (!TextUtils.isEmpty(fromPage) && fromPage.equals("hx")) {
				// 可自动登录
				if (AutoLogin.getInstance(this).autoLoginAlbe()) {
					AutoLogin.getInstance(this).login();
					Sb = R.id.message_rel;
					if (fragment2 == null) {
						fragment2 = new Fragement2();
						transaction.add(R.id.home_layout, fragment2);
					} else {
						transaction.show(fragment2);
						/*
						 * transaction.remove(fragment2); fragment2 = new
						 * Fragement2(); transaction.add(R.id.home_layout,
						 * fragment2);
						 */
					}
				} else {
					Intent intent = new Intent();
					intent.setClass(this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			transaction.commitAllowingStateLoss();
			break;
		case R.id.map_rel:
			Sb = R.id.map_rel;
			home_main_image.setImageResource(R.drawable.find_one);
			home_message_image.setImageResource(R.drawable.message_one);
			home_map_image.setImageResource(R.drawable.map_two);
			home_me_image.setImageResource(R.drawable.user_one);

			radio_textview3.setTextColor(Color.parseColor("#00a1d9"));
			radio_textview2.setTextColor(Color.parseColor("#98999a"));
			radio_textview1.setTextColor(Color.parseColor("#98999a"));
			radio_textview4.setTextColor(Color.parseColor("#98999a"));
			
			if (lablefragment == null) {
				lablefragment = new FriendsDynamicFragment();
				transaction.add(R.id.home_layout, lablefragment);
			} else {
				transaction.show(lablefragment);
			}
			transaction.commitAllowingStateLoss();
			break;
		case R.id.dialog_rel:
			Sb = R.id.dialog_rel;
			home_main_image.setImageResource(R.drawable.find_one);
			home_message_image.setImageResource(R.drawable.message_one);
			home_map_image.setImageResource(R.drawable.map_one);
			home_me_image.setImageResource(R.drawable.user_teo);

			radio_textview4.setTextColor(Color.parseColor("#00a1d9"));
			radio_textview3.setTextColor(Color.parseColor("#98999a"));
			radio_textview2.setTextColor(Color.parseColor("#98999a"));
			radio_textview1.setTextColor(Color.parseColor("#98999a"));
			
			if (fragment4 == null) {
				fragment4 = new Fragement4();
				transaction.add(R.id.home_layout, fragment4);
			} else {
				transaction.show(fragment4);
			}
			transaction.commitAllowingStateLoss();
			break;
		}
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (fragment1 != null) {
			transaction.hide(fragment1);
		}
		if (fragment4 != null) {
			transaction.hide(fragment4);
		}
		if (fragment2 != null) {
			transaction.hide(fragment2);
		}
		if (lablefragment != null) {
			transaction.hide(lablefragment);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// home_shop_rb.performClick();
		if(publish != null){
			publish = "";
		}
	}

	/**
	 * 监听事件
	 */
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

			refreshUI();
			break;
		}

		case EventOfflineMessage: {
			refreshUI();
			break;
		}

		case EventConversationListChanged: {
			refreshUI();
			break;
		}

		default:
			break;
		}
	}

	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (Sb == R.id.message_rel) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (fragment2 != null) {
						fragment2.refresh();
					}
				}
			}
		});
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/***
	 * 好友变化listener
	 * 
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 增加了联系人时回调此方法
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 收到好友邀请
		}

		@Override
		public void onContactAgreed(String username) {
			// 好友请求被同意
		}

		@Override
		public void onContactRefused(String username) {
			// 好友请求被拒绝
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/* 在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment */
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1000:
				if (null != fragment1)
					fragment1.onActivityResult(requestCode, resultCode,
							data);
				break;
			case 100:
				if (null != fragment1)
					fragment1.onActivityResult(requestCode, resultCode,
							data);
				break;

			default:
				break;
			}
		}

	}

}
