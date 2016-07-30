package com.uugty.uu.person;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.UUMessage;
import com.uugty.uu.entity.UserMessage;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.viewpage.adapter.TabFragmentPagerAdapter;

import java.util.ArrayList;

public class PersonDateActivity extends BaseActivity implements OnClickListener {
	private CirculHeadImage headImage;
	private ImageView image, persondate_sex,addFrindsImage;
	private ViewPager mPager;
	private int currIndex;// 当前页卡编号
	private float bmpW;// 横线图片宽度
	private float offset;// 图片移动的偏移量
	private TextView view1, view2, view3;
	private TextView persondate_name, persondate_work, persondate_dress,
			persondate_addfriend_text,tv_routeline;
	private ArrayList<TextView> arryTextView;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();;
	private String detailUserId,avatar,user_name,userDescription;
	private LinearLayout chat, tabar_back, addFriendsLin,personcomple_attention_lin,person_compile_text;
	private Fragment btFragment;
	private Fragment secondFragment;
	private Fragment thirdFragment;
	private boolean isFrist=true;
	private String differentiate;//用自己 或别人区分  0 自己  1 别人
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_date_detal;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			detailUserId = getIntent().getStringExtra("detailUserId");
		}

		headImage = (CirculHeadImage) findViewById(R.id.persondate_title);
		headImage.setBackPic("drawable://" + R.drawable.persion_circle_bg);
		headImage.setCirCularImageSize(80, 80, 8);
		persondate_addfriend_text = (TextView) findViewById(R.id.persondate_addfriend_text);
		persondate_addfriend_text.setEnabled(false);
		tv_routeline = (TextView) findViewById(R.id.tv_routeline);
		persondate_name = (TextView) findViewById(R.id.persondate_name);
		persondate_work = (TextView) findViewById(R.id.persondate_work);
		persondate_dress = (TextView) findViewById(R.id.persondate_dress);
		persondate_sex = (ImageView) findViewById(R.id.persondate_sex);
		person_compile_text = (LinearLayout) findViewById(R.id.person_compile_text);
		chat = (LinearLayout) findViewById(R.id.persondate_chat);
		tabar_back = (LinearLayout) findViewById(R.id.tabar_back);
		personcomple_attention_lin = (LinearLayout) findViewById(R.id.personcomple_attention_lin);
		addFriendsLin = (LinearLayout) findViewById(R.id.person_add_friends_lin);
		addFrindsImage = (ImageView) findViewById(R.id.person_add_friends_image);
		if (MyApplication.getInstance().isLogin()) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId().equals(detailUserId)) {
				differentiate="0";
				tv_routeline.setText("我的小店");
				person_compile_text.setVisibility(View.VISIBLE);
				personcomple_attention_lin.setVisibility(View.GONE);
			}else{
				differentiate="1";
				tv_routeline.setText("他的小店");				
				person_compile_text.setVisibility(View.GONE);
				personcomple_attention_lin.setVisibility(View.VISIBLE);
			}
		} else {
			// 先登录
			Intent mintent=new Intent();
			mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			mintent.putExtra("topage", PersonDateActivity.class.getName());
			mintent.setClass(this, LoginActivity.class);
			startActivity(mintent);

		}
		//
		view1 = (TextView) findViewById(R.id.tv_routeline);
		view2 = (TextView) findViewById(R.id.tv_guid2);
		view3 = (TextView) findViewById(R.id.tv_guid3);
		arryTextView = new ArrayList<TextView>();
		arryTextView.add(view1);
		arryTextView.add(view2);
		arryTextView.add(view3);
		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
