package com.uugty.uu.shop.guide.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.mhvp.core.magic.viewpager.AbsBaseFragment;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerListView;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerScroller;
import com.uugty.uu.shop.guide.Model.GuideEntity;
import com.uugty.uu.shop.guide.activity.RoadDetailActivity;
import com.uugty.uu.shop.guide.adapter.GuideShowAdapter;
import com.uugty.uu.shop.guide.utils.NetConnectedUtils;
import com.uugty.uu.shop.guide.view.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 通用的Fragment
 */
public class CommondFragment extends AbsBaseFragment implements InnerListView.OnItemClickListener, InnerListView.OnScrollListener {

    @Bind(R.id.multiplestatusview)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.content_view)
    InnerListView mListView;
    private View view;
    private GuideShowAdapter adapter;
    private String mTheme;
    private String mThemeId;
    private static final int INITIAL_DELAY_MILLIS = 500;
    private List<GuideEntity.GuideDetail> homePageList = new ArrayList<GuideEntity.GuideDetail>();
    private String mThemeCity;
    private int startId = 1;// 起始页页

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.fragment_commond, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
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
        //点击重试
        multiplestatusview.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载数据
                if("推荐".equals(mTheme)) {
                    loadHomeData(1);
                }else{
                    loadThemeData(1);
                }
            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        //加载数据
        if("推荐".equals(mTheme)) {
            loadHomeData(1);
        }else{
            loadThemeData(1);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        homePageList.clear();
        ButterKnife.unbind(this);
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
        Intent intent = new Intent();
        intent.putExtra("roadId", homePageList.get(position-1)
                .getRoadlineId());
        intent.setClass(getActivity(), RoadDetailActivity.class);
        startActivity(intent);

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
        if (startId > 1) {
            if (firstVisibleItem == (startId - 1) * 10) {
                startId++;
                loadThemeData(2);
            }
        }
    }

    private void loadHomeData(final int what) {
        RequestParams params = new RequestParams();
        params.add("markSearchType", "goal_title");
        params.add("markTitle", mThemeCity); // pageSize
        params.add("currentPage", String.valueOf(startId)); // 当前页数
        params.add("pageSize", "5"); // pageSize
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

                        if(what == 1){
                            homePageList.clear();
                            if(result.getLIST().size() > 0){
                                homePageList = result.getLIST();
                            }
                            adapter = new GuideShowAdapter(getActivity(), homePageList);
                            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                    adapter);
                            swingBottomInAnimationAdapter.setAbsListView(mListView);
                            assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                            swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                                    INITIAL_DELAY_MILLIS);

                            mListView.setAdapter(swingBottomInAnimationAdapter);
                        } else if(what == 2) {
                            homePageList.addAll(result.getLIST());
                            adapter.notifyDataSetChanged();
                        }

                        if (homePageList.isEmpty()) {
                            multiplestatusview.showEmpty();
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        if (errorCode == 3) {
                            loadHomeData(what);
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
                        }
                        if (NetConnectedUtils.isConnected(getActivity())) {
                            multiplestatusview.showError();
                        } else {
                            multiplestatusview.showNoNetwork();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }
                });
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
                        if(what == 1){
                            homePageList.clear();
                            if(result.getLIST().size() > 0){
                                homePageList = result.getLIST();
                            }
                            adapter = new GuideShowAdapter(getActivity(), homePageList);
                            SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                    adapter);
                            swingBottomInAnimationAdapter.setAbsListView(mListView);
                            assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                            swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                                    INITIAL_DELAY_MILLIS);

                            mListView.setAdapter(swingBottomInAnimationAdapter);
                        } else if(what == 2) {
                            homePageList.addAll(result.getLIST());
                            adapter.notifyDataSetChanged();
                        }
                        if (homePageList.isEmpty()) {
                            multiplestatusview.showEmpty();
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
                        }
                        if (NetConnectedUtils.isConnected(getActivity())) {
                            multiplestatusview.showError();
                        } else {
                            multiplestatusview.showNoNetwork();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }
                });
    }

}
