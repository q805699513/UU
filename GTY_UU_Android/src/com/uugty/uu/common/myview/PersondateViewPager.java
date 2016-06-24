package com.uugty.uu.common.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class PersondateViewPager extends ViewPager {

	public PersondateViewPager(Context context) {
		super(context);
	}

	public PersondateViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int height = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (getChildCount() == 3
					&& child.getClass().getName()
							.equals("android.widget.RelativeLayout")) {
				// 点击中间项
				child.measure(widthMeasureSpec,
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				int h = child.getMeasuredHeight();
				height = h;

				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
						MeasureSpec.EXACTLY);

				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			} else if(getChildCount() == 2
					&& child.getClass().getName()
					.equals("android.widget.LinearLayout")){
				// 点击第3项
				child.measure(widthMeasureSpec,
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				int h = child.getMeasuredHeight();
				height = h;

				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
						MeasureSpec.EXACTLY);

				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			}else {
				int h = child.getMeasuredHeight();
				if (h > height)
					height = h;
			}

		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
