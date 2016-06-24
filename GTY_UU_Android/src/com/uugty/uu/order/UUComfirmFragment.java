package com.uugty.uu.order;

import java.util.ArrayList;
import java.util.List;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.OrderListItem;
import com.uugty.uu.entity.OrderListItem.ItemEntity;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.person.PersonCompileActivity;
import com.uugty.uu.viewpage.adapter.ListViewAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UUComfirmFragment extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
		OnScrollListener, OnClickListener {

	private ListView list_view;
	private View view;
	private ListViewAdapter adapter;
	private List<ItemEntity> list = new ArrayList<ItemEntity>();
	private SwipeRefreshLayout mSwipeLayout;
	private int startId = 1;// 起始页页
	// 空界面
	private LinearLayout noOrderRel;
	private Button noOrderBtn;
	private TextView noOrderTextOne, noOrderTextTwo;
	private boolean isFirst = true;// 首次进入页面
	private UUOrderActivity mActivity;
	private SpotsDialog loadingDialog;
	private RelativeLayout load_date_frag;
	private String port;//接口
	private String isComment;//是否评价
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view == null) {
			view = inflater.inflate(R.layout.activity_uuordertwo, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		list_view = (ListView) view.findViewById(R.id.carry_list);
		load_date_frag = (RelativeLayout) view
				.findViewById(R.id.load_date_frag);
		load_date_frag.setVisibility(View.INVISIBLE);
		mSwipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.order_swipe_container);
		noOrderRel = (LinearLayout) view
				.findViewById(R.id.order_list_no_data_ing_rel);
		noOrderBtn = (Button) view
				.findViewById(R.id.order_list_no_data_ing_btn);
		noOrderTextOne = (TextView) view
				.findViewById(R.id.order_list_no_data_ing_text_one);
		noOrderTextTwo = (TextView) view
				.findViewById(R.id.order_list_no_data_ing_text_two);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		adapter = new ListViewAdapter(list, getActivity(), ((UUOrderActivity)getActivity()).getRole());
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(this);
		list_view.setOnScrollListener(this);
		noOrderBtn.setOnClickListener(this);
		if (((UUOrderActivity)getActivity()).getRole().equals("2")) {
			noOrderTextOne.setText("没有任何购买订单");
			noOrderTextTwo.setText("快去完善个人资料,接单机会更多");
			noOrderBtn.setText("完善资料");
		}
		loadData(1, true);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (UUOrderActivity) activity;
		mActivity.setcomfirHandler(mHandler);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				onRefresh();
				break;
			case 33:
				onRefresh();
				break;

			default:
				break;
			}
		}
	};

	private void loadData(final int what, final boolean msg) {
		if (what == 1 && msg) {
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(getActivity());
			loadingDialog.show();
			}
		}
		RequestParams params = new RequestParams();
		params.add("orderStatus", "3"); // 订单的状态
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		if(((UUOrderActivity)getActivity()).getRole().equals("1")){
			port=ServiceCode.UK_BATCH_ORDER_LIST;
		}else{
			port=ServiceCode.XIAOU_BATCH_ORDERLIST;
		}
		APPRestClient.post(getActivity(), port, params,
				new APPResponseHandler<OrderListItem>(OrderListItem.class,
						getActivity()) {
					@Override
					public void onSuccess(OrderListItem result) {
						if (null != result.getLIST()
								&& result.getLIST().size() > 0) {
							Message msg = handler.obtainMessage();
							msg.what = what;
							msg.obj = result.getLIST();
							handler.sendMessage(msg);
						} else {
							if (mSwipeLayout != null) {
								mSwipeLayout.setRefreshing(false);
							}
							if (startId == 1) {
								load_date_frag.setVisibility(View.VISIBLE);
								mSwipeLayout.setVisibility(View.GONE);
								noOrderRel.setVisibility(View.VISIBLE);
								new Handler().postDelayed(new Runnable() {
									public void run() {
										// 显示dialog
										loadingDialog.dismiss();
									}
								}, 500);
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						loadingDialog.dismiss();
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
						if (errorCode == 3) {
							new AlertDialog.Builder(getActivity())
									.setTitle("提示")
									.setMessage("登录超时!")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// 默认自动登录
													dialog.dismiss();
												}
											}).show();
						}
					}}

					@Override
					public void onFinish() {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								// 显示dialog
								ActivityCollector
										.removeSpecifiedActivity("com.uugty.uu.com.rightview.PriceDetailActivity");
							}
						}, 300);

					}
				});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<ItemEntity> result = null;
			if (msg.obj != null) {
				result = (List<ItemEntity>) msg.obj;
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					load_date_frag.setVisibility(View.VISIBLE);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							loadingDialog.dismiss();
						}
					}, 500);
					loadData(2, false);
					if (list.size() > 0) {
						isFirst = false;
						mSwipeLayout.setVisibility(View.VISIBLE);
						noOrderRel.setVisibility(View.GONE);
					}
					break;
				case 2:

					list.addAll(result);
					break;

				}
				adapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		if (position <= list.size()) {
			String orderId = list.get(position).getOrderId();
			isComment = list.get(position).getIsEval();
			if (list.get(position).getOrderStatus().equals("order_create")
					&& ((UUOrderActivity)getActivity()).getRole().equals("1")) {
				intent.putExtra("orderId", orderId);
				intent.setClass(getActivity(), UUOrederPayActivity.class);
			} else if (list.get(position).getOrderStatus()
					.equals("order_drawback")
					&& ((UUOrderActivity)getActivity()).getRole().equals("2")) {
				intent.putExtra("orderId", orderId);
				intent.putExtra("role", ((UUOrderActivity)getActivity()).getRole());
				intent.setClass(getActivity(), UUOrderPayDetailActivity.class);
			} else {
				intent.putExtra("orderId", orderId);
				intent.putExtra("isComment", isComment);
				intent.putExtra("fragment", "confirm");
				intent.putExtra("role", ((UUOrderActivity)getActivity()).getRole());
				intent.setClass(getActivity(), UUOrderPayDetailActivity.class);
			}
			startActivity(intent);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
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
		if (list_view.getCount() == 0) {
			mSwipeLayout.setVisibility(View.GONE);
			noOrderRel.setVisibility(View.VISIBLE);

		}
	}

	@Override
	public void onRefresh() {
		if (((UUOrderActivity)getActivity()).getRole().equals("2")) {
			noOrderTextOne.setText("没有任何购买订单");
			noOrderTextTwo.setText("快去完善个人资料,接单机会更多");
			noOrderBtn.setText("完善资料");
		}else{
			noOrderTextOne.setText("没有购买任何路线");
			noOrderTextTwo.setText("没有购买任何路线,快去发现好分景");
			noOrderBtn.setText("去浏览");
		}
		startId = 1;
		adapter.setRole(((UUOrderActivity)getActivity()).getRole());
		loadData(1, false);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.order_list_no_data_ing_btn:
			// 游客模式
			if (((UUOrderActivity)getActivity()).getRole().equals("1")) {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("toPage", "order");
				intent.setClass(getActivity(), MainActivity.class);
				getActivity().startActivity(intent);

			}
			// 导游模式
			if (((UUOrderActivity)getActivity()).getRole().equals("2")) {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(getActivity(), PersonCompileActivity.class);
				getActivity().startActivity(intent);
			}
			break;

		default:
			break;
		}

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			switch (requestCode) {
			case 1000:
				onRefresh();
				break;
			
			default:
				break;
			}
		}
	}

}
