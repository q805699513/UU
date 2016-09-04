package com.uugty.uu.shop.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.uugty.uu.R;
import com.uugty.uu.shop.guide.Model.ThemeCityEntity;

import java.util.List;

/**
 * Created by HolyGoose on 16/9/3.
 */
public class ThemeCityListViewAdapter extends ArrayAdapter {
    private List<ThemeCityEntity.ThemeCity> ls;
    private Context context;

    // private Animation animation;
    // private Map<Integer, Boolean> isFrist;

    public ThemeCityListViewAdapter(Context context,
                                    List<ThemeCityEntity.ThemeCity> themeCityList) {
        this.ls = themeCityList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.theme_city_view, null);
            holder.firstText = (TextView) convertView
                    .findViewById(R.id.theme_city_view_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setBackgroundResource(R.drawable.list_item_bg);
        holder.firstText.setText(ls.get(position).getRoadlineThemeArea());

        return convertView;
    }

    static class ViewHolder {
        TextView firstText;
    }

}
