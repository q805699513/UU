package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class MoreLvEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private List<MoreListEntity> LIST;

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

	public List<MoreListEntity> getLIST() {
		return LIST;
	}

	public void setLIST(List<MoreListEntity> lIST) {
		LIST = lIST;
	}

	public class MoreListEntity {
		private String avageTotalIndex;// 总评分指数
		private String roadlineId;// 路线ID
		private String roadlineTitle;// 路线标题
		private String roadlinePrice;// 路线价格
		private String roadlineBackground;// 路线背景图片
		private String roadlineInfo;// 旅行路线
		private String orderCount ;// 出行数
		private String lineNum;  // 浏览量
		private String isNew;//是否为最新;1为是，0为否
		private String isOnLine;//是否为最新;1为是，0为否
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

		public String getAvageTotalIndex() {
			return avageTotalIndex;
		}

		public void setAvageTotalIndex(String avageTotalIndex) {
			this.avageTotalIndex = avageTotalIndex;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
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

		public String getRoadlineBackground() {
			return roadlineBackground;
		}

		public void setRoadlineBackground(String roadlineBackground) {
			this.roadlineBackground = roadlineBackground;
		}

		public String getRoadlineInfo() {
			return roadlineInfo;
		}

		public void setRoadlineInfo(String roadlineInfo) {
			this.roadlineInfo = roadlineInfo;
		}

		

		public String getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(String orderCount) {
			this.orderCount = orderCount;
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

		public String getIsOnLine() {
			return isOnLine;
		}

		public void setIsOnLine(String isOnLine) {
			this.isOnLine = isOnLine;
		}
	}

}
