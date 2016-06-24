package com.uugty.uu.person;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.dialog.PerDialog;
import com.uugty.uu.common.dialog.PerDialog.OnWarnSheetSelected;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.SelectPictureActivity;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.photoview.ImagePagerActivity;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.modeal.UUlogin;


public class PersonLeftPhotoActivity extends BaseActivity implements
		OnClickListener {

	private TopBackView titleView;
	private RelativeLayout noDataRel;
	private ImageView cameraImage, albumImage;
	private List<String> selectPicList = new ArrayList<String>();
	private final static int LEFT_PHOTO_ALBUM = 101;// 相册
	private final static int PAIZHAO = 100;// 拍照
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=5";
	private String finalImageUrl;
	private LiftPhotoAdapter adapter;
	private GridView gridview;
	private UUlogin userInfo;
	private String[] arrayListPic;
	// 图片路径
	private String picPath = CacheFileUtil.carmePaht;
	// 拍照图片保存路径
	private String pturePath = null;
	private SwipeRefreshLayout mSwipeLayout;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_person_left_photo;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		titleView = (TopBackView) findViewById(R.id.left_photo_title);
		titleView.setTitle("照片墙");
		noDataRel = (RelativeLayout) findViewById(R.id.left_photo_no_data_rel);
		cameraImage = (ImageView) findViewById(R.id.left_photo_camera);
		albumImage = (ImageView) findViewById(R.id.left_photo_album);
		gridview = (GridView) findViewById(R.id.left_photo_gridview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.left_photo_swipe);
		mSwipeLayout.setColorSchemeResources(R.color.login_text_color,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setDistanceToTriggerSync(200);
		mSwipeLayout.setEnabled(false);
		adapter = new LiftPhotoAdapter(selectPicList, this);
		gridview.setAdapter(adapter);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		cameraImage.setOnClickListener(this);
		albumImage.setOnClickListener(this);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				imageBrower(position, (ArrayList<String>) selectPicList);
			}
		});

		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				PerDialog dialog = new PerDialog(PersonLeftPhotoActivity.this,
						"是否删除该图片", "删除", new OnWarnSheetSelected() {
							@Override
							public void onWarnClick(int whichButton) {
								switch (whichButton) {
								// 删除
								case 0:
									selectPicList.remove(position);
									adapter.updateList(selectPicList);
									if (selectPicList.size() == 0) {
										noDataRel.setVisibility(View.VISIBLE);
										mSwipeLayout.setVisibility(View.GONE);
									}
									break;
								// 取消
								case 1:
									break;
								}
							}
						});
				dialog.showdialog();
				return false;
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					PersonLeftPhotoActivity.class.getName());
			intent.setClass(PersonLeftPhotoActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		userInfo = MyApplication.getInstance().getUserInfo();
		}
		if (!userInfo.getOBJECT().getUserLifePhoto().equals("")) {
			noDataRel.setVisibility(View.GONE);
			mSwipeLayout.setVisibility(View.VISIBLE);
			arrayListPic = userInfo.getOBJECT().getUserLifePhoto().split(",");
			for (int i = 0; i < arrayListPic.length; i++) {
				selectPicList.add(arrayListPic[i]);
			}
			adapter.updateList(selectPicList);
		}else{
			noDataRel.setVisibility(View.VISIBLE);
			mSwipeLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_photo_camera:
			if (selectPicList.size() < 10) {
				getImageFromCamera();

			} else {
				CustomToast.makeText(PersonLeftPhotoActivity.this, "已经拍了10张照片",
						300).show();
			}
			break;
		case R.id.left_photo_album:
			Intent intent = new Intent(PersonLeftPhotoActivity.this,
					SelectPictureActivity.class);
			intent.putExtra(SelectPictureActivity.INTENT_MAX_NUM,
					10 - selectPicList.size());
			startActivityForResult(intent, LEFT_PHOTO_ALBUM);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case LEFT_PHOTO_ALBUM:
				@SuppressWarnings("unchecked")
				ArrayList<String> temp = (ArrayList<String>) data
						.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
				if (temp.size() > 0) {
					noDataRel.setVisibility(View.GONE);
					mSwipeLayout.setVisibility(View.VISIBLE);
					mSwipeLayout.setEnabled(true);
				}
				mSwipeLayout.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mSwipeLayout.setRefreshing(true);
					}
				});
				for (int i = 0; i < temp.size(); i++) {
					handler.sendMessage(handler.obtainMessage(2, temp.get(i)));
				}
				mSwipeLayout.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mSwipeLayout.setRefreshing(false);
						mSwipeLayout.setEnabled(false);
					}
				});
				break;
			case PAIZHAO:
				noDataRel.setVisibility(View.GONE);
				mSwipeLayout.setVisibility(View.VISIBLE);
				mSwipeLayout.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mSwipeLayout.setEnabled(true);
						mSwipeLayout.setRefreshing(true);
					}
				});
				handler.sendMessage(handler.obtainMessage(2, pturePath));
				mSwipeLayout.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mSwipeLayout.setRefreshing(false);
						mSwipeLayout.setEnabled(false);
					}
				});
			default:
				break;
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				CustomToast.makeText(ctx, 0, "文件上传异常", 300).show();
				break;
			case 2:
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
								selectPicList.add(finalImageUrl);
								handler.sendMessage(handler.obtainMessage(3));
							} else {
								handler.sendMessage(handler.obtainMessage(1));
							}
						}
					}.start();
				}

				break;
			case 3:
				adapter.updateList(selectPicList);
				break;
			}
			;
		};
	};

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

	// 通知后台更新数据
	private void sendServer(String str) {
		RequestParams params = new RequestParams();
		params.put("type", "11");
		params.put("content", str);
		APPRestClient.post(PersonLeftPhotoActivity.this, ServiceCode.USER_INFO,
				params, new APPResponseHandler<BaseEntity>(BaseEntity.class,
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

	protected void imageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(ctx, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		intent.putExtra(ImagePagerActivity.FLAG, "0");
		startActivity(intent);
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

		options.inJustDecodeBounds = true;
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// list转String数组
		String str = "";
		for (int i = 0; i < selectPicList.size(); i++) {
			if (i == selectPicList.size() - 1) {
				str += selectPicList.get(i);
			} else {
				str += selectPicList.get(i) + ",";
			}
		}
		sendServer(str);
		MyApplication.getInstance().getUserInfo().getOBJECT()
				.setUserLifePhoto(str);
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setClass(PersonLeftPhotoActivity.this,
				PersonCompileActivity.class);
		startActivity(intent);
	}

}

class LiftPhotoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> pictures;

	public LiftPhotoAdapter(List<String> list, Context context) {
		super();
		this.pictures = list;
		inflater = LayoutInflater.from(context);
	}

	public void updateList(List<String> list) {
		this.pictures = list;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (null != pictures) {
			return pictures.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_pricture, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (SimpleDraweeView) convertView
					.findViewById(R.id.image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.image.setImageURI(Uri.parse(APPRestClient.SERVER_IP
				+ pictures.get(position).substring(0,
						pictures.get(position).indexOf("."))
				+ "_ya"
				+ pictures.get(position).substring(
						pictures.get(position).indexOf("."))));
		return convertView;
	}

	static class ViewHolder {
		public SimpleDraweeView image;
	}

}
