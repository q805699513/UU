package com.uugty.uu.com.rightview;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BoundBankEntity;
import com.uugty.uu.entity.BoundBankEntity.BankCardInfo;
import com.uugty.uu.main.MainActivity;
import com.uugty.uu.simplistview.SwipeMenu;
import com.uugty.uu.simplistview.SwipeMenuCreator;
import com.uugty.uu.simplistview.SwipeMenuItem;
import com.uugty.uu.simplistview.SwipeMenuListView;
import com.uugty.uu.simplistview.SwipeMenuListView.OnMenuItemClickListener;
import com.uugty.uu.viewpage.adapter.BankCardAdapter;

public class BankcardActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private LinearLayout back;
	private RelativeLayout add_bank;
	private SwipeMenuListView mListView;
	private BankCardAdapter adapter;
	private List<BankCardInfo> arryList;
	private String fromType = "";

	protected int getContentLayout() {
		// TODO Auto-generated method stub 银行卡
		return R.layout.right_pop_add;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			fromType = getIntent().getStringExtra("type");
		}
		back = (LinearLayout) findViewById(R.id.tabar_back);
		add_bank = (RelativeLayout) findViewById(R.id.container_bill_add);
		mListView = (SwipeMenuListView) findViewById(R.id.bank_card_list);
		mListView.setSource(true);//设置是否与源码保持一致
		arryList = new ArrayList<BankCardInfo>();
		adapter = new BankCardAdapter(arryList, this,fromType);
		mListView.setAdapter(adapter);
	}

	@Override
	protected void initAction() {

		sendRequest();
		back.setOnClickListener(this);
		add_bank.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
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
		
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
				// TODO Auto-generated method stub

				switch (index) {
				case 0:
					CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
					builder.setMessage("确定删除银行卡");
					builder.setTitle("删除银行卡");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 删除银行卡
							sendDelCardRequest(position,arryList.get(position).getBankId());
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
		switch (v.getId()) {
		case R.id.tabar_back:
			finish();
			back.setClickable(false);
			break;
		case R.id.container_bill_add:
			Intent intent = new Intent();
			if (null != fromType && fromType.equals("cash")) {
				intent.putExtra("type", "cash");
			}
			intent.setClass(this, ChooseBankActivity.class);
			startActivity(intent);			
			break;
		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();

		APPRestClient.post(this, ServiceCode.USER_BOUND_BANK_CARDLIST, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,this) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						if (result.getLIST().size() > 0) {
							arryList = result.getLIST();
							adapter.updateList(arryList);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(BankcardActivity.this)
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
	
	//删除银行卡
	private void sendDelCardRequest(final int position,String bankId) {
		RequestParams params = new RequestParams();
		params.add("bankId", bankId);
		APPRestClient.post(this, ServiceCode.DEL_BANK_CARD, params,
				new APPResponseHandler<BoundBankEntity>(BoundBankEntity.class,this) {
					@Override
					public void onSuccess(BoundBankEntity result) {
						arryList.remove(position);
						adapter.updateList(arryList);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(BankcardActivity.this)
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != fromType && fromType.equals("cash")) {
			Intent intent = new Intent();
			intent.putExtra("cardId", arryList.get(position).getBankId());
			intent.putExtra("cardNo", arryList.get(position).getBankCard());
			intent.putExtra("bankType", chooseBankName(Integer.valueOf(arryList
					.get(position).getBankCardType())));
			intent.setClass(this, WithdrawcashActivity.class);
			startActivity(intent);
		}

	}
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	public String chooseBankName(int num) {
		String name = "";
		switch (num) {
		case 1:
			name = "中国银行";
			break;
		case 2:
			name = "中国农业银行";
			break;
		case 3:
			name = "中国工商银行";
			break;
		case 4:
			name = "中国建设银行";
			break;
		case 5:
			name = "中国交通银行";
			break;
		case 6:
			name = "中国招商银行";
			break;
		case 7:
			name = "中国光大银行";
			break;
		default:
			break;
		}
		return name;
	}
}
