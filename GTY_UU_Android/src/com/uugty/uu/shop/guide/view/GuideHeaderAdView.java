package com.uugty.uu.shop.guide.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.entity.HomePageRecommendEntity;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderUtils;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderViewPager;
import com.uugty.uu.shop.guide.Interface.HeaderViewInterface;
import com.uugty.uu.shop.guide.adapter.HeaderAdAdapter;
import com.uugty.uu.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lzh on 16/9/1.
 */
public class GuideHeaderAdView extends HeaderViewInterface<List<HomePageRecommendEntity.HomePageRecommend>> {
    @Bind(R.id.vp_ad)
    ViewPager vpAd;
    @Bind(R.id.ll_index_container)
    LinearLayout llIndexContainer;

    // 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.uu_default_image_one)
            .showImageForEmptyUri(R.drawable.square_no_pricture)
            .showImageOnLoading(R.drawable.square_no_pricture)
            .cacheInMemory(true).cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565).build();;
    private static final int TYPE_CHANGE_AD = 0;
    private Thread mThread;
    private List<ImageView> ivList;
    private boolean isStopThread = false;
    private View mView;
    private MagicHeaderViewPager magicHeaderViewPager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TYPE_CHANGE_AD) {
                vpAd.setCurrentItem(vpAd.getCurrentItem() + 1);
            }
        }
    };

    public GuideHeaderAdView(Activity context) {
        super(context);
        ivList = new ArrayList<>();
    }

    @Override
    protected void getView(List<HomePageRecommendEntity.HomePageRecommend> list, MagicHeaderViewPager viewPager) {
        mView = mInflate.inflate(R.layout.guide_header_ad_layout, viewPager, false);
        ButterKnife.bind(this, mView);
        this.magicHeaderViewPager = viewPager;
        magicHeaderViewPager.addHeaderView(mView, MagicHeaderUtils.dp2px(mContext, 210));
        dealWithTheView(list);
    }

    //移除头部
    public void removeADview(){
        magicHeaderViewPager.removeHeaderView(mView);
    }

    private void dealWithTheView(List<HomePageRecommendEntity.HomePageRecommend> list) {
        ivList.clear();
        int size = list.size();
        //广告栏图片固定3张
        for (int i = 0; i < size; i++) {
            ivList.add(createImageView(list.get(i).getRoadlineThemeImage()));
        }

        HeaderAdAdapter photoAdapter = new HeaderAdAdapter(mContext, ivList,list);
        vpAd.setAdapter(photoAdapter);

        addIndicatorImageViews(size);
        setViewPagerChangeListener(size);
        startADRotate();
    }

    // 创建要显示的ImageView
    private ImageView createImageView(String url) {
        ImageView imageView = new ImageView(mContext);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //获取网络图片
        ImageLoader.getInstance().displayImage(
                APPRestClient.SERVER_IP + url, imageView, options);
        return imageView;
    }

    // 添加指示图标
    private void addIndicatorImageViews(int size) {
        llIndexContainer.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 10));
            if (i != 0) {
                lp.leftMargin = DensityUtil.dip2px(mContext, 7);
            }
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.drawable.xml_round_orange_grey_sel);
            iv.setEnabled(false);
            if (i == 0) {
                iv.setEnabled(true);
            }
            llIndexContainer.addView(iv);
        }
    }

    // 为ViewPager设置监听器
    private void setViewPagerChangeListener(final int size) {
        vpAd.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (ivList != null && ivList.size() > 0) {
                    int newPosition = position % size;
                    for (int i = 0; i < size; i++) {
                        llIndexContainer.getChildAt(i).setEnabled(false);
                        if (i == newPosition) {
                            llIndexContainer.getChildAt(i).setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    // 启动循环广告的线程
    public void startADRotate() {
        // 一个广告的时候不用转
        if (ivList == null || ivList.size() <= 1) {
            return;
        }
        if (mThread == null) {
            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    // 当没离开该页面时一直转
                    while (!isStopThread) {
                        // 每隔5秒转一次
                        SystemClock.sleep(5000);
                        // 在主线程更新界面
                        mHandler.sendEmptyMessage(TYPE_CHANGE_AD);
                    }
                }
            });
            mThread.start();
        }
    }

    // 停止循环广告的线程，清空消息队列
    public void stopADRotate() {
        isStopThread = true;
        if (mHandler != null && mHandler.hasMessages(TYPE_CHANGE_AD)) {
            mHandler.removeMessages(TYPE_CHANGE_AD);
        }
    }
}
