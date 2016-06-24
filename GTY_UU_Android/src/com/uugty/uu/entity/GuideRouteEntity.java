package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class GuideRouteEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<RoadLineEntity> LIST;

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

	public String getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(String oBJECT) {
		OBJECT = oBJECT;
	}

	public List<RoadLineEntity> getLIST() {
		return LIST;
	}

	public void setLIST(List<RoadLineEntity> lIST) {
		LIST = lIST;
	}

}
