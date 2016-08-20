package com.uugty.uu.person;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.UUMessage;
import com.uugty.uu.entity.UserMessage;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderUtils;
import com.uugty.uu.mhvp.core.magic.viewpager.MagicHeaderViewPager;
import com.uugty.uu.mhvp.core.magic.viewpager.PagerSlidingTabStrip;
import com.uugty.uu.uuchat.ChatActivity;
import com.uugty.uu.viewpage.adapter.PersonCenterPagerAdapter;

import java.util.ArrayList;

public class PersonCenterActivity extends BaseActivity implements
		OnClickListener {

	private MagicHeaderViewPager mMagicHeaderViewPager;
	private PersonCenterPagerAdapter mPagerAdapter;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private Fragment btFragment;
	private Fragment secondFragment;
	private Fragment thirdFragment;
	private String detailUserId, avatar, user_name, userDescription;
	private String differentiate = "1";// 用自己 或别人区分 0 自己 1 别人
	private CirculHeadImage headImage;
	private TextView persondate_name, persondate_work, persondate_dress,
			persondate_addfriend_text;
	private ImageView persondate_sex, addFrindsImage;
	private LinearLayout personCenterComplileLin, chatTextLin, addFriendsLin,personcomple_attention_lin;
	private FrameLayout backFrame;
	
	private ImageView mVerU;//认证小U
	private ImageView mRealName;//实名认证
	private ImageView mAcademic;//学历证
	private ImageView mGuide;//导游证
	private ImageView mDriver;//驾驶证
	private String verU,realName,academic,guide,driver;
	private String roadNum;//是否发不过路线

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_person_center;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			detailUserId = getIntent().getStringExtra("detailUserId");
		}
		mMagicHeaderViewPager = new MagicHeaderViewPager(this) {
			@Override
			protected void initTabsArea(LinearLayout container) {
				// You can customize your tabStrip or stable area here
				ViewGroup tabsArea = (ViewGroup) LayoutInflater.from(
						PersonCenterActivity.this).inflate(
						R.layout.layout_tabs1, null);

				// TODO: Set height of stable area manually, then it can be
				// calculated.
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						MagicHeaderUtils.dp2px(PersonCenterActivity.this, 48));
				container.addView(tabsArea, lp);

				// some style
				PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) tabsArea
						.findViewById(R.id.tabs);
				pagerSlidingTabStrip.setTextColor(Color.BLACK);
				pagerSlidingTabStrip.setBackgroundColor(Color.WHITE);
				pagerSlidingTabStrip.setTextColorResource(R.color.black);
				
				// TODO: These two methods must be called to let
				// magicHeaderViewPager know who is stable area and tabs.
				setTabsArea(tabsArea);
				setPagerSlidingTabStrip(pagerSlidingTabStrip);
			}
		};
		LinearLayout mhvpParent = (LinearLayout) findViewById(R.id.mhvp_parent);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mhvpParent.addView(mMagicHeaderViewPager, lp);

		// add your custom Header content
		initCustomHeader();
		
		if (MyApplication.getInstance().isLogin()) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserId().equals(detailUserId)) {
				personCenterComplileLin.setVisibility(View.VISIBLE);
				personcomple_attention_lin.setVisibility(View.GONE);
			}else{
				personCenterComplileLin.setVisibility(View.GONE);
				personcomple_attention_lin.setVisibility(View.VISIBLE);
			}
		} else {
			personCenterComplileLin.setVisibility(View.GONE);
			personcomple_attention_lin.setVisibility(View.VISIBLE);

		}
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		// call this if needed
		mMagicHeaderViewPager
				.setTabOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageSelected(int position) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		mVerU = (ImageView) findViewById(R.id.person_img_ver);
		mRealName = (ImageView) findViewById(R.id.person_img_realname);
		mAcademic = (ImageView) findViewById(R.id.person_img_academic);
		mGuide = (ImageView) findViewById(R.id.person_img_guide);
		mDriver = (ImageView) findViewById(R.id.person_img_drive);

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

	public void initViewPager() {
		if (fragmentList.size() == 0) {
			btFragment = PersonDateFragment_MyLines.newInstance(detailUserId,
					avatar);
			secondFragment = PersonDateFragment_about.newInstance(
					userDescription);
			thirdFragment = PersonDateFragment_Myuu.newInstance(detailUserId,
					user_name, avatar, "0", differentiate);
		}
		if(!"".equals(roadNum)){
			fragmentList.add(btFragment);
		}else{
			if(MyApplication.getInstance().getUserInfo().getOBJECT().getUserId().equals(detailUserId)
					&& null != verU && verU.equals("1")){
				fragmentList.add(btFragment);
			}
		}

		fragmentList.add(thirdFragment);
		fragmentList.add(secondFragment);

		mPagerAdapter = new PersonCenterPagerAdapter(
				getSupportFragmentManager(), fragmentList);

		// TODO: Use this method instead of those of PagerSlidingTabStrip or
		// ViewPager.
		mMagicHeaderViewPager.setPagerAdapter(mPagerAdapter);

		// Then you can do anything like before:)

	}

	private void initCustomHeader() {
		ViewGroup headView = (ViewGroup) LayoutInflater.from(
				PersonCenterActivity.this).inflate(
				R.layout.person_center_header, null);
		headImage = (CirculHeadImage) headView
				.findViewById(R.id.persondate_title);
//		headImage.setBackPic("drawable://" + R.drawable.persion_circle_bg);
		headImage.setCirCularImageSize(80, 80, 1);
		persondate_name = (TextView) headView
				.findViewById(R.id.persondate_name);
		persondate_work = (TextView) headView
				.findViewById(R.id.persondate_work);
		persondate_dress = (TextView) headView
				.findViewById(R.id.persondate_dress);
		persondate_sex = (ImageView) headView.findViewById(R.id.persondate_sex);
		addFriendsLin = (LinearLayout) headView
				.findViewById(R.id.person_add_friends_lin);
		addFrindsImage = (ImageView) headView
				.findViewById(R.id.person_add_friends_image);
		persondate_addfriend_text = (TextView) headView
				.findViewById(R.id.persondate_addfriend_text);
		personCenterComplileLin = (LinearLayout) headView
				.findViewById(R.id.person_compile_text);
		chatTextLin = (LinearLayout) headView
				.findViewById(R.id.persondate_chat);
		backFrame = (FrameLayout) headView
				.findViewById(R.id.person_center_back_frame);
		personcomple_attention_lin = (LinearLayout) headView.findViewById(R.id.personcomple_attention_lin);
		
		mMagicHeaderViewPager.addHeaderView(headView, MagicHeaderUtils.dp2px(this, 290));

		initClick();
	}

	private void initClick() {
		// TODO Auto-generated method stub
		personCenterComplileLin.setOnClickListener(this);
		chatTextLin.setOnClickListener(this);
		backFrame.setOnClickListener(this);
		addFriendsLin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.person_compile_text:
			intent.setClass(PersonCenterActivity.this,
					PersonCompileActivity.class);
			startActivity(intent);
			break;
		case R.id.persondate_chat:
			// 聊天
			if (MyApplication.getInstance().isLogin()) {

				intent.putExtra("userId", detailUserId);
				intent.putExtra("avatar", avatar);
				intent.putExtra("userName", user_name);
				intent.setClass(this, ChatActivity.class);
				startActivity(intent);

			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", PersonDateActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

			}
			break;
		case R.id.person_center_back_frame:
			finish();
			break;
		case R.id.person_add_friends_lin:
			// 聊天
			if (MyApplication.getInstance().isLogin()) {

				// 调用添加好友接口
				addFriendsRequest();
			} else {
				// 先登录
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("topage", PersonDateActivity.class.getName());
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

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
				new APPResponseHandler<UserMessage>(UserMessage.class, this) {
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
								new AlertDialog.Builder(
										PersonCenterActivity.this)
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
		String post="";
		RequestParams params = new RequestParams();
		params.add("friendId", detailUserId);
		if("关注".equals(persondate_addfriend_text.getText().toString())){
			post = ServiceCode.ADD_FRIENDS;
		}else{
			post = ServiceCode.DELETE_FRIENDS;
		}
		APPRestClient.post(this, post, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						if("关注".equals(persondate_addfriend_text.getText().toString())){
							CustomToast.makeText(ctx, 0, "关注成功", 300).show();
							persondate_addfriend_text.setText("取消关注");
							addFrindsImage.setVisibility(View.GONE);
						}else{
							CustomToast.makeText(ctx, 0, "取消成功", 300).show();
							persondate_addfriend_text.setText("关注");
							addFrindsImage.setVisibility(View.VISIBLE);
						}


					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							addFriendsRequest();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(
										PersonCenterActivity.this)
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

						}
					}
				});
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				UserMessage userInfo = (UserMessage) msg.getData()
						.getSerializable("userMessage");
				avatar = userInfo.getOBJECT().getUserAvatar();
				userDescription = userInfo.getOBJECT().getUserDescription();
				user_name = userInfo.getOBJECT().getUserName();

				roadNum = userInfo.getOBJECT().getRoadlineId();//是否发布过路线
				verU = userInfo.getOBJECT().getUserIsPromoter();//是否为会员
				realName = userInfo.getOBJECT().getUserIdValidate();//身份证是否验证
				academic = userInfo.getOBJECT().getUserCertificateValidate();//学历证是否验证
				guide = userInfo.getOBJECT().getUserTourValidate();//导游证是否验证
				driver = userInfo.getOBJECT().getUserCarValidate();//驾驶证是否验证

				if(null != verU && verU.equals("1")){
					mVerU.setVisibility(View.VISIBLE);
				}else{
					mVerU.setVisibility(View.GONE);
				}
				if(null != realName && realName.equals("2")){
					mRealName.setVisibility(View.VISIBLE);
				}else{
					mRealName.setVisibility(View.GONE);
				}
				if(null != academic && academic.equals("2")){
					mAcademic.setVisibility(View.VISIBLE);
				}else{
					mAcademic.setVisibility(View.GONE);
				}
				if(null != guide && guide.equals("2")){
					mGuide.setVisibility(View.VISIBLE);
				}else{
					mGuide.setVisibility(View.GONE);
				}
				if(null != driver && driver.equals("2")){
					mDriver.setVisibility(View.VISIBLE);
				}else{
					mDriver.setVisibility(View.GONE);
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

				if (null != userInfo.getOBJECT().getUserWork()
						&& !userInfo.getOBJECT().getUserWork().equals("")) {
					persondate_work.setText(userInfo.getOBJECT().getUserWork());
				} else {
					persondate_work.setText("导游");
				}
				if (null != userInfo.getOBJECT().getUserCity()
						&& !userInfo.getOBJECT().getUserCity().equals("")) {
					persondate_dress.setText("中国·"
							+ userInfo.getOBJECT().getUserCity());
				} else {
					persondate_dress.setText("中国·");
				}
				if (null != userInfo.getOBJECT().getUserSex()
						&& !userInfo.getOBJECT().getUserSex().equals("")) {
					if (userInfo.getOBJECT().getUserSex().equals("1")) {
						persondate_sex
								.setImageResource(R.drawable.persondate_man);
					} else {
						persondate_sex
								.setImageResource(R.drawable.persondate_women);
					}

				}
				initViewPager();

				break;
			case 3:
				UUMessage result = (UUMessage) msg.getData().getSerializable(
						"uuMessage");
				if (result.getLIST() != null && result.getLIST().size() > 0) {
					for (int i = 0; i < result.getLIST().size(); i++) {
						if (result.getLIST().get(i).getUserId()
								.equals(detailUserId)) {
							persondate_addfriend_text.setText("取消关注");
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
