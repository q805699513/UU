package com.uugty.uu.entity;


public class EvaluaBaseEntity {

	private String STATUS;
	private String MSG;
	private CommentsEntity OBJECT;

	public class CommentsEntity {
		private String evalId;

		public String getEvalId() {
			return evalId;
		}

		public void setEvalId(String evalId) {
			this.evalId = evalId;
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

	public CommentsEntity getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(CommentsEntity oBJECT) {
		OBJECT = oBJECT;
	}

}
