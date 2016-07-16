package com.uugty.uu.shop;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.PeerUserEntity;
import com.uugty.uu.entity.PeerUserEntity.ThreeBean;

import java.util.ArrayList;
import java.util.List;

public class ThreeUuActivity extends BaseActivity implements OnClickListener,TextWatcher,
		OnScrollListener, SwipeRefreshLayout.OnRefreshListener{

    private TopBackView titleView;
	private ListView listview;
	private String searchString;
	private ThreeUuAdapter adapter; // 三级小u适配器
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private String mLevel;//几级小u
    private List<ThreeBean> mThreeList = new ArrayList<ThreeBean>();//分销小u列表
    private List<ThreeBean> filterList = new ArrayList<ThreeBean>();;//查找结果

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	private SearchListTask curSearchTask = null;

	private EditText mSerchEdit;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            PeerUserEntity entity = (PeerUserEntity) msg.getData()
                    .getSerializable("PeerUserEntity");
            if (entity != null) {
                List<ThreeBean> result = entity.getLIST();
                switch (msg.what) {
                    case 1:
                        mThreeList.clear();
                        mThreeList.addAll(result);
                        mSwipeLayout.setRefreshing(false);
                        startId++;
                        sendRequest(2);
                        listview.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        mThreeList.addAll(result);
                        adapter.notifyDataSetChanged();
                        break;
                }

            } else {
                mThreeList.clear();
                adapter.notifyDataSetChanged();
            }

        };
    };


	@Override
	protected int getContentLayout() {
		return R.layout.activity_threeuu;
	}

	@Override
	protected void initGui() {
		titleView = (TopBackView) findViewById(R.id.collect_titile_top);
		titleView.setTitle("我是小u");
        mSerchEdit = (EditText) findViewById(R.id.shop_isu_edittext);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.threeuu_swipe_container);
		listview = (ListView) findViewById(R.id.threeuu_list);
        // list footer
        TextView footView = new TextView(ctx);
        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(50));
        footView.setLayoutParams(params);
        listview.addFooterView(footView, null, false);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ThreeUuAdapter(ctx, mThreeList);
        listview.setAdapter(adapter);

	}

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
	@Override
	protected void initAction() {
        mSerchEdit.addTextChangedListener(this);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(200);
        listview.setOnScrollListener(this);
        mSwipeLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
	}

	@Override
	protected void initData() {
        if(getIntent() != null){
            mLevel = getIntent().getStringExtra("level");
        }
        sendRequest(1);//发送网络请求获取数据
	}


	@Override
	public void onClick(View v) {

	}

    public void sendRequest(final int what){
        RequestParams params = new RequestParams();
        params.add("level",mLevel);
        params.add("currentPage",String.valueOf(startId));
        params.add("pageSize","10");
        APPRestClient.post(this, ServiceCode.PEER_USER, params,
                new APPResponseHandler<PeerUserEntity>(PeerUserEntity.class, this) {
                    @Override
                    public void onSuccess(PeerUserEntity result) {
                        if (null != result.getLIST()
                                && result.getLIST().size() > 0) {
                            Message msg = Message.obtain();
                            msg.what = what;
                            Bundle b = new Bundle();
                            b.putSerializable("PeerUserEntity", result);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        } else {
                            if (mSwipeLayout != null) {
                                mSwipeLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        if (errorCode == 3) {
                            sendRequest(what);
                        } else {
                            CustomToast.makeText(ctx, 0, errorMsg, 300).show();
                            if (errorCode == -999) {
                                new AlertDialog.Builder(
                                        ThreeUuActivity.this)
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
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		searchString = mSerchEdit.getText().toString().trim().toUpperCase();

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

    /*
        下拉刷新,分页初始化
     */
    @Override
    public void onRefresh() {
        startId = 1;
        sendRequest(1);
    }

    public void onDestroy() {
        super.onDestroy();
        mThreeList.clear();
    }

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

    /*
        滑动到当前加载数据底部,继续分页请求
     */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (startId > 1) {
            if (firstVisibleItem == (startId - 1) * 10) {
                startId++;
                sendRequest(2);
            }
        }
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
				for (ThreeBean item : mThreeList)
				{
					boolean isVipId = item.getUserVipId().toUpperCase().indexOf(keyword) > -1;

					if (isVipId)
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

					ThreeUuAdapter adapter = new ThreeUuAdapter(ctx,filterList);
					listview.setAdapter(adapter);
				} else
				{
                    ThreeUuAdapter adapter = new ThreeUuAdapter(ctx,mThreeList);
					listview.setAdapter(adapter);
				}
			}

		}
	}


}

 class ThreeUuAdapter extends BaseAdapter{

    private Context context;
    private List<ThreeBean> ls = new ArrayList<ThreeBean>();
    private LayoutInflater inflater;

    public ThreeUuAdapter(Context context, List<ThreeBean> ls) {
        this.context = context;
        this.ls = ls;
        inflater = LayoutInflater.from(context);
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
    public View getView(int position, View view, ViewGroup parent) {
       final ViewHolder holder ;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.threeuu_list_item, null);
            holder.userHead = (SimpleDraweeView) view
                    .findViewById(R.id.threeuu_person_avatar);
            holder.userName = (TextView) view
                    .findViewById(R.id.threeuu_name);
            holder.userId = (TextView) view
                    .findViewById(R.id.threeuu_id);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (null != ls.get(position).getUserAvatar() // 用户头像
                && !ls.get(position).getUserAvatar().equals("")) {
            holder.userHead.setImageURI(Uri
                    .parse(APPRestClient.SERVER_IP+ls.get(position).getUserAvatar().substring(0, ls.get(position).getUserAvatar().indexOf("."))
                            + "_ya"
                            + ls.get(position).getUserAvatar().substring(ls.get(position).getUserAvatar().indexOf("."))));
        } else {
            holder.userHead.setImageURI(Uri.parse("res:///"
                    + R.drawable.no_default_head_img));
        }
        if(null != ls.get(position).getUserName()) {
            holder.userName.setText(ls.get(position).getUserName()); // 用户名
        }else{
            holder.userName.setText("小u"); // 用户名
        }
        if(null != ls.get(position).getUserVipId()){
            holder.userId.setText(ls.get(position).getUserVipId());
        }


        return view;
    }

    static class ViewHolder {
        SimpleDraweeView userHead;
        TextView userName;
        TextView userId;
    }
}
