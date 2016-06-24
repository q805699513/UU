package com.uugty.uu.com.find;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.entity.MoreLvEntity;
import com.uugty.uu.entity.MoreLvEntity.MoreListEntity;
import com.uugty.uu.viewpage.adapter.MoreListAdapter;

public class MoreLineActivity extends BaseActivity implements OnClickListener{
	private TopBackView back;
	private ListView listView;
	private MoreListAdapter adapter;
	private List<MoreListEntity> morelv=new ArrayList<MoreListEntity>();
	private String userId = "";
	private SpotsDialog loadingDialog;
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_more_listview;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			userId = getIntent().getStringExtra("userId");
		}
		back=(TopBackView) findViewById(R.id.more_list_top_title_detail_back);
		back.setTitle("更多路线");
		listView=(ListView) findViewById(R.id.More_listView_show);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		sendRequestMoreLine();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}
	private void moreList(final List<MoreListEntity> result) {
		// TODO Auto-generated method stub
		adapter=new MoreListAdapter(result, this,"1");
		listView.setAdapter(adapter);
		morelv = result;
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ActivityCollector.removeSpecifiedActivity("com.uugty.uu.com.find.FindTestViewPagerActivity");
				Intent intent=new Intent();
				intent.putExtra("roadId",morelv.get(position).getRoadlineId());
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.setClass(MoreLineActivity.this, FindTestViewPagerActivity.class);
				startActivity(intent);
			}
		});
	}
	Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				MoreLvEntity moreLista= (MoreLvEntity) msg.getData()
						.getSerializable("morelist");
				moreList(moreLista.getLIST());
				new Handler().postDelayed(new Runnable() {
					public void run() {
						// 显示dialog
						loadingDialog.dismiss();
					}
				}, 500);
				break;

			default:
				break;
			}
			
		};
	};
	private void sendRequestMoreLine() {
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
		loadingDialog = new SpotsDialog(this);
		loadingDialog.show();
		}

		RequestParams params = new RequestParams();
		params.add("userId",userId);
		APPRestClient.post(this, ServiceCode.ROAD_LINE_ALL_LISTSIMPLE, params,
				new APPResponseHandler<MoreLvEntity>(MoreLvEntity.class, this) {
					@Override
					public void onSuccess(MoreLvEntity result) {
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("morelist",result);						
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequestMoreLine();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(MoreLineActivity.this)
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
						new Handler().postDelayed(new Runnable() {
							public void run() {
								// 显示dialog
								loadingDialog.dismiss();
							}
						}, 500);
					}}

					@Override
					public void onFinish() {

					}
				});

	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.more_list_top_title_detail_back:			
			finish();
			break;

		default:
			break;
		}
	}
}
