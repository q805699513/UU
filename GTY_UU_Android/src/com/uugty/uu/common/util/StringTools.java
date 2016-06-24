package com.uugty.uu.common.util;

import java.util.Random;


public abstract class StringTools {
	
	
	private StringTools() {
		throw new InstantiationError("工具类无法实例化");
	}

	/**
	 * 统计字符串长�?,�?个双字节字符长度�?2，ASCII字符�?1
	 * 
	 * @param str
	 *        字符�?
	 */
	public static int getLength(String str) {
		return str.replaceAll("[^\\x00-\\xff]", "aa").length();
	}

	public static String getStringByBytes(byte[] bs) {
		return new String(bs);
	}

	/**
	 * 判断字符串是否为空，即为null�?""
	 */
	public static boolean isEmpty(String str) {
		return ((str == null) || (str.length() == 0));
	}

	/**
	 * 判断字符串是否不为空，即不为null且不�?""
	 */
	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}

	/**
	 * 判断字符串是否为空白，即为null或为" "
	 */
	public static boolean isBlank(String str) {
		return ((str == null) || (str.trim().length() == 0));
	}

	/**
	 * 判断字符串是否不为空白，即不为null且不�?" "
	 */
	public static boolean isNotBlank(String str) {
		return (!(isBlank(str)));
	}

	/**
	 * 生成长度�?5�?10的随机字符串. 随机字符串的内容包含[1-9 A-Z a-z]的字�?.
	 * 
	 * @return 随机字符�?.
	 */
	public static String buildRandomString() {
		return buildRandomString(5, 10);
	}

	/**
	 * 生成随机字符�?. 随机字符串的内容包含[1-9 A-Z a-z]的字�?.
	 * 
	 * @param length
	 *        必须为正整数 随机字符串的长度
	 * @return 随机字符�?.
	 */
	public static synchronized String buildRandomString(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length只能是正整数!");
		}
		Random random = new Random();
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < length; i++) {
			ret.append(CHAR_RANDOMS[random.nextInt(CHAR_RANDOMS.length)]);
		}
		random = null;
		return ret.toString();
	}

	/**
	 * /** 生成随机字符�?. 随机字符串的内容包含[1-9 A-Z a-z]的字�?.
	 * 
	 * @param min
	 *        必须为正整数 随机字符串的�?小长�?
	 * @param max
	 *        必须为正整数 随机字符串的�?大长�?
	 * @return 随机字符�?.
	 */
	public static synchronized String buildRandomString(int min, int max) {
		if (min <= 0) {
			throw new IllegalArgumentException("Min 只能是正整数!");
		}
		else if (max <= 0) {
			throw new IllegalArgumentException("Max 只能是正整数!");
		}
		else if (min > max) {
			throw new IllegalArgumentException("Min 必须小于或等�? Max!");
		}
		Random random = new Random();
		int length = random.nextInt(max - min + 1) + min;
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < length; i++) {
			ret.append(CHAR_RANDOMS[random.nextInt(CHAR_RANDOMS.length)]);
		}
		random = null;
		return ret.toString();
	}
	
	/**
	 * 截取固定超出长度str 结尾...
	 * @Description 
	 * @param str �?要截图的string
	 * @param length 长度
	 * @return
	 * @version 1.0 2013-7-3
	 */
	public static String subStringLength(String str,int length){
		if (isNotEmpty(str)&&str.length()>length) {
			str = str.substring(0, length)+"...";
		}
		return str;
	}

	private static final char[] CHAR_RANDOMS = {
		'1',
		'2',
		'3',
		'4',
		'5',
		'6',
		'7',
		'8',
		'9',
		'A',
		'B',
		'C',
		'D',
		'E',
		'F',
		'G',
		'H',
		'I',
		'J',
		'K',
		'L',
		'M',
		'N',
		'O',
		'P',
		'Q',
		'R',
		'S',
		'T',
		'U',
		'V',
		'W',
		'X',
		'Y',
		'Z',
		'a',
		'b',
		'c',
		'd',
		'e',
		'f',
		'g',
		'h',
		'i',
		'j',
		'k',
		'l',
		'm',
		'n',
		'o',
		'p',
		'q',
		'r',
		's',
		't',
		'u',
		'v',
		'w',
		'x',
		'y',
		'z' };
}