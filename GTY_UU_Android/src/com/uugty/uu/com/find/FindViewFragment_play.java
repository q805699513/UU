package com.uugty.uu.com.find;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.RoadEntity.RoadDetail;
import com.uugty.uu.entity.RoadLine;

import java.util.List;

public class FindViewFragment_play extends Fragment {
	private ListView find_listview;
	private FindListAdapter findadapter;
	private View view;
	private String roadId;
	private RoadEntity roadEntity;
	
	static FindViewFragment_play newInstance(String roadId) {
		FindViewFragment_play newFragment = new FindViewFragment_play();
		Bundle bundle = new Bundle();
		bundle.putString("roadId", roadId);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		roadId = args.getString("roadId");
		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.findviewfragment_play_item, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		find_listview = (ListView) view.findViewById(R.id.find_list_view);
		find_listview.setFocusable(false);
		if (null == roadEntity) {
			sendRequest();
		} else {
			Message msg = Message.obtain();
			msg.what = 1;
			Bundle b = new Bundle();
			b.putSerializable("roadLine", roadEntity);
			msg.setData(b);
			handler.sendMessage(msg);
		}

	}
	
	private void sendRequest() {

		RequestParams params = new RequestParams();
		params.add("roadlineId", roadId);// 路线Id
		APPRestClient.post(getActivity(), ServiceCode.ROAD_LINE_DETAIL_MESSAGE,
				params, new APPResponseHandler<RoadEntity>(RoadEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(RoadEntity result) {
						roadEntity = result;
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("roadLine", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				RoadEntity roadEntityResult = (RoadEntity) msg.getData()
						.getSerializable("roadLine");
				if (null != roadEntityResult.getOBJECT()) {
					RoadDetail roadDetailResult = roadEntityResult.getOBJECT();

					findadapter = new FindListAdapter(getActivity(),
							roadDetailResult.getRoadlineDescribes());
					find_listview.setAdapter(findadapter);
					// Utility.setListViewHeightBasedOnChildren(find_listview);
					break;
				}
			}
		};
	};
	

	class FindListAdapter extends BaseAdapter {
		// private ViewManger viewmanger;
		private List<RoadLine> roadAdapterList;
		private Context context;

		// private Handler handler = new Handler();

		public FindListAdapter(Context context, List<RoadLine> ls) {
			this.roadAdapterList = ls;
			this.context = context;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return roadAdapterList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return roadAdapterList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.find_route_display_paly, null);
				holder.imageView = (SimpleDraweeView) view
						.findViewById(R.id.find_route_display_paly_image);
				holder.dayText = (TextView) view
						.findViewById(R.id.find_route_display_play_days);
				holder.timeText = (TextView) view
						.findViewById(R.id.find_route_display_play_time);
				holder.explainText = (TextView) view
						.findViewById(R.id.find_route_display_play_explain);
				holder.findfrag_play_data_lin = (LinearLayout) view
						.findViewById(R.id.findfrag_play_data_lin);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.explainText.setText(roadAdapterList.get(position)
					.getDescribeArea());
			if (roadAdapterList.get(position).getDescribeTime().equals("")) {
				holder.findfrag_play_data_lin.setVisibility(View.GONE);
				holder.imageView.setAspectRatio(1.714f);
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ "images/roadlineDescribe/"
						+ roadAdapterList.get(position).getDescribeImage()));
			} else if (roadAdapterList.get(position).getDescribeTime()
					.contains("_")) {
				String strs[] = roadAdapterList.get(position).getDescribeTime()
						.split("_");
				holder.findfrag_play_data_lin.setVisibility(View.VISIBLE);
				holder.imageView.setAspectRatio(1.714f);
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ "images/roadlineDescribe/"
						+ roadAdapterList.get(position).getDescribeImage()));
				if (strs.length == 2) {
					if (null != strs[0] && !strs[0].equals("")) {
						holder.dayText.setText(strs[0]);
					} else {
						holder.dayText.setText("");
					}
					if (null != strs[1] && !strs[1].equals("")) {
						holder.timeText.setText(strs[1]);
					} else {
						holder.timeText.setText("");
					}
				} else {
					holder.dayText.setText("");
					holder.timeText.setText("");
				}
			} else {
				holder.findfrag_play_data_lin.setVisibility(View.GONE);
				holder.imageView.setAspectRatio(Float.valueOf(roadAdapterList.get(position).getDescribeTime()));
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ "images/roadlineDescribe/"
						+ roadAdapterList.get(position).getDescribeImage()));
			}

			return view;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}

	private static class ViewHolder {
		SimpleDraweeView imageView;
		TextView dayText, timeText, explainText;
		LinearLayout findfrag_play_data_lin;
	}

}
