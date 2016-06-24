package com.uugty.uu.common.myview.picture;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.uugty.uu.common.asynhttp.service.ServiceCode;

/**
 * @author zhy
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 */
public class ClipImageBorderView extends View
{
	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;
	/**
	 * 垂直方向与View的边距
	 */
	private int mVerticalPadding;
	/**
	 * 绘制的矩形的宽度
	 */
	private int mWidth;
	/**
	 * 边框的颜色，默认为白色
	 */
	private int mBorderColor = Color.parseColor("#FFFFFF");
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 1;

	private Paint mPaint;
	private int mRadios;
	private boolean isCircle;
	
	public void setCircle(boolean isCircle){
		this.isCircle = isCircle;
	}

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//mPaint.setAlpha(1);
		// 计算矩形区域的宽度
		mWidth = getWidth() - 2 * mHorizontalPadding;
		// 计算距离屏幕垂直边界 的边距
		//mVerticalPadding = (getHeight() - mWidth) / 2;
		if(isCircle){
			mVerticalPadding = (int) ((getHeight() - mWidth) / 2);
		}else{
			mVerticalPadding = (int) ((getHeight() - mWidth*ServiceCode.FIT_LINE) / 2);//1.3
		}
		mPaint.setColor(Color.parseColor("#393939"));
		mPaint.setStyle(Style.FILL);
		// 绘制左边1
		//canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
		// 绘制右边2
		//canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
			//	getHeight(), mPaint);
		// 绘制上边3
		if(!isCircle){
			
		canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
				mVerticalPadding, mPaint);
		// 绘制下边4
		canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
				getWidth() - mHorizontalPadding, getHeight(), mPaint);
		// 绘制外边框
		}else{
			mRadios = (getWidth()-mHorizontalPadding*2)/2;
			canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
					mVerticalPadding, mPaint);
			// 绘制下边4
			canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding-1,
					getWidth() - mHorizontalPadding, getHeight(), mPaint);
			// 绘制外边框
			/*canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
					- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);*/
			/*mPaint.setStrokeWidth(1);
			mPaint.setStyle(Style.STROKE);
			*/
			/*int i=0;
			int mRadios2=mRadios*mRadios;
			int arg0=mHorizontalPadding+mRadios*2;
			int arg1=mVerticalPadding+mRadios*2;
			int arg2=mVerticalPadding+mRadios;
			int arg3 = mHorizontalPadding+mRadios;
			List list = new ArrayList();
			System.err.println(mVerticalPadding+"-"+arg1);
			for(int x=mHorizontalPadding;x<=arg0;x++)
				for(int y=mVerticalPadding;y<=arg1;y++){
					if((x-arg3)*(x-arg3)+(y-arg2)*(y-arg2)>mRadios2){
						list.add(x);
						list.add(y);
					}
				}
			float[] f=new float[list.size()];
			for(i=0;i<list.size();i++){
				f[i] = (int) list.get(i);
			}*/
			//list.clear();
			//list=null;
			//mPaint.setColor(Color.parseColor("#393939"));
			//mPaint.setStyle(Style.STROKE);
			/*AvoidXfermode avoid = new AvoidXfermode(Color.BLUE, 10, AvoidXfermode.Mode.AVOID); 
			mPaint.setXfermode(avoid);
			
			canvas.drawRect(mHorizontalPadding,  mVerticalPadding,
					getWidth() - mHorizontalPadding, mVerticalPadding+2*mRadios, mPaint);*/
			
			
			mPaint.setAntiAlias(true);
			
			mPaint.setStrokeWidth(0);
			
			
			int sc = canvas.saveLayer(mHorizontalPadding,  mVerticalPadding,
					getWidth() - mHorizontalPadding, mVerticalPadding+2*mRadios, null, Canvas.ALL_SAVE_FLAG);
			 // 先绘制dis目标图
			canvas.drawCircle(mHorizontalPadding+mRadios, mVerticalPadding+mRadios, mRadios, mPaint);
			 // 设置混合模式 （只在源图像和目标图像相交的地方绘制目标图像）
			mPaint.setXfermode(new PorterDuffXfermode(Mode.XOR));
			 // 再绘制src源图
			canvas.drawRect(mHorizontalPadding,  mVerticalPadding,
					getWidth() - mHorizontalPadding, mVerticalPadding+2*mRadios, mPaint);
			 // 还原混合模式
			mPaint.setXfermode(null);
			 // 还原画布
			canvas.restoreToCount(sc);
			
			
			//mPaint1.setColor(Color.TRANSPARENT);
			//mPaint1.setStyle(Style.FILL);
			//mPaint1.setAlpha(150); 
			
			//mPaint1.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
			
			//
			//mPaint1.setStyle(Style.FILL);
			
			/*canvas.drawRect(mHorizontalPadding,  mVerticalPadding,
					getWidth() - mHorizontalPadding, mVerticalPadding+2*mRadios, mPaint1);*/
			//canvas.drawPoints(f, mPaint);
			//f=null;
			//mPaint.setColor(mBorderColor);

			//mPaint.setStrokeWidth(mBorderWidth);
			//mPaint.setStyle(Style.STROKE);
			/*canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
					- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);*/
			//canvas.drawCircle(mRadios, mVerticalPadding+mRadios, mRadios, mPaint);
			
		}
	}

	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
		
	}

}
