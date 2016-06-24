package com.uugty.uu.entity;

import java.io.Serializable;

public class AppVersionCheckVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private AppVersionCheck OBJECT;

	public class AppVersionCheck {
		private String CURRVERSION; // 最新版本
		private String REDIRECTLOCATION; // 更新地址ַ
		private String STRATERY; // 更新策略 0：不需要更新 1：不强制更新 2：强制更新

		public AppVersionCheck(){
			
		}
		public String getCURRVERSION() {
			return CURRVERSION;
		}

		public void setCURRVERSION(String cURRVERSION) {
			CURRVERSION = cURRVERSION;
		}

		public String getREDIRECTLOCATION() {
			return REDIRECTLOCATION;
		}

		public void setREDIRECTLOCATION(String rEDIRECTLOCATION) {
			REDIRECTLOCATION = rEDIRECTLOCATION;
		}

		public String getSTRATERY() {
			return STRATERY;
		}

		public void setSTRATERY(String sTRATERY) {
			STRATERY = sTRATERY;
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

	public AppVersionCheck getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(AppVersionCheck oBJECT) {
		OBJECT = oBJECT;
	}

}
