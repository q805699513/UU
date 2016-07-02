package com.uugty.uu.com.rightview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;

public class RightMenuPopupWindow extends PopupWindow {
    private Activity context;
	private View mMenuView;
	
	public RightMenuPopupWindow(Activity context) {
		super(context);
		this.context=context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.right_menu_popup_window, null);

		TextView txt_one=(TextView) mMenuView.findViewById(R.id.right_menu_popup_card);//银行卡
		TextView txt_two=(TextView) mMenuView.findViewById(R.id.right_menu_transaction_record);//交易记录
		TextView txt_three=(TextView) mMenuView.findViewById(R.id.right_menu_pwd_manager);//密码管理
		txt_one.setOnClickListener(new BandCardLister());
		txt_two.setOnClickListener(new TransactionrecordsLister());
		txt_three.setOnClickListener(new PassWordLister());
		
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth((int) (w /3.5));
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		//this.setAnimationStyle(R.style.mystyle);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		/*mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.right_menu_lin).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});*/

	}
	
	//银行卡
		private class BandCardLister implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(context, BankcardActivity.class);
				context.startActivity(intent);
				dismiss();
			}
			
		}
		//交易记录
		private class TransactionrecordsLister implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(context, BillRecordActivity.class);
				context.startActivity(intent);
				dismiss();
			}
			
			
		}
		//密码管理
		private class PassWordLister implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				String userpaypassword =MyApplication.getInstance().getUserInfo().getOBJECT().getUserPayPassword();				
				if(!userpaypassword.equals("")){
					intent.setClass(context, PasswordActivity.class);					
				}else{
					intent.setClass(context, SetPayPwdActivity.class);						
					intent.putExtra("from", "MyPriceActivity");
				}
				context.startActivity(intent);
				dismiss();
			} 
			
			
		}

}
