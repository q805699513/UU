package com.uugty.uu.wxapi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.com.rightview.MyPriceActivity;
import com.uugty.uu.com.rightview.PriceDetailActivity;
import com.uugty.uu.com.rightview.RechargeActivity;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.Util;
import com.uugty.uu.map.VipPayActivity;
import com.uugty.uu.map.VipPaySucessActivity;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.uuchat.UUChatPaypriceActivity;

public class WXPayEntryActivity extends BaseActivity implements
		IWXAPIEventHandler {

	// 申请的APPID
	private static final String APP_ID = "wx7f1866a885330eb2";
	// 微信API
	private IWXAPI api;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void initGui() {
		api = WXAPIFactory.createWXAPI(this, APP_ID, false);
		api.handleIntent(getIntent(), this);

	}

	@Override
	protected void initAction() {

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		finish();
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	/*
	 * @Override public void onResp(BaseResp resp) { Bundle bundle = new
	 * Bundle(); switch (resp.errCode) { case BaseResp.ErrCode.ERR_OK: //
	 * 可用以下两种方法获得code // resp.toBundle(bundle); // Resp sp = new Resp(bundle);
	 * // String code = sp.code;<span style="white-space:pre"> // 或者 String code
	 * = ((SendAuth.Resp) resp).code; // 上面的code就是接入指南里要拿到的code
	 * 
	 * break;
	 * 
	 * default: break; }
	 * 
	 * }
	 */

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Intent intent = new Intent();
			if (resp.errCode == 0) {

				if (Util.paySuccessPage.equals("recharge")) {
					intent.setClass(this, MyPriceActivity.class);
				}
				if (Util.paySuccessPage.equals("uuCaht")) {
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.order.UUPayActivity");
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.order.UUOrederPayActivity");
					intent.putExtra("price", Util.rechargeAmout);
					intent.putExtra("payType", "2");
					intent.setClass(this, PriceDetailActivity.class);
				}
				if (Util.paySuccessPage.equals("uutip")) {
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.uuchat.UUTipActivity");
					String price = Util.rechargeAmout;
					String red_id = Util.red_id;
					String red_message = Util.red_message;
					intent.putExtra("price", price);
					intent.putExtra("red_id", red_id);
					intent.putExtra("message", red_message);
					intent.putExtra("avatar", Util.toReceive_avatar);
					intent.putExtra("userName", Util.toReceive_userName);
					intent.putExtra("userId", Util.toReceive_userId);
					intent.putExtra("toFrom", "UUChatOKActivity");
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, ChatActivity.class);

				}
				if (Util.paySuccessPage.equals("uucom")) {
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.uuchat.UUChatCommonActivity");
					String price = Util.rechargeAmout;
					String red_id = Util.red_id;
					String red_message = Util.red_message;
					intent.putExtra("price", price);
					intent.putExtra("red_id", red_id);
					intent.putExtra("message", red_message);
					intent.putExtra("avatar", Util.toReceive_avatar);
					intent.putExtra("userName", Util.toReceive_userName);
					intent.putExtra("userId", Util.toReceive_userId);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, ChatActivity.class);
				}
				if (Util.paySuccessPage.equals("vipPay")) {
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.map.OpenShopActivity");
					ActivityCollector
							.removeSpecifiedActivity("com.uugty.uu.map.VipPayActivity");
					intent.setClass(this, VipPaySucessActivity.class);
				}
				startActivity(intent);
				finish();
			}
			if (resp.errCode == -2) {
				/*
				 * Message message = new Message(); message.what = 1;
				 * handler.sendMessage(message);
				 */
				CustomToast.makeText(WXPayEntryActivity.this, 0, "您取消了支付操作",
						200).show();
				if (Util.paySuccessPage.equals("recharge")) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, RechargeActivity.class);
					startActivity(intent);
					finish();
				} else if (Util.paySuccessPage.equals("uuCaht")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
					startActivity(intent);
					finish();
				} else if (Util.paySuccessPage.equals("uutip")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
					startActivity(intent);
					finish();
				} else if (Util.paySuccessPage.equals("uucom")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
					startActivity(intent);
					finish();
				}
				if (Util.paySuccessPage.equals("vipPay")) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, VipPayActivity.class);
					startActivity(intent);
					finish();
				}

			}
			if (resp.errCode == -1) {
				CustomToast.makeText(WXPayEntryActivity.this, 0, "支付失败", 200)
						.show();

				if (Util.paySuccessPage.equals("recharge")) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, RechargeActivity.class);
				}
				if (Util.paySuccessPage.equals("uuCaht")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
				}
				if (Util.paySuccessPage.equals("uutip")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
				}
				if (Util.paySuccessPage.equals("uucom")) {
					intent.putExtra("pageFlag", Util.pageFlag);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, UUChatPaypriceActivity.class);
				}
				if (Util.paySuccessPage.equals("vipPay")) {
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(this, VipPayActivity.class);
				}
				startActivity(intent);
				finish();
			}

		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				sendLogoutRequest();
				break;
			}
			super.handleMessage(msg);
		}

	};
}
