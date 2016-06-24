package com.uugty.uu.common.connectmanager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	private static MessageListener mMessageListener;
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

	public SMSBroadcastReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object pdu : pdus) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
				String sender = smsMessage.getDisplayOriginatingAddress();
				// 短信内容
				String content = smsMessage.getDisplayMessageBody();
				long date = smsMessage.getTimestampMillis();
				Date tiemDate = new Date(date);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String time = simpleDateFormat.format(tiemDate);

				// 过滤不需要读取的短信的发送号码
				/*
				 * if ("10690529026467".equals(sender)) {
				 * mMessageListener.onReceived(patternCode(content));
				 * //abortBroadcast(); }
				 */
				if (content.contains("UU客")) {
					mMessageListener.onReceived(patternCode(content));
					// abortBroadcast();
				}
			}
		}

	}

	// 回调接口
	public interface MessageListener {
		public void onReceived(String message);
	}

	public void setOnReceivedMessageListener(MessageListener messageListener) {
		this.mMessageListener = messageListener;
	}

	/**
	 * 匹配短信中间的6个数字（验证码等）
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}