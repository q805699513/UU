package com.uugty.uu.entity;

public class VipAlipayEntity {
	private String MSG;
	private String STATUS;
	private VipAlipay OBJECT;

	public class VipAlipay {
		private String orderNo;
		private String orderId;

		public String getPayInfo() {
			return payInfo;
		}

		public void setPayInfo(String payInfo) {
			this.payInfo = payInfo;
		}

		private String payInfo;

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
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

	public VipAlipay getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(VipAlipay oBJECT) {
		OBJECT = oBJECT;
	}

}
