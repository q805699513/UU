package com.uugty.uu.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.city.customview.OnItemClick;
import com.uugty.uu.city.customview.SearchTipsGroupView;
import com.uugty.uu.city.customview.SelectTipsGroupView;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.entity.AddMarkEntity;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.MarkEntity;
import com.uugty.uu.entity.UpVoteEntity;
import com.uugty.uu.entity.MarkEntity.PersonMark;
import com.uugty.uu.friendstask.DynamicDetailActivity;

public class AddLableActivity extends BaseActivity implements OnClickListener,
		OnItemClick {
	private Button btnback, btn_ok, add_btn;
	private TextView numText;
	private EmojiEdite lableEdit;
	private SearchTipsGroupView add_lable_lin;
	private SelectTipsGroupView fixed_label_lin;
	private List<String> label = new ArrayList<String>();
	private List<PersonMark> LabelLsit = new ArrayList<PersonMark>();
	private String[] list,fixedlist;
	private int size,fixedsize;
	private boolean flog = true;
	private List<String> fixedls=new ArrayList<String>();
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_label;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		btnback = (Button) findViewById(R.id.btn_back);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		lableEdit = (EmojiEdite) findViewById(R.id.addlable_edit);
		numText = (TextView) findViewById(R.id.lable_num_text);
		add_btn = (Button) findViewById(R.id.lable_add_btn);
		add_lable_lin = (SearchTipsGroupView) findViewById(R.id.add_lable_lin);
		fixed_label_lin = (SelectTipsGroupView) findViewById(R.id.fixed_lable_lin);
		fixedls.add("画画");
		fixedls.add("摄影");
		fixedls.add("音乐");
		fixedls.add("自驾");
		fixedls.add("美食");
		fixedls.add("购物");
		fixedls.add("咨询");
		fixedls.add("自由定制");
		fixedls.add("民宿");
		LabelList();// 查询标签			
	}

	protected void AddLabel() {
		size = label.size();
		list = label.toArray(new String[size]);
		add_lable_lin.removeAllViews();
		if (list.length > 0) {
			add_lable_lin.setVisibility(View.VISIBLE);
			add_lable_lin.initViews(list, this);
			if(MyApplication.getInstance().getUserInfo()!=null){
			MyApplication.getInstance().getUserInfo().getOBJECT().setMarkContent(label.get(0));
			}
		} else {
			add_lable_lin.setVisibility(View.INVISIBLE);
			if(MyApplication.getInstance().getUserInfo()!=null){
				MyApplication.getInstance().getUserInfo().getOBJECT().setMarkContent("暂无标签");
				}
		}
	}
	protected void SelectyLabel(List<String> ls) {
		fixedsize = fixedls.size();
		fixedlist = fixedls.toArray(new String[fixedsize]);
		fixed_label_lin.removeAllViews();
		if (fixedlist.length > 0) {
			fixed_label_lin.setVisibility(View.VISIBLE);
			fixed_label_lin.initViews(fixedlist, this,ls);
		} else {
			fixed_label_lin.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		lableEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				numText.setText(s.length() + "/6");
			}
		});
		btnback.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		add_btn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_ok:
			finish();
			break;
		case R.id.lable_add_btn:
			if (label.size() >=10) {
				CustomToast.makeText(ctx, 0, "最多10个标签哦", 300).show();
			} else {
				flog = true;
				for (int i = 0; i < label.size(); i++) {
					if (lableEdit.getEditableText().toString()
							.equals(label.get(i))) {
						flog = false;
					}
				}
				if (!flog) {
					CustomToast.makeText(ctx, 0, "已有该标签", 300).show();
				} else if (!lableEdit.getEditableText().toString().equals("")) {
					AddlabelContent(lableEdit.getEditableText().toString());
					lableEdit.setText("");
				} else {
					CustomToast.makeText(ctx, 0, "输入标签不可为空", 300).show();
				}
			}
			break;
		default:
			break;
		}
	}
	//用户标签
	@Override
	public void onClick(int position) {
		// TODO Auto-generated method stub
		DeleteLabel(LabelLsit.get(position).getMarkId());
	}
	//固定标签
	@Override
	public void onClicks(int position) {
		// TODO Auto-generated method stub
		if (label.size() >=10) {
			CustomToast.makeText(ctx, 0, "最多10个标签哦", 300).show();
		} else{
		AddlabelContent(fixedls.get(position));
		}
	}
	// 添加标签
	private void AddlabelContent(final String content) {
		RequestParams params = new RequestParams();
		params.add("markContent", content); // 标签
		APPRestClient
				.post(this, ServiceCode.ADD_USERMARK, params,
						new APPResponseHandler<AddMarkEntity>(
								AddMarkEntity.class, this) {
							@Override
							public void onSuccess(AddMarkEntity result) {
								LabelList();
							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								if (errorCode == 3) {
									AddlabelContent(content);
								} else {
									CustomToast.makeText(ctx, 0, errorMsg, 300)
											.show();
									if (errorCode == -999) {
										new AlertDialog.Builder(
												AddLableActivity.this)
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

	// 标签列表
	private void LabelList() {
		RequestParams params = new RequestParams();
		APPRestClient.post(this, ServiceCode.QUERY_USERMARK, params,
				new APPResponseHandler<MarkEntity>(MarkEntity.class, this) {
					@Override
					public void onSuccess(MarkEntity result) {
						label.clear();
						LabelLsit = result.getLIST();
						for (int i = 0; i < LabelLsit.size(); i++) {
							label.add(LabelLsit.get(i).getMarkContent());
						}
						AddLabel();
						SelectyLabel(label);//固定标签
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							LabelList();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(AddLableActivity.this)
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

	// 删除标签
	private void DeleteLabel(final String markId) {
		RequestParams params = new RequestParams();
		params.add("markId", markId);
		APPRestClient.post(this, ServiceCode.DELETE_USERMARK, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						LabelList();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							DeleteLabel(markId);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(AddLableActivity.this)
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

}
