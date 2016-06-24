package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class GroupMemberEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private List<GroupMember> LIST;

	public class GroupMember {
		private String userAvatar;
		private String userId;
		private String userName;

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
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

	public List<GroupMember> getLIST() {
		return LIST;
	}

	public void setLIST(List<GroupMember> lIST) {
		LIST = lIST;
	}

	

}
