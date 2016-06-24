package com.uugty.uu.uuchat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class UUSmailGridview extends GridView{

	public UUSmailGridview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public UUSmailGridview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
