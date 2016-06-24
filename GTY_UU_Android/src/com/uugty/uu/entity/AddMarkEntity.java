package com.uugty.uu.entity;

import java.io.Serializable;

public class AddMarkEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private AddMark OBJECT;

	public class AddMark {
		private String markId;

		public String getMarkId() {
			return markId;
		}

		public void setMarkId(String markId) {
			this.markId = markId;
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

	public AddMark getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(AddMark oBJECT) {
		OBJECT = oBJECT;
	}
	
	

}
