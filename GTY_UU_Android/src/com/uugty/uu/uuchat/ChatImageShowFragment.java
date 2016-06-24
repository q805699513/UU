package com.uugty.uu.uuchat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.uugty.uu.R;
import com.uugty.uu.common.photoview.PhotoViewAttacher;
import com.uugty.uu.common.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.uugty.uu.common.util.ImageCache;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class ChatImageShowFragment extends Fragment{
	private String localFullSizePath,remotepath,secret;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;
	private Uri uri;
	private Bitmap bitmap;
	private int default_res = R.drawable.default_image;
	private ProgressDialog pd;
	private String localFilePath;
	private EMMessage message;

	
	public static ChatImageShowFragment newInstance(EMMessage message) {
		final ChatImageShowFragment f = new ChatImageShowFragment();
		final Bundle args = new Bundle();
		/*ImageMessageBody body = (ImageMessageBody) message
				.getBody();
		String type="";
		if (message.direct == EMMessage.Direct.SEND) {
			type="2";
		}
		
		
		args.putString("localFullSizePath", filePath);
		args.putString("remotepath", body.getRemoteUrl());
		args.putString("secret", body.getSecret());
		args.putString("flag",type);*/
		args.putParcelable("message", (Parcelable)message);
		f.setArguments(args);

		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		message = (EMMessage) (getArguments() != null ? getArguments().getParcelable("message") : null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.activity_chat_show_image, container, false);
		mImageView = (ImageView) v.findViewById(R.id.photo_view_image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				if(null!=getActivity())
				getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.photo_view_loading);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ImageMessageBody body = (ImageMessageBody) message.getBody();
		//发送方的图片在本地，接收方的图片需要从环信服务器上下载
		if (message.direct == EMMessage.Direct.RECEIVE) {//接收方
			localFullSizePath = com.uugty.uu.common.util.ImageUtils.getImagePath(body.getRemoteUrl());
			remotepath =body.getRemoteUrl();
			secret = body.getSecret();
			
			File file = new File(localFullSizePath);
			if (file.exists()) {
				uri = Uri.fromFile(file);
			}
			if (uri != null && new File(uri.getPath()).exists()) {
				DisplayMetrics metrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
				// int screenWidth = metrics.widthPixels;
				// int screenHeight =metrics.heightPixels;
				bitmap = ImageCache.getInstance().get(uri.getPath());
				if (bitmap == null) {
					LoadLocalBigImgTask task = new LoadLocalBigImgTask(getActivity(), uri.getPath(), mImageView, progressBar, ImageUtils.SCALE_IMAGE_WIDTH,
							ImageUtils.SCALE_IMAGE_HEIGHT,mAttacher);
					if (android.os.Build.VERSION.SDK_INT > 10) {
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						task.execute();
					}
				} else {
					mImageView.setImageBitmap(bitmap);
					mAttacher.update();
				}
			} else if (remotepath != null) { //去服务器下载图片
				Map<String, String> maps = new HashMap<String, String>();
				if (!TextUtils.isEmpty(secret)) {
					maps.put("share-secret", secret);
				}
				downloadImage(remotepath, maps);
			} else {
				mImageView.setImageResource(default_res);
			}
			
		}else{
			localFullSizePath = body.getLocalUrl();
			if (localFullSizePath != null && new File(localFullSizePath).exists()) {
				Uri uri = Uri.fromFile(new File(localFullSizePath));
				if (uri != null && new File(uri.getPath()).exists()) {
					DisplayMetrics metrics = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
					bitmap = ImageCache.getInstance().get(uri.getPath());
					if (bitmap == null) {
						LoadLocalBigImgTask task = new LoadLocalBigImgTask(getActivity(), uri.getPath(), mImageView, progressBar, ImageUtils.SCALE_IMAGE_WIDTH,
								ImageUtils.SCALE_IMAGE_HEIGHT,mAttacher);
						if (android.os.Build.VERSION.SDK_INT > 10) {
							task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							task.execute();
						}
					} else {
						mImageView.setImageBitmap(bitmap);
						mAttacher.update();
					}
				}
				
			} else {
				//如果本地图片清除了的处理
				
			}
			
		}
		
		
	}
	
	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		String str1 ="下载图片: 0%";
		pd = new ProgressDialog(getActivity());
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							mImageView.setImageResource(default_res);
						} else {
							mImageView.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
						}
						if (pd != null) {
							pd.dismiss();
						}
						mAttacher.update();
					}
				});
			}

			public void onError(int error, String msg) {
				File file = new File(localFilePath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						mImageView.setImageResource(default_res);
						mAttacher.update();
					}
				});
			}

			public void onProgress(final int progress, String status) {
				final String str2 = "下载图片:";
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

	    EMChatManager.getInstance().downloadFile(remoteFilePath, localFilePath, headers, callback);

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
}
