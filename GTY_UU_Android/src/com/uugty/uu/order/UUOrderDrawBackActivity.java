package com.uugty.uu.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.OrderDetailEntity;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.OrderDetailEntity.OrderDetail;
import com.uugty.uu.uuchat.ChatActivity;

public class UUOrderDrawBackActivity extends BaseActivity implements OnClickListener{

	private SimpleDraweeView headImageView;
	private TextView nameTextView,cityTextView,createTexView,orderNumTextView
	        ,routTimeTextView,priceTextView,timeTextView,reasonTextView,postDaysTextView;
	private CirculHeadImage headImage;
	private String orderId;
	private SpotsDialog loadingDialog;
	private Button agreeBtn,chutBtn;
	private TopBackView titleback;
	private String userId,avatar,userName;
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_drawback;//申请退款activity
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			orderId = getIntent().getStringExtra("orderId");
		}
		titleback= (TopBackView) findViewById(R.id.order_pay_top_title_refund);
		titleback.setTitle("退款申请");
//		back=(LinearLayout) findViewById(R.id.tabar_back);
		headImageView= (SimpleDraweeView) findViewById(R.id.order_drawback_head_img);
		nameTextView= (TextView) findViewById(R.id.order_drawback_name);
		cityTextView= (TextView) findViewById(R.id.order_drawback_city);
		createTexView= (TextView) findViewById(R.id.order_drawback_order_create_time);
		orderNumTextView= (TextView) findViewById(R.id.order_drawback_order_num);
		routTimeTextView = (TextView) findViewById(R.id.order_drawback_order_time);
		priceTextView= (TextView) findViewById(R.id.order_drawback_order_price);
		headImage = (CirculHeadImage) findViewById(R.id.order_drawback_circul_head);
		timeTextView=(TextView) findViewById(R.id.order_drawback_time);
		reasonTextView=(TextView) findViewById(R.id.order_drawback_reason);
		agreeBtn = (Button) findViewById(R.id.order_drawback_agree);
		chutBtn = (Button) findViewById(R.id.order_drawback_chut);
		postDaysTextView = (TextView) findViewById(R.id.order_drawback_days);
	}

	@Override
	protected void initAction() {
		getContentInit();
//		back.setOnClickListener(this);
		agreeBtn.setOnClickListener(this);
		chutBtn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		back.setClickable(true);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.order_drawback_agree:
			sendOrderDrawback("order_drawback_success");
			break;
		case R.id.order_drawback_chut:
			Intent intent = new Intent();
			intent.putExtra("userId", userId);
			intent.putExtra("avatar", avatar);
			intent.putExtra("userName", userName);
			intent.setClass(this, ChatActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
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

		APPRestClient.post(this, ServiceCode.ORDER_DETAIL, params,
				new APPResponseHandler<OrderDetailEntity>(
						OrderDetailEntity.class,this) {
					@Override
					public void onSuccess(OrderDetailEntity result) {
						if (result != null) {
							loadingDialog.dismiss();
							OrderDetail detail = result.getOBJECT();
							if(detail.getUserAvatar()!=null){
							headImageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP+detail.getRoadlineBackground()));
							avatar = detail.getUserAvatar();
							}
							nameTextView.setText(detail.getUserRealname());
							userName = detail.getUserRealname();
							userId = detail.getUserId();
							cityTextView.setText(detail.getRoadlineGoalArea());
							createTexView.setText(detail.getOrderCreateDate().substring(0, 10));
							orderNumTextView.setText(detail.getOrderNo());
							routTimeTextView.setText(detail.getOrderTime().substring(0, 10));
							priceTextView.setText(detail.getOrderDrawbackMoney());
							headImage.setHeadPic(detail.getUserAvatar(), "net");
							timeTextView.setText(detail.getUserRealname()+"于"+detail.getOrderDrawbackDate()
									.substring(0, 16)+"提出退款申请");
							String reason = "";
							String[] arrys= detail.getOrderDrawbackReason().split(",");
							for(int i=0;i<arrys.length;i++){
								reason+=tranlteReason(Integer.valueOf(arrys[i]));
							}
							reasonTextView.setText("退款申请原因："+reason);
							postDaysTextView.setText("共"+detail.getRoadlineDays()+"天");
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
							new AlertDialog.Builder(UUOrderDrawBackActivity.this)
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
					}}

					@Override
					public void onFinish() {
						

					}
				});

	}

	private String tranlteReason(int count){
		String result="";
		switch (count) {
		case 1:
			result="服务态度差 ";
			break;
		case 2:
			result="没有按时赴约 ";
			break;
		case 3:
			result="其他 ";
			break;

		default:
			result="";
			break;
		}
		return result;
	}
	
	public void sendOrderDrawback(final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("orderStatus", status);
		APPRestClient.post(this, ServiceCode.ORDER_DRAWBACK, params,
				new APPResponseHandler<OrderEntity>(OrderEntity.class,this) {
					@Override
					public void onSuccess(OrderEntity result) {
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendOrderDrawback(status);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(UUOrderDrawBackActivity.this)
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

					}}

					@Override
					public void onFinish() {
					}
				});
	}
}
