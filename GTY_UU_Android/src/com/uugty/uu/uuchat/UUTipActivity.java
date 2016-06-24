package com.uugty.uu.uuchat;

import java.text.DecimalFormat;
import java.util.Random;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class UUTipActivity extends BaseActivity implements OnClickListener {

	private Button tip_common, tip_random;
	private ImageView tip_back;
	private String content;
	AlertDialog.Builder builder;
	AlertDialog dialog;// 随机小费dialog

	View tipview;

	private String liuYan;
	private String uuchat_id, toChatavatar, toChatUsername;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uuchat_tip;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		tip_common = (Button) findViewById(R.id.tip_rcommon);
		tip_random = (Button) findViewById(R.id.tip_random);
		tip_back = (ImageView) findViewById(R.id.uuchat_tip_back);
		uuchat_id = getIntent().getStringExtra("uuchat_id");
		toChatavatar = getIntent().getStringExtra("avatar");
		toChatUsername = getIntent().getStringExtra("userName");
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		tip_common.setOnClickListener(this);
		tip_random.setOnClickListener(new RandomLister());
		tip_back.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tip_rcommon:
			intent.putExtra("uuchat_id", uuchat_id);
			intent.putExtra("avatar", toChatavatar);
			intent.putExtra("userName", toChatUsername);
			intent.setClass(this, UUChatCommonActivity.class);
			startActivity(intent);
			break;
		case R.id.uuchat_tip_back:
			finish();
			tip_back.setClickable(false);
			break;
		default:
			break;
		}

	}

	// 弹出随机红包
	private class RandomLister implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			testRandom1();
			LayoutInflater inflater = LayoutInflater.from(UUTipActivity.this);
			tipview = inflater.inflate(R.layout.dialog, null);
			ImageView img = (ImageView) tipview.findViewById(R.id.close);
			TextView txt_content = (TextView) tipview
					.findViewById(R.id.uuchat_price_tip);
			Button enjoy_button = (Button) tipview
					.findViewById(R.id.enjoy_button);
			final EditText liuYanContent = (EditText) tipview
					.findViewById(R.id.liuyan_content);

			if (Float.valueOf(content) > 1 && Float.valueOf(content) < 2) {
				liuYan = "赏你一包辣条";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 2 && Float.valueOf(content) < 3) {
				liuYan = "老冰棍拿走不谢";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 3 && Float.valueOf(content) < 4) {
				liuYan = "500万的彩票，晚上开奖";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 4 && Float.valueOf(content) < 5) {
				liuYan = "小苹果";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 5 && Float.valueOf(content) < 6) {
				liuYan = "月亮代表我的心";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 6 && Float.valueOf(content) < 7) {
				liuYan = "黑凤梨";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 7 && Float.valueOf(content) < 8) {
				liuYan = "哎呀，不错哦";
				liuYanContent.setText(liuYan);
			} else if (Float.valueOf(content) > 8 && Float.valueOf(content) < 9) {
				liuYan = "请你吃麻辣烫8块的";
				liuYanContent.setText(liuYan);
			} else {
				liuYan = "大吉大利，恭喜发财!";
				liuYanContent.setText(liuYan);
			}

			liuYanContent.setSelection(liuYanContent.getText().toString()
					.trim().length());

			builder = new Builder(UUTipActivity.this).setView(tipview);
			builder.create();
			dialog = builder.show();
			txt_content.setText(content);
			img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated metenjoy_buttonhod stub
					if (builder != null) {
						dialog.dismiss();
					}
				}
			});
			//
			enjoy_button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					liuYan = liuYanContent.getText().toString().trim();
					Intent intent = new Intent();
					String uuchat_id = getIntent().getStringExtra("uuchat_id");
					intent.putExtra("price", content);
					intent.putExtra("message", liuYan);
					intent.putExtra("chat_id", uuchat_id);
					intent.putExtra("avatar", toChatavatar);
					intent.putExtra("userName", toChatUsername);
					intent.putExtra("pageFlag", "UUTipActivity");
					intent.setClass(UUTipActivity.this,
							UUChatPaypriceActivity.class);
					startActivity(intent);
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
		}
	}

	// 产生随机数
	private void testRandom1() {
		//
		Random r = new Random();
		double y = r.nextDouble() * (10 - 1) + 1;
		DecimalFormat df = new DecimalFormat("#.00");// 保留2位小数
		// 随机产生的小费
		content = df.format(y);

	}

	//

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tip_back.setClickable(true);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
