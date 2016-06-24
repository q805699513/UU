package com.uugty.uu.com.find;

import java.util.ArrayList;
import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindViewFragment_play.FindListAdapter;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.DynamicEntity;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.entity.UserCommentEntity;
import com.uugty.uu.entity.DynamicEntity.Dynamic;
import com.uugty.uu.entity.RoadEntity.RoadDetail;
import com.uugty.uu.entity.UserCommentEntity.UserCommentList;
import com.uugty.uu.friendstask.DynamicCommentActivity;
import com.uugty.uu.friendstask.DynamicDetailActivity;
import com.uugty.uu.friendstask.FriendsDynamicActivity;
import com.uugty.uu.friendstask.RightReportPopupWindow;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.uuchat.ChatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class InvitationCollectFragment extends Fragment implements
		OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
		OnClickListener, OnItemClickListener {
	private TextView service_collect_no_data_text_two;
	private Button service_collect_no_data_btn;
	private RelativeLayout invitaFrame;
	private View view;
	private ListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private DynamicAdapter adapter; // 论坛 list
	private List<Dynamic> list = new ArrayList<Dynamic>();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			DynamicEntity entity = (DynamicEntity) msg.getData()
					.getSerializable("DynamicEntity");
			if (entity != null) {
				List<Dynamic> result = (List<Dynamic>) entity.getLIST();
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					loadData(2, false);
					adapter.notifyDataSetChanged();
					mListView.setSelection(0);
					break;
				case 2:
					list.addAll(result);
					adapter.notifyDataSetChanged();
					break;
				}

			} else {
				list.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.service_collect_frag_layout, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(lp);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initGui();
		initAction();

	}

	private void initGui() {
		invitaFrame = (RelativeLayout) view.findViewById(R.id.service_collect_no_data_rel);
		service_collect_no_data_text_two=(TextView) view.findViewById(R.id.service_collect_no_data_text_two);
		service_collect_no_data_btn=(Button) view.findViewById(R.id.service_collect_no_data_btn);
		service_collect_no_data_text_two.setText("你还没有收藏过任何帖子，快去逛逛吧");
		mListView = (ListView) view.findViewById(R.id.carry_list);
		mSwipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.order_swipe_container);
		adapter = new DynamicAdapter(getActivity(), list,mSwipeLayout,invitaFrame);
		mListView.setAdapter(adapter);
	}

	private void initAction() {
		service_collect_no_data_btn.setOnClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		mListView.setOnScrollListener(this);
		mSwipeLayout.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(true);
			}
		});
		loadData(1, false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.service_collect_no_data_btn:
			Intent intent = new Intent();
			intent.setClass(getActivity(), FriendsDynamicActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
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
				loadData(2, false);
			}
		}
	}

	private void loadData(final int what, final boolean msg) {
		// 显示等待层
		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize

		APPRestClient.post(getActivity(), ServiceCode.COLLECT_FRIENDSAID_LIST,
				params, new APPResponseHandler<DynamicEntity>(
						DynamicEntity.class, getActivity()) {
					@Override
					public void onSuccess(DynamicEntity result) {
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("DynamicEntity", result);
							msg.setData(b);
							handler.sendMessage(msg);
							mSwipeLayout.setVisibility(View.VISIBLE);
							invitaFrame.setVisibility(view.GONE);
						} else {
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							if (startId == 1) {
								mSwipeLayout.setVisibility(View.GONE);
								invitaFrame.setVisibility(view.VISIBLE);
							}
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						mSwipeLayout.setRefreshing(false);
						if (errorCode == -999) {
							new AlertDialog.Builder(getActivity())
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
					}}

					@Override
					public void onFinish() {
					}
				});
	}

}

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
			view = LayoutInflater.from(context).inflate(
					R.layout.dynamic_img_gridview_item, null);
			holder.img = (ImageView) view
					.findViewById(R.id.dynamic_img_gridview_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ImageLoader.getInstance().displayImage(APPRestClient.SERVER_IP +
				ls[position].substring(0,ls[position].indexOf("."))+"_ya"+ls[position].substring(ls[position].indexOf(".")),
				holder.img,options);
		// holder.img.setImageURI(Uri
		// .parse(APPRestClient.SERVER_IP + ls[position]));
		return view;
	}

	static class ViewHolder {
		ImageView img;
	}
}

class DynamicAdapter extends BaseAdapter {
	private Context context;
	private List<Dynamic> ls = new ArrayList<Dynamic>();
	private SwipeRefreshLayout mSwipeLayout;
	private RelativeLayout invitaFrame;
	private String[] arry;
	DynamicImgAdapter imgadapter;

	

