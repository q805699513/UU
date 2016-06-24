package com.uugty.uu.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.uugty.uu.R;

public class BalanDialog extends Dialog {

	public BalanDialog(Context context) {
		super(context);
	}

	public BalanDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private TextView confirm;
		private DialogInterface.OnClickListener positiveButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(
				DialogInterface.OnClickListener listener) {
			this.positiveButtonClickListener = listener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.PhoneDialog);
			View layout = inflater.inflate(R.layout.balan_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the confirm button
			confirm = (TextView) layout.findViewById(R.id.balan_dialog_btn);
			if (positiveButtonClickListener != null) {
				confirm.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						positiveButtonClickListener.onClick(dialog,
								DialogInterface.BUTTON_POSITIVE);
					}
				});

			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
