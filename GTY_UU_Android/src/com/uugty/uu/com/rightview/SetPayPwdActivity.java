package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.password.view.GridPasswordView;
import com.uugty.uu.util.Md5Util;
import com.uugty.uu.uuchat.UUChatPaypriceActivity;

public class SetPayPwdActivity extends BaseActivity implements OnClickListener {

	private GridPasswordView setPwd, confirmPwd;
	private String setPwdText = "", comfirmPwdText = "";
	private Button commitBtn;
	private TopBackView title;
	private String smsCode = "", pageFrom = "";

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.set_pay_pwd;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			smsCode = getIntent().getStringExtra("smsCode");
			pageFrom = getIntent().getStringExtra("from");

		}
		title = (TopBackView) findViewById(R.id.set_pay_title);
		title.setTitle("设置U支付密码");
		setPwd = (GridPasswordView) findViewById(R.id.set_pay_pwed);
		confirmPwd = (GridPasswordView) findViewById(R.id.confirm_pay_pwed);
		commitBtn = (Button) findViewById(R.id.pay_pwd_set_btn);

	}

	@Override
	protected void initAction() {
	
		setPwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String password) {
            }

            @Override
            public void onInputFinish(String password) { 
            	setPwdText = password;
            }
        });
		confirmPwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String password) {
            }

            @Override
            public void onInputFinish(String password) { 
            	comfirmPwdText = password;
            }
        });
	

		commitBtn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.pay_pwd_set_btn:
			if (setPwdText.equals("") || comfirmPwdText.equals("")) {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("请输入支付密码");
				builder.setTitle("设置支付密码");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
				break;
			}

			if (setPwdText.equals(comfirmPwdText)) {
				// 调用接口
				sendRequest();

			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("两次输入密码不一致,请重新输入!");
				builder.setTitle("设置支付密码");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
			break;
		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("userPayPassword", Md5Util.MD5(comfirmPwdText));// 密码
		if (null != pageFrom && pageFrom.equals("WithdrawcashActivity")) {
			params.add("type", "1");
		} else if (null != pageFrom && pageFrom.equals("MyPriceActivity")) {
			params.add("type", "1");
		} else if (null != pageFrom && pageFrom.equals("ForgetPayPwdActivity")) {
			params.add("veryCode", smsCode);
			params.add("type", "3");
		} else {
			params.add("type", "1");
		}

		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.SET_USER_PAY_PWD, params, true,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						if(null==MyApplication.getInstance().getUserInfo()){
							Intent intent=new Intent();
							intent.putExtra("topage",
									SetPayPwdActivity.class.getName());
							intent.setClass(SetPayPwdActivity.this, LoginActivity.class);
							startActivity(intent);
						}else{
						MyApplication.getInstance().getUserInfo().getOBJECT()
								.setUserPayPassword(Md5Util.MD5(comfirmPwdText));
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						if (null != pageFrom
								&& pageFrom.equals("WithdrawcashActivity")) {
							intent.setClass(SetPayPwdActivity.this,
									WithdrawcashActivity.class);
						} else if (null != pageFrom
								&& pageFrom.equals("MyPriceActivity")) {
							intent.setClass(SetPayPwdActivity.this,
									MyPriceActivity.class);
						} else if (null != pageFrom
								&& pageFrom.equals("ForgetPayPwdActivity")) {
							ActivityCollector
									.removeSpecifiedActivity("com.uugty.uu.com.rightview.ForgetPayPwdActivity");
							intent.setClass(SetPayPwdActivity.this,
									PasswordActivity.class);
						} else {
							intent.putExtra("pageFlag", pageFrom);
							intent.setClass(SetPayPwdActivity.this,
									UUChatPaypriceActivity.class);
						}
						startActivity(intent);
						CustomToast.makeText(ctx, 0, "设置成功", 200).show();
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(SetPayPwdActivity.this)
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
}
