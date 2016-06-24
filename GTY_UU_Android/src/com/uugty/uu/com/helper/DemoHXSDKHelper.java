/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uugty.uu.com.helper;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.chat.help.CommonUtils;
import com.uugty.uu.com.helper.HXNotifier.HXNotificationInfoProvider;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.entity.TestEntity;
import com.uugty.uu.entity.User;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.MainActivity;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 * 
 * @author easemob
 * 
 */
public class DemoHXSDKHelper extends HXSDKHelper {
	/**
	 * EMEventListener
	 */
	protected EMEventListener eventListener = null;

	/**
	 * contact list in cache
	 */
	private Map<String, User> contactList;

	private Context context;

	/**
	 * robot list in cache
	 */
	// private CallReceiver callReceiver;

	/**
	 * 用来记录foreground Activity
	 */

	@Override
	public synchronized boolean onInit(Context context) {
		this.context = context;
		if (super.onInit(context)) {
			return true;
		}

		return false;
	}

	@Override
	protected void initHXOptions() {
		super.initHXOptions();

		// you can also get EMChatOptions to set related SDK options
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		options.allowChatroomOwnerLeave(getModel()
				.isChatroomOwnerLeaveAllowed());
	}

	@Override
	protected void initListener() {
		super.initListener();
		IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
				.getIncomingCallBroadcastAction());
		/*
		 * if(callReceiver == null){ callReceiver = new CallReceiver(); }
		 */

