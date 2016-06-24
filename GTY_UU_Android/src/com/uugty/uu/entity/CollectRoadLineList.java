package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;


public class CollectRoadLineList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private String OBJECT;
	private List<CollectRoadEntity> LIST;

	public class CollectRoadEntity {
		private String collectId;// 收藏的id
		private String collectTitle;// 收藏时说的话
		private String roadlineBackgroundImages;// 路线图片
		private String collectCreateDate;// 收藏的时间
		private String roadlineId;

		public String getCollectId() {
			return collectId;
		}

		public void setCollectId(String collectId) {
			this.collectId = collectId;
		}

		public String getCollectTitle() {
			return collectTitle;
		}

		public void setCollectTitle(String collectTitle) {
			this.collectTitle = collectTitle;
		}

		public String getCollectCreateDate() {
			return collectCreateDate;
		}

		public void setCollectCreateDate(String collectCreateDate) {
			this.collectCreateDate = collectCreateDate;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
		}

		public String getRoadlineBackgroundImages() {
			return roadlineBackgroundImages;
		}

		public void setRoadlineBackgroundImages(String roadlineBackgroundImages) {
			this.roadlineBackgroundImages = roadlineBackgroundImages;
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

	public String getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(String oBJECT) {
		OBJECT = oBJECT;
	}

	public List<CollectRoadEntity> getLIST() {
		return LIST;
	}

	public void setLIST(List<CollectRoadEntity> lIST) {
		LIST = lIST;
	}

}
