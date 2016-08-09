package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BankCardEntity;
import com.uugty.uu.entity.BoundBankEntity;
import com.uugty.uu.entity.BoundBankEntity.BankCardInfo;

import java.util.List;

public class ADDBankActivity extends BaseActivity implements OnClickListener {

//	private SimpleDraweeView defaultImage;
	private EditText nameEditText, cardNumEditText;
	private Button commitBtn;
	private String bankType = "", fromType = "";
//	private LinearLayout defaultLin;
	private int count = 0;
	private String name = "", cardNo = "";
	private LinearLayout right_back_add;
	// 银行卡卡号列表
	private List<BankCardInfo> arryList;

	protected int getContentLayout() {

		return R.layout.add_bank;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			bankType = getIntent().getStringExtra("bankType");
			fromType = getIntent().getStringExtra("type");
		}
		right_back_add = (LinearLayout) findViewById(R.id.tabar_back);
		nameEditText = (EditText) findViewById(R.id.add_bank_card_user_edit);
		cardNumEditText = (EditText) findViewById(R.id.add_bank_card_num_edit);
		commitBtn = (Button) findViewById(R.id.add_card_btn);
//		defaultLin = (LinearLayout) findViewById(R.id.add_bank_card_default_lin);
//		defaultImage = (SimpleDraweeView) findViewById(R.id.add_bank_card_default);
		commitBtn.setEnabled(false);
		bankCardNumAddSpace(cardNumEditText);

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		right_back_add.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
//		defaultLin.setOnClickListener(this);
		nameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

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

		cardNumEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				cardNo = s.toString();
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
		// TODO Auto-generated method stub
		selectCardNo();
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
		case R.id.add_card_btn:
			if (cardNumEditText.getText().toString().trim().length() < 19) {
				CustomToast.makeText(ctx, 0, "卡号不能少于16位", 300).show();
			} else if (null != arryList && arryList.size() > 0) {
				// 对比银行卡号，若相同则提示
				if (!completeCardNo(cardNumEditText.getText().toString().trim())) {
					sendRequest();
				} else {
					CustomToast.makeText(ctx, 0, "该卡号已经绑定过", 300).show();
				}
			} else {
				sendRequest();
			}
			break;
//		case R.id.add_bank_card_default_lin:
//			count++;
//			if (count % 2 > 0) {
//				defaultImage.setImageURI(Uri.parse("res///"+R.drawable.route_tianxia));
//			} else {
//				defaultImage.setImageURI(Uri.parse("res///"+R.drawable.route_tianxia1));
//			}
//
//			break;

		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("bankCard", cardNumEditText.getText().toString().trim());// 银行卡号
		params.add("bankCardType", bankType);// 提现银行卡号的所属银行
		if (count % 2 > 0) {
			params.add("bankIsDefault", "0");// 是否是默认提现银行卡号 1 是 ，0 否
		} else {
			params.add("bankIsDefault", "1");// 是否是默认提现银行卡号 1 是 ，0 否
		}
		params.add("bankOwner", nameEditText.getText().toString().trim());// 是否是默认提现银行卡号
																			// 1
																			// 是
																			// ，0
																			// 否
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.USER_BOUND_BANK_CARD, params, true,
				new APPResponseHandler<BankCardEntity>(BankCardEntity.class,
						this) {
					@Override
					public void onSuccess(BankCardEntity result) {
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.ChooseBankActivity");
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						if (null != fromType && fromType.equals("cash")) {
							intent.putExtra("cardId", result.getOBJECT()
									.getBankId());
							intent.putExtra("cardNo", cardNumEditText.getText()
									.toString().trim());
							intent.putExtra("bankType",
									chooseBankName(Integer.valueOf(bankType)));
							intent.setClass(ADDBankActivity.this,
									WithdrawcashActivity.class);
							startActivity(intent);
						}else{

							finish();
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ADDBankActivity.this)
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

	private void selectCardNo() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_BOUND_BANK_CARDLIST, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,
						this) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						if (result.getLIST().size() > 0) {
							arryList = result.getLIST();
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							selectCardNo();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ADDBankActivity.this)
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

	public String chooseBankName(int num) {
		String name = "";
		switch (num) {
		case 1:
			name = "中国银行";
			break;
		case 2:
			name = "中国农业银行";
			break;
		case 3:
			name = "中国工商银行";
			break;
		case 4:
			name = "中国建设银行";
			break;
		case 5:
			name = "中国交通银行";
			break;
		case 6:
			name = "中国招商银行";
			break;
		case 7:
			name = "中国广大银行";
			break;
		default:
			break;
		}
		return name;
	}

	public boolean completeCardNo(String cardNo) {
		for (int i = 0; i < arryList.size(); i++) {
			if (cardNo.equals(arryList.get(i).getBankCard().trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 银行卡四位加空格
	 * 
	 * @param mEditText
	 */
	protected void bankCardNumAddSpace(final EditText mEditText) {
		mEditText.addTextChangedListener(new TextWatcher() {
			int beforeTextLength = 0;
			int onTextLength = 0;
			boolean isChanged = false;

			int location = 0;// 记录光标的位置
			private char[] tempChar;
			private StringBuffer buffer = new StringBuffer();
			int konggeNumberB = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeTextLength = s.length();
				if (buffer.length() > 0) {
					buffer.delete(0, buffer.length());
				}
				konggeNumberB = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						konggeNumberB++;
					}
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onTextLength = s.length();
				buffer.append(s.toString());
				if (onTextLength == beforeTextLength || onTextLength <= 3
						|| isChanged) {
					isChanged = false;
					return;
				}
				isChanged = true;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isChanged) {
					location = mEditText.getSelectionEnd();
					int index = 0;
					while (index < buffer.length()) {
						if (buffer.charAt(index) == ' ') {
							buffer.deleteCharAt(index);
						} else {
							index++;
						}
					}

					index = 0;
					int konggeNumberC = 0;
					while (index < buffer.length()) {
						if ((index == 4 || index == 9 || index == 14 || index == 19)) {
							buffer.insert(index, ' ');
							konggeNumberC++;
						}
						index++;
					}

					if (konggeNumberC > konggeNumberB) {
						location += (konggeNumberC - konggeNumberB);
					}

					tempChar = new char[buffer.length()];
					buffer.getChars(0, buffer.length(), tempChar, 0);
					String str = buffer.toString();
					if (location > str.length()) {
						location = str.length();
					} else if (location < 0) {
						location = 0;
					}

					mEditText.setText(str);
					Editable etable = mEditText.getText();
					Selection.setSelection(etable, location);
					isChanged = false;
				}
			}
		});
	}
}