		// 注册通话广播接收者
		// appContext.registerReceiver(callReceiver, callFilter);
		// 注册消息事件监听
		initEventListener();
	}


	/**
	 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理 activityList.size()
	 * <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
	 */
	protected void initEventListener() {
		eventListener = new EMEventListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = null;
				if (event.getData() instanceof EMMessage) {
					message = (EMMessage) event.getData();
				}

				switch (event.getEvent()) {
				case EventNewMessage:
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if(ActivityCollector.activites.size() <= 0){
						HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                    }
					break;
				case EventOfflineMessage:
					if(ActivityCollector.activites.size() <= 0){
						List<EMMessage> messages = (List<EMMessage>) event
								.getData();
						HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                    }
                    break;
				// below is just giving a example to show a cmd toast, the app
				// should not follow this
				// so be careful of this
				case EventNewCMDMessage: {
					// 获取消息body
					CmdMessageBody cmdMsgBody = (CmdMessageBody) message
							.getBody();
					final String action = cmdMsgBody.action;// 获取自定义action

					// 获取扩展属性 此处省略
					final String str = appContext
							.getString(R.string.reslsf_content);

					final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
					IntentFilter cmdFilter = new IntentFilter(
							CMD_TOAST_BROADCAST);

					if (broadCastReceiver == null) {
						broadCastReceiver = new BroadcastReceiver() {

							@Override
							public void onReceive(Context context, Intent intent) {
								// TODO Auto-generated method stub
								CustomToast
										.makeText(
												appContext,
												0,
												intent.getStringExtra("cmd_value"),
												200).show();
							}
						};

						// 注册广播接收者
						appContext.registerReceiver(broadCastReceiver,
								cmdFilter);
					}

					Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
					broadcastIntent.putExtra("cmd_value", str + action);
					appContext.sendBroadcast(broadcastIntent, null);

					break;
				}
				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				// add other events in case you are interested in
				default:
					break;
				}

			}
		};

		EMChatManager.getInstance().registerEventListener(eventListener);

		EMChatManager.getInstance().addChatRoomChangeListener(
				new EMChatRoomChangeListener() {
					private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
					private final IntentFilter filter = new IntentFilter(
							ROOM_CHANGE_BROADCAST);
					private boolean registered = false;

					private void showToast(String value) {
						if (!registered) {
							// 注册广播接收者
							appContext.registerReceiver(
									new BroadcastReceiver() {

										@Override
										public void onReceive(Context context,
												Intent intent) {

											CustomToast
													.makeText(
															appContext,
															0,
															intent.getStringExtra("value"),
															200).show();
										}

									}, filter);

							registered = true;
						}

						Intent broadcastIntent = new Intent(
								ROOM_CHANGE_BROADCAST);
						broadcastIntent.putExtra("value", value);
						appContext.sendBroadcast(broadcastIntent, null);
					}

					@Override
					public void onChatRoomDestroyed(String roomId,
							String roomName) {
						showToast(" room : " + roomId + " with room name : "
								+ roomName + " was destroyed");
						Log.i("info", "onChatRoomDestroyed=" + roomName);
					}

					@Override
					public void onMemberJoined(String roomId, String participant) {
						showToast("member : " + participant
								+ " join the room : " + roomId);
						Log.i("info", "onmemberjoined=" + participant);

					}

					@Override
					public void onMemberExited(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " leave the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberExited=" + participant);

					}

					@Override
					public void onMemberKicked(String roomId, String roomName,
							String participant) {
						showToast("member : " + participant
								+ " was kicked from the room : " + roomId
								+ " room name : " + roomName);
						Log.i("info", "onMemberKicked=" + participant);

					}

				});
	}

	@Override
	protected HXSDKModel createModel() {
		return new DemoHXSDKModel(appContext);
	}

	// 下线通知
	@Override
	protected void onConnectionConflict() {
		// TODO Auto-generated method stub
		//app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)&&ActivityCollector.activites.size()==0) {
			Intent intent = new Intent(appContext, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 用这个标志，如果正在启动的Activity的Task已经在运行的话，那么，新的Activity将不会启动；代替的，当前Task会简单的移入前台
			MyApplication.getInstance().isConflict = true;
			appContext.startActivity(intent);
		}else{
			//弹出下线通知
			showConflictDialog();
		}
		
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	/**
	 * 显示帐号在别处登录dialog
	 */
	protected void showConflictDialog() {
		        final Activity activity = ActivityCollector.getTopActivity();
		        activity.runOnUiThread(new Runnable() {
		        	public void run() {
		 				MyApplication.getInstance().clearLoginData();
		 				DemoHXSDKHelper.getInstance().logout(null);
		 				sendLogoutRequest(activity);
		 				String st = activity.getResources().getString(
		 						R.string.hx_logoff_notification);
		 				if (!activity.isFinishing()) {
		 					// clear up global variables
		 					try {
		 						if (conflictBuilder == null)
		 							conflictBuilder = new android.app.AlertDialog.Builder(
		 									activity);
		 						conflictBuilder.setTitle(st);
		 						conflictBuilder
		 								.setMessage(R.string.hx_connect_conflict);
		 						conflictBuilder.setPositiveButton(R.string.ok,
		 								new DialogInterface.OnClickListener() {

		 									@Override
		 									public void onClick(DialogInterface dialog,
		 											int which) {
		 										dialog.dismiss();
		 										conflictBuilder = null;
		 										Intent intent = new Intent(activity,
		 												LoginActivity.class);
		 										intent.putExtra("offline", "offline");
		 										intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		 										activity.startActivity(intent);
		 									}
		 								});
		 						conflictBuilder.setCancelable(false);
		 						conflictBuilder.create().show();
		 						MyApplication.getInstance().isConflict = true;
		 					} catch (Exception e) {
		 					}
		 				}
		        	}
		        });
		       
	}

	
	public void sendLogoutRequest(final Context ctx) {
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.USER_LOGIN_OUT, params,
				new APPResponseHandler<TestEntity>(TestEntity.class, ctx) {
					@Override
					public void onSuccess(TestEntity result) {
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {
					}
				});

	}
	/*@Override
	public HXNotifier createNotifier() {
		return new HXNotifier() {
			public synchronized void onNewMsg(final EMMessage message) {
				if (EMChatManager.getInstance().isSlientMessage(message)) {
					return;
				}

				String chatUsename = null;
				List<String> notNotifyIds = null;
				// 获取设置的不提示新消息的用户或者群组ids
				if (message.getChatType() == ChatType.Chat) {
					chatUsename = message.getFrom();
					notNotifyIds = ((DemoHXSDKModel) hxModel)
							.getDisabledGroups();
				} else {
					chatUsename = message.getTo();
					notNotifyIds = ((DemoHXSDKModel) hxModel).getDisabledIds();
				}

				if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
					// 判断app是否在后台
					if (!EasyUtils.isAppRunningForeground(appContext)) {
						sendNotification(message, false);
					} else {
						sendNotification(message, true);

					}

					viberateAndPlayTone(message);
				}
			}
		};
	}*/

	/**
	 * get demo HX SDK Model
	 */
	public DemoHXSDKModel getModel() {
		return (DemoHXSDKModel) hxModel;
	}

	public boolean isRobotMenuMessage(EMMessage message) {

		try {
			JSONObject jsonObj = message
					.getJSONObjectAttribute(Constant.UUCHAT_ROOM);
			if (jsonObj.has("choice")) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public String getRobotMenuMessageDigest(EMMessage message) {
		String title = "";
		try {
			JSONObject jsonObj = message
					.getJSONObjectAttribute(Constant.UUCHAT_ROOM);
			if (jsonObj.has("choice")) {
				JSONObject jsonChoice = jsonObj.getJSONObject("choice");
				title = jsonChoice.getString("title");
			}
		} catch (Exception e) {
		}
		return title;
	}

	/**
	 * 自定义通知栏提示内容
	 * 
	 * @return
	 */
	@Override
	protected HXNotificationInfoProvider getNotificationListener() {
		// 可以覆盖默认的设置
		return new HXNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				// 修改标题,这里使用默认
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				// 设置小图标，这里为默认
				return 0;
			}

			@SuppressWarnings("null")
			@Override
			public String getDisplayedText(EMMessage message) {
				// 设置状态栏的消息提示，可以根据message的类型做相应提示
				String ticker = CommonUtils.getMessageDigest(message,
						appContext);
				String nick = null;
				if (message.getType() == Type.TXT) {
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				try {
					nick = message.getStringAttribute("nick");
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!TextUtils.isEmpty(nick)&&nick.equals("sys")) {
					return ticker;
				}else if(!TextUtils.isEmpty(nick)){
					return nick + ": " + ticker;
				} else {
					return "uu客" + ": " + ticker;
				}

				// return message.getUserName() + ": " + ticker;
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum,
					int messageNum) {
				return null;
				// return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// 设置点击通知栏跳转事件
				/*
				 * Intent intent = new Intent(appContext,
				 * UUChatEndActivity.class); // 有电话时优先跳转到通话页面
				 * 
				 * ChatType chatType = message.getChatType(); if (chatType ==
				 * ChatType.Chat) { // 单聊信息 intent.putExtra("userId",
				 * message.getFrom()); intent.putExtra("chatType", "1"); }
				 */
				// 跳转到住页面
				Intent intent = new Intent(appContext, MainActivity.class);
				intent.putExtra("fromPage", "hx");
				return intent;
			}
		};
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	@Override
	public void logout(final EMCallBack callback) {
		endCall();
		super.logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				setContactList(null);
				// getUserProfileManager().reset();
				// getModel().closeDB();
				if (callback != null) {
					callback.onSuccess();
				}
			}

			@Override
			public void onError(int code, String message) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onError(code, message);
				}
			}

			@Override
			public void onProgress(int progress, String status) {
				// TODO Auto-generated method stub
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

		});
	}

	void endCall() {
		try {
			EMChatManager.getInstance().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
