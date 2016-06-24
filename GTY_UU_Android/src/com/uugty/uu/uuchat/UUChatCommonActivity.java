package com.uugty.uu.uuchat;

import java.util.Timer;
import java.util.TimerTask;

import android.text.TextUtils;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.util.ActivityCollector;

public class UUChatCommonActivity extends BaseActivity implements
		OnClickListener {

	private EditText uuchat_price;// 输入金额
	private EmojiEdite uuchat_speak;
	private Button uuchat_moner;// 支付
	private LinearLayout tip_back;//
	private TextView price_content;// 红包金额
	private String leave_message;
	private String toChatavatar, toChatUsername, chtid;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.uuchat_commontip;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		uuchat_price = (EditText) findViewById(R.id.uuhcat_tipmonery);
		// 弹出键盘
		uuchat_price.setFocusable(true);
		uuchat_price.setFocusableInTouchMode(true);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) uuchat_price
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(uuchat_price, 0);
			}
		}, 500);
		uuchat_speak = (EmojiEdite) findViewById(R.id.uuchat_leave_message);
		uuchat_moner = (Button) findViewById(R.id.uuchat_payment);
		tip_back = (LinearLayout) findViewById(R.id.tabar_back);
		price_content = (TextView) findViewById(R.id.price_content);
		toChatavatar = getIntent().getStringExtra("avatar");
		toChatUsername = getIntent().getStringExtra("userName");
		chtid = getIntent().getStringExtra("uuchat_id");
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		tip_back.setOnClickListener(this);
		uuchat_moner.setOnClickListener(this);
		setPricePoint(uuchat_price);
	}

	class EditChangedListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() > 0) {
				uuchat_moner.setEnabled(true);
				uuchat_moner
						.setBackgroundResource(R.drawable.wallet_commit_bg_red_select_shape);
				
			} else {
				uuchat_moner.setEnabled(false);
				uuchat_moner
						.setBackgroundResource(R.drawable.wallet_commit_bg_red_shape);
				
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tip_back.setClickable(true);
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
		case R.id.tabar_back:
			finish();
			tip_back.setClickable(false);
			break;
		case R.id.uuchat_payment:
			if (uuchat_price.getText().toString() == null
					|| uuchat_price.getText().toString().length() == 0) {
				CustomToast.makeText(this, 0, "请输入小费金额", 200).show();
			} else if (Double.valueOf(uuchat_price.getText().toString()) < 0.01) {
				CustomToast.makeText(this, 0, "普通红包金额不能小于0.01", 200).show();
			} else if (Double.valueOf(uuchat_price.getText().toString()) > 1000) {
				CustomToast.makeText(this, 0, "普通红包金额不能超过1000", 200).show();
			}else{
				leave_message = uuchat_speak.getText().toString().toString();

				if (TextUtils.isEmpty(leave_message)) {
					leave_message = "大吉大利,恭喜发财！";
				}
				ActivityCollector
				.removeSpecifiedActivity("com.uugty.uu.uuchat.UUTipActivity");
				Intent intnet_new = new Intent();
				intnet_new.putExtra("price", uuchat_price.getText().toString()
						.trim());
				intnet_new.putExtra("message", leave_message);
				intnet_new.putExtra("chat_id", chtid);
				intnet_new.putExtra("avatar", toChatavatar);
				intnet_new.putExtra("userName", toChatUsername);
				intnet_new.putExtra("pageFlag", "UUChatCommonActivity");
				intnet_new.setClass(this, UUChatPaypriceActivity.class);
				startActivity(intnet_new);
			}

			break;
		default:
			break;
		}
	}

	public void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					uuchat_moner.setEnabled(true);
					uuchat_moner
							.setBackgroundResource(R.drawable.wallet_commit_bg_red_select_shape);
					price_content.setText(uuchat_price.getText().toString()
							.trim());
				} else {
					uuchat_moner.setEnabled(false);
					uuchat_moner
							.setBackgroundResource(R.drawable.wallet_commit_bg_red_shape);
					price_content.setText("0.00");
				}
			}

		});

	}
}
