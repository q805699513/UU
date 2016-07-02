package com.uugty.uu.util;

import java.security.MessageDigest;

public class Md5Util {
	static {
		System.loadLibrary("token");//之前在build.gradle里面设置的so名字，必须一致
	}
	public final static String MD5(String source) {
		String resultHash = null;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");

			byte[] result = new byte[md5.getDigestLength()];
			md5.reset();
			md5.update(source.getBytes("UTF-8"));
			result = md5.digest();

			StringBuffer buf = new StringBuffer(result.length * 2);

			for (int i = 0; i < result.length; i++) {
				int intVal = result[i] & 0xff;
				if (intVal < 0x10) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(intVal));
			}

			resultHash = buf.toString();

			return resultHash.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	/*
		native 方法返回token
	 */
	public static native String getToken(String source);

}
