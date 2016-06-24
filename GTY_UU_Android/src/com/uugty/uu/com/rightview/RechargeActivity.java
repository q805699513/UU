package com.uugty.uu.com.rightview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.MD5;
import com.uugty.uu.common.util.SignUtils;
import com.uugty.uu.entity.AlipayEntity;
import com.uugty.uu.entity.PayResult;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.WXPayEntity;


public class RechargeActivity extends BaseActivity implements OnClickListener {

	private LinearLayout back;
	private Button rechargeBtn;
	private EditText amountEditText;
	private String amount = "";
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	private static final String APP_ID = "wx7f1866a885330eb2";
	private PayReq request;
	private RelativeLayout alipayRel, weixinRel;
	private ImageView alipaySelectImage, weixinSelectImage;
	private SpotsDialog loadingDialog;
	// 支付方式
	private int rechargeType = 1;// 1-支付宝 2-微信
	// 商户PID
	public static final String PARTNER = "2088801318643909";
	// 商户收款账号
	public static final String SELLER = "2316986924@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOcBJTtH7ux3e4QDXECgUv2Qb+hmyaRmv8z1XRRzwRCSO42+AfLHcbCFNL1/sO3TE/ADnPbTBMW+m13c7mA/VF8LcN/8SZkp8P8g0Bw0CVUjh/qEdY6PTK/CnACY2rsJBxtr2cvRaMhv4LdYNrkp8c3AOxn8Ofn1H1oX47+0cdt5AgMBAAECgYEA0j1qoz9epfxAf++HAJJptbjFAOC72FJjcahGJJ0NM4XDicdKgqkeQaeaTnVuk6St2p2PN9zp1Qca5Bx1H0fEAllzfyG4e1+1DPIUirA/1UuMHwrqIia3EHyVIQFJt/xUWlZzcNVxCLQrgqYYvmQjLIPLP/0trKw1ujiCUSQrULkCQQD/tseLtC3AXtuSBfqF5c57bv9FDptsR/8dsnQEhmHZRhs46U4dC2U+1MQnpgwvkOcKJeVh0kXNnh9+fBMoWzAjAkEA50NKa0fKOu6prXZbcm9ZqX2eEp1OuWVU9dFlRYaEKpOJg3DgR3gQ24PWwK8lj97RGepfUuKD7hW4p2lmoO2xswJAD7ZwfoIcyZRKk6dwZGfhjy4b22dxX27xGycK//gt7Qbkx1N2rEw19W1nfDQ0zXtu5u27MY6VIXRU5RXEq5cm1wJAYJlkCYHYQFuWtqU8t4U5j6mwEJhy3NAt9+w6gBsbM+mixCuvE0tcx0S/vIasivcIoumaXbXOY/HgytUlEE2ZLwJBAJ4xqGXGq4YnhUtpmACryFUhjeetUFrPN3dQ9fC+XMr37mSsAHnWm8SAlJXYAtF8K8Z8zmndvKZb6V/eGBie8Ck=";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				msgApi.registerApp(APP_ID);
				msgApi.sendReq(request);
				Util.paySuccessPage = "recharge";
				break;
			case 2:
				CustomToast.makeText(RechargeActivity.this, 0, "获取支付ID失败", 200)
						.show();
				break;
			case 3:
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					CustomToast.makeText(RechargeActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CustomToast.makeText(RechargeActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						CustomToast.makeText(RechargeActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			super.handleMessage(msg);
		}

	};

	protected int getContentLayout() {
		// TODO Auto-generated method stub 充值
		return R.layout.right_view_rechgre;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		back = (LinearLayout) findViewById(R.id.tabar_back);
		rechargeBtn = (Button) findViewById(R.id.recharge_amount_button);
		amountEditText = (EditText) findViewById(R.id.recharge_amount_num);
		rechargeBtn.setEnabled(false);
		alipayRel = (RelativeLayout) findViewById(R.id.rechgre_alipay_rel);
		weixinRel = (RelativeLayout) findViewById(R.id.rechgre_weixin_rel);
		alipaySelectImage = (ImageView) findViewById(R.id.rechgre_alipay_select_image);
		weixinSelectImage = (ImageView) findViewById(R.id.rechgre_weixin_select_image);
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		alipayRel.setOnClickListener(this);
		weixinRel.setOnClickListener(this);
		rechargeBtn.setOnClickListener(this);
		setPricePoint(amountEditText);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		back.setClickable(true);
		if (loadingDialog != null)
			loadingDialog.dismiss(); 
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
		case R.id.recharge_amount_button:// 支付宝
			if(Float.valueOf(amount)>=0.01f){
				if (rechargeType == 1) {
					//获取充值Id
					getRechargeId();
				}
				if (rechargeType == 2) {
					getPrepayId();
				}	
			}else{
				CustomToast.makeText(RechargeActivity.this, 0, "金额必须大于0", 200)
				.show();
			}
			break;
		case R.id.rechgre_alipay_rel:
			// 支付宝
			rechargeType = 1;
			alipaySelectImage.setImageDrawable(getResources().getDrawable(
					R.drawable.rechgre_pay_selected_image));
			weixinSelectImage.setImageDrawable(getResources().getDrawable(
					R.drawable.rechgre_pay_no_select_image));
			if (!amount.equals("")) {
				rechargeBtn
						.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
				rechargeBtn.setEnabled(true);
			} else {
				rechargeBtn
						.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
				rechargeBtn.setEnabled(false);
			}
			break;
		case R.id.rechgre_weixin_rel:
			// 微信
			rechargeType = 2;
			alipaySelectImage.setImageDrawable(getResources().getDrawable(
					R.drawable.rechgre_pay_no_select_image));
			weixinSelectImage.setImageDrawable(getResources().getDrawable(
					R.drawable.rechgre_pay_selected_image));
			if (!amount.equals("")) {
				rechargeBtn
						.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
				rechargeBtn.setEnabled(true);
			} else {
				rechargeBtn
						.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
				rechargeBtn.setEnabled(false);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);
	}

	private void getRechargeId() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.add("rechargeMoney", amount);
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.ALIPAY_ORDER_RESERVATIONS, params, true,
				new APPResponseHandler<AlipayEntity>(AlipayEntity.class, this) {
					@Override
					public void onSuccess(AlipayEntity result) {
						if(!TextUtils.isEmpty(result.getOBJECT().getOutTradeNo())){
							pay("钱包充值",amount,result.getOBJECT().getOutTradeNo());	
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getRechargeId();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(RechargeActivity.this)
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
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
						}
					}}

					@Override
					public void onFinish() {

					}
				});
	}

	/**
	 * 获取prepayid
	 */
	public void getPrepayId() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}
		
		RequestParams params = new RequestParams();
		params.add("rechargeMoney", amount);
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.WALLET_RECHARGE, params, true,
				new APPResponseHandler<WXPayEntity>(WXPayEntity.class, this) {
					@Override
					public void onSuccess(WXPayEntity result) {
						Util.rechargeAmout = amount;
						Util.tradeNo = result.getOBJECT().getOut_trade_no();
						request = new PayReq();
						genPayReq(result.getOBJECT().getPrepay_id());
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getPrepayId();
						} else {
						new Handler().postDelayed(new Runnable(){   
				            public void run() {  
				                   //显示dialog
				            	loadingDialog.dismiss();			            	
				            }  
				        }, 500);
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(RechargeActivity.this)
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
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
						}
					}}

					@Override
					public void onFinish() {

					}
				});
	}

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
				amount = s.toString();
				if (!amount.equals("") && rechargeType != 0) {
					rechargeBtn
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					rechargeBtn.setEnabled(true);
				} else {
					rechargeBtn
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					rechargeBtn.setEnabled(false);
				}
			}

		});

	}
	
	public void pay(String itemName,String orderPrice,String orderNo) {
		// 订单
		String orderInfo = getOrderInfo(itemName, itemName, orderPrice,orderNo);

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
				PayTask alipay = new PayTask(RechargeActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	public String getOrderInfo(String subject, String body, String price,String orderNo) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + APPRestClient.BASE_URL+"alipay_notify_url.jsp"
				+ "\"";

		// 服务接口名称， 固定值
		//orderInfo += "&service="+ "\"" +"http://www.uugty.com:8090/uuapplication/alipay_notify_url.jsp" + "\"";
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
	
	public String getOutTradeNo(String OrderNo) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		String key = format.format(date);
		return key+OrderNo;
	}
}