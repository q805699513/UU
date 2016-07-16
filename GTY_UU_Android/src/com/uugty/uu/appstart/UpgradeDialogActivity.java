package com.uugty.uu.appstart;

import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class UpgradeDialogActivity extends BaseActivity {
	private Button btn;
	private ImageView close;
	private String url;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_appupgrade_dialog;
	}

	@Override
	protected void initGui() {

		btn = (Button) findViewById(R.id.leadPages_button);
		close = (ImageView) findViewById(R.id.upgrade_close);

	}

	@Override
	protected void initAction() {
		if(getIntent() != null){
			url = getIntent().getStringExtra("url");
		}
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开浏览器下载
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(url);
				intent.setData(content_url);
				startActivity(intent);
				exit();
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UpgradeDialogActivity.this,AppStartActivity.class);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}

	@Override
	protected void initData() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(UpgradeDialogActivity.this,AppStartActivity.class);
			setResult(RESULT_OK,intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
