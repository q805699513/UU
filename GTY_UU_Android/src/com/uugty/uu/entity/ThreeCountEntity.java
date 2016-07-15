package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class ThreeCountEntity implements Serializable{

	private String STATUS;
	private String MSG;
	private Object OBJECT;
	private List<LISTBean> LIST;

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

	public Object getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(Object OBJECT) {
		this.OBJECT = OBJECT;
	}

	public List<LISTBean> getLIST() {
		return LIST;
	}

	public void setLIST(List<LISTBean> LIST) {
		this.LIST = LIST;
	}

	public static class LISTBean {
		private int num;
		private int payLevel;
		private int payPrice;
		private int sumNum;

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public int getPayLevel() {
			return payLevel;
		}

		public void setPayLevel(int payLevel) {
			this.payLevel = payLevel;
		}

		public int getPayPrice() {
			return payPrice;
		}

		public void setPayPrice(int payPrice) {
			this.payPrice = payPrice;
		}

		public int getSumNum() {
			return sumNum;
		}

		public void setSumNum(int sumNum) {
			this.sumNum = sumNum;
		}
	}
}
