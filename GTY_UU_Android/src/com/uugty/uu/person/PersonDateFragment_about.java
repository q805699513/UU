package com.uugty.uu.person;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uugty.uu.R;
import com.uugty.uu.mhvp.core.magic.viewpager.AbsBaseFragment;
import com.uugty.uu.mhvp.core.magic.viewpager.InnerScrollView;

public class PersonDateFragment_about extends AbsBaseFragment {
	private View view;
	private String userDescription;
	protected InnerScrollView mScrollView;

	public static PersonDateFragment_about newInstance(String userDescription) {
		PersonDateFragment_about newFragment = new PersonDateFragment_about();
		Bundle bundle = new Bundle();
		bundle.putString("userDescription", userDescription);
		newFragment.setArguments(bundle);
		return newFragment;

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.persondate_about_item, null);
		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		Bundle args = getArguments();
		userDescription = args.getString("userDescription");
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mScrollView = (InnerScrollView) view
				.findViewById(R.id.persondate_user_describe_view);
		mScrollView.register2Outer(mOuterScroller, mIndex);
		init();
	}

	private void init() {
		// 此处代码性能比较差，可优化
		View viewThis = LayoutInflater.from(getActivity()).inflate(
				R.layout.person_center_about_text, null);
		TextView textView = (TextView) viewThis
				.findViewById(R.id.persondate_about_myself);
		TextView oneText = (TextView) viewThis
				.findViewById(R.id.person_center_about_text_one);
		TextView twoText = (TextView) viewThis
				.findViewById(R.id.person_center_about_text_two);
		TextView threeText = (TextView) viewThis
				.findViewById(R.id.person_center_about_text_three);
		TextView fourText = (TextView) viewThis
				.findViewById(R.id.person_center_about_text_four);
		LinearLayout textLin = (LinearLayout) viewThis
				.findViewById(R.id.person_center_about_text_lin);

		if (!TextUtils.isEmpty(userDescription)) {
			if (userDescription.contains("&&a")) {
				String data[] = userDescription.split("&&a");
				if (data.length > 0 && null!=data[0]&&!TextUtils.isEmpty(data[0])) {
					oneText.setText(data[0]);
				}
				if (data.length > 1 && null!=data[1]&&!TextUtils.isEmpty(data[1])) {
					twoText.setText(data[1]);
				}
				if (data.length > 2 && null!=data[2]&&!TextUtils.isEmpty(data[2])) {
					threeText.setText(data[2]);
				}
				if (data.length > 3 && null!=data[3]&&!TextUtils.isEmpty(data[3])) {
					fourText.setText(data[3]);
				}
				textView.setVisibility(view.GONE);
				textLin.setVisibility(View.VISIBLE);
			} else {
				textView.setText(userDescription);
				textView.setVisibility(view.VISIBLE);
				textLin.setVisibility(View.GONE);
			}

		} else {
			textView.setText("此人很懒，神马都没留下");
			textView.setVisibility(view.VISIBLE);
			textLin.setVisibility(View.GONE);
		}

		mScrollView.setContentView(viewThis);
	}
}
