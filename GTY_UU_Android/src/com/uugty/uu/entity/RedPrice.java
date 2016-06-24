package com.uugty.uu.entity;

public class RedPrice {

	private String STATUS;
	private String MSG;
	private RedStauts OBJECT;

	public class RedStauts {
		private String gratuityStatus;

		public String getGratuityStatus() {
			return gratuityStatus;
		}

		public void setGratuityStatus(String gratuityStatus) {
			this.gratuityStatus = gratuityStatus;
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

	public RedStauts getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(RedStauts oBJECT) {
		OBJECT = oBJECT;
	}

}
