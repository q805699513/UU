package com.uugty.uu.viewpage.adapter;

import java.util.List;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.entity.MoreLvEntity.MoreListEntity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class MoreListAdapter extends BaseAdapter {
	private List<MoreListEntity> list;
	private Context context;
	private ViewHolder holder;
	private String flag; //0--详情页面  1--路线列表

	public MoreListAdapter(List<MoreListEntity> list, Context context,String flag) {
		this.list = list;
		this.context = context;
		this.flag= flag;
	}

	@Override
	public int getCount() {
		if(flag.equals("0")){
			if (list.size() <= 3) {
				return list.size();
			} else {
				return 3;
			}
		}else{
			return list.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.more_listview_item, null);
			holder.img = (SimpleDraweeView) view.findViewById(R.id.more_Listview_img);
			holder.stars = (RatingBar) view
					.findViewById(R.id.more_Listview_stars);
			holder.price = (TextView) view
					.findViewById(R.id.more_Listview_price);
			holder.title = (TextView) view
					.findViewById(R.id.more_Listview_title);
			holder.more_listview_linerlayout = (LinearLayout) view
					.findViewById(R.id.more_listview_linerlayout);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.more_listview_linerlayout
				.setBackgroundResource(R.drawable.list_item_bg);
		if (!list.get(position).getRoadlineBackground().equals("")) {
			if (list.get(position).getRoadlineBackground().contains("images")) {
				holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP
								+ list.get(position).getRoadlineBackground()));
			} else {
				holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP + "images/roadlineDescribe/"
						+ list.get(position).getRoadlineBackground()));
			}
		} else {
			// 加载默认图片
			holder.img.setImageURI(Uri.parse("res///"+R.drawable.uu_default_image_one));
		}
		holder.price.setText(list.get(position).getRoadlinePrice());
		holder.title.setText(list.get(position).getRoadlineTitle());
		holder.stars.setRating(Float.valueOf(list.get(position)
				.getAvageTotalIndex()));
		return view;
	}

	class ViewHolder {
		private SimpleDraweeView img;
		private TextView title, price;
		private RatingBar stars;
		private LinearLayout more_listview_linerlayout;
	}
}
