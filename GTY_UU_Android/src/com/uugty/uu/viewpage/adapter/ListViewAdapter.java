package com.uugty.uu.viewpage.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.entity.OrderListItem.ItemEntity;
import com.uugty.uu.evaluate.EvaluateActivity;
import com.uugty.uu.order.ApplyRefundActivity;
import com.uugty.uu.order.UUOrederPayActivity;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

	private ViewHolder holder;
	private List<ItemEntity> list;
	private Activity context;
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public ListViewAdapter(List<ItemEntity> list, Context context, String role) {
		super();
		this.list = list;
		this.context = (Activity) context;
		this.role = role;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_carrydetail, null);
			holder.imageView = (SimpleDraweeView) convertView
					.findViewById(R.id.order_image);
//			holder.order_nums_text = (TextView) convertView
//					.findViewById(R.id.order_num);
			holder.title = (TextView) convertView
					.findViewById(R.id.order_time);
			holder.orderYear = (TextView) convertView
					.findViewById(R.id.order_date);
//			holder.roadPrice = (TextView) convertView
//					.findViewById(R.id.order_city);
			holder.orderDate = (TextView) convertView.findViewById(R.id.order_date_sum);
			holder.statusTextView = (TextView) convertView
					.findViewById(R.id.order_state);
			holder.priceTextView = (TextView) convertView
					.findViewById(R.id.order_unit);
			holder.evaluateTextView = (TextView) convertView
					.findViewById(R.id.order_evaluate);
			holder.txt_cancle = (TextView) convertView
					.findViewById(R.id.order_cancle);
			holder.txt_pay = (TextView) convertView
					.findViewById(R.id.order_pay);
			holder.txt_chat = (TextView) convertView
					.findViewById(R.id.order_chat);
			holder.activity_carrydetail_linearla = (LinearLayout) convertView
					.findViewById(R.id.activity_carrydetail_linearla);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.order_nums_text.setText("x" + list.get(position).getOrderTravelNumber());
		holder.orderYear.setText(list.get(position).getOrderTime().substring(0,10) + " 日出行");
		holder.orderDate.setText("共" + list.get(position).getRoadlineDays() +"天");
		holder.activity_carrydetail_linearla
				.setBackgroundResource(R.drawable.list_item_bg);
		if (list.get(position).getUserAvatar() != null
				&& !list.get(position).getUserAvatar().equals("")) {
			holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ "images/roadlineDescribe/"
					+ list.get(position).getRoadlineBackGround()));
		}
		if (list.get(position).getOrderTitle() != null
				&& !list.get(position).getOrderTitle().equals("")) {
			holder.title.setText(list.get(position).getOrderTitle());
		}
