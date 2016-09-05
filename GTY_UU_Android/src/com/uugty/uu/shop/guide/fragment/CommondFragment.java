package com.uugty.uu.shop.guide.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.Constant;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerListView;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerScroller;
import com.uugty.uu.shop.guide.Model.GuideEntity;
import com.uugty.uu.shop.guide.adapter.GuideShowAdapter;
import com.uugty.uu.shop.guide.base.BaseLazyFragment;
import com.uugty.uu.shop.guide.view.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 通用的Fragment
 */
public class CommondFragment extends BaseLazyFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    @Bind(R.id.multiplestatusview)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.content_view)
    InnerListView mListView;
    private View view;
    private GuideShowAdapter adapter;
    private int currenPage = 1;//当前页
    private String mTheme;
    private String mThemeId;
    private View mFootView;//底部控件
    private LayoutInflater mInflater;
    private boolean isBottom;//是否滑动到底部的标记
    private boolean isLoadData = false;//是否正在加载新数据的标记
    private String url;
    private static final int INITIAL_DELAY_MILLIS = 500;
    private List<GuideEntity.GuideDetail> homePageList = new ArrayList<GuideEntity.GuideDetail>();
    private String mThemeCity;
    private int startId = 1;// 起始页页

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
                    "homePageEntity");
            if (entity != null) {
                List<GuideEntity.GuideDetail> result = (List<GuideEntity.GuideDetail>) entity.getLIST();
                switch (msg.what) {
                    case 1:
                        homePageList.clear();
                        homePageList.addAll(result);
                        startId++;
                        if(mTheme.equals("推荐")) {
                            loadHomeData(2);
                        }else{
                            loadThemeData(2);
                        }
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
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_commond);
        ButterKnife.bind(this, getContentView());

        initView();
    }

    @Override
    public InnerScroller getInnerScroller() {
        return mListView;
    }

    public void initView(){
        //获取标题分类
        mTheme = getArguments().getString("theme");
        mThemeCity = getArguments().getString("city");
        mThemeId = getArguments().getString("themeId");
        mListView.setDividerHeight(0);
        mListView.register2Outer(mOuterScroller, mIndex);
        adapter = new GuideShowAdapter(getActivity(), homePageList);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                adapter);
        swingBottomInAnimationAdapter.setAbsListView(mListView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);

        mListView.setAdapter(swingBottomInAnimationAdapter);
        if(mTheme.equals("推荐")) {
            loadHomeData(1);
        }else{
            loadThemeData(1);
        }
    }


    /**
     * listview的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    ///////////////////////////listview滑动监听方法/////////////////////
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /***
     * 滚动的方法
     *
     * @param view
     * @param firstVisibleItem 第一个可见的item的下标
     * @param visibleItemCount 可见的item的数量
     * @param totalItemCount   listview中总的item数量
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //若第一个可见的item的下标+可见的条目的数量=当前listview中总的条目数量，则说明已经到达底部
        isBottom = firstVisibleItem + visibleItemCount == totalItemCount;
        if (startId > 1) {
            if (firstVisibleItem == (startId - 1) * 5) {
                startId++;
                loadHomeData(2);
            }
        }
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.unbind(this);
    }
    private void loadThemeData(final int what) {
        // 显示等待层
        RequestParams params = new RequestParams();
        params.add("roadlineThemeId", mThemeId); // 当前页数
        params.add("roadlineThemeArea",mThemeCity);
        params.add("currentPage", String.valueOf(startId)); // 当前页数
        params.add("pageSize", "10"); // pageSize

        APPRestClient.postGuide(getActivity(), ServiceCode.GUIDE_THEME, params,
                new APPResponseHandler<GuideEntity>(GuideEntity.class, getActivity()) {
                    @Override
                    public void onSuccess(GuideEntity result) {
						/*
						 * Message msg = handler.obtainMessage(); msg.what =
						 * what; msg.obj = result.getLIST();
						 * handler.sendMessage(msg);
						 */
                        if (null != result.getLIST()
                                && result.getLIST().size() > 0) {
                            Message msg = Message.obtain();
                            msg.what = what;
                            Bundle b = new Bundle();
                            b.putSerializable("homePageEntity", result);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }

                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        if (errorCode == 3) {
                            loadThemeData(what);
                        } else {
                            CustomToast.makeText(getActivity(), 0, errorMsg, 300).show();
                            if (errorCode == -999) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("提示")
                                        .setMessage("服务器连接失败！")
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
                        }}

                    @Override
                    public void onFinish() {
                    }
                });
    }

    private void loadHomeData(final int what) {
        RequestParams params = new RequestParams();
        params.add("markSearchType", "goal_title");
        params.add("markTitle", mThemeCity); // pageSize
        params.add("currentPage", String.valueOf(startId)); // 当前页数
        params.add("pageSize", "10"); // pageSize
        params.add("isOnline", ""); // 性别
        params.add("userSex", ""); // 性别
        params.add("userTourValidate", ""); // 用户的旅游证
        params.add("userCarValidate", ""); // 用户的车
        params.add("sort", ""); // 排序
        params.add("city",mThemeCity);
        params.add("markContent", mThemeCity); // 城市

        APPRestClient.postGuide(getActivity(), ServiceCode.ROAD_LINE_SEARCH, params,
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
                        if (errorCode == -999) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("提示")
                                    .setMessage("网络拥堵,请稍后重试！")
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

    /**
     * 判断是否滑动到顶端
     * 解决滑动冲突
     * @return
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mListView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mListView;
                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0 ||
                                absListView.getChildAt(0).getTop() < absListView.getPaddingTop());

            } else {
                return ViewCompat.canScrollVertically(mListView, -1) || mListView.getScrollY() > 0;
            }

        } else {

            return ViewCompat.canScrollVertically(mListView, -1);

        }

    }
}
