package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.CollectActivity;
import com.uugty.uu.com.rightview.MyPriceActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.UserLineTextAndImage;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.discount.c.MyDiscountActivity;
import com.uugty.uu.entity.AddJpushId;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.VipEntity;
import com.uugty.uu.friendstask.DynamicDetailActivity;
import com.uugty.uu.login.ForgetActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.login.RegsterActivity;
import com.uugty.uu.map.OpenShopActivity;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.modeal.UUlogin;
import com.uugty.uu.order.UUOrderActivity;
import com.uugty.uu.person.PersonCompileActivity;
import com.uugty.uu.setup.ContactUsActivity;
import com.uugty.uu.setup.FeedbookActivity;
import com.uugty.uu.setup.PersonSetupActivity;
import com.uugty.uu.setup.UUHelpActivity;
import com.uugty.uu.shop.MyShopActivity;
import com.uugty.uu.shop.NotVipShopActivity;
import com.uugty.uu.util.LogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.jpush.android.api.JPushInterface;

public class Fragement4 extends Fragment implements OnClickListener {

	private Context context;
	private LinearLayout nologin_lin, login_line;
	private View rootview;
	private CirculHeadImage userimg, login_person_circul_imageview;
	private UserLineTextAndImage route, help, about, feedback, my_buy,
			my_receive, my_wallet, my_collect, my_invitation;
	private UserLineTextAndImage my_discount_ticket;
	private Button login_insert, weix_login;
	public static IWXAPI WXapi;
	private static final String APP_ID = "wx7f1866a885330eb2";
	private SpotsDialog loadingDialog;
	private RelativeLayout release_route_line, login_person_rel,
			nologin_person_rel, my_route, my_setting;
	private TextView username, userwork, setting_route_line;
	private TextView isVer;//是否为认证小u
	private ImageView usersex;
	private boolean wxchatFlag = false;// 点击微信登录标志
	// 经纬度
	private Double geoLat, geoLng;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.context = getActivity();
		if (rootview == null) {

			rootview = inflater.inflate(R.layout.nologin_frag, null);

		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootview.getParent();

		if (parent != null) {
			parent.removeView(rootview);
		}
		return rootview;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// 登录
		setting_route_line = (TextView) rootview
				.findViewById(R.id.setting_route_line);
		login_line = (LinearLayout) rootview.findViewById(R.id.login_lin);
		login_person_rel = (RelativeLayout) rootview
				.findViewById(R.id.login_person_rel);
		login_person_circul_imageview = (CirculHeadImage) rootview
				.findViewById(R.id.login_person_circul_imageview);
		release_route_line = (RelativeLayout) rootview
				.findViewById(R.id.release_route_line);
		username = (TextView) rootview.findViewById(R.id.login_user_name);
		isVer = (TextView) rootview.findViewById(R.id.login_user_name_ver);
		userwork = (TextView) rootview.findViewById(R.id.login_user_work);
		usersex = (ImageView) rootview.findViewById(R.id.login_user_sex);
		my_route = (RelativeLayout) rootview.findViewById(R.id.my_routeline);
		my_buy = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_buy_order);
		my_buy.linesl();
		my_receive = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_receive_order);
		my_wallet = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_wallet);
		my_discount_ticket = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_discount_ticket);
		my_collect = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_collect);
		my_invitation = (UserLineTextAndImage) rootview
				.findViewById(R.id.my_invitation);
		my_setting = (RelativeLayout) rootview.findViewById(R.id.my_setting);
		my_buy.setLedtText("我购买的");
		my_buy.setLeftImageView(getResources().getDrawable(
				R.drawable.my_buy_line));
		my_receive.setLedtText("我出售的");
		my_receive.setLeftImageView(getResources().getDrawable(
				R.drawable.my_receive_login));
		my_wallet.setLedtText("我的钱包");
		my_wallet.setLeftImageView(getResources().getDrawable(
				R.drawable.wallet_login));
		my_wallet.linesl();
		my_discount_ticket.setLedtText("代金券");
		my_discount_ticket.setLeftImageView(getResources().getDrawable(
				R.drawable.lzh_discount_icon));
		my_collect.setLedtText("我的收藏");
		my_collect.setLeftImageView(getResources().getDrawable(
				R.drawable.collcte_login));
		my_invitation.setLedtText("我的uu圈");
		my_invitation.setLeftImageView(getResources().getDrawable(
				R.drawable.my_invitation));
		// 未登录
		nologin_person_rel = (RelativeLayout) rootview
				.findViewById(R.id.nologin_person_rel);
		nologin_lin = (LinearLayout) rootview.findViewById(R.id.nologin_lin);
		login_insert = (Button) rootview.findViewById(R.id.login_insert);
		weix_login = (Button) rootview.findViewById(R.id.weix_login);
		userimg = (CirculHeadImage) rootview
				.findViewById(R.id.nologin_person_circul_imageview);
		userimg.setCirCularImageSize(55, 55, 5);
		userimg.setNoHeadPic("drawable://" + R.drawable.no_default_head_img,
				"drawable");
		userimg.setBackPic("drawable://" + R.drawable.persion_circle_bg);

		route = (UserLineTextAndImage) rootview
				.findViewById(R.id.home_no_login_route_lin);
		help = (UserLineTextAndImage) rootview
				.findViewById(R.id.home_no_login_help);
		about = (UserLineTextAndImage) rootview
				.findViewById(R.id.home_no_login_contact_us_lin);
		feedback = (UserLineTextAndImage) rootview
				.findViewById(R.id.home_no_login_feedback);
		route.setLedtText("发布服务");
		route.setLeftImageView(getResources().getDrawable(
				R.drawable.nologin_release_img));
		help.setLedtText("帮助");
		help.setLeftImageView(getResources().getDrawable(
				R.drawable.help_nologin));
		about.setLedtText("关于uu客");
		about.setLeftImageView(getResources().getDrawable(
				R.drawable.about_nologin));
		feedback.setLedtText("反馈");
		feedback.setLeftImageView(getResources().getDrawable(
				R.drawable.feedback_nologin));
		login_insert.setOnClickListener(this);
		weix_login.setOnClickListener(this);
		route.setOnClickListener(this);
		help.setOnClickListener(this);
		about.setOnClickListener(this);
		feedback.setOnClickListener(this);
		my_route.setOnClickListener(this);
		my_buy.setOnClickListener(this);
		my_receive.setOnClickListener(this);
		my_wallet.setOnClickListener(this);
		my_collect.setOnClickListener(this);
		my_invitation.setOnClickListener(this);
		my_setting.setOnClickListener(this);
		nologin_person_rel.setOnClickListener(this);
		login_person_rel.setOnClickListener(this);
		release_route_line.setOnClickListener(this);
		setting_route_line.setOnClickListener(this);
		my_discount_ticket.setOnClickListener(this);

	}

	private void initview() {
		// TODO Auto-generated method stub
		// 头像
		if (MyApplication.getInstance().getUserInfo() != null) {
			if(MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserIsPromoter().equals("1")){
				isVer.setVisibility(View.VISIBLE);
			}else{
				isVer.setVisibility(View.INVISIBLE);
			}
			if (!MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserAvatar().equals("")) {
				login_person_circul_imageview.setCirCularImageSize(55, 55, 5);
				login_person_circul_imageview.setBackPic("drawable://"
						+ R.drawable.persion_circle_bg);
				// imageView.setCirCularImageSize(85, 85, 6);
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserAvatar().contains("images")) {
					login_person_circul_imageview.setNoHeadPic(MyApplication
							.getInstance().getUserInfo().getOBJECT()
							.getUserAvatar(), "net");
				} else {
					login_person_circul_imageview.setNoHeadPic(MyApplication
							.getInstance().getUserInfo().getOBJECT()
							.getUserAvatar(), "local");
				}
			} else {
				// 加载默认的图片
				login_person_circul_imageview.setBackPic("drawable://"
						+ R.drawable.persion_circle_bg);
				login_person_circul_imageview.setNoHeadPic("drawable://"
						+ R.drawable.no_default_head_img, "drawable");
				login_person_circul_imageview.setCirCularImageSize(55, 55, 5);
			}
		} else {
			login_person_circul_imageview.setBackPic("drawable://"
					+ R.drawable.persion_circle_bg);
			login_person_circul_imageview.setHeadPic("drawable://"
					+ R.drawable.no_default_head_img, "drawable");
			login_person_circul_imageview.setCirCularImageSize(55, 55, 5);
		}

		if (!MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserName().equals("")
				&& MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserName() != null) {
			username.setText(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserName());
		} else {
			username.setText("小U");
		}

		if (!TextUtils.isEmpty(MyApplication.getInstance().getUserInfo()
				.getOBJECT().getUserWork())) {
			userwork.setText(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserWork());
		} else {
			userwork.setText("他还没有填写");
		}
		if (!MyApplication.getInstance().getUserInfo().getOBJECT().getUserSex()
				.equals("")
				|| MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserSex() != null) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserSex().equals("1")) {
				usersex.setBackgroundResource(R.drawable.order_guide_man);
			} else {
				usersex.setBackgroundResource(R.drawable.order_guide_woman);
			}
		} else {
			usersex.setBackgroundResource(R.drawable.order_guide_man);
		}

		if (!MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserName().equals("")
				&& MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserName() != null) {
			username.setText(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserName());
		} else {
			username.setText("小U");
		}
		if (!MyApplication.getInstance().getUserInfo().getOBJECT().getUserSex()
				.equals("")
				|| MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserSex() != null) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserSex().equals("1")) {
				usersex.setBackgroundResource(R.drawable.persondate_man);
			} else {
				usersex.setBackgroundResource(R.drawable.persondate_women);
			}

		} else {
			usersex.setBackgroundResource(R.drawable.persondate_man);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyApplication.getInstance().isLogin()) {
			nologin_lin.setVisibility(View.GONE);
			login_line.setVisibility(View.VISIBLE);
			initview();
		} else {
			nologin_lin.setVisibility(View.VISIBLE);
			login_line.setVisibility(View.GONE);
			if (wxchatFlag) {
				if (loadingDialog != null)
					loadingDialog.dismiss();
				if (null != MyApplication.getInstance().getBaseResp()) {
					if (MyApplication.getInstance().getBaseResp().getType() == ConstantsAPI.COMMAND_SENDAUTH) {
						// code返回
						String weixinCode = ((SendAuth.Resp) MyApplication
								.getInstance().getBaseResp()).code;
						getAccessToken(weixinCode);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.login_insert:
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			break;
		case R.id.weix_login:
			WXLogin();
			break;
		case R.id.nologin_person_rel:
			if (MyApplication.getInstance().isLogin()) {
				intent.setClass(context, PersonCompileActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MainActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.home_no_login_route_lin:
			intent.putExtra("topage", MainActivity.class.getName());
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			break;
		case R.id.home_no_login_help:
			intent.setClass(context, UUHelpActivity.class);
			startActivity(intent);
			break;
		case R.id.home_no_login_contact_us_lin:
			intent.setClass(context, ContactUsActivity.class);
			startActivity(intent);
			break;
		case R.id.home_no_login_feedback:
			intent.setClass(context, FeedbookActivity.class);
			startActivity(intent);
			break;
		// 登录成功
		case R.id.my_invitation:
			if (MyApplication.getInstance().isLogin()) {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("userid", MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserId());
				intent.putExtra("username", MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserName());
				intent.putExtra("useravatar", MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserAvatar());
				intent.putExtra("usertype", "1");
				intent.setClass(context, DynamicDetailActivity.class);
				context.startActivity(intent);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MainActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.setting_route_line:
			intent.setClass(context, UUHelpActivity.class);
			context.startActivity(intent);
			break;
		case R.id.my_routeline:
			queryIsVip("shop");

			break;
		case R.id.login_person_rel:
			if (MyApplication.getInstance().isLogin()) {
				intent.setClass(context, PersonCompileActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MainActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.release_route_line:
			setPermissionRequest();
			break;
		case R.id.my_buy_order:
			if (MyApplication.getInstance().isLogin()) {
				intent.putExtra("from", "buy");
				intent.setClass(context, UUOrderActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", UUOrderActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.my_receive_order:
			if (MyApplication.getInstance().isLogin()) {
				intent.putExtra("from", "receive");
				intent.setClass(context, UUOrderActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", UUOrderActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.my_wallet:
			if (MyApplication.getInstance().isLogin()) {
				intent.setClass(context, MyPriceActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MyPriceActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.my_discount_ticket://代金卷
			if (MyApplication.getInstance().isLogin()) {
				intent.setClass(context, MyDiscountActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MyPriceActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
			}
			break;
		case R.id.my_collect:
			if (MyApplication.getInstance().isLogin()) {
				intent.setClass(context, CollectActivity.class);
				context.startActivity(intent);
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", MainActivity.class.getName());
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);

			}
			break;
		case R.id.my_setting:
			intent.setClass(context, PersonSetupActivity.class);
			context.startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 登录微信
	 */
	private void WXLogin() {
		// 不存在
		Util.sharWXType="fragment4";
		MyApplication.getInstance().setKilled(false);
		WXapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
		if (WXapi.isWXAppInstalled()) {
			if (loadingDialog != null) {
				loadingDialog.show();
			} else {
				loadingDialog = new SpotsDialog(context);
				loadingDialog.show();
			}
			WXapi.registerApp(APP_ID);
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "wechat_sdk_demo";
			WXapi.sendReq(req);
			wxchatFlag = true;
			pushJpushId();
		} else {
			// 提示用户安装微信客户端
			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setIcon(R.drawable.exit_remind)
					.setMessage("未安装微信客户端,无法使用该功!")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}

	}


	private void pushJpushId() {
		RequestParams params = new RequestParams();
		params.add("registrationID", JPushInterface.getRegistrationID(context));
		params.add("clientVersion", MyApplication.getInstance()
				.getApp_version()); // 版本号
		params.add("type", "android");
		APPRestClient.post(context, ServiceCode.PUSH_ID, params,
				new APPResponseHandler<AddJpushId>(
						AddJpushId.class, context) {
					@Override
					public void onSuccess(AddJpushId result) {
						SharedPreferenceUtil.getInstance(context).setString("JPushLoginRegistId",JPushInterface.getRegistrationID(context));
						LogUtils.printLog("JPushLoginRegistId",JPushInterface.getRegistrationID(context));
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
					}

					@Override
					public void onFinish() {
					}
				});
	}
	/*
	 * 发布路线
	 */
	private void setPermissionRequest() {

		RequestParams params = new RequestParams();
		APPRestClient.post(context, ServiceCode.ROAD_LINE_PUBLISH_PERMISSION,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						context) {
					@Override
					public void onSuccess(BaseEntity result) {
						queryIsVip("publish");
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							setPermissionRequest();
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300)
									.show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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
								new AlertDialog.Builder(context)
										.setTitle("提示")
										.setMessage(errorMsg)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														Intent intent = new Intent();
														intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
														intent.setClass(
																context,
																MainActivity.class);
														context.startActivity(intent);
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

	
	/**
	 * 获取access_token
	 */
	public void getAccessToken(final String code) {
		// https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
		// 调用接口
		new Thread() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				@SuppressWarnings("deprecation")
				HttpGet getMethod = new HttpGet(
						"https://api.weixin.qq.com/sns/oauth2/access_token?appid="
								+ APP_ID
								+ "&secret=6e360ffc74d63c28f155ac675ccfd8d4"
								+ "&code=" + code
								+ "&grant_type=authorization_code");
				try {
					HttpResponse response = httpClient.execute(getMethod);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream is = response.getEntity().getContent();
						BufferedReader br = new BufferedReader(
								new InputStreamReader(is));
						String str = "";
						StringBuffer sb = new StringBuffer();
						while ((str = br.readLine()) != null) {
							sb.append(str);
						}
						is.close();
						String josn = sb.toString();
						JSONObject json1 = new JSONObject(josn);
						String access_token = (String) json1
								.get("access_token");
						String refresh_token = (String) json1
								.get("refresh_token");
						String openid = (String) json1.get("openid");
						// 将access_token、refresh_token放入SharedPreference文件
						/*
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "access_token", access_token);
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "refresh_token", refresh_token);
						 * SharedPreferenceUtil.getInstance(ctx).setString(
						 * "open_id", openid);
						 */
						// 刚刚获取的token,不存在token超时问题,直接获取用户信息
						// 调用微信登录接口
						
						Message msg = new Message();
						msg.what = 1;
						Bundle bundle = new Bundle();
						bundle.putString("token", access_token);
						bundle.putString("openId", openid);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 执行GET方法
			}

		}.start();
	}

	private void queryIsVip(final String action) {
		RequestParams params = new RequestParams();
		APPRestClient.post(context, APPRestClient.BASE_URL
				+ ServiceCode.QUERY_IS_VIP, params, true,
				new APPResponseHandler<VipEntity>(VipEntity.class, context) {
					@Override
					public void onSuccess(VipEntity result) {
						Intent intent = new Intent();
						if (null != result.getOBJECT()) {
							if (action.equals("publish")) {// 发布
								if (result.getOBJECT().getUserIsPromoter()
										.equals("1") || !result.getOBJECT().getRoadlineId()
										.equals("0")) {
									intent.setClass(context,
											PublishServicesActivity.class);
									intent.putExtra("from", "framgent");
									context.startActivity(intent);
								} else {
									intent.setClass(context,
											OpenShopActivity.class);
									Util.vipBack="main";
									context.startActivity(intent);
								}
							} else if (action.equals("shop")) {
								if("0".equals(result.getOBJECT().getUserIsPromoter())){
									if("0".equals(result.getOBJECT().getRoadlineId())){
										intent.setClass(context,
												MyShopActivity.class);
										context.startActivity(intent);
									}else {
										intent.setClass(context,
												NotVipShopActivity.class);
										context.startActivity(intent);
									}
								}else {
									intent.setClass(context,
											MyShopActivity.class);
									context.startActivity(intent);
								}
//								if (!result.getOBJECT().getRoadlineId()
//										.equals("0")) {
//									if(result.getOBJECT().getUserIsPromoter()
//											.equals("1") ){
//										intent.setClass(context,
//												MyShopActivity.class);
//										context.startActivity(intent);
//									}else {
//										intent.setClass(context,
//												NotVipShopActivity.class);
//										context.startActivity(intent);
//									}
//								} else {
//
//									if(result.getOBJECT().getUserIsPromoter()
//											.equals("1") ){
//										intent.setClass(context,
//												MyShopActivity.class);
//										context.startActivity(intent);
//									}else {
//										intent.setClass(context,
//												OpenShopActivity.class);
//										Util.vipBack="main";
//										context.startActivity(intent);
//									}
//
//								}
							}
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							queryIsVip(action);
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300)
									.show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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
					}

					@Override
					public void onFinish() {

					}
				});

	}
	
	/**
	 * 是否为VIP
	 */
	private void queryIsVip() {
		RequestParams params = new RequestParams();
		APPRestClient.post(context, APPRestClient.BASE_URL
				+ ServiceCode.QUERY_IS_VIP, params, true,
				new APPResponseHandler<VipEntity>(VipEntity.class, context) {
					@Override
					public void onSuccess(VipEntity result) {
						if (null != result.getOBJECT()) {
						
								if (result.getOBJECT().getUserIsPromoter()
										.equals("1")) {
								isVer.setVisibility(View.VISIBLE);
							} 
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							queryIsVip();
						} else {
							CustomToast.makeText(context, 0, errorMsg, 300)
									.show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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
					}

					@Override
					public void onFinish() {

					}
				});

	}
	

	public void getWXUserInfo(final String access_token, final String openid) {
		// 显示等待层
		if (loadingDialog != null) {
			loadingDialog.show();
		} else {
			loadingDialog = new SpotsDialog(getActivity());
			loadingDialog.show();
		}
		RequestParams params = new RequestParams();

		params.add("access_token", access_token); // 手机�?
		params.add("openid", openid); // 密码
		if (null != geoLng && !geoLng.equals("")) {
			params.add("userLastLoginLng", geoLng.toString()); // 经度
			params.add("userLastLoginLat", geoLat.toString()); // 纬度
		} else {
			params.add("userLastLoginLng", "39.938897"); // 经度
			params.add("userLastLoginLat", "116.464053"); // 纬度
		}
		params.add("uuid", MyApplication.getInstance().getUuid()); // uuid

		APPRestClient.post(context, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.USER_WECHAR_LOGIN, params, true,
				new APPResponseHandler<UUlogin>(UUlogin.class, context) {
					@Override
					public void onSuccess(UUlogin result) {
						Message msg = Message.obtain();
						msg.what = 2;
						Bundle b = new Bundle();
						b.putSerializable("LoginData", result);
						b.putString("LoginType", "wechat");
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getWXUserInfo(access_token, openid);
						} else {
							loadingDialog.dismiss();
							CustomToast.makeText(context, 0, errorMsg, 300)
									.show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context)
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
					}

					@Override
					public void onFinish() {

					}
				});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String acces_token = msg.getData().getString("token");
				String openId = msg.getData().getString("openId");
				getWXUserInfo(acces_token, openId);
				break;
			case 3:
				String arg1 = msg.getData().getString("arg1");
				CustomToast.makeText(context,
						getString(R.string.hx_login_failed) + arg1,
						Toast.LENGTH_SHORT).show();
				MyApplication.getInstance().setLogin(false);
				break;
			case 2:
				UUlogin objectRcvd = (UUlogin) msg.getData().getSerializable(
						"LoginData");
				if (loadingDialog != null)
					loadingDialog.dismiss();
				wxchatFlag = false;
				Intent intent = new Intent();
				if (null == objectRcvd.getOBJECT().getUserTel()
						|| objectRcvd.getOBJECT().getUserTel().equals("")) {
					intent.putExtra("toPage", "wxLogin");
					intent.setClass(context, RegsterActivity.class);
					startActivity(intent);
				} else if (!TextUtils.isEmpty(objectRcvd.getOBJECT()
						.getUserTel())
						&& TextUtils.isEmpty(objectRcvd.getOBJECT()
								.getUserPassword())) {
					intent.putExtra("username", objectRcvd.getOBJECT()
							.getUserTel());
					intent.putExtra("toPage", "setPwd");
					intent.setClass(context, ForgetActivity.class);
					startActivity(intent);
				} else {
					// 微信绑定过
					MyApplication.getInstance().setUserInfo(objectRcvd);
					MyApplication.getInstance().setLogin(true);
					SharedPreferenceUtil.getInstance(context).setString(
							"userName", objectRcvd.getOBJECT().getUserTel());
					SharedPreferenceUtil.getInstance(context)
							.setString("userPwd",
									objectRcvd.getOBJECT().getUserPassword());
					intent.setClass(context, MainActivity.class);
					EMChatManager.getInstance().login(
							objectRcvd.getOBJECT().getUserId(),
							objectRcvd.getOBJECT().getUserEasemobPassword(),
							new EMCallBack() {
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									EMGroupManager.getInstance()
											.loadAllGroups();
									EMChatManager.getInstance()
											.loadAllConversations();
								}

								@Override
								public void onProgress(int arg0, String arg1) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onError(int arg0, final String arg1) {
									// TODO Auto-generated method stub
									Message msg = Message.obtain();
									msg.what = 3;
									Bundle bundle = new Bundle();
									bundle.putString("arg1", arg1);
									msg.setData(bundle);
									handler.sendMessage(msg);

								}
							});
					startActivity(intent);
				}
			}
		};
	};

}
