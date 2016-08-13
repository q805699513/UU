package com.uugty.uu.com.rightview.alipaywallet;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class AddAliPayActivity extends BaseActivity implements OnClickListener {

	private EditText cardNumEditText;
	private Button commitBtn;
	private String cardNo;
	private String number ="";
	private String name ="";
	private String bankId="";
	private LinearLayout right_back_add;

	protected int getContentLayout() {

		return R.layout.add_alipay_account;
	}

	@Override
	protected void initGui() {
		if(null != getIntent().getStringExtra("bankId")){
			number = getIntent().getStringExtra("number");
			name = getIntent().getStringExtra("name");
			bankId = getIntent().getStringExtra("bankId");
		}
		right_back_add = (LinearLayout) findViewById(R.id.tabar_back);
		cardNumEditText = (EditText) findViewById(R.id.add_alipay_card_num_edit);
		commitBtn = (Button) findViewById(R.id.add_alipay_card_btn);
		commitBtn.setEnabled(false);
		if(!number.equals("")){
			cardNumEditText.setText(number);
		}
		if (!number.equals("")) {
			commitBtn
					.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
			commitBtn.setEnabled(true);
		} else {
			commitBtn
					.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
			commitBtn.setEnabled(false);
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		right_back_add.setOnClickListener(this);
		commitBtn.setOnClickListener(this);

		cardNumEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				cardNo = s.toString();
				if (!cardNo.equals("")) {
					commitBtn
							.setBackgroundResource(R.drawable.price_drawcash_btn_bg);
					commitBtn.setEnabled(true);
				} else {
					commitBtn
							.setBackgroundResource(R.drawable.wallet_commit_bg_shape);
					commitBtn.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void initData() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		right_back_add.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			right_back_add.setClickable(false);
			break;
		case R.id.add_alipay_card_btn:
			Intent i = new Intent();
			if(!name.equals("")) {
				i.putExtra("name",name);
			}
			if(!bankId.equals("")){
				i.putExtra("bankId",bankId);
			}
			i.putExtra("alipayAccount", cardNumEditText.getText().toString());

			i.setClass(AddAliPayActivity.this,VerifyAliPayActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

}
