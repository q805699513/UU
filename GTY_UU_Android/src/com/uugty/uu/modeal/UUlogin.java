package com.uugty.uu.modeal;

import java.io.Serializable;
import java.util.List;


public class UUlogin implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8558721297423039851L;
	/**
	 * 
	 */
	private String MSG;
	private String STATUS;
	private LonginInfo OBJECT;
	private List<CommentsVo> LIST;

	public class CommentsVo implements  Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1415404556731156928L;
		/**
		 * 
		 */
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

	public class LonginInfo implements  Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8221849305447032952L;
		private String userId;//用户主键
		private String userAvatar;// 用户图像
		private String userName;// 用户名
		private String userPassword;//用户手机号
		private String userDescription; // 个人描述
		private String userSchool;// 学校
		private String userLanguage;// 语言
		private String userWork;// 行业
		private String userCity; // 城市
		private String userTel;// 手机
		private String userTelValidate;// 手机号是否验证 0未验证 1验证
		private String userCertificateValidate;// 学位证
		private String userIdValidate;// 身份证
		private String userTourValidate;// 导游
		private String userCarValidate;// 车
		private String userLifePhoto;// 个人生活照，以英文逗号分割
		private String userEmail;// 邮箱
		private String userSex;// 性别 1男 2女
		private String userConstellation;// 星座
		private String userBirthday; // 出生日期
		private String commentsCount;// 评价总条数
		private String roadlineId;//路线Id;
		private String userPayPassword;//支付密码
		private String userEasemobPassword;//环信密码
		private String userIdentity;//身份证路径
		private String userCertificate;//学位证路径
		private String userTourCard;//导游证路径
		private String userCar;//车的路径
		private String userIsLogin;//地图在线
		private String markContent;//标签
		private String userIsPromoter;//是否为会员
		private String mobileCountryCode;//电话号国家代码
		private String userVipId;//会员ID
		
		
		public String getMarkContent() {
			return markContent;
		}

		public void setMarkContent(String markContent) {
			this.markContent = markContent;
		}

		public String getUserCar() {
			return userCar;
		}

		public void setUserCar(String userCar) {
			this.userCar = userCar;
		}

		public String getUserTourCard() {
			return userTourCard;
		}

		public void setUserTourCard(String userTourCard) {
			this.userTourCard = userTourCard;
		}

		public String getUserCertificate() {
			return userCertificate;
		}

		public void setUserCertificate(String userCertificate) {
			this.userCertificate = userCertificate;
		}

		public String getUserIdentity() {
			return userIdentity;
		}

		public void setUserIdentity(String userIdentity) {
			this.userIdentity = userIdentity;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserDescription() {
			return userDescription;
		}

		public void setUserDescription(String userDescription) {
			this.userDescription = userDescription;
		}

		public String getUserSchool() {
			return userSchool;
		}

		public void setUserSchool(String userSchool) {
			this.userSchool = userSchool;
		}

		public String getUserLanguage() {
			return userLanguage;
		}

		public void setUserLanguage(String userLanguage) {
			this.userLanguage = userLanguage;
		}

		public String getUserWork() {
			return userWork;
		}

		public void setUserWork(String userWork) {
			this.userWork = userWork;
		}

		public String getUserTelValidate() {
			return userTelValidate;
		}

		public void setUserTelValidate(String userTelValidate) {
			this.userTelValidate = userTelValidate;
		}

		public String getUserCertificateValidate() {
			return userCertificateValidate;
		}

		public void setUserCertificateValidate(String userCertificateValidate) {
			this.userCertificateValidate = userCertificateValidate;
		}

		public String getUserIdValidate() {
			return userIdValidate;
		}

		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}

		public String getUserTourValidate() {
			return userTourValidate;
		}

		public void setUserTourValidate(String userTourValidate) {
			this.userTourValidate = userTourValidate;
		}

		public String getUserCity() {
			return userCity;
		}

		public void setUserCity(String userCity) {
			this.userCity = userCity;
		}

		public String getUserCarValidate() {
			return userCarValidate;
		}

		public void setUserCarValidate(String userCarValidate) {
			this.userCarValidate = userCarValidate;
		}

		public String getUserLifePhoto() {
			return userLifePhoto;
		}

		public void setUserLifePhoto(String userLifePhoto) {
			this.userLifePhoto = userLifePhoto;
		}

		public String getUserTel() {
			return userTel;
		}

		public void setUserTel(String userTel) {
			this.userTel = userTel;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		public String getUserSex() {
			return userSex;
		}

		public void setUserSex(String userSex) {
			this.userSex = userSex;
		}

		public String getUserConstellation() {
			return userConstellation;
		}

		public void setUserConstellation(String userConstellation) {
			this.userConstellation = userConstellation;
		}

		public String getUserBirthday() {
			return userBirthday;
		}

		public void setUserBirthday(String userBirthday) {
			this.userBirthday = userBirthday;
		}

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

		public String getUserPayPassword() {
			return userPayPassword;
		}

		public void setUserPayPassword(String userPayPassword) {
			this.userPayPassword = userPayPassword;
		}

		public String getUserEasemobPassword() {
			return userEasemobPassword;
		}

		public void setUserEasemobPassword(String userEasemobPassword) {
			this.userEasemobPassword = userEasemobPassword;
		}

		public String getUserPassword() {
			return userPassword;
		}

		public void setUserPassword(String userPassword) {
			this.userPassword = userPassword;
		}

		public String getUserIsLogin() {
			return userIsLogin;
		}

		public void setUserIsLogin(String userIsLogin) {
			this.userIsLogin = userIsLogin;
		}

		public String getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
		}


		public String getMobileCountryCode() {
			return mobileCountryCode;
		}

		public void setMobileCountryCode(String mobileCountryCode) {
			this.mobileCountryCode = mobileCountryCode;
		}

		public String getUserVipId() {
			return userVipId;
		}

		public void setUserVipId(String userVipId) {
			this.userVipId = userVipId;
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

	public LonginInfo getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(LonginInfo oBJECT) {
		OBJECT = oBJECT;
	}

	public List<CommentsVo> getLIST() {
		return LIST;
	}

	public void setLIST(List<CommentsVo> lIST) {
		LIST = lIST;
	}

	
}
