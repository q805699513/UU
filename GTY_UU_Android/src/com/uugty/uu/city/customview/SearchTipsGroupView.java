package com.uugty.uu.city.customview;

import android.app.Activity;  
import android.content.Context;  
import android.util.AttributeSet;  
import android.util.DisplayMetrics;  
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.ImageView;
import android.widget.LinearLayout;  
import android.widget.TextView;   
 
import java.util.List;  

import com.uugty.uu.R;
   
public class SearchTipsGroupView extends LinearLayout {  
  
    private Context context;  
  
    public SearchTipsGroupView(Context context) {  
        super(context);  
        this.context = context;  
        setOrientation(VERTICAL);//设置方向  
    }  
  
    public SearchTipsGroupView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.context = context;  
        setOrientation(VERTICAL);//设置方向  
    }  
  
    public SearchTipsGroupView(Context context, AttributeSet attrs, int defStyleAttr) {  
        super(context, attrs, defStyleAttr);  
        this.context = context;  
        setOrientation(VERTICAL);//设置方向  
    }  
  
  
    /** 
     * 外部接口调用 
     * 
     * @param items 
     * @param onItemClick 
     */  
    public void initViews(String items[], final OnItemClick onItemClick) {  
        int length = dp2px(24);//一行加载item 的宽度  
  
        LinearLayout layout = null;  
  
        LayoutParams layoutLp = null;  
  
        boolean isNewLine = true;//是否换行  
  
        int screenWidth = getScreenWidth();//屏幕的宽度  
  
        int size = items.length;  
        for (int i = 0; i < size; i++) {//便利items  
            if (isNewLine) {//是否开启新的一行  
                layout = new LinearLayout(context);  
                layout.setOrientation(HORIZONTAL);  
                layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  
                layoutLp.topMargin = 10;  
            }  
  
            View view = LayoutInflater.from(context).inflate(R.layout.label_item_textview, null);  
            LinearLayout itemLin= (LinearLayout) view.findViewById(R.id.label_item_linner);  
            TextView itemView = (TextView) view.findViewById(R.id.text);  
            ImageView itemImg = (ImageView) view.findViewById(R.id.label_delete);  
            itemView.setText(items[i]);  
  
            final int j = i;  
            itemImg.setOnClickListener(new OnClickListener() {//给每个item设置点击事件  
                @Override  
                public void onClick(View v) {  
                    if (null != onItemClick) {  
                        onItemClick.onClick(j);  
                    }  
                }  
            });  
  
            //设置item的参数  
            LayoutParams itemLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  
            itemLp.leftMargin = 10;  
  
            //得到当前行的长度  
            length += 10 + getViewWidth(itemLin);  
            if (length > screenWidth) {//当前行的长度大于屏幕宽度则换行  
                length = dp2px(24);  
                addView(layout, layoutLp);  
                isNewLine = true;  
                i--;  
            } else {//否则添加到当前行  
                isNewLine = false;  
                layout.addView(view, itemLp);  
            }
        }  
        addView(layout, layoutLp);  
    }  
  
    /** 
     * @param items 
     * @param onItemClick 
     */  
    public void initViews(List<String> items, OnItemClick onItemClick) {  
        initViews((String[]) items.toArray(), onItemClick);  
    }  
  
    /** 
     * 得到手机屏幕的宽度 
     * 
     * @return 
     */  
    private int getScreenWidth() {  
        DisplayMetrics dm = new DisplayMetrics();  
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);  
        return dm.widthPixels;  
    }  
  
    /** 
     * 得到view控件的宽度 
     * 
     * @param view 
     * @return 
     */  
    private int getViewWidth(View view) {  
        int w = View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED);  
        int h = View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED);  
        view.measure(w, h);  
        return view.getMeasuredWidth();  
    }  
    
    /**
     * 换算 dp
     */
    private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}  