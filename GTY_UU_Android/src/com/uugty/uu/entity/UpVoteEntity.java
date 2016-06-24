package com.uugty.uu.entity;

import java.io.Serializable;

public class UpVoteEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private UpVote OBJECT;
	public class UpVote{
		private String saidId; // 用户id
		private String  saidGoodTimes;//（点赞次数）
		private String saidUpvoteStatus;//（点赞状态）;
		public String getSaidId() {
			return saidId;
		}
		public void setSaidId(String saidId) {
			this.saidId = saidId;
		}
		public String getSaidGoodTimes() {
			return saidGoodTimes;
		}
		public void setSaidGoodTimes(String saidGoodTimes) {
			this.saidGoodTimes = saidGoodTimes;
		}
		public String getSaidUpvoteStatus() {
			return saidUpvoteStatus;
		}
		public void setSaidUpvoteStatus(String saidUpvoteStatus) {
			this.saidUpvoteStatus = saidUpvoteStatus;
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
	public UpVote getOBJECT() {
		return OBJECT;
	}
	public void setOBJECT(UpVote oBJECT) {
		OBJECT = oBJECT;
	}
	
}
