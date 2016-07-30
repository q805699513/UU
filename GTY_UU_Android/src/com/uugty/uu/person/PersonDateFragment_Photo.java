package com.uugty.uu.person;

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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.JoyGridView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.modeal.UUlogin;

import java.util.ArrayList;
import java.util.List;

public class PersonDateFragment_Photo extends Fragment {
	private View view;
	private String roadId;
	private JoyGridView gridView;
	private String[] liftPic;
	private ArrayList<String> slist = new ArrayList<String>();
	private int viewMinHeight;
	private RelativeLayout person_phone_nodate_linear;
	static PersonDateFragment_Photo newInstance(String roadId) {
		PersonDateFragment_Photo newFragment = new PersonDateFragment_Photo();
		Bundle bundle = new Bundle();
		bundle.putString("detailUserId", roadId);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.persondate_photo_item, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		Bundle args = getArguments();
		roadId = args.getString("detailUserId");
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		person_phone_nodate_linear = (RelativeLayout) view.findViewById(R.id.person_phone_nodate_linear);
		gridView = (JoyGridView) view.findViewById(R.id.persondate_photogrid);
		if(viewMinHeight==0){
			viewMinHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight()-
					(int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290, getActivity().getResources().getDisplayMetrics()))
					-(int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getActivity().getResources().getDisplayMetrics()));
		}
		view.setMinimumHeight(viewMinHeight);
		if(slist.size()>0){
			gridView.setAdapter(new JoyAdapter(slist, getActivity()));
		}else{
			sendRequest();
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				imageBrower(position, slist);
			}
		});
		
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				UUlogin roadEntityResult = (UUlogin) msg.getData()
						.getSerializable("roadLine");
				if (null != roadEntityResult.getOBJECT().getUserLifePhoto()&&!roadEntityResult.getOBJECT().getUserLifePhoto().equals("")) {
					person_phone_nodate_linear.setVisibility(View.GONE);
					gridView.setVisibility(View.VISIBLE);
					liftPic = roadEntityResult.getOBJECT().getUserLifePhoto()
							.split(",");
					for (int i = 0; i < liftPic.length; i++) {
						slist.add(liftPic[i]);
					}
					gridView.setAdapter(new JoyAdapter(slist, getActivity()));
				}else{
					person_phone_nodate_linear.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.GONE);
				}
				break;
			}
			;
		};
	};
	
	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "0");
		getActivity().startActivity(intent);
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("userId", roadId);// 路线Id
		APPRestClient.post(getActivity(), ServiceCode.USER_INFO_MESSAGE,
				params, new APPResponseHandler<UUlogin>(UUlogin.class,
						getActivity()) {
					@Override
					public void onSuccess(UUlogin result) {
						slist.clear();
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("roadLine", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
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

	class JoyAdapter extends BaseAdapter {
		private List<String> list;
		private Context context;

		public JoyAdapter(List<String> list, Context context) {
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
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.photo_item_view, null);
				holder.img = (SimpleDraweeView) view.findViewById(R.id.label_item_img);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (!list.get(position).equals("")) {
				holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP + list.get(position)));
			} else {
				// 加载默认图片
				holder.img.setImageURI(Uri.parse("res///"+R.drawable.uu_default_image_one));
			}
			return view;
			
			
		}

		 class ViewHolder {
			SimpleDraweeView img;
		}
	}
}
