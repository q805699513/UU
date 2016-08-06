package com.uugty.uu.entity;

public class AlipayEntity {
	private String STATUS;
	private String MSG;
	private AlipayRecharge OBJECT;

	public class AlipayRecharge {
		private String outTradeNo;
		private String gratutiyId;

		public String getPayInfo() {
			return payInfo;
		}

		public void setPayInfo(String payInfo) {
			this.payInfo = payInfo;
		}

		private String payInfo;

		public String getOutTradeNo() {
			return outTradeNo;
		}

		public void setOutTradeNo(String outTradeNo) {
			this.outTradeNo = outTradeNo;
		}

		public String getGratutiyId() {
			return gratutiyId;
		}

		public void setGratutiyId(String gratutiyId) {
			this.gratutiyId = gratutiyId;
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

	public AlipayRecharge getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(AlipayRecharge oBJECT) {
		OBJECT = oBJECT;
	}

    
}
