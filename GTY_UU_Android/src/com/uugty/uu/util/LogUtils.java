package com.uugty.uu.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * 日志打印管理类
 * 
 * @author lzh
 *
 */
public class LogUtils {
	
	/** 本地log日志文件存储路径 */
	private static String mLocalLogDir = "uugtv/";

	/** 微博分享保存的图片 */
	public final static String WEIBO_PATH
			= Environment.getExternalStorageDirectory() + "/UUimage/";

	/** 是否测试版本 */
	private static boolean isTest = true;
	/**
	 * <p>
	 * 打印日志。
	 * </p>
	 * 当EMPConfig.mIsPrintLog=false不打印此部分日志
	 * 
	 * @param className
	 * @param method
	 * @param msg
	 */
	public static void print(String className, String method, String msg) {
		if (isTest) {
			System.out.println("emp log class :" + className + " method :" + method + " msg :" + msg);
		}
	}

	/**
	 * <p>
	 * 打印日志。
	 * </p>
	 * 
	 * @param tag 打印的标签
	 * @param logObj 需要打印的对象
	 * 
	 * @see #printLog(String, String)
	 * @see #printLog(String, byte[])
	 */
	public static void printLog(String tag, Object logObj) {
		if (isTest) {
			if (tag == null) {
				tag = "UUDebug";
			}
			if (logObj instanceof String) {
				printLog(tag, (String) logObj);
			} else if (logObj instanceof byte[]) {
				printLog(tag, (byte[]) logObj);
			} else {
				System.out.print(tag + "：");
				System.out.println(logObj);
			}
		}
	}

	/**
	 * <p>
	 * 打印日志。
	 * </p>
	 * 
	 * @param tag 打印的标签
	 * @param logContent 需要打印的字符串
	 * 
	 * @see #printLog(String, Object)
	 * @see #printLog(String, byte[])
	 */
	public static void printLog(String tag, String logContent) {
		if (isTest) {
			if (tag == null) {
				tag = "UUDebug";
			}
			if (logContent == null) {
				logContent = "null";
			}

			int length = logContent.length();
			int offset = 3000;
			if (length == 0) {
				System.out.println(tag + "：");
			} else if (length > offset) {
				// 解决报文过长，打印不全的问题
				int n = 0;
				for (int i = 0; i < length; i += offset) {
					n += offset;
					if (n > length) {
						n = length;
					}
					android.util.Log.i(tag, logContent.substring(i, n));
				}
			} else {
				android.util.Log.i(tag, logContent);
			}
		}
	}

	/**
	 * <p>
	 * 打印日志。
	 * </p>
	 * 
	 * @param tag 打印的标签
	 * @param logByts 需要打印的比特数组
	 * 
	 * @see #printLog(String, Object)
	 * @see #printLog(String, String)
	 */
	public static void printLog(String tag, byte[] logByts) {
		if (isTest) {
			if (logByts == null) {
				return;
			}
			if (tag == null) {
				tag = "UUDebug";
			}
			for (int i = 0; i < logByts.length; i++) {
				System.out.print(tag + ": [" + i + "] : \t");
				System.out.println(logByts[i]);
			}
		}
	}

	/**
	 * 保存文件
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static String saveFile(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(WEIBO_PATH);
		if(!dirFile.exists()){
			dirFile.mkdir();
		}
		File myCaptureFile = new File(WEIBO_PATH + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
		bos.flush();
		bos.close();
		return fileName;
	}
	/**
	 * <p>
	 * 打印异常信息。
	 * </p>
	 * 
	 * @param e Exception。
	 */
	public static final void printException(Throwable e) {
		if (isTest) {
			android.util.Log.e("UUError", "", e);

			FileWriter writer = null;
			try {
				// 保存到本地文件
				SimpleDateFormat baseDateFormat = new SimpleDateFormat("yyyy_MM_dd");
				String dateStr = baseDateFormat.format(new Date()); //  日志按day区分
			     
				String mFilePath = getSDCardRoot() + mLocalLogDir.concat("errorlog/uu_android_errorlog_").concat(dateStr).concat(".log");
				File file = new File(mFilePath);
				if (!file.exists()) {
					// 判断父目录是否存在
					File fileDir = file.getParentFile();
					fileDir.mkdirs();
					file.createNewFile();
				}
				// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
				writer = new FileWriter(mFilePath, true);
				PrintWriter pw = new PrintWriter(writer);
				e.printStackTrace(pw);
				writer.write("\n");
			} catch (Exception e1) {
				android.util.Log.e("UUError", "", e1);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
					writer = null;
				} catch (IOException e2) {
					printException(e2);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * 获取SDCard路径。
	 * </p>
	 * 
	 * @return SD卡路径。
	 */
	public static final String getSDCardRoot() {
		String root = "";
		if (isSDCardExists()) {
			root = Environment.getExternalStorageDirectory().toString() + "/";
		}
		return root;
	}
	
	/**
	 * <p>
	 * 判断SDCard是否存在。
	 * </p>
	 * 
	 * @return SD卡存在与否。
	 */
	public static final boolean isSDCardExists() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
}
