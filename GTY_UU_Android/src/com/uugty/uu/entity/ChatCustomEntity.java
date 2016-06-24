package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class ChatCustomEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private ChatCustom OBJECT;

	public class ChatCustom {
		private String customId;// 主键id
		private String customDestination;// 目的地
		private String customPlaceOfDeparture;// 出发地
		private String customStartingTime;// 出发时间
		private String customTravelTime;// 旅游时间
		private String customTravelNum;// 出行人数
		private String customBudget;// 预算
		private String customMark;// 定制标签
		private String customTel;// 手机号
		private String customDemandContent;// 需求内容
		private String customUserId;// 用户id
		private String customRealName;// 用户真实姓名
		public String getCustomId() {
			return customId;
		}
		public void setCustomId(String customId) {
			this.customId = customId;
		}
		public String getCustomDestination() {
			return customDestination;
		}
		public void setCustomDestination(String customDestination) {
			this.customDestination = customDestination;
		}
		public String getCustomPlaceOfDeparture() {
			return customPlaceOfDeparture;
		}
		public void setCustomPlaceOfDeparture(String customPlaceOfDeparture) {
			this.customPlaceOfDeparture = customPlaceOfDeparture;
		}
		public String getCustomStartingTime() {
			return customStartingTime;
		}
		public void setCustomStartingTime(String customStartingTime) {
			this.customStartingTime = customStartingTime;
		}
		public String getCustomTravelTime() {
			return customTravelTime;
		}
		public void setCustomTravelTime(String customTravelTime) {
			this.customTravelTime = customTravelTime;
		}
		public String getCustomTravelNum() {
			return customTravelNum;
		}
		public void setCustomTravelNum(String customTravelNum) {
			this.customTravelNum = customTravelNum;
		}
		public String getCustomBudget() {
			return customBudget;
		}
		public void setCustomBudget(String customBudget) {
			this.customBudget = customBudget;
		}
		public String getCustomMark() {
			return customMark;
		}
		public void setCustomMark(String customMark) {
			this.customMark = customMark;
		}
		public String getCustomTel() {
			return customTel;
		}
		public void setCustomTel(String customTel) {
			this.customTel = customTel;
		}
		public String getCustomDemandContent() {
			return customDemandContent;
		}
		public void setCustomDemandContent(String customDemandContent) {
			this.customDemandContent = customDemandContent;
		}
		public String getCustomUserId() {
			return customUserId;
		}
		public void setCustomUserId(String customUserId) {
			this.customUserId = customUserId;
		}
		public String getCustomRealName() {
			return customRealName;
		}
		public void setCustomRealName(String customRealName) {
			this.customRealName = customRealName;
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

	public ChatCustom getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(ChatCustom oBJECT) {
		OBJECT = oBJECT;
	}
}
