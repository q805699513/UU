package com.uugty.uu.entity;

public class UserPurseEntity {

	private UserPurse OBJECT;
	private String STATUS;
	private String MSG;

	public class UserPurse {
		private String userPurse;
		private String withDrawMoney;
		
		public String getWithDrawMoney() {
			return withDrawMoney;
		}

		public void setWithDrawMoney(String withDrawMoney) {
			this.withDrawMoney = withDrawMoney;
		}

		public String getUserPurse() {
			return userPurse;
		}

		public void setUserPurse(String userPurse) {
			this.userPurse = userPurse;
		}

	}

	public UserPurse getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(UserPurse oBJECT) {
		OBJECT = oBJECT;
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
