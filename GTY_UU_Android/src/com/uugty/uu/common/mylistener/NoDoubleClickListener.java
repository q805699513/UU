package com.uugty.uu.common.mylistener;

import java.util.Calendar;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class NoDoubleClickListener implements OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        } 
    }   
    
    public void onNoDoubleClick(View view) {
		// TODO Auto-generated method stub

	}
}


