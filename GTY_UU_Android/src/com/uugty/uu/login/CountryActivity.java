package com.uugty.uu.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.city.customview.CityAdapter;
import com.uugty.uu.city.customview.CityData;
import com.uugty.uu.city.customview.CityItem;
import com.uugty.uu.city.customview.ContactItemInterface;
import com.uugty.uu.city.customview.ContactListViewImpl;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.CountryEntity;

import java.util.ArrayList;
import java.util.List;

public class CountryActivity extends BaseActivity implements OnClickListener,
		AMapLocationListener,TextWatcher {
	//城市列表
	private Context context_ = CountryActivity.this;

	private ContactListViewImpl listview;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;
	
    private LinearLayout button_back;
	private EditText text_content;
//	private TextView city_name;
//	private String locationCity = "";
	private boolean flog=false;
	private RelativeLayout locationLayout;
	private List<CountryEntity.Country> mCountryList;//国家列表
	// 定位
//	private LocationManagerProxy mLocationManagerProxy;
	//private RoadLineService roadLineService;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_mude;
	}

	@Override
	protected void initGui() {
		//roadLineService = new RoadLineService(ctx);
//		initLocation();
		button_back = (LinearLayout) findViewById(R.id.tabar_back);
		text_content = (EditText) findViewById(R.id.route_mude_text);
		locationLayout = (RelativeLayout)findViewById(R.id.location_city_rel_v);
		locationLayout.setVisibility(View.GONE);
//		city_name = (TextView) findViewById(R.id.route_mude_location);// 定位城市?
//		city_name.setClickable(false);
	
		filterList = new ArrayList<ContactItemInterface>();

		listview = (ContactListViewImpl)findViewById(R.id.city_listview);
		listview.setFastScrollEnabled(true);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id)
			{
				List<ContactItemInterface> searchList = inSearchMode ? filterList
						: contactList;
				text_content.setText(searchList.get(position).getDisplayInfo());
				Intent intent = new Intent();
				String s = new String(searchList.get(position).getDisplayInfo());
				String a[] = s.split("\\u002B");//"+"转义

				intent.putExtra("chageCity", a[0]);
				intent.putExtra("code",a[1]);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		text_content.addTextChangedListener(this);
	      

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		button_back.setOnClickListener(this);
//		city_name.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		getConturyList();
	}

	private void getConturyList() {

		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.COUNTRY_CODE, params,
				new APPResponseHandler<CountryEntity>(CountryEntity.class,
						this) {
					@Override
					public void onSuccess(CountryEntity result) {

						if(result.getLIST().size()>0){
							mCountryList = result.getLIST();
							StringBuilder country = new StringBuilder("{'cities':[");
							for(int i=0;i<result.getLIST().size();i++){

								country.append("\'"+result.getLIST().get(i).getName() + " +" + result.getLIST().get(i).getPhoneAreaCode()+"\'"+",");

							}

							country.append("]}");
							contactList = CityData.getSampleContactList(country.toString());
							CityAdapter cityAdapter = new CityAdapter(context_, R.layout.city_item, contactList);
							listview.setAdapter(cityAdapter);


						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getConturyList();
						} else {
							CustomToast.makeText(context_, 0,
									errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(context_)
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		button_back.setClickable(true);
	}
/**
 * 城市列表
 */
	@Override
	public void afterTextChanged(Editable s)
	{
		searchString = text_content.getText().toString().trim().toUpperCase();

		if (curSearchTask != null
				&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			try
			{
				curSearchTask.cancel(true);
			} catch (Exception e)
			{
			}

		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString); 
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		
		// do nothing
	}
	private class SearchListTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params)
		{
			filterList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode)
			{
				// get all the items matching this
				for (ContactItemInterface item : contactList)
				{
					CityItem contact = (CityItem) item;

					boolean isPinyin = contact.getFullName().toUpperCase().indexOf(keyword) > -1;
					boolean isChinese = contact.getNickName().indexOf(keyword) > -1;

					if (isPinyin || isChinese)
					{
						filterList.add(item);
					}

				}

			}
			return null;
		}

		protected void onPostExecute(String result)
		{

			synchronized (searchLock)
			{

				if (inSearchMode)
				{

					CityAdapter adapter = new CityAdapter(context_,R.layout.city_item, filterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				} else
				{
					CityAdapter adapter = new CityAdapter(context_,R.layout.city_item, contactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}

		}
	}

	
	
	/**
	 * 初始化定?
	 */
	private void initLocation() {

//		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请?
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间??1，则定位只定一?
//		mLocationManagerProxy.requestLocationData(
//				LocationProviderProxy.AMapNetwork, -1, 15, this);

//		mLocationManagerProxy.setGpsEnable(false);
	}

	/**
	 * 定位成功后回调函?
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			Double geoLat = amapLocation.getLatitude();
			Double geoLng = amapLocation.getLongitude();

//			locationCity = amapLocation.getCity();
//			city_name.setText(locationCity);
			String desc = "";
			Bundle locBundle = amapLocation.getExtras();
			if (locBundle != null) {
				desc = locBundle.getString("desc");
			}

//			if (null != locationCity
//					&& !locationCity.equals("")) {
//				city_name.setClickable(true);
//				flog=true;
//			}
		}else{
//			city_name.setText("定位失败");
			flog=false;
		}
	}

	private void stopLocation() {
//		if (mLocationManagerProxy != null) {
//			mLocationManagerProxy.removeUpdates(this);
//			mLocationManagerProxy.destory();
//		}
//		mLocationManagerProxy = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	@Override
	public void onNoDoubleClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			button_back.setClickable(false);
			break;
		case R.id.route_mude_location:
//			if(flog){
//			text_content.setText(locationCity);
//			intent.putExtra("chageCity",locationCity);
//			setResult(RESULT_OK, intent);
//			finish();
//			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}
}
