package com.uugty.uu.viewpage.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.R.drawable;
import com.uugty.uu.com.rightview.ADDBankActivity;
import com.uugty.uu.com.rightview.alipaywallet.AddAliPayActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BoundBankEntity;
import com.uugty.uu.entity.BoundBankEntity.BankCardInfo;

import java.util.List;

public class BankCardAdapter extends BaseAdapter {

	private List<BankCardInfo> cardList;
	private Context context;
	private ViewHolder holder;
	private String fromType;

	public BankCardAdapter(List<BankCardInfo> ls, Context context,
			String fromType) {
		super();
		this.cardList = ls;
		this.context = context;
		this.fromType = fromType;
	}

	public void updateList(List<BankCardInfo> list) {
		this.cardList = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != cardList) {
			return cardList.size();
		} else {
			return 0;
		}
	}

	@Override
	public BankCardInfo getItem(int position) {
		// TODO Auto-generated method stub
		return cardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.bound_bank_card_item, null);
			holder.alipayLinear = (LinearLayout) convertView.findViewById(R.id.wallet_alipay_linear);
			holder.bankCardLinear = (LinearLayout) convertView.findViewById(R.id.wallet_bankcard_linear);
			holder.setDefaultLinear = (LinearLayout) convertView.findViewById(R.id.wallet_select_default);
			holder.deleteLinear = (LinearLayout) convertView.findViewById(R.id.wallet_account_delete);
			holder.bankCardName = (TextView) convertView.findViewById(R.id.wallet_bank_name);
			holder.bankCardNumber = (TextView) convertView.findViewById(R.id.wallet_bank_account);
			holder.bankCardType = (TextView) convertView.findViewById(R.id.wallet_bankcard_type);
			holder.alipayName = (TextView) convertView.findViewById(R.id.wallet_alipay_name);
			holder.alipayNumber = (TextView) convertView.findViewById(R.id.wallet_alipay_account);
			holder.isDefaultImg = (ImageView) convertView.findViewById(R.id.wallet_select_imageview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if("8".equals(cardList.get(position).getBankCardType())){
			holder.alipayLinear.setVisibility(View.VISIBLE);
			holder.bankCardLinear.setVisibility(View.GONE);
			holder.alipayNumber.setText(cardList.get(position).getBankCard());
			holder.alipayName.setText(cardList.get(position).getBankOwner());
			holder.alipayLinear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra("bankId",cardList.get(position).getBankId());
					i.putExtra("number",cardList.get(position).getBankCard());
					i.putExtra("name",cardList.get(position).getBankOwner());
					i.setClass(context, AddAliPayActivity.class);
					context.startActivity(i);
				}
			});
		}else{
			holder.alipayLinear.setVisibility(View.GONE);
			holder.bankCardLinear.setVisibility(View.VISIBLE);
			holder.bankCardNumber.setText(cardList.get(position).getBankCard());
			holder.bankCardName.setText(cardList.get(position).getBankOwner());
			holder.bankCardType.setText(chooseBankName(Integer.valueOf(cardList.get(position).getBankCardType())));
			holder.bankCardLinear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra("bankId",cardList.get(position).getBankId());
					i.putExtra("number",cardList.get(position).getBankCard());
					i.putExtra("name",cardList.get(position).getBankOwner());
					i.putExtra("bankType",cardList.get(position).getBankCardType());
					i.setClass(context, ADDBankActivity.class);
					context.startActivity(i);
				}
			});
		}

		if(cardList.get(position).getBankIsDefault().equals("1")){
			holder.isDefaultImg.setBackgroundDrawable(context.getResources().getDrawable(drawable.pay_click));
		}else{
			holder.isDefaultImg.setBackgroundDrawable(context.getResources().getDrawable(drawable.pay_noclick));
		}
		holder.setDefaultLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setDefaultRequest(position,cardList.get(position).getBankId());
			}
		});
		holder.deleteLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDialog.Builder builder = new CustomDialog.Builder(context);
				builder.setMessage("确定删除银行卡");
				builder.setTitle("删除银行卡");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 删除银行卡
						sendDelCardRequest(position,cardList.get(position).getBankId());
					}
				});

				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			}
		});
		return convertView;
	}

	//设置默认
	private void setDefaultRequest(final int position, final String bankId) {
		RequestParams params = new RequestParams();
		params.add("bankId", bankId);
		APPRestClient.post(context, ServiceCode.SET_DEFAULT_ACCOUNT, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,context) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						if(result.getLIST().size() > 0) {
							holder.isDefaultImg.setBackgroundDrawable(context.getResources().getDrawable(drawable.pay_click));
							updateList(result.getLIST());
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendDelCardRequest(position,bankId);
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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

	//删除银行卡
	private void sendDelCardRequest(final int position, final String bankId) {
		RequestParams params = new RequestParams();
		params.add("bankId", bankId);
		APPRestClient.post(context, ServiceCode.DEL_BANK_CARD, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,context) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						cardList.remove(position);
						updateList(cardList);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendDelCardRequest(position,bankId);
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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

	private static class ViewHolder {
		LinearLayout alipayLinear,bankCardLinear,setDefaultLinear,deleteLinear;
		TextView bankCardNumber,bankCardName,bankCardType,alipayNumber,alipayName;
		ImageView isDefaultImg;
	}

	public int chooseImage(int num) {
		Class<drawable> cls = R.drawable.class;
		int imageRes = 0;
		try {
			switch (num) {
			case 1:
				imageRes = cls.getDeclaredField("bank_china").getInt(null);
				break;
			case 2:
				imageRes = cls.getDeclaredField("bank_abc").getInt(null);
				break;
			case 3:
				imageRes = cls.getDeclaredField("bank_icbc").getInt(null);
				break;
			case 4:
				imageRes = cls.getDeclaredField("bank_ccb").getInt(null);
				break;
			case 5:
				imageRes = cls.getDeclaredField("bank_bcm").getInt(null);
				break;
			case 6:
				imageRes = cls.getDeclaredField("bank_cmb").getInt(null);
				break;
			case 7:
				imageRes = cls.getDeclaredField("bank_ceb").getInt(null);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageRes;
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
			name = "中国光大银行";
			break;
			case 8:
				name = "支付宝";
				break;
		default:
			break;
		}
		return name;
	}
}
