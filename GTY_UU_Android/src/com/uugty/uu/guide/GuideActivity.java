package com.uugty.uu.guide;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.util.CacheFileUtil;
import com.uugty.uu.login.LoginActivity;
import com.uugty.uu.main.MainActivity;

public class GuideActivity extends BaseActivity implements
		SurfaceHolder.Callback, OnCompletionListener {
	/** Called when the activity is first created. */
	private static final String TAG = "SurfaceActivity";
	private MediaPlayer player;
	private SurfaceView surface;
	private SurfaceHolder surfaceHolder;
	private int vWidth, vHeight;
	private Display currDisplay;
	private Button button;
	private Intent intent;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.guide;
	}

	@Override
	protected void initGui() {
		surface = (SurfaceView) findViewById(R.id.surface);
		button = (Button) findViewById(R.id.togo_button);
		surfaceHolder = surface.getHolder();// SurfaceHolder是SurfaceView的控制接口
		surfaceHolder.addCallback(this);// 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// Surface类型
		intent = new Intent();

	}

	@Override
	protected void initAction() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(GuideActivity.this, LoginActivity.class);
				startActivity(intent);
				GuideActivity.this.finish();
			}
		});

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		// 设置显示视频显示在SurfaceView上
		try {
			player.setDataSource(CacheFileUtil.rootPath+"/1.mp4");
			// 首先取得video的宽和高
			vWidth = player.getVideoWidth();
			vHeight = player.getVideoHeight();
			// 然后，我们取得当前Display对象
			currDisplay = this.getWindowManager().getDefaultDisplay();

			if (vWidth > currDisplay.getWidth()
					|| vHeight > currDisplay.getHeight()) {
				// 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
				float wRatio = (float) vWidth / (float) currDisplay.getWidth();
				float hRatio = (float) vHeight
						/ (float) currDisplay.getHeight();

				// 选择大的一个进行缩放
				float ratio = Math.max(wRatio, hRatio);

				vWidth = (int) Math.ceil((float) vWidth / ratio);
				vHeight = (int) Math.ceil((float) vHeight / ratio);

				// 设置surfaceView的布局参数
				surface.setLayoutParams(new LinearLayout.LayoutParams(vWidth,
						vHeight));
			}
			player.prepare();
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		// TODO Auto-generated method stub
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		// 当MediaPlayer播放完成后触发
		intent.setClass(GuideActivity.this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

}
