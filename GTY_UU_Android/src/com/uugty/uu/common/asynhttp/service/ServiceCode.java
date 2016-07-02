package com.uugty.uu.common.asynhttp.service;

/**
 * 请求地址常量集合
 */

public class ServiceCode {

	// 图片的缩放比例
	public static float FIT_LINE = 0.5833F;
	// 下载图片的size
	public static long PICTURE_LOAD_MIN = 1024l;
	// 下载图片次数
	public static int PICTURE_LOAD_TIME = 5;
	// 测试
	public static final String USER_LOGIN = "test.do";
	// 版本检测
	public static final String VERSION_CHECK = "userCheckVersion.do";
	// 退出系统
	public static final String USER_LOGIN_OUT = "userLogout.do";
	// 登陆接口
	public static final String UULOGIN_INTERFACE = "userLogin.do";
	// 微信登录
	public static final String USER_WECHAR_LOGIN = "userWeChartLogin.do";
	// 注册接口
//	public static final String UUREGSTER_INTERFACE = "userRegister.do";//国际短信修改
	public static final String UUREGSTER_INTERFACE = "uukRegister.do";
	// 忘记密码
	public static final String USER_FORGET_PWD = "userForgetPassword.do";
	// 发送验证码接口
	//public static final String UUCODEY_INTERFACE = "userVerificationCode.do";//国际短信修改
	public static final String UUCODEY_INTERFACE = "gVerificationCode.do";
	//获取国家名称和电话区域代码
	public static final String COUNTRY_CODE ="queryCountryCode.do";
	// 图片上传
	public static final String UPLOAD_FILE = "userFileupload.do";
	//新图片上传
	public static final String NEW_UPLOAD_FILE = "fileupload.do";
	// 修改个人信息
	public static final String USER_INFO = "userModifyMessage.do";
	// 发布路线
	public static final String ROAD_LINE = "roadlinePublish.do";
	// 路线修改
	public static final String ROAD_LINE_MODIFY = "roadlineModify.do";
	// 地图附近的人
	public static final String MAP_USER_LOCARION = "userLocationSearch.do";
	// 大喊一声插入值
	public static final String MAP_USER_TEMP_LOGIN = "userTempLogin.do";
	// 小费支付
	public static final String ORDER_PURSE_PAYMENT = "orderGratuityPursePayment.do";
	// 下订单
	public static final String PLACE_THE_ORDER = "orderReservations.do";
	// 订单支付
	public static final String ORDER_PAYMENT = "orderTourPursePayment.do";
	// 订单列表
	public static final String ORDER_LIST = "orderList.do";
	// 订单详情
	public static final String ORDER_DETAIL = "orderDetailMessage.do";
	// 订单修改
	public static final String ORDER_MODIFY = "orderModify.do";
	// 取消订单
	public static final String ORDER_CANCLE = "orderCancel.do";
	// 删除订单
	public static final String Del_Order = "deleteInvalidOrder.do";
	// 确认订单
	public static final String ORDER_INVITATION = "orderInvitation.do";
	// 订单完成
	public static final String ORDER_COMPLETE = "orderComplete.do";
	// 订单评价
	public static final String ORDER_EVALUATION = "orderEvaluation.do";
	// 订单评价详情
	public static final String ORDER_EVALUATION_DETAIL = "orderEvaluationDetail.do";
	// 申请退款
	public static final String ORDER_DRAWBACK = "orderDrawback.do";
	// 我的钱包
	public static final String USER_PURSE = "userPurse.do";
	// 银行卡列表
	public static final String USER_BOUND_BANK_CARDLIST = "userBoundBankCardList.do";
	// 删除银行卡
	public static final String DEL_BANK_CARD = "deleteUserBankCard.do";
	// 绑定银行卡
	public static final String USER_BOUND_BANK_CARD = "userBoundBankCard.do";
	// 设置支付密码
	public static final String SET_USER_PAY_PWD = "userPayPassword.do";
	// 用户提现
	public static final String USER_WITH_DRAW = "userWithdraw.do";
	// 账单列表
	public static final String BILL_RECORD_LIST = "orderRecord.do";
	// 评论列表
	public static final String USER_COMMENT_LIST = "commentList.do";
	// 评论回复
	public static final String USER_COMMENT_REPLY = "commentReply.do";
	// homePage
	public static final String ROAD_LINE_MARK_INDEXT = "roadlineMarkIndex.do";
	//主题
	public static final String HOME_PAGE_RECOMMEND = "topicRecommend.do";
	// 检索路线
	public static final String ROAD_LINE_SEARCH = "roadlineSearch.do";
	public static final String ROAD_LINE_SEARCH_CITY = "roadlineSearchCity.do";
	// 主题搜索
	public static final String RECOMEND_THEME_DETAIL = "themeDetails.do";
	// 路线详细
	public static final String ROAD_LINE_DETAIL_MESSAGE = "roadlineDetailMessage.do";
	// 多条路线
	public static final String ROAD_LINE_ALL_LISTSIMPLE = "roadlineAllListSimple.do";
	// 分页多条路线
	public static final String ROAD_LINE_ALL_LINE = "roadlineAllList.do";
	// 收藏路线
	public static final String COLLECT_ROAD_LINE = "collectRoadline.do";
	// 取消收藏
	public static final String COLLECT_CANCEL_ROAD_LINE = "collectCancelRoadline.do";
	// 收藏列表
	public static final String COLLECT_ROAD_LINE_LIST = "collectRoadlineList.do";
	// 微信充值
	public static final String WALLET_RECHARGE = "orderCharge.do";
	// 支付宝获取充值ID
	public static final String ALIPAY_ORDER_RESERVATIONS = "OrderReservationsCharge.do";
	// 支付宝获取红包ID
	public static final String ALIPAY_GRATUITY_RESERVATIONS = "OrderReservationsGratuity.do";
	//支付宝无用接口
	public static final String ALIPAY_TOUR_PAYMENT = "orderTourAliPayment.do";
	// 获取用户详细信息接口
	public static final String USER_INFO_MESSAGE = "userDetailMessage.do";
	// 获取用户基本信息
	public static final String USER_SIMPLE_MESSAGE = "userSimpleMessage.do";
	// 领取红包的接口
	public static final String USER_RED_PRICE = "orderGratuityReceive.do";
	// 红包状态
	public static final String USER_RED_PRICE_STATUS = "orderGratuityStatus.do";
	// 路线的微信支付
	public static final String WX_ORDER_TOUR_PAYMENT = "orderTourWeChartPayment.do";
	// 红包微信支付
	public static final String WX_ORD_PRICE = "orderGratuityWeChartPayment.do";
	// 微信小费的支付
	public static final String USER_WECGAT_PAY_RED_PRICE = "orderGratuityWeChartPayment.do";
	// 微信充值取消
	public static final String WX_CHARGE_CANCLE = "orderChargeClose.do";
	// 获取好友列表
	public static final String UU_FRIEND_LIST = "easemobUser.do";
	// 获取导游自身发布的路线
	public static final String ROAD_LINE_LIST = "roadlineList.do";
	// 路线发布权限接口
	public static final String ROAD_LINE_PUBLISH_PERMISSION = "roadlinePublishPermission.do";
	// 刷新个人数据
	public static final String FEFRESH_PERSON_DATA = "userRefershData.do";
	//标签首页展示
	public static final String DISCOVERYMAIN="discoveryMain.do";
	//导游订单列表
	public static final String XIAOUORDERLIST="xiaoUOrderList.do";
	//游客订单列表
	public static final String UKORDERLIST="ukOrderList.do";
	//群组列表
	public static final String GROUP_CHAT_LIST="getJoinedChatGroups.do";
	//群组简易信息
	public static final String GROUP_DETAIL_SIMPLE="simpleGroupDetail.do";
	//群组详细信息
	public static final String GROUP_DETAIL_INFO="groupDetail.do";
	//群组成员
	public static final String GROUP_CHAT_MEMBERS="getAllMembers.do";
	//退群
	public static final String GROUP_CHAT_EXIT="deleteUserFromGroup.do";
	//入群
	public static final String GROUP_CHAT_ADD="addUserToGroup.do";
	//热门群组
	public static final String GROUP_CHAT_HOT="getAllChatgroup.do";
	//搜索群
	public static final String GROUP_CHAT_SEARCH="searchGroup.do";
	//创建群组
	public static final String GROUP_CHAT_CREATE="createChatGroup.do";
	// 发送动态
	public static final String SEND_TALK="releaseStatus.do";	
	// 朋友圈
	public static final String DYNAMIC_DETAILS="dynamicDetails.do";
	//朋友圈 点赞 
	public static final String  UP_VOTE="upvote.do";
	//朋友圈 举报  
	public static final String  COMPLAINT_USER="complaintUser.do";
	//评论列表 
	public static final String  COMMENT_DETAILS="replayFriendSaid.do";
	//朋友圈 评论回复
	public static final String  ADD_FRIENDREPLAY="addFriendReplay.do";
	//朋友圈 评论
	public static final String  COMMENT_FRIENDSAID="commentFriendSaid.do";
	//朋友圈 删除
	public static final String  DELETE_FRIENDSAID="deleteFriendSaid.do";
	//浏览量 添加
	public static final String  VIEW_DETAILS="viewDetails.do";
	//帖子收藏
	public static final String  COLLECT_FROENDSAID="collectFriendSaid.do";
	//帖子收藏列表
	public static final String  COLLECT_FRIENDSAID_LIST="collectFriendSaidList.do";
	//查询收藏状态
	public static final String  QUERY_COLLECT_FRIENDSAID="queryCollectFriendSaid.do";
	//收藏的帖子 删除
	public static final String  COLLECT_CANCEL_FRIENDSAID="collectCancelFriendSaid.do";
	//添加好友
	public static final String  ADD_FRIENDS="addFriend.do";
	//主题城市
//	public static final String THEME_CITY="cityList.do";
	public static final String THEME_CITY="searchCityList.do";
	//删除服务
	public static final String ROAD_LINE_DELETE="roadlineDelete.do";
	//添加游客身份信息
	public static final String ADD_CONTACT="addContact.do";
	//查询常用联系人接口
	public static final String QUERY_CONTACT="queryContact.do";
	//修改常用联系人接口
	public static final String MODIFY_CONTACT="modifyContact.do";
	//提交订单
	public static final String ORDER_RESERVATION_LIST="batchOrderReservations.do";
	//订单详情
	public static final String BATCH_ORDERDETAIL_MESSAGE="batchOrderDetailMessage.do";
	//修改订单
	public static final String GET_ORDERDETAIL_MESSAGE = "getOrderDetailMessage.do";
	//修改订单
	public static final String BATCH_ORDER_MODIFY="batchOrderModify.do";
	//游客订单列表
	public static final String UK_BATCH_ORDER_LIST="ukBatchOrderList.do";
	//小U订单订单列表
	public static final String XIAOU_BATCH_ORDERLIST="xiaoUBatchOrderList.do";
	//查询点赞人
	public static final String QUERY_UPVOTERS="queryUpvoters.do";
	//添加标签
	public static final String ADD_USERMARK="addUserMark.do";
	//查询标签
	public static final String QUERY_USERMARK="queryUserMark.do";
	//删除标签
	public static final String DELETE_USERMARK="deleteUserMark.do";
	//本地咨询
	public static final String QUERY_CONSULT_USERLIST="queryConsultUserList.do";
	//闪屏页
	public static final String QUERY_BOOT_BACKGROUND="queryBootBackground.do";
	//旅游定制
	public static final String ADD_TRAVEL_CUSTOM="addTravelCustom.do";
	//查询旅游定制
	public static final String QUERY_TRAVEL_CUSTOM="queryTravelCustom.do";
	//是否是VIP
	public static final String QUERY_IS_VIP="queryIsVip.do";
	//vip支付宝支付
	public static final String APP_ALI_JOIN_FEE="appAliJoinFee.do";
	//vip微信支付
	public static final String APP_WX_JOIN_FEE="appWxJoinFee.do";
	//父级
	public static final String QUERY_PARENT_ID="queryParentId.do";
	//代金券
	public static final String DISCOUNT_QUERY = "queryUserCoupon.do";
	//领取代金券
	public static final String DISCOUNT_REC = "addCoupon.do";
	//订单填写代金券过滤
	public static final String DISCOUNT_FILTER = "getFilterCoupon.do";
	//首页标签新接口
	public static final String TAG_LIST = "tagList.do";
	//通知获取回掉
	public static final String JPUSH_REQUEST = "queryCommentByMsgId.do";
	//极光推送上传registId
	public static final String PUSH_ID = "addNoLoginJPush.do";
}
