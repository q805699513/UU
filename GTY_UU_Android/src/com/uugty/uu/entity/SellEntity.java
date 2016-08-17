package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class SellEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private List<Sell> LIST;

	public List<Sell> getLIST() {
		return LIST;
	}

	public void setLIST(List<Sell> LIST) {
		this.LIST = LIST;
	}

	public class Sell {
		private float totalPrice;//销售额
		private float payPrice;//奖励
		private String userId;//userId为0时，totalPrice为分销销售额，payPrice为分销奖励


		public float getTotalPrice() {
			return totalPrice;
		}

		public void setTotalPrice(float totalPrice) {
			this.totalPrice = totalPrice;
		}

		public float getPayPrice() {
			return payPrice;
		}

		public void setPayPrice(int payPrice) {
			this.payPrice = payPrice;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
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


	

}
