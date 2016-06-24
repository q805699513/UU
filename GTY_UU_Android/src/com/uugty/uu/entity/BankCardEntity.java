package com.uugty.uu.entity;

public class BankCardEntity {
	private String STATUS;
	private String MSG;
	private CardId OBJECT;

	public class CardId {
		private String bankId;

		public String getBankId() {
			return bankId;
		}

		public void setBankId(String bankId) {
			this.bankId = bankId;
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

	public CardId getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(CardId oBJECT) {
		OBJECT = oBJECT;
	}

}
