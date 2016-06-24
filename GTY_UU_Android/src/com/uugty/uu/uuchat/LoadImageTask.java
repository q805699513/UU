/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uugty.uu.uuchat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.ImageUtils;
import com.uugty.uu.R;
import com.uugty.uu.chat.help.CommonUtils;
import com.uugty.uu.common.mylistener.NoDoubleClickListener;
import com.uugty.uu.common.util.ImageCache;


public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
	private ImageView iv = null;
	String localFullSizePath = null;
	String thumbnailPath = null;
	String remotePath = null;
	EMMessage message = null;
	ChatType chatType;
	Activity activity;
	private int width,height,imageWidth;
	private RelativeLayout.LayoutParams localLayoutParams;
	private int screenWidth;
	private List<EMMessage> pictureEmMessage = new ArrayList();

	@SuppressLint("NewApi")
	@Override
	protected Bitmap doInBackground(Object... args) {
		thumbnailPath = (String) args[0];
		localFullSizePath = (String) args[1];
		remotePath = (String) args[2];
		chatType = (ChatType) args[3];
		iv = (ImageView) args[4];
		// if(args[2] != null) {
		activity = (Activity) args[5];
		// }
		message = (EMMessage) args[6];
		this.pictureEmMessage = ((List<EMMessage>)args[7]);
		screenWidth = getScreenWidth();
		File file = new File(thumbnailPath);
		if (file.exists()) {
			return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
		} else {
			if (message.direct == EMMessage.Direct.SEND) {
				return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
			} else {
				return null;
			}
		}
		

	}

	protected void onPostExecute(Bitmap image) {
		if (image != null) {
			
			width = image.getWidth();
			height = image.getHeight();
		      localLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		      if (screenWidth!= 0)
		      {
		    	  imageWidth = 1 * screenWidth / 3;
		       // if (width <= height)
		        localLayoutParams.width = (int)imageWidth;
		        localLayoutParams.height = (int)(imageWidth * height / width);
		        iv.setLayoutParams(localLayoutParams);
		      }
		   
			
			iv.setImageBitmap(image);
			iv.setScaleType(ScaleType.FIT_XY);
			ImageCache.getInstance().put(thumbnailPath, image);
			iv.setClickable(true);
			iv.setTag(thumbnailPath);
			iv.setOnClickListener(new NoDoubleClickListener() {
				@Override
				public void onNoDoubleClick(View v) {
					Intent localIntent;
					if (thumbnailPath != null) {
						localIntent = new Intent(LoadImageTask.this.activity, ChatShowImageActivity.class);
		                localIntent.putExtra("listImage", (Serializable)LoadImageTask.this.pictureEmMessage);
		                if (LoadImageTask.this.message != null)
		                {
		                  ImageMessageBody localImageMessageBody = (ImageMessageBody)LoadImageTask.this.message.getBody();
		                  if (localImageMessageBody != null)
		                    localIntent.putExtra("image", localImageMessageBody.getLocalUrl());
		                }		
						
						/*Intent intent = new Intent(activity, ChatShowBigImageActivity.class);
						File file = new File(localFullSizePath);
						if (file.exists()) {
							Uri uri = Uri.fromFile(file);
							intent.putExtra("uri", uri);
						} else {
							// The local full size pic does not exist yet.
							// ShowBigImage needs to download it from the server
							// first
							intent.putExtra("remotepath", remotePath);
						}*/
						if (message.getChatType() != ChatType.Chat) {
							// delete the image from server after download
						}
						if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked && message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
							message.isAcked = true;
							try {
								// 看了大图后发个已读回执给对方
								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						activity.startActivity(localIntent);
					}
				}
			});
		} else {
			if (message.status == EMMessage.Status.FAIL) {
				if (CommonUtils.isNetWorkConnected(activity)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							EMChatManager.getInstance().asyncFetchMessage(message);
						}
					}).start();
				}
			}

		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	 /** 
     * 得到手机屏幕的宽度 
     * 
     * @return 
     */  
    private int getScreenWidth() {  
        DisplayMetrics dm = new DisplayMetrics();  
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);  
        return dm.widthPixels;  
    } 
}
