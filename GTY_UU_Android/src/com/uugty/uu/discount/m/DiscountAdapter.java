package com.uugty.uu.discount.m;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.discount.m.DiscountListItem.DiscountEntity;
import com.uugty.uu.entity.OrderEntity;
import com.uugty.uu.viewpage.adapter.ListViewAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscountAdapter extends BaseAdapter {
	
	private ViewHolder holder;
	private List<DiscountEntity> list;
	private Activity context;

	public DiscountAdapter(List<DiscountEntity> list, Context context) {
		super();
		this.list = list;
		this.context = (Activity) context;
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
					R.layout.activity_discount_list, null);
			holder.mTopExpired = (LinearLayout) convertView
					.findViewById(R.id.discount_top_linear_expired);
			holder.mTopUse = (TextView) convertView
					.findViewById(R.id.discount_top_linear_use);
			holder.mMidTv = (TextView) convertView
					.findViewById(R.id.discount_mid_tv);
			holder.mPrice = (TextView) convertView
					.findViewById(R.id.discount_mid_price);
			holder.mName = (TextView) convertView
					.findViewById(R.id.discount_mid_name);
			holder.mOrder = (TextView) convertView
					.findViewById(R.id.discount_mid_order);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.discount_mid_time);
			holder.mReceive = (TextView) convertView
					.findViewById(R.id.discount_mid_receive);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if("1".equals(list.get(position).getFlag())){
			holder.mTopExpired.setVisibility(View.VISIBLE);
		}else{
			holder.mTopExpired.setVisibility(View.GONE);
		}
		
		if("4".equals(list.get(position).getCouponUserStatus())
				||"5".equals(list.get(position).getCouponUserStatus())
				||"6".equals(list.get(position).getCouponUserStatus())){
			holder.mTopUse.setVisibility(View.VISIBLE);
			holder.mTopExpired.setVisibility(View.GONE);
			holder.mMidTv.setTextColor(context.getResources().getColor(R.color.black));
			holder.mPrice.setTextColor(context.getResources().getColor(R.color.black));
		}else{
			holder.mTopUse.setVisibility(View.GONE);
		}
		
		if("1".equals(list.get(position).getCouponUserStatus())){
			holder.mReceive.setVisibility(View.VISIBLE);
		}else{
			holder.mReceive.setVisibility(View.GONE);
		}
		if("".equals(list.get(position).getCouponMoney())){
			holder.mPrice.setText(" ");
		}else{
			holder.mPrice.setText(list.get(position).getCouponMoney());
		}
		if("".equals(list.get(position).getCouponName())){
			holder.mName.setText(" ");
		}else{
			holder.mName.setText(list.get(position).getCouponName());
		}
		if("".equals(list.get(position).getCouponOrderPrice())){
			holder.mOrder.setText(" ");
		}else{
			holder.mOrder.setText("满"+list.get(position).getCouponOrderPrice()+"元使用");
		}
		if("".equals(list.get(position).getCouponUserEndDate())){
			holder.mTime.setText(" ");
		}else{
			holder.mTime.setText(list.get(position).getCouponUserEndDate().substring(0, 16)+"到期");
		}
		holder.mReceive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				CustomDialog.Builder builder = new CustomDialog.Builder(
						context);
				builder.setMessage("是否领取代金券");
				builder.setTitle("代金券");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if (null != list.get(position).getCouponId()) {
									sendReceiveRequst(position);
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
		});
		
		return convertView;
	}
	
	private static class ViewHolder {
		LinearLayout mTopExpired;//即将到期
		TextView mTopUse;//已使用
		TextView mMidTv;//人名币符号
		TextView mPrice;//优惠金额
		TextView mName;//优惠券名称
		TextView mOrder;//优惠券限制规则
		TextView mTime;//优惠券到期时间
		TextView mReceive;//立即领取
	}
	
	
	/**
	 * 立即领取
	 * @param count
	 */
	public void sendReceiveRequst(final int count) {
		RequestParams params = new RequestParams();
		params.add("couponId", list.get(count).getCouponId()); // 代金券ID
		params.add("couponUserId", list.get(count).getCouponUserId());//用户和代金券关联表主键id
		APPRestClient
				.post(context, ServiceCode.DISCOUNT_REC, params,
						new APPResponseHandler<OrderEntity>(OrderEntity.class,
								context) {
							@Override
							public void onSuccess(OrderEntity result) {
								list.remove(count);
								DiscountAdapter.this.notifyDataSetChanged();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(context, 0, errorMsg, 300)
										.show();
								if (errorCode == 3) {
									sendReceiveRequst(count);
								} else {
								if (errorCode == -999) {
									new AlertDialog.Builder(context)
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

}
