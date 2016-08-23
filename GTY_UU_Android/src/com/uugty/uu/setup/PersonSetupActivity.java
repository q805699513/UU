package com.uugty.uu.setup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.appstart.UpgradeDialogActivity;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.UISwitchButton;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.entity.AppVersionCheckVo;
import com.uugty.uu.login.LoginActivity;


public class PersonSetupActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "PersonSetupActivity";

	private RelativeLayout about_ours,relativ_3,relativ_4,relativ_5,relativ_6,relativ_7,relativ_8;
	private LinearLayout back;
	AlertDialog.Builder builder;
    AlertDialog dialog;//清除缓存
    AlertDialog.Builder builder2;
    AlertDialog dialog2;//退出登录
    private TextView get_version;
    private TextView cache_size;
    private AppVersionCheckVo versionCheckVo;
    private UISwitchButton switch1;
	private SQLiteDatabase db;
	String UserIdd;
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_setup;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PersonSetupActivity.class.getName());
			intent.setClass(PersonSetupActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		UserIdd = MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserId();
		}
		switch1 = (UISwitchButton) findViewById(R.id.switch_btn);
		switch1.setChecked(false);
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					CustomToast.makeText(ctx, 0, "通讯录开启", 300).show();
				} else {
					CustomToast.makeText(ctx, 0, "通讯录关闭", 300).show();
				}
			}
		});
		about_ours = (RelativeLayout) findViewById(R.id.about_ours);
		relativ_3 = (RelativeLayout) findViewById(R.id.relate_set3);
		relativ_4=(RelativeLayout) findViewById(R.id.relate_set4);
		relativ_5=(RelativeLayout) findViewById(R.id.relate_set5);
		relativ_6=(RelativeLayout) findViewById(R.id.relate_set6);
		relativ_7=(RelativeLayout) findViewById(R.id.relate_set7);
		relativ_8=(RelativeLayout) findViewById(R.id.relate_set8);
		cache_size=(TextView) findViewById(R.id.cache_size);
		cache_size.setText(""+FileSizeUtil.getFileOrFilesSize(CacheFileUtil.rootPath, 3)+"M");
		get_version=(TextView) findViewById(R.id.get_version);
		get_version.setText("v "+MyApplication.getInstance().getApp_version());
		back=(LinearLayout) findViewById(R.id.tabar_back);	
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		relativ_3.setOnClickListener(this);
//		relativ_4.setOnClickListener(this);
//		relativ_4.setBackgroundResource(R.drawable.list_item_bg);
		relativ_5.setOnClickListener(this);
		relativ_6.setOnClickListener(this);
		relativ_7.setOnClickListener(this);
		relativ_8.setOnClickListener(this);
		about_ours.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		back.setClickable(true);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {

		// TODO Auto-generated method stub
		Intent intent=new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			back.setClickable(false);
			finish();
		break;
		/*case R.id.relate_set1:
			
			//修改密码界面
			intent.setClass(ctx, PersonPwdActivity.class);
			startActivity(intent);
			break;
        case R.id.relate_set2:
			//消息提醒
        	intent.setClass(ctx, PersonMessageActivity.class);
			startActivity(intent);
			break;
			*/
        case R.id.relate_set3:
        	//检测新版本
        	apkVersionCheck();
        	break;
//        case R.id.relate_set4:
//        	//屏蔽通讯录
//        	intent.setClass(ctx, PersonPingbiActivity.class);
//			startActivity(intent);
//        	break;
        case R.id.relate_set5:
        	//清空缓存
        	CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
			builder.setMessage("确定清空缓存吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							CacheFileUtil.deleteDirectory(CacheFileUtil.rootPath);
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							dialog.dismiss();
							cache_size.setText(""+FileSizeUtil.getFileOrFilesSize(CacheFileUtil.rootPath, 3)+"M");
						}
					});

			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
        	//ClearUUdata();
        	break;
        case R.id.relate_set7:
        	//反馈
        	intent.setClass(ctx,FeedbookActivity.class);
			startActivity(intent);
        	break;
        case R.id.relate_set8:
        	//帮助
        	intent.setClass(ctx, UUHelpActivity.class);
			startActivity(intent);
        	break;
        case R.id.relate_set6:
        	//清空缓存
        	CustomDialog.Builder builder1 = new CustomDialog.Builder(ctx);
			builder1.setMessage("确定退出吗?");
			builder1.setTitle("提示");
			builder1.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							sendLogoutRequest();
							dialog.dismiss();
							SharedPreferenceUtil.getInstance(ctx).setString(
									"userName", "");
							SharedPreferenceUtil.getInstance(ctx).setString(
									"userPwd", "");
							SharedPreferenceUtil.getInstance(ctx)
									.setString("access_token", "");
							SharedPreferenceUtil.getInstance(ctx)
									.setString("refresh_token", "");
							EMChatManager.getInstance().logout();//此方法为同步方法
							MyApplication.getInstance().clearLoginData();							
							finish();
