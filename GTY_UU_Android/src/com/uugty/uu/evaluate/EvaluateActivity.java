package com.uugty.uu.evaluate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.SelectPictureActivity;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.entity.EvaluaBaseEntity;
import com.uugty.uu.entity.EvaluationDetailEntity;
import com.uugty.uu.map.PhoneDialog;


public class EvaluateActivity extends BaseActivity implements
		OnRatingBarChangeListener, OnClickListener {

	private String orderId, name, headPic, isEval;
	private TextView nameTextView;
	private RatingBar serviceBar, freshBar, costPerforBar, compreBar;
	private EmojiEdite commentEditText;
	private Button sendBtn;
	private SimpleDraweeView headImage;
	private ImageView backImageView;
	private GridView evaluateGrid;
	private String pturePath = null;
	private String picPath = CacheFileUtil.carmePaht;
	private final static int PAIZHAO = 100;// 拍照
	private final static int PHONE = 110;// 手机相册
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=7";
	private List<String> finalList = new ArrayList<String>();// 保留最终图片
	// 图片比例
	private static List<String> ratiolist = new ArrayList<String>();
	private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示
	private PublishAdapter adapter;
	private String finalImageUrl;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_evaluate;
	}

	@Override
	protected void initGui() {
		MyApplication.getInstance().setKilled(false);
		if (null != getIntent()) {
			Bundle bundle = new Bundle();
			bundle = this.getIntent().getExtras();
			orderId = bundle.getString("orderId");
			name = bundle.getString("name");
			headPic = bundle.getString("headPic");
			isEval = bundle.getString("isEval");
		}
		backImageView = (ImageView) findViewById(R.id.evaluate_back_image);
		headImage = (SimpleDraweeView) findViewById(R.id.order_evaluate_image_id);
		nameTextView = (TextView) findViewById(R.id.order_evaluate_name_id);
		serviceBar = (RatingBar) findViewById(R.id.room_ratingbar_one);
		freshBar = (RatingBar) findViewById(R.id.room_ratingbar_two);
		costPerforBar = (RatingBar) findViewById(R.id.room_ratingbar_three);
		compreBar = (RatingBar) findViewById(R.id.room_ratingbar_four);
		commentEditText = (EmojiEdite) findViewById(R.id.order_eval_comment);
		sendBtn = (Button) findViewById(R.id.order_evalu_send_button);
		evaluateGrid = (GridView) findViewById(R.id.order_evaluae_grid);

		if (null != headPic && !headPic.equals("")) {
			headImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + headPic));

		}
		if (null != name && !name.equals("")) {
			nameTextView.setText(name);
		}
		// 没有评价
		if (!"0".equals(isEval)) {
			// 订单评价详情
			sendOrderEvaluationDetail();
		}
		adapter = new PublishAdapter(ctx, finalList);
		evaluateGrid.setAdapter(adapter);
	}

	@Override
	protected void initAction() {
		serviceBar.setOnRatingBarChangeListener(this);
		freshBar.setOnRatingBarChangeListener(this);
		costPerforBar.setOnRatingBarChangeListener(this);
		sendBtn.setOnClickListener(this);

		backImageView.setOnClickListener(this);

		evaluateGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//
				if (finalList != null && position < finalList.size()) {
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(100);
					if (isShowDelete) {
						isShowDelete = false;
					} else {
						isShowDelete = true;
					}
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		});

		evaluateGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (finalList != null && position < finalList.size()) {
					imageBrower(position, (ArrayList<String>) finalList);
				} else {
					PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
							EvaluateActivity.this);
					builder1.setMessage("选择照片");

					builder1.setPositiveButton("相册",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									// 设置你的操作事项
									Intent intent = new Intent(
											EvaluateActivity.this,
											SelectPictureActivity.class);
									intent.putExtra(
											SelectPictureActivity.INTENT_MAX_NUM,
											4 - ratiolist.size());
									startActivityForResult(intent, PHONE);

								}
							});

					builder1.setNegativeButton(
							"拍照",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									getImageFromCamera();
								}
							});

					builder1.create().show();
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	private void sendOrderEvaluation() {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id
		params.add("serviceIndex", String.valueOf(serviceBar.getRating()));
		params.add("freshIndex", String.valueOf(freshBar.getRating()));
		params.add("ratioIndex", String.valueOf(costPerforBar.getRating()));
		params.add("totalIndex", String.valueOf(compreBar.getRating()));
		params.add("commentContent", commentEditText.getText().toString()
				.trim());
		String saidPhoto="";
		if (finalList != null && finalList.size() > 0) { // 图片地址
			for (int i = 0; i < finalList.size(); i++) {
				if (i == finalList.size() - 1) {
					saidPhoto += finalList.get(i);
				} else {
					saidPhoto += finalList.get(i) + ",";
				}
			}
			
		}
		params.add("commentImages", saidPhoto);

		APPRestClient.post(this, ServiceCode.ORDER_EVALUATION, params,
				new APPResponseHandler<EvaluaBaseEntity>(
						EvaluaBaseEntity.class, this) {
					@Override
					public void onSuccess(EvaluaBaseEntity result) {
						finish();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendOrderEvaluation();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(EvaluateActivity.this)
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

						}
					}

					@Override
					public void onFinish() {
					}
				});
	}

	private void sendOrderEvaluationDetail() {
		RequestParams params = new RequestParams();
		params.add("orderId", orderId); // 路线id

		APPRestClient.post(this, ServiceCode.ORDER_EVALUATION_DETAIL, params,
				new APPResponseHandler<EvaluationDetailEntity>(
						EvaluationDetailEntity.class, this) {
					@Override
					public void onSuccess(EvaluationDetailEntity result) {
						serviceBar.setRating(Float.valueOf(result.getOBJECT()
								.getServiceIndex()));
						freshBar.setRating(Float.valueOf(result.getOBJECT()
								.getFreshIndex()));
						costPerforBar.setRating(Float.valueOf(result
								.getOBJECT().getRatioIndex()));
						compreBar.setRating(Float.valueOf(result.getOBJECT()
								.getTotalIndex()));
						commentEditText.setText(result.getOBJECT()
								.getCommentContent());
						String[] strs = result.getOBJECT().getCommentImages()
								.split(",");
						// 将strs转换成finalList,显示
						for(int i=0;i<strs.length;i++){
							finalList.add(strs[i]);
							ratiolist.add(strs[i]);
						}
						adapter.notifyDataSetChanged();

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendOrderEvaluationDetail();
						} else {
							CustomToast.makeText(ctx, 0, errorMsg, 300).show();
							if (errorCode == -999) {
								new AlertDialog.Builder(EvaluateActivity.this)
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

						}
					}

					@Override
					public void onFinish() {
					}
				});
	}

	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(ctx, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "0");
		startActivity(intent);
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		int f1 = (int) serviceBar.getRating();
		int f2 = (int) freshBar.getRating();
		int f3 = (int) costPerforBar.getRating();
		int f4 = (f1 + f2 + f3) / 3;
		compreBar.setRating(f4);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ratiolist.clear();
		finalList.clear();
		MyApplication.getInstance().setKilled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.order_evalu_send_button:
			if (String.valueOf(compreBar.getRating()).equals("0.0")
					&& String.valueOf(compreBar.getRating()) != null) {
				CustomToast.makeText(ctx, 0, "尚未评星", 500).show();
			} else {
				sendOrderEvaluation();
			}
			break;
		case R.id.evaluate_back_image:
			finish();
			break;

		default:
			break;
		}
	}

	protected void getImageFromCamera() {
		Intent getImageByCamera = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		String out_file_path = picPath;
		File dir = new File(out_file_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		pturePath = picPath + System.currentTimeMillis() + ".png";
		getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(pturePath)));
		getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(getImageByCamera, PAIZHAO);

	}

	// 重写Result方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PAIZHAO:
				handler2.sendMessage(handler2.obtainMessage(1, pturePath));
				break;
			case PHONE:
				ArrayList<String> temp = (ArrayList<String>) data
						.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
				for (int i = 0; i < temp.size(); i++) {
					handler2.sendMessage(handler2.obtainMessage(1, temp.get(i)));
				}
				break;

			default:
				break;
			}
		}

	}

	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				final String resultPic = (String) msg.obj;
				saveMyBitmap(resultPic, resizeImage2(resultPic));//会毁掉原图，建议修改
				if (resultPic != null) {
					new Thread() {
						@Override
						public void run() {
							String status = APPRestClient.post(requestURL,
									resultPic);
							if (null != status
									&& toRead(status, "STATUS").equals("0")) {
								finalImageUrl = toRead(
										toRead(status, "OBJECT"), "imageURL");
								finalList.add(finalImageUrl);
								handler.sendMessage(handler.obtainMessage(2));

							} else {
								ratiolist.remove(ratiolist.size() - 1);
								handler.sendMessage(handler.obtainMessage(1));
							}
						}
					}.start();
				}

				break;
			}
			;
		};
	};
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				CustomToast.makeText(ctx, 0, "文件上传异常", 300).show();
				break;

			case 2:
				adapter = new PublishAdapter(ctx, finalList);
				evaluateGrid.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;

			}
		};
	};

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
		ratiolist.add("" + decimal.setScale(3, BigDecimal.ROUND_HALF_UP));
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

	class PublishAdapter extends BaseAdapter {
		private Context context;
		private List<String> ls = new ArrayList<String>();

		public PublishAdapter(Context context, List<String> ls) {
			super();
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (ls.size() == 4) {
				return ls.size();
			} else {
				return ls == null ? 1 : ls.size() + 1;
			}
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
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.evaluate_grid_item, null);
				holder.img = (SimpleDraweeView) view
						.findViewById(R.id.publish_item_img);
				holder.delete = (ImageView) view
						.findViewById(R.id.publish_delete);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.delete
					.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
			if (ls != null && position < ls.size()) {
				holder.img.setImageURI(Uri.parse(APPRestClient.SERVER_IP + ls.get(position)));
			} else {
				holder.img.setImageURI(Uri.parse("res:///"
						+ R.drawable.evaluate_add));
				holder.delete.setVisibility(View.GONE);

			}
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ratiolist.remove(position);
					finalList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			if (finalList == null || finalList.size() < 1) {
				isShowDelete = false;
			}
			return view;
		}

		class ViewHolder {
			SimpleDraweeView img;
			ImageView delete;
		}
	}
}
