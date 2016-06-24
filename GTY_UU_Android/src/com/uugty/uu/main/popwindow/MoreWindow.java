package com.uugty.uu.main.popwindow;

import com.uugty.uu.R;
import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.RequestParams;
import com.uugty.uu.common.asynhttp.service.APPResponseHandler;
import com.uugty.uu.common.asynhttp.service.APPRestClient;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.entity.Util;
import com.uugty.uu.entity.VipEntity;
import com.uugty.uu.friendstask.FriendsDynamicActivity;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.map.OpenShopActivity;
import com.uugty.uu.map.PublishServicesActivity;
import com.uugty.uu.uuchat.ChatCustomActivity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MoreWindow extends PopupWindow implements OnClickListener{

	private String TAG = MoreWindow.class.getSimpleName();
	Activity mContext;
	private int mWidth;
	private int mHeight;
	private int statusBarHeight ;
	private Bitmap mBitmap= null;
	private Bitmap overlay = null;
	
	private Handler mHandler = new Handler();

	public MoreWindow(Activity context) {
		mContext = context;
	}

	public void init() {
		Rect frame = new Rect();
		mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;
		DisplayMetrics metrics = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		mWidth = metrics.widthPixels;
		mHeight = metrics.heightPixels;
		
		setWidth(mWidth);
		setHeight(mHeight);
	}
	
	private Bitmap blur() {
		if (null != overlay) {
			return overlay;
		}
		long startMs = System.currentTimeMillis();

		View view = mContext.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		mBitmap = view.getDrawingCache();
		
		float scaleFactor = 8;//ͼƬ���ű�����
		float radius = 10;//ģ���̶�
		int width = mBitmap.getWidth();
		int height =  mBitmap.getHeight();

		overlay = Bitmap.createBitmap((int) (width / scaleFactor),(int) (height / scaleFactor),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(mBitmap, 0, 0, paint);

		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		Log.i(TAG, "blur time is:"+(System.currentTimeMillis() - startMs));
		return overlay;
	}
	
	private Animation showAnimation1(final View view,int fromY ,int toY) {
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
		go.setDuration(300);
		TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
		go1.setDuration(100);
		go1.setStartOffset(250);
		set.addAnimation(go1);
		set.addAnimation(go);

		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});
		return set;
	}
	

	public void showMoreWindow(View anchor,int bottomMargin) {
		final RelativeLayout layout = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.center_music_more_window, null);
		setContentView(layout);
		
		ImageView close= (ImageView)layout.findViewById(R.id.center_music_window_close);
		android.widget.RelativeLayout.LayoutParams params =new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.bottomMargin = bottomMargin;
		params.addRule(RelativeLayout.BELOW, R.id.more_window_auto);
		params.addRule(RelativeLayout.RIGHT_OF, R.id.more_window_collect);
		params.topMargin = 200;
		params.leftMargin = 18;
		close.setLayoutParams(params);
		
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowing()) {
					closeAnimation(layout);
				}
			}

		});
		
		showAnimation(layout);
		setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
		setOutsideTouchable(true);
		setFocusable(true);
		showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
	}

	private void showAnimation(ViewGroup layout){
		for(int i=0;i<layout.getChildCount();i++){
			final View child = layout.getChildAt(i);
			if(child.getId() == R.id.center_music_window_close){
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						child.setVisibility(View.VISIBLE);
						ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "rotation", 90F, 180F);
						fadeAnim.setDuration(300);
						KickBackAnimator kickAnimator = new KickBackAnimator();
						kickAnimator.setDuration(150);
						fadeAnim.setEvaluator(kickAnimator);
						fadeAnim.start();
					}
				}, i * 50);
				continue;
			}
			child.setOnClickListener(this);
			child.setVisibility(View.INVISIBLE);
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
					fadeAnim.setDuration(300);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(150);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
				}
			}, i * 50);
		}
		
	}

	private void closeAnimation(ViewGroup layout){
		for(int i=0;i<layout.getChildCount();i++){
			final View child = layout.getChildAt(i);
			if(child.getId() == R.id.center_music_window_close){
				continue;
			}
			child.setOnClickListener(this);
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
					fadeAnim.setDuration(200);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(100);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
					fadeAnim.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animator animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							child.setVisibility(View.INVISIBLE);
						}
						
						@Override
						public void onAnimationCancel(Animator animation) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}, (layout.getChildCount()-i-1) * 30);
			
			if(child.getId() == R.id.more_window_collect){
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						dismiss();
					}
				}, (layout.getChildCount()-i) * 30 + 80);
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.more_window_collect:
			if (MyApplication.getInstance().isLogin()) {
				intent.putExtra("flag", "true");// 从主页的定制
				intent.putExtra("type", "1");
				intent.setClass(mContext, ChatCustomActivity.class);
				mContext.startActivity(intent);
			} else {
				intent.setClass(mContext, LoginActivity.class);
				mContext.startActivity(intent);
			}
			dismiss();
			break;
		case R.id.more_window_auto:
			if (MyApplication.getInstance().isLogin()) {
				queryIsVip();
			} else {
				intent.setClass(mContext, LoginActivity.class);
				mContext.startActivity(intent);
			}
			dismiss();
			break;
		case R.id.more_window_external:
			intent.setClass(mContext, FriendsDynamicActivity.class);
			mContext.startActivity(intent);
			dismiss();
			break;

		default:
			break;
		}
	}
	
	private void queryIsVip() {
		RequestParams params = new RequestParams();
		APPRestClient.post(mContext, APPRestClient.BASE_URL
				+ ServiceCode.QUERY_IS_VIP, params, true,
				new APPResponseHandler<VipEntity>(VipEntity.class, mContext) {
					@Override
					public void onSuccess(VipEntity result) {
						Intent intent = new Intent();
						if (null != result.getOBJECT()) {
						
							if (result.getOBJECT().getUserIsPromoter()
									.equals("1") || !result.getOBJECT().getRoadlineId()
									.equals("0")) {
									intent.setClass(mContext, PublishServicesActivity.class);
									intent.putExtra("from", "framgent");
									mContext.startActivity(intent);
								} else {
									intent.setClass(mContext,
											OpenShopActivity.class);
									Util.vipBack="main";
									mContext.startActivity(intent);
								
							} 
						}

					}

					@Override
					public void onFailure(int errorCode, String errorMsg) {
						if (errorCode == 3) {
							queryIsVip();
						} else {
							CustomToast.makeText(mContext, 0, errorMsg, 300)
									.show();
							if (errorCode == -999) {
								new AlertDialog.Builder(mContext)
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
	
	public void destroy() {
		if (null != overlay) {
			overlay.recycle();
			overlay = null;
			System.gc();
		}
		if (null != mBitmap) {
			mBitmap.recycle();
			mBitmap = null;
			System.gc();
		}
	}
	
}
