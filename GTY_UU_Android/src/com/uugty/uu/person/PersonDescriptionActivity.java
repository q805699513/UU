package com.uugty.uu.person;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;

public class PersonDescriptionActivity extends BaseActivity {

	private TopBackView titleView;
	private TextView confirmBtn;
	private EditText editTextOne,editTextTwo,editTextThree,editTextFour;
	private String resultText;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_person_description;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.person_descrip_title);
		titleView.setTitle("自我介绍");
		confirmBtn=(TextView) findViewById(R.id.persn_descrip_btn);
		editTextOne = (EditText) findViewById(R.id.person_descrip_text_one);
		editTextTwo= (EditText) findViewById(R.id.person_descrip_text_two);
		editTextThree= (EditText) findViewById(R.id.person_descrip_text_three);
		editTextFour= (EditText) findViewById(R.id.person_descrip_text_four);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//组合数据
				resultText = editTextOne.getText().toString().trim()+"&&a"+
						editTextTwo.getText().toString().trim()+"&&a"+
						editTextThree.getText().toString().trim()+"&&a"+
						editTextFour.getText().toString().trim();
				commitData();
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
        //填充数据
		if(null!=MyApplication.getInstance().getUserInfo()){
			resultText= MyApplication.getInstance().getUserInfo().getOBJECT().getUserDescription();
			if(!TextUtils.isEmpty(resultText)&&resultText.contains("&&a")){
				//数据格式确定，不需要循环付值
				String data[] = resultText.split("&&a");
				if(data.length > 0 && null!=data[0]&&!TextUtils.isEmpty(data[0])){
					editTextOne.setText(data[0]);
				}
				if(data.length > 1 && null!=data[1]&&!TextUtils.isEmpty(data[1])){
					editTextTwo.setText(data[1]);
				}
				if(data.length > 2 && null!=data[2]&&!TextUtils.isEmpty(data[2])){
					editTextThree.setText(data[2]);
				}
				if(data.length > 3 && null!=data[3]&&!TextUtils.isEmpty(data[3])){
					editTextFour.setText(data[3]);
				}
			}
			
		}
	}

	private void commitData(){
		if(null!=MyApplication.getInstance().getUserInfo()){
			MyApplication.getInstance().getUserInfo().getOBJECT().setUserDescription(resultText);
		}
		
		// 修改关于我
		RequestParams params = new RequestParams();
		params.put("type", "7");
		params.put("content", resultText);
		APPRestClient.post(this, ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
					@Override
					public void onSuccess(BaseEntity result) {
                      finish();
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
