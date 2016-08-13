package com.uugty.uu.entity;

import java.util.List;

public class BoundBankEntity {

	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<BankCardInfo> LIST;
	
	public class BankCardInfo{
		private String bankId; //银行id
		private String userId; //用户的id
		private String bankCard; //银行卡号
		private String bankCardType; //所属哪行
		private String bankIsDefault; //是否是默认提现银行卡号 1 是 ，0 否
		private String bankBoundDate; //银行绑定的日期
		private String bankOwner;//开户人姓名
		public String getBankOwner() {
			return bankOwner;
		}

		public void setBankOwner(String bankOwner) {
			this.bankOwner = bankOwner;
		}
		public String getBankId() {
			return bankId;
		}
		public void setBankId(String bankId) {
			this.bankId = bankId;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getBankCard() {
			return bankCard;
		}
		public void setBankCard(String bankCard) {
			this.bankCard = bankCard;
		}
		public String getBankCardType() {
			return bankCardType;
		}
		public void setBankCardType(String bankCardType) {
			this.bankCardType = bankCardType;
		}
		public String getBankIsDefault() {
			return bankIsDefault;
		}
		public void setBankIsDefault(String bankIsDefault) {
			this.bankIsDefault = bankIsDefault;
		}
		public String getBankBoundDate() {
			return bankBoundDate;
		}
		public void setBankBoundDate(String bankBoundDate) {
			this.bankBoundDate = bankBoundDate;
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

	public List<BankCardInfo> getLIST() {
		return LIST;
	}

	public void setLIST(List<BankCardInfo> lIST) {
		LIST = lIST;
	}
	
}
