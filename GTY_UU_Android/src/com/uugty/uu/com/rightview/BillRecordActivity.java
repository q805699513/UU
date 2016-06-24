package com.uugty.uu.com.rightview;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BillRecordEntity;
import com.uugty.uu.entity.BillRecordEntity.BillRecord;
import com.uugty.uu.viewpage.adapter.BillRecordListAdapter;

public class BillRecordActivity extends BaseActivity implements
		OnRefreshListener, OnClickListener, OnItemClickListener,
		 OnScrollListener {
	private ListView list_view;
	private BillRecordListAdapter adapter;
	private List<BillRecord> list = new ArrayList<BillRecord>();
	private TopBackView title;
	// loading等待
	private SpotsDialog loadingDialog;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.bill_record;
	}

	@Override
	protected void initGui() {
		list_view = (ListView) findViewById(R.id.bill_record_listview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.bills_swipe_container);
		title = (TopBackView) findViewById(R.id.bill_record_title);

		title.setTitle("账单列表");
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		adapter = new BillRecordListAdapter(this, list);
		list_view.setAdapter(adapter);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		loadData(1,true);
		list_view.setOnItemClickListener(this);
		list_view.setOnScrollListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void loadData(final int what,final boolean msg) {
		// 显示等待层
		if (what == 1&&msg) {
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
			}
		}

		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "8"); // pageSize

		APPRestClient.post(this, ServiceCode.BILL_RECORD_LIST, params,
				new APPResponseHandler<BillRecordEntity>(
						BillRecordEntity.class, this) {
					@Override
					public void onSuccess(BillRecordEntity result) {
						Message msg = handler.obtainMessage();
						msg.what = what;
						msg.obj = result.getLIST();
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData(what, msg);
						} else {
						if(null!=loadingDialog){
							loadingDialog.dismiss();
						}
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(BillRecordActivity.this)
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
			List<BillRecord> result = null;
			if (msg.obj != null) {
				result = (List<BillRecord>) msg.obj;
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							loadingDialog.dismiss();
						}
					}, 300);
					loadData(2,false);
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
		try {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("icon", list.get(position).getRecordType());
			if (list.get(position).getRecordType().equals("1")) {
				bundle.putString("title", chooseBankName(list.get(position)
						.getRecordType())
						+ "-"
						+ list.get(position ).getRoadlineTitle());
			} else {
				bundle.putString("title", chooseBankName(list.get(position )
						.getRecordType()));
			}
			bundle.putString("price", list.get(position )
					.getRecordTradeMoney());
			bundle.putString("status", list.get(position ).getRecordStatus());
			bundle.putString("time", list.get(position )
					.getRecordTradeDate());

			bundle.putString("payment", paymentType(list.get(position)
					.getRecordType()));

			intent.putExtras(bundle);
			intent.setClass(this, BillRecordDetailActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		startId = 1;
		loadData(1,false);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
	}

	public String chooseBankName(String type) {
		String name = "";
		if (type.equals("order_wx_send"))
			name = "钱包微信支付";
		if (type.equals("order_purse_send"))
			name = "钱包微信支付";
		if (type.equals("order_receive"))
			name = "订单接收";
		if (type.equals("gratuity_wx_send"))
			name = "小费微信发送";
		if (type.equals("gratuity_purse_send"))
			name = "小费钱包发送";
		if (type.equals("gratuity_receive"))
			name = "小费接收";
		if (type.equals("widthdraw"))
			name = "提现";
		if (type.equals("recharge"))
			name = "充值";
		if (type.equals("drawback_outcome"))
			name = "退款支付";
		if (type.equals("drawback_income"))
			name = "退款收入";
		if (type.equals("penalty "))
			name = "违约金";
		if (type.equals("order_wxpublic_send"))
			name = "微信公众号支付旅游订单";
		if (type.equals("wxpublic_recharge"))
			name = "微信公众号充值";
		if (type.equals("beneficiary"))
			name = "分销奖励";
		return name;
	}

	public String paymentType(String payWay) {
		String name = "";
		if (payWay.equals("order_wx_send"))
			name = "1";
		if (payWay.equals("order_purse_send"))
			name = "2";
		if (payWay.equals("order_receive"))
			name = "2";
		if (payWay.equals("gratuity_wx_send"))
			name = "1";
		if (payWay.equals("gratuity_purse_send"))
			name = "2";
		if (payWay.equals("gratuity_receive"))
			name = "2";
		if (payWay.equals("widthdraw"))
			name = "2";
		if (payWay.equals("recharge"))
			name = "1";
		if (payWay.equals("drawback_outcome"))
			name = "2";
		if (payWay.equals("drawback_income"))
			name = "2";
		if (payWay.equals("penalty "))
			name = "2";
		return name;
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
				loadData(2,false);
			}
		}
	}
}
