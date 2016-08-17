package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.GuideEntity;
import com.uugty.uu.entity.GuideEntity.GuideDetail;

import java.util.ArrayList;
import java.util.List;

public class RecomendActivity extends BaseActivity implements
		SwipeRefreshLayout.OnRefreshListener, OnClickListener,
		OnItemClickListener, OnScrollListener {

	private ListView mListView;
	private int startId = 1;// 起始页页
	private TopBackView titleText;
	private String roadlineThemeId,roadlineThemeTitle;
	private SwipeRefreshLayout mSwipeLayout;
	private List<GuideDetail> guideDetailList = new ArrayList<GuideDetail>();
	private RecomendDetailAdapter adapter;
	private static final int INITIAL_DELAY_MILLIS = 500;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
					"GuideEntity");
			if (entity != null) {
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
				guideDetailList.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.recomend_theme_view;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			roadlineThemeId = getIntent().getStringExtra("roadlineThemeId");
			roadlineThemeTitle = getIntent().getStringExtra("roadlineThemeTitle");
		}
		mListView = (ListView) findViewById(R.id.guide_detail_listview);
		titleText = (TopBackView) findViewById(R.id.recomend_theme_title);
		titleText.setTitle(roadlineThemeTitle);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.guide_swipe_container);
		// 筛选按钮
		adapter = new RecomendDetailAdapter(this, guideDetailList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
				INITIAL_DELAY_MILLIS);

		mListView.setAdapter(swingBottomInAnimationAdapter);
	}

	@Override
	protected void initAction() {
		mListView.setOnItemClickListener(this);
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
		loadData(1, true);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("roadId", guideDetailList.get(position).getRoadlineId());
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setClass(this, FindTestViewPagerActivity.class);
		startActivity(intent);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		guideDetailList.clear();
	}

	private void loadData(final int what, final boolean msg) {
		// 显示等待层
		RequestParams params = new RequestParams();
		params.add("roadlineThemeId", roadlineThemeId); // 当前页数
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize

		APPRestClient.post(this, ServiceCode.RECOMEND_THEME_DETAIL, params,
				new APPResponseHandler<GuideEntity>(GuideEntity.class, this) {
					@Override
					public void onSuccess(GuideEntity result) {
						/*
						 * Message msg = handler.obtainMessage(); msg.what =
						 * what; msg.obj = result.getLIST();
						 * handler.sendMessage(msg);
						 */
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("GuideEntity", result);
							msg.setData(b);
							handler.sendMessage(msg);
						} else {
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							if (startId == 1) {
								mSwipeLayout.setVisibility(View.GONE);
							}
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						mSwipeLayout.setRefreshing(false);
						if (errorCode == -999) {
							new AlertDialog.Builder(RecomendActivity.this)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
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
		if (startId > 1) {
			if (firstVisibleItem == (startId - 1) * 5) {
				startId++;
				loadData(2, false);
			}
		}
	}
}

 class RecomendDetailAdapter extends BaseAdapter {
	private List<GuideDetail> ls;
	private Context context;

	public RecomendDetailAdapter(Context context, List<GuideDetail> list) {
		this.ls = list;
		this.context = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
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
			holder.onlineImageView = (SimpleDraweeView) convertView
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

		if (!ls.get(position).getRoadlineBackground().equals("")) {
			if (ls.get(position).getRoadlineBackground().contains("images")) {
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ ls.get(position).getRoadlineBackground()));
			} else {
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ "images/roadlineDescribe/"
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
		}
		if (!TextUtils.isEmpty(ls.get(position).getIsOnline())
				&& ls.get(position).getIsOnline().equals("1")) {
			holder.onlineImageView.setVisibility(View.VISIBLE);
		} else {
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
			holder.headImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ ls.get(position).getUserAvatar()));
		} else {
			holder.headImage.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}
		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView imageView, onlineImageView, headImage;
		TextView priceText, titleText, orderNumText, lookNumText,consult_person_truename,
				consult_person_education, consult_person_drive,
				consult_person_guide,is_veru;
		LinearLayout new_linear,look_linear,travel_linear;
	}

}
