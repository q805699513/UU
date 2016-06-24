package com.uugty.uu.common.myview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageBitmap {

	/*
	 * 瀵瑰浘鐗囪繘琛岄�鏄庡害澶勭悊
	 */
	public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

		.getWidth(), sourceImg.getHeight());// 鑾峰緱鍥剧墖鐨凙RGB鍊�

		number = number * 255 / 100;

		for (int i = 0; i < argb.length; i++) {

			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

		}

		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

		.getHeight(), Config.ARGB_8888);

		return sourceImg;
	}
	
	/*
	 *
	 * 
	 * @param bitmap
	 *            浼犲叆Bitmap瀵硅薄
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 璁剧疆鐢荤瑪鏃犻敮榻�

		canvas.drawARGB(0, 0, 0, 0); // 濉厖鏁翠釜Canvas

		// 浠ヤ笅鏈変袱绉嶆柟娉曠敾鍦�drawRounRect鍜宒rawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 鐢诲渾瑙掔煩褰紝绗竴涓弬鏁颁负鍥惧舰鏄剧ず鍖哄煙锛岀浜屼釜鍙傛暟鍜岀涓変釜鍙傛暟鍒嗗埆鏄按骞冲渾瑙掑崐寰勫拰鍨傜洿鍦嗚鍗婂緞銆�
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 璁剧疆涓ゅ紶鍥剧墖鐩镐氦鏃剁殑妯″紡,鍙傝�http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 浠ode.SRC_IN妯″紡鍚堝苟bitmap鍜屽凡缁廳raw浜嗙殑Circle

		return output;
	}
}