	public DynamicAdapter(Context context, List<Dynamic> ls,
			SwipeRefreshLayout mSwipeLayout, RelativeLayout invitaFrame) {
		super();
		this.context = context;
		this.ls = ls;
		this.mSwipeLayout = mSwipeLayout;
		this.invitaFrame = invitaFrame;
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
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.friends_collect_dynamic_list_item, null);
			holder.dynamic_iscollect_delete_rel = (RelativeLayout) view
					.findViewById(R.id.dynamic_iscollect_delete_rel);
			holder.dynamic_iscollect_lin = (LinearLayout) view
					.findViewById(R.id.dynamic_iscollect_lin);
			holder.right_friendcollect_isdelete_img = (ImageView) view
					.findViewById(R.id.right_friendcollect_isdelete_img);
			holder.username = (TextView) view
					.findViewById(R.id.dynamic_username_text);
			holder.dynamic_browse_text = (TextView) view
					.findViewById(R.id.dynamic_browse_text);
			holder.dynamic_content_text = (TextView) view
					.findViewById(R.id.dynamic_content_text);
			holder.dynamic_createtimes = (TextView) view
					.findViewById(R.id.dynamic_createtimes);
			holder.dynamic_goodtimes_text = (TextView) view
					.findViewById(R.id.dynamic_goodtimes_text);
			holder.dynamic_comments_text = (TextView) view
					.findViewById(R.id.dynamic_comments_text);
			holder.dynamic_praise_text = (TextView) view
					.findViewById(R.id.dynamic_zambia_text);
			holder.userheand = (SimpleDraweeView) view
					.findViewById(R.id.dynamic_avatar_img);
			holder.dynamic_img_gridview_oneimg = (SimpleDraweeView) view
					.findViewById(R.id.dynamic_img_gridview_oneimg);
			holder.dynamic_browse_rel = (LinearLayout) view
					.findViewById(R.id.dynamic_browse_rel);
			holder.dynamic_praise_rel = (LinearLayout) view
					.findViewById(R.id.dynamic_zambia_rel);
			holder.dynamic_friend_comments_lin = (LinearLayout) view
					.findViewById(R.id.dynamic_friend_comments_lin);
			holder.dynamic_photo_show = (LinearLayout) view
					.findViewById(R.id.dynamic_photo_show);
			holder.dynamic_img_grid = (JoyGridView) view
					.findViewById(R.id.dynamic_img_grid);
			holder.dynamic_praise_state_img = (ImageView) view
					.findViewById(R.id.dynamic_praise_state_img);
			holder.dynamic_position_item_text = (TextView) view
					.findViewById(R.id.dynamic_position_item_text);
			holder.right_friendcollect_delete_img = (ImageView) view
					.findViewById(R.id.right_friendcollect_delete_img);
			holder.dynamic_postion_item_lin = (LinearLayout) view
					.findViewById(R.id.dynamic_postion_item_lin);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if(ls.get(position).getSaidIsFreeze().equals("1")){
			holder.dynamic_iscollect_delete_rel.setVisibility(View.VISIBLE);
			holder.dynamic_iscollect_lin.setVisibility(View.GONE);	
			// 删除状态 删除
			holder.right_friendcollect_isdelete_img
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CustomDialog.Builder builder1 = new CustomDialog.Builder(
									context);
							builder1.setMessage("确定删除吗?");
							builder1.setTitle("提示");
							builder1.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											deletefriendsaid(ls.get(position)
													.getCollectId());
											ls.remove(position);
											notifyDataSetChanged();											
											dialog.dismiss();
										}
									});

							builder1.setNegativeButton(
									"取消",
									new android.content.DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});

							builder1.create().show();
						}
					});
		}else{
			holder.dynamic_iscollect_delete_rel.setVisibility(View.GONE);
			holder.dynamic_iscollect_lin.setVisibility(View.VISIBLE);
		if (!ls.get(position).getSaidCity().equals("")) {
			holder.dynamic_postion_item_lin.setVisibility(View.VISIBLE);
			holder.dynamic_position_item_text.setText(ls.get(position)
					.getSaidCity());
		} else {
			holder.dynamic_position_item_text.setText("");
			holder.dynamic_postion_item_lin.setVisibility(View.GONE);
		}
		holder.username.setText(ls.get(position).getUserName()); // 用户名
		holder.dynamic_browse_text.setText(ls.get(position)
				.getSaidBrowseTimes()); // 浏览量
		holder.dynamic_content_text.setText(ls.get(position).getSaidContent()); // 内容
		if (ls.get(position).getSaidCreateDate().equals("")) {

		} else {
			holder.dynamic_createtimes.setText(ls
					.get(position)
					.getSaidCreateDate()
					.substring(0,
							ls.get(position).getSaidCreateDate().indexOf("."))); // 发布时间
		}
		holder.dynamic_goodtimes_text.setText(ls.get(position)
				.getSaidGoodTimes()); // 点赞次数
		holder.dynamic_comments_text.setText(ls.get(position)
				.getSaidCommentTimes()); // 评论次数
		// 点赞状态
		if (ls.get(position).getSaidUpvoteStatus().equals("1")) {
			Drawable drawable= context.getResources()
					.getDrawable(R.drawable.dynamic_havepraise_img);  
				/// 这一步必须要做,否则不会显示.  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), 
					drawable.getMinimumHeight());  
				holder.dynamic_praise_text.setCompoundDrawables(drawable,null,null,null); 
			holder.dynamic_praise_text
					.setTextColor(Color.parseColor("#00a1d9"));
//			holder.dynamic_praise_text.setText("已赞");
			holder.dynamic_goodtimes_text.setTextColor(Color.parseColor("#00a1d9"));
					} else {
						Drawable drawable= context.getResources()
								.getDrawable(R.drawable.dynamic_praise_img);  
						/// 这一步必须要做,否则不会显示.  
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), 
								drawable.getMinimumHeight());  
						holder.dynamic_praise_text.setCompoundDrawables(drawable,null,null,null); 						
						holder.dynamic_praise_text
						.setTextColor(Color.parseColor("#98999a"));
						holder.dynamic_praise_text.setText("");
						holder.dynamic_goodtimes_text.setTextColor(Color.parseColor("#98999a"));											
					}

		if (null != ls.get(position).getUserAvatar() // 用户头像
				&& !ls.get(position).getUserAvatar().equals("")) {
			holder.userheand.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ ls.get(position).getUserAvatar()));
		} else {
			holder.userheand.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}		
		// 删除
		holder.right_friendcollect_delete_img
		.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomDialog.Builder builder1 = new CustomDialog.Builder(
						context);
				builder1.setMessage("确定删除吗?");
				builder1.setTitle("提示");
				builder1.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						deletefriendsaid(ls.get(position)
								.getCollectId());
						ls.remove(position);
						notifyDataSetChanged();
						if(ls.size()<1){
							mSwipeLayout.setVisibility(View.GONE);
							invitaFrame.setVisibility(View.VISIBLE);
							}
						dialog.dismiss();
					}
				});
				
				builder1.setNegativeButton(
						"取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				
				builder1.create().show();
			}
		});

		// 跳转个人展示页
		holder.userheand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userid = ls.get(position).getUserId();
				String username = ls.get(position).getUserName();
				String useravatar = ls.get(position).getUserAvatar();
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userid", userid);
				intent.putExtra("username", username);
				intent.putExtra("useravatar", useravatar);
				intent.putExtra("usertype", "0");
				intent.setClass(context, DynamicDetailActivity.class);
				context.startActivity(intent);
			}
		});

		/*
		 * 图片展示
		 */
		if (ls.get(position).getSaidPhoto().equals("")) {
			holder.dynamic_photo_show.setVisibility(View.GONE);
		} else {
			holder.dynamic_photo_show.setVisibility(View.VISIBLE);
		}
		if (!ls.get(position).getSaidPhoto().equals("")) {
			arry = ls.get(position).getSaidPhoto().split(",");
			if (arry.length > 1) {
				holder.dynamic_img_grid.setVisibility(View.VISIBLE);
				holder.dynamic_img_gridview_oneimg.setVisibility(View.GONE);
				imgadapter = new DynamicImgAdapter(context, arry);
				holder.dynamic_img_grid.setAdapter(imgadapter);
			} else {
				holder.dynamic_img_grid.setVisibility(View.GONE);
				holder.dynamic_img_gridview_oneimg.setVisibility(View.VISIBLE);
				holder.dynamic_img_gridview_oneimg.setAspectRatio(1);
				holder.dynamic_img_gridview_oneimg.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP + arry[0]));
			}
		}
		holder.dynamic_img_gridview_oneimg
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						arry = ls.get(position).getSaidPhoto().split(",");
						List<String> urls = new ArrayList<String>();
						for (int i = 0; i < arry.length; i++) {
							urls.add(arry[i]);
						}
						imageBrower(0, (ArrayList<String>) urls);
					}
				});
		// 点击大图
		holder.dynamic_img_grid
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int positions, long id) {
						// TODO Auto-generated method stub
						arry = ls.get(position).getSaidPhoto().split(",");
						List<String> urls = new ArrayList<String>();
						for (int i = 0; i < arry.length; i++) {
							urls.add(arry[i]);
						}
						imageBrower(positions, (ArrayList<String>) urls);
					}
				});
		// 评论跳转
		holder.dynamic_friend_comments_lin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Bundle b = new Bundle();
				b.putSerializable("dynamic", ls.get(position));
				intent.putExtras(b);
				intent.setClass(context, DynamicCommentActivity.class);
				context.startActivity(intent);
			}
		});

		//点赞
		holder.dynamic_praise_rel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MyApplication.getInstance().isLogin()) {
				String state="";
				int num=Integer.parseInt(ls.get(position).getSaidGoodTimes());
				if(ls.get(position).getSaidUpvoteStatus().equals("1")){
					CustomToast.makeText(context, 0, "您已取消了赞", 300).show();
					state="0";
					ls.get(position).setSaidUpvoteStatus("0");
						Drawable drawable = context.getResources()
								.getDrawable(R.drawable.dynamic_praise_img);
						// / 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0,
								drawable.getMinimumWidth(),
								drawable.getMinimumHeight());
						holder.dynamic_praise_text.setCompoundDrawables(
								drawable, null, null, null); 
						holder.dynamic_praise_text.setTextColor(Color.parseColor("#98999a"));
					holder.dynamic_praise_text.setText("");
					holder.dynamic_goodtimes_text.setTextColor(Color.parseColor("#98999a"));
					if(num>0){
					holder.dynamic_goodtimes_text.setText(""+(num-1));
					ls.get(position).setSaidGoodTimes(""+(num-1));
					}else{
					holder.dynamic_goodtimes_text.setText(""+num);	
					}
				}else{
					state="1";
					ls.get(position).setSaidUpvoteStatus("1");
					Drawable drawable= context.getResources()
							.getDrawable(R.drawable.dynamic_havepraise_img);  
					/// 这一步必须要做,否则不会显示.  
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), 
							drawable.getMinimumHeight());  
					holder.dynamic_praise_text.setCompoundDrawables(drawable,null,null,null); 
					holder.dynamic_praise_text.setTextColor(Color.parseColor("#00a1d9"));
