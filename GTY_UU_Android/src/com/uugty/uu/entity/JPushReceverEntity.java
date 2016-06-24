package com.uugty.uu.entity;

import java.io.Serializable;

public class JPushReceverEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private Content OBJECT;

	public class Content {
		private String pushComment;

		public String getContent() {
			return pushComment;
		}

		public void setContent(String markId) {
			this.pushComment = markId;
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

	public Content getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(Content oBJECT) {
		OBJECT = oBJECT;
	}
	
	

}
