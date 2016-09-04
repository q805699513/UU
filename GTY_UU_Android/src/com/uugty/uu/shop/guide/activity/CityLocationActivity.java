package com.uugty.uu.shop.guide.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.shop.guide.Model.ThemeCityEntity;
import com.uugty.uu.shop.guide.Model.ThemeCityEntity.ThemeCity;
import com.uugty.uu.shop.guide.adapter.ThemeCityListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CityLocationActivity extends BaseActivity {
	private TopBackView tilteView;
	private ListView locationListView;
	private List<ThemeCity> themeCityList = new ArrayList<ThemeCity>();
	private ThemeCityListViewAdapter adapter;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.citylocation_item;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		tilteView = (TopBackView) findViewById(R.id.location_titile_top);
		tilteView.setTitle("选择城市");
		locationListView = (ListView) findViewById(R.id.location_title_listview);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		locationListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("themeCity", themeCityList.get(position).getRoadlineThemeArea());
                setResult(RESULT_OK, intent);
                finish();
			}
		});
	}

	@Override
	protected void initData() {

		getCityList();
	}

	private void getCityList() {

		RequestParams params = new RequestParams();

		APPRestClient.postGuide(this, ServiceCode.GUIDE_CITY, params,
				new APPResponseHandler<ThemeCityEntity>(ThemeCityEntity.class,
						this) {
					@Override
					public void onSuccess(ThemeCityEntity result) {
                        if(result.getLIST().size()>0){

							themeCityList = result.getLIST();

                        	adapter = new ThemeCityListViewAdapter(CityLocationActivity.this, themeCityList);
                        	locationListView.setAdapter(adapter);
                        }
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getCityList();
						} else {
						CustomToast.makeText(CityLocationActivity.this, 0,
								errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(CityLocationActivity.this)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													finish();
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
