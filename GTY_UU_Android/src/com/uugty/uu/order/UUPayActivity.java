package com.uugty.uu.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.uugty.uu.common.myview.ListViewForScrollView;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.discount.c.DiscountSelectActivity;
import com.uugty.uu.discount.c.MyDiscountActivity;
import com.uugty.uu.discount.m.DiscountListItem;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;
import com.uugty.uu.entity.TouristEntity;
import com.uugty.uu.order.insure.InsureActivity;
import com.uugty.uu.order.insure.OrderTouristAdapter;
import com.uugty.uu.person.TouristListActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class UUPayActivity extends BaseActivity implements OnClickListener {
	private TopBackView topBack;
	private TextView dateTextView, activity_payprice_guide_title,uu_tourist_number,uu_pay_price_num;
	private SimpleDraweeView headImageView;
	private TextView mOrderTopic;//订单主题
	private TextView mOrderPrie;//订单价格
	private LinearLayout mDiscountRelative;//代金券布局
	private TextView mDiscountRec;//代金券金额
	private TextView mDiscountTv;//代金券领取
	private Button mPayPriceButton;//显示实付价格按钮
	private String mContactName;//添加的联系人姓名
	private EmojiEdite mTrvalName;//订单联系人姓名
	private EmojiEdite mTrvalPhone;//联系人电话
	private EmojiEdite mTrvalNote;//联系人备注
	private String mPhone;//联系电话号码
	private String mName;//联系人姓名
	private String mDiscountMoney = "0";//代金券金额
	private String mDiscountId;//代金券id
	private String mUserId;//代金券用户关联id
	private String realPrice;
	private List<DiscountEntity> discountList;//代金券列表
	private List<TouristEntity.Tourist> mTouristList;//选择的出行人列表
	
	private String isNotRec;//暂不领取代金券
	private RelativeLayout activity_paypricea_select_time,activity_paypricea_add_person;
	// 留言
	private EmojiEdite msgEditText;
	float allPrice;
	private SpotsDialog loadingDialog;

	//保险模块
	private LinearLayout mTouristLinear;
	private LinearLayout mTouristListLinear;
	private ListViewForScrollView mTouristListView;
	private OrderTouristAdapter mTouristAdapter;//出行人适配器
	private LinearLayout mInsureLayout;
	private String mInsureType="";//保险类型
	private TextView mInsureDetail;
	private String mInsureNum;//保险人数
	private String mInsureContactId="";//保险人ID
	private TextView mInsureOrderDetail;
	private int mTotalInsure = 0;//保险价格总和
	
	// 支付按钮
	private Button payConfirmBtn;
	private String route_id, roadTitle, routePrice,routeBackgroundImage,contactId="";
	private String toursit_nums="0",reserve_nums="1";
	private ImageView reserve_minus, reserve_add;
	private TextView reserve_num;
	public final static int REQUEST_CHOOSE_DATE = 100;
	public final static int REQUEST_NUM = 101;
	public final static int REQUEST_SEL = 1;

	private String inday="",outday="";//出行和结束时间
	private SimpleDateFormat simpleDateFormat;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_paypricea;// 订单填写页面

	}

	@SuppressLint("NewApi")
	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		topBack = (TopBackView) findViewById(R.id.order_pay_title);
		topBack.setTitle("订单填写");

		if (null != getIntent()) {
			route_id = getIntent().getStringExtra("route_id");
			roadTitle = getIntent().getStringExtra("routeTitle");
			routePrice = getIntent().getStringExtra("routePrice");
			realPrice = routePrice;
			routeBackgroundImage = getIntent().getStringExtra(
					"routeBackgroundImage");
		}
		mOrderPrie = (TextView)findViewById(R.id.order_confirm_price);
		mOrderPrie.setText("￥"+ routePrice);
		mOrderTopic = (TextView)findViewById(R.id.order_confirm_topic);
		mOrderTopic.setText(roadTitle);
		mPayPriceButton = (Button)findViewById(R.id.uu_order_pay_cofirm_price);
