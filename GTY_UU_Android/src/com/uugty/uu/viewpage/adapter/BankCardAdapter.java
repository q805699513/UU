package com.uugty.uu.viewpage.adapter;

import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.R.drawable;
import com.uugty.uu.entity.BoundBankEntity.BankCardInfo;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BankCardAdapter extends BaseAdapter {

	private List<BankCardInfo> cardList;
	private Context context;
	private ViewHolder holder;
	private String fromType;

	public BankCardAdapter(List<BankCardInfo> ls, Context context,
			String fromType) {
		super();
		this.cardList = ls;
		this.context = context;
		this.fromType = fromType;
	}

	public void updateList(List<BankCardInfo> list) {
		this.cardList = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != cardList) {
			return cardList.size();
		} else {
			return 0;
		}
	}

	@Override
	public BankCardInfo getItem(int position) {
		// TODO Auto-generated method stub
		return cardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.bound_bank_card_item, null);
			holder.typeImageView = (SimpleDraweeView) convertView
					.findViewById(R.id.bound_bank_card_item_type_image);

//			holder.defaultImageView = (SimpleDraweeView) convertView
//					.findViewById(R.id.bound_bank_card_item_default);

			holder.bankNameTextView = (TextView) convertView
					.findViewById(R.id.bound_bank_card_item_type);
			holder.cardNoTextView = (TextView) convertView
					.findViewById(R.id.bound_bank_card_item_cardno);
			holder.bound_bank_card_item_rel=(RelativeLayout) convertView.findViewById(R.id.bound_bank_card_item_rel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bound_bank_card_item_rel.setBackgroundResource(R.drawable.list_item_bg);
		holder.typeImageView.setImageURI(Uri.parse("res///"+chooseImage(Integer.valueOf(cardList.get(position)
				.getBankCardType()))));
		
		holder.bankNameTextView.setText(chooseBankName(Integer.valueOf(cardList
				.get(position).getBankCardType())));
		holder.cardNoTextView.setText(cardList.get(position).getBankCard());
//		if (cardList.get(position).getBankIsDefault().equals("1")) {
//			/*
//			 * ImageLoader.getInstance().displayImage( "drawable://" +
//			 * R.drawable.map_shout_confirm_btn, holder.defaultImageView);
//			 */
//			holder.defaultImageView
//					.setImageResource(R.drawable.bank_card_selected);
//		}else{
//			holder.defaultImageView
//			.setImageResource(R.drawable.bank_card_select);
//		}

		return convertView;
	}

	private static class ViewHolder {
		SimpleDraweeView typeImageView;
		TextView bankNameTextView, cardNoTextView;
		RelativeLayout bound_bank_card_item_rel;
	}

	public int chooseImage(int num) {
		Class<drawable> cls = R.drawable.class;
		int imageRes = 0;
		try {
			switch (num) {
			case 1:
				imageRes = cls.getDeclaredField("bank_china").getInt(null);
				break;
			case 2:
				imageRes = cls.getDeclaredField("bank_abc").getInt(null);
				break;
			case 3:
				imageRes = cls.getDeclaredField("bank_icbc").getInt(null);
				break;
			case 4:
				imageRes = cls.getDeclaredField("bank_ccb").getInt(null);
				break;
			case 5:
				imageRes = cls.getDeclaredField("bank_bcm").getInt(null);
				break;
			case 6:
				imageRes = cls.getDeclaredField("bank_cmb").getInt(null);
				break;
			case 7:
				imageRes = cls.getDeclaredField("bank_ceb").getInt(null);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageRes;
	}

	public String chooseBankName(int num) {
		String name = "";
		switch (num) {
		case 1:
			name = "中国银行";
			break;
		case 2:
			name = "中国农业银行";
			break;
		case 3:
			name = "中国工商银行";
			break;
		case 4:
			name = "中国建设银行";
			break;
		case 5:
			name = "中国交通银行";
			break;
		case 6:
			name = "中国招商银行";
			break;
		case 7:
			name = "中国光大银行";
			break;
		default:
			break;
		}
		return name;
	}
}
