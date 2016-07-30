package com.uugty.uu.uuchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.city.customview.CityListActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.myview.UnderLineTextAndImage;
import com.uugty.uu.entity.ChatCustomEntity;
import com.uugty.uu.main.OrderDateActivty;

public class ChatCustomActivity extends BaseActivity implements OnClickListener {

	private TopBackView titleView;
	private UnderLineTextAndImage chat_custom_starting,
			chat_custom_destination, chat_custom_starting_time;
	private EmojiEdite chat_costom_need_edt, chat_costom_phone,
			chat_costom_name;
	private Button submit;
	private ImageView travel_days_minus, travel_days_add, people_num_minus,
			people_num_add, price_num_minus, price_num_add;
	private TextView chat_costom_travel, chat_costom_people, chat_costom_price;
	private TextView chat_custom_label1, chat_custom_label2,
			chat_custom_label3, chat_custom_label4, chat_custom_label5;
	private String starting = "";// 出发地
	private String destination = "";// 目的地
	private String starting_time = "";// 出发时间
	private String travel_days = "1";// 旅游天数
	private String people_num = "1";// 出行人数
	private String price_num = "500";// 出行预算
	private String label = "";// 标签
	private String name = "";// 姓名
	private String phone = "";// 手机号
	private String demand = "";// 需求
	public final static int REQUEST_CHOOSE_DATE = 100; // 日期请求
	public final static int REQUEST_STARTING = 101; // 出发地请求
	public final static int REQUEST_DESTINATION = 102; // 目的地请求
	private String flag = "false";
	private String type="";

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_chat_custom;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			flag = getIntent().getStringExtra("flag");
			if(null!=getIntent().getStringExtra("type")){		
				type=getIntent().getStringExtra("type");
			}
		}
		titleView = (TopBackView) findViewById(R.id.chat_custom_title);
		titleView.setTitle("旅行定制");
		chat_custom_starting = (UnderLineTextAndImage) findViewById(R.id.chat_custom_starting);
		chat_custom_destination = (UnderLineTextAndImage) findViewById(R.id.chat_custom_destination);
		chat_custom_starting_time = (UnderLineTextAndImage) findViewById(R.id.chat_custom_starting_time);
		chat_costom_phone = (EmojiEdite) findViewById(R.id.chat_costom_phone);
		chat_costom_name = (EmojiEdite) findViewById(R.id.chat_costom_name);
		chat_costom_need_edt = (EmojiEdite) findViewById(R.id.chat_costom_need_edt);
		submit = (Button) findViewById(R.id.chat_costom_submit);
		travel_days_minus = (ImageView) findViewById(R.id.travel_days_minus);
		travel_days_add = (ImageView) findViewById(R.id.travel_days_add);
		people_num_minus = (ImageView) findViewById(R.id.people_num_minus);
		people_num_add = (ImageView) findViewById(R.id.people_num_add);
		price_num_minus = (ImageView) findViewById(R.id.price_num_minus);
		price_num_add = (ImageView) findViewById(R.id.price_num_add);
		chat_costom_travel = (TextView) findViewById(R.id.chat_costom_travel);
		chat_costom_people = (TextView) findViewById(R.id.chat_costom_people);
		chat_costom_price = (TextView) findViewById(R.id.chat_costom_price);
		// 1：亲子游 2：自由行 3：亲友游 4：蜜月 5：陪老人
		chat_custom_label1 = (TextView) findViewById(R.id.chat_custom_label1);
		chat_custom_label2 = (TextView) findViewById(R.id.chat_custom_label2);
		chat_custom_label3 = (TextView) findViewById(R.id.chat_custom_label3);
		chat_custom_label4 = (TextView) findViewById(R.id.chat_custom_label4);
		chat_custom_label5 = (TextView) findViewById(R.id.chat_custom_label5);

	}

	@Override
	protected void initAction() {
//		if(type.equals("1")){
//			//引导页
//			int myservices = SharedPreferenceUtil.getInstance(ctx)
//					.getInt("mycustomization", 0);
//			if (myservices == 0) {
//				Intent intent = new Intent();
//				intent.putExtra("type", "定制");
//				intent.setClass(ChatCustomActivity.this,
//						LeadPageActivity.class);
//				startActivity(intent);
//				SharedPreferenceUtil.getInstance(ctx).setInt("mycustomization", 1);
//			} 
//		}
		// TODO Auto-generated method stub
		chat_custom_starting.setLeftText("出发地");
		chat_custom_starting.hideLine();
		chat_custom_destination.setLeftText("目的地");
		chat_custom_destination.hideLine();
		chat_custom_starting_time.setLeftText("出发时间");
		chat_custom_starting_time.hideLine();
		travel_days_minus.setOnClickListener(this);
		travel_days_add.setOnClickListener(this);
		people_num_minus.setOnClickListener(this);
		people_num_add.setOnClickListener(this);
		price_num_minus.setOnClickListener(this);
		price_num_add.setOnClickListener(this);
		chat_custom_starting.setOnClickListener(this);
		chat_custom_destination.setOnClickListener(this);
		chat_custom_starting_time.setOnClickListener(this);
		chat_custom_label1.setOnClickListener(this);
		chat_custom_label2.setOnClickListener(this);
		chat_custom_label3.setOnClickListener(this);
		chat_custom_label4.setOnClickListener(this);
		chat_custom_label5.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		// 旅行天数
		case R.id.travel_days_minus:
			if (Integer.parseInt(travel_days) > 1) {
				travel_days = "" + (Integer.parseInt(travel_days) - 1);
				travel_days_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
				chat_costom_travel.setText(travel_days);
				if (Integer.parseInt(travel_days) <= 1) {
					travel_days_minus
							.setImageResource(R.drawable.chat_costom_minus);
				}
			}
			break;
		case R.id.travel_days_add:
			travel_days = "" + (Integer.parseInt(travel_days) + 1);
			chat_costom_travel.setText(travel_days);
			if (Integer.parseInt(travel_days) > 1) {
				travel_days_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
			}
			break;
		// 旅行人数
		case R.id.people_num_minus:
			if (Integer.parseInt(people_num) > 1) {
				people_num = "" + (Integer.parseInt(people_num) - 1);
				people_num_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
				chat_costom_people.setText(people_num);
				if (Integer.parseInt(people_num) <= 1) {
					people_num_minus
							.setImageResource(R.drawable.chat_costom_minus);
				}
			}
			break;
		case R.id.people_num_add:
			people_num = "" + (Integer.parseInt(people_num) + 1);
			chat_costom_people.setText(people_num);
			if (Integer.parseInt(people_num) > 1) {
				people_num_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
			}
			break;
		// 预算
		case R.id.price_num_minus:
			if (Integer.parseInt(price_num) > 500) {
				price_num = "" + (Integer.parseInt(price_num) - 500);
				price_num_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
				chat_costom_price.setText(price_num+"/元");
				if (Integer.parseInt(price_num) <= 500) {
					price_num_minus
							.setImageResource(R.drawable.chat_costom_minus);
				}
			}
			break;
		case R.id.price_num_add:
			price_num = "" + (Integer.parseInt(price_num) + 500);
			chat_costom_price.setText(price_num+"/元");
			if (Integer.parseInt(price_num) > 500) {
				price_num_minus
						.setImageResource(R.drawable.chat_costom_minus_enable);
			}
			break;
		case R.id.chat_custom_label1:
			clearLabel();
			chat_custom_label1.setTextColor(Color.parseColor("#ffffff"));
			chat_custom_label1.setBackgroundResource(R.drawable.lable_bg_color);
			label = "亲子游";
			break;
		case R.id.chat_custom_label2:
			clearLabel();
			chat_custom_label2.setTextColor(Color.parseColor("#ffffff"));
			chat_custom_label2.setBackgroundResource(R.drawable.lable_bg_color);
			label = "自由行";
			break;
		case R.id.chat_custom_label3:
			clearLabel();
			chat_custom_label3.setTextColor(Color.parseColor("#ffffff"));
			chat_custom_label3.setBackgroundResource(R.drawable.lable_bg_color);
			label = "亲友游";
			break;
		case R.id.chat_custom_label4:
			clearLabel();
			chat_custom_label4.setTextColor(Color.parseColor("#ffffff"));
			chat_custom_label4.setBackgroundResource(R.drawable.lable_bg_color);
			label = "蜜月";
			break;
		case R.id.chat_custom_label5:
			clearLabel();
			chat_custom_label5.setTextColor(Color.parseColor("#ffffff"));
			chat_custom_label5.setBackgroundResource(R.drawable.lable_bg_color);
			label = "陪老人";
			break;
		default:
			break;
		}
	}

	public void clearLabel() {
		chat_custom_label1.setTextColor(Color.parseColor("#000000"));
		chat_custom_label2.setTextColor(Color.parseColor("#000000"));
		chat_custom_label3.setTextColor(Color.parseColor("#000000"));
		chat_custom_label4.setTextColor(Color.parseColor("#000000"));
		chat_custom_label5.setTextColor(Color.parseColor("#000000"));
		chat_custom_label1.setBackgroundResource(R.drawable.boder_solid_black);
		chat_custom_label2.setBackgroundResource(R.drawable.boder_solid_black);
		chat_custom_label3.setBackgroundResource(R.drawable.boder_solid_black);
		chat_custom_label4.setBackgroundResource(R.drawable.boder_solid_black);
		chat_custom_label5.setBackgroundResource(R.drawable.boder_solid_black);
	}

	public boolean isIntact() {
		name = chat_costom_name.getText().toString();
		phone = chat_costom_phone.getText().toString();
		boolean flog = true;
		if (starting.equals("")) {
			CustomToast.makeText(ctx, 0, "出发地未填写！", 200).show();
			flog = false;
		} else if (destination.equals("")) {
			CustomToast.makeText(ctx, 0, "目的地未填写！", 200).show();
			flog = false;
		} else if (starting_time.equals("")) {
			CustomToast.makeText(ctx, 0, "出发时间未填写！", 200).show();
			flog = false;
		} else if (label.equals("")) {
			CustomToast.makeText(ctx, 0, "标签未填写！", 200).show();
			flog = false;
		} else if (name.equals("")) {
			CustomToast.makeText(ctx, 0, "姓名未填写！", 200).show();
			flog = false;
		} else if (phone.equals("")) {
			CustomToast.makeText(ctx, 0, "电话未填写！", 200).show();
			flog = false;
		}else if(phone.length()!=11){
			CustomToast.makeText(ctx, 0, "请输入11位手机号码！", 200).show();
			flog = false;
		}
		return flog;
	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		Intent toIntent = new Intent();
		switch (v.getId()) {
		case R.id.chat_costom_submit:
			if (!isIntact()) {

			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
				if (!TextUtils.isEmpty(flag) && flag.equals("true")) {
					builder.setMessage("发送需求给目的地的所有小u，等待小u联系您？");
				}else{
					builder.setMessage("将您填写的需求发送给小u");
				}
				
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Submitdemand();
								dialog.dismiss();
								submit.setEnabled(false);
							}
						});

				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			}
			break;
		case R.id.chat_custom_starting_time:
			toIntent.setClass(ChatCustomActivity.this, OrderDateActivty.class);
			startActivityForResult(toIntent, REQUEST_CHOOSE_DATE);
			break;
		case R.id.chat_custom_starting:
			toIntent.setClass(ctx, CityListActivity.class);
			startActivityForResult(toIntent, REQUEST_STARTING);
			break;
		case R.id.chat_custom_destination:
			toIntent.setClass(ctx, CityListActivity.class);
			startActivityForResult(toIntent, REQUEST_DESTINATION);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CHOOSE_DATE:
				String chooseDate = data.getStringExtra("choose_date");
				chat_custom_starting_time.setRightTextRight(chooseDate);
				starting_time = chooseDate;
				break;
			case REQUEST_STARTING:
				String chageCity = data.getStringExtra("chageCity");
				chat_custom_starting.setRightTextRight(chageCity);
				starting = chageCity;
				break;
			case REQUEST_DESTINATION:
				String chageCitys = data.getStringExtra("chageCity");
				chat_custom_destination.setRightTextRight(chageCitys);
				destination = chageCitys;
				break;
			}
		}
	}

	// 提交需求
	private void Submitdemand() {
		RequestParams params = new RequestParams();
		params.add("customDestination", destination);
		params.add("customPlaceOfDeparture", starting);
		params.add("customStartingTime", starting_time);
		params.add("customTravelTime", travel_days);
		params.add("customTravelNum", people_num);
		params.add("customBudget", price_num);
		params.add("customMark", label);
		params.add("customRealName", name);
		params.add("customDemandContent",chat_costom_need_edt.getText().toString());
		params.add("customTel", phone);
		if (!TextUtils.isEmpty(flag) && flag.equals("true")) {
			params.add("flag", "true");
		} else {
			params.add("flag", "false");
		}
		APPRestClient.post(this, ServiceCode.ADD_TRAVEL_CUSTOM, params,
				new APPResponseHandler<ChatCustomEntity>(
						ChatCustomEntity.class, this) {
					@Override
					public void onSuccess(ChatCustomEntity result) {
						if (null != result.getOBJECT()) {
							if (!TextUtils.isEmpty(flag) && flag.equals("true")) {
								finish();
							} else {
								Intent intent = new Intent();
								intent.putExtra("customId", result.getOBJECT()
										.getCustomId());
								intent.putExtra("customDestination", destination);
								intent.putExtra("customBudget", price_num);
								intent.putExtra("customMark", label);
								intent.putExtra("toFrom", "ChatCustomActivity");
								intent.setClass(ChatCustomActivity.this,
										ChatActivity.class);
								startActivity(intent);
								finish();
							}

						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							Submitdemand();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ChatCustomActivity.this)
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
}
