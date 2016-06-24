package com.uugty.uu.entity;

import java.io.Serializable;

public class IsCollecteEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private IsCollct OBJECT;
	public class IsCollct{
		private String saidIsCollect; //收藏状态

		public String getSaidIsCollect() {
			return saidIsCollect;
		}

		public void setSaidIsCollect(String saidIsCollect) {
			this.saidIsCollect = saidIsCollect;
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
	public IsCollct getOBJECT() {
		return OBJECT;
	}
	public void setOBJECT(IsCollct oBJECT) {
		OBJECT = oBJECT;
	}
	
}
