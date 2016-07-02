package com.uugty.uu.entity;

import java.util.List;

public class CountryEntity {

	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<Country> LIST;

	public class Country {
		private String name;//国家名称
		private String phoneAreaCode;//电话代码


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPhoneAreaCode() {
			return phoneAreaCode;
		}

		public void setPhoneAreaCode(String phoneAreaCode) {
			this.phoneAreaCode = phoneAreaCode;
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

	public List<Country> getLIST() {
		return LIST;
	}

	public void setLIST(List<Country> lIST) {
		LIST = lIST;
	}

}
