package com.uugty.uu.uuchat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class UUChatOKActivity extends BaseActivity implements OnClickListener {

	private ImageView img_Beck;
	private TextView mTextView;// 3秒跳�?
	private int count = 3;
	private String price, uu_message;
	private String redprice_id, red_id;
	private String avatar, userName, chat_id;
	private boolean callbackFlag; //点击回退，

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uuchat_tip_okview;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		img_Beck = (ImageView) findViewById(R.id.uuchat_tip_ok_back);
		mTextView = (TextView) findViewById(R.id.uuchat_tip_ok_topage);
		uu_message = getIntent().getStringExtra("message");
		price = getIntent().getStringExtra("price");
		red_id = getIntent().getStringExtra("red_id");
		avatar = getIntent().getStringExtra("avatar");
		userName = getIntent().getStringExtra("userName");
		chat_id = getIntent().getStringExtra("chat_id");
		new TimeThread().start();
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		img_Beck.setOnClickListener(this);
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
		case R.id.uuchat_tip_ok_back:
			Intent intent = new Intent();
			intent.putExtra("price", price);
			intent.putExtra("message", uu_message);
			intent.putExtra("red_id", red_id);
			intent.putExtra("userId", chat_id);
			intent.putExtra("avatar", avatar);
			intent.putExtra("userName", userName);
			intent.putExtra("toFrom", "UUChatOKActivity");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(UUChatOKActivity.this, ChatActivity.class);
			startActivity(intent);
			callbackFlag = true;
			finish();
			break;

		default:
			break;
		}
	}

	public class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					if (count == 1) {
						count = count - 1;
						msg.what = 1;
					} else {
						msg.what = 0;
						count = count - 1;
					}
					mHandler.sendMessage(msg);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (count >= 0&&!callbackFlag);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mTextView.setText(count + "秒后自动跳转");
				break;
			case 1:
				Intent intent = new Intent();
				/*
				 * //留言为空的话 增加默认的留言 Map<String, String> map=new HashMap<>();
				 * map.put("price", price); map.put("message", uu_message);
				 * map.put("red_id", red_id); Message msgs=new Message();
				 * msgs.obj=map;
				 */
				// 红包通知
				intent.putExtra("price", price);
				intent.putExtra("message", uu_message);
				intent.putExtra("red_id", red_id);
				intent.putExtra("userId", chat_id);
				intent.putExtra("avatar", avatar);
				intent.putExtra("userName", userName);
				intent.putExtra("toFrom", "UUChatOKActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UUChatOKActivity.this, ChatActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		}
	};

}
