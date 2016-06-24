package com.uugty.uu.common.myview.picture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * 
 * @author zhy
 * 
 */
public class ClipImageLayout extends RelativeLayout {

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;
	

	/**
	 * 杩欓噷娴嬭瘯锛岀洿鎺ュ啓姝讳簡澶у皬锛岀湡姝ｄ娇鐢ㄨ繃绋嬩腑锛屽彲浠ユ彁鍙栦负鑷畾涔夊睘鎬�
	 */
	private int mHorizontalPadding = 0;

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);
		
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		/**
		 * 杩欓噷娴嬭瘯锛岀洿鎺ュ啓姝讳簡鍥剧墖锛岀湡姝ｄ娇鐢ㄨ繃绋嬩腑锛屽彲浠ユ彁鍙栦负鑷畾涔夊睘鎬�
		 */
		/*
		 * mZoomImageView.setImageDrawable(getResources().getDrawable(
		 * R.drawable.a));
		 */

		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		// 璁＄畻padding鐨刾x
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}
public void setIsCircle(boolean isCircle){
	mClipImageView.setCircle(isCircle);
	mZoomImageView.setIsCircle(isCircle);
}
	/**
	 * 瀵瑰鍏竷璁剧疆杈硅窛鐨勬柟娉�,鍗曚綅涓篸p
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;
	}

	/**
	 * 瑁佸垏鍥剧墖
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mZoomImageView.clip();
	}

	public void setPic(String url) {
		try {

			mZoomImageView.setImageDrawable(resizeImage2(url, getWidth(),
					getHeight()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length>50) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
	
	public static Drawable resizeImage2(String path, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inJustDecodeBounds = true;// 涓嶅姞杞絙itmap鍒板唴瀛樹腑
		BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;

		width =480;
		height = 800;
		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			options.inSampleSize = sampleSize;
		}

		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(path, options);
		//bm = compressImage(bm);
		int degree = getBitmapDegree(path);
		bm = rotateBitmapByDegree(bm, degree);
		return new BitmapDrawable(bm);
	}
	private static int getBitmapDegree(String path) {

	    int degree = 0;

	    try {

	        // 从指定路径下读取图片，并获取其EXIF信息

	        ExifInterface exifInterface = new ExifInterface(path);

	        // 获取图片的旋转信息

	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,

	                ExifInterface.ORIENTATION_NORMAL);

	        switch (orientation) {

	        case ExifInterface.ORIENTATION_ROTATE_90:

	            degree = 90;

	            break;

	        case ExifInterface.ORIENTATION_ROTATE_180:

	            degree = 180;

	            break;

	        case ExifInterface.ORIENTATION_ROTATE_270:

	            degree = 270;

	            break;

	        }

	    } catch (IOException e) {

	        e.printStackTrace();

	    }

	    return degree;

	}
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {

	    Bitmap returnBm = null;

	  

	    // 根据旋转角度，生成旋转矩阵

	    Matrix matrix = new Matrix();

	    matrix.postRotate(degree);

	    try {

	        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片

	        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

	    } catch (OutOfMemoryError e) {

	    }

	    if (returnBm == null) {

	        returnBm = bm;

	    }

	    if (bm != returnBm) {

	        bm.recycle();

	    }

	    return returnBm;

	}
}
