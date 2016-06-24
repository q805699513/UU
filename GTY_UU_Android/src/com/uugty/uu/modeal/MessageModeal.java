package com.uugty.uu.modeal;


public class MessageModeal {

	private String MSG;
	private String STATUS;
	private SmsEntity OBJECT;

	public class SmsEntity {
		private String userPassword;

		public String getUserPassword() {
			return userPassword;
		}

		public void setUserPassword(String userPassword) {
			this.userPassword = userPassword;
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


	public SmsEntity getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(SmsEntity oBJECT) {
		OBJECT = oBJECT;
	}
}
