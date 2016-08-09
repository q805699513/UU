package com.uugty.uu.friendstask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.CustomDialog;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.SelectPictureActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.AgreementWebActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.map.PhoneDialog;
import com.uugty.uu.person.PersonCompileActivity;
import com.uugty.uu.person.PersonInfoTextActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishTalkActivity extends BaseActivity implements
		OnClickListener {
	private RelativeLayout select_position,send_standard_rel;
	private TextView current_position, publish_send_text; // 地址 发送
	private EditText publish_content_edt; // 发送内容
	private final static int REQUESTCODA = 1; // 定位
	private final static int PAIZHAO = 100;// 拍照
	private final static int XIANGCE = 101;// 拍照
	// private SimpleDraweeView publish_item_img;
	private String picPath = CacheFileUtil.carmePaht;
	private String pturePath = null;
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=10";
	private List<String> list = new ArrayList<String>(); // 保存本地图片
	private List<String> finalList = new ArrayList<String>();// 保留最终图片
	private GridView publish_grid;
	private PublishAdapter adapter;
	private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示
	private String saidphoto = "", saidCity = "", saidContent;
    private TopBackView titleView;
	// 图片比例
	private static List<String> ratiolist = new ArrayList<String>();
	private String ratio = "";// 图片比例
	private String finalImageUrl;
	private String UserName,UserHead;
	private SwipeRefreshLayout mSwipeLayout;

	@Override
	protected int getContentLayout() {
		return R.layout.publishtalkactivity_layout;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PublishTalkActivity.class.getName());
			intent.setClass(PublishTalkActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		UserName = MyApplication.getInstance().getUserInfo().getOBJECT().getUserName();
		UserHead = MyApplication.getInstance().getUserInfo().getOBJECT().getUserAvatar();
		}
		titleView = (TopBackView) findViewById(R.id.publish_title);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.publish_swipe_layout);
		select_position = (RelativeLayout) findViewById(R.id.select_position);
		send_standard_rel = (RelativeLayout) findViewById(R.id.send_standard_rel);
		current_position = (TextView) findViewById(R.id.current_position);
		publish_content_edt = (EditText) findViewById(R.id.publish_content_edt);
		publish_send_text = (TextView) findViewById(R.id.publish_send_text);
		publish_grid = (GridView) findViewById(R.id.publish_grid);
		adapter = new PublishAdapter(ctx, list);
		publish_grid.setAdapter(adapter);
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setEnabled(false);
		titleView.setTitle("发布");
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		publish_send_text.setOnClickListener(this);
		select_position.setOnClickListener(this);
		send_standard_rel.setOnClickListener(this);
		publish_grid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//
				if (list != null && position < list.size()) {
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
		publish_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (list != null && position < list.size()) {
					imageBrower(position, (ArrayList<String>) list);
				} else {
					PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
							PublishTalkActivity.this);
					builder1.setMessage("选择照片");

					builder1.setPositiveButton("相册",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									// 设置你的操作事项
									Intent intent = new Intent(
											PublishTalkActivity.this,
											SelectPictureActivity.class);
									intent.putExtra(SelectPictureActivity.INTENT_MAX_NUM,6-ratiolist.size());
									startActivityForResult(intent, XIANGCE);
									mSwipeLayout.setEnabled(true);
									mSwipeLayout.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											mSwipeLayout.setRefreshing(true);
										}
									});
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

	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(ctx, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "1");
		startActivity(intent);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
		ratiolist.clear();
		finalList.clear();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(null!=mSwipeLayout&&mSwipeLayout.isEnabled()){
			mSwipeLayout.setRefreshing(false);
			mSwipeLayout.setEnabled(false);
		}
	}
	@Override
	public void onNoDoubleClick(View v) {
		// TODO Auto-generated method stub
		super.onNoDoubleClick(v);
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.select_position:
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(ctx, CurrentPosActivity.class);
			startActivityForResult(intent, REQUESTCODA);
			break;
		case R.id.publish_send_text:
			if(null==MyApplication.getInstance().getUserInfo()){
				intent.putExtra("topage",
						PublishTalkActivity.class.getName());
				intent.setClass(PublishTalkActivity.this, LoginActivity.class);
				startActivity(intent);
			}else{
			UserName = MyApplication.getInstance().getUserInfo().getOBJECT().getUserName();
			UserHead = MyApplication.getInstance().getUserInfo().getOBJECT().getUserAvatar();
			if(UserName.equals("")||UserName==null){
				CustomDialog.Builder builder1 = new CustomDialog.Builder(
						ctx);
				builder1.setMessage("请填写你的昵称!");
				builder1.setTitle("提示");
				builder1.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =new Intent();
								intent.setClass(ctx, PersonInfoTextActivity.class);
								intent.putExtra("titileText", "姓名");
								startActivity(intent);
								dialog.dismiss();
							}
						});

				builder1.setNegativeButton(
						"取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});			
				builder1.create().show();
			}else if(UserHead.equals("")||UserHead==null){
				CustomDialog.Builder builder1 = new CustomDialog.Builder(
						ctx);
				builder1.setMessage("请上传你的头像!");
				builder1.setTitle("提示");
				builder1.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =new Intent();
								intent.setClass(ctx, PersonCompileActivity.class);
								startActivity(intent);
								dialog.dismiss();
							}
						});

				builder1.setNegativeButton(
						"取消",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});			
				builder1.create().show();
			}else{
			if (!publish_content_edt.getText().toString().equals("") && publish_content_edt.getText().toString().trim().length() > 0) {
				saidContent = publish_content_edt.getText().toString();// 内容

				if (!current_position.getText().toString().equals("显示地理位置")) { // 位置
					saidCity = current_position.getText().toString();
				}

				if (list != null && finalList.size() > 0) { // 图片地址
					for (int i = 0; i < finalList.size(); i++) {
						if (i == list.size() - 1) {
							saidphoto += finalList.get(i);
						} else {
							saidphoto += finalList.get(i) + ",";

						}
					}
				}
				for (int i = 0; i < ratiolist.size(); i++) {
					if (i == ratiolist.size() - 1) {
						ratio += ratiolist.get(i);
					} else {
						ratio += ratiolist.get(i) + ",";
					}
				}
				toSendTalk(saidCity, saidContent, saidphoto, ratio);
			} else {
				CustomToast.makeText(ctx, 0, "输入不可为空", 500).show();
			}
			}
			}
			break;
		case R.id.send_standard_rel:
			intent.putExtra("agreement", "sendTalk");
			intent.setClass(this, AgreementWebActivity.class);
			startActivity(intent);
			break;
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

	public static String getFileName() {
		String cc = DateUtil.dateFormat(new Date(), "yyyyMMddHHmmss");
		// BigInteger bi = new BigInteger(cc);
		return cc;
	}

	/*@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		final String resultPic = intent.getStringExtra("resultPic");
		if (resultPic != null) {
			handler2.sendMessage(handler2.obtainMessage(1, resultPic));
		}
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODA:
				if (data != null) {
					String postion = data.getStringExtra("pos");
					postion = data.getStringExtra("pos");// 获取位置
					current_position.setText(postion);
				}
				break;
			case PAIZHAO:
				handler2.sendMessage(handler2.obtainMessage(1, pturePath));
				break;
			case XIANGCE:
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
				saveMyBitmap(resultPic, resizeImage2(resultPic));
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
								list.add(resultPic);
								finalList.add(finalImageUrl);
								handler.sendMessage(handler.obtainMessage(2));

							} else {
								ratiolist.remove(ratiolist.size()-1);
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				CustomToast.makeText(ctx, 0, "文件上传异常", 300).show();
				break;

			case 2:
				adapter = new PublishAdapter(ctx, list);
				publish_grid.setAdapter(adapter);
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

	private void toSendTalk(final String city,final String content,final String photo,
			final String ratio) {
		RequestParams params = new RequestParams();
		params.put("saidCity", city);
		params.put("saidContent", content);
		params.put("saidPhoto", photo);
		params.put("saidPictureRatio", ratio);
		APPRestClient.post(PublishTalkActivity.this, ServiceCode.SEND_TALK,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
						this) {
					@Override
					public void onSuccess(BaseEntity result) {
						CustomDialog.Builder builder = new CustomDialog.Builder(
								PublishTalkActivity.this);
						builder.setIsOutSideCancle(false);
						builder.setMessage("发布成功！");
						builder.setTitle("提示");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();	
										Intent intent = new Intent();
										intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										intent.putExtra("publish", "publish");
										intent.setClass(PublishTalkActivity.this, FriendsDynamicActivity.class);
										startActivity(intent);
										list.clear();
										ratiolist.clear();
										finalList.clear();
										finish();
									}
								});

						builder.create().show();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							toSendTalk(city, content, photo, ratio);
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						CustomDialog.Builder builder = new CustomDialog.Builder(
								PublishTalkActivity.this);
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
			if (ls.size() == 6) {
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
						R.layout.publishimage_grid_item, null);
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
				holder.img.setImageURI(Uri.parse("file://" + ls.get(position)));
			} else {
				holder.img.setImageURI(Uri.parse("res:///"+R.drawable.evaluate_add));
				holder.delete.setVisibility(View.GONE);

			}
			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					list.remove(position);
					ratiolist.remove(position);
					finalList.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			if (list == null || list.size() < 1) {
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
