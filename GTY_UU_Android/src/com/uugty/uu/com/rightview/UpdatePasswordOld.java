package com.uugty.uu.com.rightview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TextViewContent;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.util.Md5Util;

public class UpdatePasswordOld extends BaseActivity implements OnClickListener {
	private TextViewContent psw_old;
	private Button psw_commite;
	private String psw;
	private TopBackView back;
	private AsteriskPasswordTransformationMethod asteriskPassword;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.updata_pwd_old;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		back = (TopBackView) findViewById(R.id.update_pwd_old_title);
		back.setTitle("修改密码");
		psw_old = (TextViewContent) findViewById(R.id.update_psw_old);
		psw_commite = (Button) findViewById(R.id.update_pwd_old_commit_btn);
		asteriskPassword = new AsteriskPasswordTransformationMethod();
		psw_old.setTransformationMethod(asteriskPassword);
		psw_old.addTextChangedListener(new TextWatcher() {

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

			}
		});
	}

	@Override
	protected void initAction() {
		psw_commite.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.update_pwd_old_commit_btn:
			psw = psw_old.getText().toString();
			if (!psw.equals("")) {
				if (!(psw.length() < 6)) {
					sendRequest();
				} else {
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("密码必须是6位");
					builder.setTitle("修改支付密码");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					builder.create().show();
				}
			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("密码不能为空");
				builder.setTitle("修改支付密码");
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
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("oldPayPassword", Md5Util.MD5(psw));// 旧密码
		params.add("type", "4");
		APPRestClient.post(this, APPRestClient.HTTPS_BASE_URL
				+ ServiceCode.SET_USER_PAY_PWD, params, true,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setClass(UpdatePasswordOld.this,
								UPdataActivity.class);
						intent.putExtra("oldpsw", psw);
						startActivity(intent);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						if (errorCode == 1) {
							CustomToast.makeText(ctx, 0, "原始密码不正确", 500).show();
						}
						if (errorCode == -999) {
							new AlertDialog.Builder(UpdatePasswordOld.this)
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
