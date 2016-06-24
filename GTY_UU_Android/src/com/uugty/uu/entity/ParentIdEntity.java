package com.uugty.uu.entity;

public class ParentIdEntity {
	private String MSG;
	private String STATUS;
	private ParentId OBJECT;

	public class ParentId {
		private String parentId;

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
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

	public ParentId getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(ParentId oBJECT) {
		OBJECT = oBJECT;
	}

}
