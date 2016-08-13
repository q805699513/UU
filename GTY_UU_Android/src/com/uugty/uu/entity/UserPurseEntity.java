package com.uugty.uu.entity;

public class UserPurseEntity {

	private UserPurse OBJECT;
	private String STATUS;
	private String MSG;

	public class UserPurse {
		private String userPurse;
		private String withDrawMoney;
		private String bankCard;
		private String bankId;
		private String bankCardType;// 银行卡号的类型

		public String getBankCard() {
			return bankCard;
		}

		public void setBankCard(String bankCard) {
			this.bankCard = bankCard;
		}

		public String getBankId() {
			return bankId;
		}

		public void setBankId(String bankId) {
			this.bankId = bankId;
		}

		public String getBankCardType() {
			return bankCardType;
		}

		public void setBankCardType(String bankCardType) {
			this.bankCardType = bankCardType;
		}
		
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
