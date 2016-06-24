package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class MapUserEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<MapUser> LIST;

	public class MapUser {
		private String userId; // 用户id
		private String userName; // 用户的名称
		private String userAvatar; // 用户的头像
		private String userLastLoginLat;
		private String userLastLoginLng;
		private String tempShout;// 大喊一声的内容
		private String uuid;
		private String userStatus;//用户身份 , 1.游客 ; 2.导游 

		
		public String getUserStatus() {
			return userStatus;
		}

		public void setUserStatus(String userStatus) {
			this.userStatus = userStatus;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getUserLastLoginLat() {
			return userLastLoginLat;
		}

		public void setUserLastLoginLat(String userLastLoginLat) {
			this.userLastLoginLat = userLastLoginLat;
		}

		public String getUserLastLoginLng() {
			return userLastLoginLng;
		}

		public void setUserLastLoginLng(String userLastLoginLng) {
			this.userLastLoginLng = userLastLoginLng;
		}

		public String getTempShout() {
			return tempShout;
		}

		public void setTempShout(String tempShout) {
			this.tempShout = tempShout;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(String oBJECT) {
		OBJECT = oBJECT;
	}

	public List<MapUser> getLIST() {
		return LIST;
	}

	public void setLIST(List<MapUser> lIST) {
		LIST = lIST;
	}

}
