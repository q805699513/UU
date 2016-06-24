package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.BalanDialog;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.DrawalCashEntity;
import com.uugty.uu.entity.UserPurseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.password.view.GridPasswordView;
import com.uugty.uu.password.view.GridPasswordView.OnPasswordChangedListener;
import com.uugty.uu.util.Md5Util;

public class WithdrawcashActivity extends BaseActivity implements
		OnClickListener, OnPasswordChangedListener {

	private ImageView back;
	private Button withcash_button;
	private RelativeLayout cashWayRel;
	private TextView bankText, cardNoText, with_Draw_Money;
	private String bankType, cardNo, bankId;
	private EditText moneyNoEdit;
	AlertDialog.Builder bulider;
	AlertDialog dialog;//
	private GridPasswordView pwdEdit;
	private String priceNum;
	private String can_money;// 可提现金额

	Handler handler = new Handler() {
		Intent intent = new Intent();

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dialog.dismiss();
				CustomToast.makeText(WithdrawcashActivity.this, 0,
						"您的提现金额将在1到3个工作日到账", 200).show();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(WithdrawcashActivity.this,
						MyPriceActivity.class);
				startActivity(intent);
				finish();
				break;
			case 2:
				Bundle bundle = msg.getData();
				String bundleText = bundle.getString("msg");
				if (bundleText.equals("你账户的余额不足！")) {
					BalanDialog.Builder balanDialog = new BalanDialog.Builder(
							WithdrawcashActivity.this);
					balanDialog
							.setPositiveButton(new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					balanDialog.create().show();
				}
				CustomToast.makeText(WithdrawcashActivity.this, 0, bundleText,
						200).show();
				dialog.dismiss();
				break;
			}
			super.handleMessage(msg);
		}

	};

	protected int getContentLayout() {
		// TODO Auto-generated method stub 提现
		return R.layout.right_view_withdrawcash;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			bankType = getIntent().getStringExtra("bankType");
			cardNo = getIntent().getStringExtra("cardNo");
			bankId = getIntent().getStringExtra("cardId");
		}
		with_Draw_Money = (TextView) findViewById(R.id.with_Draw_Money);
		back = (ImageView) findViewById(R.id.right_view_withdrawcash_back);
		withcash_button = (Button) findViewById(R.id.Withdrawcash_button);
		cashWayRel = (RelativeLayout) findViewById(R.id.widthdraw_cash_way_rel);
		bankText = (TextView) findViewById(R.id.widthdraw_cash_bank);
		cardNoText = (TextView) findViewById(R.id.widthdraw_cash_card_no);
		bankText.setText(bankType);
		cardNoText.setText("尾号"
				+ cardNo.substring(cardNo.length() - 4, cardNo.length()));
		moneyNoEdit = (EditText) findViewById(R.id.recharge_price_detail);

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		setPricePoint(moneyNoEdit);
		withcash_button.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View view) {
				if(null==MyApplication.getInstance().getUserInfo()){
					Intent intent=new Intent();
					intent.putExtra("topage",
							WithdrawcashActivity.class.getName());
					intent.setClass(WithdrawcashActivity.this, LoginActivity.class);
					startActivity(intent);
				}else{
				if (!moneyNoEdit.getText().toString().trim().equals("")) {
					if (Double.valueOf(moneyNoEdit.getText().toString().trim()) >= Double
							.valueOf(10.00)) {
						if (Float.valueOf(moneyNoEdit.getText().toString()
								.trim()) <= Float.parseFloat(can_money)) {
							// 检测是否有密码
							if (!MyApplication.getInstance().getUserInfo()
									.getOBJECT().getUserPayPassword()
									.equals("")) {
								DialogPrice();
							} else {
								Intent intent = new Intent();
								intent.setClass(WithdrawcashActivity.this,
										SetPayPwdActivity.class);
								intent.putExtra("from", "WithdrawcashActivity");
								startActivity(intent);
							}
						} else if (Float.valueOf(moneyNoEdit.getText()
								.toString().trim()) == 0.00) {
							CustomDialog.Builder builder = new CustomDialog.Builder(
									WithdrawcashActivity.this);
							builder.setMessage("当前无可提现金额");
							builder.setTitle("提示");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});

							builder.create().show();
						} else {
							CustomDialog.Builder builder = new CustomDialog.Builder(
									WithdrawcashActivity.this);
							builder.setMessage("提现金额必须小于可提现金额");
							builder.setTitle("提示");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});

							builder.create().show();
						}
					} else {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								WithdrawcashActivity.this);
						builder.setMessage("您的提现金额不能少于10元!");
						builder.setTitle("提示");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.create().show();
					}
				}
			}
			}
		});
		cashWayRel.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		// 调用接口
		sendRequest();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		back.setClickable(true);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.right_view_withdrawcash_back:
			finish();
			back.setClickable(false);
			break;
		default:
			break;
		}
	}

	// 显示弹出框
	private void DialogPrice() {

		LayoutInflater inflater = LayoutInflater
				.from(WithdrawcashActivity.this);
		View view = inflater.inflate(R.layout.verif_pwd_dialog, null);
		pwdEdit = (GridPasswordView) view
				.findViewById(R.id.verif_pwd_dialog_pwd);
		pwdEdit.setOnPasswordChangedListener(WithdrawcashActivity.this);
		bulider = new AlertDialog.Builder(WithdrawcashActivity.this)
				.setView(view);
		bulider.create();
		dialog = bulider.show();
	}

	@Override
	public void onTextChanged(String psw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInputFinish(final String password) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("withdrawMoney", moneyNoEdit.getText().toString().trim());
		params.add("withDrawBankId", bankId);
		params.add("userPayPassword", Md5Util.MD5(password));

		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.USER_WITH_DRAW, params, true,
				new APPResponseHandler<DrawalCashEntity>(
						DrawalCashEntity.class, this) {
					@Override
					public void onSuccess(DrawalCashEntity result) {
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.BankcardActivity");
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.SetPayPwdActivity");
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							onInputFinish(password);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							pwdEdit.clearPassword();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										WithdrawcashActivity.this)
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
							} else {
								// 弹出错误信息
								String reutnMsg = errorMsg;
								Message message = new Message();
								message.what = 2;
								Bundle bundle = new Bundle();
								bundle.putString("msg", reutnMsg);
								message.setData(bundle);
								handler.sendMessage(message);
							}
						}
					}

					@Override
					public void onFinish() {

					}
				});
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_PURSE, params,
				new APPResponseHandler<UserPurseEntity>(UserPurseEntity.class,
						this) {
					@Override
					public void onSuccess(UserPurseEntity result) {
						if (result != null) {
							with_Draw_Money.setText(result.getOBJECT()
									.getWithDrawMoney() + "元");
							moneyNoEdit.setHint("输入金额请小于"
									+ result.getOBJECT().getWithDrawMoney()
									+ "元");
							can_money = result.getOBJECT().getWithDrawMoney();
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										WithdrawcashActivity.this)
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
						}
					}

					@Override
					public void onFinish() {

					}
				});
	}

	public void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				priceNum = s.toString();
				if (null != priceNum && !priceNum.equals("")) {
					withcash_button
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					withcash_button.setEnabled(true);
				} else {
					withcash_button
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					withcash_button.setEnabled(false);
				}
			}

		});

	}

}