//		InitViewPager();
		InitImage();
		
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		person_compile_text.setOnClickListener(this);
		headImage.setOnClickListener(this);
		chat.setOnClickListener(this);
		addFriendsLin.setOnClickListener(this);
		tabar_back.setOnClickListener(this);
		tabar_back.setFocusable(true);  
		tabar_back.setFocusableInTouchMode(true);  
		tabar_back.requestFocus();  
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		getUserInfoRequest();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyApplication.getInstance().isLogin()) {
			loadData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent mintent = new Intent();
		switch (v.getId()) {
		case R.id.person_add_friends_lin:
			// 聊天
			if (MyApplication.getInstance().isLogin()) {
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(detailUserId)) {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("这是你自己");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					//调用添加好友接口
					addFriendsRequest();
				}
			} else {
				// 先登录
				mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				mintent.putExtra("topage", PersonDateActivity.class.getName());
				mintent.setClass(this, LoginActivity.class);
				startActivity(mintent);

			}
			break;
		case R.id.persondate_chat:
			// 聊天
			if (MyApplication.getInstance().isLogin()) {
				if (MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserId().equals(detailUserId)) {
					// 弹出框，确认删除
					CustomDialog.Builder builder = new CustomDialog.Builder(
							this);
					builder.setMessage("这是你自己");
					builder.setTitle("提示");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder.create().show();
				} else {
					mintent.putExtra("userId", detailUserId);
					mintent.putExtra("avatar", avatar);
					mintent.putExtra("userName", user_name);
					mintent.setClass(this, ChatActivity.class);
					startActivity(mintent);
				}
			} else {
				// 先登录
				mintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				mintent.putExtra("topage", PersonDateActivity.class.getName());
				mintent.setClass(this, LoginActivity.class);
				startActivity(mintent);

			}
			break;
		case R.id.tabar_back:
			finish();
			break;
		case R.id.persondate_title:
			if (null!=MyApplication.getInstance().getUserInfo()&&MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId().equals(detailUserId)) {
					mintent.setClass(PersonDateActivity.this, PersonCompileActivity.class);
					startActivity(mintent);
			}
			break;
		case R.id.person_compile_text:
			if (null!=MyApplication.getInstance().getUserInfo()&&MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId().equals(detailUserId)) {
					mintent.setClass(PersonDateActivity.this, PersonCompileActivity.class);
					startActivity(mintent);
			}
			break;
		default:
			break;
		}
	}
	
	private void getUserInfoRequest() {
		RequestParams params = new RequestParams();
		params.add("userId", detailUserId);
		APPRestClient.post(this, ServiceCode.USER_INFO_MESSAGE, params,
				new APPResponseHandler<UserMessage>(UserMessage.class,this) {
					@Override
					public void onSuccess(UserMessage result) {

						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("userMessage", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							getUserInfoRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(PersonDateActivity.this)
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
	
	private void addFriendsRequest() {
		RequestParams params = new RequestParams();
		params.add("friendId", detailUserId);
		APPRestClient.post(this, ServiceCode.ADD_FRIENDS, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class,this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "关注成功", 300).show();
						persondate_addfriend_text.setEnabled(false);
						persondate_addfriend_text.setText("已关注");
						addFrindsImage.setVisibility(View.GONE);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							addFriendsRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(PersonDateActivity.this)
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

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPager.setCurrentItem(index);
			mPager.invalidate();
		}
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		if (fragmentList.size() == 0) {
			btFragment = PersonDateFragment_MyLines.newInstance(detailUserId,
					avatar);
			secondFragment= PersonDateFragment_about.newInstance(userDescription);
			thirdFragment = PersonDateFragment_Myuu
					.newInstance(detailUserId, user_name, avatar, "0",differentiate);
		}
		fragmentList.add(btFragment);
		fragmentList.add(secondFragment);
		fragmentList.add(thirdFragment);

		// 给ViewPager设置适配器
		mPager.setAdapter(new TabFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		view1.setTextColor(getResources().getColor(android.R.color.black));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		// private float one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex
					* (offset * 2 + bmpW), arg0 * (offset * 2 + bmpW), 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
			for (int n = 0; n < arryTextView.size(); n++) {
				if (n == arg0) {
					arryTextView.get(n).setTextColor(
							getResources().getColor(android.R.color.black));
				} else {
					arryTextView.get(n).setTextColor(
							getResources().getColor(
									R.color.route_selected_text_color));
				}
			}
		}
	}

	public void InitImage() {
		image = (ImageView) findViewById(R.id.cursor);

		// bmpW = BitmapFactory.decodeResource(getResources(),
		// R.drawable.fragment_chang_two).getWidth();
		// 1.计算图片与左边的距离
		// 2.bmpW=(屏宽/3-字大小)*2
		TextPaint paint = view3.getPaint();
		float measure = paint.measureText(view3.getText().toString());
		// 194
		bmpW = (float) (measure * 3.34);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;

		// imgageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	private void loadData() {
		RequestParams request = new RequestParams();
		APPRestClient.post(this, ServiceCode.UU_FRIEND_LIST, request,
				new APPResponseHandler<UUMessage>(UUMessage.class, this) {
					@Override
					public void onSuccess(UUMessage result) {
						// TODO Auto-generated method stub
						Message msg = Message.obtain();
						msg.what = 3;
						Bundle b = new Bundle();
						b.putSerializable("uuMessage", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							loadData();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						// TODO Auto-generated method stub

					}}
				});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				UserMessage userInfo = (UserMessage) msg.getData()
						.getSerializable("userMessage");
				avatar= userInfo.getOBJECT().getUserAvatar();
				userDescription = userInfo.getOBJECT().getUserDescription();
				if(isFrist){
					InitViewPager();
				}
				if (null != avatar && !avatar.equals("")) {
					headImage.setHeadPic(avatar, "net");
				} else {
					headImage.setHeadPic("drawable://"
							+ R.drawable.no_default_head_img, "drawable");
				}
				user_name = userInfo.getOBJECT().getUserName();
				if (null != user_name && !user_name.equals("")) {
					persondate_name.setText(user_name);
				} else {
					persondate_name.setText("小u");
				}
				
				if (null != userInfo.getOBJECT().getUserWork() && !userInfo.getOBJECT().getUserWork().equals("")) {
					persondate_work.setText(userInfo.getOBJECT().getUserWork());
				} else {
					persondate_work.setText("导游");
				}
				if (null != userInfo.getOBJECT().getUserCity() && !userInfo.getOBJECT().getUserCity().equals("")) {
					persondate_dress.setText("中国·" + userInfo.getOBJECT().getUserCity());
				} else {
					persondate_dress.setText("中国·");
				}
				if (null != userInfo.getOBJECT().getUserSex() && !userInfo.getOBJECT().getUserSex().equals("")) {
					if (userInfo.getOBJECT().getUserSex().equals("1")) {
						persondate_sex.setImageResource(R.drawable.persondate_man);
					} else {
						persondate_sex.setImageResource(R.drawable.persondate_women);
					}

				}
				isFrist=false;
				break;
			case 3:
				UUMessage result = (UUMessage) msg.getData().getSerializable(
						"uuMessage");
				if (result.getLIST() != null && result.getLIST().size() > 0) {
					for (int i = 0; i < result.getLIST().size(); i++) {
						if (result.getLIST().get(i).getUserId()
								.equals(detailUserId)) {
							persondate_addfriend_text.setEnabled(false);
							persondate_addfriend_text.setText("已关注");
							addFrindsImage.setVisibility(View.GONE);
							break;
						}
					}
				}
				break;
			}
		};
	};

}
