package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class UserMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private UserMessageEntity OBJECT;
	private List<CommentsVo> LIST;

	public class CommentsVo {
		private String userId;
		private String userName;
		private String userAvatar;
		private String commentContent;
		private String commentDate;

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

		public String getCommentContent() {
			return commentContent;
		}

		public void setCommentContent(String commentContent) {
			this.commentContent = commentContent;
		}

		public String getCommentDate() {
			return commentDate;
		}

		public void setCommentDate(String commentDate) {
			this.commentDate = commentDate;
		}

	}
	public class UserMessageEntity {
		private String commentsCount;// ; 评论的数量
		private String roadlineId;// 路线id
		private String userAge;// 用户年龄
		private String userArea;// 工作地点
		private String userAvatar;// 用户的头像
		private String userBirthday; // 用户的年龄
		private String userCar;// 用户的车
		private String userCarValidate; // 车是否认证 0 未认证 1 认证
		private String userCertificate; // 学位证
		private String userCertificateValidate; // 学位证是否被验证 0 未验证 1 已验证
		private String userCity; // 城市
		private String userConstellation; // 用户的星座
		private String userCreditScore; // 用户的信誉积分
		private String userDescription; // 用户描述,个人说明
		private String userEmail; //
		private String userEmailValidate;//
		private String userId; // 021c69e721924bafa89159a5cf2e9226
		private String userIdValidate; // 身份证是否验证0 未验证 1 已验证
		private String userIdentity; // 身份证
		private String userLanguage; // 语言
		private String userLifePhoto; // 用户的生活照
		private String userName; // 用户的别名，昵称
		private String userPassword;// 用户的密码
		private String userPayPassword; // 支付密码
		private String userPost; // 用户的年代
		private String userPrivacyModel;// 用户隐身的模式(对所有人可见,对微信好友隐身,对qq好友隐身)
		private String userRealname; // 真实姓名
		private String userSchool; // 学校
		private String userSex; // 用户的性别（1 男 2 女）
		private String userTel; // 手机号码
		private String userTelValidate; // 手机号码是否被验证 0 未验证 1 已验证
		private String userTourCard; // 导游证
		private String userTourValidate; // 导游证是否验证 0 未验证 1 已验证
		private String userWork; // 工作
		private String userIsPromoter;//是否认证小U 0未验证 1已验证

		public String getCommentsCount() {
			return commentsCount;
		}

		public void setCommentsCount(String commentsCount) {
			this.commentsCount = commentsCount;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
		}

		public String getUserAge() {
			return userAge;
		}

		public void setUserAge(String userAge) {
			this.userAge = userAge;
		}

		public String getUserArea() {
			return userArea;
		}

		public void setUserArea(String userArea) {
			this.userArea = userArea;
		}

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getUserBirthday() {
			return userBirthday;
		}

		public void setUserBirthday(String userBirthday) {
			this.userBirthday = userBirthday;
		}

		public String getUserCar() {
			return userCar;
		}

		public void setUserCar(String userCar) {
			this.userCar = userCar;
		}

		public String getUserCarValidate() {
			return userCarValidate;
		}

		public void setUserCarValidate(String userCarValidate) {
			this.userCarValidate = userCarValidate;
		}

		public String getUserCertificate() {
			return userCertificate;
		}

		public void setUserCertificate(String userCertificate) {
			this.userCertificate = userCertificate;
		}

		public String getUserCertificateValidate() {
			return userCertificateValidate;
		}

		public void setUserCertificateValidate(String userCertificateValidate) {
			this.userCertificateValidate = userCertificateValidate;
		}

		public String getUserCity() {
			return userCity;
		}

		public void setUserCity(String userCity) {
			this.userCity = userCity;
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

		public String getUserDescription() {
			return userDescription;
		}

		public void setUserDescription(String userDescription) {
			this.userDescription = userDescription;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		public String getUserEmailValidate() {
			return userEmailValidate;
		}

		public void setUserEmailValidate(String userEmailValidate) {
			this.userEmailValidate = userEmailValidate;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserIdValidate() {
			return userIdValidate;
		}

		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}

		public String getUserIdentity() {
			return userIdentity;
		}

		public void setUserIdentity(String userIdentity) {
			this.userIdentity = userIdentity;
		}

		public String getUserLanguage() {
			return userLanguage;
		}

		public void setUserLanguage(String userLanguage) {
			this.userLanguage = userLanguage;
		}

		public String getUserLifePhoto() {
			return userLifePhoto;
		}

		public void setUserLifePhoto(String userLifePhoto) {
			this.userLifePhoto = userLifePhoto;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserPassword() {
			return userPassword;
		}

		public void setUserPassword(String userPassword) {
			this.userPassword = userPassword;
		}

		public String getUserPayPassword() {
			return userPayPassword;
		}

		public void setUserPayPassword(String userPayPassword) {
			this.userPayPassword = userPayPassword;
		}

		public String getUserPost() {
			return userPost;
		}

		public void setUserPost(String userPost) {
			this.userPost = userPost;
		}

		public String getUserPrivacyModel() {
			return userPrivacyModel;
		}

		public void setUserPrivacyModel(String userPrivacyModel) {
			this.userPrivacyModel = userPrivacyModel;
		}

		public String getUserRealname() {
			return userRealname;
		}

		public void setUserRealname(String userRealname) {
			this.userRealname = userRealname;
		}

		public String getUserSchool() {
			return userSchool;
		}

		public void setUserSchool(String userSchool) {
			this.userSchool = userSchool;
		}

		public String getUserSex() {
			return userSex;
		}

		public void setUserSex(String userSex) {
			this.userSex = userSex;
		}

		public String getUserTel() {
			return userTel;
		}

		public void setUserTel(String userTel) {
			this.userTel = userTel;
		}

		public String getUserTelValidate() {
			return userTelValidate;
		}

		public void setUserTelValidate(String userTelValidate) {
			this.userTelValidate = userTelValidate;
		}

		public String getUserTourCard() {
			return userTourCard;
		}

		public void setUserTourCard(String userTourCard) {
			this.userTourCard = userTourCard;
		}

		public String getUserTourValidate() {
			return userTourValidate;
		}

		public void setUserTourValidate(String userTourValidate) {
			this.userTourValidate = userTourValidate;
		}

		public String getUserWork() {
			return userWork;
		}

		public void setUserWork(String userWork) {
			this.userWork = userWork;
		}

		public String getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
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

	public UserMessageEntity getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(UserMessageEntity oBJECT) {
		OBJECT = oBJECT;
	}

	public List<CommentsVo> getLIST() {
		return LIST;
	}

	public void setLIST(List<CommentsVo> lIST) {
		LIST = lIST;
	}

	
}
