package com.uugty.uu.com.find;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.UserCommentEntity;
import com.uugty.uu.entity.UserCommentEntity.UserCommentList;

import java.util.ArrayList;
import java.util.List;

public class FindViewFragment_comments extends Fragment {
	private TextView serviceTitle, freshTitle, costTitle;
	private ListView listView;
	private CommentAdapter adapter;
	private List<UserCommentList> list;
	private String userId = "";
	private View view;
	private LinearLayout noDataLin;
	private RelativeLayout totalLin;
	private UserCommentEntity commentEntity;

	static FindViewFragment_comments newInstance(String roadId) {
		FindViewFragment_comments newFragment = new FindViewFragment_comments();
		Bundle bundle = new Bundle();
		bundle.putString("roadId", roadId);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		userId = args.getString("roadId");
		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.user_comments, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initGui();
		initAction();

	}

	private void initGui() {
		serviceTitle = (TextView) view
				.findViewById(R.id.user_comment_service_index);
		freshTitle = (TextView) view
				.findViewById(R.id.user_comment_fresh_index);
		listView = (ListView) view.findViewById(R.id.user_comment_listview);
		costTitle = (TextView) view.findViewById(R.id.user_comment_cost_index);
		noDataLin = (LinearLayout) view
				.findViewById(R.id.user_comment_no_data_lin);
		totalLin = (RelativeLayout) view
				.findViewById(R.id.user_comment_total_lin);

		listView.setFocusable(false);

	}

	private void initAction() {
		// 调用接口,填充数据
		if (null == commentEntity) {
			sendRequest();
		} else {
			Message msg = Message.obtain();
			msg.what = 1;
			Bundle b = new Bundle();
			b.putSerializable("comment", commentEntity);
			msg.setData(b);
			handler.sendMessage(msg);
		}

		list = new ArrayList<UserCommentList>();
		adapter = new CommentAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}

	private void sendRequest() {
		RequestParams params = new RequestParams();
		params.add("userId", userId);
		APPRestClient.post(getActivity(), ServiceCode.USER_COMMENT_LIST,
				params, new APPResponseHandler<UserCommentEntity>(
						UserCommentEntity.class, getActivity()) {
					@Override
					public void onSuccess(UserCommentEntity result) {
						commentEntity = result;
						Message msg = Message.obtain();
						msg.what = 1;
						Bundle b = new Bundle();
						b.putSerializable("comment", result);
						msg.setData(b);
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							sendRequest();
						} else {
						CustomToast.makeText(getActivity(), 0, errorMsg, 300)
								.show();
						if (errorCode == -999) {
							new AlertDialog.Builder(getActivity())
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				UserCommentEntity roadEntityResult = (UserCommentEntity) msg
						.getData().getSerializable("comment");

				if (roadEntityResult.getLIST().size() > 0) {
					listView.setVisibility(View.VISIBLE);
					totalLin.setVisibility(View.VISIBLE);
					noDataLin.setVisibility(View.GONE);
					if (roadEntityResult.getOBJECT().getAvageServiceIndex()
							.equals("")
							|| roadEntityResult.getOBJECT()
									.getAvageServiceIndex() == null) {
						serviceTitle.setText("服务指数" + 5.0);
					} else {
						serviceTitle.setText("服务指数"
								+ roadEntityResult.getOBJECT()
										.getAvageServiceIndex());
					}
					if (roadEntityResult.getOBJECT().getAvageRatioIndex()
							.equals("")
							|| roadEntityResult.getOBJECT()
									.getAvageRatioIndex() == null) {
						freshTitle.setText("性价比指数" + 5.0);
					} else {
						freshTitle.setText("性价比指数"
								+ roadEntityResult.getOBJECT()
										.getAvageRatioIndex());
					}
					if (roadEntityResult.getOBJECT().getAvageFreshIndex()
							.equals("")
							|| roadEntityResult.getOBJECT()
									.getAvageFreshIndex() == null) {
						costTitle.setText("新鲜指数" + 5.0);
					} else {
						costTitle.setText("新鲜指数"
								+ roadEntityResult.getOBJECT()
										.getAvageFreshIndex());
					}
					list = roadEntityResult.getLIST();
					adapter.updateList(list);
				} else {
					noDataLin.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
					totalLin.setVisibility(View.GONE);
				}
				break;
			}
		}
	};

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
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.user_comment_listview_item,null);
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
				viewmanger.headImage.setImageURI(Uri
						.parse(APPRestClient.SERVER_IP
								+ list.get(position).getUserAvatar()));
			} else {
				viewmanger.headImage.setImageURI(Uri.parse("res///"
						+ R.drawable.no_default_head_img));
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
				String[] images = list.get(position).getCommentImages()
						.split(",");
				viewmanger.iamgesLin.setVisibility(View.VISIBLE);
				// 此处代码写烂了
				if (images.length == 1) {
					viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeOneImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[0]));
					viewmanger.evaluaeTwoImage.setVisibility(View.GONE);
					viewmanger.evaluaeThreeImage.setVisibility(View.GONE);
					viewmanger.evaluaeFourImage.setVisibility(View.GONE);
				}
				if (images.length == 2) {
					viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeOneImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[0]));
					viewmanger.evaluaeTwoImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[1]));
					viewmanger.evaluaeThreeImage.setVisibility(View.GONE);
					viewmanger.evaluaeFourImage.setVisibility(View.GONE);
				}
				if (images.length == 3) {
					viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeThreeImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeOneImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[0]));
					viewmanger.evaluaeTwoImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[1]));
					viewmanger.evaluaeThreeImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[2]));
					viewmanger.evaluaeFourImage.setVisibility(View.GONE);
				}
				if (images.length == 4) {
					viewmanger.evaluaeOneImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeTwoImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeThreeImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeFourImage.setVisibility(View.VISIBLE);
					viewmanger.evaluaeOneImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[0]));
					viewmanger.evaluaeTwoImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[1]));
					viewmanger.evaluaeThreeImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[2]));
					viewmanger.evaluaeFourImage.setImageURI(Uri
							.parse(APPRestClient.SERVER_IP + images[3]));

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

		class ViewManger {
			SimpleDraweeView headImage, evaluaeOneImage, evaluaeTwoImage,
					evaluaeThreeImage, evaluaeFourImage;
			TextView nameText, timeText, contentText;
			RatingBar ratinBar;
			LinearLayout iamgesLin;
		}

	}
}
