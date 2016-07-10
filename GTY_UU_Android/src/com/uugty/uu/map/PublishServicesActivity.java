package com.uugty.uu.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.ReleaseRouteImageView;
import com.uugty.uu.common.myview.ReleaseRouteImageView.OnChangeTimeListener;
import com.uugty.uu.common.myview.ReleaseRouteImageView.OnTakePhotoListener;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.common.util.SharedPreferenceUtil;
import com.uugty.uu.db.service.RoadLineService;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.entity.HomeTagEntity;
import com.uugty.uu.entity.HomeTagEntity.Tags.PlayAndBuy;
import com.uugty.uu.entity.RoadEntity;
import com.uugty.uu.entity.RoadLine;
import com.uugty.uu.entity.RoadLineEntity;
import com.uugty.uu.entity.RoadMarker;
import com.uugty.uu.entity.TagsEntity;
import com.uugty.uu.entity.Util;
import com.uugty.uu.guide.ServicesGuideActivity;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.login.AgreementWebActivity;
import com.uugty.uu.map.PublishServicesActivity.PublishAdapter.ViewHolder;
import com.uugty.uu.person.CutPicturceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PublishServicesActivity extends BaseActivity implements
		OnClickListener, OnTakePhotoListener, OnChangeTimeListener {

	private TopBackView titleView;
	private LinearLayout bgdefaultLin, bgLin, addressLin, imageLin;
	private String picPath = CacheFileUtil.carmePaht;// 图片文件夹
	private String pturePath = null;// 图片最终路径
	private final static int PAIZHAO = 100;// 拍照
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=6";// 上传图片接口
	private String bgPicPaht, finalImageUrl;// 背景图片，上传接口返回的最终图片
	private ImageView backgroudCrameImage;
	private EmojiEdite titleEdit, priceEdit;
	private TextView titleEditeWatchTextView, priceEditeWatchTextView,
			addressTextView, agreementTextView;
	private String servicesTitle="", servicesPrice="", servicesAddress="";
	private ReleaseRouteImageView routeImageViewOne;
	// 删除的ReleaseRouteImageView在imageLin中的位置
	private static int imageId;
	private Button addImageBtn, commitBtn;
	// 存储图片选项--图片路径、描述
	public static List<RoadLine> routeMarkLs = new ArrayList<RoadLine>();
	// 存储标签选项
	public static List<TagsEntity> mTagsEntity = new ArrayList<TagsEntity>(1);
	
	private String flag = "1";// 1:背景图片，2：路线图片
	private ScrollView mScrollView;
	private LinearLayout titleLin, priceLin;
	private String roadLineId, roadLineStuatus,from;
	private boolean isPublish = false;
	
	private GridView mGridLayout; //标签
	private LinearLayout mGridLinearLayout;
	private PublishAdapter mAdapter;//标签适配器
	private TextView mPlay;//当地人带你玩
	private TextView mBuy;//当地人代购
	
	private SpotsDialog loadingDialog;//上传图片loading动画
	
	private List<PlayAndBuy> mPlayList;//带玩标签
	
	private List<PlayAndBuy> mBuyList;//代购标签
	
	private boolean isPlay = true;//是否选中带你玩标签，默认选中
	
	

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_publish_services;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if (null != getIntent()) {
			roadLineId = getIntent().getStringExtra("roadLineId");
			roadLineStuatus = getIntent().getStringExtra("roadLineStuatus");
			from = getIntent().getStringExtra("from");
		}
		
		titleView = (TopBackView) findViewById(R.id.publish_services_title);
		titleView.setTitle("发布服务");
		bgdefaultLin = (LinearLayout) findViewById(R.id.publish_services_default_bg_image_lin);
		bgLin = (LinearLayout) findViewById(R.id.publish_services_bg_image_lin);
		backgroudCrameImage = (ImageView) findViewById(R.id.publish_services_bg_image);
		titleEdit = (EmojiEdite) findViewById(R.id.publish_services_title_edittext);
		titleEditeWatchTextView = (TextView) findViewById(R.id.publish_services_title_edittext_watch);
		priceEdit = (EmojiEdite) findViewById(R.id.publish_services_price_edittext);
		priceEditeWatchTextView = (TextView) findViewById(R.id.publish_services_price_edittext_watch);
		addressLin = (LinearLayout) findViewById(R.id.publish_services_address_lin);
		addressTextView = (TextView) findViewById(R.id.publish_services_address_textview);
		routeImageViewOne = (ReleaseRouteImageView) findViewById(R.id.publish_services_image_view_one);
		imageLin = (LinearLayout) findViewById(R.id.publish_services_image_lin);
		addImageBtn = (Button) findViewById(R.id.publish_services_add_image_btn);
		agreementTextView = (TextView) findViewById(R.id.publish_services_agreement);
		commitBtn = (Button) findViewById(R.id.publish_services_commit_btn);
		// 初始化时 有两个ReleaseRouteImageView
		routeMarkLs.add(new RoadLine("", "", ""));
		mScrollView = (ScrollView) findViewById(R.id.publish_services_scrollview);
		titleLin = (LinearLayout) findViewById(R.id.publish_services_title_edittext_lin);
		priceLin = (LinearLayout) findViewById(R.id.publish_services_price_edittext_lin);
		mGridLayout = (GridView) findViewById(R.id.publish_gridview);
		mGridLayout.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridLinearLayout = (LinearLayout) findViewById(R.id.publish_gridview_layout);
		mPlay = (TextView) findViewById(R.id.publish_local_play);
		mBuy = (TextView) findViewById(R.id.publish_local_buy);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		bgdefaultLin.setOnClickListener(this);
		bgLin.setOnClickListener(this);
		titleEdit.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s.toString().trim())) {
					titleEditeWatchTextView.setText(s.length() + "/30个字");
					servicesTitle = s.toString();
				} else {
					titleEditeWatchTextView.setText("30个字");
					servicesTitle = "";
				}
			}
		});
		
		setPricePoint(priceEdit);
		addressLin.setOnClickListener(this);
		routeImageViewOne.setOnTakePhotoListener(this);
		routeImageViewOne.setOnChangeTimeListener(this);
		addImageBtn.setOnClickListener(this);
		agreementTextView.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// 如果为修改路线，先填充数据
		if (!TextUtils.isEmpty(roadLineId) && null == roadLineStuatus) {
			sendRouteRequest();
		}
		//请求标签接口
		getPlayAndBuyTag();
		if (!TextUtils.isEmpty(roadLineId)
				&& !TextUtils.isEmpty(roadLineStuatus)) {
			// 查询数据库
			RoadLineService roadLineService = new RoadLineService(this);
			RoadLineEntity dbRoadLineEntity = roadLineService
					.selectRoutLine(roadLineId);
			bgPicPaht = dbRoadLineEntity.getRoadlineBackground();
			servicesTitle = dbRoadLineEntity.getRoadlineTitle();
			servicesPrice = dbRoadLineEntity.getRoadlinePrice();
			servicesAddress = dbRoadLineEntity.getRoadlineGoalArea();
			imageLin.removeAllViews();
			routeMarkLs.clear();
			routeMarkLs = dbRoadLineEntity.getRoadlineDescribes();
			mTagsEntity.clear();
			mTagsEntity = dbRoadLineEntity.getRoadlineTags();
			// 更新UI
			handler.sendMessage(handler.obtainMessage(6));
		}
		
		//引导页
		int myservices = SharedPreferenceUtil.getInstance(ctx)
				.getInt("myservices", 0);
		if (myservices == 0) {
			Intent intent = new Intent();
			intent.setClass(PublishServicesActivity.this,
					ServicesGuideActivity.class);
			startActivity(intent);
			SharedPreferenceUtil.getInstance(ctx).setInt("myservices", 1);
		} 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.publish_services_bg_image_lin:// 背景图片
			flag = "1";
			PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
					PublishServicesActivity.this);
			builder1.setMessage("选择照片");

			builder1.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(
									PublishServicesActivity.this,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									PublishServicesActivity.class.getName());
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
		case R.id.publish_services_default_bg_image_lin:// 背景图片
			flag = "1";
			PhoneDialog.Builder builder2 = new PhoneDialog.Builder(
					PublishServicesActivity.this);
			builder2.setMessage("选择照片");

			builder2.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(
									PublishServicesActivity.this,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									PublishServicesActivity.class.getName());
							startActivity(intent);
						}
					});

			builder2.setNegativeButton("拍照",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							getImageFromCamera();
						}
					});

			builder2.create().show();
			break;
		case R.id.publish_services_address_lin:// 地点
			Intent intent = new Intent();
			if (!TextUtils.isEmpty(addressTextView.getText().toString())) {
				intent.putExtra("address", addressTextView.getText().toString());
			}
			intent.setClass(this, LocationActivity.class);
			startActivityForResult(intent, 103);
			break;
		case R.id.publish_services_add_image_btn:
			if (imageLin.getChildCount() < 20) {
				ReleaseRouteImageView view = new ReleaseRouteImageView(this);
				imageLin.addView(view);
				view.setOnTakePhotoListener(this);
				view.setOnChangeTimeListener(this);
				routeMarkLs.add(new RoadLine("", "", ""));
			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("一条线路最大只能包含20张图片！");
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			}
			break;
		case R.id.publish_services_agreement:// 协议
			Intent intent1 = new Intent();
			intent1.putExtra("agreement", "release");
			intent1.setClass(this, AgreementWebActivity.class);
			startActivity(intent1);
			break;
		case R.id.publish_services_commit_btn:// 发布
			// .检查数据
			if (checkData()) {
				CustomDialog.Builder dialog = new CustomDialog.Builder(ctx);
				dialog.setTitle("发布路线");
				if (!TextUtils.isEmpty(roadLineId)
						&& TextUtils.isEmpty(roadLineStuatus)) {
					dialog.setMessage("确定要修改路线吗？");
				} else {
					dialog.setMessage("确定要发布路线吗？");
				}
				dialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Gson gson = new Gson();
								RoadLineEntity roadLineEntity = new RoadLineEntity();
								roadLineEntity.setRoadlineBackground(bgPicPaht);
								roadLineEntity.setRoadlineTitle(servicesTitle);
								roadLineEntity.setRoadlinePrice(servicesPrice);
								roadLineEntity
										.setRoadlineGoalArea(servicesAddress);
								// 无关字段，为使后台不报错加上
								// 标签
								for (int i = 0; i < routeMarkLs.size(); i++) {
									RoadLine mark = routeMarkLs.get(i);
									ArrayList<RoadMarker> titleTagLs = new ArrayList<RoadMarker>();
									RoadMarker marker = new RoadMarker();
									marker.setMarkX(String.valueOf(0.0f));
									marker.setMarkY(String.valueOf(0.0f));
									marker.setMarkContent("");
									mark.setDescribeMarks(titleTagLs);
								}

								roadLineEntity
										.setRoadLineDescribes(routeMarkLs);
								//添加标签参数
								roadLineEntity.setRoadlineTags(mTagsEntity);
								roadLineEntity.setRoadlineContent("");
								if (!TextUtils.isEmpty(roadLineId)
										&& !TextUtils.isEmpty(roadLineStuatus)) {
									roadLineEntity.setRoadlineId("");
								} else {
									roadLineEntity.setRoadlineId(roadLineId);
								}
								roadLineEntity.setRoadlineDays("");
								roadLineEntity.setRoadlineInfo("");
								roadLineEntity.setRoadlineFeeContains("");
								roadLineEntity.setRoadlineSpecialMark("");
								roadLineEntity.setRoadlineStartArea("");
								roadLineEntity.setRoadlineCreateDate("");
								String result = (gson.toJson(roadLineEntity));
								if (TextUtils.isEmpty(roadLineId)) {
									toSendRoadline(result);
								}
								if (!TextUtils.isEmpty(roadLineId)
										&& !TextUtils.isEmpty(roadLineStuatus)) {
									toSendRoadline(result);
								}
								if (!TextUtils.isEmpty(roadLineId)
										&& TextUtils.isEmpty(roadLineStuatus)) {
									modifyRoadline(result);
								}

								dialog.dismiss();
							}
						});
				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});
				dialog.create().show();
			}

			break;
		default:
			break;
		}
	}

	protected void getImageFromCamera() {
		MyApplication.getInstance().setKilled(false);
		Intent getImageByCamera = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		String out_file_path = picPath;
		File dir = new File(out_file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		int numcode = (int) ((Math.random() * 9 + 1) * 100000);
		pturePath = picPath + getFileName() + numcode + ".png";
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(pturePath)));
		getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(getImageByCamera, PAIZHAO);

	}

	public static String getFileName() {
		String cc = DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss");
		// BigInteger bi = new BigInteger(cc);
		return cc;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = new Intent();
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PAIZHAO: // 背景图片拍照
				intent.putExtra("picPath", pturePath);
				intent.putExtra("topageFrom",
						PublishServicesActivity.class.getName());
				intent.setClass(PublishServicesActivity.this,
						CutPicturceActivity.class);
				startActivity(intent); // 跳转裁剪
				MyApplication.getInstance().setKilled(true);
				break;
			case 101:
				MyApplication.getInstance().setKilled(true);
				handler.sendMessage(handler.obtainMessage(3, Util.pturePath));
				break;
			case 103:
				servicesAddress = data.getStringExtra("address");
				addressTextView.setText(servicesAddress);
				break;
			default:
				break;
			}
		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}
		final String resultPic = intent.getStringExtra("resultPic");
		// 判断是否在Bimp.markBitmap中，无就添加
		if (null != resultPic) {
			if (flag.equals("1")) {
				new Thread() {
					@Override
					public void run() {
						String status = APPRestClient.post(requestURL,
								resultPic);
						if (null != status
								&& toRead(status, "STATUS").equals("0")) {
							//拿到背景图片地址
							bgPicPaht = toRead(toRead(status, "OBJECT"),
									"imageURL");
							// 背景图片上传接口
							handler.sendMessage(handler.obtainMessage(2,
									resultPic));

						} else {
							handler.sendMessage(handler.obtainMessage(1));
						}
						handler.postDelayed(new Runnable(){   
				            public void run() {  
				                   //延时消失dialog
				            	loadingDialog.dismiss();			            	
				            }  
				        }, 500);
					}
				}.start();

			} else if (flag.equals("2")) {
				handler.sendMessage(handler.obtainMessage(3, resultPic));
			}
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

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				CustomToast.makeText(ctx, 0, "背景图片上传异常", 300).show();
				break;
			case 2:
				bgdefaultLin.setVisibility(View.GONE);
				bgLin.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						APPRestClient.SERVER_IP + "images/roadlineDescribe/"
								+ bgPicPaht, backgroudCrameImage);
				break;
			case 3:
				final String routeImagePic = (String) msg.obj;
				saveMyBitmap(routeImagePic, resizeImage2(routeImagePic));
				new Thread() {
					@Override
					public void run() {
						String status = APPRestClient.post(requestURL,
								routeImagePic);
						if (null != status
								&& toRead(status, "STATUS").equals("0")) {
							finalImageUrl = toRead(toRead(status, "OBJECT"),
									"imageURL");

							handler.sendMessage(handler.obtainMessage(5));

						} else {
							handler.sendMessage(handler.obtainMessage(4));
						}
						handler.postDelayed(new Runnable(){   
				            public void run() {  
				                   //延时消失dialog
				            	loadingDialog.dismiss();			            	
				            }  
				        }, 500);
					}
				}.start();
				break;
			case 4:
				CustomToast.makeText(ctx, 0, "路线第" + imageId + "张图片上传异常", 300)
						.show();
				break;
			case 5:
				ReleaseRouteImageView selectImageView1 = ((ReleaseRouteImageView) (imageLin
						.getChildAt(imageId)));
				routeMarkLs.get(imageId).setDescribeImage(finalImageUrl);
				selectImageView1.setImageView(finalImageUrl,
						routeMarkLs.get(imageId).getDescribeTime());
				selectImageView1.setDisTakeImage();
				break;
			case 6:
				bgdefaultLin.setVisibility(View.GONE);
				bgLin.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						APPRestClient.SERVER_IP + "images/roadlineDescribe/"
								+ bgPicPaht, backgroudCrameImage);
				titleEdit.setText(servicesTitle);
				priceEdit.setText(servicesPrice);
				addressTextView.setText(servicesAddress);
				mAdapter.notifyDataSetChanged();
				for (int i = 0; i < routeMarkLs.size(); i++) {
					RoadLine roadLine = routeMarkLs.get(i);
					ReleaseRouteImageView view = new ReleaseRouteImageView(
							PublishServicesActivity.this);
					imageLin.addView(view);
					view.setOnTakePhotoListener(PublishServicesActivity.this);
					view.setOnChangeTimeListener(PublishServicesActivity.this);
					view.setImageView(roadLine.getDescribeImage(),
							roadLine.getDescribeTime());
					view.setEditext(roadLine.getDescribeArea());
					if (!TextUtils.isEmpty(roadLine.getDescribeImage())) {
						view.setDisTakeImage();
					}

				}

				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void timeChangeListener(int type, ReleaseRouteImageView view,
			String time) {
		// TODO Auto-generated method stub
		for (int i = 0; i < imageLin.getChildCount(); i++) {
			if (imageLin.getChildAt(i) == view) {
				imageId = i;
				break;
			}
		}
		if (type == 1) {// 图片
		}
		if (type == 2) {// 文字
			routeMarkLs.get(imageId).setDescribeArea(time);
		}
	}

	@Override
	public void takePhotoListener(int type, ReleaseRouteImageView view) {
		// TODO Auto-generated method stub
		// 找到删除的view在groupView中位置
		for (int i = 0; i < imageLin.getChildCount(); i++) {
			if (imageLin.getChildAt(i) == view) {
				imageId = i;
				break;
			}
		}
		if (type == 1) {
			flag = "2";
		}
		if (type == 2) {
			// 长按删除操作
			if (imageLin.getChildCount() > 1) {
				imageLin.removeViewAt(imageId);
				routeMarkLs.remove(imageId);
			} else {
				CustomDialog.Builder builder = new CustomDialog.Builder(this);
				builder.setMessage("一条线路至少包含一张图片！");
				builder.setTitle("提示");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.create().show();
			}
		}
	}

	// 保存图片
	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(bitName);
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 压缩
	public static Bitmap resizeImage2(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;// 涓嶅姞杞絙itmap鍒板唴瀛樹腑
		BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inSampleSize = 1;
		if (outWidth != 0 && outHeight != 0 && 480 != 0 && 800 != 0) {
			int sampleSize = (outWidth / 480 + outHeight / 800) / 2;
			options.inSampleSize = sampleSize;
		}
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(path, options);
		int degree = getBitmapDegree(path);
		bm = rotateBitmapByDegree(bm, degree);
		float wid = bm.getWidth();
		float hei = bm.getHeight();
		float ratio = wid / hei;
		BigDecimal decimal = new BigDecimal(ratio);
		routeMarkLs.get(imageId).setDescribeTime(
				"" + decimal.setScale(3, BigDecimal.ROUND_HALF_UP));
		return bm;
	}

	private static int getBitmapDegree(String path) {

		int degree = 0;

		try {

			// 从指定路径下读取图片，并获取其EXIF信息

			ExifInterface exifInterface = new ExifInterface(path);

			// 获取图片的旋转信息

			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,

					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {

			case ExifInterface.ORIENTATION_ROTATE_90:

				degree = 90;

				break;

			case ExifInterface.ORIENTATION_ROTATE_180:

				degree = 180;

				break;

			case ExifInterface.ORIENTATION_ROTATE_270:

				degree = 270;

				break;

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		return degree;

	}

	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {

		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵

		Matrix matrix = new Matrix();

		matrix.postRotate(degree);
		try {

			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {

		}

		if (returnBm == null) {

			returnBm = bm;

		}

		if (bm != returnBm) {

			bm.recycle();

		}

		return returnBm;

	}

	// 发布路线接口
	private void toSendRoadline(final String json) {
		commitBtn.setEnabled(false);
		RequestParams params = new RequestParams();
		params.put("key", json);
		APPRestClient.post(PublishServicesActivity.this, ServiceCode.ROAD_LINE,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomToast.makeText(ctx, 0, "路线发布成功！", 300).show();
						commitBtn.setEnabled(true);
						isPublish = true;
						if (!TextUtils.isEmpty(roadLineId)
								&& !TextUtils
										.isEmpty(roadLineStuatus)) {
							RoadLineService roadLineService = new RoadLineService(
									PublishServicesActivity.this);
							roadLineService
									.deleteData(roadLineId);
						}
						// 设置你的操作事项
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.setClass(
								PublishServicesActivity.this,
								MyServicesActivity.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						commitBtn.setEnabled(true);
						if (errorCode == 3) {
							toSendRoadline(json);
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							CustomDialog.Builder builder = new CustomDialog.Builder(
									PublishServicesActivity.this);
							builder.setMessage(errorMsg);
							builder.setTitle("提示");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											//存储数据
											dialog.dismiss();
										}
									});

							builder.create().show();
						}}

					@Override
					public void onFinish() {

					}
				});
	}

	private boolean checkData() {
		boolean checkenData = true;
		// 检查背景图片
		if (TextUtils.isEmpty(bgPicPaht)) {
			CustomToast.makeText(ctx, 0, "背景图片为空", 300).show();
			mScrollView.smoothScrollTo(0, 0);
			return false;
		}
		if (TextUtils.isEmpty(servicesTitle.trim())) {
			CustomToast.makeText(ctx, 0, "路线标题为空", 300).show();
			mScrollView.smoothScrollTo(titleLin.getLeft(), titleLin.getTop());
			return false;
		}
		if (TextUtils.isEmpty(servicesPrice.trim())) {
			CustomToast.makeText(ctx, 0, "路线价格为空", 300).show();
			mScrollView.smoothScrollTo(priceLin.getLeft(), priceLin.getTop());
			return false;
		}if (Double.valueOf(servicesPrice)<0.01) {
			CustomToast.makeText(ctx, 0, "路线价格最小为0.01", 300).show();
			mScrollView.smoothScrollTo(priceLin.getLeft(), priceLin.getTop());
			return false;
		}
		if (TextUtils.isEmpty(servicesAddress.trim())) {
			CustomToast.makeText(ctx, 0, "路线地点为空", 300).show();
			mScrollView.smoothScrollTo(addressLin.getLeft(),
					addressLin.getTop());
			return false;
		}
		if (mTagsEntity.size() == 0) {
			CustomToast.makeText(ctx, 0, "请选择服务类别", 300).show();
			mScrollView.smoothScrollTo(addressLin.getLeft(),
					addressLin.getTop());
			return false;
		}
		// 检查routeMarkLs
		for (int i = 0; i < routeMarkLs.size(); i++) {
			RoadLine roadline = routeMarkLs.get(i);
			if (TextUtils.isEmpty(roadline.getDescribeImage().trim())) {
				CustomToast.makeText(ctx, 0, "路线的第" + (i + 1) + "张图片为空", 300)
						.show();
				mScrollView.smoothScrollTo(
						imageLin.getLeft()
								+ ((ReleaseRouteImageView) (imageLin
										.getChildAt(i))).getLeft(),
						imageLin.getTop()
								+ ((ReleaseRouteImageView) (imageLin
										.getChildAt(i))).getTop());
				return false;
			}
			if (TextUtils.isEmpty(roadline.getDescribeArea().trim())) {
				CustomToast
						.makeText(ctx, 0, "路线的第" + (i + 1) + "张图片的描述为空", 300)
						.show();
				int ImageViewHeightTotal = 0;
				for (int n = i; n >= 0; n--) {
					ImageViewHeightTotal += ((ReleaseRouteImageView) (imageLin
							.getChildAt(n))).getImageViewHeight();
					if (n - 1 >= 0) {
						ImageViewHeightTotal = ImageViewHeightTotal
								+ ((ReleaseRouteImageView) (imageLin
										.getChildAt(n - 1)))
										.geteditTextHeight();
					}
				}
				mScrollView.smoothScrollTo(imageLin.getLeft()
						+ ImageViewHeightTotal, imageLin.getTop()
						+ ImageViewHeightTotal);
				return false;
			}

		}

		return checkenData;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 缓存数据数据库
		if (TextUtils.isEmpty(roadLineId)
				&& MyApplication.getInstance().isLogin()&&!isEmptyRouteData()&& !isPublish) {
			// 如果路线Id为空，insert;
			RoadLineEntity roadLineEntity = new RoadLineEntity();
			roadLineEntity.setRoadlineBackground(bgPicPaht);
			roadLineEntity.setRoadlineTitle(servicesTitle);
			roadLineEntity.setRoadlinePrice(servicesPrice);
			roadLineEntity.setRoadlineGoalArea(servicesAddress);
			roadLineEntity.setRoadLineDescribes(routeMarkLs);
			roadLineEntity.setUserId(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserId());
			int numcode = (int) ((Math.random() * 9 + 1) * 100000);
			roadLineEntity.setRoadlineId(getFileName() + numcode);
			String cc = DateUtil.dateFormat(new Date(), "yyyyMMdd");
			roadLineEntity.setRoadlineCreateDate(cc);
			RoadLineService roadLineService = new RoadLineService(this);
			roadLineService.saveRoadLine(roadLineEntity);
		}
		if (!TextUtils.isEmpty(roadLineId)
				&& !TextUtils.isEmpty(roadLineStuatus)
				&& MyApplication.getInstance().isLogin() && !isPublish) {
			// 如果路线Id不为空roadLineStuatus不为空，先删，后插入;
			RoadLineService roadLineService = new RoadLineService(this);
			roadLineService.deleteData(roadLineId);
			RoadLineEntity roadLineEntity = new RoadLineEntity();
			roadLineEntity.setRoadlineBackground(bgPicPaht);
			roadLineEntity.setRoadlineTitle(servicesTitle);
			roadLineEntity.setRoadlinePrice(servicesPrice);
			roadLineEntity.setRoadlineGoalArea(servicesAddress);
			roadLineEntity.setRoadLineDescribes(routeMarkLs);
			roadLineEntity.setUserId(MyApplication.getInstance().getUserInfo()
					.getOBJECT().getUserId());
			int numcode = (int) ((Math.random() * 9 + 1) * 100000);
			roadLineEntity.setRoadlineId(getFileName() + numcode);
			String cc = DateUtil.dateFormat(new Date(), "yyyyMMdd");
			roadLineEntity.setRoadlineCreateDate(cc);

			roadLineService.saveRoadLine(roadLineEntity);
		}

		routeMarkLs.clear();
		if(TextUtils.isEmpty(from)){
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(
					PublishServicesActivity.this,
					MyServicesActivity.class);
			startActivity(intent);
		}
	}

	private void sendRouteRequest() {

		RequestParams params = new RequestParams();
		params.put("roadlineId", roadLineId);
		APPRestClient.post(this, ServiceCode.ROAD_LINE_DETAIL_MESSAGE, params,
				new APPResponseHandler<RoadEntity>(RoadEntity.class, this) {
					@Override
					public void onSuccess(final RoadEntity result) {
						if (null != result) {
							// 数据
							bgPicPaht = result.getOBJECT()
									.getRoadlineBackground();
							servicesTitle = result.getOBJECT()
									.getRoadlineTitle();
							servicesPrice = result.getOBJECT()
									.getRoadlinePrice();
							servicesAddress = result.getOBJECT()
									.getRoadlineGoalArea();
							imageLin.removeAllViews();
							routeMarkLs.clear();
							routeMarkLs = result.getOBJECT()
									.getRoadlineDescribes();
							if(result.getOBJECT().getTags().size() > 0){
								mTagsEntity.clear();
								TagsEntity tag = new TagsEntity(result.getOBJECT().getTags().get(0).getTagId(),
										result.getOBJECT().getTags().get(0).getTagName());
								mTagsEntity.add(tag);
							}
							// 更新UI
							handler.sendMessage(handler.obtainMessage(6));
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRouteRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(
									PublishServicesActivity.this)
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

	// 修改路线
	private void modifyRoadline(final String json) {
		commitBtn.setEnabled(false);
		RequestParams params = new RequestParams();
		params.put("key", json);
		APPRestClient.post(PublishServicesActivity.this,
				ServiceCode.ROAD_LINE_MODIFY, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
						
						CustomToast.makeText(ctx, 0, "路线修改成功！", 300).show();
						commitBtn.setEnabled(true);
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.setClass(
								PublishServicesActivity.this,
								MyServicesActivity.class);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						commitBtn.setEnabled(true);
						if (errorCode == 3) {
							modifyRoadline(json);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						CustomDialog.Builder builder = new CustomDialog.Builder(
								PublishServicesActivity.this);
						builder.setMessage(errorMsg);
						builder.setTitle("提示");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});

						builder.create().show();
					}}

					@Override
					public void onFinish() {

					}
				});
	}

	// 判断是否全部为空值
	private boolean isEmptyRouteData() {
		if (!TextUtils.isEmpty(bgPicPaht) || !TextUtils.isEmpty(servicesTitle)
				|| !TextUtils.isEmpty(servicesPrice)
				|| !TextUtils.isEmpty(servicesAddress))
			return false;
		for (int i = 0; i < routeMarkLs.size(); i++) {
			RoadLine roadline = routeMarkLs.get(i);
			if (!TextUtils.isEmpty(roadline.getDescribeImage()))
				return false;
			if (!TextUtils.isEmpty(roadline.getDescribeArea()))
				return false;
		}

		return true;
	}
	
	/**
	 * 获取标签
	 */
	private void getPlayAndBuyTag() {
		
		if(loadingDialog!=null){
			loadingDialog.show();
		}else{
			loadingDialog = new SpotsDialog(this);
			loadingDialog.show();
		}
		RequestParams params = new RequestParams();
		APPRestClient.post(ctx, ServiceCode.TAG_LIST, params,
				new APPResponseHandler<HomeTagEntity>(HomeTagEntity.class,
						ctx) {
					@Override
					public void onSuccess(final HomeTagEntity result) {
						
						if(result != null){
							mAdapter = new PublishAdapter(ctx, result,true);
							mGridLayout.setAdapter(mAdapter);
							
							mPlay.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									isPlay = true;
									mPlay.setTextColor(getResources().getColor(R.color.login_text_color));
									mBuy.setTextColor(getResources().getColor(R.color.black));
									mAdapter = new PublishAdapter(ctx, result,true);
									mAdapter.notifyDataSetChanged();
									mGridLayout.setAdapter(mAdapter);
								}
							});
							
							mBuy.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									isPlay = false;
									mPlay.setTextColor(getResources().getColor(R.color.black));
									mBuy.setTextColor(getResources().getColor(R.color.login_text_color));
									mAdapter = new PublishAdapter(ctx, result,false);
									mAdapter.notifyDataSetChanged();
									mGridLayout.setAdapter(mAdapter);
								}
							});
						}
						//外部定义一个引用，保证每次后面的灰覆盖前面的
						final TagsEntity tag = new TagsEntity("", "");
						mGridLayout.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								ViewHolder holder = (ViewHolder) view.getTag();
								// 重置，确保最多只有一项被选中
								for (String key : mAdapter.states.keySet()) {
									mAdapter.states.put(key, false);

								}
								//改变状态值
								mAdapter.states.put(String.valueOf(position), true);
								if(mTagsEntity.size() > 0){
									mTagsEntity.clear();
								}
								
								if(isPlay){
									tag.setTagId(mPlayList.get(position).getTagId());
									tag.setTagName(mPlayList.get(position).getTagName());
									mTagsEntity.add(tag);
								}else{
									tag.setTagId(mBuyList.get(position).getTagId());
									tag.setTagName(mBuyList.get(position).getTagName());
									mTagsEntity.add(tag);
								}
								//直接刷新
