package com.uugty.uu.uuchat;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.chat.photoView.ChatPhotoView;
import com.uugty.uu.common.util.ImageCache;


public class ChatShowBigImageActivity extends BaseActivity{

	private ProgressDialog pd;
	private ChatPhotoView image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;
	private int default_res = R.drawable.default_image;

	
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_chat_show_image;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		image = (ChatPhotoView) findViewById(R.id.photo_view_image);
		loadLocalPb = (ProgressBar) findViewById(R.id.photo_view_loading);
		default_res = getIntent().getIntExtra("default_image", R.drawable.default_image);
		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		//本地存在，直接显示本地的图片
				if (uri != null && new File(uri.getPath()).exists()) {
					DisplayMetrics metrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metrics);
					// int screenWidth = metrics.widthPixels;
					// int screenHeight =metrics.heightPixels;
					bitmap = ImageCache.getInstance().get(uri.getPath());
					/*if (bitmap == null) {
						LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
								ImageUtils.SCALE_IMAGE_HEIGHT);
						if (android.os.Build.VERSION.SDK_INT > 10) {
							task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							task.execute();
						}
					} else {
						image.setImageBitmap(bitmap);
					}*/
				} else if (remotepath != null) { //去服务器下载图片
					Map<String, String> maps = new HashMap<String, String>();
					if (!TextUtils.isEmpty(secret)) {
						maps.put("share-secret", secret);
					}
					downloadImage(remotepath, maps);
				} else {
					image.setImageResource(default_res);
				}

				
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 通过远程URL，确定下本地下载后的localurl
	 * @param remoteUrl
	 * @return
	 */
	public String getLocalFilePath(String remoteUrl){
		String localPath;
		if (remoteUrl.contains("/")){
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
					+ remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
		}else{
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
		}
		return localPath;
	}
	
	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		String str1 ="下载图片: 0%";
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(int error, String msg) {
				File file = new File(localFilePath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress, String status) {
				final String str2 = "下载图片:";
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

	    EMChatManager.getInstance().downloadFile(remoteFilePath, localFilePath, headers, callback);

	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
