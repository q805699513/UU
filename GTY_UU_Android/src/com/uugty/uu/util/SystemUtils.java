package com.uugty.uu.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.uugty.uu.base.application.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemUtils {
	/**
	 * Used for gzip. Re. rfc1951.
	 */
	static int gIndex = 0;
	static int bitByte = 0;
	static int bitIndex = 0;
	private static final int MAX_BITS = 16;
	private static final int[] EXTRA_L_BITS = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0 };
	private static final int[] EXTRA_L_VALUES = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258 };
	private static final int[] EXTRA_D_BITS = { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
	private static final int[] EXTRA_D_VALUES = { 1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577 };
	private static final int[] DYNAMIC_L_ORDER = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

	/** MATCH常量 */
	public static final char MATCH = '\"';
	/** encodeURI安全字符 */
	private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
	
	/** 上送日志时候的时间格式。 */
	private static final SimpleDateFormat mLogDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	/**
	 * <p>
	 * 获取客户端版本号。
	 * </p>
	 * 
	 * @param context Context
	 * 
	 * @return 客户端版本号。
	 */
	public static final String getVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

	/**
	 * <p>
	 * User-Agent string we use in all of our HTTP transactions.
	 * </p>
	 * 
	 * @param version system version number
	 * 
	 * @return transformed result.
	 */
	public static final String makeUserAgent(String version) {
		StringBuffer buf = new StringBuffer("LightPole/");
		buf.append(version);
		buf.append("/");
		buf.append("android1.5");
		return buf.toString();
	}

	/**
	 * <p>
	 * 获得客户端时区信息。
	 * </p>
	 * 
	 * @return byte[]客户端时区信息的比特数组。
	 */
	public static final byte[] getClientGMTUnixTime() {
		// get local time. 4 bytes.
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		int hours = date.getHours();
		byte[] clientGmtUnixTime = new byte[4];
		clientGmtUnixTime[0] = (byte) ((hours & 0x0000FF00) >> 8);
		clientGmtUnixTime[1] = (byte) ((hours & 0x000000FF));
		int minutes = date.getMinutes();
		clientGmtUnixTime[2] = (byte) ((minutes & 0x0000FF00) >> 8);
		clientGmtUnixTime[3] = (byte) ((minutes & 0x000000FF));
		return clientGmtUnixTime;
	}

	/**
	 * <p>
	 * 修正日期值的格式，将"-"予以去除，月份日期小于10，则前面予以0。
	 * </p>
	 * 
	 * @param dateStr 源日期字符串
	 * 
	 * @return 返回日期字符串
	 */
	public static final String adjustDate(String dateStr) {
		if (dateStr.contains("-")) {
			String[] dateTmp = dateStr.split("-");
			String year = dateTmp[0];
			String month = dateTmp[1];
			String day = dateTmp[2];
			if (Integer.parseInt(month) < 10 && month.length() < 2) {
				month = "0".concat(month);
			}
			if (Integer.parseInt(day) < 10 && day.length() < 2) {
				day = "0".concat(day);
			}
			return year.concat(month).concat(day).replace("-", "");
		}
		return dateStr;
	}

	/**
	 * <p>
	 * Divide the given string to paragraphs with the given font and width.
	 * </p>
	 * 
	 * @param strSource the source string
	 * @param font the font
	 * @param width the width of each line
	 * 
	 * @return the String array.
	 */
	public static final String[] getParagraph(final String strSource, final Paint font, final int width) {
		String[] strs = null;
		if (strSource == null || font == null || width < 0) {
			return strs;
		} else if (strSource.equals("")) {
			strs = new String[] { "" };
			return strs;
		} else if (strSource.length() == 1 || width == 0) {
			strs = new String[] { strSource };
			return strs;
		}
		// 首先判断给定宽度是否能显示所有内容
		if (font.measureText(strSource) <= width) {
			strs = new String[] { strSource };
		} else {
			Vector<String> vecRes = new Vector<String>();
			// 查找字符串内有没有换行符
			String[] strSAry = strSource.split("\n");
			try {
				// 其次判断给定宽度单行能显示几个字符
				float sW = font.measureText(".");
				int num = (int) (width / sW);  // 每行可以显示的字符数
				num = (num == 0) ? 1 : num;  // 如果给定宽度默认不能显示一个字符，则以一个字符断行
				// 将给定的字符串按单行能显示的字符数做截取
				for (int i = 0; i < strSAry.length; i++) {
					char[] chs = strSAry[i].toCharArray();
					int size = chs.length;  // 文本长度
					int pos = 0;  // 当前字符索引的位置
					float cW = 0;  // 存储本行字符显示宽度
					// 如果当前文本内没有遍历完
					while (pos < size) {
						// 计算剩余字符的个数
						int last = size - pos;
						// 获取当前行显示的字符个数
						int step = num < last ? num : last;
						// 计算这些字符显示的宽度
						cW = font.measureText(chs, pos, step);
						int off = 0;  // 存储偏移量
						// 如果当前行字符宽度小于控件宽度，且文本还剩余字符未遍历。
						// 则一个一个字符增加偏移量，直到当前行字符宽度大于控件宽度或者文本遍历结束为止。
						while (cW + sW < width && pos + step + off + 1 < size) {
							off++;
							cW = font.measureText(chs, pos, step + off);
						}
						// 如果当前行字符宽度大于控件宽度，则一个一个字符增加偏移量，
						// 直到当前行字符宽度小于于控件宽度或者当前行字符只剩一个为止。
						while (cW > width && step + off > 1) {
							off--;
							cW = font.measureText(chs, pos, step + off);
						}
						// 保存当前行字符
						vecRes.add(new String(chs, pos, step + off));
						// 重定位字符索引
						pos += step + off;
					}
					// 最后一行
//					if (pos <= size - 1) {
//						vecRes.add(new String(chs, pos, size - pos));
//					}
				}

				int nums = vecRes.size();
				if (nums > 0) {
					strs = new String[nums];
					vecRes.copyInto(strs);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return strs;
	}

	/**
	 * 从字符串中截取整数<br/>
	 * 具体作用：可以去掉 px/nat/小数点后的数
	 * 
	 * @param value
	 * 
	 * @return 转换异常返回0
	 */
	public static int getNumberFromValue(String value) {
		int valueInt = 0;
		value = value.trim().replace("\n", "");
		int index = value.indexOf(".");
		if (index > 0) {
			value = value.substring(0, index);
		} else if (value.endsWith("px")) {
			value = value.replace("px", "");
		} else if (value.endsWith("nat")) {
			value = value.replace("nat", "");
		}
		try {
			valueInt = Integer.parseInt(value);
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		return valueInt;
	}

	/**
	 * <p>
	 * 获取资源id。
	 * </p>
	 * 
	 * @param context Context
	 * @param resourcesName 资源名称
	 * @param resourcesType 资源类型
	 * 
	 * @return 资源id。
	 */
	public static final int getResourcesId(Context context, String resourcesName, String resourcesType) {

		int resourcesId = 0;
		resourcesId = context.getResources().getIdentifier(resourcesName, resourcesType, context.getPackageName());
		return resourcesId;
	}

	/**
	 * <p>
	 * 获取Color值。
	 * </p>
	 * 
	 * @param value 颜色字符串
	 * 
	 * @return int型颜色值
	 */
	public static final int getColorFromStr(String value) {
		try {
			if (value.length() > 6) {
				int color = Integer.parseInt(value, 16);
				color |= 0x00000000;
				return color;
			} else {
				int color = Integer.parseInt(value, 16);
				color |= 0xFF000000;
				return color;
			}
		} catch (Exception e) {
			return 0xFFFFFFFF;
		}
	}

	/**
	 * <p>
	 * 获取手机品牌。
	 * </p>
	 * 
	 * @return 手机品牌
	 */
	public static final String getDeviceName() {
		return android.os.Build.DEVICE;
	}

	/**
	 * <p>
	 * 当前系统平台。
	 * </p>
	 * 
	 * @return "Android"
	 */
	public static final String getPlatform() {
		return "Android";
	}

	/**
	 * <p>
	 * 获取手机操作系统版本号。
	 * </p>
	 * 
	 * @return 手机操作系统版本号
	 */
	public static final String getVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * <p>
	 * 获取sdk版本。
	 * </p>
	 * 
	 * @return 手机获取sdk版本
	 */
	public static final int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * <p>
	 * 获取手机型号。
	 * </p>
	 * 
	 * @return 手机型号。
	 */
	public static final String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * <p>
	 * 手机机主名称。
	 * </p>
	 * 
	 * @return 机主名称
	 */
	public static final String getName() {
		String name = android.os.Build.USER;
		if (isEmpty(name)) {
			name = null;
		}
		return name;
	}

	/**
	 * <p>
	 * 获取设备IMEI。
	 * </p>
	 * 
	 * @return 设备IMEI
	 */
	public static String getIMEI() {
		final Context context = MyApplication.getInstance().getContext();
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (!isEmpty(imei)) {
			return imei;
		} else {
			// 获取mac地址
			WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wifiMan != null) {
				WifiInfo wifiInf = wifiMan.getConnectionInfo();
				if (wifiInf != null && wifiInf.getMacAddress() != null) {// 48位，如FA:34:7C:6D:E4:D7
					imei = wifiInf.getMacAddress().replaceAll(":", "");
					return imei;
				}
			}
		}
		return "";
	}
	
	/**
	 * 在控制台显示device.time的结果(也可以将这个结果存放到文件中)。
	 * @return
	 */
	public static String getDeviceTestTime() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		return format.format(new Date());
	}

	/**
	 * <p>
	 * 获取设备UUID，由设备信息产生
	 * </p>
	 * 
	 * @return 设备UUID。
	 */
	public static final String getUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}

	/**
	 * <p>
	 * 获取手机的设备名称。
	 * </p>
	 * 
	 * @return 手机设备名称字符串。
	 */
	public static final String getPhoneTarget() {
		String temp = android.os.Build.DEVICE;
		temp = temp.replace(" ", "").replace("-", "_").trim();
		return temp;

	}

	/**
	 * <p>
	 * 获取设备ID。
	 * </p>
	 * 
	 * @return 设备名称id。
	 */
	public static final String getClientID() {
		String temp = android.os.Build.ID;
		temp = temp.replace(" ", "").replace("-", "_").trim();
		return temp;
	}
	
	/**
	 * 获取网络状态
	 * @return 2G、3G、4G、WIFI、UNKNOWN、null
	 */
	public static final String getNetworkType() {

		String strNetworkType = null;

		final Context context = MyApplication.getInstance().getContext();
		NetworkInfo networkInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String strSubTypeName = networkInfo.getSubtypeName();

				int networkType = networkInfo.getSubtype();
				switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: 
					strNetworkType = "2G";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: 
				case TelephonyManager.NETWORK_TYPE_EHRPD: 
				case TelephonyManager.NETWORK_TYPE_HSPAP: 
					strNetworkType = "3G";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE: 
					strNetworkType = "4G";
					break;
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					strNetworkType = "UNKNOWN";
				default:
					// 中国移动 联通 电信 三种3G制式
					if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
							|| strSubTypeName.equalsIgnoreCase("WCDMA")
							|| strSubTypeName.equalsIgnoreCase("CDMA2000")) {
						strNetworkType = "3G";
					} 
					break;
				}
			}
		}
		
		return strNetworkType;
	}

	/**
	 * <p>
	 * 查看android手机是否被刷过机.linux系统下root权限可以使用su命令，android本身也是小型的linux系统。
	 * </p>
	 * 
	 * @param command "id" 或其他的 su命令。
	 */
	public static final boolean runRootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				// nothing
			}
		}
	}

	/**
	 * <p>
	 * Network Byte Order.
	 * </p>
	 * 
	 * @param byts source byte array.
	 * @param offset the offset.
	 * 
	 * @return transformed value.
	 * 
	 * @see #intToByteArrayInNBO(int)
	 */
	public static final int byteArrayToIntInNBO(byte[] byts, int offset) {
		int intValue = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			intValue += (byts[i + offset] & 0x000000FF) << shift;
		}
		return intValue;
	}

	/**
	 * <p>
	 * Network Byte Order.
	 * </p>
	 * 
	 * @param intValue source value
	 * 
	 * @return transformed byte array
	 * 
	 * @see #byteArrayToIntInNBO(byte[], int)
	 */
	public static final byte[] intToByteArrayInNBO(int intValue) {
		byte[] byt = new byte[4];
		for (int i = 0; i < 4; i++) {
			byt[i] = (byte) (intValue >>> (24 - i * 8));
		}
		return byt;
	}

	/**
	 * Transform bytes.
	 * 
	 * @param byts source byte array
	 * 
	 * @return transformed byte array.
	 * 
	 */
	public static final byte[] joinBytes(byte[]... byts) throws Exception {
		if (byts.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (byte[] byt : byts) {
			if (byt == null) {
				continue;
			}
			out.write(byt);
		}
		try {
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] result = out.toByteArray();
		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out = null;
		return result;
	}

	/**
	 * get num bits from data. the num should not bigger than 32.
	 * 
	 * @param data
	 * @param num
	 * 
	 * @return
	 */
	private static final int getBits(byte[] data, int num) {
		int result = 0;
		int i = 0;
		if (bitIndex != 0) {
			i = 8 - bitIndex;
			result = bitByte >> bitIndex;
		}
		while (i < num) {
			bitByte = data[gIndex++] & 0xFF;
			result |= (bitByte << i);
			i += 8;
		}
		bitIndex = (bitIndex + num) & 0x7;
		result &= (0xffffffff >>> (32 - num));
		return result;
	}

	/**
	 * Get a code from data.
	 * 
	 * @param data
	 * @param tree
	 * 
	 * @return
	 */
	private static final int getCode(byte data[], int tree[]) {
		int node = tree[0];
		while (node >= 0) {
			if (bitIndex == 0) {
				bitByte = (data[gIndex++] & 0xFF);
			}
			node = (((bitByte & (1 << bitIndex)) == 0) ? tree[node >> 16] : tree[node & 0xFFFF]);
			bitIndex = (bitIndex + 1) & 0x7;
		}
		return (node & 0xFFFF);
	}

	/**
	 * 生成一个哈夫曼树
	 * 
	 * @param bits
	 * 
	 * @return
	 */
	private static final int[] huffmanTree(byte bits[]) {
		int bl_count[] = new int[MAX_BITS + 1];
		for (int i = 0; i < bits.length; i++) {
			bl_count[bits[i]]++;
		}
		int code = 0;
		bl_count[0] = 0;
		int next_code[] = new int[MAX_BITS + 1];
		// Count the number of codes for each code length.
		for (int i = 1; i <= MAX_BITS; i++) {
			code = (code + bl_count[i - 1]) << 1;
			next_code[i] = code;
		}
		int tree[] = new int[((bits.length - 1) << 1) + MAX_BITS];
		int treeInsert = 1;
		for (int i = 0; i < bits.length; i++) {
			int len = bits[i];
			if (len != 0) {
				code = next_code[len]++;
				int node = 0;
				for (int bit = len - 1; bit >= 0; bit--) {
					int value = code & (1 << bit);
					if (value == 0) {
						int left = tree[node] >> 16;
						if (left == 0) {
							tree[node] |= (treeInsert << 16);
							node = treeInsert++;
						} else {
							node = left;
						}
					} else {
						int right = tree[node] & 0xFFFF;
						if (right == 0) {
							tree[node] |= treeInsert;
							node = treeInsert++;
						} else {
							node = right;
						}
					}
				}
				tree[node] = 0x80000000 | i;
			}
		}
		return tree;
	}

	/**
	 * Decompress the literal/length code and the distance code.
	 * 
	 * @param data
	 * @param blTree
	 * @param count
	 * 
	 * @return
	 */
	private static final byte[] decompressCode(byte data[], int blTree[], int count) {
		int code = 0;
		byte previousCode = 0;
		int times = 0; // The number of the previous code's length need to repeat.
		byte treeBits[] = new byte[count];
		int index = 0;
		while (index < count) {
			code = getCode(data, blTree);
			if (code == 16) {
				times = getBits(data, 2) + 3;
			} else if (code == 17) {
				times = getBits(data, 3) + 3;
				previousCode = 0;
			} else if (code == 18) {
				times = getBits(data, 7) + 11;
				previousCode = 0;
			} else {
				times = 0;
				previousCode = (byte) code;
				treeBits[index++] = (byte) code;
			}
			for (int i = 0; i < times; i++) {
				treeBits[index++] = previousCode;
			}
		}
		return treeBits;
	}

	/**
	 * <p>
	 * 取出字符串中第一组数字<br>
	 * 如：abcd0.6667saf58af;取出为float f = 0.6667;
	 * </p>
	 * 
	 * @param str 给定字符串
	 * 
	 * @return 字符串中含有的第一个数字。
	 */
	public static float getDigital(String str) {
		String cache = "";
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == 46 || (str.charAt(i) >= 48 && str.charAt(i) <= 57)) {
				cache += str.charAt(i);
			} else if (cache.length() > 0) {
				break;
			}
		}
		return Float.valueOf(cache);
	}

	/**
	 * <p>
	 * Parse simple state response XMLs from server and retrieve attributes from it. Used by UserManager, PoiRepository(add/remove POI), etc.
	 * </p>
	 * 
	 * @param reply response from server
	 * @param attr the sttribute string
	 * @param startIndex the start position
	 * @param matchCh the char used to match
	 * 
	 * @return a parsed XML string.
	 */
	public static final String getXMLResponseAttribute(String reply, String attr, int startIndex, char matchCh) {
		int start = reply.indexOf(attr, startIndex);
		if (start < 0) {
			return null;
		}
		start += attr.length();
		int end = reply.indexOf(matchCh, start);
		if (end > start) {
			String s = reply.substring(start, end);
			return s;
		}
		return null;
	}

	/**
	 * <p>
	 * 所有使用到的URL中，特殊字符(例如 & < >)必须使用该方法进行转换。
	 * </p>
	 * 
	 * @param str 给定字符串
	 * 
	 * @return 转换后的字符串
	 */
	public static final String escapeHTML(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int num = str.length();
		for (int i = 0; i < num; i++) {
			char ch = str.charAt(i);
			switch (ch) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case ' ':
				sb.append("&nbsp;");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * <p>
	 * Unescape a HTML string by filtering out sequences, including &# {@literal <numbers>};. We do not consider the case where one sequence is broken into multiple lines.
	 * </p>
	 * 
	 * @param str source string
	 * 
	 * @return unescaped string.
	 */
	public static final String unescapeHTML(String str) {
		char[] src = str.toCharArray();
		char[] dest = new char[src.length];
		int escapeIdx = -1;
		int dstIdx = 0;
		int j;
		int ch;

		for (int i = 0; i < src.length; i++) {
			switch (src[i]) {
			case '&': {
				// we should not miss the case &aaa&.
				while (escapeIdx > 0 && escapeIdx < i) {
					dest[dstIdx++] = src[escapeIdx++];
				}
				escapeIdx = i;
				break;
			}
			case ';': {
				if (escapeIdx >= 0) {
					if (src[escapeIdx + 1] == '#') {
						ch = 0;
						for (j = escapeIdx + 2; j < i; ++j) {
							if (src[j] < '0' || src[j] > '9') {
								ch = -1;
								break;
							} else {
								ch *= 10;
								ch += src[j] - '0';
							}
						}
					} else {
						int pp1 = escapeIdx + 1;
						int pp2 = escapeIdx + 2;
						int pp3 = escapeIdx + 3;
						int pp4 = escapeIdx + 4;
						int pp5 = escapeIdx + 5;
						if (src[pp1] == 'q' && src[pp2] == 'u' && src[pp3] == 'o' && src[pp4] == 't' && pp5 == i) {
							ch = '"';
						} else if (src[pp1] == 'a' && src[pp2] == 'm' && src[pp3] == 'p' && pp4 == i) {
							ch = '&';
						} else if (src[pp1] == 'l' && src[pp2] == 't' && pp3 == i) {
							ch = '<';
						} else if (src[pp1] == 'g' && src[pp2] == 't' && pp3 == i) {
							ch = '>';
						} else if (src[pp1] == 'n' && src[pp2] == 'b' && src[pp3] == 's' && src[pp4] == 'p' && pp5 == i) {
							ch = ' ';
						} else {
							ch = -1;
						}
						if (ch != -1) {
							dest[dstIdx++] = (char) ch;
							escapeIdx = -1;
							break;
						}
					}
				} else {
					ch = -1;
				}

				// Replace the Html Special Characters likes &#<numbers>.
				if (ch >= 0) {
					switch (ch) {
					case 123: {
						ch = '{';
						break;
					}
					case 125: {
						ch = '}';
						break;
					}
					case 133: {
						for (int k = 0; k < 3; k++) {
							dest[dstIdx++] = '.';
						}
						ch = -1;
						break;
					}
					case 146: {
						ch = '\'';
						break;
					}
					case 147:
					case 148: {
						ch = '"';
						break;
					}
					case 151: {
						ch = '-';
						break;
					}
					default: {
						ch = ' ';
						break;
					}
					}
					if (ch != -1) {
						dest[dstIdx++] = (char) ch;
					}
				} else {
					// If current charactor is only a characters ";",
					// not a Html Special Characters, we copy it in dest.
					if (escapeIdx < 0) {
						dest[dstIdx++] = src[i];
					} else {
						for (j = escapeIdx; j <= i; ++j) {
							dest[dstIdx++] = src[j];
						}
					}
				}
				escapeIdx = -1;
				break;
			}
			default: {
				if (escapeIdx < 0) {
					dest[dstIdx++] = src[i];
				} else if (escapeIdx > 0 && ((i - escapeIdx) > 5)) {
					for (int k = escapeIdx; k <= i; k++) {
						dest[dstIdx++] = src[k];
					}
					escapeIdx = -1;
				}
				break;
			}
			}
		}

		return String.valueOf(dest, 0, dstIdx);
	}

	/**
	 * <p>
	 * non-ASCII characters are encoded as: first using the UTF-8 algorithm to encode to a sequence of 2 or 3 bytes, then each of these bytes is encoded as "%xx".
	 * </p>
	 * 
	 * @param str source string
	 * 
	 * @return transformed string
	 */
	public static final String escapeURIComponent(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer sbuf = new StringBuffer();
		int ch;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			ch = str.charAt(i);
			if ('A' <= ch && ch <= 'Z') {
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {
				sbuf.append((char) ch);
			} else if (ch == '-' || ch == '_' || ch == '.' || ch == '!' || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
				sbuf.append((char) ch);
			} else if (ch < 0x0F) {
				sbuf.append('%');
				sbuf.append('0');
				sbuf.append(Integer.toHexString(ch));
			} else if (ch < 0x7F) {
				sbuf.append('%');
				sbuf.append(Integer.toHexString(ch));
			} else if (ch <= 0x07FF) {
				// non-ASCII characters and value <= 0x7FF
				sbuf.append('%');
				sbuf.append(Integer.toHexString(0xc0 | (ch >> 6)));
				sbuf.append('%');
				sbuf.append(Integer.toHexString(0x80 | (ch & 0x3F)));
			} else {
				// non-ASCII characters and value <= 0xFFFF
				sbuf.append('%');
				sbuf.append(Integer.toHexString(0xe0 | (ch >> 12)));
				sbuf.append('%');
				sbuf.append(Integer.toHexString(0x80 | ((ch >> 6) & 0x3F)));
				sbuf.append('%');
				sbuf.append(Integer.toHexString(0x80 | (ch & 0x3F)));
			}
		}
		return sbuf.toString();
	}
	
	/**
	 * encodeURI编码的安全字符（82个）：!#$&'()*+,/:;=?@-._~0-9a-zA-Z
	 * @param str
	 * @return
	 */
	public static final String escapeURI(String str) {
		if (str == null) {
			return null;
		}
		return Uri.encode(str, ALLOWED_URI_CHARS);
	}
	
	public static final String unescapeURI(String str){
		return Uri.decode(str);
	}

	/**
	 * 对给定字符串进行unescape操作。
	 * 
	 * @param url 给定字符串
	 * 
	 * @return 转换后的字符串。
	 */
	public static final String unescapeURIComponent(String url) {
		if (url == null)
			return null;
		StringBuffer sbuf = new StringBuffer();
		int len = url.length();
		int ch;
		int b;
		int total = 0;
		int more = -1;
		int hb;
		int lb;
		for (int i = 0; i < len; i++) {
			switch (ch = url.charAt(i)) {
			case '%':
				ch = url.charAt(++i);
				hb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				ch = url.charAt(++i);
				lb = (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
				b = (hb << 4) | lb;
				break;
			default:
				b = ch;
			}
			// Decode byte b as UTF-8, sumb collects incomplete chars
			if ((b & 0xc0) == 0x80) {
				total = (total << 6) | (b & 0x3f);
				if (--more == 0)
					sbuf.append((char) total);
			} else if ((b & 0x80) == 0x00) {
				sbuf.append((char) b);
			} else if ((b & 0xe0) == 0xc0) {
				total = b & 0x1f;
				more = 1;
			} else if ((b & 0xf0) == 0xe0) {
				total = b & 0x0f;
				more = 2;
			} else if ((b & 0xf8) == 0xf0) {
				total = b & 0x07;
				more = 3;
			} else if ((b & 0xfc) == 0xf8) {
				total = b & 0x03;
				more = 4;
			} else {
				total = b & 0x01;
				more = 5;
			}
		}
		return sbuf.toString();
	}
	
	/**
	 * 对给定字符串做js转义
	 * 
	 * @param strSource 给定字符串
	 * 
	 * @return 转换后的字符串。
	 */
	public static final String escapeJSComponent(String strSource) {
		if (isEmpty(strSource)) {
			return strSource;
		}
		String strRes = strSource;
		strRes = strRes.replace("\\", "\\\\")
				.replace("'", "\'")
				.replace("\"", "\\\"")
				.replace("&", "\\&")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t")
				.replace("\b", "\\b")
				.replace("\f", "\\f");
		return strRes;
	}

	/**
	 * <p>
	 * 得到与matrix对应的map {m11=0,m12=3,m13=6, m21=1,m22=4,m23=7, m31=2,m32=5,m33=8, }
	 * </p>
	 * 
	 * @return Map对象，存储的是String-Integer键值对。
	 */
	public static Map<String, Integer> getMatrixIndex() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("m11", 0);
		map.put("m21", 1);
		map.put("m31", 2);
		map.put("m12", 3);
		map.put("m22", 4);
		map.put("m32", 5);
		map.put("m13", 6);
		map.put("m23", 7);
		map.put("m33", 8);
		return map;
	}

	/**
	 * multiply the given Matrix
	 * <p/>
	 * This in effect does this = matrix1*matrix2
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	public static float[] multiplyMatrix(float[] matrix1, float[] matrix2) {
		float[] tmp = new float[9];
		// first row
		tmp[0] = matrix1[0] * matrix2[0] + matrix1[1] * matrix2[3] + matrix1[2] * matrix2[6];
		tmp[1] = matrix1[0] * matrix2[1] + matrix1[1] * matrix2[4] + matrix1[2] * matrix2[7];
		tmp[2] = matrix1[0] * matrix2[2] + matrix1[1] * matrix2[5] + matrix1[2] * matrix2[8];

		// 2nd row
		tmp[3] = matrix1[3] * matrix2[0] + matrix1[4] * matrix2[3] + matrix1[5] * matrix2[6];
		tmp[4] = matrix1[3] * matrix2[1] + matrix1[4] * matrix2[4] + matrix1[5] * matrix2[7];
		tmp[5] = matrix1[3] * matrix2[2] + matrix1[4] * matrix2[5] + matrix1[5] * matrix2[8];

		// 3rd row
		tmp[6] = matrix1[6] * matrix2[0] + matrix1[7] * matrix2[3] + matrix1[8] * matrix2[6];
		tmp[7] = matrix1[6] * matrix2[1] + matrix1[7] * matrix2[4] + matrix1[8] * matrix2[7];
		tmp[8] = matrix1[6] * matrix2[2] + matrix1[7] * matrix2[5] + matrix1[8] * matrix2[8];

		return tmp;
	}

	/**
	 * 判断字符串是否为null或" "
	 * 
	 * @param str
	 * 
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static float objectToFloat(Object content) {
		if (content == null) {
			return 0;
		}
		if (content instanceof String) {
			return Float.valueOf((String) content);
		} else if (content instanceof Double) {
			return new Float((Double) content);
		} else if (content instanceof Float) {
			return ((Float) content).floatValue();
		} else if (content instanceof Integer) {
			return ((Integer) content).floatValue();
		}
		return 0;
	}

	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param activity
	 * 
	 * @return 实际屏幕分辨率 "width*height"格式
	 */
	public static String getScreenResolution(Activity activity) {
		String screenResolution = "";
		if (activity != null) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenResolution = String.valueOf(dm.widthPixels) + "*" + String.valueOf(dm.heightPixels);
		}
		return screenResolution;
	}

	public static final int convertValueToInt(CharSequence charSeq, int defaultValue) {
		if (null == charSeq) {
			return defaultValue;
		}
		String nm = charSeq.toString();

		int value;
		int sign = 1;
		int index = 0;
		int len = nm.length();
		int base = 10;

		if ('-' == nm.charAt(0)) {
			sign = -1;
			index++;
		}

		if ('0' == nm.charAt(index)) {
			// Quick check for a zero by itself
			if (index == (len - 1))
				return 0;
			char c = nm.charAt(index + 1);
			if ('x' == c || 'X' == c) {
				index += 2;
				base = 16;
			} else {
				index++;
				base = 8;
			}
		} else if ('#' == nm.charAt(index)) {
			index++;
			base = 16;
		}
		return Integer.parseInt(nm.substring(index), base) * sign;
	}

	/**
	 * 获取当前应用程序的入口类名
	 * 
	 * @param context
	 * @return
	 */
	public static String getClassName(Context context) {
		String className = "";

		if (context == null) {
			return className;
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(context.getPackageName());

		final List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			className = ri.activityInfo.name;
		}
		return className;
	}
	
	/**
	 * "false" return false,true otherwise.
	 * @param str
	 * @return
	 */
	public static boolean parseBoolean(String str) {

		if ("false".equalsIgnoreCase(str)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 为指定的颜色附加上透明度。
	 * @param originalColor 原始颜色。
	 * @param addedAlpha 待附加的透明度。
	 * @return 经过处理后的颜色。
	 */
	public static int getColorWithAlpha(int originalColor, float addedAlpha) {
		int originalAlpha = Color.alpha(originalColor);
		int originalRed = Color.red(originalColor);
		int originalGreen = Color.green(originalColor);
		int originalBlue = Color.blue(originalColor);
		return Color.argb((int) (addedAlpha * originalAlpha), originalRed, originalGreen, originalBlue);
	}
}
