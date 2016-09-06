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

//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            GuideEntity entity = (GuideEntity) msg.getData().getSerializable(
//                    "homePageEntity");
//            if (entity != null) {
//                List<GuideEntity.GuideDetail> result = (List<GuideEntity.GuideDetail>) entity.getLIST();
//                switch (msg.what) {
//                    case 1:
//                        homePageList.clear();
//                        homePageList.addAll(result);
//                        startId++;
//                        loadThemeData(2);
//                        break;
//                    case 2:
//                        homePageList.addAll(result);
//                        break;
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                homePageList.clear();
//                adapter.notifyDataSetChanged();
//            }
//
//        };
//    };

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
        adapter = new GuideShowAdapter(getActivity(), homePageList);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                adapter);
        swingBottomInAnimationAdapter.setAbsListView(mListView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);

        mListView.setAdapter(swingBottomInAnimationAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
        loadThemeData(1);

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
            if (firstVisibleItem == (startId - 1) * 5) {
                startId++;
                loadThemeData(2);
            }
        }
    }

    private void loadThemeData(final int what) {
        // 显示等待层
        RequestParams params = new RequestParams();
        params.add("roadlineThemeId", mThemeId); // 当前页数
        params.add("roadlineThemeArea",mThemeCity);
        params.add("currentPage", String.valueOf(startId)); // 当前页数
        params.add("pageSize", "5"); // pageSize

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
//                            Message msg = Message.obtain();
//                            msg.what = what;
//                            Bundle b = new Bundle();
//                            b.putSerializable("homePageEntity", result);
//                            msg.setData(b);
//                            handler.sendMessage(msg);
                            homePageList.addAll(result.getLIST());
                            adapter.notifyDataSetChanged();
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

}
