package com.uugty.uu.guide;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;

public class LeadPageActivity extends BaseActivity {
	private Button btn;
	private TextView leadPages_text;
	private ImageView leadPages_img;
	private String type;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.consult_lead_in_pages;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null!=getIntent()){
			type=getIntent().getStringExtra("type");
		}
		btn = (Button) findViewById(R.id.leadPages_button);
		leadPages_text = (TextView) findViewById(R.id.leadPages_text);
		leadPages_img = (ImageView) findViewById(R.id.leadPages_img);
		if(type.equals("咨询")){
			leadPages_text.setText("需要好玩的好吃的，赶紧勾搭个当地向导吧！");
			leadPages_img.setImageResource(R.drawable.consult_leadpage_img);
		}else if(type.equals("定制")){
			leadPages_text.setText("说一说你的旅游愿望，让那些本地小u为你的旅行出谋划策，定制你的自由之旅");
			leadPages_img.setImageResource(R.drawable.consult_leadpage_custom_img);
		}else if(type.equals("特色")){
			leadPages_text.setText("那些当地的好吃好玩的统统在此，深入当地人的生活，感受当地人的风土人情，听听一个城市的故事");			
			leadPages_img.setImageResource(R.drawable.consult_leadpage_guide_img);
		}else if(type.equals("体验")){
			leadPages_text.setText("精选各地的好玩新奇的活动，优中选优让你的旅行无忧，精中选精旅行更精彩");
			leadPages_img.setImageResource(R.drawable.consult_leadpage_experience_img);
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

}
