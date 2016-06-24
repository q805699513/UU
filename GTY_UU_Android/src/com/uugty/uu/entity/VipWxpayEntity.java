package com.uugty.uu.entity;

public class VipWxpayEntity {
	private String MSG;
	private String STATUS;
	private VipWxpay OBJECT;

	public class VipWxpay {
		private String out_trade_no;
		private String prepay_id;
		public String getOut_trade_no() {
			return out_trade_no;
		}

		public void setOut_trade_no(String out_trade_no) {
			this.out_trade_no = out_trade_no;
		}

		public String getPrepay_id() {
			return prepay_id;
		}

		public void setPrepay_id(String prepay_id) {
			this.prepay_id = prepay_id;
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

	public VipWxpay getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(VipWxpay oBJECT) {
		OBJECT = oBJECT;
	}

}
