package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.PriceDetailActivity;
import com.uugty.uu.com.rightview.RightForgetActivity;
import com.uugty.uu.com.rightview.SetPayPwdActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.BalanDialog;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.MD5;
import com.uugty.uu.common.util.SignUtils;
import com.uugty.uu.entity.AlipayEntity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.PayResult;
import com.uugty.uu.entity.TipPacketEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.WXPayEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.password.view.GridPasswordView;
import com.uugty.uu.password.view.GridPasswordView.OnPasswordChangedListener;
import com.uugty.uu.util.Md5Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UUChatPaypriceActivity extends BaseActivity implements
		OnClickListener,OnPasswordChangedListener {

	private ImageView pay_monery_back;
	private TextView layout_one, layout_two, layout_three;// 我的钱包 微信支付
	private SimpleDraweeView img1, img2;//
	private String intent_message;// 获得传值金额
	AlertDialog.Builder bulider;
	AlertDialog dialog;//
	private static String dialogMsg = "", intent_orderId, intent_content,
			intent_title, intent_no;
	private static String toReceive_avatar, toReceive_username,
			toReceive_userId;
	private RelativeLayout walletRel, weChatRel, alipayRel;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	private static final String APP_ID = "wx7f1866a885330eb2";
	private PayReq request;
	private String red_id;
	private String str;
	private SpotsDialog loadingDialog;
	private GridPasswordView pwdEdit;
	// 商户PID
	public static final String PARTNER = "2088801318643909";
	// 商户收款账号
	public static final String SELLER = "2316986924@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOcBJTtH7ux3e4QDXECgUv2Qb+hmyaRmv8z1XRRzwRCSO42+AfLHcbCFNL1/sO3TE/ADnPbTBMW+m13c7mA/VF8LcN/8SZkp8P8g0Bw0CVUjh/qEdY6PTK/CnACY2rsJBxtr2cvRaMhv4LdYNrkp8c3AOxn8Ofn1H1oX47+0cdt5AgMBAAECgYEA0j1qoz9epfxAf++HAJJptbjFAOC72FJjcahGJJ0NM4XDicdKgqkeQaeaTnVuk6St2p2PN9zp1Qca5Bx1H0fEAllzfyG4e1+1DPIUirA/1UuMHwrqIia3EHyVIQFJt/xUWlZzcNVxCLQrgqYYvmQjLIPLP/0trKw1ujiCUSQrULkCQQD/tseLtC3AXtuSBfqF5c57bv9FDptsR/8dsnQEhmHZRhs46U4dC2U+1MQnpgwvkOcKJeVh0kXNnh9+fBMoWzAjAkEA50NKa0fKOu6prXZbcm9ZqX2eEp1OuWVU9dFlRYaEKpOJg3DgR3gQ24PWwK8lj97RGepfUuKD7hW4p2lmoO2xswJAD7ZwfoIcyZRKk6dwZGfhjy4b22dxX27xGycK//gt7Qbkx1N2rEw19W1nfDQ0zXtu5u27MY6VIXRU5RXEq5cm1wJAYJlkCYHYQFuWtqU8t4U5j6mwEJhy3NAt9+w6gBsbM+mixCuvE0tcx0S/vIasivcIoumaXbXOY/HgytUlEE2ZLwJBAJ4xqGXGq4YnhUtpmACryFUhjeetUFrPN3dQ9fC+XMr37mSsAHnWm8SAlJXYAtF8K8Z8zmndvKZb6V/eGBie8Ck=";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	//
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 1:
				// 这里传小费跟留言
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.UUTipActivity");
				intent.putExtra("price", intent_content);
				intent.putExtra("message", intent_message);
				intent.putExtra("red_id", red_id);
				intent.putExtra("avatar", toReceive_avatar);
				intent.putExtra("userName", toReceive_username);
				intent.putExtra("userId", toReceive_userId);
				intent.putExtra("toFrom", "UUChatOKActivity");
				intent.setClass(UUChatPaypriceActivity.this, ChatActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				dialog.dismiss();
				break;
			case 6:
				// 这里传小费跟留言
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatCommonActivity");
				intent.putExtra("price", intent_content);
				intent.putExtra("message", intent_message);
				intent.putExtra("red_id", red_id);
				intent.putExtra("avatar", toReceive_avatar);
				intent.putExtra("userName", toReceive_username);
				intent.putExtra("userId", toReceive_userId);
				intent.putExtra("toFrom", "UUChatOKActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(UUChatPaypriceActivity.this, ChatActivity.class);
				startActivity(intent);
				dialog.dismiss();
				break;
			case 8:
				msgApi.registerApp(APP_ID);
				msgApi.sendReq(request);
				Util.paySuccessPage = "uutip";
				break;
			case 9:
				msgApi.registerApp(APP_ID);
				msgApi.sendReq(request);

				Util.paySuccessPage = "uucom";
				break;
			case 2:
				Bundle bundle = msg.getData();
				String bundleText = bundle.getString("msg");
				if (bundleText.equals("你账户的余额不足！")) {
					BalanDialog.Builder balanDialog = new BalanDialog.Builder(
							UUChatPaypriceActivity.this);
					balanDialog
							.setPositiveButton(new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
								}
							});
					balanDialog.create().show();
				}
				CustomToast.makeText(UUChatPaypriceActivity.this, 0,
						bundleText, 200).show();

				break;
			case 3:
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.order.UUOrederPayActivity");
				Bundle resultbundle = msg.getData();
				String price = resultbundle.getString("price");
				String payType = resultbundle.getString("payType");
				intent.putExtra("price", price);
				intent.putExtra("payType", payType);
				intent.setClass(UUChatPaypriceActivity.this,
						PriceDetailActivity.class);
				startActivity(intent);
				dialog.dismiss();
				break;
			case 4:
				msgApi.registerApp(APP_ID);
				msgApi.sendReq(request);
				Util.paySuccessPage = "uuCaht";
				break;
			case 5:
				CustomToast.makeText(UUChatPaypriceActivity.this, 0,
						"获取支付ID失败", 200).show();

				break;
			case 7:
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					if (Util.pageFlag != null
							&& Util.pageFlag.equals("UUPayActivity")) {
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.order.UUOrederPayActivity");
						intent.putExtra("price", intent_content);
						intent.putExtra("payType", "3");
						intent.setClass(UUChatPaypriceActivity.this,
								PriceDetailActivity.class);
						startActivity(intent);
					} else {
						if (Util.pageFlag != null
								&& Util.pageFlag.equals("UUTipActivity")) {
							ActivityCollector
									.removeSpecifiedActivity("com.uugty.uu.uuchat.UUTipActivity");
						} else if (Util.pageFlag != null
								&& Util.pageFlag.equals("UUChatCommonActivity")) {
							ActivityCollector
									.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatCommonActivity");
						}
						intent.putExtra("price", intent_content);
						intent.putExtra("message", intent_message);
						intent.putExtra("red_id", red_id);
						intent.putExtra("avatar", toReceive_avatar);
						intent.putExtra("userName", toReceive_username);
						intent.putExtra("userId", toReceive_userId);
						intent.putExtra("toFrom", "UUChatOKActivity");
						intent.setClass(UUChatPaypriceActivity.this,
								ChatActivity.class);
						startActivity(intent);
					}

					CustomToast.makeText(UUChatPaypriceActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();

				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CustomToast.makeText(UUChatPaypriceActivity.this,
								"支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						CustomToast.makeText(UUChatPaypriceActivity.this,
								"支付失败", Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			super.handleMessage(msg);
		}

	};

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_chatpay_monery;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		pay_monery_back = (ImageView) findViewById(R.id.right_myprice_back);
		layout_one = (TextView) findViewById(R.id.me_monery);
		layout_two = (TextView) findViewById(R.id.wechat_monery);
		layout_three = (TextView) findViewById(R.id.alipay_monery);
		walletRel = (RelativeLayout) findViewById(R.id.pay_one);
		weChatRel = (RelativeLayout) findViewById(R.id.pay_two);
		alipayRel = (RelativeLayout) findViewById(R.id.pay_three);
		img1 = (SimpleDraweeView) findViewById(R.id.my_price_image);
		img2 = (SimpleDraweeView) findViewById(R.id.wechat_price_image);

		if (getIntent() != null) {
			Util.pageFlag = getIntent().getStringExtra("pageFlag");
			if (Util.pageFlag != null && Util.pageFlag.equals("UUTipActivity")) {
				dialogMsg = "你确定要取消本次支付吗?";
				intent_content = getIntent().getStringExtra("price");
				intent_message = getIntent().getStringExtra("message");
				toReceive_avatar = getIntent().getStringExtra("avatar");
				toReceive_username = getIntent().getStringExtra("userName");
				toReceive_userId = getIntent().getStringExtra("chat_id");
				Util.toReceive_avatar = toReceive_avatar;
				Util.toReceive_userName = toReceive_username;
				Util.toReceive_userId = toReceive_userId;

			}
			if (Util.pageFlag != null
					&& Util.pageFlag.equals("UUChatCommonActivity")) {
				dialogMsg = "你确定要取消本次支付吗?";
				intent_content = getIntent().getStringExtra("price");
				intent_message = getIntent().getStringExtra("message");
				toReceive_avatar = getIntent().getStringExtra("avatar");
				toReceive_username = getIntent().getStringExtra("userName");
				toReceive_userId = getIntent().getStringExtra("chat_id");
				Util.toReceive_avatar = toReceive_avatar;
				Util.toReceive_userName = toReceive_username;
				Util.toReceive_userId = toReceive_userId;
			}
			if (Util.pageFlag != null && Util.pageFlag.equals("UUPayActivity")) {
				dialogMsg = "若取消本次支付，可在我的订单里继续完成支付。";
				intent_content = getIntent().getStringExtra("price");
				intent_orderId = getIntent().getStringExtra("orderId");
				intent_title = getIntent().getStringExtra("orderName");
				intent_no = getIntent().getStringExtra("orderNo");
			}

		}
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub

		pay_monery_back.setOnClickListener(this);
		weChatRel.setOnClickListener(this);
		walletRel.setOnClickListener(this);
		alipayRel.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		pay_monery_back.setClickable(true);
		if (loadingDialog != null)
			loadingDialog.dismiss();
		layout_two.setTextColor(getResources()
				.getColor(R.color.base_text_color));
		layout_three.setTextColor(getResources().getColor(
				R.color.base_text_color));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.right_myprice_back:
			showCustomDialog();
			break;
		case R.id.pay_one:
			Intent intent = new Intent();
			if(null!=MyApplication.getInstance().getUserInfo()){
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserTelValidate().equals("0")) {
					intent.setClass(this, RightForgetActivity.class);
					startActivity(intent);
				} else {
					// 手机号已验证
					if (MyApplication.getInstance().getUserInfo().getOBJECT()
							.getUserPayPassword().equals("")
							&& null != MyApplication.getInstance().getUserInfo()
									.getOBJECT().getUserPayPassword()) {
						intent.putExtra("from", Util.pageFlag);
						intent.setClass(this, SetPayPwdActivity.class);
						startActivity(intent);
					} else {
						Util.titleName = "我的钱包";
						DialogPrice();
						select();
					}
				}	
			}else{
				intent.putExtra("topage",
						UUChatPaypriceActivity.class.getName());
				intent.setClass(UUChatPaypriceActivity.this, LoginActivity.class);
				startActivity(intent);			
		}
			break;
		case R.id.pay_two:
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
			}
			Util.titleName = "微信支付";
			getPrepayId();
			select();
			break;
		case R.id.pay_three:
			Util.titleName = "支付宝支付";
			if (Util.pageFlag != null
					&& Util.pageFlag.equals("UUChatCommonActivity")) {
				// 调用接口
				getRechargeId();
			} else if (Util.pageFlag != null
					&& Util.pageFlag.equals("UUTipActivity")) {
				getRechargeId();
			} else if (Util.pageFlag != null
					&& Util.pageFlag.equals("UUPayActivity")) {
				
				sendOrderTourAlipayment();
			}
			select();

			break;
		default:
			break;
		}
	}

	private void getRechargeId() {
		// TODO Auto-generated method stub
		str = getIntent().getStringExtra("chat_id");
		RequestParams params = new RequestParams();
		params.add("gratuityReceiverUserId", str);
		params.add("gratuityReceiverGroupId", "");
		params.add("gratuityCount", "1");
		params.add("gratuityEveryMoney", intent_content);
		params.add("gratuityMark", intent_message);
		if (Util.pageFlag != null && Util.pageFlag.equals("UUTipActivity")) {
			params.add("gratuityType", "2");
		} else if (Util.pageFlag != null
				&& Util.pageFlag.equals("UUChatCommonActivity")) {
			params.add("gratuityType", "1");
		}

		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.ALIPAY_GRATUITY_RESERVATIONS, params, true,
				new APPResponseHandler<AlipayEntity>(AlipayEntity.class, this) {
					@Override
					public void onSuccess(AlipayEntity result) {
						if (!TextUtils.isEmpty(result.getOBJECT()
								.getOutTradeNo())) {
							intent_no = result.getOBJECT().getOutTradeNo();
							red_id = result.getOBJECT().getGratutiyId();
							if (Util.pageFlag != null
									&& Util.pageFlag.equals("UUTipActivity")) {
								pay("随机红包", intent_content);
							} else {
								pay("普通红包", intent_content);
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getRechargeId();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(UUChatPaypriceActivity.this)
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
						} else {
							Message message = new Message();
							message.what = 5;
							handler.sendMessage(message);
						}
					}}

					@Override
					public void onFinish() {

					}
				});
	}

	private void sendOrderTourAlipayment() {
		RequestParams params = new RequestParams();
		params.add("orderId", intent_orderId);
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.ALIPAY_TOUR_PAYMENT, params, true,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						pay(intent_title, intent_content);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendOrderTourAlipayment();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
					}}

					@Override
					public void onFinish() {

					}
				});
	}

	private void showCustomDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(
				UUChatPaypriceActivity.this);
		builder.setMessage(dialogMsg);
		builder.setTitle("确认");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
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

	// 点击文字改变后面的图片框
	private void select() {
		layout_one.setTextColor(getResources()
				.getColor(R.color.base_text_color));
		layout_two.setTextColor(getResources()
				.getColor(R.color.base_text_color));
		// 选中后
		if (Util.titleName.equals("我的钱包")) {
			layout_one.setTextColor(layout_one.getResources().getColor(
					R.color.select_color));
			img1.setImageURI(Uri.parse("res///"+R.drawable.route_ok));
		} else if (Util.titleName.equals("微信支付")) {
			layout_two.setTextColor(layout_two.getResources().getColor(
					R.color.select_color));
			img2.setImageURI(Uri.parse("res///"+R.drawable.route_ok));
		} else if (Util.titleName.equals("支付宝支付")) {
			layout_three.setTextColor(layout_two.getResources().getColor(
					R.color.select_color));
		}
	}

	// 显示弹出框
	private void DialogPrice() {
		LayoutInflater inflater = LayoutInflater
				.from(UUChatPaypriceActivity.this);
		View view = inflater.inflate(R.layout.pwd_dialog, null);
		TextView txt_content = (TextView) view
				.findViewById(R.id.price_content_pwd);
		txt_content.setText(intent_content);
		pwdEdit = (GridPasswordView) view
				.findViewById(R.id.price_pay_password);
		pwdEdit.setOnPasswordChangedListener(UUChatPaypriceActivity.this);
		bulider = new AlertDialog.Builder(UUChatPaypriceActivity.this)
				.setView(view);
		bulider.create();
		dialog = bulider.show();
		layout_one.setTextColor(getResources()
				.getColor(R.color.base_text_color));
	}

	@Override
	public void onTextChanged(String psw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInputFinish(final String password) {
		// TODO Auto-generated method stub


		if (Util.pageFlag != null && Util.pageFlag.equals("UUTipActivity")) {
			str = getIntent().getStringExtra("chat_id");

			RequestParams params = new RequestParams();
			params.add("gratuityReceiverUserId", str);
			params.add("gratuityReceiverGroupId", "");
			params.add("gratuityCount", "1");
			params.add("gratuityEveryMoney", intent_content);
			params.add("gratuityMark", intent_message);
			params.add("gratuityType", "2");
			params.add("UserPayPassword", Md5Util.MD5(password));
			APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
					+ ServiceCode.ORDER_PURSE_PAYMENT, params, true,
					new APPResponseHandler<TipPacketEntity>(
							TipPacketEntity.class, this) {
						@Override
						public void onSuccess(TipPacketEntity result) {
							red_id = result.getOBJECT().getGratuityId();
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
										UUChatPaypriceActivity.this)
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

		if (Util.pageFlag != null
				&& Util.pageFlag.equals("UUChatCommonActivity")) {
			str = getIntent().getStringExtra("chat_id");
			RequestParams params = new RequestParams();
			params.add("gratuityReceiverUserId", str);
			params.add("gratuityReceiverGroupId", "");
			params.add("gratuityCount", "1");
			params.add("gratuityEveryMoney", intent_content);
			params.add("gratuityMark", intent_message);
			params.add("gratuityType", "1");
			params.add("UserPayPassword", Md5Util.MD5(password));
			APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
					+ ServiceCode.ORDER_PURSE_PAYMENT, params, true,
					new APPResponseHandler<TipPacketEntity>(
							TipPacketEntity.class, this) {
						@Override
						public void onSuccess(TipPacketEntity result) {
							red_id = result.getOBJECT().getGratuityId();
							Message message = new Message();
							message.what = 6;
							handler.sendMessage(message);
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {
								onInputFinish(password);
							} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										UUChatPaypriceActivity.this)
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
						}}

						@Override
						public void onFinish() {

						}
					});
		}
		if (Util.pageFlag != null && Util.pageFlag.equals("UUPayActivity")) {
			str = getIntent().getStringExtra("chat_id");
			RequestParams params = new RequestParams();
			params.add("orderId", intent_orderId);
			params.add("UserPayPassword", Md5Util.MD5(password));

			APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
					+ ServiceCode.ORDER_PAYMENT, params, true,
					new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
						@Override
						public void onSuccess(BaseEntity result) {
							Message message = new Message();
							message.what = 3;
							Bundle bundle = new Bundle();
							bundle.putString("price", intent_content);
							bundle.putString("payType", "1");// 钱包支付
							message.setData(bundle);
							handler.sendMessage(message);
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							if (errorCode == 3) {			
								onInputFinish(password);
							} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										UUChatPaypriceActivity.this)
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
						}}

						@Override
						public void onFinish() {

						}
					});
		}

	
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Util.pageFlag = getIntent().getStringExtra("pageFlag");
	}

	/**
	 * 获取prepayid
	 */
	public void getPrepayId() {
		if (Util.pageFlag != null && Util.pageFlag.equals("UUPayActivity")) {
			RequestParams params = new RequestParams();
			params.add("orderId", intent_orderId);
			APPRestClient
					.post(this, APPRestClient.HTTPS_BASE_URL
							+ ServiceCode.WX_ORDER_TOUR_PAYMENT, params, true,
							new APPResponseHandler<WXPayEntity>(
									WXPayEntity.class, this) {
								@Override
								public void onSuccess(WXPayEntity result) {
									Util.rechargeAmout = intent_content;
									Util.tradeNo = result.getOBJECT()
											.getOut_trade_no();
									request = new PayReq();
									genPayReq(result.getOBJECT().getPrepay_id());
									Message message = new Message();
									message.what = 4;
									handler.sendMessage(message);
								}

								@Override
								public void onFailure(int errorCode,
										String errorMsg) {
									new Handler().postDelayed(new Runnable() {
										public void run() {
											// 显示dialog
											loadingDialog.dismiss();
										}
									}, 500);
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									if (errorCode == 3) {
										getPrepayId();
									} else {
									if (errorCode == -999) {
										new AlertDialog.Builder(
												UUChatPaypriceActivity.this)
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
									} else {
										Message message = new Message();
										message.what = 5;
										handler.sendMessage(message);
									}
								}
								}
								@Override
								public void onFinish() {

								}
							});
		}
		if (Util.pageFlag != null
				&& Util.pageFlag.equals("UUChatCommonActivity")) {
			str = getIntent().getStringExtra("chat_id");
			RequestParams params = new RequestParams();
			params.add("gratuityReceiverUserId", str);
			params.add("gratuityCount", "1");
			params.add("gratuityEveryMoney", intent_content);
			params.add("gratuityMark", intent_message);
			params.add("gratuityType", "1");
			APPRestClient
					.post(this, APPRestClient.HTTPS_BASE_URL
							+ ServiceCode.WX_ORD_PRICE, params, true,
							new APPResponseHandler<WXPayEntity>(
									WXPayEntity.class, this) {
								@Override
								public void onSuccess(WXPayEntity result) {
									// TODO Auto-generated method stub
									red_id = result.getOBJECT().getGratutiyId();
									Util.rechargeAmout = intent_content;
									Util.red_id = result.getOBJECT()
											.getGratutiyId();
									Util.red_message = intent_message;
									Util.tradeNo = result.getOBJECT()
											.getOut_trade_no();
									request = new PayReq();
									genPayReq(result.getOBJECT().getPrepay_id());
									Message message = new Message();
									message.what = 8;
									handler.sendMessage(message);
								}

								@Override
								public void onFailure(int errorCode,
										String errorMsg) {
									
									// TODO Auto-generated method stub
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									if (errorCode == 3) {
										getPrepayId();
									} else {
									if (errorCode == -999) {
										new AlertDialog.Builder(
												UUChatPaypriceActivity.this)
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
								}}

							});

		}
		if (Util.pageFlag != null && Util.pageFlag.equals("UUTipActivity")) {
			str = getIntent().getStringExtra("chat_id");
			RequestParams params = new RequestParams();
			params.add("gratuityReceiverUserId", str);
			params.add("gratuityCount", "1");
			params.add("gratuityEveryMoney", intent_content);
			params.add("gratuityMark", intent_message);
			params.add("gratuityType", "2");
			APPRestClient
					.post(this, APPRestClient.HTTPS_BASE_URL
							+ ServiceCode.WX_ORD_PRICE, params, true,
							new APPResponseHandler<WXPayEntity>(
									WXPayEntity.class, this) {
								@Override
								public void onSuccess(WXPayEntity result) {
									// TODO Auto-generated method stub
									red_id = result.getOBJECT().getGratutiyId();
									Util.rechargeAmout = intent_content;
									Util.red_id = result.getOBJECT()
											.getGratutiyId();
									Util.red_message = intent_message;
									Util.tradeNo = result.getOBJECT()
											.getOut_trade_no();
									request = new PayReq();
									genPayReq(result.getOBJECT().getPrepay_id());
									Message message = new Message();
									message.what = 9;
									handler.sendMessage(message);

								}

								@Override
								public void onFailure(int errorCode,
										String errorMsg) {
									// TODO Auto-generated method stub
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									if (errorCode == 3) {
										getPrepayId();
									} else {
									if (errorCode == -999) {
										new AlertDialog.Builder(
												UUChatPaypriceActivity.this)
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
								}}

							});

		}
	}

	// 微信红包支付
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private String genTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	private void genPayReq(String prepay_id) {
		request.appId = APP_ID;
		request.partnerId = "1260208801";
		request.prepayId = prepay_id;
		request.packageValue = "Sign=WXPay";
		request.nonceStr = genNonceStr();
		request.timeStamp = String.valueOf(genTimeStamp());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", request.appId));
		signParams.add(new BasicNameValuePair("noncestr", request.nonceStr));
		signParams.add(new BasicNameValuePair("package", request.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", request.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", request.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", request.timeStamp));
		request.sign = genAppSign(signParams);

	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append("97c2b40f1032d7a0e631cdb563cdbc41");

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();

		return appSign;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			showCustomDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (null != dialog)
			dialog.dismiss();
		MyApplication.getInstance().setKilled(true);
	}

	public void pay(String itemName, String orderPrice) {
		// 订单
		String orderInfo = getOrderInfo(itemName, itemName, orderPrice);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(UUChatPaypriceActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = 7;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	public String getOrderInfo(String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + intent_no + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + APPRestClient.BASE_URL
				+ "alipay_notify_url.jsp" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"15d\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		String key = format.format(date);
		return key + intent_no;
	}

	
}
