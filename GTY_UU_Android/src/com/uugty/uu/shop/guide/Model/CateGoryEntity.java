package com.uugty.uu.shop.guide.Model;

import java.io.Serializable;

public class CateGoryEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private CateGory OBJECT;

	public class CateGory {
		private String countNumber;

		public String getCount() {
			return countNumber;
		}

		public void setCount(String markId) {
			this.countNumber = markId;
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

	public CateGory getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(CateGory oBJECT) {
		OBJECT = oBJECT;
	}
	
	

}
