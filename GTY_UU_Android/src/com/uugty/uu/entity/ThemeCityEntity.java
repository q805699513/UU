package com.uugty.uu.entity;

import java.util.List;

public class ThemeCityEntity {

	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<ThemeCity> LIST;

	public class ThemeCity {
		private String roadlineGoalArea;
		private String areaType;//（1国内，2国外）

		private String areaIsHot;//（1不是热门，2是热门）

		public String getRoadlineThemeArea() {
			return roadlineGoalArea;
		}

		public void setRoadlineThemeArea(String roadlineThemeArea) {
			this.roadlineGoalArea = roadlineThemeArea;
		}

		public String getRoadlineIsHot() {
			return areaIsHot;
		}

		public void setRoadlineIsHot(String roadlineIsHot) {
			this.areaIsHot = roadlineIsHot;
		}

		public String getAreaType() {
			return areaType;
		}

		public void setAreaType(String areaType) {
			this.areaType = areaType;
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

	public List<ThemeCity> getLIST() {
		return LIST;
	}

	public void setLIST(List<ThemeCity> lIST) {
		LIST = lIST;
	}

}
