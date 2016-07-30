package com.uugty.uu.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.myview.SearchPopuWindow;
import com.uugty.uu.entity.DisCoveryEntity;
import com.uugty.uu.entity.DisCoveryEntity.TagTypeDetail;

import java.util.List;

public class LabelFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,OnClickListener{
	private Context context;
	private View rootview;
	private JoyGridView labelgrid;
	private ImageView label_search_btn;
	private SimpleDraweeView label_bg_img;
	private JoyAdapter adapter;
	private List<TagTypeDetail> tlist;
	private SwipeRefreshLayout mSwipeLayout;
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.context = getActivity();
		if (rootview == null) {

			rootview = inflater.inflate(R.layout.labelfragment, null);

		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootview.getParent();

		if (parent != null) {
			parent.removeView(rootview);
		}
		return rootview;
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mSwipeLayout = (SwipeRefreshLayout) rootview
				.findViewById(R.id.label_swipe_container);
		label_search_btn=(ImageView) rootview.findViewById(R.id.label_search_btn);
		label_search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				label_search_btn.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.search_btn_hit));
				Intent mintent = new Intent();
				mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				mintent.setClass(context, SearchPopuWindow.class);
				startActivity(mintent);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						label_search_btn.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.search_btn));
					}
				}, 200);
			}
		});
		label_bg_img=(SimpleDraweeView) rootview.findViewById(R.id.label_bg_img);
		labelgrid=(JoyGridView) rootview.findViewById(R.id.labelgrid);
		labelgrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String contents=tlist.get(position).getTypeName();
				if (null != contents
						&& !contents.equals("")) {
					// 发送请求
					// 调用接口
					Intent intent = new Intent();
					intent.putExtra("city", contents);
					intent.putExtra("content", "mark");
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					intent.setClass(context,
							GuideDetailActivity.class);
					startActivity(intent);
				} 
			}
		});
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
		
		loadData();
	}
	Handler handler =new Handler(){
		public void handleMessage(Message msg) {
		DisCoveryEntity entity=(DisCoveryEntity) msg.getData().getSerializable("DisCoveryEntity");
		if(entity!=null){
		switch (msg.what) {
		case 1:
			if(!entity.getOBJECT().getMainImage().equals("")){
				label_bg_img.setImageURI(Uri.parse(APPRestClient.SERVER_IP 
								+ (entity.getOBJECT().getMainImage())));
			}		
			mSwipeLayout.setRefreshing(false);
			tlist=entity.getOBJECT().getTagTypeDetails();
			 adapter=new JoyAdapter(tlist, getActivity());
			labelgrid.setAdapter(adapter);		
			break;
		
		default:
			break;
		}
		}
		};
	};
	private void loadData() {

		RequestParams params = new RequestParams();
		params.add("currentPage", "1"); // 当前页数
		params.add("pageSize", "9"); // pageSize

		APPRestClient.post(context, ServiceCode.DISCOVERYMAIN, params,
				new APPResponseHandler<DisCoveryEntity>(DisCoveryEntity.class,
						getActivity()) {
					@Override
					public void onSuccess(DisCoveryEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("DisCoveryEntity", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData();
						} else {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						if (errorCode == -999) {
							new AlertDialog.Builder(context)
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
					}}

					@Override
					public void onFinish() {
					}

				});
	}
	class JoyAdapter extends BaseAdapter {
		private List<TagTypeDetail> list;
		private Context context;

		public JoyAdapter(List<TagTypeDetail> list, Context context) {
			super();
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			if(view==null){
				holder=new ViewHolder();
				view=LayoutInflater.from(context).inflate(R.layout.joy_item_view, null);
				holder.name=(TextView) view.findViewById(R.id.lable_item_name);
				holder.img=(SimpleDraweeView) view.findViewById(R.id.label_item_img);			
				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}
			holder.name.setText(list.get(position).getTypeName());
			if (!list.get(position).getTypePicture().equals("")) {
				holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP
								+ list.get(position).getTypePicture()));
		} else {
			// 加载默认图片
			holder.img.setImageURI(Uri.parse("res:///"
					+ R.drawable.uu_default_image_one));
		}
			return view;
		}
		class ViewHolder{
			TextView name;
			SimpleDraweeView img;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadData();
	}
}
