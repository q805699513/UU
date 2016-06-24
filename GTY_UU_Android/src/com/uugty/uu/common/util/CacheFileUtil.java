package com.uugty.uu.common.util;

import java.io.File;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 缓存文件夹和文件的存储
 */
public class CacheFileUtil {

	private static final String TAG ="CacheFileUtil";
	// SD卡路径
	public final static String SD_PATH = Environment.getExternalStorageDirectory().getPath();

	// 缓存数据根目录
	public static String rootPath = SD_PATH + "/.UU/";
	//拍照缓存目录
	public static String carmePaht=rootPath+"PICTURE/";
	
	/**
	 * 首次进入应用的时候将各种目录创建好
	
	 */
	public static void initCreateFiles() {
		if (isExistSDCard()) {
			String[] paths = new String[] { rootPath,carmePaht};
			int size = paths.length;
			for (int i = 0; i < size; i++) {
				String path = paths[i];
				File file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
			}
		} else {
			Log.e(TAG, "无SD卡");
		}
	}

	/**
	 * 检测SD卡是否存在
	 * 
	 * @Description
	 * @return
	 */
	public static boolean isExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 删除SD卡缓存文件目录
	 * 
	 * @Description
	 * @param context
	 */
	public static void deleteCacheFile(Context context) {
		File cache = null;
		if (Environment.getExternalStorageState().equals("mounted")) {
			cache = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Android/data/"
					+ context.getPackageName() + "/cache/");
			deleteDirectory(cache);
		}
	}

	/**
	 * 递归删除文件夹
	 */
	private static void deleteDirectory(File file) {
		// if (file.isFile()) {
		// file.delete();
		// return;
		// }

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteDirectory(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath)
	{
		boolean flag = false;

		if (!sPath.endsWith(File.separator))
		{
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory())
		{
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			
			if (files[i].isFile())
			{
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			else
			{
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		return flag;
		/*if (dirFile.delete())
		{
			return true;
		}
		else
		{
			return false;
		}*/
	}


	public static boolean deleteFile(String sPath)
	{
		boolean flag = false;
		flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists())
		{
			file.delete();
			flag = true;
		}
		return flag;
	}

}
