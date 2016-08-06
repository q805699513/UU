package com.uugty.uu.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.MD5;
import com.uugty.uu.entity.ParentIdEntity;
import com.uugty.uu.entity.PayResult;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.VipAlipayEntity;
import com.uugty.uu.entity.WXPayEntity;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Random;


public class VipPayActivity extends BaseActivity implements OnClickListener {
	private TopBackView titleView;
	private Button alipayBtn, wxpayBtn;
	private EditText telEdit;
	private String tradeNo,telNo;
	private SimpleDraweeView headImage;
	private PayReq request;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	private static final String APP_ID = "wx7f1866a885330eb2";
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					ActivityCollector
					.removeSpecifiedActivity("com.uugty.uu.map.OpenShopActivity");
					Intent intent = new Intent();
					intent.setClass(VipPayActivity.this,
							VipPaySucessActivity.class);
					startActivity(intent);
					CustomToast.makeText(VipPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();

				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CustomToast.makeText(VipPayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						CustomToast.makeText(VipPayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			case 4:
				msgApi.registerApp(APP_ID);
				msgApi.sendReq(request);
				Util.paySuccessPage = "vipPay";
				break;
			case 5:
				CustomToast.makeText(VipPayActivity.this, 0,
						"获取微信支付ID失败", 200).show();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_vip_pay;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.vip_pay_title);
		titleView.setTitle("会员中心");
		telEdit = (EditText) findViewById(R.id.vip_pay_tel_edit);
		alipayBtn = (Button) findViewById(R.id.vip_pay_alipay_btn);
		wxpayBtn = (Button) findViewById(R.id.vip_pay_wxpay_btn);
		headImage = (SimpleDraweeView) findViewById(R.id.vip_pay_headImage);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		alipayBtn.setOnClickListener(this);
		wxpayBtn.setOnClickListener(this);
		telEdit.addTextChangedListener(new TextWatcher() {

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

				telNo = s.toString();
				if (!telNo.equals("")&&telNo.length()==11) {
					alipayBtn.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					wxpayBtn.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					alipayBtn.setEnabled(true);
					wxpayBtn.setEnabled(true);
				} else {
					alipayBtn.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					wxpayBtn.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					alipayBtn.setEnabled(false);
					wxpayBtn.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void initData() {
		if(null!=MyApplication.getInstance().getUserInfo()){
			headImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+MyApplication.getInstance().getUserInfo().getOBJECT().getUserAvatar()));
		}
		//查询有无父级
		queryParentId();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vip_pay_alipay_btn:// 支付宝
			getAliTradeNo();
			break;
		case R.id.vip_pay_wxpay_btn:
			getWxchatTradeNo();
			break;

		default:
			break;
		}
	}

	// 获取支付id
	private void getAliTradeNo() {
		RequestParams params = new RequestParams();
		params.add("userTel", telNo);
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.APP_ALI_JOIN_FEE, params, true,
				new APPResponseHandler<VipAlipayEntity>(VipAlipayEntity.class,
						this) {
					@Override
					public void onSuccess(VipAlipayEntity result) {
						if(result.getOBJECT().getPayInfo() != null){
							alipay(result.getOBJECT().getPayInfo());
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getAliTradeNo();
						} else {
							
							if (errorCode == -999) {
								new AlertDialog.Builder(
										VipPayActivity.this)
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
							}else{
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}
						}
					}

					@Override
					public void onFinish() {

					}
				});
	}

	// 获取支付id
	private void getWxchatTradeNo() {
		RequestParams params = new RequestParams();
		params.add("userTel", telNo);
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.APP_WX_JOIN_FEE, params, true,
				new APPResponseHandler<WXPayEntity>(WXPayEntity.class,
						this) {
					@Override
					public void onSuccess(WXPayEntity result) {
						if (null != result.getOBJECT().getOut_trade_no()) {
							tradeNo = result.getOBJECT().getPrepay_id();
							// 调起微信
							request = new PayReq();
							genPayReq(result);
							Message message = new Message();
							message.what = 4;
							handler.sendMessage(message);

						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getWxchatTradeNo();
						} else {
							if (errorCode == -999) {
								new AlertDialog.Builder(
										VipPayActivity.this)
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
							}else{
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}
						}
					}

					@Override
					public void onFinish() {

					}
				});
	}
	
	private void queryParentId() {
		RequestParams params = new RequestParams();
		APPRestClient.post(this, APPRestClient.BASE_URL
				+ ServiceCode.QUERY_PARENT_ID, params, true,
				new APPResponseHandler<ParentIdEntity>(ParentIdEntity.class, this) {
					@Override
					public void onSuccess(ParentIdEntity result) {
						if (null != result.getOBJECT()) {
							if(!TextUtils.isEmpty(result.getOBJECT().getParentId())){
								telEdit.setVisibility(View.GONE);
								alipayBtn.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
								wxpayBtn.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
								alipayBtn.setEnabled(true);
								wxpayBtn.setEnabled(true);
							}else{
								telEdit.setVisibility(View.VISIBLE);
								alipayBtn.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
								wxpayBtn.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
								alipayBtn.setEnabled(false);
								wxpayBtn.setEnabled(false);
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							queryParentId();
						} else {
							CustomToast.makeText(VipPayActivity.this, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(VipPayActivity.this)
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
						}
					}

					@Override
					public void onFinish() {

					}
				});

	}

	public void alipay(final String payInfo) {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(VipPayActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = 1;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void genPayReq(WXPayEntity result) {
		request.appId = APP_ID;
		request.partnerId = result.getOBJECT().getMch_id();
		request.prepayId = result.getOBJECT().getPrepay_id();
		request.packageValue = result.getOBJECT().getPackages();
		request.nonceStr = result.getOBJECT().getNonce_str();
		request.timeStamp = result.getOBJECT().getTimeStamp();
		request.sign = result.getOBJECT().getSign();

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private String genTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
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
}
