package com.uugty.uu.viewpage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.entity.BillRecordEntity.BillRecord;

import java.util.List;

public class BillRecordListAdapter extends BaseAdapter {

	private ViewHolder holder;
	private List<BillRecord> list;
	private Context context;

	public BillRecordListAdapter(Context context, List<BillRecord> list) {
		this.list = list;
		this.context = context;
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
					R.layout.bill_record_list_item, null);
			holder.tradeText = (TextView) convertView
					.findViewById(R.id.bill_record_trade);

			holder.timeTextView = (TextView) convertView
					.findViewById(R.id.bill_record_time);

			holder.priceTextView = (TextView) convertView
					.findViewById(R.id.bill_record_price);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.bill_record_status);
			holder.bill_record_list_item_linear=(LinearLayout) convertView.findViewById(R.id.bill_record_list_item_linear);
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bill_record_list_item_linear.setBackgroundResource(R.drawable.list_item_bg);

		if("order_wx_send".equals(list.get(position).getRecordType())
				|| "order_purse_send".equals(list.get(position).getRecordType())
				|| "order_wxpublic_send".equals(list.get(position).getRecordType())
				|| "order_wxqrcode_send".equals(list.get(position).getRecordType())
				|| "order_ali_send".equals(list.get(position).getRecordType())){
			if(!"".equals(list.get(position).getRoadlineTitle())) {
				holder.tradeText.setText(list.get(position).getRoadlineTitle());
			}else{
				holder.tradeText.setText("开通UU客会员");
			}
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("order_cancel".equals(list.get(position).getRecordType())){
			holder.tradeText.setText(list.get(position).getRoadlineTitle());
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}
		if("order_receive".equals(list.get(position).getRecordType())){
			holder.tradeText.setText(list.get(position).getRoadlineTitle());
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}
		if("gratuity_wx_send".equals(list.get(position).getRecordType())
				|| "gratuity_purse_send".equals(list.get(position).getRecordType())
				|| "gratuity_ali_send".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("红包支付");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("gratuity_receive".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("红包接收");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("gratuity_receiver_reject".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("红包接收过期");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("gratuity_sender_reject".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("红包发送过期退回");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("widthdraw".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("提现");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("wx_recharge".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("微信充值");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("ali_recharge".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("支付宝充值");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("penalty".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("违约金");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("beneficiary".equals(list.get(position).getRecordType())){
			holder.tradeText.setText("三级分销奖励");
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("drawback_outcome".equals(list.get(position).getRecordType())){
			if(!"".equals(list.get(position).getRoadlineTitle())) {
				holder.tradeText.setText(list.get(position).getRoadlineTitle());
			}else{
				holder.tradeText.setText("退款支付");
			}
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.realblack));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}

		if("drawback_income".equals(list.get(position).getRecordType())){
			if(!"".equals(list.get(position).getRoadlineTitle())) {
				holder.tradeText.setText(list.get(position).getRoadlineTitle());
			}else{
				holder.tradeText.setText("退款收入");
			}
			holder.priceTextView.setTextColor(context.getResources().getColor(
					R.color.login_text_color));
			holder.priceTextView.setText( "￥" +list.get(position).getRecordTradeMoney());
			holder.statusTextView.setText(chooseStauts(position,Integer.valueOf(list.get(position).getRecordStatus())));
		}


		holder.timeTextView.setText(list.get(position).getRecordTradeDate());
		return convertView;
	}

	private static class ViewHolder {
		ImageView imageView;
		TextView tradeText, timeTextView, priceTextView,statusTextView;
		LinearLayout bill_record_list_item_linear;
	}

	public String chooseStauts(int position,int num) {
		String name = "";
		switch (num) {
		case 1:
			holder.statusTextView.setTextColor(context.getResources().getColor(
					R.color.order_status_text_color));
			if("drawback_outcome".equals(list.get(position).getRecordType())
					||"drawback_income".equals(list.get(position).getRecordType())){
				name = "退款中";

			}else if("order_wx_send".equals(list.get(position).getRecordType())
					|| "order_purse_send".equals(list.get(position).getRecordType())
					|| "order_wxpublic_send".equals(list.get(position).getRecordType())
					|| "order_wxqrcode_send".equals(list.get(position).getRecordType())
					|| "order_ali_send".equals(list.get(position).getRecordType())){
				name = "等待付款";
			}else {
				name = "进行中";
			}
			break;
		case 2:
			holder.statusTextView.setTextColor(context.getResources().getColor(
					R.color.login_hint_color));
			if("drawback_outcome".equals(list.get(position).getRecordType())
					||"drawback_income".equals(list.get(position).getRecordType())){
				name = "退款成功";

			}else{
				name = "支付成功";
			}

			break;
		case 3:
			holder.statusTextView.setTextColor(context.getResources().getColor(
					R.color.login_hint_color));
			if("drawback_outcome".equals(list.get(position).getRecordType())
					||"drawback_income".equals(list.get(position).getRecordType())){
				name = "退款关闭";

			}else{
				name = "交易关闭";
			}

			break;
		case 4:
			holder.statusTextView.setTextColor(context.getResources().getColor(
					R.color.login_hint_color));
			if("drawback_outcome".equals(list.get(position).getRecordType())
					||"drawback_income".equals(list.get(position).getRecordType())){
				name = "退款取消";

			}else{
				name = "交易取消";
			}

			break;
		default:
			break;
		}
		return name;
	}
	public String chooseBankName(String type) {
		String name = "";
		if (type.equals("order_wx_send"))
			name = "钱包微信支付";
		if (type.equals("order_purse_send"))
			name = "钱包微信支付";
		if (type.equals("order_receive"))
			name = "订单接收";
		if (type.equals("gratuity_wx_send"))
			name = "小费微信发送";
		if (type.equals("gratuity_purse_send"))
			name = "小费钱包发送";
		if (type.equals("gratuity_receive"))
			name = "小费接收";
		if (type.equals("widthdraw"))
			name = "提现";
		if (type.equals("recharge"))
			name = "充值";
		if (type.equals("drawback_outcome"))
			name = "退款支付";
		if (type.equals("drawback_income"))
			name = "退款收入";
		if (type.equals("penalty"))
			name = "违约金";
		return name;
	}

}
