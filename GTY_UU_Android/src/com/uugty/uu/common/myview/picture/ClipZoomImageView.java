package com.uugty.uu.common.myview.picture;

import com.uugty.uu.common.asynhttp.service.ServiceCode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * 
 * @author zhy
 * 
 */
public class ClipZoomImageView extends ImageView implements
		OnScaleGestureListener, OnTouchListener,
		ViewTreeObserver.OnGlobalLayoutListener

{

	private static final String TAG = ClipZoomImageView.class.getSimpleName();
	public static float SCALE_MAX = 4.0f;
	private static float SCALE_MID = 2.0f;
	private boolean isCircle;

	public void setIsCircle(boolean isCircle) {
		this.isCircle = isCircle;
	}

	/**
	 * 鍒濆鍖栨椂鐨勭缉鏀炬瘮渚嬶紝濡傛灉鍥剧墖瀹芥垨楂樺ぇ浜庡睆骞曪紝姝ゅ�煎皢灏忎簬0
	 */
	private float initScale = 1.0f;
	private boolean once = true;

	/**
	 * 鐢ㄤ簬瀛樻斁鐭╅樀鐨�9涓��
	 */
	private final float[] matrixValues = new float[9];

	/**
	 * 缂╂斁鐨勬墜鍔挎娴�
	 */
	private ScaleGestureDetector mScaleGestureDetector = null;
	private final Matrix mScaleMatrix = new Matrix();

	/**
	 * 鐢ㄤ簬鍙屽嚮妫�娴�
	 */
	private GestureDetector mGestureDetector;
	private boolean isAutoScale;

	private int mTouchSlop;

	private float mLastX;
	private float mLastY;

	private boolean isCanDrag;
	private int lastPointerCount;

	public ClipZoomImageView(Context context) {
		this(context, null);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setScaleType(ScaleType.MATRIX);
		mGestureDetector = new GestureDetector(context,
				new SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale == true)
							return true;

						float x = e.getX();
						float y = e.getY();
						if (getScale() < SCALE_MID) {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(SCALE_MID, x, y), 16);
							isAutoScale = true;
						} else {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(initScale, x, y), 16);
							isAutoScale = true;
						}

						return true;
					}
				});
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		this.setOnTouchListener(this);
	}

	/**
	 * 鑷姩缂╂斁鐨勪换鍔�
	 * 
	 * @author zhy
	 * 
	 */
	private class AutoScaleRunnable implements Runnable {
		static final float BIGGER = 1.07f;
		static final float SMALLER = 0.93f;
		private float mTargetScale;
		private float tmpScale;

		/**
		 * 缂╂斁鐨勪腑蹇�
		 */
		private float x;
		private float y;

		/**
		 * 浼犲叆鐩爣缂╂斁鍊硷紝鏍规嵁鐩爣鍊间笌褰撳墠鍊硷紝鍒ゆ柇搴旇鏀惧ぇ杩樻槸缂╁皬
		 * 
		 * @param targetScale
		 */
		public AutoScaleRunnable(float targetScale, float x, float y) {
			this.mTargetScale = targetScale;
			this.x = x;
			this.y = y;
			if (getScale() < mTargetScale) {
				tmpScale = BIGGER;
			} else {
				tmpScale = SMALLER;
			}

		}

		@Override
		public void run() {
			// 杩涜缂╂斁
			mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorder();
			setImageMatrix(mScaleMatrix);

			final float currentScale = getScale();
			// 濡傛灉鍊煎湪鍚堟硶鑼冨洿鍐咃紝缁х画缂╂斁
			if (((tmpScale > 1f) && (currentScale < mTargetScale))
					|| ((tmpScale < 1f) && (mTargetScale < currentScale))) {
				ClipZoomImageView.this.postDelayed(this, 16);
			} else
			// 璁剧疆涓虹洰鏍囩殑缂╂斁姣斾緥
			{
				final float deltaScale = mTargetScale / currentScale;
				mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
				checkBorder();
				setImageMatrix(mScaleMatrix);
				isAutoScale = false;
			}

		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;

		/**
		 * 缂╂斁鐨勮寖鍥存帶鍒�
		 */
		if ((scale < SCALE_MAX && scaleFactor > 1.0f)
				|| (scale > initScale && scaleFactor < 1.0f)) {
			/**
			 * 鏈�澶у�兼渶灏忓�煎垽鏂�
			 */
			if (scaleFactor * scale < initScale) {
				scaleFactor = initScale / scale;
			}
			if (scaleFactor * scale > SCALE_MAX) {
				scaleFactor = SCALE_MAX / scale;
			}
			/**
			 * 璁剧疆缂╂斁姣斾緥
			 */
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());
			checkBorder();
			setImageMatrix(mScaleMatrix);
		}
		return true;

	}

	/**
	 * 鏍规嵁褰撳墠鍥剧墖鐨凪atrix鑾峰緱鍥剧墖鐨勮寖鍥�
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event))
			return true;
		mScaleGestureDetector.onTouchEvent(event);

		float x = 0, y = 0;
		// 鎷垮埌瑙︽懜鐐圭殑涓暟
		final int pointerCount = event.getPointerCount();
		// 寰楀埌澶氫釜瑙︽懜鐐圭殑x涓巠鍧囧��
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x = x / pointerCount;
		y = y / pointerCount;

		/**
		 * 姣忓綋瑙︽懜鐐瑰彂鐢熷彉鍖栨椂锛岄噸缃甿LasX , mLastY
		 */
		if (pointerCount != lastPointerCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}

		lastPointerCount = pointerCount;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isCanDrag(dx, dy);
			}
			if (isCanDrag) {
				if (getDrawable() != null) {

					RectF rectF = getMatrixRectF();
					// 濡傛灉瀹藉害灏忎簬灞忓箷瀹藉害锛屽垯绂佹宸﹀彸绉诲姩
					if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
						dx = 0;
					}
					// 濡傛灉楂樺害灏忛洦灞忓箷楂樺害锛屽垯绂佹涓婁笅绉诲姩
					if (rectF.height() <= getHeight() - mVerticalPadding * 2) {
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);
					checkBorder();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			lastPointerCount = 0;
			break;
		}

		return true;
	}

	/**
	 * 鑾峰緱褰撳墠鐨勭缉鏀炬瘮渚�
	 * 
	 * @return
	 */
	public final float getScale() {
		mScaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 姘村钩鏂瑰悜涓嶸iew鐨勮竟璺�
	 */
	private int mHorizontalPadding;
	/**
	 * 鍨傜洿鏂瑰悜涓嶸iew鐨勮竟璺�
	 */
	private int mVerticalPadding;

	@Override
	public void onGlobalLayout() {
		if (once) {
			Drawable d = getDrawable();
			if (d == null)
				return;
			if (isCircle)
				mVerticalPadding = (int) ((getHeight() - getWidth())) / 2;// 1.3
			else
				mVerticalPadding = (int) ((getHeight() - getWidth()
						* ServiceCode.FIT_LINE)) / 2;// 1.3
			// 鍨傜洿鏂瑰悜鐨勮竟璺�
			// mVerticalPadding = (getHeight() - (getWidth() - 2 *
			// mHorizontalPadding)) / 2;
			int width = getWidth();
			int height = getHeight();
			// 鎷垮埌鍥剧墖鐨勫鍜岄珮
			/*int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();*/
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			//int dw = d.getBounds().width();
			//int dh = d.getBounds().height();
			
			float scale = 1.0f;
			float scaleW =  ((float)getWidth() - mHorizontalPadding * 2)/dw;
			float scaleH = ((float)getHeight() - mVerticalPadding * 2)/dh;
			
			scale = Math.max(scaleW,scaleH);

			initScale = scale;
			SCALE_MID = initScale ;
			SCALE_MAX = initScale * 4;
			mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
			mScaleMatrix.postScale(scale, scale, getWidth() / 2,
					getHeight() / 2);
			// 鍥剧墖绉诲姩鑷冲睆骞曚腑蹇�
			setImageMatrix(mScaleMatrix);
			once = false;
		}

	}

	/**
	 * 鍓垏鍥剧墖锛岃繑鍥炲壀鍒囧悗鐨刡itmap瀵硅薄
	 * 
	 * @return
	 */
	public Bitmap clip() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		if (isCircle){
			if(mVerticalPadding<0){
				mVerticalPadding=0;
			}
			return Bitmap.createBitmap(bitmap, mHorizontalPadding,
					mVerticalPadding, getWidth() - 2 * mHorizontalPadding,
					(int) ((getWidth() - 2 * mHorizontalPadding)));
		}else{
			if(mVerticalPadding<0){
				mVerticalPadding=0;
			}
			return Bitmap
					.createBitmap(
							bitmap,
							mHorizontalPadding,
							mVerticalPadding,
							getWidth() - 2 * mHorizontalPadding,
							(int) ((getWidth() - 2 * mHorizontalPadding) * ServiceCode.FIT_LINE));
		}
	}

	/**
	 * 杈圭晫妫�娴�
	 */
	private void checkBorder() {

		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();
		Log.e(TAG, "rect.height() =  " + rect.height()
				+ " , height - 2 * mVerticalPadding ="
				+ (height - 2 * mVerticalPadding));

		// 濡傛灉瀹芥垨楂樺ぇ浜庡睆骞曪紝鍒欐帶鍒惰寖鍥� ;
		// 杩欓噷鐨�0.001鏄洜涓虹簿搴︿涪澶变細浜х敓闂锛屼絾鏄宸竴鑸緢灏忥紝鎵�浠ユ垜浠洿鎺ュ姞浜嗕竴涓�0.01
		if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
			if (rect.left > mHorizontalPadding) {
				deltaX = -rect.left + mHorizontalPadding;
			}
			if (rect.right < width - mHorizontalPadding) {
				deltaX = width - mHorizontalPadding - rect.right;
			}
		}
		if (rect.height() + 0.01 >= height - 2 * mVerticalPadding) {
			if (rect.top > mVerticalPadding) {
				deltaY = -rect.top + mVerticalPadding;
			}
			if (rect.bottom < height - mVerticalPadding) {
				deltaY = height - mVerticalPadding - rect.bottom;
			}
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 鏄惁鏄嫋鍔ㄨ涓�
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	private boolean isCanDrag(float dx, float dy) {
		return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
	}

	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;
	}

}
