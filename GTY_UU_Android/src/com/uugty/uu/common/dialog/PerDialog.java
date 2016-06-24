package com.uugty.uu.common.dialog;

import com.uugty.uu.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PerDialog {
	private String title;// 对话框标题
	private String mcontext;// 点击对话操作按钮
	private Context context;
	private OnWarnSheetSelected actionSheetSelected;

	/**
	 * 定义点击接口
	 */
	public interface OnWarnSheetSelected {
		void onWarnClick(int whichButton);
	}

	/**
	 * 初始化对话框 包括标题、确认按钮、上下文、点击委托事件对象
	 * 
	 * @param context
	 * @param title
	 * @param mcontext
	 * @param actionSheetSelected
	 */
	public PerDialog(Context context, String title, String mcontext,
			OnWarnSheetSelected actionSheetSelected) {
		this.context = context;
		this.title = title;
		this.mcontext = mcontext;
		this.actionSheetSelected = actionSheetSelected;
	}

	/**
	 * 显示对话框
	 * 
	 * @param context
	 * @param actionSheetSelected
	 * @return
	 */
	public Dialog showdialog() {
		final Dialog dlg = new Dialog(context, R.style.ActionSheet2);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.actionsheet, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		TextView mContent = (TextView) layout.findViewById(R.id.content);
		mContent.setText(mcontext);
		TextView titleTv = (TextView) layout.findViewById(R.id.title);
		titleTv.setText(title);
		TextView mCancel = (TextView) layout.findViewById(R.id.cancel);
		/**
		 * 点击删除按钮
		 */
		mContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				actionSheetSelected.onWarnClick(0);
				dlg.dismiss();
			}
		});
		/**
		 * 点击取消按钮
		 */
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionSheetSelected.onWarnClick(1);
				dlg.dismiss();
			}
		});
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
}
