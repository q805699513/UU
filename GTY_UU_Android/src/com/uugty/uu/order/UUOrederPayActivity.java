package com.uugty.uu.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.discount.c.DiscountSelectActivity;
import com.uugty.uu.discount.c.MyDiscountActivity;
import com.uugty.uu.discount.m.DiscountListItem;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;
import com.uugty.uu.entity.OrderDetailEntity;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.OrderDetailEntity.OrderDetail;
import com.uugty.uu.main.OrderDateActivty;
import com.uugty.uu.person.TouristListActivity;

public class UUOrederPayActivity extends BaseActivity implements
		OnClickListener {

	private SimpleDraweeView headImageView;//路线封面
	private TextView mOrderTopic;//订单主题
	private TextView mOrderPrie;//订单价格
	private TextView reserve_num;//预定数量
	private ImageView reserve_minus;//数量减少
	private ImageView reserve_add;//数量增加
	private TextView startTimeTextView;//出行时间
	private LinearLayout mDiscountRelative;//代金券布局
	private String mDiscountMoney;//代金券金额
	private TextView mDiscountRec;//代金券数额
	private TextView mDiscountTv;//领取代金券
	private TextView uu_tourist_names;//出行人
	private EmojiEdite mTrvalName;//订单联系人姓名
	private EmojiEdite mTrvalPhone;//联系人电话
	private EmojiEdite mTrvalNote;//联系人备注
	private EmojiEdite msgTextView;//留言
	
	private String mContactName;//出行人名
	private String mVisitorName;// 联系人名
	private String mVisitorTel;// 联系人手机号
	private String mVisitorContent;// 联系人留言 	
	private String routeBackgroundImage;//标题图片地址
	
	private String mDiscountId;//代金券id
	private String mUserId;//代金券用户关联id
	private String realPrice;//未使用代金券原始价格
	private String isNotRec;//暂不领取代金券
	private String route_id;//路线ID
	private List<DiscountEntity> discountList ;//代金券列表
	//修改后
	private String mPhone;//联系电话号码
	private String mName;//联系人姓名
	
	private String orderId;//订单id
	private RelativeLayout activity_paypricea_add_person;
	
	private Button commitBtn;
	private TopBackView titleBack;
	private String roadLineTitle = "";
	public final static int REQUEST_CHOOSE_DATE = 100;
	private String orderNo = "";
	private SpotsDialog loadingDialog;
	private ScrollView scrollView;
	String allPrice;
	private String contactId="",toursit_nums="",routePrice,reserve_nums="";
	public final static int REQUEST_NUM = 103;
	public final static int REQUEST_SEL = 101;
	
	
	
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_orderpays;// 未支付的页面
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			orderId = getIntent().getStringExtra("orderId");
		}
		headImageView = (SimpleDraweeView) findViewById(R.id.order_pay_user_img);
		mOrderTopic = (TextView)findViewById(R.id.order_pay__confirm_topic);
		mOrderPrie = (TextView) findViewById(R.id.order_pay_confirm_price);
		reserve_minus=(ImageView) findViewById(R.id.reserve_minus);
		reserve_num=(TextView) findViewById(R.id.reserve_num);
		reserve_add=(ImageView) findViewById(R.id.reserve_add);	
		startTimeTextView = (TextView) findViewById(R.id.order_pay_start_time);
		uu_tourist_names = (TextView) findViewById(R.id.uu_tourist_number);
		mTrvalName = (EmojiEdite)findViewById(R.id.pay_order_name);
		mTrvalPhone = (EmojiEdite)findViewById(R.id.pay_order_tel);
		mTrvalNote = (EmojiEdite)findViewById(R.id.pay_order_note);
		mTrvalPhone.setInputType(InputType.TYPE_CLASS_PHONE);//限制只能输入电话号码
		
		mDiscountRelative = (LinearLayout) findViewById(R.id.activity_payOrder_add_discount);
		mDiscountRec = (TextView) findViewById(R.id.uu_tv_discountRec);//代金券金额
		mDiscountTv = (TextView) findViewById(R.id.uu_order_discountRec);//立即领取
		mDiscountRec.setText("暂不使用代金券");
		
		msgTextView = (EmojiEdite) findViewById(R.id.pay_order_msg);
		commitBtn = (Button) findViewById(R.id.order_pay_commit_btn);				
			
		activity_paypricea_add_person = (RelativeLayout) findViewById(R.id.activity_paypricea_add_person);
		titleBack = (TopBackView) findViewById(R.id.order_pay_top_title_rel);
		titleBack.setTitle("订单修改");		
		scrollView = (ScrollView) findViewById(R.id.activity_orderpays_scrollview);
		scrollView.setVisibility(View.INVISIBLE);
		
		
		
		
		commitBtn.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void initAction() {
		reserve_minus.setOnClickListener(this);
		reserve_add.setOnClickListener(this);
		// 调用接口，填充数据
		getContentInit();
		activity_paypricea_add_person.setOnClickListener(this);
		startTimeTextView.setOnClickListener(this);
		
		mDiscountTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UUOrederPayActivity.this, MyDiscountActivity.class);
				startActivity(intent);
			}
		});


		// backPayText.setOnClickListener(this);
		// uuorder_service.setOnClickListener(this);
		commitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				commitBtn.setEnabled(false);
				if(!reserve_nums.equals("")){
				if (null != startTimeTextView.getText().toString().trim()
						&& !startTimeTextView.getText().toString().trim()
								.equals("")) {

					if (isPhoneNumberValid(mPhone)) {
						RequestParams params = new RequestParams();
						params.add("orderId", orderId); // 路线id
						params.add("orderTime", startTimeTextView.getText().toString()); // 订单的时间
						params.add("orderMark", msgTextView.getText().toString()); // 订单留言
						params.add("orderPrice", "" + allPrice); // 路线价钱
						params.add("contactId", contactId); // 联系人主键id
						params.add("orderTravelNumber", "" + reserve_nums); // 预定数量
						params.add("visitorName", mName);
						params.add("visitorTel", mPhone);
						params.add("visitorContent", mVisitorContent);
						params.add("couponId", mDiscountId);
						params.add("couponUserId", mUserId);
						APPRestClient.post(UUOrederPayActivity.this, ServiceCode.BATCH_ORDER_MODIFY, params,
								new APPResponseHandler<OrderEntity>(OrderEntity.class, UUOrederPayActivity.this) {
									@Override
									public void onSuccess(OrderEntity result) {
										Intent intent = new Intent();
										intent.putExtra("price", allPrice);
										intent.putExtra("orderId", orderId);
										intent.putExtra("orderName", roadLineTitle);
										intent.putExtra("orderNo", orderNo);
										intent.putExtra("orderRoadlineId", route_id);// 路线id
										intent.putExtra("orderImage", routeBackgroundImage);//路线图片
										intent.putExtra("orderPrice", "" + allPrice);// 路线价钱
										intent.putExtra("orderName", roadLineTitle); //路线标题
										intent.putExtra("contactName", mContactName);//出行人名
										intent.putExtra("orderTime", startTimeTextView.getText().toString());// 订单的时间
										intent.putExtra("orderMark", msgTextView.getText().toString());// 订单留言
										intent.putExtra("contactId", contactId);// 联系人主键id
										intent.putExtra("orderTravelNumber", "" + reserve_nums);//预定数量
										intent.putExtra("visitorName", mName);// 联系人名
										intent.putExtra("visitorTel", mPhone);// 联系人手机号
										intent.putExtra("visitorContent", mTrvalNote.getText().toString());// 联系人留言 	 
										intent.putExtra("pageFlag", "UUPayActivity");
										intent.putExtra("id", mDiscountId);
										intent.putExtra("userId", mUserId);
										intent.setClass(UUOrederPayActivity.this, UUPaypriceActivity.class);
										startActivity(intent);
									}

									@Override
									public void onFailure(int errorCode, String errorMsg) {
										CustomToast.makeText(UUOrederPayActivity.this, errorMsg, 300).show();
										commitBtn.setEnabled(true);
										if (errorCode == -999) {
											new AlertDialog.Builder(UUOrederPayActivity.this).setTitle("提示")
													.setMessage("服务器连接失败！")
													.setPositiveButton("确定", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();
														}
													}).show();
										}

									}

									@Override
									public void onFinish() {
									}
								});
					}else{
						// 弹出对话框
						commitBtn.setEnabled(true);
						CustomDialog.Builder builder = new CustomDialog.Builder(
								UUOrederPayActivity.this);
						builder.setMessage("请填写正确的电话号码");
						builder.setTitle("提示");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										
									}
								});
						builder.create().show();
					}
				} else {
					// 弹出对话框
					commitBtn.setEnabled(true);
					CustomDialog.Builder builder = new CustomDialog.Builder(
							UUOrederPayActivity.this);
					builder.setMessage("请选择旅行出发日期");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				}
				}else{
					// 弹出对话框
					commitBtn.setEnabled(true);
					CustomDialog.Builder builder = new CustomDialog.Builder(
							UUOrederPayActivity.this);
					builder.setMessage("请填写预定数量");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									
								}
							});
					builder.create().show();
				}
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

		APPRestClient.post(this, ServiceCode.GET_ORDERDETAIL_MESSAGE, params,
				new APPResponseHandler<OrderDetailEntity>(
						OrderDetailEntity.class, this) {
					@Override
					public void onSuccess(OrderDetailEntity result) {
						if (result != null) {
							OrderDetail detail = result.getOBJECT();
							routeBackgroundImage = APPRestClient.SERVER_IP
									+ "images/roadlineDescribe/"
									+ detail.getRoadlineBackground();
							ImageLoader.getInstance().displayImage(routeBackgroundImage,headImageView);
							contactId=detail.getContactId();
							toursit_nums=detail.getContactNum();
							if(detail.getContactNum().equals("0")){
								uu_tourist_names.setText("还未添加联系人");
							}else{
								mContactName = detail.getContactName().replace(",", " ");
								uu_tourist_names.setText(mContactName);
							}
							reserve_nums=detail.getOrderTravelNumber();
							reserve_num.setText(reserve_nums);
							
							routePrice=detail.getOrderPrice();
							
							realPrice = routePrice + detail.getOrderCouponMoney();//路线原始价格，重新选取代金券时使用
							
							
							allPrice=""+Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums);
							
							roadLineTitle = detail.getRoadlineTitle();
							mOrderTopic.setText(roadLineTitle);//设置订单标题
							startTimeTextView.setText(detail.getOrderTime()
									.substring(0, 10));
							startTimeTextView.setTextColor(Color
									.parseColor("#000000"));
							msgTextView.setText(detail.getOrderMark());
							msgTextView.setTextColor(Color
									.parseColor("#000000"));
							
							orderNo = detail.getOrderNo();
							mVisitorName = detail.getVisitorName();
							mTrvalName.setText(mVisitorName);
							mVisitorTel = detail.getVisitorTel();
							mTrvalPhone.setText(mVisitorTel);
							mVisitorContent = detail.getVisitorContent();
							mTrvalNote.setText(mVisitorContent);
							mPhone = mTrvalPhone.getText().toString();
							mName = mTrvalName.getText().toString();
							
							if("".equals(mName)){
								mName = MyApplication.getInstance().getUserInfo().getOBJECT().getUserName();
							}
							if("".equals(mPhone)){
								mPhone = MyApplication.getInstance().getUserInfo().getOBJECT().getUserTel();
							}
							if(!"0.0".equals(detail.getOrderCouponMoney())){
								mDiscountRelative.setVisibility(View.VISIBLE);
								mDiscountId = detail.getCouponId();
								mUserId = detail.getCouponUserId();
								if(mDiscountMoney != null){
									mDiscountRec.setText(mDiscountMoney + "元代金券");
									allPrice = ""+(Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums) - Float.parseFloat(mDiscountMoney));
								}else{
									if("0.0".equals(detail.getOrderCouponMoney()) || "0".equals(detail.getOrderCouponMoney())){
										mDiscountRec.setText("暂不使用代金券");
									}else{
										mDiscountRec.setText(detail.getOrderCouponMoney() + "元代金券");
									}
									
									if(isNotRec == null){//代金券界面选择暂不使用
										allPrice = ""+(Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums) - Float.parseFloat(detail.getOrderCouponMoney()));
									}
								}
							}else{
								mDiscountRelative.setVisibility(View.GONE);
							}
							commitBtn.setText(allPrice+"支付");
							mOrderPrie.setText("￥"+allPrice);//设置总价
							
							scrollView.setVisibility(View.VISIBLE);
							commitBtn.setVisibility(View.VISIBLE);
							
							route_id = detail.getRoadlineId();
							getDiscountInit(route_id);
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
							new AlertDialog.Builder(UUOrederPayActivity.this)
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
					}}

					@Override
					public void onFinish() {

					}
				});

	}
	
	private void getDiscountInit(String rodeId) {

		RequestParams params = new RequestParams();
		params.add("roadlineId", rodeId); 

		APPRestClient.post(this, ServiceCode.DISCOUNT_FILTER, params,
				new APPResponseHandler<DiscountListItem>(
						DiscountListItem.class, this) {
					@Override
					public void onSuccess(final DiscountListItem result) {
						
						if (result.getLIST().size() != 0) {
							discountList = result.getLIST();
							mDiscountRelative.setVisibility(View.VISIBLE);
							for (int i = 0; i < result.getLIST().size(); i++) {
								if ("1".equals(result.getLIST().get(i).getCouponUserStatus())) {
									mDiscountTv.setVisibility(View.VISIBLE);
								}
							}
						}
						mDiscountRelative.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.putExtra("list", (Serializable) discountList);
								intent.putExtra("from", "modify");
								intent.setClass(UUOrederPayActivity.this, DiscountSelectActivity.class);
								startActivityForResult(intent,REQUEST_SEL);
							}
						});

						new Handler().postDelayed(new Runnable(){   
				            public void run() {  
				                   //延时消失dialog
				            	loadingDialog.dismiss();			            	
				            }  
				        }, 500);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getContentInit();
						} else {
						loadingDialog.dismiss();
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(UUOrederPayActivity.this)
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
					}}

					@Override
					public void onFinish() {

					}
				});

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		commitBtn.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		Intent intent =new Intent();
		switch (v.getId()) {
		case R.id.activity_paypricea_add_person:
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(ctx, TouristListActivity.class);
			intent.putExtra("id", contactId);
			startActivityForResult(intent, REQUEST_NUM);
			break;
		case R.id.reserve_minus:
			if (Integer.parseInt(reserve_nums) > 1) {
				reserve_nums = "" + (Integer.parseInt(reserve_nums) - 1);
				reserve_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
				reserve_num.setText(reserve_nums);
				if (Integer.parseInt(reserve_nums) <= 1) {
					reserve_minus
							.setImageResource(R.drawable.chat_costom_minus);
				}
			}
			allPrice=""+Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums);
			mOrderPrie.setText("￥"+allPrice);
			commitBtn.setText("￥" + allPrice + " 支付");
			break;
		case R.id.reserve_add:
			reserve_nums = "" + (Integer.parseInt(reserve_nums) + 1);
			reserve_num.setText(reserve_nums);
			if (Integer.parseInt(reserve_nums) > 1) {
				reserve_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
			}
			
			allPrice=""+Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums);
			mOrderPrie.setText("￥"+allPrice);
			commitBtn.setText("￥" + allPrice + " 支付");
			break;
		default:
			break;
		}
	}

	@Override
	public void onNoDoubleClick(View v) {
		Intent toIntent = new Intent();
		switch (v.getId()) {
		case R.id.order_pay_start_time:
			toIntent.setClass(this, OrderDateActivty.class);
			startActivityForResult(toIntent, REQUEST_CHOOSE_DATE);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 判断电话号码是否有效
	 * 
	 * @param phoneNumber
	 * @return true 有效 / false 无效
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {

		boolean isValid = false;

		if (null != phoneNumber) {
			String expression = "((^(13|15|18|17)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
			CharSequence inputStr = phoneNumber;
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(inputStr);
			if (matcher.matches()) {
				isValid = true;
			} 
		}
		return isValid;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CHOOSE_DATE:
				String chooseDate = data.getStringExtra("choose_date");
				startTimeTextView.setText(chooseDate);
				break;
			case REQUEST_NUM:
				toursit_nums=data.getStringExtra("num");
				contactId=data.getStringExtra("allId");
				mContactName = data.getStringExtra("name");
				uu_tourist_names.setText(mContactName);
				break;
			case REQUEST_SEL:
				mDiscountId = "";
				mUserId = "";
				isNotRec = data.getStringExtra("notRec");
				mDiscountRelative.setVisibility(View.VISIBLE);
				if ("0".equals(isNotRec)) {
					mDiscountId = data.getStringExtra("id");
					mUserId = data.getStringExtra("userId");
					mDiscountMoney = data.getStringExtra("discountMoney");
					for (int i = 0; i < discountList.size(); i++) {
						if (mDiscountId.equals(discountList.get(i).getCouponId())) {
							mDiscountMoney = discountList.get(i).getCouponMoney();
							allPrice = ""+(Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums) - Float.parseFloat(mDiscountMoney));
							commitBtn.setText(allPrice+"支付");
							mOrderPrie.setText("￥"+allPrice);//设置总价
							if (discountList.get(i).getCouponUserStatus().equals("2")) {
								mDiscountRec.setText(mDiscountMoney + "元代金券");
								mDiscountRec.setVisibility(View.VISIBLE);
							}
							if ("1".equals(discountList.get(i).getCouponUserStatus())) {
								mDiscountTv.setVisibility(View.VISIBLE);
							}
						}
					} 
				}else{
					mDiscountRec.setText("暂不使用元代金券");
					mDiscountRec.setVisibility(View.VISIBLE);
					allPrice = ""+(Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums));
					commitBtn.setText(allPrice+"支付");
					mOrderPrie.setText("￥"+allPrice);//设置总价
				}
				break;
			default:
				break;
			}
		}
	}

}
