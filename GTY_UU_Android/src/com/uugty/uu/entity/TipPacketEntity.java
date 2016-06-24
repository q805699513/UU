package com.uugty.uu.entity;

public class TipPacketEntity {
	private String STATUS;
	private String MSG;
	private TipPacket OBJECT;

	public class TipPacket {
		private String gratuityId;// 小费id

		public String getGratuityId() {
			return gratuityId;
		}

		public void setGratuityId(String gratuityId) {
			this.gratuityId = gratuityId;
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

	public TipPacket getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(TipPacket oBJECT) {
		OBJECT = oBJECT;
	}

}
