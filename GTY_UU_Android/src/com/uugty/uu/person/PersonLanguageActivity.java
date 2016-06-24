package com.uugty.uu.person;

import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.login.LoginActivity;

public class PersonLanguageActivity extends BaseActivity{

	private RadioGroup mRadioGroup;
	private String result;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_language;
	}

	@Override
	protected void initGui() {
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PersonLanguageActivity.class.getName());
			intent.setClass(PersonLanguageActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		result = MyApplication.getInstance().getUserInfo().getOBJECT().getUserLanguage();
		}
		mRadioGroup = (RadioGroup) findViewById(R.id.person_language_group);
		int count = mRadioGroup.getChildCount();
		for(int i=0;i<count;i++){
			View o = mRadioGroup.getChildAt(i);
			if(o instanceof RadioButton){
				if(((RadioButton)o).getText().toString().trim().equals(result)){
					((RadioButton)o).setChecked(true);
				}
			}
		}
	}

	@Override
	protected void initAction() {
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 获取变更后的选中项的ID
				int radioButtonId = group.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) PersonLanguageActivity.this
						.findViewById(radioButtonId);
				// 更新文本内容，以符合选中项
				Intent mIntent = new Intent();
				mIntent.putExtra("language", rb.getText());
				// 设置结果，并进行传送
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

}
