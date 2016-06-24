package com.uugty.uu.common.asynhttp.service;

import java.io.Serializable;
/**
 * 返回结果处理
 * @Description 
 * @Author lewis(lgs@yitong.com.cn) 2014-1-17 下午1:45:10
 * @Class ServiceResult
 * Copyright (c) 2014 Shanghai P&C Information Technology Co.,Ltd. All rights reserved.
 */
public class ServiceResult<T> implements Serializable {
	    
	private static final long serialVersionUID = 4001359722198658981L;
	
	//结果标识
	public static final String Key_Code = "STATUS";
	
	//接口返回信息
	public static final String Key_Message = "MSG";
	
	//结果key
	public static final String Key_Result = "";
	
	
//	public static final String Key_Code = "status";
//	
//	public static final String Key_Message = "message";
//	
//	public static final String Key_Result = "result";
	
	
	
	//成功标识
	public static final int CODE_SUCCESS = 0;
	
	public int 			error_code = -1;			//错误�?		0 表示成功      其他表示失败
	public String		error_msg;					//错误信息
	public T			result;						//结果�?	
	
}
