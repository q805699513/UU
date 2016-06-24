package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class DynamicEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<Dynamic> LIST;

	public class Dynamic implements Serializable {
		private String saidId;// 主键ID
		private String saidCity;// 发布地点
		private String saidContent;// 发布内容
		private String saidPhoto;// 发布图片
		private String saidCreateDate;// 发布时间
		private String saidBrowseTimes;// 浏览次数
		private String saidRecentlyReader;// 最近浏览的人
		private String saidGoodTimes;// 点赞次数
		private String saidUpvoteStatus;// 点赞状态
		private String userId;// 用户ID
		private String userName;// 用户名
		private String userAvatar;// 用户头像
		private String saidCommentTimes;// 评论次数
		private String saidPictureRatio;// 图片比例
		private String saidIsCollect;// 是否收藏 0未收藏 1收藏
		private String userIdValidate;// 身份证验证

		private String saidIsFreeze;// 是否存在 0 存在 1 未存在
		private String collectId;// 收藏的ID

		public String getUserIdValidate() {
			return userIdValidate;
		}

		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}

		public String getCollectId() {
			return collectId;
		}

		public void setCollectId(String collectId) {
			this.collectId = collectId;
		}

		public String getSaidIsFreeze() {
			return saidIsFreeze;
		}

		public void setSaidIsFreeze(String saidIsFreeze) {
			this.saidIsFreeze = saidIsFreeze;
		}

		public String getSaidIsCollect() {
			return saidIsCollect;
		}

		public void setSaidIsCollect(String saidIsCollect) {
			this.saidIsCollect = saidIsCollect;
		}

		public String getSaidId() {
			return saidId;
		}

		public void setSaidId(String saidId) {
			this.saidId = saidId;
		}

		public String getSaidCity() {
			return saidCity;
		}

		public void setSaidCity(String saidCity) {
			this.saidCity = saidCity;
		}

		public String getSaidContent() {
			return saidContent;
		}

		public void setSaidContent(String saidContent) {
			this.saidContent = saidContent;
		}

		public String getSaidPhoto() {
			return saidPhoto;
		}

		public void setSaidPhoto(String saidPhoto) {
			this.saidPhoto = saidPhoto;
		}

		public String getSaidCreateDate() {
			return saidCreateDate;
		}

		public void setSaidCreateDate(String saidCreateDate) {
			this.saidCreateDate = saidCreateDate;
		}

		public String getSaidBrowseTimes() {
			return saidBrowseTimes;
		}

		public void setSaidBrowseTimes(String saidBrowseTimes) {
			this.saidBrowseTimes = saidBrowseTimes;
		}

		public String getSaidRecentlyReader() {
			return saidRecentlyReader;
		}

		public void setSaidRecentlyReader(String saidRecentlyReader) {
			this.saidRecentlyReader = saidRecentlyReader;
		}

		public String getSaidGoodTimes() {
			return saidGoodTimes;
		}

		public void setSaidGoodTimes(String saidGoodTimes) {
			this.saidGoodTimes = saidGoodTimes;
		}

		public String getSaidUpvoteStatus() {
			return saidUpvoteStatus;
		}

		public void setSaidUpvoteStatus(String saidUpvoteStatus) {
			this.saidUpvoteStatus = saidUpvoteStatus;
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

		public String getSaidCommentTimes() {
			return saidCommentTimes;
		}

		public void setSaidCommentTimes(String saidCommentTimes) {
			this.saidCommentTimes = saidCommentTimes;
		}

		public String getSaidPictureRatio() {
			return saidPictureRatio;
		}

		public void setSaidPictureRatio(String saidPictureRatio) {
			this.saidPictureRatio = saidPictureRatio;
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

	public List<Dynamic> getLIST() {
		return LIST;
	}

	public void setLIST(List<Dynamic> lIST) {
		LIST = lIST;
	}

}
