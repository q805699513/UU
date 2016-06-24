package com.uugty.uu.wxapi;

import android.content.Intent;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.Util;
import com.uugty.uu.main.MainActivity;

/*
 * 微信授权*/
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	// 申请的APPID
	private static final String APP_ID = "wx7f1866a885330eb2";
	// 微信API
	private IWXAPI api;

	private BaseResp baseResp = null;

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
		String result = "";
		if (resp != null) {
			baseResp = resp;
			
		}
		MyApplication.getInstance().setBaseResp(baseResp);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (Util.sharWXType.equals("share")) {
				result = "分享成功";
				CustomToast.makeText(this, 0, result, 200).show();
			} 
			//从fragment里返回，在部分机型上不跳转
			if (Util.sharWXType.equals("fragment4")) {
				MyApplication.getInstance().setKilled(true);
				Util.sharWXType="";
				Intent intent = new Intent();
				intent.setClass(this, MainActivity.class);
				startActivity(intent);
				break;
			} 
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Util.sharWXType="";
			result = "发送取消";
			CustomToast.makeText(this, 0, result, 200).show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Util.sharWXType="";
			result = "发送被拒绝";
			CustomToast.makeText(this, 0, result, 200).show();

			finish();
			break;
		default:
			Util.sharWXType="";
			result = "发送返回";
			CustomToast.makeText(this, 0, result, 200).show();
			finish();
			break;
		}
	}

}