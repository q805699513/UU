package com.uugty.uu.entity;

import com.uugty.uu.entity.HomeTagEntity.Tags.PlayAndBuy;

import java.io.Serializable;
import java.util.List;

public class RoadEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private RoadDetail OBJECT;

	public class RoadDetail {
		private String userId;//用户Id
		private String userName;// 用户姓名
		private String userAge;// 用户年龄
		private String userCity;// 用户的城市
		private String userSex;// 性别1 男 ,2 女
		private String userPost;// 用户年代
		private String tourAvatar;//导游图像
		private String roadlineBackground;//背景图片
		private String userConstellation;// 用户的星座
		private String userWork;// 职业
		private String avageTotalIndex;// 总评分指数
		private String avageRatioIndex;// 性价比指数
		private String avageFreshIndex;// 新鲜指数
		private String avageServiceIndex;// 服务指数
		private String commentCount;// 总评论数
		private String commentUserAvatar;// 游客的头像
		private String roadlineTitle;// 路线标题
		private String roadlinePrice;// 路线价格
		private String roadlineContent;// 路线内容
		private String roadlineGoalArea;// 路线的目的地
		private String roadlineInfo;//旅行路线
		private String roadlineFeeContains;//价格包含的内容
		private String roadlineStartArea;//旅行出发地
		private String roadlineSpecialMark;//特别说明
		private String commentDate;// 评论时间
		private String commentContent;// 评论内容
		private String userDescription;// 个人描述
		private String collectId;//收藏iD
		private String roadlineId;//路线Id
		private String roadlineDays;
		private String orderNum;
		private String roadlineViews;
		private String userIdValidate;//身份证验证
		private String userIsPromoter;//是否为会员
		private String userTourValidate;//导游证验证
		private String userCertificateValidate; // 学位证是否被验证 0 未验证 1 已验证
		private String userCarValidate; // 车是否认证 0 未认证 1 认证
		private List<RoadLine> roadlineDescribes;
		private List<PlayAndBuy> tags;
		private String userTel;//用户电话号码

		public String getRoadlineMarketPrice() {
			return roadlineMarketPrice;
		}

		public void setRoadlineMarketPrice(String roadlineMarketPrice) {
			this.roadlineMarketPrice = roadlineMarketPrice;
		}

		private String roadlineMarketPrice;//市场价
		
		public String getRoadlineInfo() {
			return roadlineInfo;
		}

		public void setRoadlineInfo(String roadlineInfo) {
			this.roadlineInfo = roadlineInfo;
		}

		public String getRoadlineFeeContains() {
			return roadlineFeeContains;
		}

		public void setRoadlineFeeContains(String roadlineFeeContains) {
			this.roadlineFeeContains = roadlineFeeContains;
		}

		public String getRoadlineStartArea() {
			return roadlineStartArea;
		}

		public void setRoadlineStartArea(String roadlineStartArea) {
			this.roadlineStartArea = roadlineStartArea;
		}

		public String getRoadlineSpecialMark() {
			return roadlineSpecialMark;
		}

		public void setRoadlineSpecialMark(String roadlineSpecialMark) {
			this.roadlineSpecialMark = roadlineSpecialMark;
		}

		

		public String getRoadlineDays() {
			return roadlineDays;
		}

		public void setRoadlineDays(String roadlineDays) {
			this.roadlineDays = roadlineDays;
		}

		public String getRoadlineBackground() {
			return roadlineBackground;
		}

		public void setRoadlineBackground(String roadlineBackground) {
			this.roadlineBackground = roadlineBackground;
		}

		public String getTourAvatar() {
			return tourAvatar;
		}

		public void setTourAvatar(String tourAvatar) {
			this.tourAvatar = tourAvatar;
		}
		
		
		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getCollectId() {
			return collectId;
		}

		public void setCollectId(String collectId) {
			this.collectId = collectId;
		}

		public List<RoadLine> getRoadlineDescribes() {
			return roadlineDescribes;
		}

		public void setRoadlineDescribes(List<RoadLine> roadlineDescribes) {
			this.roadlineDescribes = roadlineDescribes;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserAge() {
			return userAge;
		}

		public void setUserAge(String userAge) {
			this.userAge = userAge;
		}

		public String getUserCity() {
			return userCity;
		}

		public void setUserCity(String userCity) {
			this.userCity = userCity;
		}

		public String getUserSex() {
			return userSex;
		}

		public void setUserSex(String userSex) {
			this.userSex = userSex;
		}

		public String getUserPost() {
			return userPost;
		}

		public void setUserPost(String userPost) {
			this.userPost = userPost;
		}

		public String getUserConstellation() {
			return userConstellation;
		}

		public void setUserConstellation(String userConstellation) {
			this.userConstellation = userConstellation;
		}

		public String getUserWork() {
			return userWork;
		}

		public void setUserWork(String userWork) {
			this.userWork = userWork;
		}

		public String getAvageTotalIndex() {
			return avageTotalIndex;
		}

		public void setAvageTotalIndex(String avageTotalIndex) {
			this.avageTotalIndex = avageTotalIndex;
		}

		public String getAvageRatioIndex() {
			return avageRatioIndex;
		}

		public void setAvageRatioIndex(String avageRatioIndex) {
			this.avageRatioIndex = avageRatioIndex;
		}

		public String getAvageFreshIndex() {
			return avageFreshIndex;
		}

		public void setAvageFreshIndex(String avageFreshIndex) {
			this.avageFreshIndex = avageFreshIndex;
		}

		public String getAvageServiceIndex() {
			return avageServiceIndex;
		}

		public void setAvageServiceIndex(String avageServiceIndex) {
			this.avageServiceIndex = avageServiceIndex;
		}

		public String getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(String commentCount) {
			this.commentCount = commentCount;
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

		public String getRoadlineContent() {
			return roadlineContent;
		}

		public void setRoadlineContent(String roadlineContent) {
			this.roadlineContent = roadlineContent;
		}

		public String getRoadlineGoalArea() {
			return roadlineGoalArea;
		}

		public void setRoadlineGoalArea(String roadlineGoalArea) {
			this.roadlineGoalArea = roadlineGoalArea;
		}

		public String getCommentUserAvatar() {
			return commentUserAvatar;
		}

		public void setCommentUserAvatar(String commentUserAvatar) {
			this.commentUserAvatar = commentUserAvatar;
		}

		public String getCommentDate() {
			return commentDate;
		}

		public void setCommentDate(String commentDate) {
			this.commentDate = commentDate;
		}

		public String getCommentContent() {
			return commentContent;
		}

		public void setCommentContent(String commentContent) {
			this.commentContent = commentContent;
		}

		public String getUserDescription() {
			return userDescription;
		}

		public void setUserDescription(String userDescription) {
			this.userDescription = userDescription;
		}

		public String getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(String orderNum) {
			this.orderNum = orderNum;
		}

		public String getRoadlineViews() {
			return roadlineViews;
		}

		public void setRoadlineViews(String roadlineViews) {
			this.roadlineViews = roadlineViews;
		}

		public List<PlayAndBuy> getTags() {
			return tags;
		}

		public void setTags(List<PlayAndBuy> tags) {
			this.tags = tags;
		}

		public String getUserIdValidate() {
			return userIdValidate;
		}

		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}

		public String getUserTel() {
			return userTel;
		}

		public void setUserTel(String userTel) {
			this.userTel = userTel;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
		}

		public String getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
		}

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

	public RoadDetail getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(RoadDetail oBJECT) {
		OBJECT = oBJECT;
	}
}