//		holder.roadPrice
//				.setText("￥"+ Float.parseFloat(list.get(position).getOrderPrice().substring(0,list.get(position).getOrderPrice().indexOf("."))));

		holder.statusTextView.setText(transOrderStatus(list.get(position)
				.getOrderStatus()));

		if(!TextUtils.isEmpty(list.get(position).getOrderTravelNumber())&&!TextUtils.isEmpty(list.get(position).getOrderPrice())){
			if("0.0".equals(list.get(position).getOrderCouponMoney())){
				holder.priceTextView.setText("合计:  ￥" + Integer.parseInt(list.get(position).getOrderTravelNumber())*Float.parseFloat(list.get(position).getOrderPrice().substring(0,list.get(position).getOrderPrice().indexOf("."))));
			}else{
				holder.priceTextView.setText("合计:  ￥"
						+ (Integer.parseInt(list.get(position).getOrderTravelNumber())
								* Float.parseFloat(list.get(position).getOrderPrice())
								- Float.parseFloat(list.get(position).getOrderCouponMoney()))
						);
			}
		}else{
			holder.priceTextView.setText("0");
		}
		
		// 游客
		if (role.equals("1")) {
			if (list.get(position).getOrderStatus().equals("order_create")) {
				holder.txt_pay
						.setBackgroundResource(R.drawable.text_view_board_black);
				// holder.txt_pay.setBackgroundColor(Color.parseColor("#000000"));
				holder.txt_pay.setTextColor(Color.parseColor("#ffffff"));
			} else {
				holder.txt_pay
						.setBackgroundResource(R.drawable.text_view_board);
				holder.txt_pay.setTextColor(Color.parseColor("#666666"));
			}
			// 未付款
			if (list.get(position).getOrderStatus().equals("order_create")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_cancle.setText("取消订单");
				holder.txt_pay.setText("支付");
			}
			if (list.get(position).getOrderStatus().equals("order_payment")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("取消订单");
			}
			if (list.get(position).getOrderStatus().equals("order_invalid")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus()
					.equals("order_no_pay_cancel")
					|| list.get(position).getOrderStatus()
							.equals("order_agree_cancel")
					|| list.get(position).getOrderStatus()
							.equals("order_not_agree_cancel")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus().equals("order_agree")) {

				/*
				 * if (DateUtil.comperaBeforeTime(list.get(position)
				 * .getOrderTime().substring(0, 10)))
				 */
				holder.evaluateTextView.setVisibility(View.VISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_cancle.setText("申请退款");
				holder.txt_pay.setText("旅行完成");
				if (list.get(position).getIsEval().equals("0")) {
					holder.evaluateTextView.setText("评价");
				} else {
					holder.evaluateTextView.setText("修改评价");
				}

			}
			// 个人模式下，缺少已拒绝
			if (list.get(position).getOrderStatus().equals("order_deny")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			// 订单完成
			if (list.get(position).getOrderStatus().equals("order_success")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
				
				if (list.get(position).getIsEval().equals("0")) {
				  holder.txt_cancle.setText("评价订单"); } else {
				 holder.txt_cancle.setText("修改评价"); }
				 /* 
				 * holder.txt_pay.setText("申请退款");
				 */
			}
			// 退款中
			if (list.get(position).getOrderStatus().equals("order_drawback")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);

				if (list.get(position).getIsEval().equals("0")) {
					holder.txt_cancle.setText("评价订单");
				} else {
					holder.txt_cancle.setText("修改评价");
				}

				holder.txt_pay.setText("取消退款");
			}
			// 退款完成
			if (list.get(position).getOrderStatus()
					.equals("order_drawback_success")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_cancle.setText("删除订单");
				if (list.get(position).getIsEval().equals("0")) {
					holder.txt_pay.setText("评价订单");
				} else {
					holder.txt_pay.setText("修改评价");
				}
			}
			// 退款关闭
			if (list.get(position).getOrderStatus().equals("order_drawback_close")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_cancle.setText("删除订单");
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				if (list.get(position).getIsEval().equals("0")) {
					holder.txt_pay.setText("评价订单");
				} else {
					holder.txt_pay.setText("修改评价");
				}
			}
			if (list.get(position).getOrderStatus()
					.equals("order_drawback_failure")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.INVISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			// 取消订单
			holder.txt_cancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (list.get(position).getOrderStatus()
							.equals("order_success")) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("orderId", list.get(position)
								.getOrderId());
						bundle.putString("name", list.get(position)
								.getUserRealname());
						bundle.putString("headPic", list.get(position)
								.getUserAvatar());
						bundle.putString("isEval", list.get(position)
								.getIsEval());
						intent.putExtras(bundle);
						intent.setClass(context, EvaluateActivity.class);
						context.startActivity(intent);
					} else if (list.get(position).getOrderStatus()
							.equals("order_drawback")) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("orderId", list.get(position)
								.getOrderId());
						bundle.putString("name", list.get(position)
								.getUserName());
						bundle.putString("headPic", list.get(position)
								.getUserAvatar());
						bundle.putString("isEval", list.get(position)
								.getIsEval());
						intent.putExtras(bundle);
						intent.setClass(context, EvaluateActivity.class);
						context.startActivity(intent);
					} else if (list.get(position).getOrderStatus()
							.equals("order_agree")) {
						Intent intent = new Intent();
						intent.putExtra("orderId", list.get(position)
								.getOrderId());
						intent.putExtra("price", (Float.parseFloat(list.get(position)
								.getOrderPrice())*Float.parseFloat(list.get(position).getOrderTravelNumber())-Float.parseFloat(list.get(position).getOrderCouponMoney()))+"");
						intent.setClass(context, ApplyRefundActivity.class);
						context.startActivityForResult(intent, 1000);
					} else if(list.get(position).getOrderStatus()
							.equals("order_drawback_success") || list.get(position).getOrderStatus()
							.equals("order_drawback_close")){
						
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("确认删除订单吗");
						builder.setTitle("删除订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										SendDelOrderRequest(position);
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
						
						
					} else {

						// 弹出对话框
						// 弹出框，确认删除
						String dialogMsg = "您的订单正在取消";
						if (list.get(position).getOrderStatus()
								.equals("order_create")
								|| list.get(position).getOrderStatus()
										.equals("order_agree")) {
							dialogMsg = "您的订单正在取消";
						}
						/*
						 * if (list.get(position).getOrderStatus()
						 * .equals("order_agree")) { dialogMsg =
						 * "导游已确认订单，现在取消订单将扣除总价的15%补偿导游的损失"; }
						 */
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage(dialogMsg);
						builder.setTitle("取消订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										if (list.get(position).getOrderStatus()
												.equals("order_create")) {
											sendVisitorRequst(position,
													"order_no_pay_cancel");
										}
										if (list.get(position).getOrderStatus()
												.equals("order_agree")) {
											sendVisitorRequst(position,
													"order_agree_cancel");
										}
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
				}
			});

			holder.evaluateTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (list.get(position).getOrderStatus()
							.equals("order_agree")) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("orderId", list.get(position)
								.getOrderId());
						bundle.putString("name", list.get(position)
								.getUserName());
						bundle.putString("headPic", list.get(position)
								.getUserAvatar());
						bundle.putString("isEval", list.get(position)
								.getIsEval());
						intent.putExtras(bundle);
						intent.setClass(context, EvaluateActivity.class);
						context.startActivity(intent);
					}
				}
			});

			// 支付
			holder.txt_pay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					if(list.get(position).getOrderStatus()
							.equals("order_drawback_close")){
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("orderId", list.get(position)
								.getOrderId());
						bundle.putString("name", list.get(position)
								.getUserRealname());
						bundle.putString("headPic", list.get(position)
								.getUserAvatar());
						bundle.putString("isEval", list.get(position)
								.getIsEval());
						intent.putExtras(bundle);
						intent.setClass(context, EvaluateActivity.class);
						context.startActivity(intent);
					}
					//删除订单逻辑
					if (list.get(position).getOrderStatus()
							.equals("order_no_pay_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_agree_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_not_agree_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_invalid")
							|| list.get(position).getOrderStatus()
									.equals("order_deny")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("确认删除订单吗");
						builder.setTitle("删除订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										SendDelOrderRequest(position);
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
					
					
					
					if (list.get(position).getOrderStatus()
							.equals("order_create")) {
						Intent intent = new Intent();
						intent.putExtra("orderId", list.get(position)
								.getOrderId());
						intent.setClass(context, UUOrederPayActivity.class);
						context.startActivity(intent);
					}
					if (list.get(position).getOrderStatus()
							.equals("order_drawback_success")) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("orderId", list.get(position)
								.getOrderId());
						bundle.putString("name", list.get(position)
								.getUserName());
						bundle.putString("headPic", list.get(position)
								.getUserAvatar());
						bundle.putString("isEval", list.get(position)
								.getIsEval());
						intent.putExtras(bundle);
						intent.setClass(context, EvaluateActivity.class);
						context.startActivity(intent);
					}
					if (list.get(position).getOrderStatus()
							.equals("order_payment")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("您的订单正在取消");
						builder.setTitle("取消订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										sendVisitorRequst(position,
												"order_not_agree_cancel");
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
					if (list.get(position).getOrderStatus()
							.equals("order_agree")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("确认订单完成，打款给小U。");
						builder.setTitle("确认订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										// 调用旅行完成
										sendOrderCompRequst(position);
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
					if (list.get(position).getOrderStatus()
							.equals("order_success")) {
							CustomDialog.Builder builder = new CustomDialog.Builder(
									context);
							builder.setMessage("确认删除订单吗");
							builder.setTitle("删除订单");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											dialog.dismiss();
											SendDelOrderRequest(position);
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
						if (list.get(position).getOrderStatus()
								.equals("order_drawback")) {
							// 发请求
							sendOrderDrawback(position,
									"order_drawback_failure");
						
					}
				}
			});

		}
		// 导游
		if (role.equals("2")) {
			if (list.get(position).getOrderStatus().equals("order_create")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.INVISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus().equals("order_payment")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.VISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_cancle.setText("接受");
				holder.txt_pay.setText("拒绝");
			}
			if (list.get(position).getOrderStatus().equals("order_agree")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.INVISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus().equals("order_deny")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus()
					.equals("order_agree_cancel")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus()
					.equals("order_not_agree_cancel")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus().equals("order_success")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus().equals("order_drawback")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_chat.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("同意退款");
			}
			if (list.get(position).getOrderStatus()
					.equals("order_drawback_success")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getOrderStatus()
					.equals("order_drawback_failure")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			// 未付款取消
			if (list.get(position).getOrderStatus()
					.equals("order_no_pay_cancel")
					|| list.get(position).getOrderStatus()
							.equals("order_agree_cancel")
					|| list.get(position).getOrderStatus()
							.equals("order_not_agree_cancel")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			// 已失效
			if (list.get(position).getOrderStatus().equals("order_invalid")) {
				holder.evaluateTextView.setVisibility(View.INVISIBLE);
				holder.txt_cancle.setVisibility(View.INVISIBLE);
				holder.txt_pay.setVisibility(View.VISIBLE);
				holder.txt_pay.setText("删除订单");
				holder.txt_chat.setVisibility(View.VISIBLE);
			}
			// 取消订单
			holder.txt_cancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (list.get(position).getOrderStatus()
							.equals("order_payment")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("您确定要接受订单吗");
						builder.setTitle("确认订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										// 发请求
										sendGuideRequst(position, "order_agree");
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
					
				}
			});

			holder.txt_pay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (list.get(position).getOrderStatus()
							.equals("order_payment")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("您确定要拒绝订单吗");
						builder.setTitle("确认订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										// 发请求
										sendGuideRequst(position, "order_deny");
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
					
					//删除订单逻辑
					if (list.get(position).getOrderStatus()
							.equals("order_no_pay_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_agree_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_not_agree_cancel")
							|| list.get(position).getOrderStatus()
									.equals("order_invalid")
							|| list.get(position).getOrderStatus()
									.equals("order_drawback_close")
							|| list.get(position).getOrderStatus()
									.equals("order_drawback_success")
							|| list.get(position).getOrderStatus()
									.equals("order_success")
							|| list.get(position).getOrderStatus()
									.equals("order_deny")) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								context);
						builder.setMessage("确认删除订单吗");
						builder.setTitle("删除订单");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										SendDelOrderRequest(position);
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
//					if (list.get(position).getOrderStatus()
//							.equals("order_drawback")) {
//						// 发请求
//						sendOrderDrawback(position, "order_drawback_failure");
//					}
					if (list.get(position).getOrderStatus()
							.equals("order_drawback")) {
						// 发请求
						sendOrderDrawback(position, "order_drawback_success");
					}

				}
			});
		}
		holder.txt_chat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("userId", list.get(position).getUserId());
				intent.putExtra("avatar", list.get(position).getUserAvatar());
				intent.putExtra("userName", list.get(position)
						.getUserRealname());
				intent.setClass(context, ChatActivity.class);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		SimpleDraweeView imageView;
		TextView title,statusTextView,priceTextView, evaluateTextView,orderDate,orderYear;
		TextView txt_pay, txt_chat, txt_cancle;
		LinearLayout activity_carrydetail_linearla;
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
			rsultStatus = "已取消";
		else if (status.equals("order_success"))
			rsultStatus = "订单完成";
		else if (status.equals("order_drawback"))
			rsultStatus = "退款中";
		else if (status.equals("order_drawback_success"))
			rsultStatus = "退款完成";
		else if (status.equals("order_drawback_failure"))
			rsultStatus = "退款已拒绝";
		else if(status.equals("order_drawback_close"))
			rsultStatus = "退款关闭";
		
		return rsultStatus;
	}

	public void sendVisitorRequst(final int count, final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", list.get(count).getOrderId()); // 路线id
		params.add("OrderStatus", status);
		APPRestClient
				.post(context, ServiceCode.ORDER_CANCLE, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								list.remove(count);
								ListViewAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendVisitorRequst(count,status);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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

	public void sendGuideRequst(final int count, final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", list.get(count).getOrderId()); // 路线id
		params.add("OrderStatus", status);
		APPRestClient
				.post(context, ServiceCode.ORDER_INVITATION, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								if (status.equals("order_agree")) {
									list.get(count).setOrderStatus(
											"order_agree");
									list.remove(count);
								}
								if (status.equals("order_deny")) {
									list.remove(count);
								}
								ListViewAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendGuideRequst(count, status);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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

	public void sendOrderCompRequst(final int count) {
		RequestParams params = new RequestParams();
		params.add("orderId", list.get(count).getOrderId()); // 路线id
		APPRestClient
				.post(context, ServiceCode.ORDER_COMPLETE, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								// list.get(count).setOrderStatus("order_success");
								list.remove(count);
								ListViewAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendOrderCompRequst(count);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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

	public void sendOrderDrawback(final int count, final String status) {
		RequestParams params = new RequestParams();
		params.add("orderId", list.get(count).getOrderId()); // 路线id
		params.add("orderStatus", status); // 路线id
		APPRestClient
				.post(context, ServiceCode.ORDER_DRAWBACK, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								list.remove(count);
								ListViewAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendOrderDrawback(count, status);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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
	
	/**
	 * 删除订单
	 * @param count
	 */
	public void SendDelOrderRequest(final int count) {
		RequestParams params = new RequestParams();
		params.add("orderId", list.get(count).getOrderId()); // 路线id
		APPRestClient
				.post(context, ServiceCode.Del_Order, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								list.remove(count);
								ListViewAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									SendDelOrderRequest(count);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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
