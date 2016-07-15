//
// Created by HolyGoose on 16/6/29.
//
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <string.h>
#include "com_uugty_uu_util_Md5Util.h"
#include <android/log.h>
#include <malloc.h>
#include "md5c.h"
#define LOG_TAG "System.out.c"
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/**
 * 工具方法
 * 返回值 char* 这个代表char数组的首地址
 *  Jstring2CStr 把java中的jstring的类型转化成一个c语言中的char 字符串
 */
char* Jstring2CStr(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
//	strcat(jstr,"uuk");
	jclass clsstring = (*env)->FindClass(env, "java/lang/String");
	jstring strencode = (*env)->NewStringUTF(env, "utf-8");
	jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid,
			strencode);
	jsize alen = (*env)->GetArrayLength(env, barr);

	jbyte* ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}

	(*env)->DeleteLocalRef(env, strencode);
	(*env)->ReleaseByteArrayElements(env, barr, ba, 0);
	return rtn;
}

/*
 * Class:     com_uugty_uu_util_Md5Util
 * Method:    getToken
 * Signature: (Ljava/lang/String;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_uugty_uu_util_Md5Util_getToken(JNIEnv* env,
		jclass clazz, jstring jInfo) {


	char* jstr = Jstring2CStr(env, jInfo);
	char* cstr = "uuk";
	strcat(jstr,cstr);
	//LOGI("%s", final);
	MD5_CTX context = { 0 };
	MD5Init(&context);
	MD5Update(&context, jstr, strlen(jstr));
	unsigned char dest[16] = { 0 };
	MD5Final(dest, &context);

	int i;
	char destination[32]={0};
	for (i = 0; i < 16; i++) {
		sprintf(destination, "%s%02x", destination, dest[i]);
	}
//	LOGI("%s", destination);
	return (*env)->NewStringUTF(env, destination);
}




