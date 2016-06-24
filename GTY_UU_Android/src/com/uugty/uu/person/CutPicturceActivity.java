package com.uugty.uu.person;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.myview.picture.ClipImageLayout;
import com.uugty.uu.common.util.ActivityCollector;
import com.uugty.uu.loaderimg.MyAdapter;

public class CutPicturceActivity extends BaseActivity implements
		OnClickListener {

	private ClipImageLayout mClipImageLayout;
	// 图片的地址
	private String url;
	// 确定图片
	private LinearLayout confirmImage;
	// 那个页面跳转的
	private String topageFrom;
	private RelativeLayout okRel;

	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.cut_picture;
	}

	@Override
	protected void initGui() {
		confirmImage = (LinearLayout) findViewById(R.id.tabar_back);
		okRel =(RelativeLayout) findViewById(R.id.cut_picture_ok);
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.cut_picturce_tool);
		Intent intent = getIntent();
		url = intent.getStringExtra("picPath");
		topageFrom = intent.getStringExtra("topageFrom");
		String shape = intent.getStringExtra("shape");
		
		
		if("circle".equals(shape)){
			mClipImageLayout.setIsCircle(true);
			ServiceCode.FIT_LINE=1.0f;
		}else{
			ServiceCode.FIT_LINE=0.5833F;
		}
		mClipImageLayout.setPic(url);
	}

	@Override
	protected void initAction() {
		confirmImage.setOnClickListener(this);
		okRel.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		confirmImage.setClickable(true);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
       super.onClick(v);
	}
	
	@Override
	public void onNoDoubleClick(View v) {
		switch (v.getId()) {
		case R.id.tabar_back:
			confirmImage.setClickable(false);
			finish();
			break;
		case R.id.cut_picture_ok:
			ActivityCollector.removeSpecifiedActivity("com.uugty.uu.loaderimg.PhoneimageActivity");
			final Bitmap bitmap = mClipImageLayout.clip();
			/*new Thread() {
				@Override
				public void run() {
					
				}
			}.start();*/
			saveMyBitmap(url, bitmap);
			String picSelect = getIntent().getStringExtra("picSelect");
			if(picSelect!=null){
				MyAdapter.mSelectedImage.add(picSelect);
			}
			Intent intent = new Intent();
			Class c = null;
			try {
				c = Class.forName(topageFrom);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			intent.setClass(this, c);
			intent.putExtra("resultPic", url);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

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
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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

	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	private int getBitmapDegree(String path) {
	    int degree = 0;
	    try {
	        // 从指定路径下读取图片，并获取其EXIF信息
	        ExifInterface exifInterface = new ExifInterface(path);
	        // 获取图片的旋转信息
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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
	        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
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
	
	public static Bitmap createImageThumbnail(String filePath){  
	     Bitmap bitmap = null;  
	     BitmapFactory.Options opts = new BitmapFactory.Options();  
	     opts.inJustDecodeBounds = true;  
	     BitmapFactory.decodeFile(filePath, opts);  
	  
	     opts.inSampleSize =1;
	     opts.inJustDecodeBounds = false;  
	  
	     try {  
	         bitmap = BitmapFactory.decodeFile(filePath, opts);  
	     }catch (Exception e) {  
	        // TODO: handle exception  
	    }  
	    return bitmap;  
	}  
	  
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {  
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}  
	  
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {  
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
	    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));  
	    if (upperBound < lowerBound) {  
	        // return the larger one when there is no overlapping zone.  
	        return lowerBound;  
	    }  
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
	        return 1;  
	    } else if (minSideLength == -1) {  
	        return lowerBound;  
	    } else {  
	        return upperBound;  
	    }  
	}  
}
