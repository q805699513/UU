package com.uugty.uu.common.myview;

import java.io.File;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.entity.Util;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.map.PhoneDialog;

public class ReleaseRouteImageView extends LinearLayout implements
		OnClickListener, OnLongClickListener {
	private EmojiEdite editText;
	private View view;
	private SimpleDraweeView mImageView;
	private LinearLayout footLin;
	private Context context;
	private FrameLayout photoFram;
	private RelativeLayout imageRel;
	private String picPath = CacheFileUtil.carmePaht;
	private final static int PAIZHAO = 101;// 拍照
	private OnTakePhotoListener onTakePhotoListener;
	private OnChangeTimeListener onChangeTimeListener;

	public OnChangeTimeListener getOnChangeTimeListener() {
		return onChangeTimeListener;
	}

	public void setOnChangeTimeListener(
			OnChangeTimeListener onChangeTimeListener) {
		this.onChangeTimeListener = onChangeTimeListener;
	}

	public OnTakePhotoListener getOnTakePhotoListener() {
		return onTakePhotoListener;
	}

	public void setOnTakePhotoListener(OnTakePhotoListener onTakePhotoListener) {
		this.onTakePhotoListener = onTakePhotoListener;
	}

	public interface OnTakePhotoListener {
		// 1.选中操作 2，删除操作
		public void takePhotoListener(int type, ReleaseRouteImageView view);
	}

	public interface OnChangeTimeListener {
		// 1.图片 2.文字
		public void timeChangeListener(int type,ReleaseRouteImageView view, String time);
	}

	public ReleaseRouteImageView(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	public ReleaseRouteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context);
	}

	public ReleaseRouteImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(context);

	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.release_route_image_item, this);
		mImageView = (SimpleDraweeView)view.findViewById(R.id.release_route_image_view);
		photoFram = (FrameLayout)view.findViewById(R.id.release_route_image_take_photo);
		imageRel = (RelativeLayout) view.findViewById(R.id.release_route_round_iamge_rel);
		editText = (EmojiEdite) view.findViewById(R.id.route_foot_address_edit);
		footLin = (LinearLayout) view.findViewById(R.id.route_foot_address_edit_lin);
		// if(Util.routeMarkLs.size()>2&&Util.itemLocation>1){
		// editText.setText(Util.routeMarkLs.get(Util.itemLocation).getDescribeArea());
		//
		// }
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (onChangeTimeListener != null) {
					onChangeTimeListener.timeChangeListener(2,
							(ReleaseRouteImageView) (imageRel.getParent()
									.getParent()), s.toString());

				}
			}
		});
		imageRel.setOnClickListener(this);
		imageRel.setOnLongClickListener(this);

	}

	public void setEditext(String ex) {
		// TODO Auto-generated method stub
		editText.setText(ex);
	}

	public void setImageView(String path,String ratio) {
		// ImageLoader.getInstance().displayImage("file://" + path, mImageView);
		if(TextUtils.isEmpty(ratio)||ratio.contains("_")){
			mImageView.setAspectRatio(1.714f);	
		}else{
			float imaRatio=Float.parseFloat(ratio);
			mImageView.setAspectRatio(imaRatio);
		}
		mImageView.setImageURI(Uri.parse("file://"+path));
		mImageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
				+ "images/roadlineDescribe/"+path));
	}


	public void setDisTakeImage() {
		photoFram.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.release_route_round_iamge_rel:
			if (onTakePhotoListener != null) {
				onTakePhotoListener.takePhotoListener(1,
						(ReleaseRouteImageView) (imageRel.getParent()
								.getParent()));

			}
			PhoneDialog.Builder builder1 = new PhoneDialog.Builder(context);
			builder1.setMessage("选择照片");

			builder1.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(context,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									ReleaseRouteImageView.class.getName());
							context.startActivity(intent);
						}
					});

			builder1.setNegativeButton("拍照",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							getImageFromCamera();
						}
					});

			builder1.create().show();

			break;
		default:
			break;
		}

	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.release_route_round_iamge_rel:
			CustomDialog.Builder builder = new CustomDialog.Builder(context);
			builder.setMessage("确定要删除该项吗?");
			builder.setTitle("确认");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (onTakePhotoListener != null) {
								onTakePhotoListener.takePhotoListener(2,
										(ReleaseRouteImageView) (imageRel
												.getParent()
												.getParent()));

							}
						}
					});

			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
			break;

		default:
			break;
		}
		return false;
	}

	public RelativeLayout getImageRel() {
		return imageRel;
	}

	protected void getImageFromCamera() {
		MyApplication.getInstance().setKilled(false);
		Intent getImageByCamera = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		String out_file_path = picPath;
		File dir = new File(out_file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Util.pturePath = picPath + getFileName() + ".png";
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Util.pturePath)));
		getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		((Activity) context).startActivityForResult(getImageByCamera, PAIZHAO);

	}

	public static String getFileName() {
		int numcode = (int) ((Math.random() * 9 + 1) * 100000);
		String cc = DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss") + numcode;
		// BigInteger bi = new BigInteger(cc);
		return cc;
	}
	
	public int getImageViewHeight(){
		return mImageView.getHeight();
	}
	public int geteditTextHeight(){
		return footLin.getHeight();
	}

}
