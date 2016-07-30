package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TextViewContent;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.util.Md5Util;

public class UPdataActivity extends BaseActivity implements OnClickListener {

	private TopBackView topTitle;
	private TextViewContent setPwd;
	private String oldPwdText = "", setPwdText = "";
	private Button commitBtn;
	private ImageView control;
	private AsteriskPasswordTransformationMethod asteriskPassword;
	private boolean flag = false;
	private RelativeLayout control_relative;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.updata_pwd;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		topTitle = (TopBackView) findViewById(R.id.update_pwd_title);
		setPwd = (TextViewContent) findViewById(R.id.update_psw_new_psw);
		commitBtn = (Button) findViewById(R.id.update_pwd_commit_btn);
		control = (ImageView) findViewById(R.id.control_psw);
		control_relative = (RelativeLayout) findViewById(R.id.control_relative);
		topTitle.setTitle("修改密码");
		asteriskPassword = new AsteriskPasswordTransformationMethod();
		setPwd.setTransformationMethod(asteriskPassword);
	}

	@Override
	protected void initAction() {
		Intent intent = getIntent();
		oldPwdText = intent.getStringExtra("oldpsw");
		commitBtn.setOnClickListener(this);
		control_relative.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// updata_Back.setClickable(true);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {

		case R.id.update_pwd_commit_btn:
			setPwdText = setPwd.getText().toString();
			if (setPwdText.equals("")) {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("输入的支付密码为空");
				builder.setTitle("修改支付密码");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			} else if (setPwdText.length() < 6) {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("密码最少6位");
				builder.setTitle("修改支付密码");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			} else {
				setPwdText = setPwd.getText().toString();
				// 调用接口
				sendRequest();
			}
			break;
		case R.id.control_relative:
			if (!flag) {
				flag = true;
				setPwd.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				control.setImageResource(R.drawable.show_psw);
				setPwd.setSelection(setPwd.getText().toString().length());
			} else {
				flag = false;
				setPwd.setTransformationMethod(asteriskPassword);
				control.setImageResource(R.drawable.hide_psw);
				setPwd.setSelection(setPwd.getText().toString().length());
			}

			break;

		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("oldPayPassword", Md5Util.MD5(oldPwdText));// 旧密码
		params.add("newPayPassword", Md5Util.MD5(setPwdText));// 新密码
		params.add("type", "2");
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.SET_USER_PAY_PWD, params, true,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						if(null==MyApplication.getInstance().getUserInfo()){
							Intent intent=new Intent();
							intent.putExtra("topage",
									UPdataActivity.class.getName());
							intent.setClass(UPdataActivity.this, LoginActivity.class);
							startActivity(intent);
						}else{
						ActivityCollector
								.removeSpecifiedActivity("com.uugty.uu.com.rightview.UpdatePasswordOld");
						MyApplication.getInstance().getUserInfo().getOBJECT()
								.setUserPayPassword(setPwdText);
						CustomToast.makeText(UPdataActivity.this, 0, "修改密码成功",
								200).show();
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.setClass(UPdataActivity.this,
								PasswordActivity.class);
						startActivity(intent);
					}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(UPdataActivity.this)
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

	/**
	 * 更改密码默认替代字符,系统默认的字符太小了
	 * 
	 * @author
	 * 
	 */

	class AsteriskPasswordTransformationMethod extends
			PasswordTransformationMethod {

		@Override
		public CharSequence getTransformation(CharSequence source, View view) {
			return new PasswordCharSequence(source);
		}

		private class PasswordCharSequence implements CharSequence {
			private CharSequence mSource;

			public PasswordCharSequence(CharSequence source) {
				mSource = source;
			}

			@Override
			public int length() {
				return mSource.length();
			}

			@Override
			public char charAt(int index) {
				return '●';
			}

			@Override
			public CharSequence subSequence(int start, int end) {
				return mSource.subSequence(start, end);
			}

		}
	}
}
