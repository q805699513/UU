package com.uugty.uu.friendstask;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.EmojiEdite;


public class CurrentPosActivity extends BaseActivity implements
		AMapLocationListener, OnClickListener, OnPoiSearchListener {
	private EmojiEdite nearby_city_edit;
	private TextView nearby_sure_text;
	private String locationCity = "";
	private ImageView location_delete_hint_img, location_search_hint_img;
	private ListView nearby_city_list;
	private NearbyAdapter adapter;
	// 定位
	private LocationManagerProxy mLocationManagerProxy;
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private LatLonPoint lp;
	private PoiSearch poiSearch;
	private List<PoiItem> poiItems;// poi数据
	private Double geoLat, geoLng;
	private boolean hav=true;//搜索框是否有输入
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.currentpostion_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		initLocation();
		location_delete_hint_img = (ImageView) findViewById(R.id.location_delete_hint_img);
		location_search_hint_img = (ImageView) findViewById(R.id.location_search_hint_img);
		nearby_city_edit = (EmojiEdite) findViewById(R.id.nearby_city_edit);
		nearby_sure_text = (TextView) findViewById(R.id.nearby_sure_text);
		nearby_city_list = (ListView) findViewById(R.id.nearby_city_list);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		location_delete_hint_img.setOnClickListener(this);
		nearby_sure_text.setOnClickListener(this);
		nearby_city_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (nearby_city_edit.getText().toString().equals("")) {
					nearby_sure_text.setText("取消");
					location_delete_hint_img.setVisibility(View.GONE);
					location_search_hint_img.setVisibility(View.VISIBLE);
					doSearchQuery("", "", locationCity);
				} else {
					nearby_sure_text.setText("确定");
					location_delete_hint_img.setVisibility(View.VISIBLE);
					location_search_hint_img.setVisibility(View.GONE);
					doSearchQuery(nearby_city_edit.getText().toString(), "",
							locationCity);
				}
				nearby_city_edit.setSelection(nearby_city_edit.getText().toString().length());
			}
		});
		nearby_city_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (poiItems != null && poiItems.size() > 0) {
					nearby_city_edit.setText(poiItems.get(position).getTitle());
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.nearby_sure_text:
			if (nearby_sure_text.getText().toString().equals("取消")) {
				intent.putExtra("pos", "显示地理位置");
			} else {
				if(!hav){
					locationCity="";
				}
				intent.putExtra("pos", locationCity+nearby_city_edit.getText().toString());
			}
			ctx.setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.location_delete_hint_img:
			nearby_city_edit.setText("");
			break;
		}
	}

	/**
	 * 初始化定?
	 */
	private void initLocation() {

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请?
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间??1，则定位只定一?
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		mLocationManagerProxy.setGpsEnable(false);
	}

	/**
	 * 定位成功后回调函?
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 获取位置信息
			geoLat = amapLocation.getLatitude();
			geoLng = amapLocation.getLongitude();
			lp = new LatLonPoint(geoLat, geoLng);
			locationCity = amapLocation.getCity();
			String desc = "";
			Bundle locBundle = amapLocation.getExtras();
			if (locBundle != null) {
				desc = locBundle.getString("desc");
			}
			doSearchQuery("", "", locationCity);
		}
	}

	private void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destory();
		}
		mLocationManagerProxy = null;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopLocation();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

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

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(String key, String type, String city) {
		currentPage = 0;
		query = new PoiSearch.Query(key, type, city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		if (lp != null) {
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(lp, 3000, true));//
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	@Override
	public void onPoiItemSearched(PoiItem arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * POI搜索回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始				
					if (poiItems != null && poiItems.size() > 0) {
						hav=true;
						nearby_city_list.setVisibility(View.VISIBLE);
						adapter = new NearbyAdapter(ctx, poiItems);
						nearby_city_list.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} else {
						nearby_city_list.setVisibility(View.GONE);
						hav=false;
					}
				}
			}
		}
	}
	class NearbyAdapter extends BaseAdapter {
		private Context context;
		private List<PoiItem> ls;

		public NearbyAdapter(Context context, List<PoiItem> ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ls.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.nearby_city_list_item, null);
				holder.scenic_name = (TextView) view
						.findViewById(R.id.nearby_city_list_item_name);
				holder.poi_distance = (TextView) view
						.findViewById(R.id.poi_distance);
				holder.nearby_city_list_item_type_city = (TextView) view
						.findViewById(R.id.nearby_city_list_item_type_city);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.poi_distance.setVisibility(View.GONE);
			holder.scenic_name.setText(ls.get(position).getTitle());
			holder.nearby_city_list_item_type_city.setText(ls.get(position).getCityName()+ls.get(position).getBusinessArea());
			return view;
		}

		class ViewHolder {
			private TextView scenic_name, poi_distance,nearby_city_list_item_type_city;
		}
	}
}
