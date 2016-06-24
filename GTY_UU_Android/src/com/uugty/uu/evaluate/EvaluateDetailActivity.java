package com.uugty.uu.evaluate;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.uugty.uu.R;


public class EvaluateDetailActivity extends FragmentActivity{
	private RadioButton mefragmentbtn, fragmentbtn;
	private RadioGroup group_three;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_evaluatedetail);
		initview();
		
	}

	private void initview() {
		mefragmentbtn = (RadioButton) findViewById(R.id.order_list_mefragment);
		fragmentbtn = (RadioButton) findViewById(R.id.order_list_fragment);
		group_three = (RadioGroup) findViewById(R.id.group_three);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.layout_three, new MeEvaluateFragment()).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.layout_three, new MeEvaluateFragment()).commit();
		mefragmentbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fragment_chang_two);
		group_three.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.order_list_mefragment:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.layout_three, new MeEvaluateFragment()).commit();
					mefragmentbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fragment_chang_two);
					fragmentbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
					break;
				case R.id.order_list_fragment:
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.layout_three, new EvaluateMeFragment()).commit();
					mefragmentbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
					fragmentbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.fragment_chang_two);
					break;
				}
			}
		});
	}
}
