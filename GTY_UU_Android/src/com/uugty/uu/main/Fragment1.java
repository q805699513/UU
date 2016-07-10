package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.ScrollViewUITools;
import com.uugty.uu.common.myview.SearchPopuWindow;
import com.uugty.uu.common.myview.SlideShowView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.GuideEntity;
import com.uugty.uu.entity.GuideEntity.GuideDetail;
import com.uugty.uu.entity.HomePageRecommendEntity;
import com.uugty.uu.entity.HomePageRecommendEntity.HomePageRecommend;
import com.uugty.uu.entity.HomeTagEntity;
import com.uugty.uu.entity.HomeTagEntity.Tags.PlayAndBuy;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.util.UUConfig;

import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
		OnScrollListener, OnClickListener {
	private TextView location_text, search_text;
	private View rootView;// 缓存Fragment view
	// private ListView lv;
	private List<GuideDetail> homePageList = new ArrayList<GuideDetail>();
	private FragmentOneListViewAdapter adapter;
	private Context context;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeLayout;
	private int startId = 1;// 起始页页
	private int count = 1;// 点击次数
	private static final int INITIAL_DELAY_MILLIS = 500;
	private RelativeLayout location_linear;
	private SlideShowView homeSlideShowView ;
	private List<HomePageRecommend> recommendList;
	private List<PlayAndBuy> mPlayList;
	private List<PlayAndBuy> mBuyList;
	private LinearLayout horizontalLin, release_lin, fatherLin,mHomePlayLin,mHomeBuyLin;
	private String themeCity = "北京";
	private String cityType = "1";//1是国内 2是国外
	private HorizontalScrollView horizontalScrollView;
	private HorizontalScrollView mHomePlayScrollView;
	private HorizontalScrollView mHomeBuyScrollView;
	private LinearLayout mHomeHeadLayout;//推荐景区去处
	private float y = 0;
	private TextView footMoreText,headMoreText;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
					"homePageEntity");
			if (entity != null) {
				List<GuideDetail> result = (List<GuideDetail>) entity.getLIST();
				switch (msg.what) {
				case 1:
					homePageList.clear();
					homePageList.addAll(result);
					mSwipeLayout.setRefreshing(false);
					mListView.setVisibility(View.VISIBLE);
					startId++;
					loadData(2);
					break;
				case 2:
					homePageList.addAll(result);
					break;
				}
				adapter.notifyDataSetChanged();
			} else {
				homePageList.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.context = getActivity();

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_one, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		location_text = (TextView) rootView.findViewById(R.id.location_text);
		location_linear = (RelativeLayout) rootView
				.findViewById(R.id.location_linear);
		fatherLin = (LinearLayout) rootView.findViewById(R.id.fatherLin);
		location_linear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(context, HomeCityListActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
		search_text = (TextView) rootView
				.findViewById(R.id.home_page_search_text);
		release_lin = (LinearLayout) rootView
				.findViewById(R.id.home_page_release_lin);
		search_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				y = dp2px(50);
				TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
						-y);
				animation.setDuration(200);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Intent intent = new Intent();
						intent.setClass(context, SearchPopuWindow.class);
						startActivityForResult(intent, 100);
						getActivity().overridePendingTransition(
								R.anim.group_chat_search_out,
								R.anim.group_chat_search_in);
					}
				});
				fatherLin.startAnimation(animation);

			}
		});

		release_lin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				if(MyApplication.getInstance().isLogin()){
					intent.setClass(context, MapActivity.class);
					startActivity(intent);
				}else{
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//					intent.putExtra("topage", ConsultFragment.class.getName());
					intent.setClass(context, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		mListView = (ListView) rootView.findViewById(R.id.home_page_listview);
		// list header
		View headerView = LayoutInflater.from(context).inflate(
				R.layout.home_page_listview_header_view, null);
		homeSlideShowView = (SlideShowView) headerView
				.findViewById(R.id.home_page_slideview);
		horizontalLin = (LinearLayout) headerView
				.findViewById(R.id.home_page_horizontal_lin);
		mHomeHeadLayout = (LinearLayout) headerView.findViewById(R.id.home_head_layout);
		horizontalScrollView = (HorizontalScrollView) headerView
				.findViewById(R.id.home_page_horizontal_scrollview);
		mHomePlayLin = (LinearLayout) headerView
				.findViewById(R.id.home_page_play_lin);
		mHomePlayScrollView = (HorizontalScrollView) headerView
				.findViewById(R.id.home_page_play_scrollview);
		mHomeBuyLin = (LinearLayout) headerView
				.findViewById(R.id.home_page_buy_lin);
		mHomeBuyScrollView = (HorizontalScrollView) headerView
				.findViewById(R.id.home_page_buy_scrollview);
//		consultationLin = (LinearLayout) headerView
//				.findViewById(R.id.home_page_consultation_lin);
//		customizLin = (LinearLayout) headerView
//				.findViewById(R.id.home_page_customiz_lin);
//		serviceLin = (LinearLayout) headerView
//				.findViewById(R.id.home_page_service_lin);
//		recommendLin = (LinearLayout) headerView
//				.findViewById(R.id.home_page_recommend_lin);
		headMoreText=(TextView) headerView.findViewById(R.id.home_page_horizontal_more_text);
		ScrollViewUITools.elasticPadding(horizontalScrollView, 200);
		ScrollViewUITools.elasticPadding(mHomePlayScrollView, 200);
		ScrollViewUITools.elasticPadding(mHomeBuyScrollView, 200);
		// list footer
		View footView = LayoutInflater.from(context).inflate(
				R.layout.fragment1_foot_view, null);
		footMoreText= (TextView) footView.findViewById(R.id.fragment_foot_view_more);
		footMoreText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("city", themeCity);
				intent.putExtra("content", "goal_title");
				intent.setClass(context, GuideDetailActivity.class);
				startActivity(intent);
			}
		});
		headMoreText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("city", themeCity);
				intent.putExtra("content", "goal_title");
				intent.setClass(context, GuideDetailActivity.class);
				startActivity(intent);
			}
		});
		mListView.addHeaderView(headerView);
		mListView.addFooterView(footView, null, false);
		adapter = new FragmentOneListViewAdapter(getActivity(), homePageList);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(mListView);
		assert swingBottomInAnimationAdapter.getViewAnimator() != null;
		swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
				INITIAL_DELAY_MILLIS);

		mListView.setAdapter(swingBottomInAnimationAdapter);
		// mListView.setAdapter(adapter);
		mSwipeLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipe_container);

		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新

		mSwipeLayout.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(true);
			}
		});
		homeSlideShowView.setSwipeRefreshLayout(mSwipeLayout);

