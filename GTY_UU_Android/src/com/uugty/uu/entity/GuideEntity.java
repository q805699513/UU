package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class GuideEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<GuideDetail> LIST;

	public class GuideDetail {
		private String roadlineTitle;// 发布路线的标题
		private String roadlinePrice;// 路线需要的价格
		private String roadlineId;// 路线id
		private String userAvatar;// 导游的头像
		private String roadlineImages;//图片
		private String roadlineBackground;
		private String orderCount;//成交数量
		private String lineNum;
		private String isNew;//是否为最新;1为是，0为否
		private String isOnline;//是否在线1为是，0为否
		private String collectId;//是否收藏 0为否
		private String userIdValidate;//身份证是否验证
		private String userTourValidate;// 导游证是否验证	2
		private String userCertificateValidate;// 学位证是否验证	2
		private String userCarValidate; // 驾驶证是否验证 2
		private String userIsPromoter;//是否支付199 1为支付

		public String getUserTourValidate() {
			return userTourValidate;
		}

		public void setUserTourValidate(String userTourValidate) {
			this.userTourValidate = userTourValidate;
		}

		public String getUserCertificateValidate() {
			return userCertificateValidate;
		}

		public void setUserCertificateValidate(String userCertificateValidate) {
			this.userCertificateValidate = userCertificateValidate;
		}

		public String getUserCarValidate() {
			return userCarValidate;
		}

		public void setUserCarValidate(String userCarValidate) {
			this.userCarValidate = userCarValidate;
		}

		public String getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
		}

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

		public String getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(String orderCount) {
			this.orderCount = orderCount;
		}

		public String getRoadlineBackground() {
			return roadlineBackground;
		}

		public void setRoadlineBackground(String roadlineBackground) {
			this.roadlineBackground = roadlineBackground;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
		}

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getRoadlineImages() {
			return roadlineImages;
		}

		public void setRoadlineImages(String roadlineImages) {
			this.roadlineImages = roadlineImages;
		}

		public String getRoadlineTitle() {
			return roadlineTitle;
		}

		public void setRoadlineTitle(String roadlineTitle) {
			this.roadlineTitle = roadlineTitle;
		}

		public String getRoadlinePrice() {
			return roadlinePrice;
		}

		public void setRoadlinePrice(String roadlinePrice) {
			this.roadlinePrice = roadlinePrice;
		}

		public String getLineNum() {
			return lineNum;
		}

		public void setLineNum(String lineNum) {
			this.lineNum = lineNum;
		}

		public String getIsNew() {
			return isNew;
		}

		public void setIsNew(String isNew) {
			this.isNew = isNew;
		}

		public String getIsOnline() {
			return isOnline;
		}

		public void setIsOnline(String isOnline) {
			this.isOnline = isOnline;
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

	public List<GuideDetail> getLIST() {
		return LIST;
	}

	public void setLIST(List<GuideDetail> lIST) {
		LIST = lIST;
	}
}
