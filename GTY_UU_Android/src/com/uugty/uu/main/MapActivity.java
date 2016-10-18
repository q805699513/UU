package com.uugty.uu.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.google.gson.Gson;
import com.uugty.uu.R;
import com.uugty.uu.appstart.Constant;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.MapCirculImage;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.MapUserEntity;
import com.uugty.uu.entity.MapUserEntity.MapUser;
import com.uugty.uu.entity.MarkerEntity;
import com.uugty.uu.util.MathUtil;
import com.uugty.uu.uuchat.ChatActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class MapActivity extends Activity implements AMapLocationListener
    ,OnMapLoadedListener,LocationSource,OnCameraChangeListener,InfoWindowAdapter
    ,OnMarkerClickListener,OnInfoWindowClickListener{

	private TopBackView titleView;
	private MapView mapView;// 一个显示地图的视图
	// 定位按钮
	private Button locationBtn;
	// 隐身按钮
	private Button visiableBtn;
	private Button invisiableBtn;
	private LocationManagerProxy proxy;
	private OnLocationChangedListener mListener;
	private String resultContent = "";
	// 我的位置
	private static LocationEntity locaEntity;
	private AMap aMap; // 定义AMap 地图对象的操作方法与接口
	private boolean moveFlag = false;
	private boolean markClick = false;
	private boolean flag = true;
	private boolean first = true;
	// 当前定位的经纬度
	private LatLng mYLatng = null;
	private float oraginLayer;
	private final float distance = 0.38f;// 加载范围，单位为公里,此处为相对屏幕比例
	private String mDistance;//距离字符串
	private Iterator<String> i;
	// 定义一个数组，放入3个附近经纬度
	private Map<String, MarkerEntity> MarkMap = new HashMap<String, MarkerEntity>();
	private View view = null;
	String key1 = null;
	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
			if (flag) {
				if (moveFlag) {
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				}
			} else {
				flag = true;
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				try {
					key1 = (String) i.next();
				} catch (Exception e) {
					i = MarkMap.keySet().iterator();
					if (MarkMap.keySet() == null || MarkMap.keySet().isEmpty())
						break;
					key1 = (String) i.next();
				}
				if (key1 == null) {
					i = MarkMap.keySet().iterator();
					key1 = (String) i.next();
				}
				MarkMap.get(key1).getMarker().showInfoWindow();

				break;
			}
			super.handleMessage(msg);
		}

	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_3);
		titleView = (TopBackView) findViewById(R.id.map_title_view);
		titleView.setTitle("地图");
		mapView = (MapView) findViewById(R.id.location_map);
		locationBtn = (Button) findViewById(R.id.map_person_location);
		visiableBtn = (Button) findViewById(R.id.map_person_visibile);
		invisiableBtn = (Button) findViewById(R.id.map_person_invisibile);

		// 根据登录返回的userIsLogin判断上次退出状态
		if (null!=MyApplication.getInstance().getUserInfo()&&MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserIsLogin().equals("1")) {
			visiableBtn.setVisibility(View.VISIBLE);
			invisiableBtn.setVisibility(View.GONE);
		} else {
			visiableBtn.setVisibility(View.GONE);
			invisiableBtn.setVisibility(View.VISIBLE);
		}
		mapView.onCreate(savedInstanceState);
		initLocation();
		init();
		initMark();
		view = LayoutInflater.from(this).inflate(
				R.layout.infowindow_content, null);
		initAction();
	};

	

	protected void initAction() {
		// TODO Auto-generated method stub
		visiableBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				visiableBtn.setVisibility(View.GONE);
				invisiableBtn.setVisibility(View.VISIBLE);
				perVisiable("-1");
				if (MarkMap.size() > 0) {
					MarkMap.get(key1).getMarker().hideInfoWindow();
					MarkMap.clear();
					aMap.clear();
				}

			}
		});

		invisiableBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				visiableBtn.setVisibility(View.VISIBLE);
				invisiableBtn.setVisibility(View.GONE);
				perVisiable("1");
			}
		});
		locationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (locaEntity != null) {
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(
							Double.valueOf(locaEntity.userLastLoginLat), Double
									.valueOf(locaEntity.userLastLoginLng))));
					aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
					loadMapMark();
				}
			}
		});
	}

	
	
	private void initLocation() {
		proxy = LocationManagerProxy.getInstance(this);

		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请�?
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间�?1，则定位只定一�?
		proxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 30,
				this);
		proxy.setGpsEnable(false);

	}
	
	// 初始化aMap对象
		private void init() {

			if (aMap == null) {
				aMap = mapView.getMap();
				setUpMap();
			}
		}
		
		// aMap的属�?
		private void setUpMap() {

			// TODO Auto-generated method stub
			// 自定义系统定位小蓝点
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
			myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
			myLocationStyle.strokeWidth(0.0F);
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.brand_tag_point_white_bg));
			// MapCirculImage headImage = new MapCirculImage(getActivity());
			// headImage.setPic(R.drawable.top_head);
			// myLocationStyle.myLocationIcon(BitmapDescriptorFactory
			// .fromView(headImage));// 设置小蓝点的图标
			// myLocationStyle.anchor(0.9f, 0.9f);
			/*
			 * myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜�?
			 * myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));//
			 * 设置圆形的填充颜�?
			 */// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
				// myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗�?
			aMap.setMyLocationStyle(myLocationStyle);

			aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺功能是否可�?
			aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
			aMap.getUiSettings().setScrollGesturesEnabled(true);// 是否允许通过手势来移�?
			aMap.getUiSettings().setZoomGesturesEnabled(true);// 是否允许通过手势来缩�?
			aMap.getUiSettings().setZoomControlsEnabled(false);
			aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
			aMap.setOnMapLoadedListener(this);

			aMap.setLocationSource(this);// 设置定位监听
			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听�?
			aMap.setInfoWindowAdapter(this);
			// aMap.moveCamera(CameraUpdateFactory.zoomTo(5));

		}

		/**
		 * 初始化Marker
		 */
		private void initMark() {
			aMap.setOnMarkerClickListener(this);
			aMap.setOnInfoWindowClickListener(this);
			/*
			 * aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听�?调用 onMapLoaded()
			 * aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听�?//
			 * 调用onMarkerClick(Marker marker)
			 * aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听�?//
			 * 调用onInfoWindowClick(Marker // marker)
			 * aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式 //
			 * getInfoContents(Marker // marker)提供了一个给默认信息窗口定制内容的方法�?//
			 * getInfoWindow(Marker marker)提供了一个个性化定制信息窗口的marker 对象�?
			 */
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

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub
		if (arg0 != null && arg0.getAMapException().getErrorCode() == 0) {
			if (mListener != null) {
				mListener.onLocationChanged(arg0);// 显示系统小蓝�?
				locaEntity = new LocationEntity(String.valueOf(arg0
						.getLatitude()), String.valueOf(arg0.getLongitude()));
				RequestParams params = new RequestParams();
				params.put("type", "13");
				params.put("content", new Gson().toJson(locaEntity));
				APPRestClient.post(this, ServiceCode.USER_INFO,
						params, new APPResponseHandler<BaseEntity>(
								BaseEntity.class, MapActivity.this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(MapActivity.this, 0, errorMsg, 300).show();

							}

							@Override
							public void onFinish() {

							}
						});
			}

		}
	}

	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	private void stopLocation() {
		if (proxy != null) {
			proxy.removeUpdates(this);
			proxy.destory();
		}
		proxy = null;
	}
	
	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub

		super.onPause();
		stopLocation();
		mapView.onPause();
		APPRestClient.cancelRequests(this);
	}
	
	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.schedule(task, 1000 * 2, 1000 * 1);
		}
	}
	
	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		mListener = arg0;
		if (proxy == null) {
			if (null != this)
				proxy = LocationManagerProxy.getInstance(this);

		}
		aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (proxy != null) {
			proxy.removeUpdates(this);
			proxy.destory();
		}
		proxy = null;
	}
	
	@Override
	public void onCameraChange(CameraPosition pos) {
		// TODO Auto-generated method stub
		moveFlag = false;
		// TODO Auto-generated method stub
		// Toast.makeText(this, "change", 1).show();
		if (!markClick) {
			Set<String> set = MarkMap.keySet();
			for (String key : set) {
				if (MarkMap.get(key).getMarker().isInfoWindowShown()) {
					MarkMap.get(key).getMarker().hideInfoWindow();
				}
			}
		} else {
			markClick = false;
		}
	}

	@Override
	public void onCameraChangeFinish(CameraPosition pos) {
		// TODO Auto-generated method stub
		if (pos.zoom > 20) {
			aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
		}

		APPRestClient.cancelRequests(this);

		if (first
				|| mYLatng == null
				|| oraginLayer != pos.zoom
				|| (pos.target != null && 2 * 6378.137 * asin(sqrt(pow(
						sin(0.008726646259971648 * (mYLatng.latitude - pos.target.latitude)),
						2)
						+ cos(0.17453292519943296 * mYLatng.latitude)
						* cos(0.17453292519943296 * pos.target.latitude)
						* pow(sin(0.008726646259971648 * (mYLatng.longitude - pos.target.longitude)),
								2))) > distance * aMap.getScalePerPixel() * 1.5)) {
			oraginLayer = pos.zoom;
			moveFlag = false;
			mYLatng = pos.target;
			loadMapMark();

		}
		moveFlag = true;
	}
	
	private void loadMapMark() {
		if (MarkMap.size() == 1) {
			MarkMap.clear();
			aMap.clear();
		}
		RequestParams params = new RequestParams();

		params.add("userLastLoginLng", mYLatng.longitude + ""); // 经度
		params.add("userLastLoginLat", mYLatng.latitude + ""); // 纬度
		if("NaN".equals(distance * aMap.getScalePerPixel() + "")){
			mDistance = "0";
		}else{
			mDistance = distance * aMap.getScalePerPixel() + "";
		}
		params.add("distance", mDistance);
		params.add("size", 30 + "");

		APPRestClient.post(this, ServiceCode.MAP_USER_LOCARION,
				params, new APPResponseHandler<MapUserEntity>(
						MapUserEntity.class, MapActivity.this) {
					@Override
					public void onSuccess(MapUserEntity result) {
						if (first) {
							first = false;
						}
						Set<String> set = MarkMap.keySet();
						if (set.size() > 0) {
							for (String key : set) {
								if (MarkMap.get(key) != null
										&& MarkMap.get(key).getMarker() != null)
									MarkMap.get(key).getMarker().destroy();
							}
						}
						MarkMap.clear();
						// 瑁呴厤鏁版嵁锛岄渶瑕佷粠缃戠粶鎷挎暟锟�
						if (result.getLIST() != null
								&& result.getLIST().size() > 0) {
							LatLng ull = null;
							for (int i = 0; i < result.getLIST().size(); i++) {
								MapUser mapUser = result.getLIST().get(i);
								LatLng ull1 = new LatLng(Double.valueOf(mapUser
										.getUserLastLoginLat()), Double
										.valueOf(mapUser.getUserLastLoginLng()));
								if (ull != null
										&& Math.abs(ull1.latitude
												- ull.latitude)
												+ Math.abs(ull.longitude
														- ull1.longitude) < 0.0000001 * aMap
												.getScalePerPixel()) {
									double ull1lat = Double.valueOf(mapUser
											.getUserLastLoginLat())
											+ MathUtil.gapRandom()
											/ 500000
											* aMap.getScalePerPixel();
									ull1 = new LatLng(
											ull1lat,
											Double.valueOf(0.0000001
													* aMap.getScalePerPixel()
													- (ull1.latitude - ull.latitude)
													+ Math.abs(ull.longitude
															- ull1.longitude)
													- Double.valueOf(mapUser
															.getUserLastLoginLng())));
								}
								ull = ull1;
								MarkMap.put(
										"map" + i,
										new MarkerEntity(ull1, mapUser
												.getTempShout()
												+ ","
												+ mapUser.getUuid()
												+ ","
												+ mapUser.getUserId(), mapUser
												.getUserName(), mapUser
												.getUserAvatar(), mapUser
												.getUserStatus()));
							}
						}
						// 缁撴潫瑁呴厤
						set = MarkMap.keySet();
						for (final String key : set) {
							addMarkerToMap(MarkMap.get(key).getLatLng(), key,
									MarkMap.get(key).getUserName(), MarkMap
											.get(key).getText(),
									MarkMap.get(key).getUserHead(), MarkMap
											.get(key).getUserStatus());
							/*final Marker m = MarkMap.get(key).getMarker();
							
							String userStatus = MarkMap.get(key)
									.getUserStatus();
							if (null != MarkMap.get(key).getUserHead()
									&& !MarkMap.get(key).getUserHead()
											.equals("")){
								Message msg = new Message();
								Bundle b = new Bundle();
								msg.obj = m;
								b.putString("path", MarkMap.get(key).getUserHead());
								b.putString("status", userStatus);
								msg.setData(b);
								msg.what = 2;
								mHandler.sendMessage(msg);
							}else {
								// 默认图像
								Message msg = new Message();
								Bundle b = new Bundle();
								msg.obj = m;
								b.putString("status", userStatus);
								msg.setData(b);
								msg.what = 1;
								mHandler.sendMessage(msg);
							}*/
						}

						i = MarkMap.keySet().iterator();

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(MapActivity.this, 0, errorMsg, 300).show();
					}

					@Override
					public void onFinish() {

					}
				});
	}
	
	/**
	 * 打标�?mark
	 */
	private void addMarkerToMap(LatLng latlng, String key, String title,
			String voice, String userHead, String userStatus) {
		if (this != null) {
			MarkerOptions markerOption = new MarkerOptions();
			MapCirculImage headImage = new MapCirculImage(this);
			if (userStatus.equals("2")) {
				headImage.setBackPic("drawable://" + R.drawable.map_guide_bg);
			}
			if(!TextUtils.isEmpty(userHead)){
				headImage.setAvatar(userHead,"net");
			}else{
				headImage.setAvatar("","drwable");
			}
			
			markerOption.position(latlng);// 设置经纬�?

			// markerOption.anchor(0.5f, 0.5f);// 定义marker 图标的锚点。锚点是marker
			// 图标接触地图平面的点。图标的左顶点为�?,0）点，右底点为（1,1）点�?
			markerOption.title(title);
			markerOption.snippet(voice + "," + userHead);
			markerOption.icon(BitmapDescriptorFactory.fromView(headImage));// 锚点显示图标资源文件
			// markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.user));//
			// 锚点显示图标资源文件
			// markerOption.icon(BitmapDescriptorFactory.fromBitmap(addbackground4onlyicon(bmp1)));
			// aMap.addMarker(markerOption).showInfoWindow();
			Marker marker = aMap.addMarker(markerOption);

			MarkMap.get(key).setMarker(marker);
		}
	}

		Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// 如果该消息是本程序发出的
				if (msg.what == 2) {
					// List<Marker> markList = aMap.getMapScreenMarkers();
					Bundle b = msg.getData();
					String path = b.getString("path");
					String stauts = b.getString("status");
					Marker marker = (Marker) msg.obj;
					if (null != MapActivity.this) {
						MapCirculImage headImage = new MapCirculImage(MapActivity.this);
						if (stauts.equals("2")) {
							headImage.setBackPic("drawable://"
									+ R.drawable.map_guide_bg);
						}
						headImage.setAvatar(path,"net");
						marker.setIcon(BitmapDescriptorFactory.fromView(headImage));// 锚点显示图标资源文件
					}

				}
				if (msg.what == 1) {
					// List<Marker> markList = aMap.getMapScreenMarkers();
					Bundle b = msg.getData();
					String stauts = b.getString("status");
					Marker marker = (Marker) msg.obj;
					if (null != MapActivity.this) {
						MapCirculImage headImage = new MapCirculImage(MapActivity.this);
						if (stauts.equals("2")) {
							headImage.setBackPic("drawable://"
									+ R.drawable.map_guide_bg);
						}
						headImage.setAvatar("","drwable");
						marker.setIcon(BitmapDescriptorFactory.fromView(headImage));// 锚点显示图标资源文件
					}

				}
			}
		};
		
		@Override
		public View getInfoContents(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			customInfoContentsView(arg0, view);
			return view;
		}
		
		/**
		 * 自定义弹出框控件初始�?
		 * 
		 * @param marker
		 * @param view
		 */
		private void customInfoContentsView(Marker marker, View view) {
			TextView userText = (TextView) view
					.findViewById(R.id.infowindow_content_user);
			TextView voiceText = (TextView) view
					.findViewById(R.id.infowindow_content_voice);
			String userTitle = marker.getTitle();
			String voiceTitle = marker.getSnippet();
			String[] str = voiceTitle.split(",");
			if (userTitle != null && !userTitle.equals("")) {
				userText.setText(userTitle);
			} else {
				userText.setText("游客");
			}
			if (str[0] != null && !str[0].equals("")) {
				voiceText.setText(str[0]);
			} else {
				voiceText.setText("旅游心愿");
			}
		}
		
		@Override
		public boolean onMarkerClick(Marker arg0) {
			// 点击Mark
			flag = false;
			arg0.showInfoWindow();
			markClick = true;
			return false;
		}
		
		@Override
		public void onInfoWindowClick(Marker arg0) {
			// TODO Auto-generated method stub
			// 检查这个mark是不是你
			String voiceTitle = arg0.getSnippet();
			// 此处有bug,如果输入中带有","。会有问题
			String[] str = voiceTitle.split(",");
			if (MyApplication.getInstance().isLogin()) {
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(str[2])) {
					if (!resultContent.equals("")) {
						arg0.setSnippet(resultContent + "," + str[1] + "," + str[2]);
					}
					showMyDialog();
				} else {
					Intent intent = new Intent();
					intent.putExtra("userId", str[2]);
					if(null!=str[3]&&!str[3].equals("")){
						intent.putExtra("avatar", str[3]);
					}else{
						intent.putExtra("avatar", "");
					}
					intent.putExtra("userName", arg0.getTitle());
					intent.setClass(this, ChatActivity.class);
					startActivity(intent);
				}
			}
		}
		
		// dialog
		public boolean showMyDialog() {

			final Dialog dialog = new Dialog(this,
					R.style.myDialogActivity);
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.map_shout, null);
			dialog.setContentView(view);
			dialog.show();

			EditText content = (EditText) view.findViewById(R.id.shout_edit);
			final ImageView confirmBtn = (ImageView) view
					.findViewById(R.id.shout_edit_confirm);
			confirmBtn.setEnabled(false);
			// final AlertDialog.Builder builder = new
			// Builder(getActivity()).setView(view);

			// builder.create();
			content.addTextChangedListener(new TextWatcher() {

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
					if (s != null) {
						confirmBtn
								.setImageResource(R.drawable.map_shout_confirm_btn);
						confirmBtn.setEnabled(true);
						resultContent = s.toString().trim();
					}

				}
			});
			confirmBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					RequestParams params = new RequestParams();
					params.add("tempUUID", Constant.UUID);
					params.add("tempShout", resultContent);
					APPRestClient.post(MapActivity.this,
							ServiceCode.MAP_USER_TEMP_LOGIN, params,
							new APPResponseHandler<BaseEntity>(BaseEntity.class,
									MapActivity.this) {
								@Override
								public void onSuccess(BaseEntity result) {
									if (MarkMap != null) {

										aMap.getMapScreenMarkers().get(0)
												.setSnippet(resultContent);

									}
								}

								@Override
								public void onFailure(int errorCode, String errorMsg) {
									CustomToast.makeText(MapActivity.this, 0, errorMsg, 300).show();

								}

								@Override
								public void onFinish() {

									dialog.dismiss();

								}
							});

				}
			});
			if (!resultContent.equals(""))
				return true;
			return false;
		}
		
		private void perVisiable(final String userIsLogin) {
			RequestParams params = new RequestParams();
			params.put("type", "14");
			params.put("content", userIsLogin);// 1.在线 -1隐身


			APPRestClient.post(this, ServiceCode.USER_INFO, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class,
							MapActivity.this) {
						@Override
						public void onSuccess(BaseEntity result) {
							if(userIsLogin.equals("1")){
								MyApplication.getInstance().getUserInfo().getOBJECT().setUserIsLogin("1");
							}else{
								MyApplication.getInstance().getUserInfo().getOBJECT().setUserIsLogin("-1");
							}
							loadMapMark();
							//mapLayout.performClick();
							/*
							 * MotionEvent
							 * me=MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN
							 * ,50,50,0); view.dispatchTouchEvent(me);
							 */
							/*
							 * LatLng lng = aMap.getCameraPosition().target;
							 * aMap.moveCamera(CameraUpdateFactory.changeLatLng(new
							 * LatLng
							 * (lng.longitude-0.000000000001,lng.latitude-0.000000000001
							 * )));
							 */
							/*
							 * float mZoom = aMap.getCameraPosition().zoom;
							 * System.out.println("mZoom:"+mZoom);
							 * aMap.moveCamera(CameraUpdateFactory.zoomTo(mZoom));
							 */
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							CustomToast.makeText(MapActivity.this, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(MapActivity.this)
										.setTitle("提示")
										.setMessage("网络拥堵,请稍后重试！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.dismiss();
													}
												}).show();
							}
						}

						@Override
						public void onFinish() {

						}
					});
		}
		
		@Override
		public void onDestroy() {
			/*
			 * task.cancel(); timer.purge(); timer.cancel(); if (pool != null)
			 * pool.shutdownNow(); Set<String> set = MarkMap.keySet(); if
			 * (set.size() > 0) { for (String key : set) { if (MarkMap.get(key) !=
			 * null && MarkMap.get(key).getMarker() != null)
			 * MarkMap.get(key).getMarker().destroy(); } }
			 */
			MarkMap.clear();
			mapView.onDestroy();

			task.cancel();
			timer.purge();
			timer.cancel();
			super.onDestroy();
		}

	public class LocationEntity {
		private String userLastLoginLat;
		private String userLastLoginLng;

		public LocationEntity(String lat, String lng) {
			this.userLastLoginLat = lat;
			this.userLastLoginLng = lng;
		}

		public String getUserLastLoginLat() {
			return userLastLoginLat;
		}

		public void setUserLastLoginLat(String userLastLoginLat) {
			this.userLastLoginLat = userLastLoginLat;
		}

		public String getUserLastLoginLng() {
			return userLastLoginLng;
		}

		public void setUserLastLoginLng(String userLastLoginLng) {
			this.userLastLoginLng = userLastLoginLng;
		}

	}

}
