package com.uugty.uu.shop.guide.activity;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.nostra13.universalimageloader.core.ImageLoader;
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
import com.uugty.uu.entity.AlipayEntity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.PayResult;
import com.uugty.uu.entity.TipPacketEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.WXPayEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.order.UUOrderActivity;
import com.uugty.uu.order.UUPaypriceActivity;
import com.uugty.uu.password.view.GridPasswordView;
import com.uugty.uu.password.view.GridPasswordView.OnPasswordChangedListener;
import com.uugty.uu.util.Md5Util;
import com.uugty.uu.uuchat.ChatActivity;

public class GuidePaypriceActivity extends BaseActivity implements
		OnClickListener,OnPasswordChangedListener {

	private ImageView pay_monery_back;
	private TextView layout_one, layout_two, layout_three;// 我的钱包 微信支付
	private ImageView img1, img2;//
	private ImageView mWalt;//钱包
	private ImageView mWeiCaht;//微信
	private ImageView mAlipay;//阿里
	private int type = 1;//支付方式标识，默认为1 钱包
	private Boolean isPay = false;//是否点击支付，点击支付没有支付成功，会把订单保留
	private String intent_message;// 获得传值金额
	AlertDialog.Builder bulider;
	AlertDialog dialog;//
	private String dialogMsg = "", intent_orderId, intent_content,
			intent_title, intent_no;
	private static String toReceive_avatar, toReceive_username,
			toReceive_userId;
	private RelativeLayout walletRel, weChatRel, alipayRel;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	private static final String APP_ID = "wx65aced6f03b1cffe";
	private PayReq request;
	private String red_id;
	private String str;
	private SpotsDialog loadingDialog;
	private GridPasswordView pwdEdit;
	private ImageView mImageIV;//线路图片
	private TextView mAllPriceTV;// 路线价钱
	private TextView mRoadTitleTV;//路线标题
	private TextView mContactNameTV;//出行人名
	private TextView mOrderTravelNumberTV;//预定数量
	private TextView mVisitorNameTV;// 联系人名
	private TextView mVisitorTelTV;// 联系人手机号
	private TextView mVisitorContentTV;// 联系人留言
	private TextView mOrderTimeTV;// 订单的时间
	private Button mPayButtonBtn;

	private String mAllPrice="";// 路线价钱
	private String mRoadTitle="";//路线标题
	private String mContactName="";//出行人名
	private String mOrderTravelNumber="";//预定数量
	private String mVisitorName="";// 联系人名
	private String mVisitorTel="";// 联系人手机号
	private String mVisitorContent="";// 联系人留言
	private String mOrderTime;// 订单的时间
	private String mRoute_id;// 路线id
	private String mRouteBackgroundImage;//路线图片
	private String mOrderMark;// 订单留言
	private String mContactId;// 联系人主键id

	private String mId;
	private String mUserId;

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
				intent.setClass(GuidePaypriceActivity.this, ChatActivity.class);
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
				intent.setClass(GuidePaypriceActivity.this, ChatActivity.class);
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
							GuidePaypriceActivity.this);
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
				CustomToast.makeText(GuidePaypriceActivity.this, 0,
						bundleText, 200).show();

				break;
			case 3:
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.order.UUOrederPayActivity");
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.order.UUPaypriceActivity");
				Bundle resultbundle = msg.getData();
				String price = resultbundle.getString("price");
				String payType = resultbundle.getString("payType");
				intent.putExtra("price", price);
				intent.putExtra("payType", payType);
				intent.setClass(GuidePaypriceActivity.this,
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
				CustomToast.makeText(GuidePaypriceActivity.this, 0,
						"获取支付ID失败", 200).show();

				break;
			case 7:
				mPayButtonBtn.setClickable(true);
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
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.order.UUPaypriceActivity");
						intent.putExtra("price", intent_content);
						intent.putExtra("payType", "3");
						intent.setClass(GuidePaypriceActivity.this,
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
						intent.setClass(GuidePaypriceActivity.this,
								ChatActivity.class);
						startActivity(intent);
					}

					CustomToast.makeText(GuidePaypriceActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();

				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CustomToast.makeText(GuidePaypriceActivity.this,
								"支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						CustomToast.makeText(GuidePaypriceActivity.this,
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
		return R.layout.activity_pay_monery;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		pay_monery_back = (ImageView) findViewById(R.id.right_myprice_back);
		layout_one = (TextView) findViewById(R.id.me_monery);
		layout_two = (TextView) findViewById(R.id.wechat_monery);
		layout_three = (TextView) findViewById(R.id.alipay_monery);
		mImageIV = (ImageView)findViewById(R.id.pay_img);
		mRoadTitleTV = (TextView)findViewById(R.id.pay_confirm_topic);
		mAllPriceTV = (TextView)findViewById(R.id.pay_confirm_price);
		mContactNameTV = (TextView)findViewById(R.id.pay_people);
		mOrderTravelNumberTV = (TextView)findViewById(R.id.pay_orders);
		mOrderTimeTV = (TextView)findViewById(R.id.pay_travl_time);
		mVisitorNameTV =(TextView)findViewById(R.id.pay_people_name);
		mVisitorTelTV = (TextView)findViewById(R.id.pay_people_phone);
		mVisitorContentTV = (TextView)findViewById(R.id.pay_people_note);
		mPayButtonBtn = (Button)findViewById(R.id.pay_cofirm);
		mWalt = (ImageView)findViewById(R.id.my_pay_image);
		mWeiCaht = (ImageView)findViewById(R.id.wechat_pay_image);
		mAlipay = (ImageView)findViewById(R.id.alipay_pay_image);
		mWalt.setBackgroundResource(R.drawable.pay_click);
		mWeiCaht.setBackgroundResource(R.drawable.pay_noclick);
		mAlipay.setBackgroundResource(R.drawable.pay_noclick);

		walletRel = (RelativeLayout) findViewById(R.id.pay_one);
		weChatRel = (RelativeLayout) findViewById(R.id.pay_two);
		alipayRel = (RelativeLayout) findViewById(R.id.pay_three);
		img1 = (ImageView) findViewById(R.id.my_price_monery);
		img2 = (ImageView) findViewById(R.id.wechat_price_monery);

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
				intent_no =getIntent().getStringExtra("orderNo");

				mId = getIntent().getStringExtra("id");
				mUserId = getIntent().getStringExtra("userId");

				mRouteBackgroundImage = getIntent().getStringExtra("orderImage");//路线图片
				mAllPrice = getIntent().getStringExtra("orderPrice");// 路线价钱
				mRoadTitle = getIntent().getStringExtra("orderName"); //路线标题
				mContactName = getIntent().getStringExtra("contactName");//出行人名
				mRoute_id = getIntent().getStringExtra("orderRoadlineId");// 路线id
				mOrderTime = getIntent().getStringExtra("orderTime");// 订单的时间
				mOrderMark = getIntent().getStringExtra("orderMark");// 订单留言
				mContactId = getIntent().getStringExtra("contactId");// 联系人主键id
				mOrderTravelNumber = getIntent().getStringExtra("orderTravelNumber");//预定数量
				mVisitorName = getIntent().getStringExtra("visitorName");// 联系人名
				mVisitorTel = getIntent().getStringExtra("visitorTel");// 联系人手机号
				mVisitorContent = getIntent().getStringExtra("visitorContent");// 联系人留言
				if(mVisitorName == null){
					mVisitorName = MyApplication.getInstance().getUserInfo().getOBJECT().getUserName();
				}
				if(mVisitorTel == null){
					mVisitorTel = MyApplication.getInstance().getUserInfo().getOBJECT().getUserTel();
				}

			}
			mRoadTitleTV.setText(mRoadTitle);
			mAllPriceTV.setText("￥" + mAllPrice);
			mContactNameTV.setText(mContactName);
			mOrderTravelNumberTV.setText(mOrderTravelNumber);
			mOrderTimeTV.setText(mOrderTime);
			mVisitorContentTV.setText(mVisitorContent);
			mVisitorNameTV.setText(mVisitorName);
			mVisitorTelTV.setText(mVisitorTel);
			mPayButtonBtn.setText("￥" + mAllPrice + "立即支付");
			ImageLoader.getInstance().displayImage(mRouteBackgroundImage,mImageIV);


		}
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {

		pay_monery_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isPay){
					showCustomDialog();
				}else{
					finish();
				}
			}
		});
		weChatRel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type=2;
				mWalt.setBackgroundResource(R.drawable.pay_noclick);
				mWeiCaht.setBackgroundResource(R.drawable.pay_click);
				mAlipay.setBackgroundResource(R.drawable.pay_noclick);
			}
		});
		walletRel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type=1;
				mWalt.setBackgroundResource(R.drawable.pay_click);
				mWeiCaht.setBackgroundResource(R.drawable.pay_noclick);
				mAlipay.setBackgroundResource(R.drawable.pay_noclick);
			}
		});
		alipayRel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type=3;
				mWalt.setBackgroundResource(R.drawable.pay_noclick);
				mWeiCaht.setBackgroundResource(R.drawable.pay_noclick);
				mAlipay.setBackgroundResource(R.drawable.pay_click);
			}
		});
		mPayButtonBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPayButtonBtn.setClickable(false);
				isPay = true;//点击支付按钮后，改变标记
				if(intent_orderId != null && intent_no != null){
					ConfirmPay();
				}else{
					if(null!=MyApplication.getInstance().getUserInfo()){

						//数据发送给服务器
						RequestParams params = new RequestParams();
						params.add("orderRoadlineId", mRoute_id); // 路线id
						params.add("orderTime", mOrderTime); // 订单的时间
						params.add("orderMark", mOrderMark); // 订单留言
						params.add("orderPrice", ""+mAllPrice); // 路线价钱
						params.add("contactId", mContactId); // 联系人主键id
						params.add("orderTravelNumber", ""+mOrderTravelNumber); //预定数量
						params.add("visitorName", mVisitorName);
						params.add("visitorTel", mVisitorTel);
						params.add("visitorContent", mVisitorContent);
						params.add("couponUserId", mUserId);
						params.add("couponId", mId);
						APPRestClient.postGuide(GuidePaypriceActivity.this,
								ServiceCode.ORDER_RESERVATION_GUIDE, params,
								new APPResponseHandler<OrderEntity>(
										OrderEntity.class, GuidePaypriceActivity.this) {
									@Override
									public void onSuccess(OrderEntity result) {
										intent_orderId = result.getOBJECT().getOrderId();
										intent_no = result.getOBJECT().getOrderNo();
										intent_title = result.getOBJECT().getOrderId();
										ConfirmPay();
									}



									@Override
									public void onFailure(int errorCode,
											String errorMsg) {
										CustomToast.makeText(ctx, 0, errorMsg, 300)
												.show();
										mPayButtonBtn.setEnabled(true);
										if (errorCode == -999) {
											new AlertDialog.Builder(
													GuidePaypriceActivity.this)
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

									@Override
									public void onFinish() {
									}
								});
						}else{
							Intent intent = new Intent();
							intent.putExtra("topage",
									UUPaypriceActivity.class.getName());
							intent.setClass(GuidePaypriceActivity.this, LoginActivity.class);
							startActivity(intent);
						}
					}
				}
			});
	}

	private void ConfirmPay() {
		if(type == 1){
			Intent intent = new Intent();
			if(null!=MyApplication.getInstance().getUserInfo()){
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserTelValidate().equals("0")) {
					intent.setClass(GuidePaypriceActivity.this, RightForgetActivity.class);
					startActivity(intent);
				} else {
					// 手机号已验证
					if (MyApplication.getInstance().getUserInfo().getOBJECT()
							.getUserPayPassword().equals("")
							&& null != MyApplication.getInstance().getUserInfo()
									.getOBJECT().getUserPayPassword()) {
						intent.putExtra("from", Util.pageFlag);
						intent.setClass(GuidePaypriceActivity.this, SetPayPwdActivity.class);
						startActivity(intent);
					} else {
						Util.titleName = "我的钱包";
						DialogPrice();
					}
				}
			}else{
				intent.putExtra("topage",
						UUPaypriceActivity.class.getName());
				intent.setClass(GuidePaypriceActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		}
		if(type == 2){
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(GuidePaypriceActivity.this);
			loadingDialog.show();
			}
			Util.titleName = "微信支付";
			getPrepayId();
		}
		if(type == 3){
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
		}
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
								if(result.getOBJECT().getPayInfo() != null) {
									pay(result.getOBJECT().getPayInfo());
								}
							} else {
								if(result.getOBJECT().getPayInfo() != null) {
									pay(result.getOBJECT().getPayInfo());
								}
							}
							mPayButtonBtn.setClickable(true);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getRechargeId();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(GuidePaypriceActivity.this)
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
				new APPResponseHandler<AlipayEntity>(AlipayEntity.class, this) {
					@Override
					public void onSuccess(AlipayEntity result) {
						if(result.getOBJECT().getPayInfo() != null) {
							pay(result.getOBJECT().getPayInfo());
						}
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
				GuidePaypriceActivity.this);
		builder.setMessage(dialogMsg);
		builder.setTitle("确认");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				if("若取消本次支付，可在我的订单里继续完成支付。".equals(dialogMsg)){
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.order.UUOrederPayActivity");
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.order.UUPaypriceActivity");
					Intent intent = new Intent();
					intent.putExtra("from", "priceDetail");
					intent.setClass(GuidePaypriceActivity.this, UUOrderActivity.class);
					startActivity(intent);
				}
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
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
				.from(GuidePaypriceActivity.this);
		View view = inflater.inflate(R.layout.pwd_dialog, null);
		TextView txt_content = (TextView) view
				.findViewById(R.id.price_content_pwd);
		txt_content.setText(intent_content);
		pwdEdit = (GridPasswordView) view
				.findViewById(R.id.price_pay_password);
		pwdEdit.setOnPasswordChangedListener(GuidePaypriceActivity.this);
		bulider = new AlertDialog.Builder(GuidePaypriceActivity.this)
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
										GuidePaypriceActivity.this)
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
										GuidePaypriceActivity.this)
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
										GuidePaypriceActivity.this)
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
									genPayReq(result);
									Message message = new Message();
									message.what = 4;
									handler.sendMessage(message);
									mPayButtonBtn.setClickable(true);
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
												GuidePaypriceActivity.this)
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
									genPayReq(result);
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
												GuidePaypriceActivity.this)
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
									genPayReq(result);
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
												GuidePaypriceActivity.this)
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
								}}

							});

		}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isPay){
				showCustomDialog();
			}
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

	public void pay(final String payInfo) {

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(GuidePaypriceActivity.this);
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
	
}
