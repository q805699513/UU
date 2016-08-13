package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.com.rightview.alipaywallet.AddWithDrawActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BoundBankEntity;
import com.uugty.uu.entity.BoundBankEntity.BankCardInfo;
import com.uugty.uu.viewpage.adapter.BankCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class BankcardActivity extends BaseActivity implements OnClickListener{

	private LinearLayout back;
	private LinearLayout add_bank;
	private ListView mListView;
	private BankCardAdapter adapter;
	private List<BankCardInfo> arryList;
	private String fromType = "";

	protected int getContentLayout() {
		// TODO Auto-generated method stub 银行卡
		return R.layout.right_pop_add;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			fromType = getIntent().getStringExtra("type");
		}
		back = (LinearLayout) findViewById(R.id.tabar_back);
		add_bank = (LinearLayout) findViewById(R.id.container_bill_add);
		mListView = (ListView) findViewById(R.id.bank_card_list);
		arryList = new ArrayList<BankCardInfo>();
		adapter = new BankCardAdapter(arryList, this,fromType);
		mListView.setAdapter(adapter);
	}

	@Override
	protected void initAction() {

		back.setOnClickListener(this);
		add_bank.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sendRequest();
		back.setClickable(true);

	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			back.setClickable(false);
			break;
		case R.id.container_bill_add:
			Intent intent = new Intent();
			intent.setClass(this, AddWithDrawActivity.class);
			startActivity(intent);			
			break;
		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_BOUND_BANK_CARDLIST, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,this) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						if (result.getLIST().size() > 0) {
							arryList = result.getLIST();
							adapter.updateList(arryList);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(BankcardActivity.this)
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
	

}
