package com.uugty.uu.entity;

import java.util.List;

public class OrderListItem {
	private List<ItemEntity> LIST;
	private String STATUS;
	private String MSG;

	public class ItemEntity {

		private String orderCreateDate; // 订单创建时间
		private String orderId;// 订单id
		private String orderPrice;// 订单的价格
		private String orderStatus;// 订单的状态
		private String orderTime;// 订单时间
		private String roadlineGoalArea;// 路线目的地
		private String userAvatar;// 用户的头像
		private String userId;// 用户id
		private String userRealname;// 用户真实姓名
		private String isEval;//0 未评价  1 已评价
		private String roadlineDays;//路线天数
		private String roadlineBackGround;
		private String userName;
		private String orderTravelNumber; //预定人数
		private String orderCouponMoney;//代金券金额
		private String roadlineTitle;//路线标题
		private String orderType;//区分是否导游圈路线


		public String getOrderType() {
			return orderType;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public String getOrderTravelNumber() {
			return orderTravelNumber;
		}

		public void setOrderTravelNumber(String orderTravelNumber) {
			this.orderTravelNumber = orderTravelNumber;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getRoadlineBackGround() {
			return roadlineBackGround;
		}

		public void setRoadlineBackGround(String roadlineBackGround) {
			this.roadlineBackGround = roadlineBackGround;
		}

		public String getOrderCreateDate() {
			return orderCreateDate;
		}

		public void setOrderCreateDate(String orderCreateDate) {
			this.orderCreateDate = orderCreateDate;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getOrderPrice() {
			return orderPrice;
		}

		public void setOrderPrice(String orderPrice) {
			this.orderPrice = orderPrice;
		}

		public String getOrderStatus() {
			return orderStatus;
		}

		public void setOrderStatus(String orderStatus) {
			this.orderStatus = orderStatus;
		}

		public String getOrderTime() {
			return orderTime;
		}

		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}

		public String getRoadlineGoalArea() {
			return roadlineGoalArea;
		}

		public void setRoadlineGoalArea(String roadlineGoalArea) {
			this.roadlineGoalArea = roadlineGoalArea;
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

		public String getUserRealname() {
			return userRealname;
		}

		public void setUserRealname(String userRealname) {
			this.userRealname = userRealname;
		}

		public String getIsEval() {
			return isEval;
		}

		public void setIsEval(String isEval) {
			this.isEval = isEval;
		}

		public String getRoadlineDays() {
			return roadlineDays;
		}

		public void setRoadlineDays(String roadlineDays) {
			this.roadlineDays = roadlineDays;
		}

		public String getOrderCouponMoney() {
			return orderCouponMoney;
		}

		public void setOrderCouponMoney(String orderCouponMoney) {
			this.orderCouponMoney = orderCouponMoney;
		}

		public String getOrderTitle() {
			return roadlineTitle;
		}

		public void setOrderTitle(String orderTitle) {
			this.roadlineTitle = orderTitle;
		}

	}

	public List<ItemEntity> getLIST() {
		return LIST;
	}

	public void setLIST(List<ItemEntity> lIST) {
		LIST = lIST;
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

}
