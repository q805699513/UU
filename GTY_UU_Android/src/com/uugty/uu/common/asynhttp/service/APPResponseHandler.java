package com.uugty.uu.common.asynhttp.service;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.uugty.uu.appstart.AppStartActivity;
import com.uugty.uu.com.find.FindTestViewPagerActivity;
import com.uugty.uu.common.asynhttp.TextHttpResponseHandler;
import com.uugty.uu.common.myview.CustomToast;
import com.uugty.uu.common.util.AutoLogin;
import com.uugty.uu.common.util.StringTools;
import com.uugty.uu.login.LoginActivity;

/**
 * 处理网络请求回调，可根据指定的对象泛型，进行反射填充对象
 */
public abstract class APPResponseHandler<T> extends TextHttpResponseHandler {
	private static final String LOG_TAG = "ResponseHandler";
	private Context context;

	// 网络连接错误
	private static final int ERROR_CODE_NET = -999;
	// Json字符串转对象错误
	private static final int ERROR_CODE_FROM_JSON_TO_OBJECT = -888;
	// Json字符串解析错�?
	private static final int ERROR_CODE_JSON_PARSE = -777;

	private Class<T> classOfT; // 指定结果集泛�?

	boolean isResultNull = true; // 设置结果集是否为�?

	/**
	 * 创建�?个默认编码为utf-8对象,该对象的回调结果集为�?
	 */
	public APPResponseHandler() {
		super();
		isResultNull = true;
	}

	/**
	 * 创建�?个默认编码为utf-8对象,该对象的回调结果集为指定泛型
	 */
	public APPResponseHandler(Class<T> classOfT, Context context) {
		this(classOfT, DEFAULT_CHARSET);
		isResultNull = false;
		this.context = context;
	}

	/**
	 * 创建�?个默认编码为指定编码的对�?,该对象的回调结果集为指定泛型
	 */
	public APPResponseHandler(Class<T> classOfT, String encoding) {
		super(encoding);
		this.classOfT = classOfT;
	}

	/**
	 * Called when request succeeds
	 * 
	 * @param result
	 */
	public abstract void onSuccess(T result);

	/**
	 * Called when request fails
	 * 
	 * @param errorCode
	 * @param errorMsg
	 */
	public abstract void onFailure(int errorCode, String errorMsg);

	@Override
	public void onSuccess(int statusCode, Header[] headers,
			String responseString) {
		Gson gson = new Gson();

		ServiceResult<T> serviceResult = new ServiceResult<T>();
		try {
			JsonObject jsonObject = new JsonParser().parse(responseString)
					.getAsJsonObject();
			if (jsonObject.has(ServiceResult.Key_Code)) {
				JsonElement errorCodeJsonElement = jsonObject
						.get(ServiceResult.Key_Code);
				serviceResult.error_code = errorCodeJsonElement.getAsInt();
			}

			if (jsonObject.has(ServiceResult.Key_Message)) {
				JsonElement errorMsgJsonElement = jsonObject
						.get(ServiceResult.Key_Message);
				if (errorMsgJsonElement.isJsonNull()) {
					serviceResult.error_msg = "";
				} else {
					serviceResult.error_msg = errorMsgJsonElement.getAsString();
				}
			}

			if (isResultNull) {
				if (serviceResult.error_code == ServiceResult.CODE_SUCCESS) {
					onSuccess((T) null);
				} else {
					onFailure(serviceResult.error_code, serviceResult.error_msg);
				}
			} else {
				// 成功判断
				if (serviceResult.error_code == ServiceResult.CODE_SUCCESS) {
					// key常量不为�?
					if (StringTools.isNotEmpty(ServiceResult.Key_Result)) {
						// json结果中包含为key的结�?
						if (jsonObject.has(ServiceResult.Key_Result)) {
							JsonElement resultJsonElement = jsonObject
									.get(ServiceResult.Key_Result);
							try {
								T result = gson.fromJson(resultJsonElement,
										classOfT);
								serviceResult.result = result;
							} catch (JsonSyntaxException e) {
								onFailure(
										ERROR_CODE_FROM_JSON_TO_OBJECT,
										getErrorMsg(ERROR_CODE_FROM_JSON_TO_OBJECT));
								return;
							}
						}
					} else {
						try {
							T result = gson.fromJson(jsonObject, classOfT);
							serviceResult.result = result;
						} catch (JsonSyntaxException e) {
							onFailure(ERROR_CODE_FROM_JSON_TO_OBJECT,
									getErrorMsg(ERROR_CODE_FROM_JSON_TO_OBJECT));
							return;
						}
					}
					onSuccess(serviceResult.result);
				} else {
					if (serviceResult.error_code == 3
							&& !context.getClass().getName()
									.equals("AppStartActivity")) {
						// 重新登录;
						if (AutoLogin.getInstance(context).autoLoginAlbe()) {
							AutoLogin.getInstance(context).login();
							onFailure(serviceResult.error_code,
									serviceResult.error_msg);
						}
					} else {
						onFailure(serviceResult.error_code,
								serviceResult.error_msg);
					}

				}
			}
		} catch (JsonSyntaxException e) {
			onFailure(ERROR_CODE_JSON_PARSE, getErrorMsg(ERROR_CODE_JSON_PARSE));
		}
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			String responseString, Throwable throwable) {
		throwable.printStackTrace();
		onFailure(ERROR_CODE_NET, getErrorMsg(ERROR_CODE_NET));
	}

	private String getErrorMsg(int errorCode) {
		String errorMsg = "";
		switch (errorCode) {
		case ERROR_CODE_NET:
			errorMsg = "网络连接失败，请稍后重试";
			break;
		case ERROR_CODE_FROM_JSON_TO_OBJECT:
			errorMsg = "对象转换失败";
			break;
		case ERROR_CODE_JSON_PARSE:
			errorMsg = "对象解析失败";
			break;
		default:
			errorMsg = "未知错误";
			break;
		}
		return errorMsg;
	}
}
