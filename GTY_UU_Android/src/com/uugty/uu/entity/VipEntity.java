package com.uugty.uu.entity;

public class VipEntity {

	private String MSG;
	private String STATUS;
	private QueryVipEntity OBJECT;

	public class QueryVipEntity {
		private String userIsPromoter;// 0不是vip
		private String roadlineId;// 0没有路线

		public String getUserIsPromoter() {
			return userIsPromoter;
		}

		public void setUserIsPromoter(String userIsPromoter) {
			this.userIsPromoter = userIsPromoter;
		}

		public String getRoadlineId() {
			return roadlineId;
		}

		public void setRoadlineId(String roadlineId) {
			this.roadlineId = roadlineId;
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

	public QueryVipEntity getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(QueryVipEntity oBJECT) {
		OBJECT = oBJECT;
	}

}