//		mPayPriceButton.setText("实付：￥"+ routePrice);
		mPayPriceButton.setText(routePrice);

		mInsureOrderDetail = (TextView) findViewById(R.id.order_insure_detail);
		
		mDiscountRelative = (LinearLayout) findViewById(R.id.activity_paypricea_add_discount);
		mDiscountRec = (TextView) findViewById(R.id.uu_order_discountRec);//代金券金额
		mDiscountTv = (TextView) findViewById(R.id.uu_tourist_discountRec);//立即领取
		mDiscountRec.setText("暂不使用代金券");
		
		reserve_minus=(ImageView) findViewById(R.id.reserve_minus);
		reserve_add=(ImageView) findViewById(R.id.reserve_add);		
		reserve_num=(TextView) findViewById(R.id.reserve_num);
		uu_tourist_number = (TextView) findViewById(R.id.uu_tourist_number);
		activity_payprice_guide_title = (TextView) findViewById(R.id.activity_payprice_guide_title);
		activity_paypricea_select_time = (RelativeLayout) findViewById(R.id.activity_paypricea_select_time);
		activity_paypricea_add_person = (RelativeLayout) findViewById(R.id.activity_paypricea_add_person);

		headImageView = (SimpleDraweeView) findViewById(R.id.uu_user_img);
		allPrice=Float.parseFloat(routePrice);
		dateTextView = (TextView) findViewById(R.id.uu_data_number);
		msgEditText = (EmojiEdite) findViewById(R.id.order_pay_msg);
		mTrvalName = (EmojiEdite) findViewById(R.id.order_pay_name);
		mTrvalNote = (EmojiEdite) findViewById(R.id.order_pay_note);
		mTrvalPhone = (EmojiEdite) findViewById(R.id.order_pay_phone);
		mTrvalPhone.setInputType(InputType.TYPE_CLASS_PHONE);//限制只能输入电话号码
		payConfirmBtn = (Button) findViewById(R.id.uu_order_pay_cofirm);

		//保险
		mInsureLayout = (LinearLayout)findViewById(R.id.activity_paypricea_add_insure);
		mTouristListLinear = (LinearLayout)findViewById(R.id.order_tourist_list_linear);
		mTouristLinear = (LinearLayout)findViewById(R.id.order_tourist_linear);
		mTouristListView = (ListViewForScrollView) findViewById(R.id.contact_list);
		mTouristListLinear.setVisibility(View.GONE);
		mTouristLinear.setVisibility(View.VISIBLE);
		mInsureDetail = (TextView) findViewById(R.id.order_write_insure);
		ImageLoader.getInstance().displayImage(routeBackgroundImage,
				headImageView);
		activity_payprice_guide_title.setText(roadTitle);
	}

	@Override
	protected void initAction() {
		mPhone = mTrvalPhone.getText().toString();
		mName = mTrvalName.getText().toString();
		if("".equals(mName)){
			mName = MyApplication.getInstance().getUserInfo().getOBJECT().getUserName();
		}
		if("".equals(mPhone)){
			mPhone = MyApplication.getInstance().getUserInfo().getOBJECT().getUserTel();
		}
		reserve_minus.setOnClickListener(this);
		reserve_add.setOnClickListener(this);
		//订单明细
		mInsureOrderDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent();
				i.putExtra("topic",mOrderTopic.getText().toString());
				i.putExtra("perPrice",mOrderPrie.getText().toString()+"x"+reserve_num.getText().toString());
				i.putExtra("insure",mInsureType);
				i.putExtra("insureNum",mInsureNum);
				i.putExtra("discount",mDiscountMoney);
				i.putExtra("realPrice",mPayPriceButton.getText().toString());
				i.setClass(UUPayActivity.this,InsureOrderDetailActivity.class);
				startActivity(i);

			}
		});
		//出行人
		mTouristListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(ctx, TouristListActivity.class);
				intent.putExtra("id", contactId);
				startActivityForResult(intent, REQUEST_NUM);
			}
		});
		//选择保险
		mInsureLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				if(null !=mTouristList && mTouristList.size() > 0){
					for(int a= 0 ; a< mTouristList.size();a++){

						if(!mInsureContactId.contains(mTouristList.get(a).getContactId())) {
							mTouristList.get(a).setContactStatus("0");
						}else{
							mTouristList.get(a).setContactStatus("1");
						}

					}
					i.putExtra("list",(Serializable)mTouristList);
					i.putExtra("type",mInsureType);
					i.setClass(UUPayActivity.this, InsureActivity.class);
					startActivityForResult(i,1000);
				}else {
					CustomToast.showToast(ctx,"请先选择出行人");
				}

			}
		});
		mDiscountTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UUPayActivity.this, MyDiscountActivity.class);
				startActivity(intent);
			}
		});
		activity_paypricea_add_person.setOnClickListener(this);
		activity_paypricea_select_time.setOnClickListener(this);
		payConfirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				payConfirmBtn.setEnabled(false);
				if(!reserve_nums.equals("") && Integer.parseInt(reserve_nums) >= Integer.parseInt(toursit_nums)){
				if (null != dateTextView.getText().toString().trim()
						&& !dateTextView.getText().toString().trim().equals("")) {	
					
						if (isPhoneNumberValid(mPhone)) {
							Intent intent = new Intent();
							intent.putExtra("orderImage", routeBackgroundImage);//路线图片
							intent.putExtra("price", mPayPriceButton.getText().toString());
							intent.putExtra("orderPrice", mPayPriceButton.getText().toString());// 路线价钱
							intent.putExtra("orderName", roadTitle); //路线标题
							intent.putExtra("contactName", mContactName);//出行人名
							intent.putExtra("pageFlag", "UUPayActivity");
							intent.putExtra("orderRoadlineId", route_id);// 路线id
							intent.putExtra("orderTime", dateTextView.getText().toString());// 订单的时间
							intent.putExtra("orderMark", msgEditText.getText().toString());// 订单留言
							intent.putExtra("id", mDiscountId);
							intent.putExtra("userId", mUserId);
							intent.putExtra("contactId", contactId);// 联系人主键id
							intent.putExtra("orderTravelNumber", "" + reserve_nums);//预定数量
							intent.putExtra("visitorName", mName);// 联系人名
							intent.putExtra("visitorTel", mPhone);// 联系人手机号
							intent.putExtra("visitorContent", mTrvalNote.getText().toString());// 联系人留言
							intent.putExtra("orderInsuranceType",mInsureType);
							intent.putExtra("insuranceContactId",mInsureContactId);
							intent.setClass(UUPayActivity.this, UUPaypriceActivity.class);
							startActivity(intent);
						}else{
							// 弹出对话框
							payConfirmBtn.setEnabled(true);
							CustomDialog.Builder builder = new CustomDialog.Builder(
									UUPayActivity.this);
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
					payConfirmBtn.setEnabled(true);
					CustomDialog.Builder builder = new CustomDialog.Builder(
							UUPayActivity.this);
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
				if(Integer.parseInt(reserve_nums) < Integer.parseInt(toursit_nums)) {
					payConfirmBtn.setEnabled(true);
					CustomDialog.Builder builder = new CustomDialog.Builder(
							UUPayActivity.this);
					builder.setMessage("预定数量不能小于出行人数");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int which) {
									dialog.dismiss();

								}
							});
					builder.create().show();
				}else{
					payConfirmBtn.setEnabled(true);
					CustomDialog.Builder builder = new CustomDialog.Builder(
							UUPayActivity.this);
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
			}
		});
	}

	@Override
	protected void initData() {
		getContentInit();
	}

	private void getContentInit() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.add("roadlineId", route_id); // 订单的状态

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
							if (result.getLIST().get(0).getCouponUserStatus().equals("2")) {
								mDiscountRec.setVisibility(View.VISIBLE);
								mDiscountMoney = result.getLIST().get(0).getCouponMoney();
								mDiscountId = result.getLIST().get(0).getCouponId();

								mUserId = result.getLIST().get(0).getCouponUserId();

								allPrice = Float.parseFloat(routePrice) - Float.parseFloat(mDiscountMoney);
								mDiscountRec.setText(mDiscountMoney + "元代金券");
								mPayPriceButton.setText(""+allPrice);
							}
						} else {
							mDiscountRelative.setVisibility(View.GONE);
						}
						mDiscountRelative.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.putExtra("list", (Serializable) discountList);
								intent.putExtra("from", "write");
								intent.setClass(UUPayActivity.this, DiscountSelectActivity.class);
								startActivityForResult(intent, REQUEST_SEL);
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
							new AlertDialog.Builder(UUPayActivity.this)
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
		// TODO Auto-generated method stub
		super.onResume();
		payConfirmBtn.setEnabled(true);

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
			allPrice=Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums)- Float.parseFloat(mDiscountMoney)+mTotalInsure;
			mPayPriceButton.setText(""+allPrice);
			break;
		case R.id.reserve_add:
			reserve_nums = "" + (Integer.parseInt(reserve_nums) + 1);
			reserve_num.setText(reserve_nums);
			if (Integer.parseInt(reserve_nums) > 1) {
				reserve_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
			}
			allPrice=Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums)- Float.parseFloat(mDiscountMoney)+mTotalInsure;
			mPayPriceButton.setText(""+allPrice);
			break;
		}		
	}
	@Override
	public void onNoDoubleClick(View v) {
		Intent toIntent = new Intent();
		switch (v.getId()) {
		case R.id.activity_paypricea_select_time:

			toIntent.putExtra("dateIn",inday);
			toIntent.putExtra("dateOut",outday);
			toIntent.setClass(UUPayActivity.this, DatePickActivity.class);
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

		if (!"".equals(phoneNumber)) {
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
			case 1000:
				float price;
				mInsureNum = data.getStringExtra("num");
				mInsureContactId = data.getStringExtra("allId");
				if(null != data.getStringExtra("type")
						&& !"".equals(data.getStringExtra("type"))
						&& !"0".equals(data.getStringExtra("type"))) {
					mInsureType = data.getStringExtra("type");
					if(mTotalInsure > 0){
						allPrice = Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums)- Float.parseFloat(mDiscountMoney);
					}
					if (mInsureType.contains("1")) {
						mInsureDetail.setText("￥5/天 x " + mInsureNum + "人");
						mTotalInsure = Integer.parseInt(mInsureNum) * 5;
						price = allPrice + mTotalInsure;
						mPayPriceButton.setText(""+price);
					} else if (mInsureType.contains("2")) {
						mInsureDetail.setText("￥10/天 x " + mInsureNum + "人");
						mTotalInsure = Integer.parseInt(mInsureNum) * 10;
						price = allPrice + mTotalInsure;
						mPayPriceButton.setText("" + price);
					} else if (mInsureType.contains("3")) {
						mInsureDetail.setText("￥15/天 x " + mInsureNum + "人");
						mTotalInsure = Integer.parseInt(mInsureNum) * 15;
						price = allPrice + mTotalInsure;
						mPayPriceButton.setText("" + price);
					}else{
						mInsureDetail.setText("选择保险类型");
					}
				}else{
					mTotalInsure = 0;
					allPrice = Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums)- Float.parseFloat(mDiscountMoney);
					mPayPriceButton.setText("" + allPrice);
					mInsureDetail.setText("选择保险类型");
				}

				break;
			case REQUEST_CHOOSE_DATE:
				String chooseDate = data.getStringExtra("choose_date");
				dateTextView.setText(chooseDate);
				dateTextView.setTextColor(Color.parseColor("#000000"));
//				simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
//				if(!"".equals(data.getStringExtra("dateIn")) && !"".equals(data.getStringExtra("dateOut"))){
//					inday = data.getStringExtra("dateIn");
//					outday = data.getStringExtra("dateOut");
//					long days = 0;
//					try {
//						days = ((simpleDateFormat.parse(outday).getTime()-simpleDateFormat.parse(inday).getTime())/86400000) + 1;
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					dateTextView.setText(inday + " - " + outday + " 共" + days+ "天");
//				}

				break;
			case REQUEST_NUM:
				toursit_nums=data.getStringExtra("num");
				contactId=data.getStringExtra("allId");
				mContactName = data.getStringExtra("name");
				mTouristList = (List<TouristEntity.Tourist>) data.getSerializableExtra("list");
				for(int a= 0 ; a< mTouristList.size();a++){
					if ("1".equals(mTouristList.get(a).getContactStatus())) {
						if(!mInsureContactId.contains(mTouristList.get(a).getContactId())) {
							mTouristList.get(a).setContactStatus("0");
						}else{
							mTouristList.get(a).setContactStatus("1");
						}
					}
				}
				if(null !=mTouristList && mTouristList.size() > 0){
					mTouristLinear.setVisibility(View.GONE);
					mTouristListLinear.setVisibility(View.VISIBLE);
					mTouristAdapter = new OrderTouristAdapter(ctx,mTouristList);
					mTouristListView.setAdapter(mTouristAdapter);
				}
//				uu_tourist_number.setText(mContactName);
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
							allPrice = Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums) - Float.parseFloat(mDiscountMoney);
							mPayPriceButton.setText("" + allPrice);
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
					allPrice =Float.parseFloat(routePrice)*Float.parseFloat(reserve_nums);
					mPayPriceButton.setText(""+ allPrice);
				}
				break;
			default:
				break;
			}
		}
	}
}
