package com.uugty.uu.viewpage.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.uugty.uu.R;
import com.uugty.uu.entity.ChannelItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OtherGrideAdapter extends BaseAdapter {

	private Context context;

	/** 可以拖动的列表（即用户选择的频道列表） */
	public List<ChannelItem> channelList;
	/** TextView 频道内容 */
	private TextView item_text;

	public OtherGrideAdapter(Context context, List<ChannelItem> channelList) {
		this.context = context;
		this.channelList = channelList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		// TODO Auto-generated method stub
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		ChannelItem channel = getItem(position);
		item_text.setText(channel.getText());
		/*if ((position == 0) || (position == 1)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
			item_text.setEnabled(false);
		}*/
	/*	if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + channelList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}*/
		return view;
	}

	/** 添加first频道列表 */
	public void addFirstItem(ChannelItem channel) {
		channelList.add(channel);
		notifyDataSetChanged();
	}
	/** 添加频道列表 */
	@SuppressWarnings("unchecked")
	public void addItem(ChannelItem channel) {
		channelList.add(channel);
		 Collections.sort(channelList, new Comparator() { 
			 @Override
		      public int compare(Object o1, Object o2) {
				 if (((ChannelItem)o1).getId()>((ChannelItem)o2).getId()) {
						return 0;
					} else if (((ChannelItem)o1).getId()<((ChannelItem)o2).getId()) {
						return -1;
					} else {
						return 1;
					}
		      }
		    });
		
		 notifyDataSetChanged();
		
	}
	
	/** 获取频道列表 */
	public List<ChannelItem> getChannnelLst() {
		return channelList;
	}
	
	/** 设置频道列表 */
	public void setListDate(List<ChannelItem> list) {
		channelList = list;
	}
}