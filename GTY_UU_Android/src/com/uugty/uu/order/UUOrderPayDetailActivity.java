package com.uugty.uu.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.ListViewForScrollView;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.OrderDetailEntity;
import com.uugty.uu.entity.OrderDetailEntity.OrderDetail;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.TouristEntity;
import com.uugty.uu.evaluate.EvaluateActivity;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class UUOrderPayDetailActivity extends BaseActivity implements
		OnClickListener {

	private SimpleDraweeView orderDetailImage;//订单封面图片
	private TextView mDetailTopic;//订单标题
	private TextView mOrderState;//订单状态
	private TextView mOrderPrice;//订单价格
	private TextView mApplyRetrun;//申请退款
	private TextView mScheduleNum;//预定数量
	private TextView mJourneyDate;//出行时间
	private TextView mPayQuota;//支付金额
	private TextView mPayQuota1;
	private TextView mTourist;//游客
	private TextView mOrderNum;//订单编号
	private TextView mScheduDate;//预定日期
	private TextView mContactPerson;//联系人
	private TextView mPhone;//手机号
	private TextView mChatText;//底部固定存在的聊一聊
	private TextView mTv2;//底部变化的第二个textview
	private TextView mTv3;//底部变化的第三个textView
	private TextView mTv4;//底部变化的第四个texView
	private LinearLayout mLinearTop;//顶部订单详情展示
	private LinearLayout mLinearReturn;//退款状态时需要显示的控件
	private TextView mReturnMoney;//退款金额
	private TextView mReturnReson;//退款原因
	private LinearLayout mLinearDetail;//订单详情
	private RelativeLayout mRelativeTourist;//游客信息
	private LinearLayout mLinearContact;//联系人
	private LinearLayout mLinearBottom;//底部
	private LinearLayout mLinearDrawback;//退款结束的组件
	private LinearLayout mLinearReturnTv;//退款状态底部text
	private TextView mReturnAcept;//退款按钮左1
	private TextView mReturnCancle;//退款按钮左2
	private TextView mReturnChat;//聊天联系
	private ImageView mReturnImg;//退款图标
	private TextView mReturnTopic;//退款标题
	private TextView mReturnTv1;
	private TextView mReturnTv2;
	private TextView mReturnTv11;
	private TextView mReturnTv22;
	
	private RelativeLayout mOrderTotileLayout;//订单总价布局
	private RelativeLayout mOrderDiscountLayout;//代金券布局
	private RelativeLayout mOrderPayLayout;//支付布局
	private TextView mOrderTotile;//订单总价
	private TextView mDiscount;//代金券金额
	private TextView mOrderDetailImg;//三角图片
	private int clickTime = 1;//点击次数
	
	private String orderId;//订单编号
	private String mStateString;//订单状态字符串
	private String mUserId;//用户ID
	private String mHeadPic;//用户头像
	private String mUserName;//用户姓名
	private String mIsComment;//是否评价
	private String mFragment;//跳转上一层
	private String mRole;//1为购买，2为出售
	private String mRoadLineId;//路线ID
	private TopBackView titleback;
	private SpotsDialog loadingDialog;
	private ScrollView scrollView;

	//保险
	private ListViewForScrollView mContactListView;
	
	protected int getContentLayout() {
		return R.layout.activity_order_paydetails;// 订单详情
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			orderId = getIntent().getStringExtra("orderId");//订单编号，请求数据接口参数
			mIsComment = getIntent().getStringExtra("isComment");//是否评价
			mFragment = getIntent().getStringExtra("fragment");
			mRole = getIntent().getStringExtra("role");
		}		
		orderDetailImage = (SimpleDraweeView) findViewById(R.id.pay_detail_img);//订单图片
		mDetailTopic = (TextView) findViewById(R.id.detail_topic);//订单标题
		mOrderState = (TextView) findViewById(R.id.pay_wait_acept_state);//订单状态
		mOrderPrice = (TextView) findViewById(R.id.pay_wait_acept_price);//订单价格
		mApplyRetrun = (TextView) findViewById(R.id.order_detail_pay);//申请退款
		mScheduleNum = (TextView) findViewById(R.id.detail_order_num);//预定数量
		mJourneyDate = (TextView) findViewById(R.id.order_time);//出行时间
		mPayQuota = (TextView) findViewById(R.id.order_num);//支付金额
		mPayQuota1 = (TextView) findViewById(R.id.order_num1);//支付金额
		mTourist = (TextView) findViewById(R.id.uu_reserve_number);//游客
		mOrderNum = (TextView) findViewById(R.id.detail_order_pay_num);//订单编号
		mScheduDate = (TextView) findViewById(R.id.detail_order_time);//预定日期
		mContactPerson = (TextView) findViewById(R.id.detail_order_name);//联系人你
		mPhone = (TextView) findViewById(R.id.detail_order_phone);//手机号
		mChatText = (TextView)findViewById(R.id.order_chat);//聊一聊
		mLinearTop = (LinearLayout)findViewById(R.id.orderDetail_Top);//顶部
		
		mLinearReturn = (LinearLayout)findViewById(R.id.activity_return);//退款
		mReturnMoney = (TextView)findViewById(R.id.detail_return_num);//退款金额
		mReturnReson = (TextView)findViewById(R.id.order_return_reson);
		
		mLinearReturnTv = (LinearLayout)findViewById(R.id.activity_return_linear);
		mReturnAcept = (TextView)findViewById(R.id.order_return_acept);//左一
		mReturnCancle = (TextView)findViewById(R.id.order_return_cancle);//左二
		mReturnChat = (TextView)findViewById(R.id.order_return_chat);//左三
		
		mLinearDetail = (LinearLayout)findViewById(R.id.activity_paypricea_add_person);//订单详情
		mRelativeTourist = (RelativeLayout)findViewById(R.id.orderDetail_tourist);//游客信息
		mLinearContact = (LinearLayout)findViewById(R.id.activity_del);//联系人
		mLinearBottom = (LinearLayout)findViewById(R.id.orderDetail_bottom);//底部
		
		mLinearDrawback = (LinearLayout)findViewById(R.id.return_top);
		mReturnImg = (ImageView)findViewById(R.id.return_top_Img);//退款图标
		mReturnTopic = (TextView)findViewById(R.id.return_top_topic);//退款标题
		mReturnTv1 = (TextView)findViewById(R.id.return_top_tv1);
		mReturnTv2 = (TextView)findViewById(R.id.return_top_tv2);
		mReturnTv11 = (TextView)findViewById(R.id.return_top_tv11);
		mReturnTv22 = (TextView)findViewById(R.id.return_top_tv22);
		
		mTv2 = (TextView)findViewById(R.id.order_pay);
		mTv3 = (TextView)findViewById(R.id.order_cancle);
		mTv4 = (TextView)findViewById(R.id.order_evaluate);		
		
		mOrderTotileLayout = (RelativeLayout) findViewById(R.id.order_detail_total_layout);
		mOrderDiscountLayout = (RelativeLayout) findViewById(R.id.order_detail_discount_layout);
		mOrderPayLayout = (RelativeLayout) findViewById(R.id.order_detail_layout);
		mOrderTotile = (TextView) findViewById(R.id.order_total_num);
		mDiscount = (TextView) findViewById(R.id.order_discount);
		mOrderDetailImg = (TextView) findViewById(R.id.order_detail_image);
		
		titleback = (TopBackView) findViewById(R.id.order_pay_top_title_detail_back);
		titleback.setTitle("订单详情");
		scrollView = (ScrollView) findViewById(R.id.activity_orderpays_scrollview);
		scrollView.setVisibility(View.INVISIBLE);

		mContactListView = (ListViewForScrollView) findViewById(R.id.orderdetail_contact_list);
		mContactListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
	}

	@Override
	protected void initAction() {
		getContentInit();//获取数据
		if("finish".equals(mFragment)){
			if("1".equals(mRole)){
				mTv2.setVisibility(View.VISIBLE);
				mTv2.setText("取消订单");
			}else{
				mTv2.setVisibility(View.VISIBLE);
				mTv3.setVisibility(View.VISIBLE);
				mTv2.setText("拒绝");
				mTv3.setText("接受");
			}
		}
		if("confirm".equals(mFragment)){
			if("1".equals(mRole)){
				mApplyRetrun.setVisibility(View.VISIBLE);
				mTv2.setVisibility(View.VISIBLE);
				mTv3.setVisibility(View.VISIBLE);
				if ("0".equals(mIsComment)) {
					mTv2.setText("评价");
				} else {
					mTv2.setText("修改评价");
				}
				mTv3.setText("旅行完成");
			}
		}
		
		if("after".equals(mFragment)){
			mTv2.setVisibility(View.VISIBLE);
			if("1".equals(mRole)){
				if ("0".equals(mIsComment)) {
					mTv2.setText("评价");
				} else {
					mTv2.setText("修改评价");
				}
			}else{
				mApplyRetrun.setVisibility(View.VISIBLE);
				mApplyRetrun.setText("退款中");
				mTv2.setText("同意退款");
			}
		}
		
		mPhone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 弹出框，确认删除
				CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
				builder.setMessage(mPhone.getText().toString());
				builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setData(Uri.parse("tel:" + mPhone.getText().toString()));
						intent.setAction(Intent.ACTION_CALL);
						startActivity(intent);
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
		
		//聊一聊点击事件
		mChatText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("userId", mUserId);
				intent.putExtra("avatar", mHeadPic);
				intent.putExtra("userName",mUserName);
				intent.setClass(UUOrderPayDetailActivity.this, ChatActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 发送取消订单的请求
	 * 
	 * @param status 订单状态
	 */
	public void sendVisitorRequst(final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("OrderStatus", status);
		APPRestClient.post(this, ServiceCode.ORDER_CANCLE, params,
				new APPResponseHandler<OrderEntity>(OrderEntity.class, this) {
					@Override
					public void onSuccess(OrderEntity result) {
						// 不做任何操作，给后台发送取消订单的请求
						new Handler().postDelayed(new Runnable(){   
				            public void run() {  
				                   //显示dialog
				            	loadingDialog.dismiss();
				            	finish();
				            }  
				        }, 500);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						
						if (errorCode == 3) {
							sendVisitorRequst(status);
						} else {
							loadingDialog.dismiss();
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(UUOrderPayDetailActivity.this).setTitle("提示").setMessage("网络拥堵,请稍后重试！")
										.setPositiveButton("确定", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										}).show();
							}

						}
					}

					@Override
					public void onFinish() {
					}
				});
	}

	private void getContentInit() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 订单的状态

		APPRestClient.post(this, ServiceCode.BATCH_ORDERDETAIL_MESSAGE, params,
				new APPResponseHandler<OrderDetailEntity>(
						OrderDetailEntity.class, this) {
					@Override
					public void onSuccess(OrderDetailEntity result) {
						if (result != null) {
							final OrderDetail detail = result.getOBJECT();
							if (detail.getUserAvatar() != null) {
								ImageLoader
										.getInstance()
										.displayImage(
												APPRestClient.SERVER_IP
														+ "images/roadlineDescribe/"
														+ detail.getRoadlineBackground(),
												orderDetailImage);
							}
							if(detail.getContactNum().equals("0")){
								mTourist.setText("未添加联系人");
							}else{
								mTourist.setText(detail.getContactName().replace(",", " "));
							}
							//保险
							List<TouristEntity.Tourist> mTouristList = new ArrayList<TouristEntity.Tourist>();

							if(result.getLIST().size() > 0){
								if("1".equals(result.getLIST().get(0).getInsuranceType())) {
									mTourist.setText("初级保险: ￥5/天");
								}else if("2".equals(result.getLIST().get(0).getInsuranceType())){
									mTourist.setText("中级保险: ￥10/天");
								}else if("3".equals(result.getLIST().get(0).getInsuranceType())){
									mTourist.setText("高级保险: ￥15/天");
								}else{
									mTourist.setText("未选择");
								}
								for(int i=0;i<result.getLIST().size();i++){
									TouristEntity entity = new TouristEntity();
									TouristEntity.Tourist tour = entity.new Tourist();
									tour.setContactIDCard(result.getLIST().get(i).getContactIDCard());
									tour.setContactId(result.getLIST().get(i).getContactId());
									tour.setContactName(result.getLIST().get(i).getContactName());
									mTouristList.add(tour);
								}
							}

							OrderDetailTouristAdapter mTouristAdapter = new OrderDetailTouristAdapter(ctx,mTouristList);
							mContactListView.setAdapter(mTouristAdapter);


							mUserId = detail.getUserId();
							mHeadPic = detail.getUserAvatar();
							mUserName = detail.getUserRealname();
							mRoadLineId = detail.getRoadlineId();
							mDetailTopic.setText(detail.getRoadlineTitle());
							mStateString = detail.getOrderStatus();//订单状态字符串
							mOrderState.setText(transOrderStatus(detail.getOrderStatus()));
							mOrderPrice.setText("￥"+detail.getOrderActualPayment());
							mScheduleNum.setText(detail.getOrderTravelNumber());
							mJourneyDate.setText(detail.getOrderTime().substring(0, 10));
							
							mPayQuota.setTextColor(Color.parseColor("#F15353"));
							mPayQuota.setText(detail.getOrderActualPayment()+ "元");
							
							mPayQuota1.setTextColor(Color.parseColor("#F15353"));
							mPayQuota1.setText(detail.getOrderActualPayment() + "元");
							
							mOrderTotile.setText(Float.parseFloat(detail.getOrderPrice())*Float.parseFloat(detail.getOrderTravelNumber())+"元");
							mDiscount.setText("-"+ Float.parseFloat(detail.getOrderCouponMoney()) + "元");
							mOrderPayLayout.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									if(clickTime >0){
										clickTime = 0;
										mOrderDiscountLayout.setVisibility(View.VISIBLE);
										mOrderTotileLayout.setVisibility(View.VISIBLE);
										mPayQuota1.setVisibility(View.VISIBLE);
										mPayQuota.setVisibility(View.INVISIBLE);
										mOrderDetailImg.setVisibility(View.GONE);
									}else{
										clickTime = 1;
										mOrderDiscountLayout.setVisibility(View.GONE);
										mOrderTotileLayout.setVisibility(View.GONE);
										mPayQuota1.setVisibility(View.GONE);
										mPayQuota.setVisibility(View.VISIBLE);
										mOrderDetailImg.setVisibility(View.VISIBLE);
									}
								}
							});
							mOrderNum.setText(detail.getOrderNo());
							mScheduDate.setText(detail.getOrderCreateDate().substring(0, 10));
							mContactPerson.setText(detail.getVisitorName());
							mPhone.setText(detail.getVisitorTel());
							scrollView.setVisibility(View.VISIBLE);
							
							//进入路线详情界面
							mLinearTop.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.putExtra("roadId", mRoadLineId);
									intent.setClass(UUOrderPayDetailActivity.this, FindTestViewPagerActivity.class);
									startActivity(intent);
								}
							});
							
							//显示申请退款按钮
							if("order_agree".equals(mStateString)){
								
								if ("1".equals(mRole)) {
									//旅行完成点击事件
									mTv3.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
											builder.setMessage("确认订单完成，打款给小U。");
											builder.setTitle("确认订单");
											builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
													// 调用旅行完成
													sendOrderCompRequst();
												}
											});

											builder.setNegativeButton("取消",
													new android.content.DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();
														}
													});

											builder.create().show();
										}
									});
									//评价点击事件
									mTv2.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent();
											Bundle bundle = new Bundle();
											bundle.putString("orderId", detail.getOrderNo());
											bundle.putString("name", detail.getUserRealname());
											bundle.putString("headPic", detail.getUserAvatar());
											bundle.putString("isEval", mIsComment);
											intent.putExtras(bundle);
											intent.setClass(UUOrderPayDetailActivity.this, EvaluateActivity.class);
											startActivity(intent);
										}
									});
									//申请退款点击事件
									mApplyRetrun.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent();
											intent.putExtra("orderId", orderId);
											intent.putExtra("price", mPayQuota.getText().toString().substring(1, mPayQuota.getText().toString().length()));
											intent.setClass(UUOrderPayDetailActivity.this, ApplyRefundActivity.class);
											startActivityForResult(intent, 100);
										}
									});
								}
							}else if("order_payment".equals(mStateString)){
								
								if ("1".equals(mRole)) {
									//取消订单点击事件
									mTv2.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											// 弹出对话框
											// 弹出框，确认删除
											String dialogMsg = "您的订单正在取消";
											if (mStateString.equals("order_create")
													|| mStateString.equals("order_agree")) {
												dialogMsg = "您的订单正在取消";
											}
											/*
											 * if (list.get(position).getOrderStatus()
											 * .equals("order_agree")) { dialogMsg =
											 * "导游已确认订单，现在取消订单将扣除总价的15%补偿导游的损失"; }
											 */
											CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
											builder.setMessage(dialogMsg);
											builder.setTitle("取消订单");
											builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
													if (loadingDialog != null) {
														loadingDialog.show();
													} else {
														loadingDialog = new SpotsDialog(UUOrderPayDetailActivity.this);
														loadingDialog.show();
													}
													if (mStateString.equals("order_payment")) {
														sendVisitorRequst("order_not_agree_cancel");
													}
												}
											});

											builder.setNegativeButton("取消",
													new android.content.DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();
														}
													});

											builder.create().show();
										}
									});
								}else{
									
									mTv2.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
											builder.setMessage("您确定要拒绝订单吗");
											builder.setTitle("确认订单");
											builder.setPositiveButton("确定",
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															dialog.dismiss();
															// 发请求
															sendGuideRequst("order_deny");
														}
													});

											builder.setNegativeButton(
													"取消",
													new android.content.DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});

											builder.create().show();
											
										}
									});
									
									mTv3.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
												CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
												builder.setMessage("您确定要接受订单吗");
												builder.setTitle("确认订单");
												builder.setPositiveButton("确定",
														new DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog,
																	int which) {
																dialog.dismiss();
																// 发请求
																sendGuideRequst("order_agree");
															}
														});

												builder.setNegativeButton(
														"取消",
														new android.content.DialogInterface.OnClickListener() {
															public void onClick(DialogInterface dialog,
																	int which) {
																dialog.dismiss();
															}
														});

												builder.create().show();
												
											}
											
									});
								}
							}
							
							if("order_drawback".equals(mStateString)){//退款中
								titleback.setTitle("退款");
								mLinearReturn.setVisibility(View.VISIBLE);
								mLinearBottom.setVisibility(View.GONE);
								mLinearContact.setVisibility(View.GONE);
								mLinearDetail.setVisibility(View.GONE);
								mRelativeTourist.setVisibility(View.GONE);
								mContactListView.setVisibility(View.GONE);
								mReturnAcept.setVisibility(View.GONE);
								mReturnMoney.setText("￥"+detail.getOrderDrawbackMoney());
								mReturnReson.setText(detail.getOrderDrawbackReason());
								if("1".equals(mRole)){
//									if ("0".equals(mIsComment)) {
//										mReturnAcept.setText("评价订单");
//									} else {
//										mReturnAcept.setText("修改评价");
//									}
//									//评价点击事件
//									mReturnAcept.setOnClickListener(new OnClickListener() {
//
//										@Override
//										public void onClick(View v) {
//											Intent intent = new Intent();
//											Bundle bundle = new Bundle();
//											bundle.putString("orderId", detail.getOrderNo());
//											bundle.putString("name", detail.getUserRealname());
//											bundle.putString("headPic", detail.getUserAvatar());
//											bundle.putString("isEval", mIsComment);
//											intent.putExtras(bundle);
//											intent.setClass(UUOrderPayDetailActivity.this, EvaluateActivity.class);
//											startActivity(intent);
//										}
//									});
									mReturnCancle.setText("撤销退款申请");
									mReturnCancle.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											CustomDialog.Builder builder = new CustomDialog.Builder(
													ctx);
											builder.setMessage("您确定要撤消退款吗");
											builder.setTitle("撤消退款");
											builder.setPositiveButton("确定",
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															// 发请求
															sendOrderDrawback("order_drawback_close");
															dialog.dismiss();
														}
													});

											builder.setNegativeButton(
													"取消",
													new android.content.DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});

											builder.create().show();
										}
									});
									mReturnChat.setText("联系向导");
									mReturnChat.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											Intent intent = new Intent();
											intent.putExtra("userId", mUserId);
											intent.putExtra("avatar", mHeadPic);
											intent.putExtra("userName",mUserName);
											intent.setClass(UUOrderPayDetailActivity.this, ChatActivity.class);
											startActivity(intent);
										}
									});
									
								}else{
									mReturnAcept.setVisibility(View.INVISIBLE);
									mReturnCancle.setText("接受退款申请");
									mReturnCancle.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											// 发请求
											CustomDialog.Builder builder = new CustomDialog.Builder(
													ctx);
											builder.setMessage("您确定要接受退款吗");
											builder.setTitle("接受退款");
											builder.setPositiveButton("确定",
													new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															dialog.dismiss();
															// 发请求
															sendOrderDrawback("order_drawback_success");
														}
													});

											builder.setNegativeButton(
													"取消",
													new android.content.DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});

											builder.create().show();
										}
									});
									mReturnChat.setText("联系u客");
									mReturnChat.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											Intent intent = new Intent();
											intent.putExtra("userId", mUserId);
											intent.putExtra("avatar", mHeadPic);
											intent.putExtra("userName",mUserName);
											intent.setClass(UUOrderPayDetailActivity.this, ChatActivity.class);
											startActivity(intent);
										}
									});
								}
							}
							if("order_drawback_close".equals(mStateString)){//退款关闭
								titleback.setTitle("退款详情");
								mLinearReturn.setVisibility(View.VISIBLE);
								mReturnMoney.setText("￥"+detail.getOrderDrawbackMoney());
								mReturnReson.setText(detail.getOrderDrawbackReason());
								mLinearTop.setVisibility(View.GONE);
								mLinearBottom.setVisibility(View.GONE);
								mLinearContact.setVisibility(View.GONE);
								mLinearDetail.setVisibility(View.GONE);
								mRelativeTourist.setVisibility(View.GONE);
								mContactListView.setVisibility(View.GONE);
								mLinearReturnTv.setVisibility(View.GONE);
								mLinearDrawback.setVisibility(View.VISIBLE);
								mReturnImg.setImageResource(R.drawable.return_close);
								mReturnTopic.setText("退款关闭");
								mReturnTv1.setText("关闭原因：");
								mReturnTv2.setText("由于您取消退款，退款关闭");
								mReturnTv11.setText("关闭时间：");
								mReturnTv22.setText(detail.getOrderDrawbackDate().substring(0, 16));
							}
							if("order_drawback_success".equals(mStateString)){//退款完成
								titleback.setTitle("退款详情");
								mLinearReturn.setVisibility(View.VISIBLE);
								mReturnMoney.setText("￥"+detail.getOrderDrawbackMoney());
								mReturnReson.setText(detail.getOrderDrawbackReason());
								mLinearTop.setVisibility(View.GONE);
								mLinearBottom.setVisibility(View.GONE);
								mLinearContact.setVisibility(View.GONE);
								mLinearDetail.setVisibility(View.GONE);
								mRelativeTourist.setVisibility(View.GONE);
								mContactListView.setVisibility(View.GONE);
								mLinearReturnTv.setVisibility(View.GONE);
								mLinearDrawback.setVisibility(View.VISIBLE);
								mReturnImg.setImageResource(R.drawable.return_sucess);
								mReturnTopic.setText("退款成功");
								mReturnTv1.setText("退款金额：");
								mReturnTv2.setText("￥"+detail.getOrderDrawbackMoney());
								mReturnTv11.setText("退款时间：");
								mReturnTv22.setText(detail.getOrderDrawbackDate().substring(0, 16));
							}
							new Handler().postDelayed(new Runnable(){   
					            public void run() {  
					                   //显示dialog
					            	loadingDialog.dismiss();			            	
					            }  
					        }, 500);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getContentInit();
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(
									UUOrderPayDetailActivity.this)
									.setTitle("提示")
									.setMessage("网络拥堵,请稍后重试！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													finish();
													dialog.dismiss();
												}
											}).show();
							
						}
					}}

					@Override
					public void onFinish() {
						

					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			switch (requestCode) {
			case 100:
				finish();
				break;
			
			default:
				break;
			}
		}
	}

	/**
	 * 完成旅行请求接口
	 */
	public void sendOrderCompRequst() {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		APPRestClient
				.post(ctx, ServiceCode.ORDER_COMPLETE, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								ctx) {
							@Override
							public void onSuccess(OrderEntity result) {
								
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendOrderCompRequst();
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(ctx)
											.setTitle("提示")
											.setMessage("网络拥堵,请稍后重试！")
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													}).show();
								}

							}
							}
							@Override
							public void onFinish() {
							}
						});
	}
	
	
	public void sendGuideRequst(final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("OrderStatus", status);
		APPRestClient
				.post(ctx, ServiceCode.ORDER_INVITATION, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								ctx) {
							@Override
							public void onSuccess(OrderEntity result) {
								//接受订单
								finish();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendGuideRequst(status);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(ctx)
											.setTitle("提示")
											.setMessage("网络拥堵,请稍后重试！")
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													}).show();
								}

							}
							}

							@Override
							public void onFinish() {
							}
						});
	}
	
	public void sendOrderDrawback(final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("orderStatus", status); // 路线id
		APPRestClient
				.post(ctx, ServiceCode.ORDER_DRAWBACK, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								ctx) {
							@Override
							public void onSuccess(OrderEntity result) {
								//发送请求
								finish();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendOrderDrawback(status);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(ctx)
											.setTitle("提示")
											.setMessage("网络拥堵,请稍后重试！")
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													}).show();
								}

							}
							}
							@Override
							public void onFinish() {
							}
						});
	}
	
	
	private String transOrderStatus(String status) {
		String rsultStatus = "";
		if (status.equals("order_create"))
			rsultStatus = "未付款";
		else if (status.equals("order_no_pay_cancel"))
			rsultStatus = "未付款取消";
		else if (status.equals("order_invalid"))
			rsultStatus = "已失效";
		else if (status.equals("order_payment"))
			rsultStatus = "待确认";
		else if (status.equals("order_not_agree_cancel"))
			rsultStatus = "取消";
		else if (status.equals("order_deny"))
			rsultStatus = "已拒绝";
		else if (status.equals("order_agree"))
			rsultStatus = "已接受";
		else if (status.equals("order_agree_cancel"))
			rsultStatus = "导游同意后取消";
		else if (status.equals("order_success"))
			rsultStatus = "订单完成";
		else if (status.equals("order_drawback"))
			rsultStatus = "退款中";
		else if (status.equals("order_drawback_success"))
			rsultStatus = "退款完成";
		else if (status.equals("order_drawback_failure"))
			rsultStatus = "退款已拒绝";
		else if(status.equals("order_drawback_close")){
			rsultStatus = "退款关闭";
		}
		return rsultStatus;
	}

}
