package com.uugty.uu.main;

import android.annotation.SuppressLint;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uugty.uu.R;
import com.uugty.uu.base.BaseActivity;
import com.uugty.uu.common.myview.TopBackView;

public class RecomendWebActivity extends BaseActivity{

	 private WebView webview;  
	 private TopBackView topBack;
	 private String roadlineThemeTitle;
	 private String roadlineThemeUrl;
	 
	@Override
	protected int getContentLayout() {
		// TODO Auto-generated method stub
		return R.layout.recomend_web;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initGui() {
		// TODO Auto-generated method stub
		if(null!=getIntent()){
			roadlineThemeTitle = getIntent().getStringExtra("roadlineThemeTitle");
			roadlineThemeUrl= getIntent().getStringExtra("roadlineThemeUrl");
		}
		topBack = (TopBackView) findViewById(R.id.recomend_webview_title);
		topBack.setTitle(roadlineThemeTitle);
		webview = (WebView) findViewById(R.id.recomend_webview);
		webview.getSettings().setJavaScriptEnabled(true); 
	    webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	    webview.setWebViewClient(new HelloWebViewClient ());
	    webview.loadUrl(roadlineThemeUrl); 
        //设置Web视图  
	}

	@Override
	protected void initAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override

	protected void onDestroy() {
		webview.destroy();
		webview = null;
		super.onDestroy();

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
