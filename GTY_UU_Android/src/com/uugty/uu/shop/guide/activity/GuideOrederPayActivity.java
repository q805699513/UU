package com.uugty.uu.shop.guide.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.discount.c.DiscountSelectActivity;
import com.uugty.uu.discount.c.MyDiscountActivity;
import com.uugty.uu.discount.m.DiscountListItem;
import com.uugty.uu.entity.OrderDetailEntity;
import com.uugty.uu.entity.OrderDetailEntity.OrderDetail;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.main.OrderDateActivty;
import com.uugty.uu.person.TouristListActivity;
import com.uugty.uu.shop.guide.Model.CateGoryEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuideOrederPayActivity extends BaseActivity implements
		OnClickListener {

	private SimpleDraweeView headImageView;//路线封面
	private TextView mOrderTopic;//订单主题
	private TextView mOrderPrie;//订单价格
	private TextView reserve_num;//预定数量
	private ImageView reserve_minus;//数量减少
	private ImageView reserve_add;//数量增加
	private TextView startTimeTextView;//出行时间
	private RelativeLayout mDiscountRelative;//代金券布局
	private String mDiscountMoney;//代金券金额
	private TextView mDiscountRec;//代金券数额
	private TextView mDiscountTv;//领取代金券
	private TextView uu_tourist_names;//出行人
	private EmojiEdite mTrvalName;//订单联系人姓名
	private EmojiEdite mTrvalPhone;//联系人电话
	private EmojiEdite mTrvalNote;//联系人备注
	private EmojiEdite msgTextView;//留言
	
	private String mContactName = "";//出行人名
	private String mVisitorName;// 联系人名
	private String mVisitorTel;// 联系人手机号
	private String mVisitorContent;// 联系人留言 	
	private String routeBackgroundImage;//标题图片地址
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

	//库存
	private RelativeLayout mCateGoryLayout;
	private TextView mCateGoryTextView;
	private String roadLineId = "";
	
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_guide_orderpays;// 未支付的页面
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
		
		mDiscountRelative = (RelativeLayout) findViewById(R.id.activity_payOrder_add_discount);
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

		//库存
		mCateGoryLayout = (RelativeLayout) findViewById(R.id.activity_pay_category);
		mCateGoryTextView = (TextView) findViewById(R.id.order_kucun);
		
		
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
				intent.setClass(GuideOrederPayActivity.this, MyDiscountActivity.class);
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
						if(!"库存不足".equals(mCateGoryTextView.getText().toString())) {
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
							params.add("couponId", "");
							params.add("couponUserId", "");
							params.add("orderInsuranceType","");
							params.add("insuranceContactId","");
							APPRestClient.post(GuideOrederPayActivity.this, ServiceCode.BATCH_ORDER_MODIFY, params,
									new APPResponseHandler<OrderEntity>(OrderEntity.class, GuideOrederPayActivity.this) {
										@Override
										public void onSuccess(OrderEntity result) {
											ActivityCollector.removeSpecifiedActivity("com.uugty.uu.order.UUOrderActivity");
											Intent intent = new Intent();
											intent.putExtra("price", allPrice);
											intent.putExtra("orderId", orderId);
											intent.putExtra("orderName", "折扣商品");
											intent.putExtra("orderNo", orderNo);
											intent.putExtra("orderImage", routeBackgroundImage);//路线图片
											intent.putExtra("orderPrice", "" + allPrice);// 路线价钱
											intent.putExtra("contactName", mContactName);//出行人名
											intent.putExtra("orderTime", startTimeTextView.getText().toString());// 订单的时间
											intent.putExtra("orderMark", msgTextView.getText().toString());// 订单留言
											intent.putExtra("contactId", contactId);// 联系人主键id
											intent.putExtra("orderTravelNumber", "" + reserve_nums);//预定数量
											intent.putExtra("visitorName", mName);// 联系人名
											intent.putExtra("visitorTel", mPhone);// 联系人手机号
											intent.putExtra("visitorContent", mTrvalNote.getText().toString());// 联系人留言
											intent.putExtra("pageFlag", "GuidePayActivity");
											intent.setClass(GuideOrederPayActivity.this, GuidePaypriceActivity.class);
											startActivity(intent);
										}

										@Override
										public void onFailure(int errorCode, String errorMsg) {
											CustomToast.makeText(GuideOrederPayActivity.this, errorMsg, Toast.LENGTH_LONG).show();
											commitBtn.setEnabled(true);
											if (errorCode == -999) {
												new AlertDialog.Builder(GuideOrederPayActivity.this).setTitle("提示")
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
							commitBtn.setEnabled(true);
							CustomToast.makeText(GuideOrederPayActivity.this, "库存不足", Toast.LENGTH_LONG).show();
						}
					}else{
						// 弹出对话框
						commitBtn.setEnabled(true);
						CustomDialog.Builder builder = new CustomDialog.Builder(
								GuideOrederPayActivity.this);
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
							GuideOrederPayActivity.this);
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
							GuideOrederPayActivity.this);
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
	
	private void getDiscountInit(final String id) {
		RequestParams params = new RequestParams();
		params.add("roadlineId", id); // 订单的状态

		APPRestClient.post(this, ServiceCode.DISCOUNT_FILTER, params,
				new APPResponseHandler<DiscountListItem>(
						DiscountListItem.class, this) {
					@Override
					public void onSuccess(DiscountListItem result) {
						if (result.getLIST().size() != 0) {
//							mDiscountRelative.setVisibility(View.VISIBLE);
							for(int i=0;i<result.getLIST().size();i++){
								if("1".equals(result.getLIST().get(i).getCouponUserStatus())){
									mDiscountTv.setVisibility(View.VISIBLE);
								}
							}
							mDiscountMoney = result.getLIST().get(0).getCouponMoney();
							mDiscountRec.setText(mDiscountMoney+"元代金券");
							mDiscountRelative.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.putExtra("roadlineId", id);
									intent.setClass(GuideOrederPayActivity.this, DiscountSelectActivity.class);
									startActivity(intent);
								}
							});
						}else{
							mDiscountRelative.setVisibility(View.GONE);
						}
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
							new AlertDialog.Builder(GuideOrederPayActivity.this)
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
							OrderDetail detail = result.getOBJECT();
							routeBackgroundImage = APPRestClient.SERVER_IP
									+ "images/roadlineDescribe/"
									+ detail.getRoadlineBackground();
							ImageLoader.getInstance().displayImage(routeBackgroundImage,headImageView);
							contactId=detail.getContactId();
							toursit_nums=detail.getContactNum();
							if(result.getLIST().size()>0) {
								for (int i = 0; i < result.getLIST().size(); i++) {
									if (i == result.getLIST().size() - 1) {
										mContactName+=result.getLIST().get(i).getContactName();
									}else{
										mContactName+=result.getLIST().get(i).getContactName()+" ";
									}
								}
								uu_tourist_names.setText(mContactName);
							}
							if(detail.getContactNum().equals("0")){
								uu_tourist_names.setText("还未添加联系人");
							}
//							else{
//								mContactName = detail.getContactName().replace(",", " ");
//								uu_tourist_names.setText(mContactName);
//							}
							reserve_nums=detail.getOrderTravelNumber();
							reserve_num.setText(reserve_nums);
							
							routePrice=detail.getOrderPrice();
							roadLineId = detail.getRoadlineId();
							allPrice=""+Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums);
							commitBtn.setText(allPrice+"支付");
							mOrderPrie.setText("￥"+allPrice);//设置总价
							roadLineTitle = detail.getRoadlineTitle();
//							if("".equals(roadLineTitle) || "2".equals(detail.getOrderType())) {
//								mOrderTopic.setText("折扣商品");//设置订单标题
//							}
							mOrderTopic.setText(roadLineTitle);
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
							scrollView.setVisibility(View.VISIBLE);
							commitBtn.setVisibility(View.VISIBLE);
							//代金券接口
							//getDiscountInit(detail.getRoadlineId());
							new Handler().postDelayed(new Runnable(){   
					            public void run() {  
					                   //延时消失dialog
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
							new AlertDialog.Builder(GuideOrederPayActivity.this)
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

	//获取库存
	private void getCateGory(String time) {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.add("orderRoadlineId", roadLineId); // 路线ID
		params.add("orderTime",time);//订单时间

		APPRestClient.postGuide(this, ServiceCode.CATEGORY_COUNT, params,
				new APPResponseHandler<CateGoryEntity>(
						CateGoryEntity.class, this) {
					@Override
					public void onSuccess(final CateGoryEntity result) {

						if( null != result.getOBJECT() ){
							mCateGoryLayout.setVisibility(View.VISIBLE);

							if("0".equals(result.getOBJECT().getCount())){
								mCateGoryTextView.setText("库存不足");
							}else{
								mCateGoryTextView.setText(result.getOBJECT().getCount());
							}
						}

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
								new AlertDialog.Builder(GuideOrederPayActivity.this)
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CHOOSE_DATE:
				String chooseDate = data.getStringExtra("choose_date");
				getCateGory(chooseDate);
				startTimeTextView.setText(chooseDate);
				break;
			case REQUEST_NUM:
				toursit_nums=data.getStringExtra("num");
				contactId=data.getStringExtra("allId");
				mContactName = data.getStringExtra("name");
				uu_tourist_names.setText(mContactName);
				break;
			default:
				break;
			}
		}
	}

}
