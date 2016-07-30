package com.uugty.uu.order;

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
import com.uugty.uu.common.util.MD5;
import com.uugty.uu.common.util.SignUtils;
import com.uugty.uu.entity.AlipayEntity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.PayResult;
import com.uugty.uu.entity.TipPacketEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.WXPayEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.password.view.GridPasswordView;
import com.uugty.uu.password.view.GridPasswordView.OnPasswordChangedListener;
import com.uugty.uu.util.Md5Util;
import com.uugty.uu.uuchat.ChatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class UUPaypriceActivity extends BaseActivity implements
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
	private String mInsureType;// 保险类型
	private String mInsureContactId;// 保险人id

	private String mId;
	private String mUserId;
	
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
				intent.setClass(UUPaypriceActivity.this, ChatActivity.class);
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
				intent.setClass(UUPaypriceActivity.this, ChatActivity.class);
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
							UUPaypriceActivity.this);
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
				CustomToast.makeText(UUPaypriceActivity.this, 0,
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
				intent.setClass(UUPaypriceActivity.this,
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
				CustomToast.makeText(UUPaypriceActivity.this, 0,
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
						intent.setClass(UUPaypriceActivity.this,
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
						intent.setClass(UUPaypriceActivity.this,
								ChatActivity.class);
						startActivity(intent);
					}

					CustomToast.makeText(UUPaypriceActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();

				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						CustomToast.makeText(UUPaypriceActivity.this,
								"支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						CustomToast.makeText(UUPaypriceActivity.this,
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
				mInsureType = getIntent().getStringExtra("orderInsuranceType");// 联系人留言
				mInsureContactId = getIntent().getStringExtra("insuranceContactId");// 联系人留言
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
				ActivityCollector.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
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
						params.add("orderInsuranceType",mInsureType);
						params.add("insuranceContactId",mInsureContactId);
						APPRestClient.post(UUPaypriceActivity.this,
								ServiceCode.ORDER_RESERVATION_LIST, params,
								new APPResponseHandler<OrderEntity>(
										OrderEntity.class, UUPaypriceActivity.this) {
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
													UUPaypriceActivity.this)
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
		
									@Override
									public void onFinish() {
									}
								});
						}else{
							Intent intent = new Intent();
							intent.putExtra("topage",
									UUPaypriceActivity.class.getName());
							intent.setClass(UUPaypriceActivity.this, LoginActivity.class);
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
					intent.setClass(UUPaypriceActivity.this, RightForgetActivity.class);
					startActivity(intent);
				} else {
					// 手机号已验证
					if (MyApplication.getInstance().getUserInfo().getOBJECT()
							.getUserPayPassword().equals("")
							&& null != MyApplication.getInstance().getUserInfo()
									.getOBJECT().getUserPayPassword()) {
						intent.putExtra("from", Util.pageFlag);
						intent.setClass(UUPaypriceActivity.this, SetPayPwdActivity.class);
						startActivity(intent);
					} else {
						Util.titleName = "我的钱包";
						DialogPrice();
					}
				}	
			}else{
				intent.putExtra("topage",
						UUPaypriceActivity.class.getName());
				intent.setClass(UUPaypriceActivity.this, LoginActivity.class);
				startActivity(intent);			
			}
		}
		if(type == 2){
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(UUPaypriceActivity.this);
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


//	@Override
//	public void onNoDoubleClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.right_myprice_back:
//			showCustomDialog();
//			break;
//		case R.id.pay_one:
//			type=1;
//			mWalt.setBackgroundResource(R.drawable.pay_click);
//			mWeiCaht.setBackgroundResource(R.drawable.pay_noclick);
//			mAlipay.setBackgroundResource(R.drawable.pay_noclick);
//			
//			break;
//		case R.id.pay_two:
//			type=2;
//			mWalt.setBackgroundResource(R.drawable.pay_noclick);
//			mWeiCaht.setBackgroundResource(R.drawable.pay_click);
//			mAlipay.setBackgroundResource(R.drawable.pay_noclick);
//			
//			break;
//		case R.id.pay_three:
//			type=3;
//			mWalt.setBackgroundResource(R.drawable.pay_noclick);
//			mWeiCaht.setBackgroundResource(R.drawable.pay_noclick);
//			mAlipay.setBackgroundResource(R.drawable.pay_click);
//
//			break;
//		default:
//			break;
//		}
//	}

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
							new AlertDialog.Builder(UUPaypriceActivity.this)
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
				UUPaypriceActivity.this);
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
				.from(UUPaypriceActivity.this);
		View view = inflater.inflate(R.layout.pwd_dialog, null);
		TextView txt_content = (TextView) view
				.findViewById(R.id.price_content_pwd);
		txt_content.setText(intent_content);
		pwdEdit = (GridPasswordView) view
				.findViewById(R.id.price_pay_password);
		pwdEdit.setOnPasswordChangedListener(UUPaypriceActivity.this);
		bulider = new AlertDialog.Builder(UUPaypriceActivity.this)
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
										UUPaypriceActivity.this)
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
										UUPaypriceActivity.this)
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
										UUPaypriceActivity.this)
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
												UUPaypriceActivity.this)
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
												UUPaypriceActivity.this)
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
												UUPaypriceActivity.this)
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
				PayTask alipay = new PayTask(UUPaypriceActivity.this);
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
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

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

	//支付宝增加17位
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		String key = format.format(date);
//		LogUtils.printLog("支付宝", key + intent_no);
		return key + intent_no;
	}
	
	//微信增加14位
//	public String getOutTradePrePayId(String mPrePayId){
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//		Date date = new Date();
//		String key = format.format(date);
//		LogUtils.printLog("微信", key + mPrePayId);
//		return key + mPrePayId;
//	}

	
}
