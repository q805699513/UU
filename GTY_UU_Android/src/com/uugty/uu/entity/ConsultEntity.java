package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class ConsultEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<Consult> LIST;

	public class Consult implements Serializable {
		private String markId;// 标签的id	    
	    private String userId;// 用户id	    
	    private String markContent;// 标签的内容	    
	    private String markCreateDate;// 标签的创建日期	    
	    private String userCity;// 用户的城市	    
	    private String userAvatar;// 用户头像	    
	    private String userIdValidate;// 是否验证身份证	   2 
	    private String userDescription;// 用户签名	    
	    private String userName;// 用户名	    
	    private String isOnline;// 是否在线	    1在  0否
	    private String favourableCommentNum;// 好评数	    
	    private String userTourValidate;// 导游证是否验证	2    
	    private String userCertificateValidate;// 学位证是否验证	2    
	    private String userCarValidate; // 驾驶证是否验证 2
	    private String userIsPromoter;//是否支付199 1为支付
		public String getMarkId() {
			return markId;
		}
		public void setMarkId(String markId) {
			this.markId = markId;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getMarkContent() {
			return markContent;
		}
		public void setMarkContent(String markContent) {
			this.markContent = markContent;
		}
		public String getMarkCreateDate() {
			return markCreateDate;
		}
		public void setMarkCreateDate(String markCreateDate) {
			this.markCreateDate = markCreateDate;
		}
		public String getUserCity() {
			return userCity;
		}
		public void setUserCity(String userCity) {
			this.userCity = userCity;
		}
		public String getUserAvatar() {
			return userAvatar;
		}
		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}
		public String getUserIdValidate() {
			return userIdValidate;
		}
		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}
		public String getUserDescription() {
			return userDescription;
		}
		public void setUserDescription(String userDescription) {
			this.userDescription = userDescription;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getIsOnline() {
			return isOnline;
		}
		public void setIsOnline(String isOnline) {
			this.isOnline = isOnline;
		}
		public String getFavourableCommentNum() {
			return favourableCommentNum;
		}
		public void setFavourableCommentNum(String favourableCommentNum) {
			this.favourableCommentNum = favourableCommentNum;
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
		public String getUserIsPromoter() {
			return userIsPromoter;
		}
		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
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

	public List<Consult> getLIST() {
		return LIST;
	}

	public void setLIST(List<Consult> lIST) {
		LIST = lIST;
	}
	
	

}
