package com.uugty.uu.entity;

public class UserSimpleEntity {

	private String STATUS;
	private String MSG;
	private UserSimpleInfo OBJECT;

	public class UserSimpleInfo {
		private String userAvatar;// 用户的头像
		private String userName; //昵称

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

	    
		
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public UserSimpleInfo getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(UserSimpleInfo oBJECT) {
		OBJECT = oBJECT;
	}
	
	
}
