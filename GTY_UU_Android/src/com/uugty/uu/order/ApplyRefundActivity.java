package com.uugty.uu.order;

import java.util.Map;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.AgreementWebActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ApplyRefundActivity extends BaseActivity implements
		OnClickListener {
	private Button okbtn;
	private EditText mReturnRes;//退款原因
	private EditText mReturnMon;//退款金额
//	private int count1 = 1, count2 = 1, count3 = 1;
	private String orderId;
	private String price;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.order_apply_refund;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			orderId = getIntent().getStringExtra("orderId");
			price = getIntent().getStringExtra("price");
		}
//		title = (TextView) findViewById(R.id.order_apply_refund_title);// 退订手册
		okbtn = (Button) findViewById(R.id.order_apply_refund_commit);
		mReturnRes = (EditText) findViewById(R.id.return_resone);
		mReturnMon = (EditText) findViewById(R.id.return_money);
		mReturnMon.setText(price);

	}

	@Override
	protected void initAction() {
		okbtn.setOnClickListener(this);
		setPricePoint(mReturnMon);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
//		case R.id.order_apply_refund_title:
//			intent.putExtra("agreement", "applyForRefund");
//			intent.setClass(this, AgreementWebActivity.class);
//			startActivity(intent);
//			break;
		case R.id.order_apply_refund_commit:
			if (!TextUtils.isEmpty(mReturnRes.getText())) {
				if (!TextUtils.isEmpty(mReturnMon.getText().toString().trim())) {
					if (Double.valueOf(mReturnMon.getText().toString().trim()) >= Double
							.valueOf(0.01)) {

						if (Double
								.valueOf(mReturnMon.getText().toString().trim()) <= Double
								.valueOf(price)) {
							sendRequest();
						} else {
							CustomDialog.Builder builder = new CustomDialog.Builder(
									this);
							builder.setMessage("退款金额不能大于订单金额");
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
								this);
						builder.setMessage("退款金额不能为0");
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

				} else {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("请输入退款金额");
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
			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("请选择退款原因");
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
			break;

		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("orderStatus", "order_drawback");
		params.add("orderDrawbackReason", mReturnRes.getText().toString());
		params.add("orderDrawbackMoney", mReturnMon.getText().toString().trim());

		APPRestClient.post(this, ServiceCode.ORDER_DRAWBACK, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						setResult(RESULT_OK);
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ApplyRefundActivity.this)
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

	// 将原因拼接
//	public String addString() {
//		String str = "";
//		String str1 = "";
//		String str2 = "";
//		String str3 = "";
//		if (count2 % 2 == 0)
//			str2 = "1";
//		if (count1 % 2 == 0)
//			str1 = "2";
//		if (count3 % 2 == 0)
//			str3 = "3";
//
//		str = str2.equals("") ? "" : str2 + ",";
//		str += str1.equals("") ? "" : str1 + ",";
//		str += str3.equals("") ? "" : str3;
//
//		return str;
//	}

	public String MaptoString(Map<String, String> map) {
		String resultString = "";
		for (int i = 0; i < map.size(); i++) {
			if (map.get(i + "") != null) {
				resultString += map.get(i + "") + ",";
			}
		}
		return resultString;
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
			}

		});

	}
}
