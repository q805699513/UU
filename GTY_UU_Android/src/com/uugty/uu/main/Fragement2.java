package com.uugty.uu.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.chat.help.SmileUtils;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.Utility;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.entity.GroupChatSimpleEntity;
import com.uugty.uu.entity.UserSimpleEntity;
import com.uugty.uu.simplistview.SwipeMenu;
import com.uugty.uu.simplistview.SwipeMenuCreator;
import com.uugty.uu.simplistview.SwipeMenuListView;
import com.uugty.uu.simplistview.SwipeMenuListView.OnMenuItemClickListener;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.uuchat.ChatFriendsActivity;
import com.uugty.uu.uuchat.GroupChatListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Fragement2 extends Fragment {

	private SwipeMenuListView chat_listview;
	private View rootview;
	private TextView mFriendsChatTv;
	private ImageView mFriendImage;
	// 会话信息
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	ArrayList<String> strArray = new ArrayList<String>();
	private UUchatListAdapter adapter;
	private Context context;
	private String user_name;
	private boolean hidden;
	// 有无系统消息
	boolean systemFlag = false;
	// 有无系统定制消息
	boolean customerFlag = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.context = getActivity();
		if (rootview == null) {

			rootview = inflater.inflate(R.layout.fragment_2, null);

		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootview.getParent();

		if (parent != null) {
			parent.removeView(rootview);
		}
		return rootview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		mFriendsChatTv = (TextView)rootview.findViewById(R.id.find_friendsTv);
//		mFriendImage = (ImageView)rootview.findViewById(R.id.friend_image);
		conversationList.addAll(loadConversationsWithRecentChat());
		chat_listview = (SwipeMenuListView) rootview
				.findViewById(R.id.uuchat_listview);
		chat_listview.setSource(false);//设置左滑删除Boolean值
		TextView footView = new TextView(context);
		footView.setBackgroundColor(getResources().getColor(
				R.color.chat_bg_color));
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, dp2px(50));
		footView.setLayoutParams(params);
		chat_listview.addFooterView(footView, null, false);
		adapter = new UUchatListAdapter(MyApplication.getInstance(), 1,
				conversationList);
		chat_listview.setAdapter(adapter);
		if (null != conversationList && conversationList.size() > 0) {
			chat_listview.setVisibility(View.VISIBLE);
			Utility.setSwipeMenuListViewHeightBasedOnChildren(chat_listview);
			// mScrollView.smoothScrollTo(0, 0);
		} else {
			chat_listview.setVisibility(View.GONE);
		}
		final String st2 = "不能与自己聊天";
		// adapter.notifyDataSetChanged();
		
		//点击进入群聊
//		mFriendsChatTv.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(context, GroupChatListActivity.class);
//				startActivity(intent);
//			}
//		});
		
		//点击进入好友
//		mFriendImage.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(context, ChatFriendsActivity.class);
//				startActivity(intent);
//			}
//		});
		// 新消息的提示
		chat_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				// 得到聊天对象
				if (position == 1) {
					Intent intent = new Intent();
					intent.setClass(context, ChatFriendsActivity.class);
					startActivity(intent);
				} else if (position == 2) {
					Intent intent = new Intent();
					intent.setClass(context, GroupChatListActivity.class);
					startActivity(intent);
//				} else if(position == 0){ 
					
				}else {
					EMConversation conversation = adapter.getItem(position);
					String userId = conversation.getUserName();
					if (userId.equals(MyApplication.getInstance().getUserInfo()
							.getOBJECT().getUserId())){
						CustomToast.makeText(context, st2, Toast.LENGTH_SHORT)
								.show();

					}else {
						Intent intent = new Intent(context, ChatActivity.class);
						if (conversation.isGroup()) {
							if (conversation.getType() == EMConversationType.ChatRoom) {
								// it is group chat
								intent.putExtra("chatType",
										ChatActivity.CHATTYPE_CHATROOM);
								intent.putExtra("userId", userId);
							} else {
								// it is group chat
								intent.putExtra("chatType",
										ChatActivity.CHATTYPE_GROUP);
								intent.putExtra("userId", userId);
								intent.putExtra("userName", userId);
							}
						}
						// 目前只有单聊，暂时不考虑群聊。进入聊天页面

						// it is single chat
						intent.putExtra("userId", userId);
						if (userId.equals("admin") && !systemFlag) {
							intent.putExtra("noMessage", "noMessage");
						}
						if (userId.equals("requirementpublisher")
								&& !customerFlag) {
							intent.putExtra("noMessage", "noMessage");
						}
						startActivity(intent);
					}
				}

			}
		});
		// 添加向右滑动删除
		SwipeMenuCreator menucreatot = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// TODO Auto-generated method stub
				com.uugty.uu.simplistview.SwipeMenuItem deleteltem = new com.uugty.uu.simplistview.SwipeMenuItem(
						getActivity());
				// 设置item的背景
				deleteltem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// 设置item的宽度
				deleteltem.setWidth(dp2px(75));

				// 设置item的图片
				deleteltem.setIcon(R.drawable.ic_delete);
				// add to menu

				menu.addMenuItem(deleteltem);
			}
		};
		chat_listview.setMenuCreator(menucreatot); // 设置左滑删除事件
		// 右滑删除事件
		chat_listview.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				// TODO Auto-generated method stub

				switch (index) {
				case 0:
					EMConversation tobeDeleteCons = adapter.getItem(position);
					// 删除此会话
					EMChatManager.getInstance().deleteConversation(
							tobeDeleteCons.getUserName(),
							tobeDeleteCons.isGroup(), true);
					adapter.remove(tobeDeleteCons);
					adapter.notifyDataSetChanged();
					// 更新消息未读数
					((MainActivity) getActivity()).updateUnreadLabel();
					break;

				default:
					break;
				}
			}

		});

	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if (adapter != null) {
			chat_listview.setVisibility(View.VISIBLE);
			Utility.setSwipeMenuListViewHeightBasedOnChildren(chat_listview);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 刷新无消息界面
	 */
	/*
	 * public void refreshNoMessage() { conversationList.clear();
	 * onActivityCreated(null);
	 * 
	 * }
	 */

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden && !MyApplication.getInstance().isConflict) {
			refresh();
		}
	}

	/**
	 * 获取所有会话
	 * 
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();

		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0
						&& !TextUtils.isEmpty(conversation.getUserName())) {
					if (conversation.getUserName().equals("admin"))
						systemFlag = true;
					if (conversation.getUserName().equals(
							"requirementpublisher"))
						customerFlag = true;
					if (!TextUtils.isEmpty(conversation.getUserName())
							&& !conversation.getUserName().equals(
									"client_customer")) {
						sortList.add(new Pair<Long, EMConversation>(
								conversation.getLastMessage().getMsgTime(),
								conversation));
					}
				}
			}
			if (systemFlag) {
				EMConversation friendsConversation = new EMConversation(
						"friends");
				sortList.add(new Pair<Long, EMConversation>(null,
						friendsConversation));
				EMConversation groupConversation = new EMConversation(
						"groupchat");
				sortList.add(new Pair<Long, EMConversation>(null,
						groupConversation));
				if (!customerFlag) {
					EMConversation customerConversation = new EMConversation(
							"requirementpublisher");
					sortList.add(new Pair<Long, EMConversation>(null,
							customerConversation));
				}

			} else {
				EMConversation friendsConversation = new EMConversation(
						"friends");
				EMConversation adminConversation = new EMConversation("admin");
				sortList.add(new Pair<Long, EMConversation>(null,
						adminConversation));
				sortList.add(new Pair<Long, EMConversation>(null,
						friendsConversation));
				EMConversation groupConversation = new EMConversation(
						"groupchat");
				sortList.add(new Pair<Long, EMConversation>(null,
						groupConversation));
				if (!customerFlag) {
					EMConversation customerConversation = new EMConversation(
							"requirementpublisher");
					sortList.add(new Pair<Long, EMConversation>(null,
							customerConversation));
				}

			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
			/*
			 * if (sortItem.second.getUserName().equals("admin")) {
			 * systemEMConversation = sortItem.second; } else {
			 * 
			 * }
			 */

		}
		return list;
	}

	// 获取最后一条消息
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {
						// 将系统通知置顶
						// if(con1.second.getType().equals(other))
						//if (con1.second.getType() == EMConversationType.GroupChat) {
						
						   if(con1.second.getUserName().length()>con2.second.getUserName().length()){
							   if(con2.second.getType()==EMConversationType.GroupChat){
								   return -1;
							   }else{
								   return 1; 
							   }
							  
						   }else if(con1.second.getUserName().length() == con2.second
									.getUserName().length()){
							   if (con1.first == con2.first) {
									return 0;
								} else if (con2.first > con1.first) {
									return 1;
								} else {
									return -1;
								}
						   }else if(con1.second.getUserName().length()<con2.second.getUserName().length()){
							   if(con1.second.getType()==EMConversationType.GroupChat){
								   return 1;
							   }else{
								   return -1;
							   }
						   }else{
							   //没用代码
							   return -1;
						   }
						
						
						
							/*if (con1.second.getUserName().length() == con2.second
									.getUserName().length()) {
								if (con1.first == con2.first) {
									return 0;
								} else if (con2.first > con1.first) {
									return 1;
								} else {
									return -1;
								}
							} else if (con1.second.getUserName().length() > con2.second
									.getUserName().length()) {
								return 1;
							} else {
								return -1;
							}
						*/

						/*
						 * if (con1.first == con2.first) { return 0; } else if
						 * (con2.first > con1.first) { return 1; } else { return
						 * -1; }
						 */

					}
				});
	}

	public class UUchatListAdapter extends ArrayAdapter<EMConversation> {
		private LayoutInflater inflater;
		private List<EMConversation> copyConversationList;

		public UUchatListAdapter(Context context, int textViewResourceId,
				List<EMConversation> objects) {
			super(context, textViewResourceId, objects);
			copyConversationList = new ArrayList<EMConversation>();
			copyConversationList.addAll(objects);
			inflater = LayoutInflater.from(context);

		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				// 系统通知
				return 0;
			} else if (position == 1) {
				// 好友
				return 1;
			} else if (position == 2) {
				// 群聊
				return 2;
			} else if (position == 3) {
				// 定制
				return 3;
			}
			return 4;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 5;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewManger holder;
			final TextView textView;
			final CirculHeadImage userimg;
			int type = getItemViewType(position);
			if (convertView == null) {
				holder = new ViewManger();
				switch (type) {
				case 0:
					convertView = inflater.inflate(
							R.layout.fragmnet_viewmanger_one, null);
					holder.content_messgae = (TextView) convertView
							.findViewById(R.id.uuchat_usermessage_content);
					break;
				case 1:
					convertView = inflater.inflate(
							R.layout.fragmnet_viewmanger_friends, null);
					break;
				case 2:
					convertView = inflater.inflate(
							R.layout.fragmnet_viewmanger_group, null);
					break;
				case 3:
					convertView = inflater.inflate(
							R.layout.fragmnet_viewmanger_custom, null);
					holder.content_messgae = (TextView) convertView
							.findViewById(R.id.uuchat_usermessage_content);
					break;
				case 4:
					convertView = inflater.inflate(
							R.layout.fragmnet_viewmanger, null);
					holder.user_image = (CirculHeadImage) convertView
							.findViewById(R.id.uuchat_user_image);
					holder.txt_name = (TextView) convertView
							.findViewById(R.id.uuchat_user_name);
					holder.content_timer = (TextView) convertView
							.findViewById(R.id.uuchat_user_data);
					holder.end_content = (TextView) convertView
							.findViewById(R.id.uuchat_user_content);
					holder.content_messgae = (TextView) convertView
							.findViewById(R.id.uuchat_usermessage_content);
					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewManger) convertView.getTag();
			}
			convertView.setEnabled(true);
			convertView.setBackgroundResource(R.drawable.list_item_bg);
			// 获取与此用户对话
			EMConversation conversation = getItem(position);
			if (position == 0) {
				// 获取每一个userid的未读消息数量
				if (conversation.getUnreadMsgCount() > 0) {
					holder.content_messgae.setText(String.valueOf(conversation
							.getUnreadMsgCount()));
					holder.content_messgae.setVisibility(View.VISIBLE);
				} else {
					holder.content_messgae.setVisibility(View.INVISIBLE);
				}
			} else if (position == 1) {

			} else if (position == 2) {

			} else if (position == 3) {
				if (conversation.getUnreadMsgCount() > 0) {
					holder.content_messgae.setText(String.valueOf(conversation
							.getUnreadMsgCount()));
					holder.content_messgae.setVisibility(View.VISIBLE);
				} else {
					holder.content_messgae.setVisibility(View.INVISIBLE);
				}
			} else {
				textView = holder.txt_name;
				userimg = holder.user_image;
				// 获取每一个userid的未读消息数量
				if (conversation.getUnreadMsgCount() > 0) {
					holder.content_messgae.setText(String.valueOf(conversation
							.getUnreadMsgCount()));
					holder.content_messgae.setVisibility(View.VISIBLE);
				} else {
					holder.content_messgae.setVisibility(View.INVISIBLE);
				}
				if (conversation.getMsgCount() != 0) {
					// 把最后一条消息的内容作为item的message内容
					EMMessage lastMessage = conversation.getLastMessage();
					// 显示最后一条内容
					holder.end_content.setText(
							SmileUtils.getSmiledText(
									getContext(),
									getMessageDigest(lastMessage,
											(this.getContext()))),
							BufferType.SPANNABLE);
					// 设置发送时间
					holder.content_timer.setText(DateUtils
							.getTimestampString(new Date(lastMessage
									.getMsgTime())));
					// 设置聊天对象名
					// 获取用户username
					String username = conversation.getUserName();
					if (lastMessage.getChatType().equals(ChatType.GroupChat)) {
						getSimpleGroupRequest(username, holder);
					} else {
						RequestParams reqestparm = new RequestParams();
						reqestparm.add("userId", username);
						APPRestClient.post(context,
								ServiceCode.USER_SIMPLE_MESSAGE, reqestparm,
								new APPResponseHandler<UserSimpleEntity>(
										UserSimpleEntity.class, getActivity()) {
									@Override
									public void onSuccess(
											UserSimpleEntity result) {
										// TODO Auto-generated method stub
										// 判断好友是否有名字
										if (null != result.getOBJECT()) {
											if (result.getOBJECT()
													.getUserName().equals(null)
													|| result.getOBJECT()
															.getUserName()
															.equals("")) {
												user_name = "小u";
												textView.setText(user_name);
											} else {
												user_name = result.getOBJECT()
														.getUserName();
												textView.setText(user_name);
											}

											userimg.setHeadPic(result
													.getOBJECT()
													.getUserAvatar(), "net");

										} else {
											textView.setText("小u");
											userimg.setHeadPic(
													"drawable://"
															+ R.drawable.no_default_head_img,
													"drawable");
										}
									}

									@Override
									public void onFailure(int errorCode,
											String errorMsg) {
										CustomToast.makeText(getActivity(), 0,
												errorMsg, 300).show();
										// TODO Auto-generated method stub
									}
								});
					}

				}
			}
			return convertView;

		}

	}

	private void getSimpleGroupRequest(final String groupId,
			final ViewManger holder) {
		RequestParams params = new RequestParams();
		params.add("groupEasemobID", groupId);// 路线Id
		APPRestClient.post(getActivity(), ServiceCode.GROUP_DETAIL_SIMPLE,
				params, new APPResponseHandler<GroupChatSimpleEntity>(
						GroupChatSimpleEntity.class, getActivity()) {
					@Override
					public void onSuccess(GroupChatSimpleEntity result) {
						if (null != result.getOBJECT()) {
							holder.user_image.setHeadPic(result.getOBJECT()
									.getGroupImages(), "net");
							holder.txt_name.setText(result.getOBJECT()
									.getGroupName() + "群");
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getSimpleGroupRequest(groupId, holder);
						} else {
							CustomToast.makeText(getActivity(), 0, errorMsg,
									300).show();
						}
					}
				});

	}

	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case TXT: // 文本消息
			if (message.getBooleanAttribute(
					Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call)
						+ txtBody.getMessage();
			} else if (message.getStringAttribute(Constant.UUCHAT_RED_PRICE,
					null) != null
					&& message.getStringAttribute(Constant.UUCHAT_RED_MESSAGE,
							null) != null) {
				digest = "发来一个红包";
			} else if (message.getStringAttribute(
					Constant.MESSAGE_ATTR_CUSTOMER, null) != null) {
				digest = "发来一个旅行定制";

			}else if(message.getStringAttribute(
					Constant.UUCHAT_ROADLINEID, null) != null){
				digest = "分享了一条路线";
			} else {
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}
			break;
		case VOICE:// 语音消息
			digest = "发来一段语音";
			break;
		case IMAGE:// 语音消息
			digest = "发来一张图片";
			break;
		default:

			return digest;
		}

		return digest;
	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	static class ViewManger {
		CirculHeadImage user_image;// 用户头像
		TextView txt_name;// 用户名称
		TextView end_content;// 最后一条消息展示
		TextView content_timer;// 当前消息发送时间
		TextView content_messgae;// 未读消息条数
		RelativeLayout rel;// 整个item布局
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