/*							EMChatManager.getInstance().logout(new EMCallBack() {
								 
								@Override
								public void onSuccess() {
								    // TODO Auto-generated method stub
							 
								}
							 
								@Override
								public void onProgress(int progress, String status) {
								    // TODO Auto-generated method stub
							 
								}
							 
								@Override
								public void onError(int code, String message) {
								    // TODO Auto-generated method stub
							 
								}
							});*/
							
							
						}
					});

			builder1.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						CustomToast.makeText(ctx, 0, "取消", 100).show();
						}
					});

			builder1.create().show();
        	break;
        case R.id.about_ours:
        	intent.setClass(PersonSetupActivity.this, ContactUsActivity.class);
			PersonSetupActivity.this.startActivity(intent);
        	break;
		default:
			break;
		}
	
	}
	
	private void apkVersionCheck() {

		try {
			RequestParams params = new RequestParams();
			params.add("clientVersion", MyApplication.getInstance().getApp_version()); // 版本号
			params.add("osType", "android");
			Log.i(TAG, "apkVersionCheck url:" + APPRestClient.BASE_URL
					+ ServiceCode.VERSION_CHECK);
			APPRestClient.post(this, ServiceCode.VERSION_CHECK, params,
					new APPResponseHandler<AppVersionCheckVo>(
							AppVersionCheckVo.class,this) {
						@Override
						public void onSuccess(AppVersionCheckVo result) {
							Message msg = Message.obtain(); 
							msg.what = 5;
							Bundle b = new Bundle(); 
							b.putSerializable("appVersionCheckVo",result); 
							msg.setData(b); 
							handler.sendMessage(msg); 
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(ctx)
										.setTitle("提示")
										.setMessage("网络拥堵,请稍后重试！")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														exit();
													}
												}).show();
							}
						}

						@Override
						public void onFinish() {
							Log.d(TAG, "appVersionCheck onFinish:");
							Message msg = new Message();
							msg.what = 3;
							handler.sendMessage(msg);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "---------版本检测失败---------");
		}

	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3:
				apkUpdate();// apk更新提示
				break;
			case 5:
				// 检查版本
				AppVersionCheckVo vo = (AppVersionCheckVo) msg.getData().getSerializable("appVersionCheckVo");
				versionCheckVo = vo;
				break;
			}
			super.handleMessage(msg);
		};
	};
	
	/**
	 * apk更新
	 */
	public void apkUpdate() {
		Log.i(TAG, "---------apk更新提示........");

		if (versionCheckVo != null) {
			// apk更新逻辑
			if ("0".equals(versionCheckVo.getOBJECT().getSTRATERY())) {
				Log.i(TAG, "---------已是最新版本........");
				// 没有更新, 获取数据字典和区域数据
				CustomToast.makeText(this, 0, "已是最新版本", 200).show();
			} else if ("1".equals(versionCheckVo.getOBJECT().getSTRATERY())) {// 可更新
				Log.i(TAG, "---------有可更新版本........");
				Intent i = new Intent();
				i.putExtra("url",versionCheckVo.getOBJECT().getREDIRECTLOCATION());
				i.setClass(PersonSetupActivity
						.this,UpgradeDialogActivity.class);
				startActivityForResult(i,1);
//				AppVersionCheckDialog.Builder builder2 = new AppVersionCheckDialog.Builder(ctx);
//				builder2.setTitle("发现新版本("+versionCheckVo.getOBJECT()
//						.getCURRVERSION()+")");
//				builder2.setMessage("UU客APP"+versionCheckVo.getOBJECT()
//						.getCURRVERSION()+"发布啦，诚邀您快来体验!");
//				builder2.setPositiveButton("安装",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// 打开浏览器下载
//								Intent intent = new Intent();
//								intent.setAction("android.intent.action.VIEW");
//								Uri content_url = Uri.parse(versionCheckVo
//										.getOBJECT().getREDIRECTLOCATION());
//								intent.setData(content_url);
//								startActivity(intent);
//								exit();
//							}
//						});
//
//				builder2.setNegativeButton("取消",
//						new android.content.DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//							}
//						});
//
//				builder2.create().show();
			} else if ("2".equals(versionCheckVo.getOBJECT().getSTRATERY())) {// 强制升级
				Log.i(TAG, "---------客户端版本太老，强制升级........");
				
			}
		}
	}
}
