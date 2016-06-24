package com.uugty.uu.common.myview;


import com.uugty.uu.R;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

//评论，圆形图像在右侧
public class TextCircleImage extends RelativeLayout {

	private View view;
	private TextView mtextName, mtextTime, mtextContent;
	private CirculHeadImage mImageView;

	//给在代码中new
	public TextCircleImage(Context context) {
		super(context);
		init(context);
	}

	//支持xml配置
	public TextCircleImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TextCircleImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	private void init(Context context) {

		view = LayoutInflater.from(context).inflate(R.layout.text_circle_iamge,
				this);
		mtextName = (TextView) view.findViewById(R.id.text_circle_iamge_name);
		mtextTime = (TextView) view.findViewById(R.id.text_circle_iamge_time);
		mtextContent = (TextView) view
				.findViewById(R.id.text_circle_iamge_content);
		mImageView = (CirculHeadImage) view
				.findViewById(R.id.text_circle_iamge_iamge);
	}

	public void setTextContent(String name, String time, String content,
			String image) {
		if (null != name && !name.equals("")) {
			mtextName.setText(name);
		} else {
			mtextName.setText("小u");
		}
		if (null != image && !image.equals("")) {
			mImageView.setCirCularImageSize(45, 45, 5);
			mImageView.setBackPic("drawable://" + R.drawable.persion_circle_bg);
			mImageView.setHeadPic(image, "net");
		} else {
			// 加载默认的图片
			mImageView.setBackPic("drawable://" + R.drawable.persion_circle_bg);
			mImageView.setHeadPic("drawable://"
					+ R.drawable.map_no_user_head_image_default, "drawable");
			mImageView.setCirCularImageSize(45, 45, 5);
		}
		if (null != time && !time.equals("")) {
			mtextTime.setText(time);
		}
		mtextContent.setText(content);

	}
}
