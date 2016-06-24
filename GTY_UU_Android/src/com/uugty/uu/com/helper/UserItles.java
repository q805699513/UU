package com.uugty.uu.com.helper;

import com.easemob.chat.EMMessage;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.ImageBitmap;
import com.uugty.uu.entity.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class UserItles {
	 public static User getUserInfo(String username){
	        User user=null;
	        if(user == null){
	            user = new User(username);
	        }
	            
	        if(user != null){
	           
	            user.setNick(username);
	          
	        }
	        return user;
	    }
	    
	    /**
	     * 设置用户头像
	     * @param username
	     */
	    public static void setUserAvatar(Context context, String username, CirculHeadImage imageView){
	        User user = getUserInfo(username);
	        if(user != null){
	        	Bitmap btmap=ImageBitmap.toRoundBitmap(BitmapFactory.decodeResource(null, R.drawable.img_heard));
	        	
	        	
	        	
	        }else{
	        	Bitmap btmap=ImageBitmap.toRoundBitmap(BitmapFactory.decodeResource(null, R.drawable.img_heard));
	        }
	    }
	    
}