//		consultationLin.setOnClickListener(this);
//		customizLin.setOnClickListener(this);
//		serviceLin.setOnClickListener(this);
//		recommendLin.setOnClickListener(this);

		loadData(1);
		getThemeRecommend();
		getPlayAndBuyTag();

	}

	private void loadData(final int what) {
		UUConfig.INSTANCE.setmCtity(themeCity);//设置城市
		RequestParams params = new RequestParams();
		params.add("roadlineGoalArea", themeCity);
		params.add("areaType",cityType);
		params.add("currentPage", String.valueOf(startId));
		params.add("pageSize", "5");
//		params.add("markSearchType", "goal_title");
//		params.add("markTitle", themeCity); // pageSize
//		params.add("currentPage", String.valueOf(startId)); // 当前页数
//		params.add("pageSize", "5"); // pageSize
//		params.add("isOnline", ""); // 性别
//		params.add("userSex", ""); // 性别
//		params.add("userTourValidate", ""); // 用户的旅游证
//		params.add("userCarValidate", ""); // 用户的车
//
//		params.add("sort", ""); // 排序
//		params.add("markContent", themeCity); // 城市

		APPRestClient.post(getActivity(), ServiceCode.ROAD_LINE_SEARCH_CITY, params,
				new APPResponseHandler<GuideEntity>(GuideEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(GuideEntity result) {
						Message msg = Message.obtain();
						msg.what = what;
						Bundle b = new Bundle();
						b.putSerializable("homePageEntity", result);
						msg.setData(b);
						handler.sendMessage(msg);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						mSwipeLayout.setRefreshing(false);
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ImageLoader.getInstance()
															.clearMemoryCache(); // 清除内存缓存
													MyApplication.getInstance()
															.clearLoginData();
													ActivityCollector
															.finishAll();
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

	private void getThemeRecommend() {

		RequestParams params = new RequestParams();
		params.add("roadlineThemeArea", themeCity); // 地点

		APPRestClient.post(context, ServiceCode.HOME_PAGE_RECOMMEND, params,
				new APPResponseHandler<HomePageRecommendEntity>(
						HomePageRecommendEntity.class, getActivity()) {
					@Override
					public void onSuccess(HomePageRecommendEntity result) {
						if (result.getLIST().size() > 0) {
							if (null != recommendList)
								recommendList.clear();
							horizontalLin.removeAllViews();
							recommendList = result.getLIST();
							homeSlideShowView.setSource(recommendList);
							/*
							 * LayoutParams params = new LayoutParams(
							 * (recommendList.size() - 3) * (dp2px(102)),
							 * dp2px(91));
							 *
							 */
							if(recommendList.size() > 3){
								mHomeHeadLayout.setVisibility(View.VISIBLE);
							}else{
								mHomeHeadLayout.setVisibility(View.GONE);
							}
								for (int i = 3; i < recommendList.size(); i++) {
									View horizeView = LayoutInflater
											.from(context)
											.inflate(
													R.layout.home_page_grideview_item,
													horizontalLin,false);
									SimpleDraweeView horizeImage = (SimpleDraweeView) horizeView
											.findViewById(R.id.home_page_gridview_image);// 拿个这行的icon
																							// 就可以设置图片
									TextView horizeText = (TextView) horizeView
											.findViewById(R.id.home_page_gridview_text);// 拿个这行的icon
																						// 就可以设置图片
									horizeImage.setImageURI(Uri
											.parse(APPRestClient.SERVER_IP
													+ recommendList
															.get(i)
															.getRoadlineThemeImage()));
									horizeText.setText(recommendList.get(i)
											.getRoadlineThemeTitle());
									horizontalLin.addView(horizeView);
									onHorizeClick(horizeView, i);
								}

						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ImageLoader.getInstance()
															.clearMemoryCache(); // 清除内存缓存
													MyApplication.getInstance()
															.clearLoginData();
													ActivityCollector
															.finishAll();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		try {
				intent.putExtra("roadId", homePageList.get(position - 1)
						.getRoadlineId());
				/*Bundle bundle = new Bundle();
				bundle.putString("linenum", homePageList.get(position - 1)
						.getLineNum());
				bundle.putString("markcontent", homePageList.get(position - 1)
						.getMarkContent());
				intent.putExtras(bundle);*/
				intent.setClass(context, FindTestViewPagerActivity.class);
			
			startActivity(intent);
		} catch (Exception e) {
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (y > 0) {
			TranslateAnimation animation = new TranslateAnimation(0, 0, -y, 0);
			animation.setDuration(200);
			animation.setFillAfter(false);
			fatherLin.startAnimation(animation);
			y = 0;
		}

	}

	public void onHorizeClick(View view, final int position) {
		final Intent intent = new Intent();
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent.putExtra("roadlineThemeTitle",
						recommendList.get(position).getRoadlineThemeTitle());
				intent.putExtra("roadlineThemeId", recommendList.get(position)
						.getRoadlineThemeId());
				intent.setClass(context, RecomendActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public void onPlayTagsClick(View view, final int position) {
		final Intent intent = new Intent();
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("city", mPlayList.get(position).getTagName());
				intent.putExtra("content", "mark");
				intent.putExtra("markContent", themeCity);
				intent.setClass(context, GuideDetailActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public void onBuyTagsClick(View view, final int position) {
		final Intent intent = new Intent();
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("city", mBuyList.get(position).getTagName());
				intent.putExtra("content", "mark");
				intent.putExtra("markContent", themeCity);
				intent.setClass(context, GuideDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	/*
	 * 监听器SwipeRefreshLayout.OnRefreshListener中的方法，当下拉刷新后触发
	 */
	public void onRefresh() {
		if (count % 2 > 0) {
			count++;
		}
		startId = 1;
		loadData(1);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		homePageList.clear();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			if (count % 2 > 0) {
				count++;
			}
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		/*
		 * mListView.onScroll(view, firstVisibleItem, visibleItemCount,
		 * totalItemCount);
		 */
		if (startId > 1) {
			if (firstVisibleItem == (startId - 1) * 5) {
				startId++;
				if(startId<3){
					footMoreText.setVisibility(View.GONE);
					loadData(2);
				}else if(startId==2){
					footMoreText.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	
	private void getPlayAndBuyTag() {
		RequestParams params = new RequestParams();
		APPRestClient.post(getActivity(), ServiceCode.TAG_LIST, params,
				new APPResponseHandler<HomeTagEntity>(HomeTagEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(HomeTagEntity result) {
						if(result != null){
							//当地人带你玩
							if(mPlayList != null){
								mPlayList.clear();
							}
							mHomePlayLin.removeAllViews();
							mPlayList = result.getOBJECT().getTypeandTags().get(0).getLIST();
							for (int i = 0; i < mPlayList.size(); i++) {
								View horizeView = LayoutInflater.from(context)
										.inflate(R.layout.home_page_grideview_playandbuy, mHomePlayLin, false);
								SimpleDraweeView horizeImage = (SimpleDraweeView) horizeView
										.findViewById(R.id.home_page_gridview_image);
								TextView horizeText = (TextView) horizeView.findViewById(R.id.home_page_gridview_text);
								horizeImage.setImageURI(
										Uri.parse(APPRestClient.SERVER_IP + mPlayList.get(i).getTagPicture()));
								horizeText.setText(mPlayList.get(i).getTagName());
								mHomePlayLin.addView(horizeView);
								onPlayTagsClick(horizeView, i);
							}
							//当地人代购
							if(mBuyList != null){
								mBuyList.clear();
							}
							mHomeBuyLin.removeAllViews();
							mBuyList = result.getOBJECT().getTypeandTags().get(1).getLIST();
							for (int i = 0; i < mBuyList.size(); i++) {
								View horizeView = LayoutInflater.from(context)
										.inflate(R.layout.home_page_grideview_playandbuy, mHomeBuyLin, false);
								SimpleDraweeView horizeImage = (SimpleDraweeView) horizeView
										.findViewById(R.id.home_page_gridview_image);
								TextView horizeText = (TextView) horizeView.findViewById(R.id.home_page_gridview_text);
								horizeImage.setImageURI(
										Uri.parse(APPRestClient.SERVER_IP + mBuyList.get(i).getTagPicture()));
								horizeText.setText(mBuyList.get(i).getTagName());
								mHomeBuyLin.addView(horizeView);
								onBuyTagsClick(horizeView, i);
							}
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						mSwipeLayout.setRefreshing(false);
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ImageLoader.getInstance()
															.clearMemoryCache(); // 清除内存缓存
													MyApplication.getInstance()
															.clearLoginData();
													ActivityCollector
															.finishAll();
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
	public void onClick(View v) {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			switch (requestCode) {
			case 1000:
				themeCity = data.getStringExtra("themeCity");
				cityType = data.getStringExtra("areaType");
				location_text.setText(themeCity);
				startId=1;
				loadData(1);
				getThemeRecommend();
				// adapter.notifyDataSetChanged();
				mListView.setSelection(0);
				break;
			case 100:
				TranslateAnimation animation = new TranslateAnimation(0, 0, -y,
						0);
				animation.setDuration(200);
				animation.setFillAfter(false);
				fatherLin.startAnimation(animation);
				y = 0;
				break;
			}
		}

	}
}

class FragmentOneListViewAdapter extends ArrayAdapter {
	private List<GuideDetail> ls;
	private Context context;

	// private Animation animation;
	// private Map<Integer, Boolean> isFrist;

	public FragmentOneListViewAdapter(Context context, List<GuideDetail> list) {
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
					R.layout.recommend_lv, null);
			holder.imageView = (SimpleDraweeView) convertView
					.findViewById(R.id.recommend_image);
			holder.addressText = (TextView) convertView
					.findViewById(R.id.recmmend_address_text);
			holder.titleText = (TextView) convertView
					.findViewById(R.id.recmmend_title_text);
			holder.played = (TextView) convertView
					.findViewById(R.id.recmmend_title_played);
			holder.roadTitleText = (TextView) convertView
					.findViewById(R.id.recmmend_road_title_text);
			holder.roadOrderNumText = (TextView) convertView
					.findViewById(R.id.recmmend_road_order_num_text);
			holder.roadPriceText = (TextView) convertView
					.findViewById(R.id.recmmend_road_price_text);
			holder.headImageView = (SimpleDraweeView) convertView
					.findViewById(R.id.recmmend_road_headImage);
			holder.roadRel = (RelativeLayout) convertView
					.findViewById(R.id.recmmend_road_rel);
			holder.roadLookNumText = (TextView) convertView
					.findViewById(R.id.recmmend_road_look_num_text);
			holder.newImageView = (ImageView) convertView
					.findViewById(R.id.recmmend_road_new_route_image);
			holder.onlineImageView = (ImageView) convertView
					.findViewById(R.id.recmmend_road_online_route_image);
			holder.playLin = (LinearLayout) convertView
					.findViewById(R.id.recmmend_title_played_lin);
			holder.recommend_iscollect_img = (SimpleDraweeView) convertView
					.findViewById(R.id.recommend_iscollect_img);
			holder.view = convertView
					.findViewById(R.id.recmmend_city_empty_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 图片
		if (!ls.get(position).getRoadlineBackground().equals("")) {
			if (ls.get(position).getRoadlineBackground().contains("images")) {
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ ls.get(position).getRoadlineBackground()));
			} else {
				holder.imageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
						+ "images/roadlineDescribe/"
						+ ls.get(position).getRoadlineBackground()));
			}
		} else {
			// 加载默认图片
			holder.imageView.setImageURI(Uri.parse("res:///"
					+ R.drawable.uu_default_image_one));
		}
		if (!TextUtils.isEmpty(ls.get(position).getIsNew())
				&& ls.get(position).getIsNew().equals("1")) {
			holder.newImageView.setVisibility(View.VISIBLE);
		} else {
			holder.newImageView.setVisibility(View.GONE);
		}
//		holder.recommend_iscollect_img.setVisibility(View.VISIBLE);
		if (MyApplication.getInstance().isLogin()) {
			if (!ls.get(position).getCollectId().equals("0")) {
				holder.recommend_iscollect_img.setImageURI(Uri.parse("res:///"
						+ R.drawable.home_page_collected_img));

			} else {
				holder.recommend_iscollect_img.setImageURI(Uri.parse("res:///"
						+ R.drawable.home_page_default_collect_img));
			}
		} else {
			holder.recommend_iscollect_img.setImageURI(Uri.parse("res:///"
					+ R.drawable.home_page_default_collect_img));
		}

		holder.roadRel.setVisibility(View.VISIBLE);
		holder.addressText.setVisibility(View.GONE);
		holder.playLin.setVisibility(View.GONE);
		holder.view.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(ls.get(position).getIsOnline())
				&& ls.get(position).getIsOnline().equals("1")) {
			holder.onlineImageView.setVisibility(View.VISIBLE);
		} else {
			holder.onlineImageView.setVisibility(View.GONE);
		}
		holder.roadTitleText.setText(ls.get(position).getRoadlineTitle());
		holder.roadOrderNumText.setText(ls.get(position).getOrderCount());
		if (!TextUtils.isEmpty(ls.get(position).getLineNum())) {
			holder.roadLookNumText.setText(ls.get(position).getLineNum());
		} else {
			holder.roadLookNumText.setText("0");
		}
		holder.roadPriceText.setText(ls.get(position).getRoadlinePrice());
		holder.headImageView.setVisibility(View.VISIBLE);
		if (null != ls.get(position).getUserAvatar()
				&& !ls.get(position).getUserAvatar().equals("")) {
			holder.headImageView.setImageURI(Uri.parse(APPRestClient.SERVER_IP
					+ ls.get(position)
							.getUserAvatar()
							.substring(
									0,
									ls.get(position).getUserAvatar()
											.indexOf("."))
					+ "_ya"
					+ ls.get(position)
							.getUserAvatar()
							.substring(
									ls.get(position).getUserAvatar()
											.indexOf("."))));

		} else {
			holder.headImageView.setImageURI(Uri.parse("res:///"
					+ R.drawable.no_default_head_img));
		}

		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView imageView, headImageView, recommend_iscollect_img;
		ImageView newImageView, onlineImageView;
		TextView addressText, titleText, roadTitleText, roadOrderNumText,
				roadLookNumText, roadPriceText, played;
		RelativeLayout roadRel;
		LinearLayout playLin;
		View view;
	}

}
