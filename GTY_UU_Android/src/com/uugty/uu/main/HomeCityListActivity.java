package com.uugty.uu.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
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
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.ThemeCityEntity;

public class HomeCityListActivity extends BaseActivity implements OnClickListener,TextWatcher{
	//城市列表
	private Context context_ = HomeCityListActivity.this;

	private ContactListViewImpl listview;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	List<ContactItemInterface> contactList;
	List<ContactItemInterface> foreginList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;
	
	private View view;//listview头部view 
    private LinearLayout button_back;
    private LinearLayout mRecentlyLayout,mHotCityLayout;
	private EditText text_content;
	private TextView china,foreign;
//	private TextView city_name;
	private GridView mRecentlyCity,mForeginCity,mHotCity;
	private String city;//最近访问的三个城市
	private String city1;//国外最近访问的三个城市
//	private String locationCity = "";
	private static List<String> mRecentlyCityList = new ArrayList<String>();
	private static List<String> mRecentlyForeginCityList = new ArrayList<String>();
	private List<String> mChinaHotCityList = new ArrayList<String>();
	private List<String> mForeginHotCityList = new ArrayList<String>();
	private boolean flog=false;
	private boolean isChina = true;
	private boolean isAddHistory = true;
	private boolean isForeginHistory = true;
	// 定位
	private LocationManagerProxy mLocationManagerProxy;
	//private RoadLineService roadLineService;

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_home_city;
	}

	@Override
	protected void initGui() {
		//roadLineService = new RoadLineService(ctx);
//		initLocation();
		button_back = (LinearLayout) findViewById(R.id.tabar_back);
		text_content = (EditText) findViewById(R.id.route_mude_text);
		
		listview = (ContactListViewImpl)findViewById(R.id.city_listview);
		china = (TextView) findViewById(R.id.china);
		foreign = (TextView)findViewById(R.id.foreign);
		
		
		filterList = new ArrayList<ContactItemInterface>();
//		view = LayoutInflater.from(ctx).inflate(R.layout.home_city_headview, null);
		mRecentlyLayout = (LinearLayout) findViewById(R.id.home_city_recently_layout);
		mHotCityLayout = (LinearLayout) findViewById(R.id.home_city_hot_layout);
//		city_name = (TextView) findViewById(R.id.route_mude_location);// 定位城市?
//		city_name.setClickable(false);
		listview.setFastScrollEnabled(true);
		mRecentlyCity = (GridView)findViewById(R.id.home_city_recently_gridview);
		mForeginCity = (GridView)findViewById(R.id.home_city_foregin_gridview);
        mHotCity = (GridView)findViewById(R.id.home_city_hot_gridview);
        
        
		initChinaHistoryCity(); 
		
//		listview.addHeaderView(view);
        
        
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id)
			{
				List<ContactItemInterface> searchList;
				if(isChina){
					searchList = inSearchMode ? filterList
							: contactList;
				}else{
					searchList = inSearchMode ? filterList
							: foreginList;
				}
				
				text_content.setText(searchList.get(position).getDisplayInfo());
				Intent intent = new Intent();
				intent.putExtra("themeCity", searchList.get(position).getDisplayInfo());
				if(isChina){
					SharedPreferenceUtil.getInstance(ctx).setString("city", searchList.get(position).getDisplayInfo());
				}else{
					SharedPreferenceUtil.getInstance(ctx).setString("city1", searchList.get(position).getDisplayInfo());
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		

		text_content.addTextChangedListener(this);

	}

	private void initChinaHistoryCity() {
		city = SharedPreferenceUtil.getInstance(ctx).getString("city", null);
		mForeginCity.setVisibility(View.GONE);
		mRecentlyCity.setVisibility(View.VISIBLE);
		if (city != null && mRecentlyCityList.size() == 0) {
			mRecentlyCityList.add(city);
		}
		if (mRecentlyCityList.size() == 0) {
			mRecentlyLayout.setVisibility(View.GONE);
		} else {
			mRecentlyLayout.setVisibility(View.VISIBLE);
			for (int i = 0; i < mRecentlyCityList.size(); i++) {
				if (mRecentlyCityList.get(i).equals(city)) {
					isAddHistory = false;
				}
			}
			if (isAddHistory) {
				mRecentlyCityList.add(city);
			}
			List<String> tmp = new ArrayList<>();
			for (int i = mRecentlyCityList.size(); i > 0; i--) {
				if (mRecentlyCityList.get(i - 1).equals(city)) {
					tmp.add(0, city);
				} else {
					tmp.add(mRecentlyCityList.get(i - 1));
				}
			}
			HomeCityAdapter adapter = new HomeCityAdapter(context_, tmp);
			mRecentlyCity.setAdapter(adapter);
		}
	}
	
	private void initForeignHistoryCity() {
		city1 = SharedPreferenceUtil.getInstance(ctx).getString("city1", null);
		mRecentlyCity.setVisibility(View.GONE);
		mForeginCity.setVisibility(View.VISIBLE);
		if (city1 != null && mRecentlyForeginCityList.size() == 0) {
			mRecentlyForeginCityList.add(city1);
		}
		if (mRecentlyForeginCityList.size() == 0) {
			mRecentlyLayout.setVisibility(View.GONE);
		} else {
			mRecentlyLayout.setVisibility(View.VISIBLE);
			for (int i = 0; i < mRecentlyForeginCityList.size(); i++) {
				if (mRecentlyForeginCityList.get(i).equals(city1)) {
					isForeginHistory = false;
				}
			}
			if (isForeginHistory) {
				mRecentlyForeginCityList.add(city1);
			}
			List<String> tmp = new ArrayList<>();
			for (int i = mRecentlyForeginCityList.size(); i > 0; i--) {
				if (mRecentlyForeginCityList.get(i - 1).equals(city1)) {
					tmp.add(0, city1);
				} else {
					tmp.add(mRecentlyForeginCityList.get(i - 1));
				}
			}
			HomeCityAdapter adapter = new HomeCityAdapter(context_, tmp);
			mForeginCity.setAdapter(adapter);
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		button_back.setOnClickListener(this);
//		city_name.setOnClickListener(this);
		china.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChina = true;
				initChinaHistoryCity(); 
				china.setBackgroundColor(getResources().getColor(R.color.login_text_color));
				china.setTextColor(getResources().getColor(R.color.white));
				foreign.setBackgroundColor(getResources().getColor(R.color.white));
				foreign.setTextColor(getResources().getColor(R.color.btn_gray_normal));
				if(mChinaHotCityList.size()>0 ||contactList.size()>0){
					mHotCityLayout.setVisibility(view.VISIBLE);
					HomeCityAdapter adapter = new HomeCityAdapter(context_, mChinaHotCityList);
					mHotCity.setAdapter(adapter);
					CityAdapter cityAdapter = new CityAdapter(context_,R.layout.city_item, contactList);
            		listview.setAdapter(cityAdapter);
				}else{
					mHotCityLayout.setVisibility(view.GONE);
				}
			}
		});
		foreign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isChina = false;
				initForeignHistoryCity(); 
				china.setBackgroundColor(getResources().getColor(R.color.white));
				china.setTextColor(getResources().getColor(R.color.btn_gray_normal));
				foreign.setBackgroundColor(getResources().getColor(R.color.login_text_color));
				foreign.setTextColor(getResources().getColor(R.color.white));
				CityAdapter cityAdapter = new CityAdapter(context_,R.layout.city_item, foreginList);
				listview.setAdapter(cityAdapter);
				if(mForeginHotCityList.size()>0 || foreginList.size()>0){
					mHotCityLayout.setVisibility(view.VISIBLE);
					HomeCityAdapter adapter = new HomeCityAdapter(context_, mForeginHotCityList);
					mHotCity.setAdapter(adapter);
				}else{
					mHotCityLayout.setVisibility(view.GONE);
				}
			}
		});
		
	}

	@Override
	protected void initData() {
		getCityList();
	}
	
	private void getCityList() {

		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.THEME_CITY, params,
				new APPResponseHandler<ThemeCityEntity>(ThemeCityEntity.class,
						this) {
					@Override
					public void onSuccess(ThemeCityEntity result) {
						
                        if(result.getLIST().size()>0){
                        	StringBuilder chinaBuilder = new StringBuilder("{'cities':[");
                        	StringBuilder foreginBuilder = new StringBuilder("{'cities':[");
                        	for(int i=0;i<result.getLIST().size();i++){
                        		if(result.getLIST().get(i).getAreaType().equals("1")){
                        			chinaBuilder.append("\'"+result.getLIST().get(i).getRoadlineThemeArea()+"\'"+",");
                        		}else{
                        			foreginBuilder.append("\'"+result.getLIST().get(i).getRoadlineThemeArea()+"\'"+",");
                        		}
                        		if(result.getLIST().get(i).getRoadlineIsHot().equals("2")){
                        			if(result.getLIST().get(i).getAreaType().equals("1")){
                        				mChinaHotCityList.add(result.getLIST().get(i).getRoadlineThemeArea());
                        			}else{
                        				mForeginHotCityList.add(result.getLIST().get(i).getRoadlineThemeArea());
                        			}
                        		}
                        	}
							if(mChinaHotCityList.size() == 0){
								mHotCityLayout.setVisibility(View.GONE);
							}
                        	chinaBuilder.append("]}");
                        	foreginBuilder.append("]}");
							HomeCityAdapter adapter = new HomeCityAdapter(context_, mChinaHotCityList);
							mHotCity.setAdapter(adapter);
							contactList = CityData.getSampleContactList(chinaBuilder.toString());
							CityAdapter cityAdapter = new CityAdapter(context_, R.layout.city_item, contactList);
							listview.setAdapter(cityAdapter);
							foreginList = CityData.getSampleContactList(foreginBuilder.toString());
                        	

                        }
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getCityList();
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
		if(count == 0){
			mRecentlyLayout.setVisibility(View.VISIBLE);
			mHotCityLayout.setVisibility(View.VISIBLE);
		}else{
			mRecentlyLayout.setVisibility(View.GONE);
			mHotCityLayout.setVisibility(View.GONE);
		}
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
				List<ContactItemInterface> List;
				if(isChina){
					List = contactList;
				}else{
					List = foreginList;
				}
				for (ContactItemInterface item : List)
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
					List<ContactItemInterface> List;
					if(isChina){
						List = contactList;
					}else{
						List = foreginList;
					}
					CityAdapter adapter = new CityAdapter(context_,R.layout.city_item, List);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}

		}
	}

	
	