//								mGridLayout.setAdapter(mAdapter);
								//刷新adapter的值
								PublishAdapter pa = (PublishAdapter)mGridLayout.getAdapter();
								mGridLayout.setAdapter(pa);
							}
						});
						
						new Handler().postDelayed(new Runnable(){   
				            public void run() {  
				                   //延时消失dialog
				            	loadingDialog.dismiss();			            	
				            }  
				        }, 500);

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						CustomToast.makeText(ctx, 0, errorMsg, 300)
								.show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ctx)
									.setTitle("提示")
									.setMessage("服务器连接失败！")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													ImageLoader.getInstance()
															.clearMemoryCache(); // 清除内存缓存
													MyApplication.getInstance()
															.clearLoginData();
													ActivityCollector
															.finishAll();
													dialog.dismiss();
												}
											}).show();
						}
					}

					@Override
					public void onFinish() {
					}
				});
	}
	
	public void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s)) {
					priceEditeWatchTextView.setText(s.length() + "/6个字");
					servicesPrice = s.toString();
				} else {
					priceEditeWatchTextView.setText("6个字");
					servicesPrice = "";
				}
			}

		});

	}
	
	class PublishAdapter extends BaseAdapter {
		private Context context;
		private List<PlayAndBuy> tagList;
		private HashMap<String,Boolean> states=new HashMap<String,Boolean>();

		public PublishAdapter(Context context, HomeTagEntity entity , boolean isPlay) {
			super();
			this.context = context;
			if(isPlay){
				this.tagList = entity.getOBJECT().getTypeandTags().get(0).getLIST();
				mPlayList = tagList;
			}else{
				this.tagList = entity.getOBJECT().getTypeandTags().get(1).getLIST();
				mBuyList = tagList;
			}
		}

		@Override
		public int getCount() {
			return tagList.size();
		}

		@Override
		public Object getItem(int position) {
			return tagList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.publishroadline_gridview, null);
				holder.meta = (TextView) view
						.findViewById(R.id.road_meta);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.meta.setText(tagList.get(position).getTagName());
			boolean res = false;
			if (mAdapter.states.get(String.valueOf(position)) == null || mAdapter.states.get(String.valueOf(position)) == false) {
				res = false;
				mAdapter.states.put(String.valueOf(position), false);
			} else{
				res = true;
			}
			if(res){
				holder.meta.setBackgroundColor(getResources().getColor(R.color.login_text_color));
				holder.meta.setTextColor(getResources().getColor(R.color.white));
			}
			if(mTagsEntity.size() >0){
				if(mTagsEntity.get(0).getTagId().equals(tagList.get(position).getTagId())){
					holder.meta.setBackgroundColor(getResources().getColor(R.color.login_text_color));
					holder.meta.setTextColor(getResources().getColor(R.color.white));
				}
			}
			
			return view;
		}

		class ViewHolder {
			TextView meta;
		}
	}
}
