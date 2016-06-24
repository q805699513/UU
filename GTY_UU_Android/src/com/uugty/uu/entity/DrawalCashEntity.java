package com.uugty.uu.entity;

public class DrawalCashEntity {

	private String STATUS;
	private String MSG;
	private DrawalCash OBJECT;
	
	public class DrawalCash{
		private String withDrawId;

		public String getWithDrawId() {
			return withDrawId;
		}

		public void setWithDrawId(String withDrawId) {
			this.withDrawId = withDrawId;
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

	public DrawalCash getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(DrawalCash oBJECT) {
		OBJECT = oBJECT;
	}
	
}
