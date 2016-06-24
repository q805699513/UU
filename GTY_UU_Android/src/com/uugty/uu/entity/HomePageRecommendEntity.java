package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class HomePageRecommendEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<HomePageRecommend> LIST;

	public class HomePageRecommend {
		private String roadlineThemeArea;// 主题地点
		private String roadlineThemeContent;// 标签内容
		private String roadlineThemeCreateDate;
		private String roadlineThemeEndDate;
		private String roadlineThemeId;
		private String roadlineThemeImage;
		private String roadlineThemeIsValid;
		private String roadlineThemeStartDate;
		private String roadlineThemeStatus;//
		private String roadlineThemeTitle;
		private String roadlineThemeUrl;//主题城市
		private String roadlineThemeWeight;//主题权重
		public String getRoadlineThemeArea() {
			return roadlineThemeArea;
		}
		public void setRoadlineThemeArea(String roadlineThemeArea) {
			this.roadlineThemeArea = roadlineThemeArea;
		}
		public String getRoadlineThemeContent() {
			return roadlineThemeContent;
		}
		public void setRoadlineThemeContent(String roadlineThemeContent) {
			this.roadlineThemeContent = roadlineThemeContent;
		}
		public String getRoadlineThemeCreateDate() {
			return roadlineThemeCreateDate;
		}
		public void setRoadlineThemeCreateDate(String roadlineThemeCreateDate) {
			this.roadlineThemeCreateDate = roadlineThemeCreateDate;
		}
		public String getRoadlineThemeEndDate() {
			return roadlineThemeEndDate;
		}
		public void setRoadlineThemeEndDate(String roadlineThemeEndDate) {
			this.roadlineThemeEndDate = roadlineThemeEndDate;
		}
		public String getRoadlineThemeId() {
			return roadlineThemeId;
		}
		public void setRoadlineThemeId(String roadlineThemeId) {
			this.roadlineThemeId = roadlineThemeId;
		}
		public String getRoadlineThemeImage() {
			return roadlineThemeImage;
		}
		public void setRoadlineThemeImage(String roadlineThemeImage) {
			this.roadlineThemeImage = roadlineThemeImage;
		}
		public String getRoadlineThemeIsValid() {
			return roadlineThemeIsValid;
		}
		public void setRoadlineThemeIsValid(String roadlineThemeIsValid) {
			this.roadlineThemeIsValid = roadlineThemeIsValid;
		}
		public String getRoadlineThemeStartDate() {
			return roadlineThemeStartDate;
		}
		public void setRoadlineThemeStartDate(String roadlineThemeStartDate) {
			this.roadlineThemeStartDate = roadlineThemeStartDate;
		}
		public String getRoadlineThemeStatus() {
			return roadlineThemeStatus;
		}
		public void setRoadlineThemeStatus(String roadlineThemeStatus) {
			this.roadlineThemeStatus = roadlineThemeStatus;
		}
		public String getRoadlineThemeTitle() {
			return roadlineThemeTitle;
		}
		public void setRoadlineThemeTitle(String roadlineThemeTitle) {
			this.roadlineThemeTitle = roadlineThemeTitle;
		}
		public String getRoadlineThemeUrl() {
			return roadlineThemeUrl;
		}
		public void setRoadlineThemeUrl(String roadlineThemeUrl) {
			this.roadlineThemeUrl = roadlineThemeUrl;
		}
		public String getRoadlineThemeWeight() {
			return roadlineThemeWeight;
		}
		public void setRoadlineThemeWeight(String roadlineThemeWeight) {
			this.roadlineThemeWeight = roadlineThemeWeight;
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

	public List<HomePageRecommend> getLIST() {
		return LIST;
	}

	public void setLIST(List<HomePageRecommend> lIST) {
		LIST = lIST;
	}
}
