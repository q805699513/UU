package com.uugty.uu.shop.guide.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.RoadEntity.RoadDetail;
import com.uugty.uu.com.find.FindViewFragment_explain;

public class FindViewFragment_guide_explain extends Fragment {
	private View view;
	private TextView roadContentTextView;
	private RoadDetail roadDetailResult;
	private LinearLayout findview_fragment_explain_view_price_linear,
			findview_fragment_explain_view_content_linear,
			findview_fragment_explain_view_line_linear;
	private TextView findview_fragment_explain_roadlineInfo,
			findview_fragment_explain_roadlineStartArea,
			findview_fragment_explain_roadlineSpecialMark,
			findview_fragment_explain_roadlinePrice,
			findview_fragment_explain_roadlineFeeContains,
			findview_fragment_explain_view_price_text,
			findview_fragment_explain_view_content_text,
			findview_fragment_explain_view_line_text,
			findview_fragment_explain_roadlineFeeContains_text;
	private String roadId;
	private RoadEntity roadEntity;

	public static FindViewFragment_guide_explain newInstance(String roadId) {
		FindViewFragment_guide_explain newFragment = new FindViewFragment_guide_explain();
		Bundle bundle = new Bundle();
		bundle.putString("roadId", roadId);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.findviewfragment_explain_view, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		Bundle args = getArguments();
		roadId = args.getString("roadId");
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		if(null==roadEntity){
			sendRequest();
		}else{
			Message msg = Message.obtain();
			msg.what = 1;
			Bundle b = new Bundle();
			b.putSerializable("roadLine", roadEntity);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		findview_fragment_explain_roadlineFeeContains_text = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlineFeeContains_text);
		roadContentTextView = (TextView) view
				.findViewById(R.id.find_detail_road_content);
		findview_fragment_explain_view_price_linear = (LinearLayout) view
				.findViewById(R.id.findview_fragment_explain_view_price_linear);
		findview_fragment_explain_view_content_linear = (LinearLayout) view
				.findViewById(R.id.findview_fragment_explain_view_content_linear);
		findview_fragment_explain_view_line_linear = (LinearLayout) view
				.findViewById(R.id.findview_fragment_explain_view_line_linear);
		findview_fragment_explain_roadlineInfo = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlineInfo);
		findview_fragment_explain_roadlineStartArea = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlineStartArea);
		findview_fragment_explain_view_price_text = (TextView) view
				.findViewById(R.id.findview_fragment_explain_view_price_text);
		findview_fragment_explain_roadlineSpecialMark = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlineSpecialMark);
		findview_fragment_explain_roadlinePrice = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlinePrice);
		findview_fragment_explain_roadlineFeeContains = (TextView) view
				.findViewById(R.id.findview_fragment_explain_roadlineFeeContains);
		findview_fragment_explain_view_content_text = (TextView) view
				.findViewById(R.id.findview_fragment_explain_view_content_text);
		findview_fragment_explain_view_line_text = (TextView) view
				.findViewById(R.id.findview_fragment_explain_view_line_text);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				RoadEntity roadEntityResult = (RoadEntity) msg.getData()
						.getSerializable("roadLine");
				if (null != roadEntityResult.getOBJECT()) {
					roadDetailResult = roadEntityResult.getOBJECT();
					if (!roadEntityResult.getOBJECT().getRoadlineSpecialMark()
							.equals("")) {
						findview_fragment_explain_view_line_linear
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_line_text
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_price_text
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_price_linear
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_content_text
								.setVisibility(View.GONE);
						findview_fragment_explain_view_content_linear
								.setVisibility(View.GONE);

						findview_fragment_explain_roadlineInfo
								.setText(roadEntityResult.getOBJECT()
										.getRoadlineInfo());
						findview_fragment_explain_roadlineStartArea
								.setText(roadEntityResult.getOBJECT()
										.getRoadlineStartArea());
						findview_fragment_explain_roadlineSpecialMark
								.setText(roadEntityResult.getOBJECT()
										.getRoadlineSpecialMark());
						findview_fragment_explain_roadlinePrice
								.setText(roadEntityResult.getOBJECT()
										.getRoadlinePrice() + "（仅针对旅游同行内部价）");
						findview_fragment_explain_roadlineFeeContains
								.setText(roadEntityResult.getOBJECT()
										.getRoadlineFeeContains());
					} else if (roadEntityResult.getOBJECT().getRoadlineDays()
							.equals("1")) {
						findview_fragment_explain_view_line_linear
								.setVisibility(View.GONE);
						findview_fragment_explain_view_line_text
								.setVisibility(View.GONE);
						findview_fragment_explain_view_price_text
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_price_linear
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_content_text
								.setVisibility(View.GONE);
						findview_fragment_explain_view_content_linear
								.setVisibility(View.GONE);
						findview_fragment_explain_roadlinePrice
								.setText(roadEntityResult.getOBJECT()
										.getRoadlinePrice() + "（仅针对旅游同行内部价）");
						if (roadEntityResult.getOBJECT()
								.getRoadlineFeeContains().equals("")) {
							findview_fragment_explain_roadlineFeeContains_text.setVisibility(View.GONE);
							findview_fragment_explain_roadlineFeeContains
									.setVisibility(View.GONE);
						} else {
							findview_fragment_explain_roadlineFeeContains_text.setVisibility(View.VISIBLE);
							findview_fragment_explain_roadlineFeeContains
									.setVisibility(View.VISIBLE);
							findview_fragment_explain_roadlineFeeContains
									.setText(roadEntityResult.getOBJECT()
											.getRoadlineFeeContains());
						}
					} else {
						findview_fragment_explain_view_line_linear
								.setVisibility(View.GONE);
						findview_fragment_explain_view_line_text
								.setVisibility(View.GONE);
						findview_fragment_explain_view_price_text
								.setVisibility(View.GONE);
						findview_fragment_explain_view_price_linear
								.setVisibility(View.GONE);
						findview_fragment_explain_view_content_text
								.setVisibility(View.VISIBLE);
						findview_fragment_explain_view_content_linear
								.setVisibility(View.VISIBLE);

						roadContentTextView.setText(roadEntityResult
								.getOBJECT().getRoadlineContent());
					}
				}
				break;
			}
		};
	};

	private void sendRequest() {

		RequestParams params = new RequestParams();
		params.add("roadlineId", roadId);// 路线Id
		APPRestClient.postGuide(getActivity(), ServiceCode.ROAD_LINE_DETAIL_MESSAGE,
				params, new APPResponseHandler<RoadEntity>(RoadEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(RoadEntity result) {
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
}
