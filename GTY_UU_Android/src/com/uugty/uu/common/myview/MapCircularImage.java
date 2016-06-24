package com.uugty.uu.common.myview;

import com.uugty.uu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//地图上带人图像的标记。此处可与个人图像做在一起，后期修改
public class MapCircularImage extends RelativeLayout{
	
	private View view;
	private ImageView bgImage; //图标背景
	private CircularImage cirCularImage; //圆形人图像
	
	public MapCircularImage(Context context){
		super(context);
		initView(context);
	}

	public MapCircularImage(Context context,AttributeSet attrs){
		super(context,attrs);
		initView(context);
	}
	
	public MapCircularImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context){
		view = LayoutInflater.from(context).inflate(R.layout.map_circul_head_iamge, this);
		bgImage = (ImageView) view.findViewById(R.id.map_circlue_head_bg);
		cirCularImage = (CircularImage) view.findViewById(R.id.map_circul_head_photo);
	}
	
	//更改背景图pain
	public void setBgImage(int res){
		bgImage.setImageResource(res);
	}
	
	//更改圆形人图像
	public void setHeadImage(int res){
		cirCularImage.setImageResource(res);
	}
}
