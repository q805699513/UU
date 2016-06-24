package com.uugty.uu.entity;

import java.util.List;

import com.uugty.uu.entity.OrderListItem.ItemEntity;

public class BillRecordEntity {

	private List<BillRecord> LIST;
	private String STATUS;
	private String MSG;

	public class BillRecord {
		private String orderId;// 订单记录id
		private String outTradeNo;// 订单流水号
		private String userId;// 操作用户的用户id
		private String bankId;// 银行id
		private String recordType;// 交易记录类型
		private String recordStatus;// 交易状态 1 进行中 ，2 成功完成 ，3 关闭
		private String recordTradeMoney;// 交易金额
		private String recordTradeDate;// 交易时间;
		private String month;// 订单的月份
		private String roadlineTitle;// 路线标题

		public String getRecordType() {
			return recordType;
		}

		public void setRecordType(String recordType) {
			this.recordType = recordType;
		}

		public String getRecordStatus() {
			return recordStatus;
		}

		public void setRecordStatus(String recordStatus) {
			this.recordStatus = recordStatus;
		}

		public String getRecordTradeMoney() {
			return recordTradeMoney;
		}

		public void setRecordTradeMoney(String recordTradeMoney) {
			this.recordTradeMoney = recordTradeMoney;
		}

		public String getRecordTradeDate() {
			return recordTradeDate;
		}

		public void setRecordTradeDate(String recordTradeDate) {
			this.recordTradeDate = recordTradeDate;
		}

		public String getRoadlineTitle() {
			return roadlineTitle;
		}

		public void setRoadlineTitle(String roadlineTitle) {
			this.roadlineTitle = roadlineTitle;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getOutTradeNo() {
			return outTradeNo;
		}

		public void setOutTradeNo(String outTradeNo) {
			this.outTradeNo = outTradeNo;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getBankId() {
			return bankId;
		}

		public void setBankId(String bankId) {
			this.bankId = bankId;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

	}

	public List<BillRecord> getLIST() {
		return LIST;
	}

	public void setLIST(List<BillRecord> lIST) {
		LIST = lIST;
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
