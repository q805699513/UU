package com.uugty.uu.shop.guide.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nhaarman.listviewanimations.ArrayAdapter;
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
import com.uugty.uu.entity.GuideEntity;
import com.uugty.uu.mhvp.core.magic.viewpager.AbsBaseFragment;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerListView;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerScroller;
import com.uugty.uu.shop.guide.view.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 通用的Fragment
 */
public class CommondFragment extends AbsBaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    @Bind(R.id.multiplestatusview)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.content_view)
    InnerListView mListView;
    private View inflate;
    private GuideListViewAdapter adapter;
    private int currenPage = 1;//当前页
    private String mTheme;
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
        inflate = inflater.inflate(R.layout.fragment_commond, container, false);
        ButterKnife.bind(this, inflate);
        if (mInflater == null) {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return inflate;
    }

    @Override
    public InnerScroller getInnerScroller() {
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取标题分类
        mTheme = getArguments().getString("theme");
        mThemeCity = getArguments().getString("city");
        mListView.setDividerHeight(0);
        mListView.register2Outer(mOuterScroller, mIndex);
        adapter = new GuideListViewAdapter(getActivity(), homePageList);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                adapter);
        swingBottomInAnimationAdapter.setAbsListView(mListView);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(
                INITIAL_DELAY_MILLIS);

        mListView.setAdapter(swingBottomInAnimationAdapter);
        if(mTheme.equals("推荐")) {
            loadData(1);
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
                loadData(2);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadData(final int what) {
        RequestParams params = new RequestParams();
        params.add("roadlineGoalArea", mThemeCity);
        params.add("areaType","1");
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
class GuideListViewAdapter extends ArrayAdapter {
    private List<GuideEntity.GuideDetail> ls;
    private Context context;

    // private Animation animation;
    // private Map<Integer, Boolean> isFrist;

    public GuideListViewAdapter(Context context, List<GuideEntity.GuideDetail> list) {
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
            holder.onlineImageView = (ImageView) convertView
                    .findViewById(R.id.recmmend_road_online_route_image);
            holder.playLin = (LinearLayout) convertView
                    .findViewById(R.id.recmmend_title_played_lin);
            holder.recommend_iscollect_img = (SimpleDraweeView) convertView
                    .findViewById(R.id.recommend_iscollect_img);
            holder.view = convertView
                    .findViewById(R.id.recmmend_city_empty_view);

            //认证信息
            holder.consult_person_truename = (TextView) convertView
                    .findViewById(R.id.consult_person_truename);
            holder.consult_person_education = (TextView) convertView
                    .findViewById(R.id.consult_person_education);
            holder.consult_person_drive = (TextView) convertView
                    .findViewById(R.id.consult_person_drive);
            holder.consult_person_guide = (TextView) convertView
                    .findViewById(R.id.consult_person_guide);
            holder.is_veru = (TextView) convertView.findViewById(R.id.consult_person_veru);

            //浏览出行量
            holder.new_linear = (LinearLayout) convertView.findViewById(R.id.home_new_linear);
            holder.look_linear = (LinearLayout) convertView.findViewById(R.id.home_look_linear);
            holder.travel_linear = (LinearLayout) convertView.findViewById(R.id.home_travel_linear);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //是否会员
        if (ls.get(position).getUserIsPromoter().equals("1")) {
            holder.is_veru.setVisibility(View.VISIBLE);
        } else {
            holder.is_veru.setVisibility(View.GONE);
        }
        // 实名
        if (ls.get(position).getUserIdValidate().equals("2")) {
            holder.consult_person_truename.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_truename.setVisibility(View.GONE);
        }
        // 导游
        if (ls.get(position).getUserTourValidate().equals("2")) {
            holder.consult_person_guide.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_guide.setVisibility(View.GONE);
        }
        // 驾驶
        if (ls.get(position).getUserCarValidate().equals("2")) {
            holder.consult_person_drive.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_drive.setVisibility(View.GONE);
        }
        // 学历
        if (ls.get(position).getUserCertificateValidate().equals("2")) {
            holder.consult_person_education.setVisibility(View.VISIBLE);
        } else {
            holder.consult_person_education.setVisibility(View.GONE);
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
            holder.new_linear.setVisibility(View.VISIBLE);
        } else {
            holder.new_linear.setVisibility(View.GONE);
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
        if("0".equals(ls.get(position).getOrderCount())){
            holder.travel_linear.setVisibility(View.GONE);
        }else {
            holder.travel_linear.setVisibility(View.VISIBLE);
            holder.roadOrderNumText.setText(ls.get(position).getOrderCount() + "人参加过");
        }
        if (!TextUtils.isEmpty(ls.get(position).getLineNum())) {
            holder.roadLookNumText.setText(ls.get(position).getLineNum() + "人浏览过");
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
        ImageView onlineImageView;
        TextView addressText, titleText, roadTitleText, roadOrderNumText,
                roadLookNumText, roadPriceText, played,consult_person_truename,
                consult_person_education, consult_person_drive,
                consult_person_guide,is_veru;
        RelativeLayout roadRel;
        LinearLayout playLin;
        LinearLayout new_linear,look_linear,travel_linear;
        View view;
    }

}
