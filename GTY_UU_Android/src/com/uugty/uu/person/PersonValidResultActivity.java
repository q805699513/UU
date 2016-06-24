package com.uugty.uu.person;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;

public class PersonValidResultActivity extends BaseActivity implements OnClickListener{

	private TopBackView topBack;
	// 验证类型
	private String picType="idcard";
	// 上传结果
	private String result;
	//图片路径
	private String picPath;
	//结果图片
	private ImageView resultImage;
    private TextView resultText,resultDes;
    private Button resultBtn;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_valid_result;
	}

	@Override
	protected void initGui() {
		topBack= (TopBackView) findViewById(R.id.person_valid_resut_topback);
		topBack.setTitle("身份证");
		resultImage = (ImageView) findViewById(R.id.person_valid_resut_system_photo);
		resultText = (TextView) findViewById(R.id.person_valid_resut_text);
		resultDes = (TextView) findViewById(R.id.person_valid_result_descrition);
		resultBtn = (Button) findViewById(R.id.person_valid_result_btn);
		Intent intent = getIntent();
		result = intent.getStringExtra("STATUS");
		picType = intent.getStringExtra("verifyItem");
		picPath =intent.getStringExtra("picPath");
		
        if(result.equals("FAILE")){
        	resultImage.setImageResource(R.drawable.person_valid_result_faile);
        	resultText.setText("上传失败");
        	resultDes.setText("很遗憾您的证件照片上传失败");
        	resultBtn.setText("再试一次");
        	if(picType.equals("mobile")){
        		resultImage.setImageResource(R.drawable.person_valid_result_fail_mobil);
        		resultText.setText("验证失败");
        		resultDes.setText("很遗憾您的手机号码验证失败");
        	}
        }else{
        	if(picType.equals("mobile")){
        		resultImage.setImageResource(R.drawable.person_valid_result_sucess_mobil);
        		resultText.setText("提交成功");
        		resultDes.setText("您的手机号码验证请求提交成功!");
        	}
        }
        if (picType.equals("education")) {
			topBack.setTitle("学历证");
		}
		if (picType.equals("legally")) {
			topBack.setTitle("导游证");
			
		}
		if (picType.equals("car")) {
			topBack.setTitle("车");
		}
		if (picType.equals("mobile")) {
			topBack.setTitle("手机号");
		}
		if(result.equals("SUCCESS")){
			//跳转编辑界面,修改状态
			//修改MyApplication里的内容
			if(picType.equals("idcard")){
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserIdValidate("1");
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserIdentity(picPath);
			}
			if(picType.equals("education")){
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserCertificateValidate("1");
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserCertificate(picPath);
			}
			if(picType.equals("legally")){
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserTourValidate("1");
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserTourCard(picPath);
			}
			if(picType.equals("car")){
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserCarValidate("1");
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserCar(picPath);
			}
			if(picType.equals("mobile")){
				MyApplication.getInstance().getUserInfo().getOBJECT().setUserTelValidate("1");
				//MyApplication.getInstance().getUserInfo().getOBJECT().setUserCar(picPath);
			}
		}
	}

	@Override
	protected void initAction() {
		resultBtn.setOnClickListener(this);

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
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.person_valid_result_btn:
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.person.PersonPhotoVeriActivity");
			if(result.equals("SUCCESS")){
				//跳转编辑界面,修改状态
				//修改MyApplication里的内容
				if(picType.equals("idcard")){
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserIdValidate("1");
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserIdentity(picPath);
				}
				if(picType.equals("education")){
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserCertificateValidate("1");
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserCertificate(picPath);
				}
				if(picType.equals("legally")){
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserTourValidate("1");
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserTourCard(picPath);
				}
				if(picType.equals("car")){
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserCarValidate("1");
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserCar(picPath);
				}
				intent.setClass(PersonValidResultActivity.this, PersonCompileActivity.class);
				if(picType.equals("mobile")){
					MyApplication.getInstance().getUserInfo().getOBJECT().setUserTelValidate("1");
					intent.setClass(PersonValidResultActivity.this, PersonCompileActivity.class);
				}
			}else{
				//跳转上传界面
				intent.putExtra("verifyItem", picType);
				intent.setClass(PersonValidResultActivity.this, PersonCompileActivity.class);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.person.PersonPhotoVeriActivity");
		}
		return super.onKeyDown(keyCode, event);
	}

}
