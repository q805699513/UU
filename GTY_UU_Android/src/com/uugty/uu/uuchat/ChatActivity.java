package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.VoiceRecorder;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.chat.help.CommonUtils;
import com.uugty.uu.chat.help.ExpandGridView;
import com.uugty.uu.chat.help.ExpressionAdapter;
import com.uugty.uu.chat.help.GroupRemoveListener;
import com.uugty.uu.chat.help.SmileUtils;
import com.uugty.uu.chat.help.VoicePlayClickListener;
import com.uugty.uu.com.helper.HXSDKHelper;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.SelectPictureActivity;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.entity.GroupChatSimpleEntity;
import com.uugty.uu.entity.UserSimpleEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.viewpage.adapter.ChatMessageAdapter;
import com.uugty.uu.viewpage.adapter.ExpressionPagerAdapter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements OnClickListener,
		EMEventListener {

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_FORWARD = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;

	// 聊天对象个人头像
	public String toChatavatar;
	// 聊天对象姓名
	public String toChatUsername;
	public String toChatUserId;
	// 标题栏
	// public TopBackView topTitle;
	// 语音布局
	public View recordingContainer;
	public ImageView micImage;
	public TextView recordingHint;
	// 聊天记录
	public ListView listView;
	// 消息输入框
	public EditText mEditTextContent;
	// 切换成键盘输入
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private RelativeLayout edittext_layout;
	private LinearLayout bottomLin;
	// 发送按钮
	private Button buttonSend;
	private Button btnMore;
	// 语音按住说话
	private View buttonPressToSpeak;
	// 表情布局
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ViewPager expressionViewpager;
	// 表情按钮
	private ImageView iv_emoticons_normal;
	// 底部表情和红包
	private View more;
	private Drawable[] micImages;
	private List<String> reslist;
	private VoiceRecorder voiceRecorder;
	public static ChatActivity activityInstance = null;
	// 录音
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private int chatType;
	// 单聊
	public static final int CHATTYPE_SINGLE = 1;
	// 群聊
	public static final int CHATTYPE_GROUP = 2;
	// 聊天室
	public static final int CHATTYPE_CHATROOM = 3;
	private EMConversation conversation;
	private ChatMessageAdapter adapter;
	// 下拉刷新
	private SwipeRefreshLayout swipeRefreshLayout;
	// 加载20条数据
	private final int pagesize = 20;
	// 加载中
	private boolean isloading;
	private boolean haveMoreData = true;
	// 复制
	private ClipboardManager clipboard;
	// 键盘
	private InputMethodManager manager;
	public String playMsgId;
	private View lineView;
	private RelativeLayout noMessageRel, chatContentRel;
	private LinearLayout backLin;
	private TextView titleTextView;
	// 转发
	private String forward_msg_id;
	private File cameraFile;
	public static final int REQUEST_CODE_CAMERA = 18; // 拍照
	public static final int REQUEST_CODE_LOCAL = 19;
	public EMGroup group;
	private GroupListener groupListener;
	private static List<String> ratiolist = new ArrayList<String>();

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		activityInstance = this;
		return R.layout.activity_chat;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		// topTitle = (TopBackView) findViewById(R.id.chat_top_title);
		backLin = (LinearLayout) findViewById(R.id.tabar_back);
		titleTextView = (TextView) findViewById(R.id.chat_top_title_content);
		bottomLin = (LinearLayout) findViewById(R.id.chat_bottom_lin);
		recordingContainer = findViewById(R.id.recording_container);
		micImage = (ImageView) findViewById(R.id.mic_image);
		recordingHint = (TextView) findViewById(R.id.recording_hint);
		listView = (ListView) findViewById(R.id.list);
		mEditTextContent = (EditText) findViewById(R.id.chat_sendmessage_paste_edittext);
		buttonSetModeKeyboard = findViewById(R.id.chat_set_mode_keyboard_btn);
		buttonSetModeVoice = findViewById(R.id.chat_set_mode_voice_btn);
		edittext_layout = (RelativeLayout) findViewById(R.id.chat_edittext_layout);
		buttonSend = (Button) findViewById(R.id.btn_send);
		buttonPressToSpeak = findViewById(R.id.chat_press_to_speak_lin);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		iv_emoticons_normal = (ImageView) findViewById(R.id.chat_sendmessage_smail_btn);
		btnMore = (Button) findViewById(R.id.chat_btn_more);
		more = findViewById(R.id.more);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		lineView = findViewById(R.id.chat_edittext_line);
		noMessageRel = (RelativeLayout) findViewById(R.id.fragment_no_message_rel);
		chatContentRel = (RelativeLayout) findViewById(R.id.chate_content_rel);
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14) };
		// 表情list
		reslist = getExpressionRes(42);
		// 初始化标签viewpager
		List<View> views = new ArrayList<View>();
		View view1 = getGridChildView(1);
		View view2 = getGridChildView(2);
		View view3 = getGridChildView(3);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		// 录音图片
		voiceRecorder = new VoiceRecorder(micImageHandler);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {
		// 按住说话
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lineView.setBackgroundColor(getResources().getColor(
							R.color.select_color));

				} else {
					lineView.setBackgroundColor(getResources().getColor(
							R.color.chat_press_speak_btn_bg));
				}

			}
		});

		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				lineView.setBackgroundColor(getResources().getColor(
						R.color.select_color));
			}
		});

		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// 加载历史数据

		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (listView.getFirstVisiblePosition() == 0
								&& !isloading && haveMoreData) {
							List<EMMessage> messages;
							try {
								if (chatType == CHATTYPE_SINGLE) {
									messages = conversation.loadMoreMsgFromDB(
											adapter.getItem(0).getMsgId(),
											pagesize);
								} else {
									messages = conversation
											.loadMoreGroupMsgFromDB(adapter
													.getItem(0).getMsgId(),
													pagesize);
								}
							} catch (Exception e1) {
								swipeRefreshLayout.setRefreshing(false);
								return;
							}

							if (messages.size() > 0) {
								adapter.notifyDataSetChanged();
								adapter.refreshSeekTo(messages.size() - 1);
								if (messages.size() != pagesize) {
									haveMoreData = false;
								}
							} else {
								haveMoreData = false;
							}

							isloading = false;

						} else {
							CustomToast.makeText(
									ChatActivity.this,
									0,
									getResources().getString(
											R.string.no_more_messages),
									Toast.LENGTH_SHORT).show();
						}
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 500);
			}
		});
		backLin.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
				if (more.getVisibility() == View.VISIBLE) {
					more.setVisibility(View.GONE);
					iv_emoticons_normal.setVisibility(View.VISIBLE);
				}
				finish();
			}
		});
		iv_emoticons_normal.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// 判断单聊还是群聊
		chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
		toChatUserId = getIntent().getStringExtra("userId");
		toChatUsername = getIntent().getStringExtra("userName");
		if (chatType == CHATTYPE_SINGLE) {
			String toFrom = getIntent().getStringExtra("toFrom");
			if (null != toFrom && toFrom.equals("UUChatOKActivity")) {
				if (btnContainer.getVisibility() == View.VISIBLE) {
					btnContainer.setVisibility(View.GONE);
				}
				toChatavatar = getIntent().getStringExtra("avatar");
				// 红包金额
				String price = getIntent().getStringExtra("price");
				// 留言
				String red_message = getIntent().getStringExtra("message");
				// 红包Id
				String red_ID = getIntent().getStringExtra("red_id");
				sendRedHot(price, red_message, red_ID);
			}
			if (toChatUserId.equals("admin")) {
				bottomLin.setVisibility(View.GONE);
				if ("noMessage".equals(getIntent().getStringExtra("noMessage"))) {
					chatContentRel.setVisibility(View.GONE);
					noMessageRel.setVisibility(View.VISIBLE);
				}
			}
			if (toChatUserId.equals("requirementpublisher")) {
				bottomLin.setVisibility(View.GONE);
				if ("noMessage".equals(getIntent().getStringExtra("noMessage"))) {
					chatContentRel.setVisibility(View.GONE);
					noMessageRel.setVisibility(View.VISIBLE);
				}
			}
		} else {
			// 群聊
			findViewById(R.id.chat_container_to_group).setVisibility(
					View.VISIBLE);
			findViewById(R.id.chat_red_package_lin).setVisibility(View.GONE);

			if (chatType == CHATTYPE_GROUP) {
				onGroupViewCreation();
			}
		}

		if (chatType != CHATTYPE_CHATROOM) {
			onConversationInit();
			if (chatType == CHATTYPE_SINGLE) {
				getSimpleUserInfo();
			}
			if (chatType == CHATTYPE_GROUP) {
				getSimpleGroupRequest();
			}
			// onListViewCreation();

			// show forward message if the message is not null

			forward_msg_id = getIntent().getStringExtra("forward_msg_id");

		}
	}

	private void getSimpleGroupRequest() {
		RequestParams params = new RequestParams();
		params.add("groupEasemobID", toChatUserId);
		APPRestClient.post(ChatActivity.this, ServiceCode.GROUP_DETAIL_SIMPLE,
				params, new APPResponseHandler<GroupChatSimpleEntity>(
						GroupChatSimpleEntity.class, ChatActivity.this) {
					@Override
					public void onSuccess(GroupChatSimpleEntity result) {
						toChatUsername = result.getOBJECT().getGroupName();
						titleTextView.setText(toChatUsername);
						onListViewCreation();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getSimpleGroupRequest();
						} else {
							CustomToast.makeText(ChatActivity.this, 0,
									errorMsg, 300).show();
						}
					}
				});

	}

	public void getSimpleUserInfo() {
		RequestParams reqestparm = new RequestParams();
		reqestparm.add("userId", toChatUserId);
		APPRestClient.post(ChatActivity.this, ServiceCode.USER_SIMPLE_MESSAGE,
				reqestparm, new APPResponseHandler<UserSimpleEntity>(
						UserSimpleEntity.class, ChatActivity.this) {
					@Override
					public void onSuccess(UserSimpleEntity result) {
						// TODO Auto-generated method stub
						// 判断好友是否有名字
						if (null != result.getOBJECT()) {
							if (toChatUserId.equals("admin")) {
								titleTextView.setText("系统通知");
							}else if(toChatUserId.equals("requirementpublisher")){
								titleTextView.setText("旅行定制");
							} else if (result.getOBJECT().getUserName()
									.equals(null)
									|| result.getOBJECT().getUserName()
											.equals("")) {
								toChatUsername = "小u";
								titleTextView.setText("与" + toChatUsername
										+ "聊天");
							} else {
								toChatUsername = result.getOBJECT()
										.getUserName();
								titleTextView.setText("与" + toChatUsername
										+ "聊天");
							}
							if (result.getOBJECT().getUserAvatar().equals("")
									|| result.getOBJECT().getUserAvatar()
											.equals(null)) {
								toChatavatar = "";
							} else {
								toChatavatar = result.getOBJECT()
										.getUserAvatar();
							}
						} else {
							toChatavatar = "";
							toChatUsername = "小u";
							titleTextView.setText("与" + toChatUsername + "聊天");
						}
						onListViewCreation();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getSimpleUserInfo();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ChatActivity.this)
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
														finish();
													}
												}).show();
							}
						}
					}

					@Override
					public void onFinish() {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								// 显示dialog
								ActivityCollector
										.removeSpecifiedActivity("com.uugty.uu.uuchat.ChatFriendsActivity");
								ActivityCollector
										.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatPaypriceActivity");
							}
						}, 500);

					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.refresh();
		}
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck });
	}

	@Override
	protected void onStop() {
		// unregister this event listener when this activity enters the
		// background
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying
				&& VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		if (groupListener != null) {
			EMGroupManager.getInstance().removeGroupChangeListener(
					groupListener);
		}
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);
	}

	protected void onConversationInit() {
		if (chatType == CHATTYPE_SINGLE) {
			conversation = EMChatManager.getInstance().getConversationByType(
					toChatUserId, EMConversationType.Chat);
		} else if (chatType == CHATTYPE_GROUP) {
			conversation = EMChatManager.getInstance().getConversationByType(
					toChatUserId, EMConversationType.GroupChat);
		} else if (chatType == CHATTYPE_CHATROOM) {
			conversation = EMChatManager.getInstance().getConversationByType(
					toChatUserId, EMConversationType.ChatRoom);
		}

		// 把此会话的未读数置为0
		conversation.markAllMessagesAsRead();

		// 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
		// 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
		final List<EMMessage> msgs = conversation.getAllMessages();
		int msgCount = msgs != null ? msgs.size() : 0;
		if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
			String msgId = null;
			if (msgs != null && msgs.size() > 0) {
				msgId = msgs.get(0).getMsgId();
			}
			if (chatType == CHATTYPE_SINGLE) {
				conversation.loadMoreMsgFromDB(msgId, pagesize);
			} else {
				conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
			}
		}
	}

	protected void onListViewCreation() {
		adapter = new ChatMessageAdapter(ChatActivity.this, toChatUserId,
				toChatavatar, toChatUsername, chatType);
		// 显示消息
		listView.setAdapter(adapter);

		adapter.refreshSelectLast();

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
				return false;
			}
		});
		if (forward_msg_id != null) {
			// 显示发送要转发的消息
			forwardMessage(forward_msg_id);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data
						.getIntExtra("position", -1)));
				// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
				// ((TextMessageBody) copyMsg.getBody()).getMessage()));
				clipboard.setText(((TextMessageBody) copyMsg.getBody())
						.getMessage());
				break;

			case RESULT_CODE_FORWARD: // 转发消息
				EMMessage forwardMsg = (EMMessage) adapter.getItem(data
						.getIntExtra("position", 0));
				Intent intent = new Intent(this, ChatFriendsActivity.class);
				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
				intent.putExtra("toForm", "ChatActivity");
				startActivity(intent);

				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists())
					sendPicture(cameraFile.getAbsolutePath());
			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				ArrayList<String> temp = (ArrayList<String>) data
						.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
				for (int i = 0; i < temp.size(); i++) {
					File file = new File(temp.get(i));
					if (file.exists()) {
						Uri selectedImage = Uri.fromFile(file);
						sendPicByUri(selectedImage);
					}
				}
			}
		}
	}

	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		final EMMessage forward_msg = EMChatManager.getInstance().getMessage(
				forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			if (forward_msg.getStringAttribute(Constant.MESSAGE_ATTR_CUSTOMER,
					null) != null) {
				String customId = forward_msg.getStringAttribute(
						Constant.MESSAGE_ATTR_CUSTOMER, "");
				TextMessageBody txtBody = (TextMessageBody) forward_msg
						.getBody();
				String customMark = txtBody.getMessage().substring(
						txtBody.getMessage().lastIndexOf(",") + 1);
				String messagePrice = txtBody.getMessage().substring(0,
						txtBody.getMessage().lastIndexOf("," + customMark));
				String customBudget = messagePrice.substring(messagePrice
						.lastIndexOf(",") + 1);
				String customDestination = messagePrice.substring(0,
						messagePrice.lastIndexOf("," + customBudget));

				sendCustom(customId, customDestination, customBudget,
						customMark);
			} else {
				String content = ((TextMessageBody) forward_msg.getBody())
						.getMessage();
				sendText(content);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 *            boolean resend
	 */
	public void sendText(String content) {

		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			TextMessageBody txtBody = new TextMessageBody(content);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP) {
				message.setChatType(ChatType.GroupChat);
				message.setAttribute("avatar", MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserAvatar());
			} else if (chatType == CHATTYPE_CHATROOM) {
				message.setChatType(ChatType.ChatRoom);
			}
			message.setAttribute("nick", MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserName());
			message.setAttribute("avatar",MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserAvatar());

			// 设置消息body
			message.addBody(txtBody);
			//设置
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(toChatUserId);
			// 把messgage加到conversation中
			conversation.addMessage(message);
			// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
			adapter.refreshSelectLast();
			mEditTextContent.setText("");

			setResult(RESULT_OK);

		}
	}

	// 获取表情的gridview
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.uuchat_facegridview, null);
		ExpandGridView grideView = (ExpandGridView) view
				.findViewById(R.id.uu_gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20,40));
		} else if( i == 3){
			list.addAll(reslist.subList(40,reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		grideView.setAdapter(expressionAdapter);
		grideView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，表情才可输入
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
						if (filename != "delete_expression") { // 不是删除键，显示表情
							Class clz = Class
									.forName("com.uugty.uu.chat.help.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									ChatActivity.this, (String) field.get(null)));
						} else {// 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {
								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return view;
	}

	// 保持屏幕常亮
	private PowerManager.WakeLock wakeLock;

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(
							R.string.Send_voice_need_sdcard_support);
					CustomToast.makeText(ChatActivity.this, st4,
							Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener
								.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUserId,
							getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					CustomToast.makeText(ChatActivity.this,
							R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					// recordingHint.setBackgroundColor(R.color.grid_kuan);
					// recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(
							R.string.Recording_without_permission);
					String st2 = getResources().getString(
							R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(
							R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							sendVoice(voiceRecorder.getVoiceFilePath(),
									voiceRecorder
											.getVoiceFileName(toChatUserId),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2,
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ChatActivity.this, st3,
								Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP) {
				message.setChatType(ChatType.GroupChat);
				message.setAttribute("avatar", MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserAvatar());
			} else if (chatType == CHATTYPE_CHATROOM) {
				message.setChatType(ChatType.ChatRoom);
			}
			message.setReceipt(toChatUserId);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath),
					len);
			message.setAttribute("nick", MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserName());
			message.setAttribute("avatar",MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserAvatar());
			message.addBody(body);
			/*
			 * if(isRobot){ message.setAttribute("em_robot_message", true); }
			 */
			conversation.addMessage(message);
			adapter.refreshSelectLast();
			// setResult(RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		String to = toChatUserId;
		// create and add image message in view
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP) {
			message.setChatType(ChatType.GroupChat);
			message.setAttribute("avatar", MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserAvatar());
		} else if (chatType == CHATTYPE_CHATROOM) {
			message.setChatType(ChatType.ChatRoom);
		}

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		if (null == MyApplication.getInstance().getUserInfo()) {
			Intent intent = new Intent();
			intent.putExtra("topage", ChatActivity.class.getName());
			intent.setClass(ChatActivity.this, LoginActivity.class);
			startActivity(intent);
		} else {
			message.setAttribute("avatar",MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserAvatar());
			message.setAttribute("nick", MyApplication.getInstance()
					.getUserInfo().getOBJECT().getUserName());
		}
		message.addBody(body);
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.refreshSelectLast();
		setResult(RESULT_OK);
	}

	// 红包传过来的值
	public void sendRedHot(String price, String message, String redId) {

		EMConversation conviercoin = EMChatManager.getInstance()
				.getConversation(toChatUserId);
		// 消息体
		EMMessage redMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
		TextMessageBody txtBody = new TextMessageBody(message + "," + price
				+ "," + redId);
		redMessage.addBody(txtBody);
		// 添加自定义消息扩展类型
		redMessage.setAttribute("attributel1", price);
		redMessage.setAttribute("attributel2", message);
		redMessage.setAttribute("nick", MyApplication.getInstance()
				.getUserInfo().getOBJECT().getUserName());
		redMessage.setAttribute("avatar",MyApplication.getInstance()
				.getUserInfo().getOBJECT().getUserAvatar());
		// 设置发送接收人
		redMessage.setReceipt(toChatUserId);
		conviercoin.addMessage(redMessage);
		try {
			EMContactManager.getInstance().addContact(toChatUserId, "");
		} catch (EaseMobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};

	// 定制
	public void sendCustom(String customId, String customDestination,
			String customBudget, String customMark) {
		EMConversation conviercoin = EMChatManager.getInstance()
				.getConversation(toChatUserId);
		// 消息体
		EMMessage redMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP) {
			redMessage.setChatType(ChatType.GroupChat);
		} else if (chatType == CHATTYPE_CHATROOM) {
			redMessage.setChatType(ChatType.ChatRoom);
		}
		TextMessageBody txtBody = new TextMessageBody(customDestination + ","
				+ customBudget + "," + customMark);
		redMessage.addBody(txtBody);
		// 添加自定义消息扩展类型
		redMessage.setAttribute("customId", customId);
		redMessage.setAttribute("avatar", MyApplication.getInstance()
				.getUserInfo().getOBJECT().getUserAvatar());
		redMessage.setAttribute("nick", MyApplication.getInstance()
				.getUserInfo().getOBJECT().getUserName());
		// 设置发送接收人
		redMessage.setReceipt(toChatUserId);
		conviercoin.addMessage(redMessage);
		// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
		adapter.refreshSelectLast();
		setResult(RESULT_OK);

	};

	/**
	 * 消息图标点击事件
	 * 
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View view) {
		Intent intent = new Intent();
		String st1 = "尚未连接至服务器，请稍后重试";
		switch (view.getId()) {
		case R.id.btn_send:
			String s = mEditTextContent.getText().toString();
			sendText(s);
			break;
		case R.id.chat_sendmessage_smail_btn:
			if (more.getVisibility() != View.VISIBLE) {
				more.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.INVISIBLE);
				btnContainer.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.VISIBLE);
				hideKeyboard();
			} else {
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				btnContainer.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
			}
			break;
		case R.id.chat_set_mode_voice_btn:
			hideKeyboard();
			edittext_layout.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
			buttonSetModeKeyboard.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
			btnMore.setVisibility(View.VISIBLE);
			buttonPressToSpeak.setVisibility(View.VISIBLE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			break;
		case R.id.chat_set_mode_keyboard_btn:
			edittext_layout.setVisibility(View.VISIBLE);
			more.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
			buttonSetModeVoice.setVisibility(View.VISIBLE);
			// mEditTextContent.setVisibility(View.VISIBLE);
			mEditTextContent.requestFocus();
			// buttonSend.setVisibility(View.VISIBLE);
			buttonPressToSpeak.setVisibility(View.GONE);
			if (TextUtils.isEmpty(mEditTextContent.getText())) {
				btnMore.setVisibility(View.VISIBLE);
				buttonSend.setVisibility(View.GONE);
			} else {
				btnMore.setVisibility(View.GONE);
				buttonSend.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.chat_sendmessage_paste_edittext:
			listView.setSelection(listView.getCount() - 1);
			if (more.getVisibility() == View.VISIBLE) {
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.chat_btn_more:
			if (more.getVisibility() == View.GONE) {
				hideKeyboard();
				more.setVisibility(View.VISIBLE);
				btnContainer.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
			} else {
				if (emojiIconContainer.getVisibility() == View.VISIBLE) {
					emojiIconContainer.setVisibility(View.GONE);
					btnContainer.setVisibility(View.VISIBLE);
					iv_emoticons_normal.setVisibility(View.VISIBLE);
				} else {
					more.setVisibility(View.GONE);
				}

			}
			break;
		case R.id.chat_red_package_image:
			intent.setClass(ChatActivity.this, UUTipActivity.class);
			intent.putExtra("uuchat_id", toChatUserId);
			intent.putExtra("avatar", toChatavatar);
			intent.putExtra("userName", toChatUsername);
			startActivity(intent);
			break;
		case R.id.chat_take_picture_image: // 拍照
			selectPicFromCamera();
			break;
		case R.id.chat_select_picture_image: // 图片
			intent.setClass(ChatActivity.this, SelectPictureActivity.class);
			intent.putExtra(SelectPictureActivity.INTENT_MAX_NUM,
					6 - ratiolist.size());
			startActivityForResult(intent, REQUEST_CODE_LOCAL);
			break;
		case R.id.chat_select_customiz_image: // 定制
			intent.setClass(ChatActivity.this, ChatCustomActivity.class);
			startActivity(intent);
			break;
		case R.id.chat_container_to_group: // 群组详情
			intent.setClass(ChatActivity.this, GroupDetailActivity.class);
			intent.putExtra("groupId", toChatUserId);
			intent.putExtra("groupName", toChatUsername);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		// String userId = intent.getStringExtra("userId");
		String toFrom = intent.getStringExtra("toFrom");
		if (null != toFrom && toFrom.equals("UUChatOKActivity")) {
			if (btnContainer.getVisibility() == View.VISIBLE) {
				btnContainer.setVisibility(View.GONE);
			}
			// 红包金额
			String price = intent.getStringExtra("price");
			// 留言
			String red_message = intent.getStringExtra("message");
			// 红包Id
			String red_ID = intent.getStringExtra("red_id");
			sendRedHot(price, red_message, red_ID);
		}
		if (null != toFrom && toFrom.equals("ChatCustomActivity")) {
			if (btnContainer.getVisibility() == View.VISIBLE) {
				btnContainer.setVisibility(View.GONE);
			}
			// 定制id
			String customId = intent.getStringExtra("customId");
			// 目的地
			String customDestination = intent
					.getStringExtra("customDestination");
			// 预算金额
			String customBudget = intent.getStringExtra("customBudget");
			// 标签
			String customMark = intent.getStringExtra("customMark");
			sendCustom(customId, customDestination, customBudget, customMark);
		}
		/*
		 * if (toChatUserId.equals(userId)) super.onNewIntent(intent);
		 */

	}

	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
		}
		super.onBackPressed();
	}

	@Override
	public void onEvent(EMNotifierEvent event) {

		switch (event.getEvent()) {
		case EventNewMessage: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();

			String username = null;
			// 群组消息
			if (message.getChatType() == ChatType.GroupChat
					|| message.getChatType() == ChatType.ChatRoom) {
				username = message.getTo();
			} else {
				// 单聊消息
				username = message.getFrom();
			}

			// 如果是当前会话的消息，刷新聊天页面
			if (username.equals(getToChatUserId())) {
				refreshUIWithNewMessage();
				// 声音和震动提示有新消息
				HXSDKHelper.getInstance().getNotifier()
						.viberateAndPlayTone(message);
			} else {
				// 如果消息不是和当前聊天ID的消息
				HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			}

			break;
		}
		case EventDeliveryAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventReadAck: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			refreshUI();
			break;
		}
		case EventOfflineMessage: {
			// a list of offline messages
			// List<EMMessage> offlineMessages = (List<EMMessage>)
			// event.getData();
			refreshUI();
			break;
		}
		default:
			break;
		}
	}

	public String getToChatUserId() {
		return toChatUserId;
	}

	private void refreshUIWithNewMessage() {
		if (adapter == null) {
			return;
		}

		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refreshSelectLast();
			}
		});
	}

	private void refreshUI() {
		if (adapter == null) {
			return;
		}

		runOnUiThread(new Runnable() {
			public void run() {
				adapter.refresh();
			}
		});
	}

	public ListView getListView() {
		return listView;
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = "SD卡不存在，不能拍照";
			Toast.makeText(this, st, Toast.LENGTH_LONG).show();
			return;
		}
		cameraFile = new File(CacheFileUtil.carmePaht, MyApplication
				.getInstance().getUserInfo().getOBJECT().getUserId()
				+ System.currentTimeMillis() + ".jpg");
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		String st8 = "找不到图片";
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}

	protected void onGroupViewCreation() {

		// 监听当前会话的群聊解散被T事件
		groupListener = new GroupListener();
		EMGroupManager.getInstance().addGroupChangeListener(groupListener);
	}

	/**
	 * 监测群组解散或者被T事件
	 * 
	 */
	class GroupListener extends GroupRemoveListener {

		@Override
		public void onUserRemoved(final String groupId, String groupName) {
			runOnUiThread(new Runnable() {
				String st13 = "你被群创建者从此群中移除";

				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(ChatActivity.this, st13, Toast.LENGTH_LONG).show();
						/*
						 * if (GroupDetailsActivity.instance != null)
						 * GroupDetailsActivity.instance.finish();
						 */
						finish();
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(final String groupId, String groupName) {
			// 群组解散正好在此页面，提示群组被解散，并finish此页面
			runOnUiThread(new Runnable() {
				String st14 = "当前群聊已被群创建者解散";

				public void run() {
					if (toChatUsername.equals(groupId)) {
						Toast.makeText(ChatActivity.this, st14, Toast.LENGTH_LONG).show();
						/*
						 * if (GroupDetailsActivity.instance != null)
						 * GroupDetailsActivity.instance.finish();
						 */
						finish();
					}
				}
			});
		}

	}
}
