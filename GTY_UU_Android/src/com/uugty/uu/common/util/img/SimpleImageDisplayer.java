package com.uugty.uu.common.util.img;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class SimpleImageDisplayer implements BitmapDisplayer {

	private int targetWidth;

	public SimpleImageDisplayer(int targetWidth) {
		this.targetWidth = targetWidth;
	}
	
	@Override
	public void display(Bitmap bitmap, ImageAware imageView, LoadedFrom arg2) {
		// TODO Auto-generated method stub
		if (bitmap != null) {
			bitmap = ImageUtils.resizeImageByWidth(bitmap, targetWidth);
		}
		imageView.setImageBitmap(bitmap);
	}

}
