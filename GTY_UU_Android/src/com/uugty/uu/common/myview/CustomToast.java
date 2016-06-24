package com.uugty.uu.common.myview;

import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast extends Toast{
	 private static Toast mToast;
	public CustomToast(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    /** 
     * 自定义Toast样式 
     */  
    public static Toast makeText(Context context, int resId, CharSequence text,  
            int duration) {  
        Toast result = new Toast(context);  
  
        // 获取LayoutInflater对象  
        LayoutInflater inflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        // 由layout文件创建一个View对象  
        View layout = inflater.inflate(R.layout.custom_toast, null);  
  
        // 实例化ImageView和TextView对象    
        TextView textView = (TextView) layout.findViewById(R.id.toast_message);  
  
        //这里我为了给大家展示就使用这个方面既能显示无图也能显示带图的toast  
//        if (resId == 0) {  
//            imageView.setVisibility(View.GONE);  
//        } else {  
//            imageView.setImageResource(resId);  
//        }  
          
        textView.setText(text);  
  
        result.setView(layout);  
        float itemHeight = MyApplication.getInstance().getResources().getDimension(R.dimen.toast_height);
        result.setGravity(Gravity.BOTTOM, 0, (int) itemHeight);  
        result.setDuration(duration);  
  
        return result;  
    }  
  
    public static void showToast(Context context, String content) {  
        mToast = Toast.makeText(context, content, 500); 
        mToast.show();  
    }  
}
