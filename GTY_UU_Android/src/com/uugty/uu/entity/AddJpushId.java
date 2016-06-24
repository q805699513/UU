package com.uugty.uu.entity;

import java.io.Serializable;

public class AddJpushId implements Serializable{

	private String STATUS;
	private String MSG;

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
