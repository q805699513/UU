package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class HomePageEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<HomePage> LIST;

	public class HomePage {
		private String markId;// 标签id
		private String markTitle;// 标签内容
		private String markImages;// 标签图片
		private String markSearchType;// 标签搜索方式 goal content
		private String markDate;// 标签日期生成
		private String markContent;
		private String markHeader;//头像
		private String markPrice;//价格
		private String lineNum;//浏览量
		private String isNew;//是否为最新;1为是，0为否
		private String isOnline;//是否在线1为是，0为否
		private String collectId;//是否收藏 0为否
		private String userIdValidate;//身份证是否验证
		
		
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

		public String getMarkHeader() {
			return markHeader;
		}

		public void setMarkHeader(String markHeader) {
			this.markHeader = markHeader;
		}

		public String getMarkPrice() {
			return markPrice;
		}

		public void setMarkPrice(String markPrice) {
			this.markPrice = markPrice;
		}

		public String getMarkId() {
			return markId;
		}

		public void setMarkId(String markId) {
			this.markId = markId;
		}

		public String getMarkTitle() {
			return markTitle;
		}

		public void setMarkTitle(String markTitle) {
			this.markTitle = markTitle;
		}

		public String getMarkImages() {
			return markImages;
		}

		public void setMarkImages(String markImages) {
			this.markImages = markImages;
		}

		public String getMarkSearchType() {
			return markSearchType;
		}

		public void setMarkSearchType(String markSearchType) {
			this.markSearchType = markSearchType;
		}

		public String getMarkDate() {
			return markDate;
		}

		public void setMarkDate(String markDate) {
			this.markDate = markDate;
		}

		public String getMarkContent() {
			return markContent;
		}

		public void setMarkContent(String markContent) {
			this.markContent = markContent;
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

	public List<HomePage> getLIST() {
		return LIST;
	}

	public void setLIST(List<HomePage> lIST) {
		LIST = lIST;
	}
}
