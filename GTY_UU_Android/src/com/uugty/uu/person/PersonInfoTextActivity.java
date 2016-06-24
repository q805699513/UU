package com.uugty.uu.person;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.MaxLengthWatcher;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.modeal.UUlogin;

public class PersonInfoTextActivity extends BaseActivity {

	private TopBackView topBackView;
	private EmojiEdite mEidt;
	// 传递过来的参数
	String str;
	// 文本框中输入的参数
	private String resultText;
	// 个人信息
	private UUlogin userInfo;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_info_text;
	}

	@Override
	protected void initGui() {
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PersonInfoTextActivity.class.getName());
			intent.setClass(PersonInfoTextActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		userInfo = MyApplication.getInstance().getUserInfo();
		}
		Intent intent = getIntent();
		str = intent.getStringExtra("titileText");
		mEidt = (EmojiEdite) findViewById(R.id.person_info_edit);
		//弹出键盘
		mEidt.setFocusable(true);
		mEidt.setFocusableInTouchMode(true);
		Timer timer = new Timer(); 
	      timer.schedule(new TimerTask() 
	      { 
	          public void run()  
	          { 
	              InputMethodManager inputManager = 
	                  (InputMethodManager)mEidt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
	              inputManager.showSoftInput(mEidt, 0); 
	          } 
	      },500); 
		
		topBackView = (TopBackView) findViewById(R.id.person_info_text_topback);
		// 刚开始有值的话就填充值
		if (str.equals("电子邮箱")) {
			topBackView.setTitle(str);
			mEidt.addTextChangedListener(new MaxLengthWatcher(20, mEidt)); 
			if (!userInfo.getOBJECT().getUserEmail().equals("")) {
				mEidt.setText(userInfo.getOBJECT().getUserEmail());
			} else {
				topBackView.setTitle(str);
				mEidt.setHint("请输入" + str);
			}
		}
		if (str.equals("姓名")) {
			topBackView.setTitle(str);
			mEidt.addTextChangedListener(new MaxLengthWatcher(12, mEidt)); 
			if (!userInfo.getOBJECT().getUserName().equals("")) {
				mEidt.setText(userInfo.getOBJECT().getUserName());
			} else {
				topBackView.setTitle(str);
				mEidt.setHint("请输入" + str);
			}
		}
		if (str.equals("关于我")) {
			topBackView.setTitle(str);
			mEidt.addTextChangedListener(new MaxLengthWatcher(500, mEidt)); 
			if (!userInfo.getOBJECT().getUserDescription().equals("")) {
				mEidt.setText(userInfo.getOBJECT().getUserDescription());
			} else {
				topBackView.setTitle(str);
				mEidt.setHint("请输入" + str);
			}
		}

		if (str.equals("学校")) {
			topBackView.setTitle(str);
			mEidt.addTextChangedListener(new MaxLengthWatcher(20, mEidt)); 
			if (!userInfo.getOBJECT().getUserSchool().equals("")) {
				mEidt.setText(userInfo.getOBJECT().getUserSchool());
			} else {
				topBackView.setTitle(str);
				mEidt.setHint("请输入" + str);
			}
		}
		if (str.equals("城市")) {
			topBackView.setTitle(str);
			mEidt.addTextChangedListener(new MaxLengthWatcher(10, mEidt)); 
			if (!userInfo.getOBJECT().getUserCity().equals("")) {
				mEidt.setText(userInfo.getOBJECT().getUserCity());
			} else {
				mEidt.setHint("请输入" + str);
			}
		}

	}

	@Override
	protected void initAction() {
		mEidt.setSelection(mEidt.getText().toString().trim().length());
		mEidt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
				if(s.toString().contains("\\")){
					mEidt.setText(s.toString().replace("\\", ""));
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		resultText = mEidt.getText().toString().trim();
		if (!resultText.equals("") && resultText != null) {
			if (str.equals("电子邮箱")) {
				userInfo.getOBJECT().setUserEmail(resultText);
				// 修改邮箱
				RequestParams params = new RequestParams();
				params.put("type", "5");
				params.put("content", resultText);
				APPRestClient.post(this, ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});

			}
			if (str.equals("姓名")) {
				userInfo.getOBJECT().setUserName(resultText);
				// 修改姓名
				RequestParams params = new RequestParams();
				params.put("type", "0");
				params.put("content", resultText);
				APPRestClient.post(this, ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
			}
			if (str.equals("关于我")) {
				userInfo.getOBJECT().setUserDescription(resultText);
				// 修改关于我
				RequestParams params = new RequestParams();
				params.put("type", "7");
				params.put("content", resultText);
				APPRestClient.post(this, ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
			}
			if (str.equals("学校")) {
				userInfo.getOBJECT().setUserSchool(resultText);
				// 修改学校
				RequestParams params = new RequestParams();
				params.put("type", "9");
				params.put("content", resultText);
				APPRestClient.post(this, ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
			}
			if (str.equals("城市")) {
				userInfo.getOBJECT().setUserCity(resultText);
				// 修改城市
				RequestParams params = new RequestParams();
				params.put("type", "8");
				params.put("content", resultText);
				APPRestClient.post(this, ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
			}
		}

	}
}
