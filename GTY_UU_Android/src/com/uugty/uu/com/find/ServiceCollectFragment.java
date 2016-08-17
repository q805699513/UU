package com.uugty.uu.com.find;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.GuideEntity;
import com.uugty.uu.entity.GuideEntity.GuideDetail;
import com.uugty.uu.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ServiceCollectFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnClickListener,
OnScrollListener{
	private static final int INITIAL_DELAY_MILLIS = 500;
	private ListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private List<GuideDetail> guideDetailList = new ArrayList<GuideDetail>();
	private ServiceAdapter adapter;
	private View view;
	private Button wish_guang;
	private RelativeLayout wishFrame;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
					"GuideEntity");
			if (entity != null&&entity.getLIST().size()>0) {
				List<GuideDetail> result = (List<GuideDetail>) entity.getLIST();
				switch (msg.what) {
				case 1:
					guideDetailList.clear();
					guideDetailList.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					mListView.setVisibility(View.VISIBLE);
					loadData(2, false);
					break;
				case 2:
					guideDetailList.addAll(result);
					break;
				}
				adapter.notifyDataSetChanged();
			} else {
				mSwipeLayout.setRefreshing(false);
				guideDetailList.clear();
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
		 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);  
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
		mListView = (ListView) view.findViewById(R.id.carry_list);
		wishFrame = (RelativeLayout) view.findViewById(R.id.service_collect_no_data_rel);
		wish_guang = (Button) view.findViewById(R.id.service_collect_no_data_btn);
		mSwipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.order_swipe_container);
		adapter = new ServiceAdapter(getActivity(), guideDetailList,mSwipeLayout,wishFrame);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
				INITIAL_DELAY_MILLIS);
		mListView.setAdapter(swingBottomInAnimationAdapter);
	}

	private void initAction() {
		mSwipeLayout.setOnRefreshListener(this);
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
		loadData(1, true);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("roadId", guideDetailList.get(position)
						.getRoadlineId());
				intent.setClass(getActivity(), FindTestViewPagerActivity.class);
				startActivity(intent);

			}
		});
		wish_guang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("toPage", "order");
				intent.setClass(getActivity(), MainActivity.class);
				startActivity(intent);
			}
		});
	}
	private void loadData(final int what, final boolean msg) {
		RequestParams params = new RequestParams();		
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		APPRestClient.post(getActivity(), ServiceCode.COLLECT_ROAD_LINE_LIST, params,
				new APPResponseHandler<GuideEntity>(GuideEntity.class, getActivity()) {
					@Override
					public void onSuccess(GuideEntity result) {
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("GuideEntity", result);
							msg.setData(b);
							handler.sendMessage(msg);
							mSwipeLayout.setVisibility(View.VISIBLE);
							wishFrame.setVisibility(view.GONE);
						}else{
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							if (startId == 1) {
								wishFrame.setVisibility(view.VISIBLE);
								mSwipeLayout.setVisibility(View.GONE);	
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(getActivity())
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
	}
}
class ServiceAdapter extends BaseAdapter {
	private List<GuideDetail> ls;
	private Context context;
	private SwipeRefreshLayout mSwipeLayout;
	private RelativeLayout wishFrame;

	

	public ServiceAdapter(Context context, List<GuideDetail> ls,SwipeRefreshLayout mSwipeLayout,RelativeLayout wishFrame) {
		super();
		this.ls = ls;
		this.context = context;
		this.mSwipeLayout=mSwipeLayout;
		this.wishFrame = wishFrame;
	}

	@Override
	public int getCount() {
		return ls.size();
	}

	@Override
	public Object getItem(int position) {
		return ls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.guide_detail_listview_item, null);
			holder.imageView = (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_item_image);
			holder.headImage = (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_item_headImage);
			holder.priceText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_price);
			holder.titleText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_title);
			holder.orderNumText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_num_text);
			holder.lookNumText = (TextView) convertView
					.findViewById(R.id.guide_detail_item_look_num_text);

			holder.onlineImageView =  (SimpleDraweeView) convertView
					.findViewById(R.id.guide_detail_online_route_image);
			//认证信息
			holder.consult_person_truename = (TextView) convertView
					.findViewById(R.id.consult_person_truename);
			holder.consult_person_education = (TextView) convertView
					.findViewById(R.id.consult_person_education);
			holder.consult_person_drive = (TextView) convertView
					.findViewById(R.id.consult_person_drive);
			holder.consult_person_guide = (TextView) convertView
					.findViewById(R.id.consult_person_guide);
			holder.is_veru = (TextView) convertView.findViewById(R.id.consult_person_veru);

			//浏览出行量
			holder.new_linear = (LinearLayout) convertView.findViewById(R.id.home_new_linear);
			holder.look_linear = (LinearLayout) convertView.findViewById(R.id.home_look_linear);
			holder.travel_linear = (LinearLayout) convertView.findViewById(R.id.home_travel_linear);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		//收藏
//		holder.collect_image.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent();
//				// 判断登录
//				if (MyApplication.getInstance().isLogin()) {
//					if (!ls.get(position).getCollectId().equals("0")) {
//						sendCollectCancleRequest(ls.get(position).getCollectId(),position);
//						ls.get(position).setCollectId("0");
//					}
//				} else {
//					intent.putExtra("topage",
//							GuideDetailActivity.class.getName());
//					intent.setClass(context, LoginActivity.class);
//					context.startActivity(intent);
//				}
//			}
//		});
		//是否会员
		if (ls.get(position).getUserIsPromoter().equals("1")) {
			holder.is_veru.setVisibility(View.VISIBLE);
		} else {
			holder.is_veru.setVisibility(View.GONE);
		}
		// 实名
		if (ls.get(position).getUserIdValidate().equals("2")) {
			holder.consult_person_truename.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_truename.setVisibility(View.GONE);
		}
		// 导游
		if (ls.get(position).getUserTourValidate().equals("2")) {
			holder.consult_person_guide.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_guide.setVisibility(View.GONE);
		}
		// 驾驶
		if (ls.get(position).getUserCarValidate().equals("2")) {
			holder.consult_person_drive.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_drive.setVisibility(View.GONE);
		}
		// 学历
		if (ls.get(position).getUserCertificateValidate().equals("2")) {
			holder.consult_person_education.setVisibility(View.VISIBLE);
		} else {
			holder.consult_person_education.setVisibility(View.GONE);
		}
		if (!ls.get(position).getRoadlineBackground().equals("")) {
			if (ls.get(position).getRoadlineBackground().contains("images")) {
				holder.imageView.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP
								+ ls.get(position).getRoadlineBackground()));
			} else {
				holder.imageView.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP + "images/roadlineDescribe/"
								+ (ls.get(position).getRoadlineBackground())));
				
			}
		} else {
			// 加载默认图片
			holder.imageView.setImageURI(Uri.parse("res:///"
					+ R.drawable.uu_default_image_one));
		}
		holder.priceText.setText(ls.get(position).getRoadlinePrice());
		if (!TextUtils.isEmpty(ls.get(position).getIsNew())
				&& ls.get(position).getIsNew().equals("1")) {
			holder.new_linear.setVisibility(View.VISIBLE);
		} else {
			holder.new_linear.setVisibility(View.GONE);
		}if(!TextUtils.isEmpty(ls.get(position).getIsOnline())&& ls.get(position).getIsOnline().equals("1")){
			holder.onlineImageView.setVisibility(View.VISIBLE);
		}else {
			holder.onlineImageView.setVisibility(View.GONE);
		}
		holder.titleText.setText(ls.get(position).getRoadlineTitle());
		if("0".equals(ls.get(position).getOrderCount())){
			holder.travel_linear.setVisibility(View.GONE);
		}else {
			holder.travel_linear.setVisibility(View.VISIBLE);
			holder.orderNumText.setText(ls.get(position).getOrderCount() + "人参加过");
		}
		if (!TextUtils.isEmpty(ls.get(position).getLineNum())) {
			holder.lookNumText.setText(ls.get(position).getLineNum() + "人浏览过");
		} else {
			holder.lookNumText.setText("0");
		}
		if (null != ls.get(position).getUserAvatar()
				&& !ls.get(position).getUserAvatar().equals("")) {
			holder.headImage.setImageURI(Uri
					.parse(APPRestClient.SERVER_IP + ls.get(position).getUserAvatar()));
		} else {
			holder.headImage.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}
		return convertView;
	}
	// 取消收藏
			private void sendCollectCancleRequest(final String id,final int postion) {
				RequestParams params = new RequestParams();
				params.add("collectId", id);// 路线Id
				APPRestClient.post(context, ServiceCode.COLLECT_CANCEL_ROAD_LINE, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class, context) {
							@Override
							public void onSuccess(BaseEntity result) {
								ls.remove(postion);
								notifyDataSetChanged();
								if(ls.size()<1){
								mSwipeLayout.setVisibility(View.GONE);
								wishFrame.setVisibility(View.VISIBLE);
								}
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									sendCollectCancleRequest(id, postion);
								} else {
								CustomToast.makeText(context, 0, errorMsg, 300).show();
								if (errorCode == -999) {
									new AlertDialog.Builder(
											context)
											.setTitle("提示")
											.setMessage("网络拥堵,请稍后重试！")
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
		SimpleDraweeView imageView,onlineImageView,headImage;
		TextView priceText, titleText, orderNumText, lookNumText,consult_person_truename,
				consult_person_education, consult_person_drive,
				consult_person_guide,is_veru;
		LinearLayout new_linear,look_linear,travel_linear;
	}
}
