package com.uugty.uu.login;

import android.annotation.SuppressLint;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;

@SuppressLint("SetJavaScriptEnabled")
public class AgreementWebActivity extends BaseActivity{

	 private WebView webview;  
	 private TopBackView topBack;
	 private String helpUrl = "http://1.lukai1990.sinaapp.com/help2/html/help.html?case=";
	 private String agreement;
	 
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.agreement_web;
	}

	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		topBack = (TopBackView) findViewById(R.id.argment_webview_title);
		topBack.setTitle("注册协议");
		if (null != getIntent()) {
			agreement = getIntent().getStringExtra("agreement");
		}
		
		webview = (WebView) findViewById(R.id.argment_webview);
		webview.getSettings().setJavaScriptEnabled(true); 
	    webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	    webview.setWebViewClient(new HelloWebViewClient ());  
	    
	    if (null != agreement && agreement.equals("regist")) {
	    	topBack.setTitle("注册协议");
	    	webview.loadUrl("http://1.lukai1990.sinaapp.com/dis/html/dis.html"); 
		}else if(null != agreement && agreement.equals("useUU")){
			topBack.setTitle("预定流程");
			webview.loadUrl(helpUrl+"1"); 
		}
		else if(null != agreement && agreement.equals("becomeGuide")){
			topBack.setTitle("如何成为导游");
			webview.loadUrl(helpUrl+"2"); 
		}else if (null != agreement && agreement.equals("priceEnact")) {
			topBack.setTitle("价格制定");
			webview.loadUrl(helpUrl+"3"); 
		}
		else if (null != agreement && agreement.equals("cancleTour")) {
			topBack.setTitle("取消行程");
			webview.loadUrl(helpUrl+"4"); 
		}else if (null != agreement && agreement.equals("tourAccident")) {
			topBack.setTitle("旅行意外");
			webview.loadUrl(helpUrl+"5"); 
		}
		else if (null != agreement && agreement.equals("guideDimss")) {
			topBack.setTitle("导游没出现");
			webview.loadUrl(helpUrl+"6"); 
		}else if (null != agreement && agreement.equals("applyForRefund")) {
			topBack.setTitle("申请退款");
			webview.loadUrl(helpUrl+"7"); 
		}
		else if (null != agreement && agreement.equals("release")) {
			topBack.setTitle("服务条款");
			webview.loadUrl("http://1.lukai1990.sinaapp.com/dis/html/dis.html");  
		}
		else if (null != agreement && agreement.equals("sendTalk")) {
			topBack.setTitle("帖子发布规范");
			webview.loadUrl("http://www.uugty.com/uuapplication/wxprojectbendi/html/forum_help.html");  
		}else if(null != agreement && agreement.equals("generalize")){
			topBack.setTitle("推广员帮助");
			webview.loadUrl("http://1.lukai1990.sinaapp.com/help2/html/help.html?case=8&v=1"); 
		}
        
       
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	 //Web视图  
    private class HelloWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
    }
}
