package com.uugty.uu.person;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.dialog.loading.SpotsDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.RoundAngleImageView;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.map.PhoneDialog;
import com.uugty.uu.modeal.UUlogin;

public class PersonPhotoVeriActivity extends BaseActivity implements
		OnClickListener {

	private String verifyItem;
	private TextView titelTextView, verifyResultText, noticeText;
	private RoundAngleImageView exampleImageView, useImageView;
	private LinearLayout tip_back;// 返回
	private ImageView takeCrameImageView;
	private Button rightTextView;
	// 拍照图片路径
	private String picPath = CacheFileUtil.carmePaht;
	private String pturePath = null;
	private final static int PAIZHAO = 100;// 拍照
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE;
	// 上传接口的type
	private String type = "1";
	// loading等待
	private SpotsDialog loadingDialog;
	// 个人信息
	private UUlogin userInfo;
	// 点击状态
	private String clickStaut = "";
	private String finalImageUrl;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.person_photo_verification;
	}

	@Override
	protected void initGui() {
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PersonPhotoVeriActivity.class.getName());
			intent.setClass(PersonPhotoVeriActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		userInfo = MyApplication.getInstance().getUserInfo();
		}
		titelTextView = (TextView) findViewById(R.id.person_photo_top_text);
		noticeText = (TextView) findViewById(R.id.person_photo_notice_text);
		exampleImageView = (RoundAngleImageView) findViewById(R.id.person_photo_example_image);
		tip_back = (LinearLayout) findViewById(R.id.tabar_back);
		takeCrameImageView = (ImageView) findViewById(R.id.person_photo_default_crame_image);
		useImageView = (RoundAngleImageView) findViewById(R.id.person_photo_use_image);
		rightTextView = (Button) findViewById(R.id.person_photo_top_right_text);
		verifyResultText = (TextView) findViewById(R.id.person_photo_status_text);
		if (null != getIntent()) {
			verifyItem = getIntent().getStringExtra("verifyItem");
		}
		if (null != verifyItem && !verifyItem.equals("")) {
			if (verifyItem.equals("idcard")) {
				titelTextView.setText("身份证认证");
				type = "1";
				iamgeShowByStatus("idcard", Integer.valueOf(userInfo
						.getOBJECT().getUserIdValidate()));
			} else if (verifyItem.equals("education")) {
				titelTextView.setText("学历证认证");
				noticeText.setText("按照示例拍摄或者上传学历证");
				type = "2";
				iamgeShowByStatus("education", Integer.valueOf(userInfo
						.getOBJECT().getUserCertificateValidate()));
			} else if (verifyItem.equals("legally")) {
				titelTextView.setText("导游证认证");
				noticeText.setText("按照示例拍摄或者上传导游证");
				type = "3";
				iamgeShowByStatus("legally", Integer.valueOf(userInfo
						.getOBJECT().getUserTourValidate()));
			} else if (verifyItem.equals("car")) {
				titelTextView.setText("驾驶证认证");
				noticeText.setText("按照示例拍摄或者上传驾驶证");
				type = "4";
				iamgeShowByStatus("car", Integer.valueOf(userInfo.getOBJECT()
						.getUserCarValidate()));
			}
		}
	}

	@Override
	protected void initAction() {
		tip_back.setOnClickListener(this);
		takeCrameImageView.setOnClickListener(this);
		useImageView.setOnClickListener(this);
		rightTextView.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	public void iamgeShowByStatus(String type, int status) {
		switch (status) {
		case 0:
			verifyResultText.setVisibility(View.GONE);
			if (type.equals("idcard")) {
				ImageLoader
						.getInstance()
						.displayImage(
								"drawable://"
										+ R.drawable.person_valid_sample_id_iamge,
								exampleImageView);
			} else if (type.equals("education")) {
				ImageLoader
						.getInstance()
						.displayImage(
								"drawable://"
										+ R.drawable.person_valid_sample_education_iamge,
								exampleImageView);
			} else if (type.equals("legally")) {
				ImageLoader.getInstance().displayImage(
						"drawable://"
								+ R.drawable.person_valid_sample_legally_iamge,
						exampleImageView);
			} else if (type.equals("car"))
				ImageLoader
						.getInstance()
						.displayImage(
								"drawable://"
										+ R.drawable.person_valid_jia_drive_example_iamge,
								exampleImageView);
			break;
		case 1:
			verifyResultText.setText("审核中");
			clickStaut = "1";
			if (type.equals("idcard")) {
				if (userInfo.getOBJECT().getUserIdentity().contains("images")) {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("education")) {
				if (userInfo.getOBJECT().getUserCertificate()
						.contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									APPRestClient.SERVER_IP
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									"file://"
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("legally")) {
				if (userInfo.getOBJECT().getUserTourCard().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("car")) {
				if (userInfo.getOBJECT().getUserCar().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			}

			break;
		case 2:
			verifyResultText.setText("审核通过");
			clickStaut = "2";
			if (type.equals("idcard")) {
				if (userInfo.getOBJECT().getUserIdentity().contains("images")) {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("education")) {
				if (userInfo.getOBJECT().getUserCertificate()
						.contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									APPRestClient.SERVER_IP
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									"file://"
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else

			if (type.equals("legally")) {
				if (userInfo.getOBJECT().getUserTourCard().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}

			} else if (type.equals("car")) {
				if (userInfo.getOBJECT().getUserCar().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}

			}
			break;
		case 3:
			verifyResultText.setText("审核失败");
			clickStaut = "3";
			if (type.equals("idcard")) {
				if (userInfo.getOBJECT().getUserIdentity().contains("images")) {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader.getInstance().displayImage(
							"drawable://"
									+ R.drawable.person_valid_sample_id_iamge,
							exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserIdentity(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("education")) {
				if (userInfo.getOBJECT().getUserCertificate()
						.contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									APPRestClient.SERVER_IP
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_education_iamge,
									exampleImageView);
					ImageLoader.getInstance()
							.displayImage(
									"file://"
											+ userInfo.getOBJECT()
													.getUserCertificate(),
									useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("legally")) {
				if (userInfo.getOBJECT().getUserTourCard().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_sample_legally_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserTourCard(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			} else if (type.equals("car")) {
				if (userInfo.getOBJECT().getUserCar().contains("images")) {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							APPRestClient.SERVER_IP
									+ userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				} else {
					ImageLoader
							.getInstance()
							.displayImage(
									"drawable://"
											+ R.drawable.person_valid_jia_drive_example_iamge,
									exampleImageView);
					ImageLoader.getInstance().displayImage(
							"file://" + userInfo.getOBJECT().getUserCar(),
							useImageView);
					takeCrameImageView.setVisibility(View.GONE);
				}
			}
			break;
		default:
			break;
		}
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
			tip_back.setClickable(false);
			finish();
			break;
		case R.id.person_photo_default_crame_image:
			// 谈出照片框
			PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
					PersonPhotoVeriActivity.this);
			builder1.setMessage("选择照片");

			builder1.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(
									PersonPhotoVeriActivity.this,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									PersonPhotoVeriActivity.class.getName());
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
		case R.id.person_photo_use_image:
			// 谈出照片框
			if (clickStaut.equals("1") || clickStaut.equals("2")) {
				CustomDialog.Builder builder3 = new CustomDialog.Builder(this);
				builder3.setMessage("状态为审核中或审核成功,不允许修改!");
				builder3.setTitle("提示");
				builder3.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder3.create().show();
			} else {
				PhoneDialog.Builder builder2 = new PhoneDialog.Builder(
						PersonPhotoVeriActivity.this);
				builder2.setMessage("选择照片");

				builder2.setPositiveButton("相册",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 设置你的操作事项
								Intent intent = new Intent(
										PersonPhotoVeriActivity.this,
										PhoneimageActivity.class);
								intent.putExtra("topageFrom",
										PersonPhotoVeriActivity.class.getName());
								startActivity(intent);
							}
						});

				builder2.setNegativeButton("拍照",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								getImageFromCamera();
							}
						});
				builder2.create().show();
			}
			break;
		case R.id.person_photo_top_right_text:
			if (clickStaut.equals("1") || clickStaut.equals("2")) {
				CustomDialog.Builder builder3 = new CustomDialog.Builder(this);
				builder3.setMessage("状态为审核中或审核成功,不允许修改!");
				builder3.setTitle("提示");
				builder3.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder3.create().show();
			} else {
				if (null != pturePath && !pturePath.equals("")) {
					if(loadingDialog!=null){
						loadingDialog.show();
					}else{
					loadingDialog = new SpotsDialog(this);
					loadingDialog.show();
					}
					new Thread() {
						@Override
						public void run() {
							String status = APPRestClient.post(requestURL
									+ "?type=" + type, pturePath);
							if (null != status && toRead(status, "STATUS").equals("0")) {
								finalImageUrl = toRead(toRead(status, "OBJECT"),
										"imageURL");
								Message msg = new Message();
								msg.what = 3;
								handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = 2;
								handler.sendMessage(msg);
							}
						}
					}.start();

				} else {
					CustomDialog.Builder builder3 = new CustomDialog.Builder(
							this);
					builder3.setMessage("请先选择图片!");
					builder3.setTitle("提示");
					builder3.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					builder3.create().show();
				}

			}
			break;
		default:
			break;
		}
	}
	
	//通知后台更新数据
	private void sendServer(String typeVerificaty){
		RequestParams params = new RequestParams();
		params.put("type", typeVerificaty);
		params.put("content", finalImageUrl);
		APPRestClient.post(PersonPhotoVeriActivity.this,
				ServiceCode.USER_INFO, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class,
						this) {
					@Override
					public void onSuccess(BaseEntity result) {
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
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

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		pturePath = intent.getStringExtra("picSelect");
		// 判断是否在Bimp.markBitmap中，无就添加
		if (null != pturePath) {
			Message msg = new Message();
			msg.what = 1;
			handler1.sendMessage(msg);
			/*ImageLoader.getInstance().displayImage("file://" + resultPic,
					useImageView);
			takeCrameImageView.setVisibility(View.GONE);*/
		}

	}

	public String toRead(String json) {
		JSONObject jsonObject;
		String status = "";
		try {
			jsonObject = new JSONObject(json);
			status = jsonObject.getString("STATUS");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return status;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PAIZHAO:
				/*
				 * intent.putExtra("picPath", pturePath);
				 * intent.putExtra("topageFrom",
				 * PersonPhotoVeriActivity.class.getName());
				 * intent.setClass(PersonPhotoVeriActivity.this,
				 * CutPicturceActivity.class); startActivity(intent);
				 */
				Message msg = new Message();
				msg.what = 1;
				handler1.sendMessage(msg);
				
				break;
			default:
				break;
			}
		}
	}

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
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

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

	//
	protected void getImageFromCamera() {
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

	public String getFileName() {
		String cc = DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss");
		// BigInteger bi = new BigInteger(cc);
		return cc;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (loadingDialog != null) {
				loadingDialog.dismiss();
			}
			Intent tointent = new Intent();
			switch (msg.what) {
			case 1:
				tointent.putExtra("STATUS", "SUCCESS");
				tointent.putExtra("verifyItem", verifyItem);
				tointent.putExtra("picPath", pturePath);
				tointent.setClass(PersonPhotoVeriActivity.this,
						PersonValidResultActivity.class);
				startActivity(tointent);
				break;
			case 2:
				tointent.putExtra("STATUS", "FAILE");
				tointent.putExtra("verifyItem", verifyItem);
				tointent.putExtra("picPath", pturePath);
				tointent.setClass(PersonPhotoVeriActivity.this,
						PersonValidResultActivity.class);
				startActivity(tointent);
				break;
			case 3:
				if(type.equals("1")) sendServer("17");
				if(type.equals("2")) sendServer("18");
				if(type.equals("3")) sendServer("21");
				if(type.equals("4")) sendServer("19");
				break;
			}
			
			super.handleMessage(msg);
		};
	};
	
	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				saveMyBitmap(pturePath, resizeImage2(pturePath));
				ImageLoader.getInstance().displayImage("file://" + pturePath,
						useImageView);
				takeCrameImageView.setVisibility(View.GONE);
				break;
			}
		};
	};

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
	
}
