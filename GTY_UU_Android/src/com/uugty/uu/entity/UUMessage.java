package com.uugty.uu.entity;


import java.io.Serializable;
import java.util.List;


public class UUMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<DetailModeal> LIST;

	public class DetailModeal {
		private String userId;
		private String userName;
		private String userAvatar;
		private String userCity;

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

		public String getUserCity() {
			return userCity;
		}

		public void setUserCity(String userCity) {
			this.userCity = userCity;
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

	public List<DetailModeal> getLIST() {
		return LIST;
	}

	public void setLIST(List<DetailModeal> lIST) {
		LIST = lIST;
	}

	
}
