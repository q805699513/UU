package com.uugty.uu.setup;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.login.AgreementWebActivity;

public class UUHelpActivity extends BaseActivity implements OnClickListener {

	private LinearLayout imag_back;
	private RelativeLayout helpOne, helpTwo, helpThree, helpFour, helpFive,
			helpSix, helpSeven,help_eight;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_set_help;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		imag_back = (LinearLayout) findViewById(R.id.tabar_back);
		helpOne = (RelativeLayout) findViewById(R.id.help_one);
		helpTwo = (RelativeLayout) findViewById(R.id.help_two);
		helpThree = (RelativeLayout) findViewById(R.id.help_three);
		helpFour = (RelativeLayout) findViewById(R.id.help_four);
		helpFive = (RelativeLayout) findViewById(R.id.help_five);
		helpSix = (RelativeLayout) findViewById(R.id.help_six);
		helpSeven = (RelativeLayout) findViewById(R.id.help_seven);
		help_eight = (RelativeLayout) findViewById(R.id.help_eight);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		imag_back.setOnClickListener(this);
		helpOne.setOnClickListener(this);
		helpTwo.setOnClickListener(this);
		helpThree.setOnClickListener(this);
		helpFour.setOnClickListener(this);
		helpFive.setOnClickListener(this);
		helpSix.setOnClickListener(this);
		helpSeven.setOnClickListener(this);
		help_eight.setOnClickListener(this);
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
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
//			imag_back.setBackgroundResource(R.color.tab_back_color);
			imag_back.setClickable(false);
			break;
		case R.id.help_one:
			intent.putExtra("agreement", "useUU");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_two:
			intent.putExtra("agreement", "becomeGuide");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_three:
			intent.putExtra("agreement", "priceEnact");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_four:
			intent.putExtra("agreement", "cancleTour");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_five:
			intent.putExtra("agreement", "tourAccident");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_six:
			intent.putExtra("agreement", "guideDimss");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_seven:
			intent.putExtra("agreement", "applyForRefund");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
		case R.id.help_eight:
			intent.putExtra("agreement", "generalize");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
