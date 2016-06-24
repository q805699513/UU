package com.uugty.uu.entity;


public class WXPayEntity {

	private String STATUS;
	private String MSG;
	private WXPrepayId OBJECT;

	public class WXPrepayId {
		private String appid;
		private String code_url;
		private String device_info;
		private String err_code;
		private String err_code_des;
		private String mch_id;
		private String nonce_str;
		private String sign;
		private String result_code;
		private String return_msg;
		private String gratutiyId;
		private String trade_type;
		private String prepay_id; // 支付ID
		private String out_trade_no; // 交易号

		public String getPrepay_id() {
			return prepay_id;
		}

		public void setPrepay_id(String prepay_id) {
			this.prepay_id = prepay_id;
		}

		public String getOut_trade_no() {
			return out_trade_no;
		}

		public void setOut_trade_no(String out_trade_no) {
			this.out_trade_no = out_trade_no;
		}

		public String getAppid() {
			return appid;
		}

		public void setAppid(String appid) {
			this.appid = appid;
		}

		public String getCode_url() {
			return code_url;
		}

		public void setCode_url(String code_url) {
			this.code_url = code_url;
		}

		public String getDevice_info() {
			return device_info;
		}

		public void setDevice_info(String device_info) {
			this.device_info = device_info;
		}

		public String getErr_code() {
			return err_code;
		}

		public void setErr_code(String err_code) {
			this.err_code = err_code;
		}

		public String getErr_code_des() {
			return err_code_des;
		}

		public void setErr_code_des(String err_code_des) {
			this.err_code_des = err_code_des;
		}

		public String getMch_id() {
			return mch_id;
		}

		public void setMch_id(String mch_id) {
			this.mch_id = mch_id;
		}

		public String getNonce_str() {
			return nonce_str;
		}

		public void setNonce_str(String nonce_str) {
			this.nonce_str = nonce_str;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		public String getResult_code() {
			return result_code;
		}

		public void setResult_code(String result_code) {
			this.result_code = result_code;
		}

		public String getTrade_type() {
			return trade_type;
		}

		public void setTrade_type(String trade_type) {
			this.trade_type = trade_type;
		}

		public String getReturn_msg() {
			return return_msg;
		}

		public void setReturn_msg(String return_msg) {
			this.return_msg = return_msg;
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

	public WXPrepayId getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(WXPrepayId oBJECT) {
		OBJECT = oBJECT;
	}

}
