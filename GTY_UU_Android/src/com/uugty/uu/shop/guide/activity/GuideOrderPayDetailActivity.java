package com.uugty.uu.shop.guide.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.OrderDetailEntity;
import com.uugty.uu.entity.OrderDetailEntity.OrderDetail;
import com.uugty.uu.entity.OrderEntity;

public class GuideOrderPayDetailActivity extends BaseActivity implements
		OnClickListener {

	private SimpleDraweeView orderDetailImage;//订单封面图片
	private TextView mDetailTopic;//订单标题
	private TextView mOrderState;//订单状态
	private TextView mOrderPrice;//订单价格
	private TextView mApplyRetrun;//申请退款
	private TextView mScheduleNum;//预定数量
	private TextView mJourneyDate;//出行时间
	private TextView mPayQuota;//支付金额
	private TextView mTourist;//游客
	private TextView mOrderMark;//订单留言
	private TextView mTouristMark;//游客留言
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
	
	protected int getContentLayout() {
		return R.layout.activity_guide_order_paydetails;// 订单详情
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
		mTourist = (TextView) findViewById(R.id.uu_reserve_number);//游客
		mOrderMark = (TextView) findViewById(R.id.order_mark);
		mTouristMark = (TextView) findViewById(R.id.visiter_mark);
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
		
		
		titleback = (TopBackView) findViewById(R.id.order_pay_top_title_detail_back);
		titleback.setTitle("订单详情");
		scrollView = (ScrollView) findViewById(R.id.activity_orderpays_scrollview);
		scrollView.setVisibility(View.INVISIBLE);
		
	}

	@Override
	protected void initAction() {
		getContentInit();//获取数据
		
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
		APPRestClient.postGuide(this, ServiceCode.ORDER_CANCLE, params,
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
								new AlertDialog.Builder(GuideOrderPayDetailActivity.this).setTitle("提示").setMessage("服务器连接失败！")
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

		APPRestClient.postGuide(this, ServiceCode.BATCH_ORDERDETAIL_MESSAGE, params,
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
							if(detail.getOrderMark() != null && !"".equals(detail.getOrderMark())){
								mOrderMark.setText(detail.getOrderMark());
							}
							if(detail.getVisitorContent() != null && !"".equals(detail.getVisitorContent())){
								mTouristMark.setText(detail.getVisitorContent());
							}
							mUserId = detail.getUserId();
							mHeadPic = detail.getUserAvatar();
							mUserName = detail.getUserRealname();
							mRoadLineId = detail.getRoadlineId();
							mDetailTopic.setText(detail.getRoadlineTitle());
							mStateString = detail.getOrderStatus();//订单状态字符串
							mOrderState.setText(transOrderStatus(detail.getOrderStatus()));
							mOrderPrice.setText("￥"+detail.getOrderPrice());
							mScheduleNum.setText(detail.getOrderTravelNumber());
							mJourneyDate.setText(detail.getOrderTime().substring(0, 10));
							mPayQuota.setTextColor(Color.parseColor("#F15353"));
							mPayQuota.setText("￥"+Float.parseFloat(detail.getOrderPrice())*Float.parseFloat(detail.getOrderTravelNumber()));
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
									intent.setClass(GuideOrderPayDetailActivity.this, RoadDetailActivity.class);
									startActivity(intent);
								}
							});
							new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
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
									GuideOrderPayDetailActivity.this)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
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
					}
					}

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
				.postGuide(ctx, ServiceCode.ORDER_COMPLETE, params,
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
											.setMessage("服务器连接失败！")
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
				.postGuide(ctx, ServiceCode.ORDER_INVITATION, params,
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
											.setMessage("服务器连接失败！")
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
				.postGuide(ctx, ServiceCode.ORDER_DRAWBACK, params,
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
											.setMessage("服务器连接失败！")
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
