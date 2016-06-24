package com.uugty.uu.com.rightview;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class ChooseBankActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout chinaRel, abcRel, icbcRel, ccbRel, bcmRel, cmbRel,
			cebRel;
	private String fromType = "";
	private LinearLayout tab_back;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.choose_bank;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			fromType = getIntent().getStringExtra("type");
		}
		chinaRel = (RelativeLayout) findViewById(R.id.bank_china_rel);
		abcRel = (RelativeLayout) findViewById(R.id.bank_abc_rel);
		icbcRel = (RelativeLayout) findViewById(R.id.bank_icbc_rel);
		ccbRel = (RelativeLayout) findViewById(R.id.bank_ccb_rel);
		bcmRel = (RelativeLayout) findViewById(R.id.bank_bcm_rel);
		cmbRel = (RelativeLayout) findViewById(R.id.bank_cmb_rel);
		cebRel = (RelativeLayout) findViewById(R.id.bank_ceb_rel);
		tab_back = (LinearLayout) findViewById(R.id.tabar_back);
	}

	@Override
	protected void initAction() {
		chinaRel.setOnClickListener(this);
		abcRel.setOnClickListener(this);
		icbcRel.setOnClickListener(this);
		ccbRel.setOnClickListener(this);
		bcmRel.setOnClickListener(this);
		cmbRel.setOnClickListener(this);
		cebRel.setOnClickListener(this);
		tab_back.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tab_back.setClickable(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		Intent intent = new Intent();
		if (null != fromType && fromType.equals("cash")) {
			intent.putExtra("type", "cash");
		}

		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			tab_back.setClickable(false);
			break;
		case R.id.bank_china_rel:
			/*
			 * ImageLoader.getInstance().displayImage( "drawable://" +
			 * R.drawable.map_shout_confirm_btn, chinaRightImage);
			 */
			intent.putExtra("bankType", "1");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_abc_rel:
			intent.putExtra("bankType", "2");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_icbc_rel:
			intent.putExtra("bankType", "3");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_ccb_rel:
			intent.putExtra("bankType", "4");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_bcm_rel:
			intent.putExtra("bankType", "5");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_cmb_rel:
			intent.putExtra("bankType", "6");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		case R.id.bank_ceb_rel:
			intent.putExtra("bankType", "7");
			intent.setClass(this, ADDBankActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

}
