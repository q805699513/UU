package com.uugty.uu.evaluate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.com.rightview.UPdataActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CirculHeadImage;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.entity.BaseEntity;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.map.PhoneDialog;
import com.uugty.uu.person.CutPicturceActivity;

public class ReplyActivity extends BaseActivity implements OnClickListener {

	private ImageView back;
	private CirculHeadImage headImage;
	private TextView nameText;
	private EditText contentEdit;
	private Button sendBtn;
	private String pturePath = null;
	private String picPath = CacheFileUtil.carmePaht;
	private final static int PAIZHAO = 100;// 拍照
	private final static int PHONE = 110;// 手机相册
	private String select = "0";
	private Map<String, String> imageMap = new HashMap<String, String>();
	private ImageView oneImageView, twoImageView, threeImageView,
			fourImageView;
	private static String requestURL = ServiceCode.UPLOAD_FILE + "?type=7";

	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_reply;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.comments_reply_back);
		headImage = (CirculHeadImage) findViewById(R.id.comments_reply_head_iamge);
		nameText = (TextView) findViewById(R.id.comments_reply_user_name);
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					ReplyActivity.class.getName());
			intent.setClass(ReplyActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		headImage.setHeadPic(MyApplication.getInstance().getUserInfo().getOBJECT()
						.getUserAvatar(), "net");
		nameText.setText(MyApplication.getInstance().getUserInfo().getOBJECT()
				.getUserName());
		}
		contentEdit = (EditText) findViewById(R.id.comments_reply_content);
		sendBtn = (Button) findViewById(R.id.comments_reply_button);
		oneImageView = (ImageView) findViewById(R.id.comments_reply_one);
		twoImageView = (ImageView) findViewById(R.id.comments_reply_two);
		threeImageView = (ImageView) findViewById(R.id.comments_reply_three);
		fourImageView = (ImageView) findViewById(R.id.comments_reply_four);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		back.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		oneImageView.setOnClickListener(this);
		twoImageView.setOnClickListener(this);
		threeImageView.setOnClickListener(this);
		fourImageView.setOnClickListener(this);
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
		switch (v.getId()) {
		case R.id.comments_reply_back:
			finish();
			back.setClickable(false);
			break;
		case R.id.comments_reply_button:
			sendRequest();
			break;
		case R.id.comments_reply_one:
			select = "0";
			showDialog();
			break;
		case R.id.comments_reply_two:
			select = "1";
			showDialog();
			break;
		case R.id.comments_reply_three:
			select = "2";
			showDialog();
			break;
		case R.id.comments_reply_four:
			select = "3";
			showDialog();
			break;

		default:
			break;
		}
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("replyContent", contentEdit.getText().toString().trim());// 回复内容
		params.add("replyImages", contentEdit.getText().toString().trim());// 回复的图片，多张以逗号隔开
		params.add("commentId", contentEdit.getText().toString().trim());// 要回复的评论id

		APPRestClient.post(this, ServiceCode.USER_COMMENT_REPLY, params,
				new APPResponseHandler<BaseEntity>(BaseEntity.class, this) {
					@Override
					public void onSuccess(BaseEntity result) {
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(ReplyActivity.this)
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

	private void showDialog() {
		PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
				ReplyActivity.this);
		builder1.setMessage("选择照片");

		builder1.setPositiveButton("相册", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 设置你的操作事项
				Intent intent = new Intent(ReplyActivity.this,
						PhoneimageActivity.class);
				intent.putExtra("topageFrom", ReplyActivity.class.getName());
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
				Intent intent = new Intent();
				intent.putExtra("picPath", pturePath);
				intent.putExtra("topageFrom", ReplyActivity.class.getName());
				intent.setClass(ReplyActivity.this, CutPicturceActivity.class);
				startActivity(intent);
				break;
			case PHONE:

				break;

			default:
				break;
			}
		}

	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		final String resultPic = intent.getStringExtra("resultPic");
		if (null != resultPic) {
			if (select.equals("0")) {
				oneImageView.setImageURI(Uri.parse("file://" + resultPic));
			}
			if (select.equals("1")) {
				twoImageView.setImageURI(Uri.parse("file://" + resultPic));
			}
			if (select.equals("2")) {
				threeImageView.setImageURI(Uri.parse("file://" + resultPic));
			}
			if (select.equals("3")) {
				fourImageView.setImageURI(Uri.parse("file://" + resultPic));
			}
			imageMap.put(select, resultPic);
			new Thread() {
				@Override
				public void run() {
					String json = APPRestClient.post(requestURL, resultPic);
					String netPictName = toRead(json, "OBJECT");
					String imagURL = toRead(netPictName, "imageURL");
					imageMap.put(select, imagURL.replace("\\", ""));
				}
			}.start();
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

	public String MaptoString(Map<String, String> map) {
		String resultString = "";
		for (int i = 0; i < map.size(); i++) {
			if (map.get(i + "") != null) {
				resultString += map.get(i + "") + ",";
			}
		}
		return resultString;
	}
}
