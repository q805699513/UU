package com.uugty.uu.map;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;

public class LocationActivity extends BaseActivity implements
		AMapLocationListener, OnScrollListener, OnClickListener {

	private TextView locationCity, locationBtn;
	private EmojiEdite locationAddressEdit;
	private String city;
	private ListView locationList;
	private LatLonPoint locationLat; // 当前位置坐标
	// 定位
	private LocationManagerProxy mLocationManagerProxy;
	private PoiSearch.Query query;// Poi查询条件类
	private int currentPage = 0;
	private List<PoiItem> locationPois = new ArrayList<PoiItem>();
	private LocationAdapter adapter;
	private boolean nearFirst = true;// 第一次调用关键字搜索
	private boolean keywordFirst = true;// 第一次调用关键字搜索

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_location;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			city = getIntent().getStringExtra("address");
		}
		initLocation();
		locationCity = (TextView) findViewById(R.id.location_city_text);
		locationAddressEdit = (EmojiEdite) findViewById(R.id.loation_address_edit);
		locationBtn = (TextView) findViewById(R.id.location_btn);
		locationList = (ListView) findViewById(R.id.location_list);

	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		locationBtn.setOnClickListener(this);
		locationList.setOnScrollListener(this);
		locationAddressEdit.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s.toString())) {
					locationBtn.setText("确定");
					// 关键字搜索
					currentPage = 0;
					searchKeyWordPoi(s.toString());
//					locationAddressEdit.setSelection(s.length());
				} else {
					if (null != locationLat) {
						locationBtn.setText("取消");
						currentPage = 0;
						searchNearPoi();
					}
				}
			}
		});

		locationList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				locationAddressEdit.setText(locationPois.get(position)
						.getCityName() + locationPois.get(position).getTitle());
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		locationAddressEdit.setText(city);
	}

	@Override
	public void onNoDoubleClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		super.onNoDoubleClick(view);
		switch (view.getId()) {
		case R.id.location_btn:
			if (locationBtn.getText().toString().trim().equals("取消")) {
				finish();
			} else {
				// 给值
				intent.putExtra("address", locationAddressEdit.getText()
						.toString().trim());
				setResult(RESULT_OK, intent);
				finish();
			}
			break;
		default:
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
			locationCity.setText(amapLocation.getCity());
			locationLat = new LatLonPoint(amapLocation.getLatitude(),
					amapLocation.getLongitude());
			if (TextUtils.isEmpty(locationAddressEdit.getText().toString()
					.trim()))
				searchNearPoi();
		} else {
			locationCity.setText("定位失败");
			CustomToast.makeText(LocationActivity.this, "定位失败,请手动输入地址", 500)
					.show();
		}
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

	// 关键字搜索
	private void searchKeyWordPoi(String keyWord) {
		query = new PoiSearch.Query(keyWord, "餐饮服务|购物服务|生活服务|体育休闲服务|"
				+ "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|" + "地名地址信息|公共设施",
				locationCity.getText().toString().trim());
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页
		PoiSearch poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {

			@Override
			public void onPoiSearched(PoiResult arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == 0) {// 搜索成功

					if (null != locationPois && locationPois.size() > 0) {
						if (keywordFirst) {
							locationPois.clear();
							locationPois = arg0.getPois();
							adapter = new LocationAdapter(ctx, locationPois,
									locationLat.getLatitude(), locationLat
											.getLongitude());
							locationList.setAdapter(adapter);
						} else {
							locationPois.addAll(arg0.getPois());
							adapter.notifyDataSetChanged();
						}

					} else {
						locationPois = arg0.getPois();
						adapter = new LocationAdapter(ctx, locationPois,
								locationLat.getLatitude(), locationLat
										.getLongitude());
						locationList.setAdapter(adapter);
					}
				}

			}

			@Override
			public void onPoiItemSearched(PoiItem arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});// 设置数据返回的监听器
		poiSearch.searchPOIAsyn();// 开始搜索
		currentPage++;
		nearFirst = true;

	}

	// keyWord为空,附近搜索，否则关键字搜索
	private void searchNearPoi() {
		query = new PoiSearch.Query("", "餐饮服务|购物服务|生活服务|体育休闲服务|"
				+ "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|" + "地名地址信息|公共设施",
				locationCity.getText().toString().trim());
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页
		PoiSearch poiSearch = new PoiSearch(this, query);
		poiSearch.setBound(new SearchBound(locationLat, 1000));// 设置周边搜索的中心点以及区域
		poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {

			@Override
			public void onPoiSearched(PoiResult arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == 0) {// 搜索成功
					if (null != locationPois && locationPois.size() > 0) {
						if (nearFirst) {
							locationPois.clear();
							locationPois = arg0.getPois();
							adapter = new LocationAdapter(ctx, locationPois,
									locationLat.getLatitude(), locationLat
											.getLongitude());
							locationList.setAdapter(adapter);
						} else {
							locationPois.addAll(arg0.getPois());
							adapter.notifyDataSetChanged();
						}
					} else {
						locationPois = arg0.getPois();
						adapter = new LocationAdapter(ctx, locationPois,
								locationLat.getLatitude(), locationLat
										.getLongitude());
						locationList.setAdapter(adapter);
					}

				}
			}

			@Override
			public void onPoiItemSearched(PoiItem arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});// 设置数据返回的监听器
		poiSearch.searchPOIAsyn();// 开始搜索
		currentPage++;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (currentPage > 0) {
			if (firstVisibleItem == (currentPage) * 5) {
				if (TextUtils.isEmpty(locationAddressEdit.getText().toString()
						.trim())) {
					nearFirst = false;
					searchNearPoi();
				} else {
					keywordFirst = false;
					searchKeyWordPoi(locationAddressEdit.getText().toString()
							.trim());
				}
			}

		}
	}

}

class LocationAdapter extends BaseAdapter {
	private Context context;
	private List<PoiItem> ls;
	private LatLng startLatlng;

	public LocationAdapter(Context context, List<PoiItem> ls, double geoLat,
			double geoLng) {
		super();
		this.context = context;
		this.ls = ls;
		startLatlng = new LatLng(geoLat, geoLng);
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
			holder.typeText = (TextView) view
					.findViewById(R.id.nearby_city_list_item_type_city);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		view.setBackgroundResource(R.drawable.list_item_bg);
		holder.scenic_name.setText(ls.get(position).getTitle());
		holder.typeText.setText(ls.get(position).getCityName() + "."
				+ ls.get(position).getTypeDes() + "."
				+ ls.get(position).getSnippet());
		LatLng endLatlng = new LatLng(ls.get(position).getLatLonPoint()
				.getLatitude(), ls.get(position).getLatLonPoint()
				.getLongitude());
		// 计算量坐标点距离
		int distance = Math.round(AMapUtils.calculateLineDistance(startLatlng,
				endLatlng));
		float distancef = (float) (distance / 1000.0);
		BigDecimal decimal = new BigDecimal(distancef);
		if (distance > 1000) {
			holder.poi_distance.setText(decimal.setScale(2,
					BigDecimal.ROUND_HALF_UP) + "km");
		} else {
			holder.poi_distance.setText(+distance + "m");
		}
		return view;
	}

	static class ViewHolder {
		private TextView scenic_name, poi_distance, typeText;
	}
}
