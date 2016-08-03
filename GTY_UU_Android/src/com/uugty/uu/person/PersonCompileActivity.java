package com.uugty.uu.person;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
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
import com.uugty.uu.common.myview.UnderLineTextAndImage;
import com.uugty.uu.common.pullzoomview.PullToZoomScrollViewEx;
import com.uugty.uu.common.pullzoomview.PullToZoomScrollViewEx.OnPullScrollViewListener;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.map.PhoneDialog;
import com.uugty.uu.modeal.UUlogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class PersonCompileActivity extends BaseActivity implements
		OnClickListener {

	// 请求码
	public final static int REQUEST_SEX = 100;
	public final static int REQUEST_CONSTELLATION = 101;
	public final static int REQUEST_LANGUAGE = 103;
	public final static int REQUEST_PROFESSION = 104;
	// 个人头像
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=0";
	// 图片路径
	private String picPath = CacheFileUtil.carmePaht;
	private CirculHeadImage headImage;
	// 拍照图片保存路径
	private String capturePath = null;
	private final static int PAIZHAO = 108;// 拍照
	// 个人信息
	private UUlogin userInfo;
	private UnderLineTextAndImage compileName, compileMy, image_phone2,person_compile_Label;
	private LinearLayout tabbar_back;
	// 基本信息
	private UnderLineTextAndImage sexView, constellationView, birthView,
			eamilView;
	// 可选信息
	private UnderLineTextAndImage schoolView, cityView, professionalView,
			languageView;
	// 验证信息
	private UnderLineTextAndImage mobileTextImage, idcardTextImage, carTextImage, legallyTextImage;
	// 图片轮循区
	private FrameLayout titlebar;
	private TextView title_persondate;
	// 第一次进入是图片
	private ImageView back_image_title;
	private PullToZoomScrollViewEx scrollview;
	private View viewline;
	private int bg_height;
	private View headerView,zoomView,contentView;
	private String finalImageUrl;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_compile;
	}

	@SuppressLint("NewApi")
	@Override
	protected void initGui() {
		// 进入页面之前设置图片裁剪比例
		ServiceCode.FIT_LINE = 0.5833F;
		if (null!=MyApplication.getInstance().getUserInfo())
		userInfo = MyApplication.getInstance().getUserInfo();
		
		scrollview = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
		
		headerView = LayoutInflater.from(this).inflate(R.layout.person_compile_header, null, false);
		zoomView = LayoutInflater.from(this).inflate(R.layout.person_compile_zoom_view, null, false);
		contentView= LayoutInflater.from(this).inflate(R.layout.person_compile_content_view, null, false);
		scrollview.setHeaderView(headerView);
		scrollview.setZoomView(zoomView);
		scrollview.setScrollContentView(contentView);
		
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollview.setHeaderLayoutParams(localObject);
		
		titlebar = (FrameLayout)headerView.findViewById(R.id.person_top_bar);
		viewline = (View)headerView.findViewById(R.id.title_view_line);
		viewline.setVisibility(View.GONE);
		tabbar_back = (LinearLayout)headerView.findViewById(R.id.tabar_back);
		titlebar.getBackground().mutate().setAlpha(0);
		
		
		// 头像
		back_image_title = (ImageView)findViewById(R.id.back_image_title);
		setBackgroundOfVersion(back_image_title,getResources().getDrawable(R.drawable.back_title_whiteimg));
		headImage = (CirculHeadImage) zoomView.findViewById(R.id.person_compile_circuleHead);

		compileName = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_name);
		title_persondate = (TextView)findViewById(R.id.title_persondate);
		title_persondate.setAlpha(0);
		compileName.setLeftText("姓名");
		person_compile_Label = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_Label);
		person_compile_Label.setLeftText("标签");
//		person_compile_Label.setRightText("号啊海大富");
//		person_compile_Label.setTextBG();
		compileMy = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_my);
		compileMy.setLeftText("自我介绍");
		sexView = (UnderLineTextAndImage) contentView.findViewById(R.id.sex);
		sexView.setLeftText("性别");
		constellationView = (UnderLineTextAndImage) contentView.findViewById(R.id.constellation);
		constellationView.setLeftText("星座");
		birthView = (UnderLineTextAndImage) contentView.findViewById(R.id.birth);
		birthView.setLeftText("出生年月");
		eamilView = (UnderLineTextAndImage) contentView.findViewById(R.id.email);
		eamilView.setLeftText("电子邮箱");
		eamilView.hideLine();

		// 可选信息
		cityView = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_city);
		cityView.setLeftText("城市");
		schoolView = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_school);
		schoolView.setLeftText("学校");
		professionalView = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_professional);
		professionalView.setLeftText("行业");
		professionalView.hideLine();
		languageView = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_language);
		languageView.setLeftText("语言");

