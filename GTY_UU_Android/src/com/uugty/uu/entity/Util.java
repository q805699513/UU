package com.uugty.uu.entity;


public class Util {
	
	public static String titleName = "";
	// 微信支付返回界面
	public static String paySuccessPage = "";
	// 分享or登录
	public static String sharWXType = "";
	// 充值金额
	public static String rechargeAmout = "";
	// 充值订单编号
	public static String tradeNo = "";
	// 红包id
	public static String red_id = "";
	// 红包留言
	public static String red_message = "";
	// 接红包人的信息
	public static String toReceive_avatar = "";
	// 红包id
	public static String toReceive_userName = "";
	// 红包留言
	public static String toReceive_userId = "";

	// 路线照相图片
	public static String pturePath = "";

	// 支付类型
	public static String pageFlag = "";
	//vip支付返回界面
	public static String vipBack="";

	// 退出系统清除
	static void clearData() {
		paySuccessPage = "";
		rechargeAmout = "";
		tradeNo = "";
	}
}
