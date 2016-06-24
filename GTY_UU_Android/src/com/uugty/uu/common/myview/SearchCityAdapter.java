package com.uugty.uu.common.myview;

import java.util.ArrayList;
import java.util.List;

import com.uugty.uu.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class SearchCityAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<SearchAutoData> mOriginalValues;// ���е�Item
	private List<SearchAutoData> mObjects;// ���˺��item
	private final Object mLock = new Object();
	private int mMaxMatch = 10;// �����ʾ���ٸ�ѡ��,�����ʾȫ��

	public SearchCityAdapter(Context context, int maxMatch) {
		this.mContext = context;
		this.mMaxMatch = maxMatch;
		initSearchHistory();
		mObjects = mOriginalValues;
	}

	public int getSize() {
		return mObjects.size();
	}

	@Override
	public int getCount() {
		return null == mObjects ? 0 : mObjects.size();
	}

	@Override
	public Object getItem(int position) {
		if (position == mObjects.size()) {
			clear();
		}
		return null == mObjects ? 0 : mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AutoHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.seach_list_item, parent, false);
			holder = new AutoHolder();
			holder.content = (TextView) convertView
					.findViewById(R.id.auto_content);
			convertView.setTag(holder);
		} else {
			holder = (AutoHolder) convertView.getTag();
		}
		SearchAutoData data = mObjects.get(position);
		holder.content.setText(data.getContent());
		return convertView;
	}

	/**
	 * ��ȡ��ʷ������¼
	 */
	public void initSearchHistory() {
		SharedPreferences sp = mContext.getSharedPreferences(
				SearchCityWindow.SEARCH_CITY_HISTORY, 0);
		String longhistory = sp.getString(SearchCityWindow.SEARCH_CITY_HISTORY, "");
		String[] hisArrays = longhistory.split(",");
		mOriginalValues = new ArrayList<SearchAutoData>();
		if (longhistory.equals("")) {
			return;
		}
		for (int i = 0; i < hisArrays.length; i++) {
			mOriginalValues.add(new SearchAutoData().setContent(hisArrays[i]));
		}
		mObjects = mOriginalValues;
		notifyDataSetChanged();
	}

	/**
	 * �����ʷ
	 */
	public void clear() {
		SharedPreferences sp = mContext.getSharedPreferences(
				SearchCityWindow.SEARCH_CITY_HISTORY, 0);
		sp.edit().putString(SearchCityWindow.SEARCH_CITY_HISTORY, "").commit();
		mObjects = null;
		mObjects = new ArrayList<SearchAutoData>();
		mOriginalValues = null;
		mOriginalValues = new ArrayList<SearchAutoData>();
		notifyDataSetChanged();
	}

	/**
	 * ƥ�������������
	 * 
	 * @param prefix
	 *            ����������������
	 */
	public void performFiltering(CharSequence prefix) {
		if (prefix == null || prefix.length() == 0) {// ����������Ϊ�յ�ʱ����ʾ������ʷ��¼
			synchronized (mLock) {
				mObjects = mOriginalValues;
			}
		} else {
			String prefixString = prefix.toString().toLowerCase();
			int count = mOriginalValues.size();
			ArrayList<SearchAutoData> newValues = new ArrayList<SearchAutoData>(
					count);
			for (int i = 0; i < count; i++) {
				final String value = mOriginalValues.get(i).getContent();
				final String valueText = value.toLowerCase();
				if (valueText.contains(prefixString)) {

				}
				if (valueText.startsWith(prefixString)) {
					newValues.add(new SearchAutoData().setContent(valueText));
				} else {
					final String[] words = valueText.split(" ");
					final int wordCount = words.length;
					for (int k = 0; k < wordCount; k++) {
						if (words[k].startsWith(prefixString)) {
							newValues.add(new SearchAutoData()
									.setContent(value));
							break;
						}
					}
				}
				if (mMaxMatch > 0) {
					if (newValues.size() > mMaxMatch - 1) {
						break;
					}
				}
			}
			mObjects = newValues;
		}
		notifyDataSetChanged();
	}

	private class AutoHolder {
		TextView content;
	}
}
