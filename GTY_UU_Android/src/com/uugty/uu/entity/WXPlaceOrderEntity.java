package com.uugty.uu.entity;


public class WXPlaceOrderEntity {

	private String MSG;
	private String STATUS;
	private WXPlaceOrder OBJECT;
	
	public class WXPlaceOrder{
		
		private String prepay_id;//支付Id
		private String sign;
		public String getPrepay_id() {
			return prepay_id;
		}
		public void setPrepay_id(String prepay_id) {
			this.prepay_id = prepay_id;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
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

	public WXPlaceOrder getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(WXPlaceOrder oBJECT) {
		OBJECT = oBJECT;
	}

	
	
	
}
