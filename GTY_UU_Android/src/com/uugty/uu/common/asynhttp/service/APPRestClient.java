package com.uugty.uu.common.asynhttp.service;



import org.apache.http.protocol.HTTP;

import com.uugty.uu.common.asynhttp.AsyncHttpClient;
import com.uugty.uu.common.asynhttp.AsyncHttpResponseHandler;
import com.uugty.uu.common.asynhttp.RequestParams;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * 异步网络请求接口
 * 
 * @Description
 * @Class APPRestClient Copyright (c) 2014 Shanghai P&C Information Technology
 *        Co.,Ltd. All rights reserved.
 */
public class APPRestClient {
	private static final String LOG_TAG = "APPRestClient";
	// 推�?服务
	//线上测试环境
	public static String SERVER_IP = "http://www.uugty.com:100/";
	public static String BASE_URL ="http://www.uugty.com:7777/uuapplication/";
	public static String HTTPS_BASE_URL ="http://www.uugty.com:7777/uuapplication/security/";
	//123环境
//	public static String SERVER_IP = "http://www.uugty.com:100/";
//	public static String BASE_URL ="http://192.168.1.123:8090/uuapplication/";
//	public static String HTTPS_BASE_URL ="http://192.168.1.123:8090/uuapplication/security/";	 
	//https
	//上线环境
//	public static String HTTPS_BASE_URL = "https://www.uugty.com:8443/uuapplication/security/";
//	public static String SERVER_IP = "http://www.uugty.com:100/";
//	public static String BASE_URL = "http://www.uugty.com:8090/uuapplication/";
	
//	public static String HTTPS_BASE_URL ="https://192.168.1.123:8443/uuapplication/security/";
//	public static String HTTPS_BASE_URL ="http://www.uugty.com:8090/uuapplication/security/";

	private static AsyncHttpClient client = new AsyncHttpClient(true, 8090,
			8443);
	public static final String Encoding = HTTP.UTF_8;

	static {

		 //client.setTimeout(30000); // 设置链接超时，如果不设置，默认为10s
		client.setMaxRetriesAndTimeout(3, 12000);
	}

	/**
	 * 获取 AsyncHttpClient
	 * 
	 * @return
	 */
	public static AsyncHttpClient getAsyncHttpClient() {
		return client;
	}

	/**
	 * @describle: "key:value&key:value"
	 * @param relativeUrl
	 *            相对地址
	 * @param params
	 *            保存key:value的对�?
	 */
	public static void post(String relativeUrl, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(relativeUrl), params, responseHandler);
	}

	/**
	 * 发�?�请�?
	 * 
	 * @Description
	 * @param context
	 * @param relativeUrl
	 * @param params
	 * @param responseHandler
	 */
	public static void post(Context context, String relativeUrl,
			RequestParams params, AsyncHttpResponseHandler responseHandler) {

		client.post(context, getAbsoluteUrl(relativeUrl), params,
				responseHandler);

	}

	public static void post(Context context, String relativeUrl,
			RequestParams params, boolean isSSL,
			AsyncHttpResponseHandler responseHandler) {
		client.post(context, relativeUrl, params, responseHandler);

	}

	public static String post(String url, String filePath) {
		return client.post(getAbsoluteUrl(url), filePath);
	}

	// 下载图片
	public static Bitmap post(String url) {
		return client.post(url);
	}

	/**
	 * 调用此方�? 封装参数为StringEntity传�??
	 * 
	 * @Description
	 * @param context
	 * @param url
	 * @param params
	 *            参数String�?
	 * @param responseHandler
	 */
	public static void post(Context context, String url, String params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(context, getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 调用此方�? 封装参数为StringEntity传�??
	 * 
	 * @Description
	 * @param context
	 * @param url
	 * @param params
	 *            参数String�?
	 * @param responseHandler
	 * 
	 */
	public static void post(Context context, Application app, String url,
			String params, boolean isEnvrypt,
			AsyncHttpResponseHandler responseHandler) {
		
		String sendStr = params;
		if (isEnvrypt) {// 加密
			// sendStr = CryptoUtil.encryptData(app, params);
			Log.v(LOG_TAG, "sendStr:" + sendStr);
		}
		client.post(context, getAbsoluteUrl(url), sendStr, responseHandler);
	}

	/**
	 * 调用此方�? 封装参数为StringEntity传�??
	 * 
	 * @Description
	 * @param client
	 * @param context
	 * @param url
	 * @param params
	 *            参数String
	 * @param responseHandler
	 */
	public static void post(AsyncHttpClient client, Context context,
			String url, String params, AsyncHttpResponseHandler responseHandler) {
		client.post(context, url, params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	/**
	 * �?毁activity使用
	 * 
	 * @Description
	 * @param context
	 */
	public static void cancelRequests(Context context) {
		client.cancelRequests(context, true);
	}

}