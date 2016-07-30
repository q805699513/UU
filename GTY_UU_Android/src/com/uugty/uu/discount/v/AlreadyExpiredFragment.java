package com.uugty.uu.discount.v;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.discount.c.MyDiscountActivity;
import com.uugty.uu.discount.m.DiscountAdapter;
import com.uugty.uu.discount.m.DiscountListItem;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;

import java.util.ArrayList;
import java.util.List;

public class AlreadyExpiredFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	private ListView list_view;
	private View view;
	private DiscountAdapter adapter;
	private List<DiscountEntity> list = new ArrayList<DiscountEntity>();
	private SwipeRefreshLayout mSwipeLayout;
	// 空界面
	private LinearLayout noDiscountRel;
	private TextView noDiscountText;
	private MyDiscountActivity mActivity;
	private RelativeLayout load_date_discount;
	private SpotsDialog loadingDialog;//加载动画
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_discount_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		list_view = (ListView) view.findViewById(R.id.discount_auto_listview);
		load_date_discount = (RelativeLayout) view.findViewById(R.id.load_date_discount);
		noDiscountRel = (LinearLayout) view
				.findViewById(R.id.discount_list_no_data_end_rel);
		noDiscountText = (TextView) view
				.findViewById(R.id.discount_list_no_data_end_text);
		mSwipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.discount_finish_swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		adapter = new DiscountAdapter(list, getActivity());
		list_view.setAdapter(adapter);
		loadData(1,false);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<DiscountEntity> result = null;
			if (msg.obj != null) {
				result = (List<DiscountEntity>) msg.obj;
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					load_date_discount.setVisibility(View.VISIBLE);
					new Handler().postDelayed(new Runnable(){   
			            public void run() {  
			                   //显示dialog
			            	loadingDialog.dismiss();			            	
			            }  
			        }, 500);
					if (list.size() > 0) {
						mSwipeLayout.setVisibility(View.VISIBLE);
						noDiscountRel.setVisibility(View.GONE);
					}
					break;
				}
				adapter.notifyDataSetChanged();

			} 
		};
	};
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MyDiscountActivity) activity;
	}

	private void loadData(final int what,final boolean isPull) {
		if(!isPull){
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
				loadingDialog = new SpotsDialog(getActivity());
				loadingDialog.show();
			}
		}
		// 显示等待层
		RequestParams params = new RequestParams();
		params.add("couponUserStatus", "3"); // 已过期																		// 1游客
		APPRestClient.post(getActivity(), ServiceCode.DISCOUNT_QUERY, params,
				new APPResponseHandler<DiscountListItem>(DiscountListItem.class,
						getActivity()) {
					@Override
					public void onSuccess(DiscountListItem result) {

						if (null != result && null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = handler.obtainMessage();
							msg.what = what;
							msg.obj = result.getLIST();
							handler.sendMessage(msg);
						} else {
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							//没有数据显示空白页
							mSwipeLayout.setVisibility(View.GONE);
							noDiscountRel.setVisibility(View.VISIBLE);
							new Handler().postDelayed(new Runnable() {
								public void run() {
									// 显示dialog
									loadingDialog.dismiss();
								}
							}, 500);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what,isPull);
						} else {
						loadingDialog.dismiss();
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
	public void onRefresh() {
		loadData(1,true);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		list.clear();
	}
}
