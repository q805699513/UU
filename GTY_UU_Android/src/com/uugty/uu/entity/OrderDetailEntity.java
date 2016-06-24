package com.uugty.uu.entity;



public class OrderDetailEntity {

	private OrderDetail OBJECT;
	private String STATUS;
	private String MSG;
	
	public class OrderDetail{
		private String orderCreateDate;//订单创建时间
		private String orderMark;//下单留言
		private String orderNo;//订单编号
		private String orderPrice;//订单的价格
		private String orderStatus;//订单的状态
		private String orderTime;//订单时间
		private String roadlineGoalArea;//路线目的地
		private String userAvatar;//用户头像
		private String userConstellation;//用户的星座
		private String userCreditScore;//用户信誉积分
		private String userId;//用户主键id
		private String userSex;//用户的性别 1 男 ,2 女
		private String userWork;//用户的工作
		private String userRealname;
		private String orderDrawbackReason;//退款原因
		private String orderDrawbackMoney;//退款金额
		private String orderDrawbackDate;//退款日期
		private String roadlineId;//路线ID
		private String roadlineDays;//路线天数
		private String userPost; //用户的年代
		private String roadlineTitle;//标题
		private String orderCommentCount;//品论条数
		private String orderCount;//订单数
		private String roadlineBrownTimes;//浏览次数
		private String roadlineBackground;//背景图
		private String orderTravelNumber;//预定数量
		private String contactId;//预定人数ID
		private String contactNum;//预定人数 
		private String contactName;//出行人名
		private String visitorName;//联系人名
		private String visitorTel;//联系人电话
		private String visitorContent;//备注
		private String orderActualPayment;//实际支付金额
		private String orderCouponMoney;//代金券金额
		private String couponUserId;//用户和代金券关联表主键id
		private String couponId;// 代金券主键

		
		
		public String getContactNum() {
			return contactNum;
		}
		public void setContactNum(String contactNum) {
			this.contactNum = contactNum;
		}
		public String getOrderTravelNumber() {
			return orderTravelNumber;
		}
		public void setOrderTravelNumber(String orderTravelNumber) {
			this.orderTravelNumber = orderTravelNumber;
		}
		public String getContactId() {
			return contactId;
		}
		public void setContactId(String contactId) {
			this.contactId = contactId;
		}
		public String getOrderCommentCount() {
			return orderCommentCount;
		}
		public void setOrderCommentCount(String orderCommentCount) {
			this.orderCommentCount = orderCommentCount;
		}
		public String getOrderCount() {
			return orderCount;
		}
		public void setOrderCount(String orderCount) {
			this.orderCount = orderCount;
		}
		public String getRoadlineBrownTimes() {
			return roadlineBrownTimes;
		}
		public void setRoadlineBrownTimes(String roadlineBrownTimes) {
			this.roadlineBrownTimes = roadlineBrownTimes;
		}
		public String getRoadlineBackground() {
			return roadlineBackground;
		}
		public void setRoadlineBackground(String roadlineBackground) {
			this.roadlineBackground = roadlineBackground;
		}
		public String getOrderCreateDate() {
			return orderCreateDate;
		}
		public void setOrderCreateDate(String orderCreateDate) {
			this.orderCreateDate = orderCreateDate;
		}
		public String getOrderMark() {
			return orderMark;
		}
		public void setOrderMark(String orderMark) {
			this.orderMark = orderMark;
		}
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
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
		public String getUserConstellation() {
			return userConstellation;
		}
		public void setUserConstellation(String userConstellation) {
			this.userConstellation = userConstellation;
		}
		public String getUserCreditScore() {
			return userCreditScore;
		}
		public void setUserCreditScore(String userCreditScore) {
			this.userCreditScore = userCreditScore;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUserSex() {
			return userSex;
		}
		public void setUserSex(String userSex) {
			this.userSex = userSex;
		}
		public String getUserWork() {
			return userWork;
		}
		public void setUserWork(String userWork) {
			this.userWork = userWork;
		}
		public String getUserRealname() {
			return userRealname;
		}
		public void setUserRealname(String userRealname) {
			this.userRealname = userRealname;
		}
		public String getOrderDrawbackReason() {
			return orderDrawbackReason;
		}
		public void setOrderDrawbackReason(String orderDrawbackReason) {
			this.orderDrawbackReason = orderDrawbackReason;
		}
		public String getOrderDrawbackMoney() {
			return orderDrawbackMoney;
		}
		public void setOrderDrawbackMoney(String orderDrawbackMoney) {
			this.orderDrawbackMoney = orderDrawbackMoney;
		}
		public String getOrderDrawbackDate() {
			return orderDrawbackDate;
		}
		public void setOrderDrawbackDate(String orderDrawbackDate) {
			this.orderDrawbackDate = orderDrawbackDate;
		}
		public String getRoadlineDays() {
			return roadlineDays;
		}
		public void setRoadlineDays(String roadlineDays) {
			this.roadlineDays = roadlineDays;
		}
		public String getUserPost() {
			return userPost;
		}
		public void setUserPost(String userPost) {
			this.userPost = userPost;
		}
		public String getRoadlineTitle() {
			return roadlineTitle;
		}
		public void setRoadlineTitle(String roadlineTitle) {
			this.roadlineTitle = roadlineTitle;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
		public String getVisitorName() {
			return visitorName;
		}
		public void setVisitorName(String visitorName) {
			this.visitorName = visitorName;
		}
		public String getVisitorTel() {
			return visitorTel;
		}
		public void setVisitorTel(String visitorPhone) {
			this.visitorTel = visitorPhone;
		}
		public String getVisitorContent() {
			return visitorContent;
		}
		public void setVisitorContent(String visitorContent) {
			this.visitorContent = visitorContent;
		}
		public String getRoadlineId() {
			return roadlineId;
		}
		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
		}
		public String getOrderActualPayment() {
			return orderActualPayment;
		}
		public void setOrderActualPayment(String orderActualPayment) {
			this.orderActualPayment = orderActualPayment;
		}
		public String getOrderCouponMoney() {
			return orderCouponMoney;
		}
		public void setOrderCouponMoney(String orderCouponMoney) {
			this.orderCouponMoney = orderCouponMoney;
		}
		public String getCouponUserId() {
			return couponUserId;
		}
		public void setCouponUserId(String couponUserId) {
			this.couponUserId = couponUserId;
		}
		public String getCouponId() {
			return couponId;
		}
		public void setCouponId(String couponId) {
			this.couponId = couponId;
		}

	}



	public OrderDetail getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(OrderDetail oBJECT) {
		OBJECT = oBJECT;
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