//					holder.dynamic_praise_text.setText("已赞");
					holder.dynamic_goodtimes_text.setTextColor(Color.parseColor("#00a1d9"));
					holder.dynamic_goodtimes_text.setText(""+(num+1));
					ls.get(position).setSaidGoodTimes(""+(num+1));									
				}
				UpdatePraise(ls.get(position).getSaidId(), state);	
				}else{
					// 先登录
					Intent intent=new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.putExtra("topage", DynamicDetailActivity.class.getName());
					intent.setClass(context, LoginActivity.class);
					context.startActivity(intent);
				}
			}
		});		
	}
		return view;
	}

	// 大图展示
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "0");
		context.startActivity(intent);
	}

	// 点赞
	private void UpdatePraise(final String id, final String state) {
		RequestParams params = new RequestParams();
		params.add("saidId", id); // 用户ID
		params.add("saidUpvoteStatus", state); // 点赞状态

		APPRestClient.post(context, ServiceCode.UP_VOTE, params,
				new APPResponseHandler<UpVoteEntity>(UpVoteEntity.class,
						context) {
					@Override
					public void onSuccess(UpVoteEntity result) {

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							UpdatePraise(id, state);
						} else {
						CustomToast.makeText(context, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													onFinish();
													dialog.dismiss();
												}
											}).show();
						}
					}}

					@Override
					public void onFinish() {
					}
				});
	}

	// 删除
	private void deletefriendsaid(final String id) {
		RequestParams params = new RequestParams();
		params.add("collectId", id); // 用户ID
		APPRestClient.post(context, ServiceCode.COLLECT_CANCEL_FRIENDSAID,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						context) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(context, 0, "删除成功", 200).show();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							deletefriendsaid(id);
						} else {
						CustomToast.makeText(context, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
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
					}}

					@Override
					public void onFinish() {
					}
				});
	}

	static class ViewHolder {
		TextView username, dynamic_browse_text, dynamic_content_text,
				dynamic_createtimes, dynamic_comments_text,
				dynamic_goodtimes_text, dynamic_praise_text,
				dynamic_position_item_text;
		SimpleDraweeView userheand, dynamic_img_gridview_oneimg;
		LinearLayout dynamic_photo_show, dynamic_browse_rel,dynamic_postion_item_lin,
				dynamic_friend_comments_lin, dynamic_praise_rel,dynamic_iscollect_lin;
		JoyGridView dynamic_img_grid;
		ImageView dynamic_praise_state_img, right_friendcollect_delete_img,right_friendcollect_isdelete_img;
		RelativeLayout dynamic_iscollect_delete_rel;
	}
}
