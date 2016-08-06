package com.uugty.uu.com.rightview;

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
	private String APP_ID = "wx7f1866a885330eb2";//微信appid,由后台获取
	private PayReq request;
	private RelativeLayout alipayRel, weixinRel;
	private ImageView alipaySelectImage, weixinSelectImage;
	private SpotsDialog loadingDialog;
	// 支付方式
	private int rechargeType = 1;// 1-支付宝 2-微信

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
						if(result.getOBJECT().getPayInfo() != null ) {
							//获取后台数据,调支付宝支付
							pay(result.getOBJECT().getPayInfo());
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
						APP_ID = result.getOBJECT().getAppid();
						request = new PayReq();
						genPayReq(result);
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
							message.what = 2;
							handler.sendMessage(message);
						}
					}}

					@Override
					public void onFinish() {

					}
				});
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
	
	public void pay(final String payInfo) {

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
}
