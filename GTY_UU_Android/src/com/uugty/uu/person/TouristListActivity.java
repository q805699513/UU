package com.uugty.uu.person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.TouristEntity;
import com.uugty.uu.entity.TouristEntity.Tourist;
import com.uugty.uu.order.UUPayActivity;
import com.uugty.uu.simplistview.SwipeMenu;
import com.uugty.uu.simplistview.SwipeMenuCreator;
import com.uugty.uu.simplistview.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TouristListActivity extends BaseActivity implements
OnScrollListener, SwipeRefreshLayout.OnRefreshListener,
OnClickListener{
	private ImageView tourist_list_back;
	private TextView tourist_list_confirm,tourist_add_text;
	private SwipeMenuListView mListView;
	private int startId = 1;// 起始页页
	private SwipeRefreshLayout mSwipeLayout;
	private SpotsDialog loadingDialog;
	private List<Tourist> list = new ArrayList<Tourist>(); //联系人数据
	private List<String> ilist=new ArrayList<String>(); // 联系人 ID
	private List<String> clist=new ArrayList<String>(); //对比联系人ID
	private List<Tourist> selectList = new ArrayList<Tourist>();
	private List<String> mNameList = new ArrayList<String>();
	private TouristAdapter adapter;
	private String contactId="";
	private String touristType="";//0是添加   1是修改
	private final static int ADDTOURIST=101;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			TouristEntity entity = (TouristEntity) msg.getData()
					.getSerializable("TouristEntity");
			if (entity != null) {
				List<Tourist> result = (List<Tourist>) entity.getLIST();
				switch (msg.what) {
				case 1:
					list.clear();
					list.addAll(result);
					mSwipeLayout.setRefreshing(false);
					startId++;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// 显示dialog
							loadingDialog.dismiss();
						}
					}, 500);
					loadData(2, false);
					break;
				case 2:
					list.addAll(result);
					break;
				}
				adapter.notifyDataSetChanged();
			} else {
				list.clear();
				adapter.notifyDataSetChanged();
			}

		};
	};
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.tourist_card_list_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(getIntent()!=null){
			contactId=getIntent().getStringExtra("id");
		}
		String ids[]=contactId.split(",");
		for (int i = 0; i < ids.length; i++) {
			clist.add(ids[i]);
		}
		tourist_list_back=(ImageView) findViewById(R.id.tourist_list_back);
		tourist_add_text=(TextView) findViewById(R.id.tourist_add_text);
		tourist_list_confirm=(TextView) findViewById(R.id.tourist_list_confirm);
		mListView=(SwipeMenuListView) findViewById(R.id.tourist_card_list);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.tourist_swipe_container);
		adapter=new TouristAdapter(ctx, list);
		mListView.setAdapter(adapter);
		
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		tourist_add_text.setOnClickListener(this);
		tourist_list_confirm.setOnClickListener(this);
		tourist_list_back.setOnClickListener(this);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		mListView.setOnScrollListener(this);
		mListView.setSource(true);//设置是否与源码保持一致
		// 添加向右滑动删除
		SwipeMenuCreator menucreatot = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// TODO Auto-generated method stub
				com.uugty.uu.simplistview.SwipeMenuItem deleteltem = new com.uugty.uu.simplistview.SwipeMenuItem(getApplicationContext());
				// 设置item的背景
				deleteltem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// 设置item的宽度
				deleteltem.setWidth(dp2px(75));

				// 设置item的图片
				deleteltem.setIcon(R.drawable.ic_delete);
				// add to menu

				menu.addMenuItem(deleteltem);
			}
		};

		mListView.setMenuCreator(menucreatot); // 设置左滑删除事件

		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
				// TODO Auto-generated method stub

				switch (index) {
					case 0:
						CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
						builder.setMessage("确定删联系人吗");
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// 删除联系人
								deleteContact(list.get(position).getContactId());
								dialog.dismiss();


							}
						});

						builder.setNegativeButton("取消",
								new android.content.DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});

						builder.create().show();

						break;

					default:
						break;
				}
			}

		});
		loadData(1, true);
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				touristType="1";
//				Intent intent=new Intent();
//				intent.setClass(ctx, TouristFillActivity.class);
//				intent.putExtra("touristType", touristType);
//				intent.putExtra("name", list.get(position).getContactName());
//				intent.putExtra("id_card", list.get(position).getContactIDCard());
//				intent.putExtra("toristId", list.get(position).getContactId());
//				startActivityForResult(intent, ADDTOURIST);
//			}
//		});
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		Intent intent=new Intent();
		switch (v.getId()) {
		case R.id.tourist_list_confirm:
			String all = "";
			String name = "";
			ilist.clear();
			mNameList.clear();
			for (int i = 0; i < list.size(); i++) {				
				if(list.get(i).getContactStatus().equals("1")){
					selectList.add(list.get(i));
					ilist.add(list.get(i).getContactId());	
					mNameList.add(list.get(i).getContactName());
				}
			}
			for (int i = 0; i < ilist.size(); i++) {
				if (i == ilist.size() - 1) {
					all+=ilist.get(i);
				}else{
						all+=ilist.get(i)+",";
				}
			}
			for(int i = 0;i< mNameList.size();i++){
				if (i == mNameList.size() - 1) {
					name+=mNameList.get(i);
				}else{
						name+=mNameList.get(i)+" ";
				}
			}
			if(ilist.size()<1){
				CustomToast.makeText(ctx, 0, "请选择出行人", 300).show();
			}else{
				intent.setClass(ctx, UUPayActivity.class);
				intent.putExtra("num", ilist.size()+"");
				intent.putExtra("list",(Serializable)selectList);
				intent.putExtra("name", name);
				intent.putExtra("allId",all);
				setResult(RESULT_OK, intent);
				finish();				
			}
			break;
		case R.id.tourist_list_back:
			finish();
		break;
		case R.id.tourist_add_text:
			touristType="0";
			intent.setClass(this, TouristFillActivity.class);
			intent.putExtra("touristType", touristType);
			startActivityForResult(intent, ADDTOURIST);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			switch (requestCode) {
			case ADDTOURIST:
				onRefresh();
				break;
			default:
				break;
			}
		}
	}
	@Override  
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	private void deleteContact(final String contactId) {
		RequestParams params = new RequestParams();
		params.add("contactId", contactId); // 当前页数
		APPRestClient
				.post(this, ServiceCode.DELETE_CONTACT, params,
						new APPResponseHandler<BaseEntity>(
								BaseEntity.class, this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									deleteContact(contactId);
								} else {
									loadingDialog.dismiss();
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									if (errorCode == -999) {
										new AlertDialog.Builder(
												TouristListActivity.this)
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
		ilist.clear();
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startId = 1;
		loadData(1, false);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (startId > 1) {
			if (firstVisibleItem == (startId - 2) * 2) {
				startId++;
				loadData(2, false);
			}
		}
	}
	private void loadData(final int what, final boolean msg) {
		// 显示等待层
		if (what == 1 && msg) {
			if(loadingDialog!=null){
				loadingDialog.show();
			}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
			}
		}
		RequestParams params = new RequestParams();
		params.add("currentPage", String.valueOf(startId)); // 当前页数
		params.add("pageSize", "5"); // pageSize
		APPRestClient
				.post(this, ServiceCode.QUERY_CONTACT, params,
						new APPResponseHandler<TouristEntity>(
								TouristEntity.class, this) {
							@Override
							public void onSuccess(TouristEntity result) {
								if (null != result.getLIST()
										&& result.getLIST().size() > 0) {
									Message msg = Message.obtain();
									msg.what = what;
									Bundle b = new Bundle();
									b.putSerializable("TouristEntity", result);
									msg.setData(b);
									handler.sendMessage(msg);
								} else {
									if (mSwipeLayout != null) {
										mSwipeLayout.setRefreshing(false);
									}
									if (startId == 1) {
										new Handler().postDelayed(
												new Runnable() {
													public void run() {
														// 显示dialog
														loadingDialog.dismiss();
													}
												}, 500);
									}
								}

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									loadData(what, msg);
								} else {
								loadingDialog.dismiss();
								CustomToast.makeText(ctx, 0, errorMsg, 300)
										.show();
								if (errorCode == -999) {
									new AlertDialog.Builder(
											TouristListActivity.this)
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
	public String replaceSubString(String str){
		if(str!=null && str.length() >=18 ){
            StringBuilder sb  =new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (i > 10&& i <= 14) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            str=sb.toString();
        }
		return str;
	}
	class TouristAdapter extends BaseAdapter{
		private Context context;
		private List<Tourist> ls=new ArrayList<Tourist>();
		
		public TouristAdapter(Context context, List<Tourist> ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ls.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(view==null){
				holder=new ViewHolder();
				view=LayoutInflater.from(context).inflate(R.layout.tourist_card_list_item_layout, null);
				holder.touristname_selected_img=(ImageView) view.findViewById(R.id.touristname_selected_img);
				holder.touristname_text=(TextView) view.findViewById(R.id.touristname_text);
				holder.tourist_id_card_text=(TextView) view.findViewById(R.id.tourist_id_card_text);
				holder.contact_edit = (LinearLayout) view.findViewById(R.id.contact_edit);
				holder.contact_check = (LinearLayout) view.findViewById(R.id.contact_check);

				view.setTag(holder);
			}else{
				holder=(ViewHolder) view.getTag();
			}
			for (int i = 0; i < clist.size(); i++) {
				if(clist.get(i).equals(ls.get(position).getContactId())){
					ls.get(position).setContactStatus("1");
					clist.remove(i);
				}
			}
			holder.contact_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					touristType="1";
					Intent intent=new Intent();
					intent.setClass(ctx, TouristFillActivity.class);
					intent.putExtra("touristType", touristType);
					intent.putExtra("name", ls.get(position).getContactName());
					intent.putExtra("id_card", ls.get(position).getContactIDCard());
					intent.putExtra("toristId", ls.get(position).getContactId());
					startActivityForResult(intent, ADDTOURIST);
				}
			});
			holder.touristname_text.setText(ls.get(position).getContactName());
//			holder.tourist_id_card_text.setText(replaceSubString(ls.get(position).getContactIDCard()));
			holder.tourist_id_card_text.setText(ls.get(position).getContactIDCard());
			if (ls.get(position).getContactStatus().equals("1")) {
				holder.touristname_selected_img.setImageResource(R.drawable.lzh_check_on);
			} else {
				holder.touristname_selected_img.setImageResource(R.drawable.lzh_check_off);
				}
			holder.contact_check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(ls.get(position).getContactStatus().equals("1")){
							ls.get(position).setContactStatus("0");
						}else{
							ls.get(position).setContactStatus("1");	
						}
						notifyDataSetChanged();	
				}
			});
			return view;
		}
		class ViewHolder{
			ImageView touristname_selected_img;
			LinearLayout contact_check,contact_edit;
			TextView touristname_text,tourist_id_card_text;
		}
	}
}
