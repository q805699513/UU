package com.uugty.uu.setup;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.CustomToast;

public class FeedbookActivity extends BaseActivity implements OnClickListener{

	private LinearLayout imag_back;
	private RelativeLayout send;
	private EditText send_content;
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_set_feedbook;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		imag_back=(LinearLayout) findViewById(R.id.tabar_back);
		send=(RelativeLayout) findViewById(R.id.container_send);
		send_content=(EditText) findViewById(R.id.feedbook_edit);
	}
	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		imag_back.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imag_back.setClickable(true);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tabar_back:
			imag_back.setClickable(false);
			finish();
			break;
		case R.id.container_send:
			String content=send_content.getText().toString().toString();
			if(content.equals("")){				
				CustomToast.makeText(ctx, 0, "不能为空", 300).show();
			}else{
				CustomToast.makeText(ctx, 0, "吐槽成功~", 300).show();
				finish();
			}
			break;
		default:
			break;
		}
	}

}
