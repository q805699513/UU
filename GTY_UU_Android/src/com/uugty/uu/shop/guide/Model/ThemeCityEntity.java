package com.uugty.uu.shop.guide.Model;

import java.util.List;

public class ThemeCityEntity {

	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<ThemeCity> LIST;

	public class ThemeCity {
		private String roadlineThemeArea;

		public String getRoadlineThemeArea() {
			return roadlineThemeArea;
		}

		public void setRoadlineThemeArea(String roadlineThemeArea) {
			this.roadlineThemeArea = roadlineThemeArea;
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
