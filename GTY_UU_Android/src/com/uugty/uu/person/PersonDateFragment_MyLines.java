package com.uugty.uu.person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.WaveView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.MoreLvEntity;
import com.uugty.uu.entity.MoreLvEntity.MoreListEntity;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.mhvp.core.magic.viewpager.AbsBaseFragment;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerListView;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerScroller;

import java.util.ArrayList;
import java.util.List;


public class PersonDateFragment_MyLines extends AbsBaseFragment implements
		OnScrollListener {
	private View view;
	protected InnerListView mListView;
	private String userId, avatar;
	private PersonDateDetailAdapter adapter;
	private int startId = 1;// 起始页页
	private WaveView waveView;
	private Button btn;
	private List<MoreListEntity> homePageList = new ArrayList<MoreListEntity>();

	public static PersonDateFragment_MyLines newInstance(String userId, String avatar) {
		PersonDateFragment_MyLines newFragment = new PersonDateFragment_MyLines();
		Bundle bundle = new Bundle();
		bundle.putString("detailUserId", userId);
		bundle.putString("avatar", avatar);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	public InnerScroller getInnerScroller() {
		return mListView;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.persondate_myself_item, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		Bundle args = getArguments();
		userId = args.getString("detailUserId");
		avatar = args.getString("avatar");
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mListView = (InnerListView) view
				.findViewById(R.id.persondate_myself_lines);
		waveView = (WaveView) view.findViewById(R.id.my_services_no_data_rel);
		btn = (Button) view.findViewById(R.id.my_services_no_data_release_btn);
		mListView.setDividerHeight(0);
		mListView.register2Outer(mOuterScroller, mIndex);
		adapter = new PersonDateDetailAdapter(homePageList, getActivity(),
				avatar);
		mListView.setAdapter(adapter);
		if (homePageList.size() > 0) {
			adapter.notifyDataSetChanged();
		} else {
			sendRequestMoreLine(1);
		}

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.person.PersonCenterActivity");
				Intent intent = new Intent();
				intent.setClass(getActivity(),
						PublishServicesActivity.class);
				startActivity(intent);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Object object = parent.getAdapter().getItem(position);
				if (null != object) {
					MoreListEntity entity = (MoreListEntity) object;
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.com.find.FindTestViewPagerActivity");
					Intent intent = new Intent();
					intent.putExtra("roadId", entity.getRoadlineId());
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(getActivity(),
							FindTestViewPagerActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			MoreLvEntity roadEntityResult = (MoreLvEntity) msg.getData()
					.getSerializable("morelist");
			if (roadEntityResult != null) {
				List<MoreListEntity> result = (List<MoreListEntity>) roadEntityResult
						.getLIST();
				switch (msg.what) {
				case 1:
					homePageList.clear();
					homePageList.addAll(result);
					mListView.setVisibility(View.VISIBLE);
					startId++;
					sendRequestMoreLine(2);
					break;
				case 2:
					homePageList.addAll(result);
					break;
				}
				if(homePageList.size()>0){
					mListView.setVisibility(View.VISIBLE);
					waveView.setVisibility(View.GONE);

				}else{
					mListView.setVisibility(View.GONE);
					waveView.setVisibility(View.VISIBLE);
				}
				adapter.notifyDataSetChanged();

			}
		};
	};

	private void sendRequestMoreLine(final int what) {
		RequestParams params = new RequestParams();
		params.add("userId", userId);
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		APPRestClient.post(getActivity(), ServiceCode.ROAD_LINE_ALL_LINE,
				params, new APPResponseHandler<MoreLvEntity>(
						MoreLvEntity.class, getActivity()) {
					@Override
					public void onSuccess(MoreLvEntity result) {

							Message msg = Message.obtain();
							msg.what = what;
							Bundle b = new Bundle();
							b.putSerializable("morelist", result);
							msg.setData(b);
							handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequestMoreLine(1);
						} else {
							CustomToast.makeText(getActivity(), 0, errorMsg,
									300).show();
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
						}
					}

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
				sendRequestMoreLine(2);
			}
		}
	}
}

class PersonDateDetailAdapter extends BaseAdapter {
	private List<MoreListEntity> ls;
	private Context context;
	private String title;

	public PersonDateDetailAdapter(List<MoreListEntity> ls, Context context,
			String title) {
		super();
		this.ls = ls;
		this.context = context;
		this.title = title;
	}

	@Override
	public int getCount() {
		return ls.size();
	}

	@Override
	public MoreListEntity getItem(int position) {
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
			holder.isOnlineView = (SimpleDraweeView) convertView
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

		holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
				+ "images/roadlineDescribe/"
				+ (ls.get(position).getRoadlineBackground())));

		holder.priceText.setText(ls.get(position).getRoadlinePrice());
		if (!TextUtils.isEmpty(ls.get(position).getIsNew())
				&& ls.get(position).getIsNew().equals("1")) {
			holder.new_linear.setVisibility(View.VISIBLE);
		} else {
			holder.new_linear.setVisibility(View.GONE);
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
		if (null != title && !title.equals("")) {
			holder.headImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ title));
		} else {
			holder.headImage.setImageURI(Uri.parse("res///"
					+ R.drawable.no_default_head_img));
		}
		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView imageView, headImage, isOnlineView;
		TextView priceText, titleText, orderNumText, lookNumText, consult_person_truename,
				consult_person_education, consult_person_drive,
				consult_person_guide, is_veru;
		LinearLayout new_linear, look_linear, travel_linear;
	}
}
