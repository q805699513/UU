package com.uugty.uu.common.myview;

import java.util.ArrayList;
import java.util.Arrays;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.main.GuideDetailActivity;
import com.uugty.uu.main.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ViewConstructor")
public class SearchCityWindow extends BaseActivity {
	public static final String SEARCH_CITY_HISTORY = "search_city_history";
	/**
	 * 搜索框
	 */
	private EditText searchEdit;
	/**
	 * 搜索按钮
	 */
	private Button searchBtn;
	/**
	 * 历史搜索
	 */
	private ListView listView;
	private SearchCityAdapter mSearchAutoAdapter;
	private View footer;
	private boolean hasFooter = false;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.search_popu;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		initWidget();
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 初始化搜索栏控件
	 * 
	 * @param view
	 */
	private void initWidget() {
		mSearchAutoAdapter = new SearchCityAdapter(this, -1);
		searchEdit = (EditText) findViewById(R.id.etSearch);
		searchBtn = (Button) findViewById(R.id.btnSearch);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				searchEdit.setFocusable(true);
				searchEdit.setFocusableInTouchMode(true);
				InputMethodManager inputManager = (InputMethodManager) searchEdit
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 300);

		listView = (ListView) findViewById(R.id.auto_listview);
		footer = View.inflate(this, R.layout.footer, null);
		if (haveHistory()) {
			listView.addFooterView(footer);
			hasFooter = true;
		}
		listView.setAdapter(mSearchAutoAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (mSearchAutoAdapter.getSize() == position) {
					mSearchAutoAdapter.clear();
					listView.removeFooterView(footer);
					hasFooter = false;
				} else {
					SearchAutoData data = (SearchAutoData) mSearchAutoAdapter
							.getItem(position);
					searchEdit.setText(data.getContent());
					searchEdit.setSelection(data.getContent().length());
				}
			}
		});
		searchEdit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSearchAutoAdapter.performFiltering(s);
				int i = mSearchAutoAdapter.getSize();
				if (i == 0) {
					if (hasFooter) {
						listView.removeFooterView(footer);
						hasFooter = false;
					}
				} else {
					if (!hasFooter) {
						listView.addFooterView(footer);
						listView.setAdapter(mSearchAutoAdapter);
						hasFooter = true;
					}
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					searchBtn.setText("取消");
				} else {
					searchBtn.setText("搜索");
				}
			}
		});
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveSearchHistory();
				if (null != searchEdit.getText().toString()
						&& !searchEdit.getText().toString().equals("")) {
					Intent intent = new Intent();
					intent.putExtra("location", searchEdit.getText().toString());
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(SearchCityWindow.this,
							MainActivity.class);
					startActivity(intent);
				} else {
					finish();
				}
			}
		});
	}

	/**
	 * 有无历史记录
	 * 
	 * @return
	 */
	private boolean haveHistory() {
		SharedPreferences sp = this.getSharedPreferences(SEARCH_CITY_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_CITY_HISTORY, "");
		if (longhistory.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 保存搜索记录
	 */
	private void saveSearchHistory() {
		String text = searchEdit.getText().toString().trim();
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = this.getSharedPreferences(SEARCH_CITY_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_CITY_HISTORY, "");
		String[] tmpHistory = longhistory.split(",");
		ArrayList<String> history = new ArrayList<String>(
				Arrays.asList(tmpHistory));
		if (history.size() > 0) {
			int i;
			for (i = 0; i < history.size(); i++) {
				if (text.equals(history.get(i))) {
					history.remove(i);
					break;
				}
			}
			history.add(0, text);
		}
		if (history.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < history.size(); i++) {
				sb.append(history.get(i) + ",");
			}
			sp.edit().putString(SEARCH_CITY_HISTORY, sb.toString()).commit();
		} else {
			sp.edit().putString(SEARCH_CITY_HISTORY, text + ",").commit();
		}
	}

	

}
