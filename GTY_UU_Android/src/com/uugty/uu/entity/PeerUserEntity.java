package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class PeerUserEntity implements Serializable{

	private String MSG;
	private Object OBJECT;
	private String STATUS;

	private List<ThreeBean> LIST;

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String MSG) {
		this.MSG = MSG;
	}

	public Object getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(Object OBJECT) {
		this.OBJECT = OBJECT;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String STATUS) {
		this.STATUS = STATUS;
	}

	public List<ThreeBean> getLIST() {
		return LIST;
	}

	public void setLIST(List<ThreeBean> LIST) {
		this.LIST = LIST;
	}

	public static class ThreeBean {
		private String parentId;//父级ID
		private int payStatus;//支付状态
		private String userAvatar;//下级头像
		private String userId;//用户userid
		private int userIsPromoter;//是否会员
		private String userName;//用户名
		private String userRegisterDate;//注册时间
		private String userVipId;//会员id

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public int getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(int payStatus) {
			this.payStatus = payStatus;
		}

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

		public int getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(int userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserRegisterDate() {
			return userRegisterDate;
		}

		public void setUserRegisterDate(String userRegisterDate) {
			this.userRegisterDate = userRegisterDate;
		}

		public String getUserVipId() {
			return userVipId;
		}

		public void setUserVipId(String userVipId) {
			this.userVipId = userVipId;
		}
	}
}
