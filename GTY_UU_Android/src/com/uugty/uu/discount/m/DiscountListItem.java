package com.uugty.uu.discount.m;

import java.io.Serializable;
import java.util.List;

public class DiscountListItem {
	private List<DiscountEntity> LIST;
	private String STATUS;
	private String MSG;

	public class DiscountEntity implements Serializable{

		private String couponId;//代金券主键
		private String couponName;//代金券名称
	    private String couponMoney;//代金券金额
	    private String couponAreas;//代金券限制地区，逗号分隔开
	    private String couponOrderPrice;//代金券订单限额
	    private String couponCount;//代金券发行数量
	    private String couponRemainder;//代金券余存量
	    private String couponUserStartDate;//限制起始时间
	    private String couponUserEndDate;//限制结束时间
	    private String couponLimitWeek;//限制星期逗号分隔，如1,2,3,4,5,6,7
	    private String couponLimitDate;//限制领取后有效期几天
	    private String couponLimitHour;//限制领取后延时生效
	    private String couponCreateDate;//创建时间 
	    private String couponType;//代金券类型 
	    private String couponUserStatus;//1:待领取,2:未使用,3:已过期,4:已使用
	    private String flag;//是否即将到期 1是 0否
	    private String couponUserId;//用户和代金券关联表主键id
	    
	    
	    public String getCouponId() {
			return couponId;
		}
		public void setCouponId(String couponId) {
			this.couponId = couponId;
		}
		public String getCouponName() {
			return couponName;
		}
		public void setCouponName(String couponName) {
			this.couponName = couponName;
		}
		public String getCouponMoney() {
			return couponMoney;
		}
		public void setCouponMoney(String couponMoney) {
			this.couponMoney = couponMoney;
		}
		public String getCouponAreas() {
			return couponAreas;
		}
		public void setCouponAreas(String couponAreas) {
			this.couponAreas = couponAreas;
		}
		public String getCouponOrderPrice() {
			return couponOrderPrice;
		}
		public void setCouponOrderPrice(String couponOrderPrice) {
			this.couponOrderPrice = couponOrderPrice;
		}
		public String getCouponCount() {
			return couponCount;
		}
		public void setCouponCount(String couponCount) {
			this.couponCount = couponCount;
		}
		public String getCouponRemainder() {
			return couponRemainder;
		}
		public void setCouponRemainder(String couponRemainder) {
			this.couponRemainder = couponRemainder;
		}
		public String getCouponLimitWeek() {
			return couponLimitWeek;
		}
		public void setCouponLimitWeek(String couponLimitWeek) {
			this.couponLimitWeek = couponLimitWeek;
		}
		public String getCouponLimitDate() {
			return couponLimitDate;
		}
		public void setCouponLimitDate(String couponLimitDate) {
			this.couponLimitDate = couponLimitDate;
		}
		public String getCouponLimitHour() {
			return couponLimitHour;
		}
		public void setCouponLimitHour(String couponLimitHour) {
			this.couponLimitHour = couponLimitHour;
		}
		public String getCouponCreateDate() {
			return couponCreateDate;
		}
		public void setCouponCreateDate(String couponCreateDate) {
			this.couponCreateDate = couponCreateDate;
		}
		public String getCouponType() {
			return couponType;
		}
		public void setCouponType(String couponType) {
			this.couponType = couponType;
		}
		public String getCouponUserStatus() {
			return couponUserStatus;
		}
		public void setCouponUserStatus(String couponUserStatus) {
			this.couponUserStatus = couponUserStatus;
		}
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public String getCouponUserStartDate() {
			return couponUserStartDate;
		}
		public void setCouponUserStartDate(String couponUserStartDate) {
			this.couponUserStartDate = couponUserStartDate;
		}
		public String getCouponUserEndDate() {
			return couponUserEndDate;
		}
		public void setCouponUserEndDate(String couponUserEndDate) {
			this.couponUserEndDate = couponUserEndDate;
		}
		public String getCouponUserId() {
			return couponUserId;
		}
		public void setCouponUserId(String couponUserId) {
			this.couponUserId = couponUserId;
		}

	}

	public List<DiscountEntity> getLIST() {
		return LIST;
	}

	public void setLIST(List<DiscountEntity> lIST) {
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
