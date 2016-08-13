package com.uugty.uu.com.rightview.alipaywallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BankCardEntity;

public class VerifyAliPayActivity extends BaseActivity implements OnClickListener {

	private EditText nameEditText;
	private Button commitBtn;
	private String name = "", cardNo = "";
	private String bankId="";
	private LinearLayout right_back_add;
	private TextView mVerifyText;

	protected int getContentLayout() {

		return R.layout.verify_alipay_account;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			cardNo = getIntent().getStringExtra("alipayAccount");
			if(null != getIntent().getStringExtra("name")){
				name = getIntent().getStringExtra("name");
				bankId = getIntent().getStringExtra("bankId");
			}
		}
		right_back_add = (LinearLayout) findViewById(R.id.tabar_back);
		nameEditText = (EditText) findViewById(R.id.add_alipay_card_num_edit);
		mVerifyText = (TextView) findViewById(R.id.verify_alipaynumber);
		mVerifyText.setText("支付宝账号" + cardNo + "以验证成功");
		commitBtn = (Button) findViewById(R.id.add_alipay_card_btn);
		commitBtn.setEnabled(false);
		if(!name.equals("")){
			nameEditText.setText(name);
		}
		if (!name.equals("") && !cardNo.equals("")) {
			commitBtn
					.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
			commitBtn.setEnabled(true);
		} else {
			commitBtn
					.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
			commitBtn.setEnabled(false);
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		right_back_add.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
		nameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				name = s.toString();
				if (!name.equals("") && !cardNo.equals("")) {
					commitBtn
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					commitBtn.setEnabled(true);
				} else {
					commitBtn
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					commitBtn.setEnabled(false);
				}
			}
		});

	}

	@Override
	protected void initData() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		right_back_add.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			right_back_add.setClickable(false);
			break;
		case R.id.add_alipay_card_btn:
			CustomDialog.Builder builder1 = new CustomDialog.Builder(
					ctx);
			builder1.setMessage("提交前请仔细核对,以免造成不必要的损失.");
			builder1.setRelationShip(true);
			builder1.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
											int which) {
							if(!bankId.equals("")){
								updateRequest();
							}else {
								sendRequest();
							}
							dialog.dismiss();
						}
					});

			builder1.setNegativeButton(
					"取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
											int which) {
							dialog.dismiss();
						}
					});

			builder1.create().show();

			break;

		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("bankCard", cardNo);// 银行卡号
		params.add("bankCardType", "8");// 提现银行卡号的所属银行
		params.add("bankIsDefault", "1");// 是否是默认提现银行卡号 1 是 ，0 否
		params.add("bankOwner", nameEditText.getText().toString().trim());
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
						+ ServiceCode.USER_BOUND_BANK_CARD, params, true,
				new APPResponseHandler<BankCardEntity>(BankCardEntity.class,
						this) {
					@Override
					public void onSuccess(BankCardEntity result) {
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.alipaywallet.AddWithDrawActivity");
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.alipaywallet.AddAliPayActivity");
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(VerifyAliPayActivity.this)
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

	private void updateRequest() {
		RequestParams params = new RequestParams();
		params.add("bankCard", cardNo);// 银行卡号
		params.add("bankId",bankId);
		params.add("bankOwner", nameEditText.getText().toString().trim());
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
						+ ServiceCode.USER_UPDATE_BANK_CARD, params, true,
				new APPResponseHandler<BankCardEntity>(BankCardEntity.class,
						this) {
					@Override
					public void onSuccess(BankCardEntity result) {
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.alipaywallet.AddWithDrawActivity");
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.alipaywallet.AddAliPayActivity");
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							updateRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(VerifyAliPayActivity.this)
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
