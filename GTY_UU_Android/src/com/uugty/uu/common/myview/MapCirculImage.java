package com.uugty.uu.common.myview;


import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;

public class MapCirculImage extends RelativeLayout {

	private View view;
	private ImageView imageView1;
	private SimpleDraweeView circul;

	public MapCirculImage(Context context) {
		super(context);
		initView(context);

	}

	public MapCirculImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MapCirculImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.map_circul_head_iamge, this);
		imageView1 = (ImageView) view.findViewById(R.id.map_circlue_head_bg);
		circul = (SimpleDraweeView) view
				.findViewById(R.id.map_circul_head_photo);
	}

	public void setBackPic(String res) {
		// ImageLoader.getInstance().displayImage(res, imageView1);
		imageView1.setImageResource(R.drawable.map_guide_bg);
	}

	public void setAvatar(String avatar,String type) {
		if(type.equals("net")){
			circul.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ avatar.substring(0, avatar.indexOf(".")) + "_ya"
					+ avatar.substring(avatar.indexOf("."))));
		}else{
			circul.setImageURI(Uri.parse("res:///" + R.drawable.no_default_head_img));
			
		}
		
	}

}
