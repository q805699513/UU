package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class UpVotersTitleEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<UpvotersTitle> LIST;

	public class UpvotersTitle {	
		private String userId;// 用户ID
		private String userName;// 用户名
		private String userAvatar;// 用户头像
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

	public List<UpvotersTitle> getLIST() {
		return LIST;
	}

	public void setLIST(List<UpvotersTitle> lIST) {
		LIST = lIST;
	}
	
}
