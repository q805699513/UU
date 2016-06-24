package com.uugty.uu.com.find;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.order.UUComfirmFragment;

public class CollectActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	private TopBackView titleView;
	private RadioButton serviceRoRadioBtn, invitationRadioBtn;
	private RadioGroup group;
	private ServiceCollectFragment serviceCollect;
	private InvitationCollectFragment invitationCollect;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_mycollect;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.collect_titile_top);
		titleView.setTitle("我的收藏");
		serviceRoRadioBtn=(RadioButton) findViewById(R.id.serviceRoRadioBtn);
		invitationRadioBtn=(RadioButton) findViewById(R.id.invitationRadioBtn);
		group = (RadioGroup) findViewById(R.id.group);
		
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		group.setOnCheckedChangeListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((RadioButton) group.findViewById(R.id.serviceRoRadioBtn))
						.setChecked(true);
			}
		}, 200);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		hideFragments(transaction);
		Resources res = this.getResources();
		Drawable fragment_img = res
				.getDrawable(R.drawable.order_fragment_chang_image);
		switch (checkedId) {
		case R.id.serviceRoRadioBtn:
			fragment_img.setBounds(1, 1, serviceRoRadioBtn.getWidth() / 3 * 2,
					5);
			serviceRoRadioBtn.setCompoundDrawables(null, null, null,
					fragment_img);
			serviceRoRadioBtn.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			invitationRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			invitationRadioBtn.setTextColor(Color.parseColor("#666666"));
			if (serviceCollect == null) {
				serviceCollect = new ServiceCollectFragment();
				transaction.add(R.id.order_list_layout, serviceCollect);
			} else {
				transaction.show(serviceCollect);
			}
			transaction.commit();
			break;
		case R.id.invitationRadioBtn:
			fragment_img.setBounds(1, 1, serviceRoRadioBtn.getWidth() / 3 * 2,
					5);
			invitationRadioBtn.setCompoundDrawables(null, null, null,
					fragment_img);
			invitationRadioBtn.setTextColor(getResources().getColor(
					R.color.order_status_text_color));
			serviceRoRadioBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			serviceRoRadioBtn.setTextColor(Color.parseColor("#666666"));
			if (invitationCollect == null) {
				invitationCollect = new InvitationCollectFragment();
				transaction.add(R.id.order_list_layout, invitationCollect);
			} else {
				transaction.show(invitationCollect);
			}
			transaction.commit();
			break;

		default:
			break;
		}
	}
	private void hideFragments(FragmentTransaction transaction) {
		if (serviceCollect != null) {
			transaction.hide(serviceCollect);
		}
		if (invitationCollect != null) {
			transaction.hide(invitationCollect);
		}
	}
}