//	/**
//	 * 初始化定?
//	 */
//	private void initLocation() {
//
//		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
//
//		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请?
//		// 在定位结束后，在合适的生命周期调用destroy()方法
//		// 其中如果间隔时间??1，则定位只定一?
//		mLocationManagerProxy.requestLocationData(
//				LocationProviderProxy.AMapNetwork, -1, 15, this);
//
//		mLocationManagerProxy.setGpsEnable(false);
//	}

	/**
	 * 定位成功后回调函?
	 */
//	@Override
//	public void onLocationChanged(AMapLocation amapLocation) {
//		if (amapLocation != null
//				&& amapLocation.getAMapException().getErrorCode() == 0) {
//			// 获取位置信息
//			Double geoLat = amapLocation.getLatitude();
//			Double geoLng = amapLocation.getLongitude();
//
//			locationCity = amapLocation.getCity();
//			city_name.setText(locationCity);
//			String desc = "";
//			Bundle locBundle = amapLocation.getExtras();
//			if (locBundle != null) {
//				desc = locBundle.getString("desc");
//			}
//
//			if (null != locationCity
//					&& !locationCity.equals("")) {
//				city_name.setClickable(true);
//				flog=true;
//			}
//		}else{
//			city_name.setText("定位失败");
//			flog=false;
//		}
//	}

