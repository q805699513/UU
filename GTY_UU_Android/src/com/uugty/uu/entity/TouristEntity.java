package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class TouristEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<Tourist> LIST;

	public class Tourist implements Serializable {
		private String userId;// 用户id
	    private String contactName;// 常用联系人姓名
	    private String contactIDCard;//常用联系人身份证号
	    private String contactId;//主键id
	    private String contactStatus;//状态
	    private String contactCreateDate;//创建时间
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
		public String getContactIDCard() {
			return contactIDCard;
		}
		public void setContactIDCard(String contactIDCard) {
			this.contactIDCard = contactIDCard;
		}
		
		public String getContactId() {
			return contactId;
		}
		public void setContactId(String contactId) {
			this.contactId = contactId;
		}
		public String getContactStatus() {
			return contactStatus;
		}
		public void setContactStatus(String contactStatus) {
			this.contactStatus = contactStatus;
		}
		public String getContactCreateDate() {
			return contactCreateDate;
		}
		public void setContactCreateDate(String contactCreateDate) {
			this.contactCreateDate = contactCreateDate;
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

	public String getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(String oBJECT) {
		OBJECT = oBJECT;
	}

	public List<Tourist> getLIST() {
		return LIST;
	}

	public void setLIST(List<Tourist> lIST) {
		LIST = lIST;
	}
	
	
}
