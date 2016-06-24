package com.uugty.uu.entity;

import java.io.Serializable;

public class CollectRoadLineEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private CollectRoadLine OBJECT;

	public class CollectRoadLine {
		private String collectId;

		public String getCollectId() {
			return collectId;
		}

		public void setCollectId(String collectId) {
			this.collectId = collectId;
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

	public CollectRoadLine getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(CollectRoadLine oBJECT) {
		OBJECT = oBJECT;
	}

}
