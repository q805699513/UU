package com.uugty.uu.uuchat;

import java.io.File;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.EmojiEdite;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.common.util.DateUtil;
import com.uugty.uu.entity.GroupChatEntity;
import com.uugty.uu.loaderimg.PhoneimageActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.map.LocationActivity;
import com.uugty.uu.map.PhoneDialog;
import com.uugty.uu.person.CutPicturceActivity;

public class GroupChatCreateActivity extends BaseActivity implements
		OnClickListener {

	private ImageView avatarImage;
	private String capturePath = null;
	private String picPath = CacheFileUtil.carmePaht;
	private final static int PAIZHAO = 100;// 拍照
	private EmojiEdite nameEditText, contentEditText;
	private TextView nameNumText, contentNumText, addressTextView;
	private Button createBtn;
	private String avatarImageName;
	private TopBackView titleView;
	private LinearLayout addressLin;
	private String finalImageUrl;
	// 个人头像
	private static String requestURL = ServiceCode.NEW_UPLOAD_FILE + "?type=12";

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.group_chat_create;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		avatarImage = (ImageView) findViewById(R.id.group_chat_create_avatar);
		nameEditText = (EmojiEdite) findViewById(R.id.grou_chat_create_name);
		nameNumText = (TextView) findViewById(R.id.grou_chat_create_name_num);
		contentEditText = (EmojiEdite) findViewById(R.id.grou_chat_create_content);
		contentNumText = (TextView) findViewById(R.id.grou_chat_create_content_num);
		createBtn = (Button) findViewById(R.id.group_chat_create_btn);
		titleView = (TopBackView) findViewById(R.id.group_chat_create_title);
		addressLin = (LinearLayout) findViewById(R.id.group_chat_create_address_lin);
		addressTextView = (TextView) findViewById(R.id.group_chat_create_address);
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		avatarImage.setOnClickListener(this);
		createBtn.setOnClickListener(this);
		addressLin.setOnClickListener(this);
		nameEditText.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s)) {
					nameNumText.setText(s.length() + "/15个字");
				} else {
					nameNumText.setText("15个字");
				}
			}
		});
		contentEditText.addTextChangedListener(new TextWatcher() {

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
				if (!TextUtils.isEmpty(s)) {
					contentNumText.setText(s.length() + "/150个字");
				} else {
					contentNumText.setText("150个字");
				}
			}
		});
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		titleView.setTitle("群聊创建");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
	}

	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.group_chat_create_avatar:
			PhoneDialog.Builder builder1 = new PhoneDialog.Builder(
					GroupChatCreateActivity.this);
			builder1.setMessage("选择照片");

			builder1.setPositiveButton("相册",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 设置你的操作事项
							Intent intent = new Intent(
									GroupChatCreateActivity.this,
									PhoneimageActivity.class);
							intent.putExtra("topageFrom",
									GroupChatCreateActivity.class.getName());
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
		case R.id.group_chat_create_btn:
			// 调用创建接口
			// 检查数据
			if (checkData()) {
				ActivityCollector
						.removeSpecifiedActivity("com.uugty.uu.uuchat.GroupChatListActivity");
				createGroupRequest();
			}

			break;
		case R.id.group_chat_create_address_lin:
			Intent intent = new Intent();
			if (!TextUtils.isEmpty(addressTextView.getText().toString().trim())) {
				intent.putExtra("address", addressTextView.getText().toString()
						.trim());
			}
			intent.setClass(this, LocationActivity.class);
			startActivityForResult(intent, 1001);
			break;

		default:
			break;
		}

	}

	private boolean checkData() {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(avatarImageName)) {
			CustomToast.makeText(ctx, 0, "头像为空", 300).show();
			return false;
		}
		if (TextUtils.isEmpty(nameEditText.getText().toString().trim())) {
			CustomToast.makeText(ctx, 0, "群组的名字为空", 300).show();
			return false;
		}
		if (TextUtils.isEmpty(contentEditText.getText().toString().trim())) {
			CustomToast.makeText(ctx, 0, "群组的内容为空", 300).show();
			return false;
		}
		if (TextUtils.isEmpty(addressTextView.getText().toString().trim())) {
			CustomToast.makeText(ctx, 0, "群组的地址为空", 300).show();
			return false;
		}
		return true;
	}

	// 路线信息
	private void createGroupRequest() {

		RequestParams params = new RequestParams();
		if(null==MyApplication.getInstance().getUserInfo()){
			Intent intent=new Intent();
			intent.putExtra("topage",
					GroupChatCreateActivity.class.getName());
			intent.setClass(GroupChatCreateActivity.this, LoginActivity.class);
			startActivity(intent);
		}else{
		params.add("groupOwnerID", MyApplication.getInstance().getUserInfo()
				.getOBJECT().getUserId());
		}
		params.add("groupname", nameEditText.getText().toString().trim());
		params.add("groupContent", contentEditText.getText().toString().trim());
		params.add("groupLocation", addressTextView.getText().toString().trim());
		params.add("groupImages", finalImageUrl);
		APPRestClient.post(this, ServiceCode.GROUP_CHAT_CREATE, params,
				new APPResponseHandler<GroupChatEntity>(GroupChatEntity.class,
						this) {
					@Override
					public void onSuccess(GroupChatEntity result) {
						Intent intent = new Intent();
						intent.setClass(GroupChatCreateActivity.this,
								GroupChatListActivity.class);
						startActivity(intent);
						CustomToast.makeText(ctx, 0, "创建群成功", 300).show();
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							createGroupRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
					}}

					@Override
					public void onFinish() {
					}
				});

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
						GroupChatCreateActivity.class.getName());
				intent.putExtra("shape", "circle");
				intent.setClass(GroupChatCreateActivity.this,
						CutPicturceActivity.class);
				startActivity(intent);
				break;
			case 1001:
				addressTextView.setText(data.getStringExtra("address"));
				break;
			default:
				break;
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
					} else {
						Message msg = new Message();
						handler.sendMessage(msg);
					}

				}
			}.start();
			avatarImageName = resultPic.substring(resultPic
					.lastIndexOf(File.separator) + 1);
			ImageLoader.getInstance().displayImage("file://" + resultPic,
					avatarImage);
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			CustomToast.makeText(ctx, 0, "文件上传异常", 300).show();
		}

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
}
