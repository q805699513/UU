package com.uugty.uu.loaderimg;

import java.util.List;

import com.uugty.uu.R;
import com.uugty.uu.loaderimg.ImageLoader.Type;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImgMainActivity extends Activity{

	private LinearLayout viwe;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img);
		viwe=(LinearLayout) findViewById(R.id.viwe);
		List<String> list=(List<String>) getIntent().getSerializableExtra("ss");
		for (int i = 0; i < list.size(); i++) {
			ImageView viewId=new ImageView(this);
			ImageLoader.getInstance(3,Type.LIFO).loadImage(list.get(i), viewId);
			viwe.addView(viewId);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
