package com.uugty.uu.com.find;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.myview.TopBackView;
import com.uugty.uu.entity.UserCommentEntity;
import com.uugty.uu.entity.UserCommentEntity.UserCommentList;

public class UserCommentActivity extends BaseActivity {

	private TopBackView topTitle;
	private TextView totalTitle, serviceTitle, freshTitle, costTitle;
	private ListView listView;
	private CommentAdapter adapter;
	private List<UserCommentList> list;
	private String userId = "";

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.user_comment;
	}

	@Override
	protected void initGui() {
		if (null != getIntent()) {
			userId = getIntent().getStringExtra("userId");
		}
		topTitle = (TopBackView) findViewById(R.id.user_comment_title);
		topTitle.setTitle("评论");
		totalTitle = (TextView) findViewById(R.id.user_comment_total);
		serviceTitle = (TextView) findViewById(R.id.user_comment_service_index);
		freshTitle = (TextView) findViewById(R.id.user_comment_fresh_index);
		listView = (ListView) findViewById(R.id.user_comment_listview);
		costTitle = (TextView) findViewById(R.id.user_comment_cost_index);

	}

	@Override
	protected void initAction() {
		// 调用接口,填充数据
		sendRequest();
		list = new ArrayList<UserCommentList>();
		adapter = new CommentAdapter(this, list);
		listView.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("userId", userId);
		APPRestClient.post(this, ServiceCode.USER_COMMENT_LIST, params,
				new APPResponseHandler<UserCommentEntity>(
						UserCommentEntity.class,this) {
					@Override
					public void onSuccess(UserCommentEntity result) {
						totalTitle.setText(result.getOBJECT().getCommentCount()
								+ "条评论");
						if (!result.getOBJECT().getAvageServiceIndex()
								.equals("")) {
							serviceTitle
									.setText("服务指数"
											+ result.getOBJECT()
													.getAvageServiceIndex());
						} else {
							serviceTitle.setText("服务指数0");
						}
						if (!result.getOBJECT().getAvageRatioIndex().equals("")) {
							freshTitle.setText("性价比指数"
									+ result.getOBJECT().getAvageRatioIndex());
						} else {
							freshTitle.setText("性价比指数0");
						}
						if (!result.getOBJECT().getAvageFreshIndex().equals("")) {
							costTitle.setText("新鲜指数"
									+ result.getOBJECT().getAvageFreshIndex());
						} else {
							costTitle.setText("新鲜指数0");
						}

						if (result.getLIST().size() > 0) {
							list = result.getLIST();
							adapter.updateList(list);
						}
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(ctx, 0, errorMsg, 300).show();
						if (errorCode == -999) {
							new AlertDialog.Builder(UserCommentActivity.this)
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
}

class CommentAdapter extends BaseAdapter {
	private List<UserCommentList> list;
	private Context context;

	public CommentAdapter(Context context, List<UserCommentList> list) {
		this.list = list;
		this.context = context;
	}

	public void updateList(List<UserCommentList> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewManger viewmanger;
		if (convertView == null) {
			viewmanger = new ViewManger();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.user_comment_listview_item, null);
			viewmanger.headImage = (SimpleDraweeView) convertView
					.findViewById(R.id.user_comment_list_item_head);
			viewmanger.nameText = (TextView) convertView
					.findViewById(R.id.user_comment_list_item_name);
			viewmanger.timeText = (TextView) convertView
					.findViewById(R.id.user_comment_list_item_time);
			viewmanger.ratinBar = (RatingBar) convertView
					.findViewById(R.id.user_comment_list_item_ratingbar);
			viewmanger.contentText = (TextView) convertView
					.findViewById(R.id.user_comment_list_item_content);
			viewmanger.iamgesLin = (LinearLayout) convertView
					.findViewById(R.id.user_comment_list_iamges_lin);
			viewmanger.evaluaeOneImage = (SimpleDraweeView) convertView
					.findViewById(R.id.user_comment_list_item_evaluae_one);
			viewmanger.evaluaeTwoImage = (SimpleDraweeView) convertView
					.findViewById(R.id.user_comment_list_item_evaluae_two);
			viewmanger.evaluaeThreeImage = (SimpleDraweeView) convertView
					.findViewById(R.id.user_comment_list_item_evaluae_three);
			viewmanger.evaluaeFourImage = (SimpleDraweeView) convertView
					.findViewById(R.id.user_comment_list_item_evaluae_four);

			convertView.setTag(viewmanger);
		} else {
			viewmanger = (ViewManger) convertView.getTag();
		}
		if (!list.get(position).getUserAvatar().equals("")) {
			viewmanger.headImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP
							+ list.get(position).getUserAvatar()));
		} else {
			viewmanger.headImage.setImageURI(Uri.parse("res///"+R.drawable.no_default_head_img));
		}
		viewmanger.nameText.setText(list.get(position).getUserName());
		viewmanger.timeText.setText(list.get(position).getCommentDate());
		viewmanger.ratinBar.setRating(Float.valueOf(list.get(position)
				.getTotalIndex()));
		if (list.get(position).getCommentContent().equals("")) {
			viewmanger.contentText.setVisibility(View.GONE);
		} else {
			viewmanger.contentText.setText(list.get(position)
					.getCommentContent());
		}
		if (list.get(position).getCommentImages().equals("")) {
			viewmanger.iamgesLin.setVisibility(View.GONE);
		} else {
			// 截取字符串
			String[] images = list.get(position).getCommentImages().split(",");
			viewmanger.iamgesLin.setVisibility(View.VISIBLE);
			// 此处代码写烂了
			if (images.length == 1) {
				viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeOneImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[0]));
				viewmanger.evaluaeTwoImage.setVisibility(View.GONE);
				viewmanger.evaluaeThreeImage.setVisibility(View.GONE);
				viewmanger.evaluaeFourImage.setVisibility(View.GONE);
			}
			if (images.length == 2) {
				viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeOneImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[0]));
				viewmanger.evaluaeTwoImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[1]));
				viewmanger.evaluaeThreeImage.setVisibility(View.GONE);
				viewmanger.evaluaeFourImage.setVisibility(View.GONE);
			}
			if (images.length == 3) {
				viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeThreeImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeOneImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[0]));
				viewmanger.evaluaeTwoImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[1]));
				viewmanger.evaluaeThreeImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[2]));
				viewmanger.evaluaeFourImage.setVisibility(View.GONE);
			}
			if (images.length == 4) {
				viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeThreeImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeFourImage.setVisibility(View.VISIBLE);
				viewmanger.evaluaeOneImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[0]));
				viewmanger.evaluaeTwoImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[1]));
				viewmanger.evaluaeThreeImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[2]));
				viewmanger.evaluaeFourImage.setImageURI(Uri.parse(APPRestClient.SERVER_IP + images[3]));
			}
			/*
			 * viewmanger.evaluaeOneImage.setVisibility(View.GONE);
			 * viewmanger.evaluaeTwoImage.setVisibility(View.GONE);
			 * viewmanger.evaluaeThreeImage.setVisibility(View.GONE);
			 * viewmanger.evaluaeFourImage.setVisibility(View.GONE);
			 */
		}
		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	static class ViewManger {
		SimpleDraweeView evaluaeOneImage, evaluaeTwoImage, evaluaeThreeImage,
				evaluaeFourImage,headImage;
		TextView nameText, timeText, contentText;
		RatingBar ratinBar;
		LinearLayout iamgesLin;
	}
}