//		image_phone2 = (UnderLineTextAndImage) contentView.findViewById(R.id.container_phone);
//		image_phone2.setLeftText("相册");
		// 初始化基本信息
		setUserBaseInfo(userInfo);

		mobileTextImage = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_mobile);
		idcardTextImage = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_idcard);
		legallyTextImage = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_legally);
		// educationTextImage = (UnderLineTextAndImage)
		// findViewById(R.id.person_compile_education);
		carTextImage = (UnderLineTextAndImage) contentView.findViewById(R.id.person_compile_car);
		carTextImage.hideLine();
		mobileTextImage.setLedtText("电话");
		idcardTextImage.setLedtText("身份证");
		// educationTextImage.setLedtText("学历证");
		legallyTextImage.setLedtText("导游证");
		carTextImage.setLedtText("车");
		// 初始化验证项
		setVerifyItem(userInfo);
		Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(),
				R.drawable.person_compile_background_img);
		bg_height = bitmap.getHeight() - titlebar.getLayoutParams().height;
		
		MyApplication.getInstance().setKilled(false);
	}

	@Override
	protected void initAction() {
		// backImage.setOnClickListener(this);
		person_compile_Label.setOnClickListener(this);
		tabbar_back.setOnClickListener(this);
		compileName.setOnClickListener(this);
		compileMy.setOnClickListener(this);
		sexView.setOnClickListener(this);
		eamilView.setOnClickListener(this);
		constellationView.setOnClickListener(this);
		cityView.setOnClickListener(this);
		schoolView.setOnClickListener(this);
		languageView.setOnClickListener(this);
		professionalView.setOnClickListener(this);
		birthView.setOnClickListener(this);
//		image_phone2.setOnClickListener(this);
		mobileTextImage.setOnClickListener(this);
		idcardTextImage.setOnClickListener(this);
		// educationTextImage.setOnClickListener(this);
		legallyTextImage.setOnClickListener(this);
		carTextImage.setOnClickListener(this);
		headImage.setOnClickListener(this);
		
		
		scrollview.setOnPullScrollViewListener(new OnPullScrollViewListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPullScrollChanged(PullToZoomScrollViewEx v, int l, int t, int oldl,
					int oldt) {
				// TODO Auto-generated method stub
				if (v.getScrollY() > bg_height) {
					viewline.setVisibility(View.VISIBLE);
					back_image_title.setBackground(ctx.getResources()
							.getDrawable(R.drawable.back_title_img));
				} else {
					back_image_title.setBackground(ctx.getResources()
							.getDrawable(R.drawable.back_title_whiteimg));
					viewline.setVisibility(View.GONE);
				}
				titlebar.getBackground().mutate()
						.setAlpha(getAlphaforActionBar(v.getScrollY()));
				title_persondate.setAlpha(getAlphaforText(v.getScrollY()));
			}

			private int getAlphaforActionBar(int scrollY) {
				int minDist = 0, maxDist = bg_height;
				if (scrollY > maxDist) {
					return 255;
				} else if (scrollY < minDist) {
					return 0;
				} else {
					int alpha = 0;
					alpha = (int) ((255.0 / maxDist) * scrollY);
					return alpha;
				}
			}

			private float getAlphaforText(int scrollY) {
				int minDist = 0, maxDist = bg_height;
				if (scrollY > maxDist) {
					return 1;
				} else if (scrollY < minDist) {
					return 0;
				} else {
					float alpha = 0;
					alpha = (float) ((1.00 / maxDist) * scrollY);
					return alpha;
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().setKilled(true);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {

		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tabar_back:
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.person.PersonValidResultActivity");
			finish();
			tabbar_back.setClickable(false);
			break;
		case R.id.person_compile_name:
			intent.setClass(this, PersonInfoTextActivity.class);
			intent.putExtra("titileText", "姓名");
			startActivity(intent);
			break;
		case R.id.person_compile_Label:
			intent.setClass(this, AddLableActivity.class);
			startActivity(intent);
			break;
		case R.id.person_compile_my:
			intent.setClass(this, PersonDescriptionActivity.class);
			intent.putExtra("titileText", "关于我");
			startActivity(intent);
			break;
		case R.id.sex:
			intent.setClass(this, PersonSexActivity.class);
			startActivityForResult(intent, REQUEST_SEX);
			break;
		case R.id.constellation:
			intent.setClass(this, PersonConstellation.class);
			startActivityForResult(intent, REQUEST_CONSTELLATION);
			break;
		case R.id.birth:
			pickDate();
			break;
		case R.id.email:
			intent.setClass(this, PersonInfoTextActivity.class);
			intent.putExtra("titileText", "电子邮箱");
			startActivity(intent);
			break;
		case R.id.person_compile_city:
			intent.setClass(this, PersonInfoTextActivity.class);
			intent.putExtra("titileText", "城市");
			startActivity(intent);
			break;
		case R.id.person_compile_school:
			intent.setClass(this, PersonInfoTextActivity.class);
			intent.putExtra("titileText", "学校");
			startActivity(intent);
			break;
		case R.id.person_compile_language:
			intent.setClass(this, PersonLanguageActivity.class);
			startActivityForResult(intent, REQUEST_LANGUAGE);
			break;
		case R.id.person_compile_professional:
			intent.setClass(this, PersonProfessionActivity.class);
			startActivityForResult(intent, REQUEST_PROFESSION);
			break;
//		case R.id.container_phone:
//			intent.setClass(this, PersonLeftPhotoActivity.class);
//			startActivity(intent);
//			break;
		// 手机
		/*
		 * case R.id.person_compile_mobile: intent.setClass(this,
		 * PersonValidMobActivity.class); startActivity(intent); break;
		 */
		case R.id.person_compile_idcard:
			intent.setClass(this, PersonPhotoVeriActivity.class);
			intent.putExtra("verifyItem", "idcard");
			startActivity(intent);
			break;
		// 学历证
		// case R.id.person_compile_education:
		// intent.setClass(this, PersonPhotoVeriActivity.class);
		// intent.putExtra("verifyItem", "education");
		// startActivity(intent);
		case R.id.person_compile_legally:
			intent.setClass(this, PersonPhotoVeriActivity.class);
			intent.putExtra("verifyItem", "legally");
			startActivity(intent);
			break;
		case R.id.person_compile_car:
			intent.setClass(this, PersonPhotoVeriActivity.class);
			intent.putExtra("verifyItem", "car");
			startActivity(intent);
			break;
		// 个人头像
		case R.id.person_compile_circuleHead:
			// ServiceCode.FIT_LINE=1.0F;
			/*
			 * intent.setClass(this, PersonChangeHead.class);
			 * startActivityForResult(intent, 100);
			 */
			PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
					PersonCompileActivity.this);
			builder1.setMessage("选择照片");

			builder1.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(
									PersonCompileActivity.this,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									PersonCompileActivity.class.getName());
							intent.putExtra("shape", "circle");
							startActivity(intent);
						}
					});

			builder1.setNegativeButton("拍照",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							getImageFromCamera();
						}
					});

			builder1.create().show();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 根据上面发送过去的请求吗来区别
		Intent intent = new Intent();
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PAIZHAO:
				intent.putExtra("picPath", capturePath);
				intent.putExtra("topageFrom",
						PersonCompileActivity.class.getName());
				intent.putExtra("shape", "circle");
				intent.setClass(PersonCompileActivity.this,
						CutPicturceActivity.class);
				startActivity(intent);
				break;
			case REQUEST_SEX:
				String sex = "1";
				sexView.setRightText(data.getStringExtra("sex"));
				if ("男".equals(data.getStringExtra("sex"))) {
					sex = "1";
					userInfo.getOBJECT().setUserSex(sex);
				} else {
					sex = "2";
					userInfo.getOBJECT().setUserSex(sex);
				}
				RequestParams params = new RequestParams();
				params.put("type", "2");
				params.put("content", sex);
				APPRestClient.post(PersonCompileActivity.this,
						ServiceCode.USER_INFO, params,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,
								this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
				break;
			case REQUEST_CONSTELLATION:
				constellationView.setRightText(data
						.getStringExtra("constellation"));
				userInfo.getOBJECT().setUserConstellation(
						data.getStringExtra("constellation"));
				RequestParams params2 = new RequestParams();
				params2.put("type", "3");
				params2.put("content", data.getStringExtra("constellation"));
				APPRestClient.post(PersonCompileActivity.this,
						ServiceCode.USER_INFO, params2,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,
								this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
				break;
			case REQUEST_LANGUAGE:
				languageView.setRightText(data.getStringExtra("language"));
				userInfo.getOBJECT().setUserLanguage(
						data.getStringExtra("language"));
				RequestParams params3 = new RequestParams();
				params3.put("type", "1");
				params3.put("content", data.getStringExtra("language"));
				APPRestClient.post(PersonCompileActivity.this,
						ServiceCode.USER_INFO, params3,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,
								this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
				break;
			case REQUEST_PROFESSION:
				professionalView
						.setRightText(data.getStringExtra("profession"));
				userInfo.getOBJECT().setUserWork(
						data.getStringExtra("profession"));
				RequestParams params4 = new RequestParams();
				params4.put("type", "10");
				params4.put("content", data.getStringExtra("profession"));
				APPRestClient.post(PersonCompileActivity.this,
						ServiceCode.USER_INFO, params4,
						new APPResponseHandler<BaseEntity>(BaseEntity.class,
								this) {
							@Override
							public void onSuccess(BaseEntity result) {

							}

							@Override
							public void onFailure(int errorCode, String errorMsg) {
								CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							}

							@Override
							public void onFinish() {

							}
						});
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tabbar_back.setClickable(true);
		// 个人头像
		if (null!=MyApplication.getInstance().getUserInfo()&&!MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserAvatar().equals("")) {
			if (MyApplication.getInstance().getUserInfo().getOBJECT()
					.getUserAvatar().contains("images")) {
				headImage.setNoHeadPic(MyApplication.getInstance().getUserInfo().getOBJECT()
								.getUserAvatar(), "net");
			} else {
				headImage.setNoHeadPic(MyApplication.getInstance()
						.getUserInfo().getOBJECT().getUserAvatar(), "local");
			}
//			headImage.setBackPic("drawable://" + R.drawable.persion_circle_bg);
			headImage.setCirCularImageSize(80, 80, 1);

		} else {
			// 加载默认的图片
			headImage.setNoHeadPic("drawable://" + R.drawable.no_default_head_img,
					"drawable");
//			headImage.setBackPic("drawable://" + R.drawable.persion_circle_bg);
			headImage.setCirCularImageSize(80, 80, 1);
		}

//		if (!userInfo.getOBJECT().getUserLifePhoto().equals("")) {
//			arrayListPic = userInfo.getOBJECT().getUserLifePhoto().split(",");
//			image_phone2.setRightText("已添加" + arrayListPic.length + "张");
//		}
		if (!userInfo.getOBJECT().getUserEmail().equals(""))
			eamilView.setRightText(userInfo.getOBJECT().getUserEmail());
		if (!userInfo.getOBJECT().getUserName().equals(""))
			compileName.setRightText(userInfo.getOBJECT().getUserName());
		if (!userInfo.getOBJECT().getUserDescription().equals(""))
			compileMy.setRightText(userInfo.getOBJECT().getUserDescription());
		if (!userInfo.getOBJECT().getUserSchool().equals(""))
			schoolView.setRightText(userInfo.getOBJECT().getUserSchool());
		if (!userInfo.getOBJECT().getUserCity().equals(""))
			cityView.setRightText(userInfo.getOBJECT().getUserCity());
		if (!userInfo.getOBJECT().getMarkContent().equals("")){
			if(userInfo.getOBJECT().getMarkContent().contains(",")){
			String [] mark=userInfo.getOBJECT().getMarkContent().split(",");
			person_compile_Label.setRightText(mark[0]);
			}else{				
				person_compile_Label.setRightText(userInfo.getOBJECT().getMarkContent());
			}
		}else{
			person_compile_Label.setRightText("暂无标签");
		}
		setVerifyItem(userInfo);
	}

	public void pickDate() {
		final DatePickerDialog mDialog;
		if (!userInfo.getOBJECT().getUserBirthday().equals("")) {
			mDialog = new DatePickerDialog(this, null, Integer.valueOf(userInfo
					.getOBJECT().getUserBirthday().split("-")[0]),
					Integer.valueOf(userInfo.getOBJECT().getUserBirthday()
							.split("-")[1]), Integer.valueOf(userInfo
							.getOBJECT().getUserBirthday().split("-")[2]));
		} else {
			Calendar cal = Calendar.getInstance();
			/*
			 * mDialog = new DatePickerDialog(this, null,
			 * cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
			 * cal.get(Calendar.DAY_OF_MONTH));
			 */
			mDialog = new DatePickerDialog(this, null, cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		}
		// 手动设置按钮
		mDialog.setTitle("选择日期");
		mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
						DatePicker datePicker = mDialog.getDatePicker();
						int year = datePicker.getYear();
						int month = datePicker.getMonth();
						int day = datePicker.getDayOfMonth();
						String fianlMonth = "";
						String fianlDay = "";
						if (month < 10) {
							fianlMonth = "0" + month;
						} else {
							fianlMonth = "" + month;
						}
						if (day < 10) {
							fianlDay = "0" + day;
						} else {
							fianlDay = "" + day;
						}
						userInfo.getOBJECT().setUserBirthday(
								year + "-" + fianlMonth + "-" + fianlDay);
						String viewMonth = "";
						if (Integer.valueOf(fianlMonth) + 1 < 10) {
							viewMonth = "0" + (Integer.valueOf(fianlMonth) + 1);
						} else {
							viewMonth = String.valueOf(Integer
									.valueOf(fianlMonth) + 1);
						}
						birthView.setRightText(year + "-" + viewMonth + "-"
								+ fianlDay);
						RequestParams params = new RequestParams();
						params.put("type", "4");
						params.put("content", year + "-" + viewMonth + "-"
								+ fianlDay);
						APPRestClient.post(PersonCompileActivity.this,
								ServiceCode.USER_INFO, params,
								new APPResponseHandler<BaseEntity>(
										BaseEntity.class,
										PersonCompileActivity.this) {
									@Override
									public void onSuccess(BaseEntity result) {

									}

									@Override
									public void onFailure(int errorCode,
											String errorMsg) {
										CustomToast.makeText(ctx, 0, errorMsg, 300).show();
									}

									@Override
									public void onFinish() {

									}
								});
					}
				});
		// 取消按钮，如果不需要直接不设置即可
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
	}

	public void setUserBaseInfo(UUlogin userInfo) {
//		if (!userInfo.getOBJECT().getUserLifePhoto().equals("")) {
//			arrayListPic = userInfo.getOBJECT().getUserLifePhoto().split(",");
//			image_phone2.setRightText("" + arrayListPic.length);
//		}
		if (!userInfo.getOBJECT().getUserEmail().equals(""))
			eamilView.setRightText(userInfo.getOBJECT().getUserEmail());
		if (!userInfo.getOBJECT().getUserName().equals(""))
			compileName.setRightText(userInfo.getOBJECT().getUserName());
		if (!userInfo.getOBJECT().getUserDescription().equals(""))
			compileMy.setRightText(userInfo.getOBJECT().getUserDescription());
		if (!userInfo.getOBJECT().getUserSchool().equals(""))
			schoolView.setRightText(userInfo.getOBJECT().getUserSchool());
		if (!userInfo.getOBJECT().getUserCity().equals(""))
			cityView.setRightText(userInfo.getOBJECT().getUserCity());
		if (!userInfo.getOBJECT().getUserSex().equals("0")) {
			if ("1".equals(userInfo.getOBJECT().getUserSex())) {
				sexView.setRightText("男");
			} else {
				sexView.setRightText("女");
			}
		}

		if (!userInfo.getOBJECT().getUserConstellation().equals(""))
			constellationView.setRightText(userInfo.getOBJECT()
					.getUserConstellation());
		if (!userInfo.getOBJECT().getUserLanguage().equals(""))
			languageView.setRightText(userInfo.getOBJECT().getUserLanguage());
		if (!userInfo.getOBJECT().getUserWork().equals(""))
			professionalView.setRightText(userInfo.getOBJECT().getUserWork());
		if (!userInfo.getOBJECT().getUserBirthday().equals(""))
			birthView.setRightText(userInfo.getOBJECT().getUserBirthday());

	}

	public void setVerifyItem(UUlogin userInfo) {

		if (userInfo.getOBJECT().getUserTelValidate().equals("1")) {
			mobileTextImage.setRightText("已验证");
			mobileTextImage.setClickable(false);
		}

		if (!userInfo.getOBJECT().getUserIdValidate().equals("0")) {
			if (userInfo.getOBJECT().getUserIdValidate().equals("1")) {
				idcardTextImage.setRightText("审核中");
			} else if (userInfo.getOBJECT().getUserIdValidate().equals("2")) {
				idcardTextImage.setRightText("已验证");
			} else {
				idcardTextImage.setRightText("审核失败");
			}
		}

		// if (!userInfo.getOBJECT().getUserCertificateValidate().equals("0")) {
		// if (userInfo.getOBJECT().getUserCertificateValidate().equals("1")) {
		// educationTextImage.setRightText("审核中");
		// } else
		// if(userInfo.getOBJECT().getUserCertificateValidate().equals("2")){
		// educationTextImage.setRightText("已验证");
		// }else{
		// educationTextImage.setRightText("审核失败");
		// }
		// }

		if (!userInfo.getOBJECT().getUserTourValidate().equals("0")) {
			if (userInfo.getOBJECT().getUserTourValidate().equals("1")) {
				legallyTextImage.setRightText("审核中");
			} else if (userInfo.getOBJECT().getUserTourValidate().equals("2")) {
				legallyTextImage.setRightText("已验证");
			} else {
				legallyTextImage.setRightText("审核失败");
			}
		}

		if (!userInfo.getOBJECT().getUserCarValidate().equals("0")) {
			if (userInfo.getOBJECT().getUserCarValidate().equals("1")) {
				carTextImage.setRightText("审核中");
			} else if (userInfo.getOBJECT().getUserCarValidate().equals("2")) {
				carTextImage.setRightText("已验证");
			} else {
				carTextImage.setRightText("审核失败");
			}
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		final String resultPic = intent.getStringExtra("resultPic");
		// 判断是否在Bimp.markBitmap中，无就添加
		if (null != resultPic) {
			new Thread() {
				@Override
				public void run() {
					String status = APPRestClient.post(requestURL, resultPic);
					if (null != status && toRead(status, "STATUS").equals("0")) {
						finalImageUrl = toRead(toRead(status, "OBJECT"),
								"imageURL");
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
			}.start();
			headImage.setHeadPic(resultPic, "local");
			MyApplication.getInstance().getUserInfo().getOBJECT()
					.setUserAvatar(resultPic);
//			if (!userInfo.getOBJECT().getUserLifePhoto().equals("")) {
//				arrayListPic = userInfo.getOBJECT().getUserLifePhoto().split(",");
//				image_phone2.setRightText("已添加" + arrayListPic.length + "张");
//			}
		}

	}
	
	public String toRead(String json, String key) {
		JSONObject jsonObject;
		String status = "";
		try {
			jsonObject = new JSONObject(json);
			status = jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return status;
	}

	protected void getImageFromCamera() {
		Intent getImageByCamera = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		String out_file_path = picPath;
		File dir = new File(out_file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		capturePath = picPath + getFileName() + ".png";
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(capturePath)));
		getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(getImageByCamera, PAIZHAO);

	}

	public static String getFileName() {
		int numcode = (int) ((Math.random() * 9 + 1) * 100000);
		String cc = DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss") + numcode;
		// BigInteger bi = new BigInteger(cc);
		return cc;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityCollector
			.removeSpecifiedActivity("com.uugty.uu.person.PersonValidResultActivity");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				CustomToast.makeText(ctx, 0, "文件上传异常", 300).show();
				break;
			case 2:
				sendServer("22");
				break;
			}
			
			super.handleMessage(msg);
		};
	};
	
	//通知后台更新数据
		private void sendServer(String typeVerificaty){
			RequestParams params = new RequestParams();
			params.put("type", typeVerificaty);
			params.put("content", finalImageUrl);
			APPRestClient.post(PersonCompileActivity.this,
					ServiceCode.USER_INFO, params,
					new APPResponseHandler<BaseEntity>(BaseEntity.class,
							this) {
						@Override
						public void onSuccess(BaseEntity result) {
							
						}

						@Override
						public void onFailure(int errorCode, String errorMsg) {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						}

						@Override
						public void onFinish() {

						}
					});
		}
}
