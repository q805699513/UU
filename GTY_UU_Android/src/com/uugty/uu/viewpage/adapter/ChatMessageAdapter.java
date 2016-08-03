package com.uugty.uu.viewpage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.chat.help.SmileUtils;
import com.uugty.uu.chat.help.VoicePlayClickListener;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ImageCache;
import com.uugty.uu.common.util.ImageUtils;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.entity.RedPrice;
import com.uugty.uu.person.PersonCenterActivity;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.uuchat.ChatCustomGuideActivity;
import com.uugty.uu.uuchat.ChatShowImageActivity;
import com.uugty.uu.uuchat.ContextMenu;
import com.uugty.uu.uuchat.LoadImageTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatMessageAdapter extends BaseAdapter {
	private static final String TAG = "ChatMessageAdapter";
	// 聊天对象
	private String toChatUserId;
	private String toChatAvatar;
	private String toChatUserName;
	private Context context;
	private LayoutInflater inflater;
	private Activity activity;
	private EMConversation conversation;
	EMMessage[] messages = null;
	private AlertDialog dialog = null;
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_VOICE = 2;
	private static final int MESSAGE_TYPE_RECV_VOICE = 3;
	private static final int MESSAGE_TYPE_SENT_RED = 4;
	private static final int MESSAGE_TYPE_RECV_RED = 5;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 6;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 7;
	private static final int MESSAGE_TYPE_SENT_CUSTOMER = 8;
	private static final int MESSAGE_TYPE_RECV_CUSTOMER = 9;

	public static final String IMAGE_DIR = "chat/image/";
	private Map<String, Timer> timers = new Hashtable<String, Timer>();

	private String redStauts;
	private int width,height,imageWidth;
	private RelativeLayout.LayoutParams localLayoutParams;
	private int screenWidth;
	private List<EMMessage> pictureEmMessage = new ArrayList();

	public ChatMessageAdapter(Context context, String toChatUserId,
			String toChatavatar, String toChatUsername, int chatType) {
		this.toChatUserId = toChatUserId;
		this.context = context;
		this.toChatAvatar = toChatavatar;
		this.toChatUserName = toChatUsername;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.conversation = EMChatManager.getInstance().getConversation(
				toChatUserId);
		if (this.conversation != null)
		      for (int i = 0; i < this.conversation.getMsgCount(); i++)
		      {
		        EMMessage localEMMessage = this.conversation.getMessage(i);
		        if ((localEMMessage != null) && (this.conversation.getMessage(i).getType() == EMMessage.Type.IMAGE))
		          this.pictureEmMessage.add(localEMMessage);
		      }
	}

	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(
					new EMMessage[conversation.getAllMessages().size()]);
			for (int i = 0; i < messages.length; i++) {
				// getMessage will set message as read status
				conversation.getMessage(i);
			}

			notifyDataSetChanged();
		}

		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case HANDLER_MESSAGE_REFRESH_LIST:
				refreshList();
				break;
			case HANDLER_MESSAGE_SELECT_LAST:
				if (activity instanceof ChatActivity) {
					ListView listView = ((ChatActivity) activity).getListView();
					if (messages.length > 0) {
						listView.setSelection(messages.length - 1);
					}
				}
				break;
			case HANDLER_MESSAGE_SEEK_TO:
				int position = message.arg1;
				if (activity instanceof ChatActivity) {
					ListView listView = ((ChatActivity) activity).getListView();
					listView.setSelection(position);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if ((this.pictureEmMessage != null) && (this.pictureEmMessage.size() > 0))
	        this.pictureEmMessage.clear();
	      for (int k = 0; k < this.conversation.getMsgCount(); k++)
	      {
	        EMMessage localEMMessage2 = this.conversation.getMessage(k);
	        if ((localEMMessage2 != null) && (localEMMessage2.getType() == EMMessage.Type.IMAGE))
	          this.pictureEmMessage.add(localEMMessage2);
	      }
	   
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler
				.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}

	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}

	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		/*
		 * obtainmessage（）是从消息池中拿来一个msg 不需要另开辟空间new
		 * new需要重新申请，效率低，obtianmessage可以循环利用； 是message 从handler
		 * 类获取，从而可以直接向该handler 对象发送消息
		 */
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

	private View createViewByMessage(EMMessage message, int position) {
		View view = null;
		switch (message.getType()) {
		case VOICE:
			view = message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.chatvoice_row_left, null) : inflater
					.inflate(R.layout.chatvoice_row_right, null);
			break;
		case TXT:
			// 自定义红包类型
			if (message.getStringAttribute(Constant.UUCHAT_RED_PRICE, null) != null
					&& message.getStringAttribute(Constant.UUCHAT_RED_MESSAGE,
							null) != null) {
				view = message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.uuchat_redprice_left, null)
						: inflater
								.inflate(R.layout.uuchat_redprice_right, null);
			} else if(message.getStringAttribute(Constant.MESSAGE_ATTR_CUSTOMER, null)!=null) {
				view = message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.uuchat_cutomer_left, null)
						: inflater
						.inflate(R.layout.uuchat_cutomer_right, null);

			} else if(message.getStringAttribute(Constant.UUCHAT_ROADTITLE, null)!=null
					&& message.getStringAttribute(Constant.UUCHAT_ROADPRICE, null)!=null
					&& message.getStringAttribute(Constant.UUCHAT_ROADIMG, null)!=null){
				view = message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.uuchat_shareroad_left, null)
						: inflater
						.inflate(R.layout.uuchat_shareroad_right, null);


			}else{
				view = message.direct == EMMessage.Direct.RECEIVE ? inflater
						.inflate(R.layout.chattxt_row_left, null) : inflater
						.inflate(R.layout.chattxt_row_right, null);
			}
			break;
		case IMAGE:
			view = message.direct == EMMessage.Direct.RECEIVE ? inflater
					.inflate(R.layout.uuchat_picture_left, null) : inflater
					.inflate(R.layout.uuchat_picture_right, null);
			break;
		}
		return view;
	}

	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
		return 10;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = getItem(position);
		if (message == null) {
			return -1;
		}
		if (message.getType() == EMMessage.Type.TXT) {
			// 自定义红包消息
			if (message.getStringAttribute(Constant.UUCHAT_RED_PRICE, null) != null
					&& message.getStringAttribute(Constant.UUCHAT_RED_MESSAGE,
							null) != null) {
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_RED
						: MESSAGE_TYPE_SENT_RED;
			}
			if (message.getStringAttribute(Constant.MESSAGE_ATTR_CUSTOMER, null)!=null) {
				return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_CUSTOMER
						: MESSAGE_TYPE_SENT_CUSTOMER;
			}
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
					: MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE
					: MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE
					: MESSAGE_TYPE_SENT_IMAGE;
		}

		return -1;// invalid
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages == null ? 0 : messages.length;
	}

	@Override
	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (message.getType() == EMMessage.Type.TXT) {
				holder.pb = (ProgressBar) convertView
						.findViewById(R.id.pb_sending);
				holder.staus_iv = (ImageView) convertView
						.findViewById(R.id.msg_status);
				holder.iv_avatar = (CirculHeadImage) convertView
						.findViewById(R.id.iv_imgImg_left);
				// 这里是文字内容
				holder.tv = (TextView) convertView
						.findViewById(R.id.tv_chatcontent);
				// 红包
				if (message.getStringAttribute(Constant.UUCHAT_RED_PRICE, null) != null
						&& message.getStringAttribute(
								Constant.UUCHAT_RED_MESSAGE, null) != null) {
					holder.relayout = (FrameLayout) convertView
							.findViewById(R.id.red_price_layout);
					holder.txt_content = (TextView) convertView
							.findViewById(R.id.txt_red_message);
				}
				//定制
				if (message.getStringAttribute(Constant.MESSAGE_ATTR_CUSTOMER, null)!=null) {
					holder.customRel = (RelativeLayout) convertView
							.findViewById(R.id.uucht_custom_rel);
					holder.txt_content = (TextView) convertView
							.findViewById(R.id.uuchat_custom_destination);
					holder.txt_price = (TextView) convertView
							.findViewById(R.id.uuchat_custom_price);
					holder.txt_tag = (TextView) convertView
							.findViewById(R.id.uuchat_custom_tag);
				}
			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_voice));
					holder.iv_avatar = (CirculHeadImage) convertView
							.findViewById(R.id.iv_imgImg_left);
					holder.tv = (TextView) convertView
							.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView
							.findViewById(R.id.iv_sendPicture));
					holder.iv_avatar = (CirculHeadImage) convertView
							.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView
							.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView
							.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView
							.findViewById(R.id.msg_status);
				} catch (Exception e) {
				}

			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (!(chatType == ChatType.GroupChat || chatType == ChatType.ChatRoom)
				&& message.direct == EMMessage.Direct.SEND) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			holder.tv_delivered = (TextView) convertView
					.findViewById(R.id.tv_delivered);
			if (holder.tv_ack != null) {
				if (message.isAcked) {
					if (holder.tv_delivered != null) {
						holder.tv_delivered.setVisibility(View.INVISIBLE);
					}
					holder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					holder.tv_ack.setVisibility(View.INVISIBLE);

					// check and display msg delivered ack status
					if (holder.tv_delivered != null) {
						if (message.isDelivered) {
							holder.tv_delivered.setVisibility(View.VISIBLE);
						} else {
							holder.tv_delivered.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae,chatroom message，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION)
					&& !message.isAcked
					&& chatType != ChatType.GroupChat
					&& chatType != ChatType.ChatRoom) {
				// 不是语音通话记录
				if (!message.getBooleanAttribute(
						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						// 发送已读回执
						message.isAcked = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 设置用户头像
		setUserAvatar(message, holder.iv_avatar);

		// 根据消息类型加载item
		switch (message.getType()) {
		case TXT:
			// 红包事件
			if (message.getStringAttribute(Constant.UUCHAT_RED_PRICE, null) != null
					&& message.getStringAttribute(Constant.UUCHAT_RED_MESSAGE,
							null) != null) {
				handleRedMessage(message, holder, position);
			} else if(message.getStringAttribute(Constant.MESSAGE_ATTR_CUSTOMER, null)!=null){
				handleCustomMessage(message, holder, position);
			}
			else {
				handleTextMessage(message, holder, position);
			}

			break;

		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case IMAGE: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		default:
			break;
		}

		if (message.direct == EMMessage.Direct.SEND) {/*
													 * View statusView =
													 * convertView
													 * .findViewById(R
													 * .id.msg_status); //
													 * 重发按钮点击事件
													 * statusView.setOnClickListener
													 * (new OnClickListener() {
													 * 
													 * @Override public void
													 * onClick(View v) {
													 * 
													 * // 显示重发消息的自定义alertdialog
													 * Intent intent = new
													 * Intent(activity,
													 * AlertDialog.class);
													 * intent.putExtra("msg",
													 * activity
													 * .getString(R.string
													 * .confirm_resend));
													 * intent.putExtra("title",
													 * activity
													 * .getString(R.string
													 * .resend));
													 * intent.putExtra("cancel",
													 * true);
													 * intent.putExtra("position"
													 * , position); if
													 * (message.getType() ==
													 * EMMessage.Type.TXT)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_TEXT); else
													 * if (message.getType() ==
													 * EMMessage.Type.VOICE)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_VOICE); else
													 * if (message.getType() ==
													 * EMMessage.Type.IMAGE)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_PICTURE);
													 * else if
													 * (message.getType() ==
													 * EMMessage.Type.LOCATION)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_LOCATION);
													 * else if
													 * (message.getType() ==
													 * EMMessage.Type.FILE)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_FILE); else
													 * if (message.getType() ==
													 * EMMessage.Type.VIDEO)
													 * activity
													 * .startActivityForResult
													 * (intent, ChatActivity.
													 * REQUEST_CODE_VIDEO);
													 * 
													 * } });
													 */
		}

		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message
					.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			EMMessage prevMessage = getItem(position - 1);
			if (prevMessage != null
					&& DateUtils.isCloseEnough(message.getMsgTime(),
							prevMessage.getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message
						.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;

	}

	/**
	 * 显示用户头像
	 * 
	 * @param message
	 * @param imageView
	 */
	private void setUserAvatar(final EMMessage message,
			CirculHeadImage imageView) {
		if (message.direct == Direct.SEND) {
			// 显示自己头像
			if (null!=MyApplication.getInstance().getUserInfo()&&MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserAvatar().contains("storage")) {
				imageView.setHeadPic(MyApplication.getInstance().getUserInfo()
						.getOBJECT().getUserAvatar(), "local");
			} else {
				if(null!=MyApplication.getInstance().getUserInfo())
				imageView.setHeadPic(MyApplication.getInstance().getUserInfo().getOBJECT()
								.getUserAvatar(), "net");
			}

		} else if (message.getUserName().equals("admin")) {
			imageView.setHeadPic("drawable://" + R.drawable.chat_system_avata_image,
					"drawable");
		}else if (message.getUserName().equals("requirementpublisher")) {
			imageView.setHeadPic("drawable://" + R.drawable.chat_custom_avata_image,
					"drawable");
		} else {
			if(message.getChatType().equals(ChatType.GroupChat)){
				 String groupAvatar="";
				try {
					groupAvatar = message.getStringAttribute("avatar");
					imageView.setHeadPic(groupAvatar, "net");
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			}else{
				imageView.setHeadPic(toChatAvatar, "net");
			}
		}
		if(!message.getUserName().equals("admin")&&!message.getUserName().equals("requirementpublisher"))
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, PersonCenterActivity.class);
				intent.putExtra("detailUserId", message.getFrom());
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, ViewHolder holder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils
				.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		holder.tv.setText(span, BufferType.SPANNABLE);
		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent();
				intent.setClass(activity, ContextMenu.class);
				intent.putExtra("position", position);
				intent.putExtra("type", EMMessage.Type.TXT.ordinal());
				activity.startActivityForResult(intent,
						ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	
	private void handleCustomMessage(final EMMessage message, ViewHolder holder,
			final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		String customMark =txtBody.getMessage().substring(
				txtBody.getMessage().lastIndexOf(",") + 1);
		String messagePrice = txtBody.getMessage().substring(0,
				txtBody.getMessage().lastIndexOf("," + customMark));
		String customBudget = messagePrice.substring(messagePrice
				.lastIndexOf(",") + 1);
		String customDestination = messagePrice.substring(0,
				messagePrice.lastIndexOf("," + customBudget));
		
		
		holder.txt_content.setText(customDestination);
		holder.txt_price.setText("￥"+customBudget);
		holder.txt_tag.setText(customMark);
			holder.customRel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						String customId= message.getStringAttribute("customId");
						Intent intent = new Intent();
						intent.putExtra("customUserId", toChatUserId);
						intent.putExtra("customId", customId);
						intent.setClass(activity, ChatCustomGuideActivity.class);
						activity.startActivity(intent);
					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			});
			
			holder.customRel.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Intent intent = new Intent();
					intent.setClass(activity, ContextMenu.class);
					intent.putExtra("position", position);
					intent.putExtra("type", 1001);
					activity.startActivityForResult(intent,
							ChatActivity.REQUEST_CODE_CONTEXT_MENU);
					return true;
				}
			});
			
			if (message.direct == EMMessage.Direct.SEND) {
				switch (message.status) {
				case SUCCESS: // 发送成功
					holder.pb.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.GONE);
					break;
				case FAIL: // 发送失败
					holder.pb.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.VISIBLE);
					break;
				case INPROGRESS: // 发送中
					holder.pb.setVisibility(View.VISIBLE);
					holder.staus_iv.setVisibility(View.GONE);
					break;
				default:
					// 发送消息
					sendMsgInBackground(message, holder);
				}
			}
		}
	
	/**
	 * 红包事件
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleRedMessage(EMMessage message, ViewHolder holder,
			final int position) {
		final TextMessageBody txtBody = (TextMessageBody) message.getBody();
		final String redId = txtBody.getMessage().substring(
				txtBody.getMessage().lastIndexOf(",") + 1);
		String messagePrice = txtBody.getMessage().substring(0,
				txtBody.getMessage().lastIndexOf("," + redId));
		final String price = messagePrice.substring(messagePrice
				.lastIndexOf(",") + 1);
		final String leaveMessage = messagePrice.substring(0,
				messagePrice.lastIndexOf("," + price));

		// final String str[] = txtBody.getMessage().split(",");
		holder.txt_content.setText(leaveMessage);
		if (message.direct == EMMessage.Direct.RECEIVE) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.relayout.setOnClickListener(new NoDoubleClickListener() {
					@Override
					public void onNoDoubleClick(View v) {
						// TODO Auto-generated method stub
						RequestParams parms = new RequestParams();
						parms.add("gratuityId", redId);
						APPRestClient.post(context,
								APPRestClient.HTTPS_BASE_URL
										+ ServiceCode.USER_RED_PRICE_STATUS,
								parms, true, new APPResponseHandler<RedPrice>(
										RedPrice.class, context) {
									@Override
									// 1:发送中 2：已领取 3：已过期 4:为领取
									public void onSuccess(RedPrice result) {
										redStauts = result.getOBJECT()
												.getGratuityStatus();

										if (!TextUtils.isEmpty(redStauts)
												&& redStauts.equals("4")) {
											LayoutInflater inflater = LayoutInflater
													.from(context);
											View tipview = inflater.inflate(
													R.layout.red_dialog, null);
											ImageView img = (ImageView) tipview
													.findViewById(R.id.red_close);
											Button button = (Button) tipview
													.findViewById(R.id.red_send_ok);
											CirculHeadImage image_heard = (CirculHeadImage) tipview
													.findViewById(R.id.red_send_image);
											TextView send_name = (TextView) tipview
													.findViewById(R.id.red_send_naem);
											TextView text_message = (TextView) tipview
													.findViewById(R.id.red_send_liuyan);
											text_message.setText(leaveMessage);
											if (TextUtils.isEmpty(toChatAvatar)) {
												image_heard
														.setHeadPic(
																"drawable://"
																		+ R.drawable.no_default_head_img,
																"drawable");
											} else {
												image_heard.setHeadPic(toChatAvatar,
														"net");
											}
											if (TextUtils
													.isEmpty(toChatUserName)) {
												send_name.setText("小u给我发来的红包");
											} else {
												send_name
														.setText(toChatUserName
																+ "给我发来的红包");
											}

											final AlertDialog.Builder builder = new Builder(
													context).setView(tipview);
											builder.create();
											dialog = builder.show();
											img.setOnClickListener(new OnClickListener() {
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													if (builder != null) {
														dialog.dismiss();
													}
												}
											});
											// redMessage.put(id, txtbody1);
											button.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													if (builder != null) {
														dialog.dismiss();
													}
													LingQu(redId, price,
															leaveMessage);
												}
											});

										} else if (!TextUtils
												.isEmpty(redStauts)
												&& redStauts.equals("2")) {
											Receive(leaveMessage, price);
										} else if (!TextUtils
												.isEmpty(redStauts)
												&& redStauts.equals("1")) {
											CustomToast.makeText(context, 0,
													"该红包正在发送", 200).show();
										}
									}

									@Override
									public void onFailure(int errorCode,
											String errorMsg) {
										// TODO Auto-generated method stub
										CustomToast.makeText(context, 0,
												errorMsg, 300).show();
									}
								});
					}
				});
				break;
			case FAIL: // 发送失败
				break;
			case INPROGRESS: // 发送中
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
		// 发送方
		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							EMContactManager.getInstance().addContact(
									toChatUserId, null);// 需异步处理
						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();

				holder.relayout.setOnClickListener(new NoDoubleClickListener() {
					@Override
					public void onNoDoubleClick(View v) {
						RequestParams parms = new RequestParams();
						parms.add("gratuityId", redId);
						APPRestClient.post(context,
								APPRestClient.HTTPS_BASE_URL
										+ ServiceCode.USER_RED_PRICE_STATUS,
								parms, true, new APPResponseHandler<RedPrice>(
										RedPrice.class, context) {
									@Override
									public void onSuccess(RedPrice result) {
										redStauts = result.getOBJECT()
												.getGratuityStatus();

										LayoutInflater inflater = LayoutInflater
												.from(context);
										View tipview = inflater.inflate(
												R.layout.red_lingqu_back, null);
										ImageView img = (ImageView) tipview
												.findViewById(R.id.red_ling_close);
										TextView send_name2 = (TextView) tipview
												.findViewById(R.id.red_ling_name);
										CirculHeadImage image_heard2 = (CirculHeadImage) tipview
												.findViewById(R.id.red_ling_image);
										TextView txt_name_two = (TextView) tipview
												.findViewById(R.id.red_ling_message);
										TextView txt_price = (TextView) tipview
												.findViewById(R.id.red_ling_monery);
										TextView txt_bottom = (TextView) tipview
												.findViewById(R.id.liqu_bottom_text);

										txt_name_two.setText(leaveMessage);
										txt_price.setText(price + "元");
										txt_bottom.setVisibility(View.GONE);

										final AlertDialog.Builder builder2 = new Builder(
												context).setView(tipview);
										builder2.create();
										final AlertDialog dialog2 = builder2
												.show();
										image_heard2
												.setHeadPic(MyApplication
																		.getInstance()
																		.getUserInfo()
																		.getOBJECT()
																		.getUserAvatar(),
														"net");
										String chatUserName = "";
										if (TextUtils.isEmpty(toChatUserName)) {
											chatUserName = "小u";
										} else {
											chatUserName = toChatUserName;
										}
										if (redStauts.equals("1")
												|| redStauts.equals("4")) {
											send_name2.setText("我给"
													+ chatUserName + "发了一个红包");
										} else if (redStauts.equals("2")) {
											send_name2.setText(chatUserName
													+ "已经接受我的红包");
										} else if (redStauts.equals("3")) {
											send_name2.setText("红包已退回");
										}

										img.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												if (builder2 != null) {
													if (dialog != null)
														dialog.dismiss();
													dialog2.dismiss();
												}
											}
										});

									}

									@Override
									public void onFailure(int errorCode,
											String errorMsg) {
										// TODO Auto-generated method stub
										CustomToast.makeText(context, 0,
												errorMsg, 300).show();
									}
								});
					}
				});
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	// 领取红包
	public void LingQu(final String redId, final String price,
			final String leaveMessage) {
		RequestParams parms = new RequestParams();
		parms.add("gratuityId", redId);
		APPRestClient.post(context, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.USER_RED_PRICE, parms, true,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, context) {
					@Override
					public void onSuccess(BaseEntity result) {
						Receive(leaveMessage, price);
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									EMChatManager.getInstance()
											.acceptInvitation(toChatUserId);
								} catch (EaseMobException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						
						// TODO Auto-generated method stub
						CustomToast.makeText(context, 0, errorMsg, 300).show();
						if (errorCode == 3) {
							LingQu(redId, price, leaveMessage);
						}
					}
				});
	}

	// 领取小费的dialog
	private void Receive(String leaveMessage, String price) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View tipview = inflater.inflate(R.layout.red_lingqu_back, null);
		ImageView img = (ImageView) tipview.findViewById(R.id.red_ling_close);
		TextView send_name2 = (TextView) tipview
				.findViewById(R.id.red_ling_name);
		CirculHeadImage image_heard2 = (CirculHeadImage) tipview
				.findViewById(R.id.red_ling_image);
		TextView txt_name_two = (TextView) tipview
				.findViewById(R.id.red_ling_message);
		TextView txt_price = (TextView) tipview
				.findViewById(R.id.red_ling_monery);

		txt_name_two.setText(leaveMessage);
		txt_price.setText(price + "元");
		final AlertDialog.Builder builder2 = new Builder(context)
				.setView(tipview);
		builder2.create();
		final AlertDialog dialog2 = builder2.show();

		if (TextUtils.isEmpty(toChatAvatar)) {
			image_heard2.setHeadPic("drawable://"
					+ R.drawable.no_default_head_img, "drawable");
		} else {
			image_heard2.setHeadPic(toChatAvatar,
					"net");
		}
		if (TextUtils.isEmpty(toChatUserName)) {
			send_name2.setText("小u给我发来的红包");
		} else {
			send_name2.setText(toChatUserName + "给我发来的红包");
		}

		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (builder2 != null) {
					if (dialog != null)
						dialog.dismiss();
					dialog2.dismiss();
				}
			}
		});

	}

	/**
	 * 语音消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		int len = voiceBody.getLength();
		if (len > 0) {
			holder.tv.setText(voiceBody.getLength() + "\"");
			holder.tv.setVisibility(View.VISIBLE);
		} else {
			holder.tv.setVisibility(View.INVISIBLE);
		}

		holder.iv.setOnClickListener(new VoicePlayClickListener(message,
				holder.iv, this, activity, toChatUserId));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				/*
				 * activity.startActivityForResult((new Intent(activity,
				 * ContextMenu.class)).putExtra("position", position)
				 * .putExtra("type", EMMessage.Type.VOICE.ordinal()),
				 * ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				 */

				return true;
			}
		});

		if (((ChatActivity) activity).playMsgId != null
				&& ((ChatActivity) activity).playMsgId.equals(message
						.getMsgId()) && VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(+R.anim.voice_from_icon);
			} else {
				holder.iv.setImageResource(+R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {
			/*
			 * if (message.isListened()) { // 隐藏语音未听标志
			 * holder.iv_read_status.setVisibility(View.INVISIBLE); } else {
			 * holder.iv_read_status.setVisibility(View.VISIBLE); }
			 */
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				((FileMessageBody) message.getBody())
						.setDownloadCallback(new EMCallBack() {

							@Override
							public void onSuccess() {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										holder.pb.setVisibility(View.INVISIBLE);
										notifyDataSetChanged();
									}
								});

							}

							@Override
							public void onProgress(int progress, String status) {
							}

							@Override
							public void onError(int code, String message) {
								activity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										holder.pb.setVisibility(View.INVISIBLE);
									}
								});

							}
						});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 图片消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message,
			final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		/*
		 * holder.iv.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * activity.startActivityForResult( (new Intent(activity,
		 * ContextMenu.class)).putExtra("position", position).putExtra("type",
		 * EMMessage.Type.IMAGE.ordinal()),
		 * ChatActivity.REQUEST_CODE_CONTEXT_MENU); return true; } });
		 */

		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";进行中
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.chat_default_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();//远程图片uri
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();//缩略图的url
					if (TextUtils.isEmpty(thumbRemoteUrl)
							&& !TextUtils.isEmpty(remotePath)) {
						thumbRemoteUrl = remotePath;
					}
					String thumbnailPath = ImageUtils
							.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath,
							imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath),
					holder.iv, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								Toast.makeText(
										activity,
										activity.getString(R.string.send_fail)
												+ activity
														.getString(R.string.connect_failuer_toast),
										Toast.LENGTH_LONG).show();
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	private void sendPictureMessage(final EMMessage message,
			final ViewHolder holder) {
		try {
			String to = message.getTo();

			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");

			final long start = System.currentTimeMillis();
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {

					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(
									activity,
									activity.getString(R.string.send_fail)
											+ activity
													.getString(R.string.connect_failuer_toast),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showDownloadImageProgress(final EMMessage message,
			final ViewHolder holder) {
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if (holder.pb != null)
			holder.pb.setVisibility(View.VISIBLE);
		if (holder.tv != null)
			holder.tv.setVisibility(View.VISIBLE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	@SuppressLint("NewApi")
	private boolean showImageView(final String thumbernailPath,
			final ImageView iv, final String localFullSizePath,
			String remoteDir, final EMMessage message) {
		// String imagename =
		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
		// localFullSizePath.length());
		// final String remote = remoteDir != null ? remoteDir+imagename :
		// imagename;
		final String remote = remoteDir;
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			width = bitmap.getWidth();
			height = bitmap.getHeight();
		      localLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		      if(screenWidth==0){
		    	  screenWidth =  getScreenWidth();
		      }
		      if (screenWidth!= 0)
		      {
		    	  imageWidth = 1 * screenWidth / 3;
		       // if (width <= height)
		        localLayoutParams.width = (int)imageWidth;
		        localLayoutParams.height = (int)(imageWidth * height / width);
		        iv.setLayoutParams(localLayoutParams);
		      }
			iv.setImageBitmap(bitmap);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setClickable(true);

			iv.setOnClickListener(new NoDoubleClickListener() {
				@Override
				public void onNoDoubleClick(View v) {
					
					Intent localIntent = new Intent(activity, ChatShowImageActivity.class);
		              localIntent.putExtra("listImage", (Serializable)pictureEmMessage);
		              if (message != null)
		              {
		                ImageMessageBody localImageMessageBody = (ImageMessageBody)message.getBody();
		                if (localImageMessageBody != null)
		                  localIntent.putExtra("image", localImageMessageBody.getLocalUrl());
		              }
					
					
					/*Intent intent = new Intent(activity,
							ChatShowBigImageActivity.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImageMessageBody body = (ImageMessageBody) message
								.getBody();
						intent.putExtra("secret", body.getSecret());
						intent.putExtra("remotepath", remote);
					}*/
					if (message != null
							&& message.direct == EMMessage.Direct.RECEIVE
							&& !message.isAcked
							&& message.getChatType() != ChatType.GroupChat
							&& message.getChatType() != ChatType.ChatRoom) {
						try {
							EMChatManager.getInstance().ackMessageRead(
									message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(localIntent);
				}
			});
			return true;
		} else {
			
			if(!pictureEmMessage.contains(message)){
				pictureEmMessage.add(message);
			}
			new LoadImageTask().execute(thumbernailPath, localFullSizePath,
					remote, message.getChatType(), iv, activity, message,pictureEmMessage);
			return true;
		}

	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message,
			final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);

		final long start = System.currentTimeMillis();
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {

				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {

				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message,
			final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// holder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// holder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// }
					// holder.staus_iv.setVisibility(View.VISIBLE);

					if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
						Toast.makeText(
								activity,
								activity.getString(R.string.send_fail)
										+ activity
												.getString(R.string.error_send_invalid_content),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								activity,
								activity.getString(R.string.send_fail)
										+ activity
												.getString(R.string.connect_failuer_toast),
								Toast.LENGTH_LONG).show();
					}
				}

				notifyDataSetChanged();
			}
		});
	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		CirculHeadImage iv_avatar;
		ImageView iv_leftavatar;
		TextView txt_content,txt_price,txt_tag;
		FrameLayout relayout;
		TextView tv_ack;
		TextView tv_delivered;
		RelativeLayout customRel;
	}
	
	private int getScreenWidth() {  
        DisplayMetrics dm = new DisplayMetrics();  
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);  
        return dm.widthPixels;  
    } 
}
