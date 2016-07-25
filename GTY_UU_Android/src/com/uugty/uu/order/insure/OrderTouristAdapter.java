package com.uugty.uu.order.insure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.entity.TouristEntity;

import java.util.ArrayList;
import java.util.List;

public class OrderTouristAdapter extends BaseAdapter {
		private Context context;
		private List<TouristEntity.Tourist> ls=new ArrayList<TouristEntity.Tourist>();

		public OrderTouristAdapter(Context context, List<TouristEntity.Tourist> ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			return ls.size();
		}

		@Override
		public Object getItem(int position) {
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			ViewHolder holder;
			if(view==null){
				holder=new ViewHolder();
				view=LayoutInflater.from(context).inflate(R.layout.order_tourist_item_layout, null);
				holder.touristname_text=(TextView) view.findViewById(R.id.order_tourist_text);
				holder.tourist_id_card_text=(TextView) view.findViewById(R.id.order_tourist_id_card_text);
				holder.tourist_num=(TextView) view.findViewById(R.id.order_tourist_num);

				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}

			holder.touristname_text.setText(ls.get(position).getContactName());
			holder.tourist_id_card_text.setText(ls.get(position).getContactIDCard());
			holder.tourist_num.setText(String.valueOf(position+1));

			return view;
		}
		class ViewHolder{
			TextView touristname_text,tourist_id_card_text,tourist_num;
		}

}
