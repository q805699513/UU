package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class MarkEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<PersonMark> LIST;

	public class PersonMark implements Serializable {
		private String markId;// 标签的id
		private String markUserId;// 标签的用户id
		private String markContent;// 标签的内容
		private String markCreateDate;// 标签的创建日期
		
		public String getMarkId() {
			return markId;
		}
		public void setMarkId(String markId) {
			this.markId = markId;
		}
		public String getMarkUserId() {
			return markUserId;
		}
		public void setMarkUserId(String markUserId) {
			this.markUserId = markUserId;
		}
		public String getMarkContent() {
			return markContent;
		}
		public void setMarkContent(String markContent) {
			this.markContent = markContent;
		}
		public String getMarkCreateDate() {
			return markCreateDate;
		}
		public void setMarkCreateDate(String markCreateDate) {
			this.markCreateDate = markCreateDate;
		}
		public PersonMark(String markId, String markContent) {
			super();
			this.markId = markId;
			this.markContent = markContent;
		}
		public PersonMark() {
			super();
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

	public List<PersonMark> getLIST() {
		return LIST;
	}

	public void setLIST(List<PersonMark> lIST) {
		LIST = lIST;
	}
	
}