//	private void stopLocation() {
//		if (mLocationManagerProxy != null) {
//			mLocationManagerProxy.removeUpdates(this);
//			mLocationManagerProxy.destory();
//		}
//		mLocationManagerProxy = null;
//	}

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
//		case R.id.route_mude_location:
//			if(flog){
//			text_content.setText(locationCity);
//			intent.putExtra("themeCity", locationCity);
//			SharedPreferenceUtil.getInstance(ctx).setString("city", locationCity);
//			setResult(RESULT_OK, intent);
//			finish();
//			}
//			break;
		default:
			break;
		}
	}

//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}
	
	class HomeCityAdapter extends BaseAdapter {
		private Context context;
		private List<String> list;

		public HomeCityAdapter(Context context, List<String> list) {
			super();
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.home_city_griditem, null);
				holder.meta = (Button) view
						.findViewById(R.id.city_item);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.meta.setText(list.get(position).toString());
			holder.meta.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
	 				text_content.setText(list.get(position).toString());
	 				intent.putExtra("themeCity", list.get(position).toString());
	 				if(isChina){
						SharedPreferenceUtil.getInstance(ctx).setString("city", list.get(position).toString());
					}else{
						SharedPreferenceUtil.getInstance(ctx).setString("city1", list.get(position).toString());
					}
	 				setResult(RESULT_OK, intent);
	 				finish();
				}
			});
			
			return view;
		}

		class ViewHolder {
			Button meta;
		}
	}
}
