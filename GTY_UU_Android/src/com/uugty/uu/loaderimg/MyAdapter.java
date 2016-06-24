package com.uugty.uu.loaderimg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.uugty.uu.R;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.friendstask.PublishTalkActivity;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.person.CutPicturceActivity;
import com.uugty.uu.person.PersonPhotoVeriActivity;

public class MyAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	private Activity myContext;
	private int num = 0;
	private String topageFrom;
	private String shape;
	private Bitmap bitmap;

	public MyAdapter(Activity context, List<String> mDatas, int itemLayoutId,
			String dirPath, String topageFrom, String shape) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.myContext = context;
		this.topageFrom = topageFrom;
		this.shape = shape;
	}

	@Override
	public void convert(final ViewHolder helper, final String item) {
		// 设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		// 设置no_selected
		// helper.setImageResource(R.id.id_item_select,
		// R.drawable.picture_unselected);
		// 设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);

		mImageView.setColorFilter(null);
		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				// Intent intent=new Intent(myContext,
				// CutPicturceActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.putExtra("picPath",(mDirPath+"/"+item));
				// myContext.startActivity(intent);
				//
				/*
				 * if (mSelectedImage.contains(mDirPath + "/" + item)) {
				 * Toast.makeText(myContext, "本张图片你已经裁剪了,请选择其他图片进行裁剪!",
				 * 1).show();
				 * 
				 * } else // 未选择该图片 {
				 */
				/*
				 * if (mSelectedImage.size()==10) {
				 * 
				 * Toast.makeText(myContext, "你只能选择10张图片裁剪", 1).show();
				 * 
				 * return; }else {
				 */
				// mSelect.setImageResource(R.drawable.pictures_selected);

				// 选中图片蒙一层的阴影 颜色
				File dir = new File(CacheFileUtil.carmePaht);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				mImageView.setColorFilter(Color.parseColor("#77000000"));
				int numcode = (int) ((Math.random() * 9 + 1) * 100000);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"
						+ numcode);
				String picName="";
				if(item.contains(".")){
					picName = sdf.format(new Date())
							+ item.substring(item.lastIndexOf("."));
				}else{
					CustomToast.makeText(myContext, "图片格式错误", 200);
					return;
				}
				
				File file = new File(CacheFileUtil.carmePaht + picName);
				if (file.exists()) {
					file.delete();
				}
				BufferedInputStream fis = null;
				BufferedOutputStream fos = null;
				byte[] buffer = new byte[4096];
				try {
					file.createNewFile();
					fis = new BufferedInputStream(new FileInputStream(mDirPath
							+ "/" + item));
					fos = new BufferedOutputStream(new FileOutputStream(file));
					int length = 0;
					while ((length = fis.read(buffer)) != -1) {
						fos.write(buffer, 0, length);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (fis != null) {
							fis.close();
						}
						if (fos != null) {
							fos.close();
						}
					} catch (Exception e) {

					}
				}
				Intent intent = new Intent();
				if (topageFrom
						.equals("com.uugty.uu.person.PersonPhotoVeriActivity")) {
					intent.putExtra("picSelect", CacheFileUtil.carmePaht
							+ picName);
					intent.setClass(myContext, PersonPhotoVeriActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				}else if(topageFrom
						.equals("com.uugty.uu.common.myview.ReleaseRouteImageView")){
					intent.putExtra("resultPic", CacheFileUtil.carmePaht
							+ picName);
					intent.setClass(myContext, PublishServicesActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				}else if(topageFrom
						.equals("com.uugty.uu.friendstask.PublishTalkActivity")){
					intent.putExtra("resultPic", CacheFileUtil.carmePaht
							+ picName);
					intent.setClass(myContext, PublishTalkActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				}else {
					intent.putExtra("topageFrom", topageFrom);
					intent.setClass(myContext, CutPicturceActivity.class);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("shape", shape);
					intent.putExtra("picPath", CacheFileUtil.carmePaht
							+ picName);
				}

				myContext.startActivity(intent);
				notifyDataSetChanged();
			}
			// }

			// }
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		/*
		 * if (mSelectedImage.contains(mDirPath + "/" + item)) {
		 * mSelect.setImageResource(R.drawable.pictures_selected);
		 * mImageView.setColorFilter(Color.parseColor("#77000000")); }else {
		 * mSelect.setImageResource(0); mImageView.setColorFilter(null); }
		 */

	}

}
