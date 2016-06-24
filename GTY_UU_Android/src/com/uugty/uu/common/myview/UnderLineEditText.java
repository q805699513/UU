package com.uugty.uu.common.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class UnderLineEditText extends EditText {

	private int lineColor;// 横线颜色
	private float lineWidth;// 横线宽度

	public UnderLineEditText(Context context) {
		super(context);
		lineWidth = 5f;// 宽度为2
	}

	public UnderLineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		lineWidth = 5f;// 宽度为2
	}

	public UnderLineEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		lineWidth = 5f;// 宽度为2
	}

	public UnderLineEditText(Context context, int color, float width) {
		super(context);

		// 设置颜色和横线宽度
		this.lineColor = color;
		this.lineWidth = width;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		// 创建画笔
		Paint mPaint = new Paint();
		mPaint.setStrokeWidth(lineWidth);
		mPaint.setStyle(Paint.Style.FILL);
		if(lineColor==0){
			mPaint.setColor(Color.BLACK);
		}else{
			mPaint.setColor(lineColor);
		}

		// 获取参数
		int w = this.getWidth();// 获取控件宽度
		int h = this.getHeight();// 获取控件高度
		int padB = this.getPaddingBottom();// 获取底部留白
		int padL = this.getPaddingLeft();// 获取左边留白
		int padR = this.getPaddingRight();// 获取右边留白
		float size = this.getTextSize() * 7 / 6;// 获取横栏间距（TextSize的默认值为18）
		/*
		 * 设置size的值是一个核心问题，size的值要适应不同的字体大小（不同大小字体的行距也不同）
		 * 经过多次尝试发现以字体大小的7/6倍作为横栏间距最合适
		 */
		int lines = (int) (h / size);// 获取行数

		// 从下向上画线
		for (int i = 0; i < lines; i++)
			canvas.drawLine(padL// startX
					, this.getHeight() - padB - size * i// startY
					, this.getWidth() - padR// endX
					, this.getHeight() - padB - size * i// endY
					, mPaint);
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int color) {
		this.lineColor = color;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float width) {
		this.lineWidth = width;
	}

}
