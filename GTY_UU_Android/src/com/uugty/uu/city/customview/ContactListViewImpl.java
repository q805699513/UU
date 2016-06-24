package com.uugty.uu.city.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

public class ContactListViewImpl extends ContactListView
{

	public ContactListViewImpl(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	@Override
//    /**
//     * 重写该方法，达到使ListView适应ScrollView的效果
//     */
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//        MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }

	public void createScroller()
	{

		mScroller = new IndexScroller(getContext(), this);

		mScroller.setAutoHide(autoHide);

		// style 1
		// mScroller.setShowIndexContainer(false);
		// mScroller.setIndexPaintColor(Color.argb(255, 49, 64, 91));

		// style 2
		mScroller.setShowIndexContainer(true);
		mScroller.setIndexPaintColor(Color.WHITE);

		if (autoHide)
			mScroller.hide();
		else
			mScroller.show();

	}
}
