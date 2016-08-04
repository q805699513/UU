package com.uugty.uu.uuchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.easemob.chat.EMMessage;
import com.uugty.uu.R;
import com.uugty.uu.common.util.ActivityCollector;

public class ContextMenu extends Activity {

	private int position;
	private String isShare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		//复制文字的布局
		if (type == EMMessage.Type.TXT.ordinal()) {
			setContentView(R.layout.context_menu_for_text) ;
		}
		if(type == 1001) {
			setContentView(R.layout.context_menu_for_custom) ;
		}
		position = getIntent().getIntExtra("position", -1);
		isShare = getIntent().getStringExtra("isShare");
		ActivityCollector.addActivity(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	public void copy(View view){
		setResult(ChatActivity.RESULT_CODE_COPY, new Intent().putExtra("position", position));
		finish();
	}
	
	public void forward(View view){
		Intent i = new Intent();
		if(null != isShare && "true".equals(isShare)){
			i.putExtra("isShare","true");
			i.putExtra("position", position);
		}else{
			i.putExtra("position", position);
		}
		setResult(ChatActivity.RESULT_CODE_FORWARD, i);
		finish();
	}
}
