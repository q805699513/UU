package com.uugty.uu.com.helper;

import java.util.HashMap;
import java.util.Map;

import com.easemob.util.HanziToPinyin;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.entity.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class UserDao {

	public UserDao(Context context){
		
	}
	/**
	 * 获取好友list
	 * 
	 * @return
	 */
//	public Map<String, User> getContactList() {
//		SQLiteDatabase db =new SQLiteDatabase;  // 从数据库中获取数据
//		Map<String, User> users = new HashMap<String, User>();
//		if (db.isOpen()) {
//			Cursor cursor = db.rawQuery("select * from " + "", null);
//			while (cursor.moveToNext()) {
////				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
////				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
////				String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
//				String username="";
//				String user_img="";
//				User user = new User();
//				user.setUsername(username);
//			
//				user.setAvatar(user_img);
//				String headerName = null;
//				if (!TextUtils.isEmpty(user.getNick())) {
//					headerName = user.getNick();
//				} else {
//					headerName = user.getUsername();
//				}
//				
//				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
//					user.setHeader("");
//				} else if (Character.isDigit(headerName.charAt(0))) {
//					user.setHeader("#");
//				} else {
//					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
//							.get(0).target.substring(0, 1).toUpperCase());
//					char header = user.getHeader().toLowerCase().charAt(0);
//					if (header < 'a' || header > 'z') {
//						user.setHeader("#");
//					}
//				}
//				users.put(username, user);
//			}
//			cursor.close();
//		}
//		return users;
//	}
}
