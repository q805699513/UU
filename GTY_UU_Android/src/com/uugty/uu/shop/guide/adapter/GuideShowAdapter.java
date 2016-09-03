package com.uugty.uu.shop.guide.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.shop.guide.Model.GuideEntity;

import java.util.List;

/**
 * Created by HolyGoose on 16/9/3.
 */
public class GuideShowAdapter extends BaseAdapter {
    private List<GuideEntity.GuideDetail> ls;
    private Context context;

    public GuideShowAdapter(Context context, List<GuideEntity.GuideDetail> list) {
        this.ls = list;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.guide_detail_listview_item_new, null);

            holder.imageView = (SimpleDraweeView) convertView
                    .findViewById(R.id.guide_detail_item_image);
            holder.titleText = (TextView) convertView
                    .findViewById(R.id.guide_detail_item_title);
            holder.orderNumText = (TextView) convertView
                    .findViewById(R.id.guide_detail_item_num_text);
            holder.lookNumText = (TextView) convertView
                    .findViewById(R.id.guide_detail_item_look_num_text);

            holder.marketPriceText = (TextView) convertView.findViewById(R.id.guide_marketprice);
            holder.nowPrice = (TextView) convertView.findViewById(R.id.guide_nowprice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.marketPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.marketPriceText.setText("￥" +ls.get(position).getRoadlineMarketPrice());
        holder.nowPrice.setText("￥" + ls.get(position).getRoadlinePrice());
        if (!ls.get(position).getRoadlineBackground().equals("")) {
            if (ls.get(position).getRoadlineBackground().contains("images")) {
                holder.imageView.setImageURI(Uri
                        .parse(APPRestClient.SERVER_IP
                                + ls.get(position).getRoadlineBackground()));
            } else {
                holder.imageView.setImageURI(Uri
                        .parse(APPRestClient.SERVER_IP + "images/roadlineDescribe/"
                                + (ls.get(position).getRoadlineBackground())));

            }
        } else {
            // 加载默认图片
            holder.imageView.setImageURI(Uri.parse("res:///"
                    + R.drawable.uu_default_image_one));
        }

        holder.titleText.setText(ls.get(position).getRoadlineTitle());
        holder.orderNumText.setText(ls.get(position).getOrderCount());
        if (null != ls.get(position).getLineNum()
                && !ls.get(position).getLineNum().equals("")) {
            holder.lookNumText.setText(ls.get(position).getLineNum());
        } else {
            holder.lookNumText.setText("0");
        }
        return convertView;
    }
    static class ViewHolder {
        SimpleDraweeView imageView;
        TextView titleText, orderNumText, lookNumText,marketPriceText,nowPrice;
    }

}
