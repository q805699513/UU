package com.uugty.uu.common.myview;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

//圆形个人图像
public class CirculHeadImage extends RelativeLayout {

	private View view;
	private CircularImage cirCularImage;
	private ImageView backImageView;

	public void setScaleType(ScaleType scaleType) {
		this.cirCularImage.setScaleType(scaleType);
	}

	public CirculHeadImage(Context context) {
		super(context);
		initView(context);
	}

	public CirculHeadImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CirculHeadImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.circul_head_image,
				this);
		backImageView = (ImageView) findViewById(R.id.circul_head_backphoto);
		cirCularImage = (CircularImage) view
				.findViewById(R.id.circul_head_photo);

	}

	// 更改背景图片
	public void setBackPic(String res) {
		ImageLoader.getInstance().displayImage(res, backImageView);
	}

	// 更换图像照片 此处后期修改加载网络图片，还是加载本地图片
	public void setHeadPic(String res, String type) {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.no_default_head_img)
				.showImageForEmptyUri(R.drawable.no_default_head_img)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		if (type.equals("local")) {
			// 加载本地图片
			ImageLoader.getInstance().displayImage("file://" + res,
					cirCularImage, options);
		}
		if (type.equals("net")) {
			if (!TextUtils.isEmpty(res)) {
				ImageLoader.getInstance().displayImage(
						APPRestClient.SERVER_IP
								+ res.substring(0, res.indexOf(".")) + "_ya"
								+ res.substring(res.indexOf(".")),
						cirCularImage, options);

			}else{
				ImageLoader.getInstance().displayImage("drawable://"
						+ R.drawable.no_default_head_img, cirCularImage);
			}
		}
		if (type.equals("drawable")) {
			ImageLoader.getInstance().displayImage(res, cirCularImage, options);
		}
	}

	// 更换图像照片 此处后期修改加载网络图片，还是加载本地图片 头像默认图
	public void setNoHeadPic(String res, String type) {
		// 显示图片的配置
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.no_default_head_img)
				.showImageForEmptyUri(R.drawable.no_default_head_img)
				.showImageOnLoading(R.drawable.no_default_head_img)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		if (type.equals("local")) {

			// 加载本地图片
			ImageLoader.getInstance().displayImage("file://" + res,
					cirCularImage, options);
		}
		if (type.equals("net")) {
			if (!TextUtils.isEmpty(res)) {
				ImageLoader.getInstance().displayImage(
						APPRestClient.SERVER_IP
								+ res.substring(0, res.indexOf(".")) + "_ya"
								+ res.substring(res.indexOf(".")),
						cirCularImage, options);

			}
		}
		if (type.equals("drawable")) {
			ImageLoader.getInstance().displayImage(res, cirCularImage);
		}
	}

	public void setCirCularImageSize(int width, int height, int marginNum) {
		int mWidth = (int) (TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, width, getResources()
						.getDisplayMetrics()));
		int mHeight = (int) (TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, height, getResources()
						.getDisplayMetrics()));
		LayoutParams lp1 = (LayoutParams) backImageView.getLayoutParams();
		lp1.width = mWidth;
		lp1.height = mHeight;
		backImageView.setLayoutParams(lp1);
		backImageView.invalidate();
		marginNum = (int) (TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, marginNum, getResources()
						.getDisplayMetrics()));
		LayoutParams lp2 = (LayoutParams) cirCularImage.getLayoutParams();
		lp2.width = mWidth - marginNum;
		lp2.height = mHeight - marginNum;
		cirCularImage.setLayoutParams(lp2);
		cirCularImage.invalidate();
	}

}
