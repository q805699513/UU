package com.uugty.uu.entity;

public class OrderEntity {

	private String STATUS;
	private String MSG;
	private OrderId OBJECT;

	public class OrderId {
		private String orderId; // 订单Id
		private String orderNo; //订单

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
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

	public OrderId getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(OrderId oBJECT) {
		OBJECT = oBJECT;
	}
	
	
}
