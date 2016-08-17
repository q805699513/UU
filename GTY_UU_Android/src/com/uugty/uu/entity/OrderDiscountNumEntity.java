package com.uugty.uu.entity;

public class OrderDiscountNumEntity {
	private String STATUS;
	private String MSG;
	private HintNumber OBJECT;

	public class HintNumber {
		private String orderBuyNum;//我购买的提示
		private String orderReceiverNum;//我出售的
		private String discountNum;//代金券提示
		private int ukCreateNum;//游客代付款
		private int ukPaymentNum;//游客待确认
		private int ukAgreeNum;//游客已确认
		private int xiaouCreateNum;
		private int xiaouPaymentNum;
		private int xiaouAgreeNum;
		private int ukDrawbackNum;
		private int xiaouDrawbackNum;

		public int getUkCreateNum() {
			return ukCreateNum;
		}

		public void setUkCreateNum(int ukCreateNum) {
			this.ukCreateNum = ukCreateNum;
		}

		public int getUkPaymentNum() {
			return ukPaymentNum;
		}

		public void setUkPaymentNum(int ukPaymentNum) {
			this.ukPaymentNum = ukPaymentNum;
		}

		public int getUkAgreeNum() {
			return ukAgreeNum;
		}

		public void setUkAgreeNum(int ukAgreeNum) {
			this.ukAgreeNum = ukAgreeNum;
		}

		public int getXiaouCreateNum() {
			return xiaouCreateNum;
		}

		public void setXiaouCreateNum(int xiaouCreateNum) {
			this.xiaouCreateNum = xiaouCreateNum;
		}

		public int getXiaouPaymentNum() {
			return xiaouPaymentNum;
		}

		public void setXiaouPaymentNum(int xiaouPaymentNum) {
			this.xiaouPaymentNum = xiaouPaymentNum;
		}

		public int getXiaouAgreeNum() {
			return xiaouAgreeNum;
		}

		public void setXiaouAgreeNum(int xiaouAgreeNum) {
			this.xiaouAgreeNum = xiaouAgreeNum;
		}

		public int getUkDrawbackNum() {
			return ukDrawbackNum;
		}

		public void setUkDrawbackNum(int ukDrawbackNum) {
			this.ukDrawbackNum = ukDrawbackNum;
		}

		public int getXiaouDrawbackNum() {
			return xiaouDrawbackNum;
		}

		public void setXiaouDrawbackNum(int xiaouDrawbackNum) {
			this.xiaouDrawbackNum = xiaouDrawbackNum;
		}

		public String getOrderBuyNum() {
			return orderBuyNum;
		}

		public void setOrderBuyNum(String orderBuyNum) {
			this.orderBuyNum = orderBuyNum;
		}

		public String getOrderReceiverNum() {
			return orderReceiverNum;
		}

		public void setOrderReceiverNum(String orderReceiverNum) {
			this.orderReceiverNum = orderReceiverNum;
		}

		public String getDiscountNum() {
			return discountNum;
		}

		public void setDiscountNum(String discountNum) {
			this.discountNum = discountNum;
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

	public HintNumber getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(HintNumber oBJECT) {
		OBJECT = oBJECT;
	}

}
