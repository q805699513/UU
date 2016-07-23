/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.uugty.uu.common.asynhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.uugty.uu.base.application.MyApplication;
import com.uugty.uu.common.asynhttp.service.ServiceCode;
import com.uugty.uu.common.util.StringTools;
import com.uugty.uu.util.Md5Util;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.ContentBody;
import internal.org.apache.http.entity.mime.content.FileBody;

/**
 * The AsyncHttpClient can be used to make asynchronous GET, POST, PUT and
 * DELETE HTTP requests in your Android applications. Requests can be made with
 * additional parameters by passing a {@link RequestParams} instance, and
 * responses can be handled by passing an anonymously overridden
 * {@link ResponseHandlerInterface} instance.
 * <p>
 * &nbsp;
 * </p>
 * For example:
 * <p>
 * &nbsp;
 * </p>
 * 
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get(&quot;http://www.google.com&quot;, new ResponseHandlerInterface() {
 * 	&#064;Override
 * 	public void onSuccess(String response) {
 * 		System.out.println(response);
 * 	}
 * });
 * </pre>
 */
public class AsyncHttpClient {

	public static final String VERSION = "1.4.5";
	public static int DEFAULT_MAX_EXECUTE = 3;
	public static int DEFAULT_MAX_CONNECTIONS = 10;
	public static int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
	public static int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
	public static int DEFAULT_MAX_RETRIES = 5;
	public static int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1000;
	public static int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	public static String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	public static String ENCODING_GZIP = "gzip";
	public static String LOG_TAG = "AsyncHttpClient";

	private int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private int timeout = DEFAULT_SOCKET_TIMEOUT;
	private int conTimeout = DEFAULT_CONNECTION_TIMEOUT;

	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private ExecutorService threadPool;
	private final Map<Context, List<RequestHandle>> requestMap;
	private final Map<String, String> clientHeaderMap;
	private boolean isUrlEncodingEnabled = true;
	private static final String KEY_STORE_TYPE_BKS = "bks";

	private static final String KEY_STORE_TYPE_P12 = "PKCS12";

	private static final String SCHEME_HTTPS = "https";

	private static final int HTTPS_PORT = 8443;

	private static final String KEY_STORE_CLIENT_PATH = "client.p12";

	private static final String KEY_STORE_TRUST_PATH = "client.bks";

	private static final String KEY_STORE_PASSWORD = "ucf815";

	private static final String KEY_STORE_TRUST_PASSWORD = "ucf815";

	private static KeyStore keyStore;

	private static KeyStore trustStore;

	/**
	 * Creates a new AsyncHttpClient with default constructor arguments values
	 */
	public AsyncHttpClient() {
		this(false, 80, 443);
	}

	/**
	 * Creates a new AsyncHttpClient.
	 * 
	 * @param httpPort
	 *            non-standard HTTP-only port
	 */
	public AsyncHttpClient(int httpPort) {
		this(false, httpPort, 443);
	}

	/**
	 * Creates a new AsyncHttpClient.
	 * 
	 * @param httpPort
	 *            non-standard HTTP-only port
	 * @param httpsPort
	 *            non-standard HTTPS-only port
	 */
	public AsyncHttpClient(int httpPort, int httpsPort) {
		this(false, httpPort, httpsPort);
	}

	/**
	 * Creates new AsyncHttpClient using given params
	 * 
	 * @param fixNoHttpResponseException
	 *            Whether to fix or not issue, by ommiting SSL verification
	 * @param httpPort
	 *            HTTP port to be used, must be greater than 0
	 * @param httpsPort
	 *            HTTPS port to be used, must be greater than 0
	 */
	public AsyncHttpClient(boolean fixNoHttpResponseException, int httpPort,
			int httpsPort) {
		this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort,
				httpsPort));

		// this(getSVNSchemeRegistry(fixNoHttpResponseException, httpPort,
		// httpsPort), true);
	}

	/**
	 * Returns default instance of SchemeRegistry
	 * 
	 * @param fixNoHttpResponseException
	 *            Whether to fix or not issue, by ommiting SSL verification
	 * @param httpPort
	 *            HTTP port to be used, must be greater than 0
	 * @param httpsPort
	 *            HTTPS port to be used, must be greater than 0
	 */
	private static SchemeRegistry getDefaultSchemeRegistry(
			boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
		/*
		 * if (fixNoHttpResponseException) { Log.d(LOG_TAG,
		 * "Beware! Using the fix is insecure, as it doesn't verify SSL certificates."
		 * ); }
		 * 
		 * if (httpPort < 1) { httpPort = 80; Log.d(LOG_TAG,
		 * "Invalid HTTP port number specified, defaulting to 80"); }
		 * 
		 * if (httpsPort < 1) { httpsPort = 443; Log.d(LOG_TAG,
		 * "Invalid HTTPS port number specified, defaulting to 443"); }
		 * 
		 * // Fix to SSL flaw in API < ICS // See
		 * https://code.google.com/p/android/issues/detail?id=13117
		 * SSLSocketFactory sslSocketFactory; if (fixNoHttpResponseException)
		 * sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory(); else
		 * sslSocketFactory = SSLSocketFactory.getSocketFactory();
		 * 
		 * SchemeRegistry schemeRegistry = new SchemeRegistry();
		 * schemeRegistry.register(new Scheme("http", PlainSocketFactory
		 * .getSocketFactory(), httpPort)); schemeRegistry .register(new
		 * Scheme("https", sslSocketFactory, httpsPort));
		 */SchemeRegistry schemeRegistry = null;
		try {

			// 服务器端需要验证的客户端证书

			KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

			// 客户端信任的服务器端证书

			// trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

			// InputStream ksIn =
			// pContext.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);

			InputStream tsIn = MyApplication.getInstance().getResources()
					.getAssets().open(KEY_STORE_TRUST_PATH);
			SSLSocketFactory socketFactory = null;
			schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), httpPort));
			try {

				// keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());

				trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
				socketFactory = new SSLSocketFactory(trustStore);
				Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
				schemeRegistry.register(sch);
			} catch (Exception e) {
				try {
					trustStore.load(null, null);
				} catch (NoSuchAlgorithmException | CertificateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					socketFactory = new SSLSocketFactoryEx(trustStore);
				} catch (KeyManagementException | UnrecoverableKeyException
						| NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				socketFactory
						.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
				schemeRegistry.register(sch);

			} finally {

				try {
					tsIn.close();

				} catch (Exception ignore) {
					ignore.printStackTrace();
				}

			}

			// socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		} catch (KeyStoreException e) {

			e.printStackTrace();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return schemeRegistry;
	}

	/**
	 * Creates a new AsyncHttpClient.
	 * 
	 * @param schemeRegistry
	 *            SchemeRegistry to be used
	 */
	public AsyncHttpClient(SchemeRegistry schemeRegistry) {

		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, String.format(
				"android-async-http/%s (http://loopj.com/android-async-http)",
				VERSION));

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		// 鍒涘缓绾跨▼姹� 骞朵笖璁剧疆骞跺彂鏁伴噺
		threadPool = Executors.newFixedThreadPool(DEFAULT_MAX_EXECUTE);
		requestMap = new WeakHashMap<Context, List<RequestHandle>>();
		clientHeaderMap = new HashMap<String, String>();

		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(cm, httpParams);
		/*
		 * PersistentCookieStore myCookieStore = new
		 * PersistentCookieStore(paramContext);
		 * httpClient.setCookieStore(myCookieStore);
		 */
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(entity));
							break;
						}
					}
				}
			}
		});

		httpClient.setHttpRequestRetryHandler(new RetryHandler(
				DEFAULT_MAX_RETRIES, DEFAULT_RETRY_SLEEP_TIME_MILLIS));
	}

	/**
	 * Creates a new AsyncHttpClient.
	 * 
	 * @param schemeRegistry
	 * @param isSVNFlg
	 *            鏄惁涓篠VN httpClient
	 */
	public AsyncHttpClient(SchemeRegistry schemeRegistry, boolean isSVNFlg) {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, String.format(
				"android-async-http/%s (http://loopj.com/android-async-http)",
				VERSION));

		ClientConnectionManager cm = null;
		/*
		 * if (isSVNFlg) { //
		 * 璁剧疆浣跨敤绾跨▼�?夊叏鐨勮繛鎺ョ鐞嗘潵鍒涘缓HttpClient锛屽苟灏嗗垱寤虹殑HttpClient閲囩敤
		 * �?夊叏闅ч亾鐨勮繛鎺ユ柟寮� cm = new ThreadSafeClientConnManager(httpParams,
		 * schemeRegistry) { overide this for dns parse
		 * 
		 * @Override protected ClientConnectionOperator
		 * createConnectionOperator(SchemeRegistry schreg) { return new
		 * SvnClientConnectionOperator(schreg); } }; } else {
		 */
		cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		// 鍒涘缓绾跨▼姹� 骞朵笖璁剧疆骞跺彂鏁伴噺
		threadPool = Executors.newFixedThreadPool(DEFAULT_MAX_EXECUTE);
		requestMap = new WeakHashMap<Context, List<RequestHandle>>();
		clientHeaderMap = new HashMap<String, String>();

		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(cm, httpParams);
		httpClient.setRoutePlanner(new DefaultHttpRoutePlanner(httpClient
				.getConnectionManager().getSchemeRegistry()));
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(entity));
							break;
						}
					}
				}
			}
		});

		httpClient.setHttpRequestRetryHandler(new RetryHandler(
				DEFAULT_MAX_RETRIES, DEFAULT_RETRY_SLEEP_TIME_MILLIS));
	}

	/**
	 * http锛堝崟椤硅璇侊級鍒濆鍖�
	 * 
	 * @Description
	 * @Author lewis(lgs@yitong.com.cn) 2014-3-9 涓嬪�?:16:48
	 */
	public AsyncHttpClient(int connectTime, int socketTime, int maxExecute) {

		DEFAULT_CONNECTION_TIMEOUT = connectTime;
		DEFAULT_SOCKET_TIMEOUT = socketTime;
		DEFAULT_MAX_EXECUTE = maxExecute;

		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, conTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		// HttpProtocolParams.setUserAgent(httpParams,
		// String.format("android-async-http/%s (http://loopj.com/android-async-http)",
		// VERSION));
		HttpProtocolParams.setUserAgent(httpParams, "android");
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		// 鍒涘缓绾跨▼姹� 骞朵笖璁剧疆骞跺彂鏁伴噺
		threadPool = Executors.newFixedThreadPool(DEFAULT_MAX_EXECUTE);
		requestMap = new WeakHashMap<Context, List<RequestHandle>>();
		clientHeaderMap = new HashMap<String, String>();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(cm, httpParams);
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : clientHeaderMap.keySet()) {
					request.addHeader(header, clientHeaderMap.get(header));
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(entity));
							break;
						}
					}
				}
			}
		});
		httpClient.setHttpRequestRetryHandler(new RetryHandler(
				DEFAULT_MAX_RETRIES, DEFAULT_RETRY_SLEEP_TIME_MILLIS));
	}

	public static void allowRetryExceptionClass(Class<?> cls) {
		if (cls != null) {
			RetryHandler.addClassToWhitelist(cls);
		}
	}

	public static void blockRetryExceptionClass(Class<?> cls) {
		if (cls != null) {
			RetryHandler.addClassToBlacklist(cls);
		}
	}

	/**
	 * Get the underlying HttpClient instance. This is useful for setting
	 * additional fine-grained settings for requests by accessing the client's
	 * ConnectionManager, HttpParams and SchemeRegistry.
	 * 
	 * @return underlying HttpClient instance
	 */
	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	/**
	 * Get the underlying HttpContext instance. This is useful for getting and
	 * setting fine-grained settings for requests by accessing the context's
	 * attributes such as the CookieStore.
	 * 
	 * @return underlying HttpContext instance
	 */
	public HttpContext getHttpContext() {
		return this.httpContext;
	}

	/**
	 * Sets an optional CookieStore to use when making requests
	 * 
	 * @param cookieStore
	 *            The CookieStore implementation to use, usually an instance of
	 *            {@link PersistentCookieStore}
	 */
	public void setCookieStore(CookieStore cookieStore) {
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	/**
	 * Overrides the threadpool implementation used when queuing/pooling
	 * requests. By default, Executors.newFixedThreadPool() is used.
	 * 
	 * @param threadPool
	 *            an instance of {@link ThreadPoolExecutor} to use for
	 *            queuing/pooling requests.
	 */
	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * Simple interface method, to enable or disable redirects. If you set
	 * manually RedirectHandler on underlying HttpClient, effects of this method
	 * will be canceled.
	 * 
	 * @param enableRedirects
	 *            boolean
	 */
	public void setEnableRedirects(final boolean enableRedirects) {
		httpClient.setRedirectHandler(new DefaultRedirectHandler() {
			@Override
			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return enableRedirects;
			}
		});
	}

	/**
	 * Sets the User-Agent header to be sent with each request. By default,
	 * "Android Asynchronous Http Client/VERSION
	 * (http://loopj.com/android-async-http/)" is used.
	 * 
	 * @param userAgent
	 *            the string to use in the User-Agent header.
	 */
	public void setUserAgent(String userAgent) {
		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
	}

	/**
	 * Returns current limit of parallel connections
	 * 
	 * @return maximum limit of parallel connections, default is 10
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Sets maximum limit of parallel connections
	 * 
	 * @param maxConnections
	 *            maximum parallel connections, must be at least 1
	 */
	public void setMaxConnections(int maxConnections) {
		if (maxConnections < 1)
			maxConnections = DEFAULT_MAX_CONNECTIONS;
		this.maxConnections = maxConnections;
		final HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(this.maxConnections));
	}

	/**
	 * Returns current socket timeout limit (milliseconds), default is 10000
	 * (10sec)
	 * 
	 * @return Socket Timeout limit in milliseconds
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Set the connection and socket timeout. By default, 10 seconds.
	 * 
	 * @param timeout
	 *            the connect/socket timeout in milliseconds, at least 1 second
	 */
	public void setTimeout(int timeout) {
		if (timeout < 1000)
			timeout = DEFAULT_SOCKET_TIMEOUT;
		this.timeout = timeout;
		final HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, this.timeout);
		HttpConnectionParams.setSoTimeout(httpParams, this.timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, this.timeout);
	}

	/**
	 * Sets the Proxy by it's hostname and port
	 * 
	 * @param hostname
	 *            the hostname (IP or DNS name)
	 * @param port
	 *            the port number. -1 indicates the scheme default port.
	 */
	public void setProxy(String hostname, int port) {
		final HttpHost proxy = new HttpHost(hostname, port);
		final HttpParams httpParams = this.httpClient.getParams();
		httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	/**
	 * Sets the Proxy by it's hostname,port,username and password
	 * 
	 * @param hostname
	 *            the hostname (IP or DNS name)
	 * @param port
	 *            the port number. -1 indicates the scheme default port.
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public void setProxy(String hostname, int port, String username,
			String password) {
		httpClient.getCredentialsProvider().setCredentials(
				new AuthScope(hostname, port),
				new UsernamePasswordCredentials(username, password));
		final HttpHost proxy = new HttpHost(hostname, port);
		final HttpParams httpParams = this.httpClient.getParams();
		httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	/**
	 * Sets the SSLSocketFactory to user when making requests. By default, a
	 * new, default SSLSocketFactory is used.
	 * 
	 * @param sslSocketFactory
	 *            the socket factory to use for https requests.
	 */
	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.httpClient.getConnectionManager().getSchemeRegistry()
				.register(new Scheme("https", sslSocketFactory, 443));
	}

	/**
	 * Sets the maximum number of retries and timeout for a particular Request.
	 * 
	 * @param retries
	 *            maximum number of retries per request
	 * @param timeout
	 *            sleep between retries in milliseconds
	 */
	public void setMaxRetriesAndTimeout(int retries, int timeout) {
		this.httpClient.setHttpRequestRetryHandler(new RetryHandler(retries,
				timeout));
	}

	/**
	 * Sets headers that will be added to all requests this client makes (before
	 * sending).
	 * 
	 * @param header
	 *            the name of the header
	 * @param value
	 *            the contents of the header
	 */
	public void addHeader(String header, String value) {
		clientHeaderMap.put(header, value);
	}

	/**
	 * Remove header from all requests this client makes (before sending).
	 * 
	 * @param header
	 *            the name of the header
	 */
	public void removeHeader(String header) {
		clientHeaderMap.remove(header);
	}

	/**
	 * Sets basic authentication for the request. Uses AuthScope.ANY. This is
	 * the same as setBasicAuth('username','password',AuthScope.ANY)
	 * 
	 * @param username
	 *            Basic Auth username
	 * @param password
	 *            Basic Auth password
	 */
	public void setBasicAuth(String username, String password) {
		AuthScope scope = AuthScope.ANY;
		setBasicAuth(username, password, scope);
	}

	/**
	 * Sets basic authentication for the request. You should pass in your
	 * AuthScope for security. It should be like this
	 * setBasicAuth("username","password", new
	 * AuthScope("host",port,AuthScope.ANY_REALM))
	 * 
	 * @param username
	 *            Basic Auth username
	 * @param password
	 *            Basic Auth password
	 * @param scope
	 *            - an AuthScope object
	 */
	public void setBasicAuth(String username, String password, AuthScope scope) {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		this.httpClient.getCredentialsProvider().setCredentials(scope,
				credentials);
	}

	/**
	 * Removes set basic auth credentials
	 */
	public void clearBasicAuth() {
		this.httpClient.getCredentialsProvider().clear();
	}

	/**
	 * Cancels any pending (or potentially active) requests associated with the
	 * passed Context.
	 * <p>
	 * &nbsp;
	 * </p>
	 * <b>Note:</b> This will only affect requests which were created with a
	 * non-null android Context. This method is intended to be used in the
	 * onDestroy method of your android activities to destroy all requests which
	 * are no longer required.
	 * 
	 * @param context
	 *            the android Context instance associated to the request.
	 * @param mayInterruptIfRunning
	 *            specifies if active requests should be cancelled along with
	 *            pending requests.
	 */
	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		List<RequestHandle> requestList = requestMap.get(context);
		if (requestList != null) {
			int count = 0;
			for (RequestHandle requestHandle : requestList) {
				count++;
				requestHandle.cancel(mayInterruptIfRunning);
			}
			requestMap.remove(context);
		}
	}

	// [+] HTTP HEAD

	/**
	 * Perform a HTTP HEAD request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle head(String url,
			ResponseHandlerInterface responseHandler) {
		return head(null, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP HEAD request with parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional HEAD parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle head(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return head(null, url, params, responseHandler);
	}

	/**
	 * Perform a HTTP HEAD request without any parameters and track the Android
	 * Context which initiated the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle head(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		return head(context, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP HEAD request and track the Android Context which initiated
	 * the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional HEAD parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle head(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		return sendRequest(httpClient, httpContext, new HttpHead(
				getUrlWithQueryString(isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	/**
	 * Perform a HTTP HEAD request and track the Android Context which initiated
	 * the request with customized headers
	 * 
	 * @param context
	 *            Context to execute request against
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set headers only for this request
	 * @param params
	 *            additional HEAD parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle head(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpUriRequest request = new HttpHead(getUrlWithQueryString(
				isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(httpClient, httpContext, request, null,
				responseHandler, context);
	}

	// [-] HTTP HEAD
	// [+] HTTP GET

	/**
	 * Perform a HTTP GET request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle get(String url,
			ResponseHandlerInterface responseHandler) {
		return get(null, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP GET request with parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional GET parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle get(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return get(null, url, params, responseHandler);
	}

	/**
	 * Perform a HTTP GET request without any parameters and track the Android
	 * Context which initiated the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle get(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		return get(context, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP GET request and track the Android Context which initiated
	 * the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional GET parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle get(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return sendRequest(httpClient, httpContext, new HttpGet(
				getUrlWithQueryString(isUrlEncodingEnabled, url, params)),
				null, responseHandler, context);
	}

	/**
	 * Perform a HTTP GET request and track the Android Context which initiated
	 * the request with customized headers
	 * 
	 * @param context
	 *            Context to execute request against
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set headers only for this request
	 * @param params
	 *            additional GET parameters to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle get(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(
				isUrlEncodingEnabled, url, params));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(httpClient, httpContext, request, null,
				responseHandler, context);
	}

	// [-] HTTP GET
	// [+] HTTP POST

	/**
	 * Perform a HTTP POST request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(String url,
			ResponseHandlerInterface responseHandler) {
		return post(null, url, "", responseHandler);
	}

	/**
	 * Perform a HTTP POST request with parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional POST parameters or files to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return post(null, url, params, responseHandler);
	}

	/**
	 * Perform a HTTP POST request and track the Android Context which initiated
	 * the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional POST parameters or files to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(Context context, String url,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		List<BasicNameValuePair> paramsList = params.getParamsList();

		/*String useUrl = url.substring(url.lastIndexOf("/") + 1);
		if (!useUrl.contains("userLocationSearch.do")) {
			StringBuffer key = new StringBuffer(useUrl.substring(
					useUrl.length() - 6, useUrl.length() - 3));

			for (int i = 0; i < 5 && i < paramsList.size(); i++) {
				StringBuffer bnvp = new StringBuffer(paramsList.get(i)
						.getValue());
				if (bnvp.toString()
						.matches(
								"[\uD83C\uDC04-\uD83C\uDD9A]|([\uD83C\uDDE8-\uD83C\uDDFA][\uD83C\uDDE7-\uD83C\uDDFA])|[\uD83C\uDE01-\uD83D\uDEC0]")) {
					key.append("123");
				} else if (bnvp.length() >= 3) {
					key.append(bnvp.substring(bnvp.length() - 3, bnvp.length()));
				} else {
					key.append(bnvp);
				}

			}

			// params.add("token", JNIProcess.getToken(key.toString()));
			params.add("token", Md5Util.MD5(key + "uuk"));
		}*/
		
		//Map<String, String[]> parameterMap = request.getParameterMap();
		// for (Entry<String, String[]> entry : parameterMap.entrySet()) {
		// System.out.println(entry.getKey() + "---->>>>"
		// + entry.getValue()[0]);
		// }
		String useUrl = url.substring(url.lastIndexOf("/") + 1);
		StringBuffer builder = new StringBuffer(useUrl.substring(
				useUrl.length() - 6, useUrl.length() - 3));
		int i = 0;
		for (BasicNameValuePair key : paramsList) {
			i++;
				String value = key.getValue();
				if (value != null && !"".equals(value)) {
					Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
		             Matcher emojiMatcher = emoji.matcher(value);
					if(emojiMatcher.find()){
						builder.append("123");
					}else if (value.length() > 3) {
						builder.append(value.substring(value.length() - 3));
					} else {
						builder.append(value);
					}
				
			}
			if (i == 5) {
				break;
			}
		}
//		params.add("token", Md5Util.getToken(builder.toString()));
		params.add("token", Md5Util.MD5(builder.toString() + "uuk"));

		return post(context, url, paramsToEntity(params, responseHandler),
				null, responseHandler);
	}

	/**
	 * 灏佽鍙傛暟涓篠tring璋冪敤post璇锋�? *
	 * 
	 * @Description
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @return
	 * @Author lewis(lgs@yitong.com.cn) 2014-1-17 涓嬪�?:49:32
	 */
	public RequestHandle post(Context context, String url, String params,
			ResponseHandlerInterface responseHandler) {
		return post(context, url, paramsToEntity(params, responseHandler),
				null, responseHandler);
	}

	/**
	 * Perform a HTTP POST request and track the Android Context which initiated
	 * the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param entity
	 *            a raw {@link org.apache.http.HttpEntity} to send with the
	 *            request, for example, use this to send string/json/xml
	 *            payloads to a server by passing a
	 *            {@link org.apache.http.entity.StringEntity}.
	 * @param contentType
	 *            the content type of the payload you are sending, for example
	 *            application/json if sending a json payload.
	 * @param responseHandler
	 *            the response ha ndler instance that should handle the
	 *            response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {
		return sendRequest(httpClient, httpContext,
				addEntityToRequestBase(new HttpPost(url), entity), contentType,
				responseHandler, context);
	}

	public String post(String url, String filePath) {
		return sendRequest(httpClient, url, filePath);
	}

	public Bitmap post(String url) {
		return sendRequest(httpClient, url);
	}

	/**
	 * Perform a HTTP POST request and track the Android Context which initiated
	 * the request. Set headers only for this request
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set headers only for this request
	 * @param params
	 *            additional POST parameters to send with the request.
	 * @param contentType
	 *            the content type of the payload you are sending, for example
	 *            application/json if sending a json payload.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(Context context, String url, Header[] headers,
			RequestParams params, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = new HttpPost(url);
		if (params != null)
			request.setEntity(paramsToEntity(params, responseHandler));
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(httpClient, httpContext, request, contentType,
				responseHandler, context);
	}

	/**
	 * Perform a HTTP POST request and track the Android Context which initiated
	 * the request. Set headers only for this request
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set headers only for this request
	 * @param entity
	 *            a raw {@link HttpEntity} to send with the request, for
	 *            example, use this to send string/json/xml payloads to a server
	 *            by passing a {@link org.apache.http.entity.StringEntity}.
	 * @param contentType
	 *            the content type of the payload you are sending, for example
	 *            application/json if sending a json payload.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle post(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPost(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(httpClient, httpContext, request, contentType,
				responseHandler, context);
	}

	// [-] HTTP POST
	// [+] HTTP PUT

	/**
	 * Perform a HTTP PUT request, without any parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle put(String url,
			ResponseHandlerInterface responseHandler) {
		return put(null, url, null, responseHandler);
	}

	/**
	 * Perform a HTTP PUT request with parameters.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional PUT parameters or files to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle put(String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return put(null, url, params, responseHandler);
	}

	/**
	 * Perform a HTTP PUT request and track the Android Context which initiated
	 * the request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param params
	 *            additional PUT parameters or files to send with the request.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle put(Context context, String url, RequestParams params,
			ResponseHandlerInterface responseHandler) {
		return put(context, url, paramsToEntity(params, responseHandler), null,
				responseHandler);
	}

	/**
	 * Perform a HTTP PUT request and track the Android Context which initiated
	 * the request. And set one-time headers for the request
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param entity
	 *            a raw {@link HttpEntity} to send with the request, for
	 *            example, use this to send string/json/xml payloads to a server
	 *            by passing a {@link org.apache.http.entity.StringEntity}.
	 * @param contentType
	 *            the content type of the payload you are sending, for example
	 *            application/json if sending a json payload.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle put(Context context, String url, HttpEntity entity,
			String contentType, ResponseHandlerInterface responseHandler) {
		return sendRequest(httpClient, httpContext,
				addEntityToRequestBase(new HttpPut(url), entity), contentType,
				responseHandler, context);
	}

	/**
	 * Perform a HTTP PUT request and track the Android Context which initiated
	 * the request. And set one-time headers for the request
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set one-time headers for this request
	 * @param entity
	 *            a raw {@link HttpEntity} to send with the request, for
	 *            example, use this to send string/json/xml payloads to a server
	 *            by passing a {@link org.apache.http.entity.StringEntity}.
	 * @param contentType
	 *            the content type of the payload you are sending, for example
	 *            application/json if sending a json payload.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle put(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			ResponseHandlerInterface responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(
				new HttpPut(url), entity);
		if (headers != null)
			request.setHeaders(headers);
		return sendRequest(httpClient, httpContext, request, contentType,
				responseHandler, context);
	}

	// [-] HTTP PUT
	// [+] HTTP DELETE

	/**
	 * Perform a HTTP DELETE request.
	 * 
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle delete(String url,
			ResponseHandlerInterface responseHandler) {
		return delete(null, url, responseHandler);
	}

	/**
	 * Perform a HTTP DELETE request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle delete(Context context, String url,
			ResponseHandlerInterface responseHandler) {
		final HttpDelete delete = new HttpDelete(url);
		return sendRequest(httpClient, httpContext, delete, null,
				responseHandler, context);
	}

	/**
	 * Perform a HTTP DELETE request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set one-time headers for this request
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle delete(Context context, String url, Header[] headers,
			ResponseHandlerInterface responseHandler) {
		final HttpDelete delete = new HttpDelete(url);
		if (headers != null)
			delete.setHeaders(headers);
		return sendRequest(httpClient, httpContext, delete, null,
				responseHandler, context);
	}

	/**
	 * Perform a HTTP DELETE request.
	 * 
	 * @param context
	 *            the Android Context which initiated the request.
	 * @param url
	 *            the URL to send the request to.
	 * @param headers
	 *            set one-time headers for this request
	 * @param params
	 *            additional DELETE parameters or files to send along with
	 *            request
	 * @param responseHandler
	 *            the response handler instance that should handle the response.
	 * @return RequestHandle of future request process
	 */
	public RequestHandle delete(Context context, String url, Header[] headers,
			RequestParams params, ResponseHandlerInterface responseHandler) {
		HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(
				isUrlEncodingEnabled, url, params));
		if (headers != null)
			httpDelete.setHeaders(headers);
		return sendRequest(httpClient, httpContext, httpDelete, null,
				responseHandler, context);
	}

	// [-] HTTP DELETE

	/**
	 * Puts a new request in queue as a new thread in pool to be executed
	 * 
	 * @param client
	 *            HttpClient to be used for request, can differ in single
	 *            requests
	 * @param contentType
	 *            MIME body type, for POST and PUT requests, may be null
	 * @param context
	 *            Context of Android application, to hold the reference of
	 *            request
	 * @param httpContext
	 *            HttpContext in which the request will be executed
	 * @param responseHandler
	 *            ResponseHandler or its subclass to put the response into
	 * @param uriRequest
	 *            instance of HttpUriRequest, which means it must be of
	 *            HttpDelete, HttpPost, HttpGet, HttpPut, etc.
	 * @return RequestHandle of future request process
	 */
	protected RequestHandle sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, ResponseHandlerInterface responseHandler,
			Context context) {

		List<Cookie> cookies = client.getCookieStore().getCookies();

		if (contentType != null) {
			uriRequest.setHeader("Content-Type", contentType);
		}
		responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
		responseHandler.setRequestURI(uriRequest.getURI());
		AsyncHttpRequest request = new AsyncHttpRequest(client, httpContext,
				uriRequest, responseHandler);
		threadPool.submit(request);
		RequestHandle requestHandle = new RequestHandle(request);

		if (context != null) {
			// Add request to request map
			List<RequestHandle> requestList = requestMap.get(context);
			if (requestList == null) {
				requestList = new LinkedList<RequestHandle>();
				requestMap.put(context, requestList);
			}

			requestList.add(requestHandle);

			Iterator<RequestHandle> iterator = requestList.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().shouldBeGarbageCollected()) {
					iterator.remove();
				}
			}
		}
		cookies = client.getCookieStore().getCookies();

		return requestHandle;
	}

	public String sendRequest(DefaultHttpClient httpclient, String uriRequest,
			String filePath) {
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(uriRequest);
		File file = new File(filePath);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		ContentBody cbFile = new FileBody(file);
		mpEntity.addPart("userfile", cbFile); // <input type="file"
												// name="userfile" /> 对应的
		httppost.setEntity(mpEntity);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(response != null){
			HttpEntity resEntity = response.getEntity();
			String json = "";
			String path = "";
			if (resEntity != null) {
				try {
					json = EntityUtils.toString(resEntity, "utf-8");
					if (resEntity != null) {
						resEntity.consumeContent();
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JSONObject p = null;
				try {
					p = new JSONObject(json);
					// path = (String) p.get("path");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return json;
		}else{
			return "";
		}

	}

	// 下载文件
	public Bitmap sendRequest(DefaultHttpClient httpclient, String uriRequest) {
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		Bitmap img = null;
		HttpResponse response = null;
		try {
			HttpGet httppost = new HttpGet(uriRequest);
			response = httpclient.execute(httppost);
			int code = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Long l = response.getEntity().getContentLength();
				if (l < ServiceCode.PICTURE_LOAD_MIN) {
					return null;
				}
				InputStream is = response.getEntity().getContent();
				// 如果是返回得字符串，可以直接用 EntityUtils来处理
				// EntityUtils.toString(response.getEntity());
				img = BitmapFactory.decodeStream(is);
			} else {
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	/**
	 * Sets state of URL encoding feature, see bug #227, this method allows you
	 * to turn off and on this auto-magic feature on-demand.
	 * 
	 * @param enabled
	 *            desired state of feature
	 */
	public void setURLEncodingEnabled(boolean enabled) {
		this.isUrlEncodingEnabled = enabled;
	}

	/**
	 * Will encode url, if not disabled, and adds params on the end of it
	 * 
	 * @param url
	 *            String with URL, should be valid URL without params
	 * @param params
	 *            RequestParams to be appended on the end of URL
	 * @param shouldEncodeUrl
	 *            whether url should be encoded (replaces spaces with %20)
	 * @return encoded url if requested with params appended if any available
	 */
	public static String getUrlWithQueryString(boolean shouldEncodeUrl,
			String url, RequestParams params) {
		if (shouldEncodeUrl)
			url = url.replace(" ", "%20");

		if (params != null) {
			// Construct the query string and trim it, in case it
			// includes any excessive white spaces.
			String paramString = params.getParamString().trim();

			// Only add the query string if it isn't empty and it
			// isn't equal to '?'.
			if (!paramString.equals("") && !paramString.equals("?")) {
				url += url.contains("?") ? "&" : "?";
				url += paramString;
			}
		}

		return url;
	}

	/**
	 * Returns HttpEntity containing data from RequestParams included with
	 * request declaration. Allows also passing progress from upload via
	 * provided ResponseHandler
	 * 
	 * @param params
	 *            additional request params
	 * @param responseHandler
	 *            ResponseHandlerInterface or its subclass to be notified on
	 *            progress
	 */
	private HttpEntity paramsToEntity(RequestParams params,
			ResponseHandlerInterface responseHandler) {
		HttpEntity entity = null;

		try {
			if (params != null) {
				entity = params.getEntity(responseHandler);
			}
		} catch (Throwable t) {
			if (responseHandler != null)
				responseHandler.sendFailureMessage(0, null, null, t);
			else
				t.printStackTrace();
		}

		return entity;
	}

	/**
	 * 灏佽鑾峰彇StringEntity
	 * 
	 * @Description
	 * @param params
	 * @param responseHandler
	 * @return
	 * @Author lewis(lgs@yitong.com.cn) 2014-1-17 涓嬪�?:43:52
	 */
	private HttpEntity paramsToEntity(String params,
			ResponseHandlerInterface responseHandler) {
		HttpEntity entity = null;

		try {
			if (StringTools.isNotEmpty(params)) {
				entity = new StringEntity(params, HTTP.UTF_8);
			}
		} catch (Throwable t) {
			if (responseHandler != null)
				responseHandler.sendFailureMessage(0, null, null, t);
			else
				t.printStackTrace();
		}

		return entity;
	}

	public boolean isUrlEncodingEnabled() {
		return isUrlEncodingEnabled;
	}

	/**
	 * Applicable only to HttpRequest methods extending
	 * HttpEntityEnclosingRequestBase, which is for example not DELETE
	 * 
	 * @param entity
	 *            entity to be included within the request
	 * @param requestBase
	 *            HttpRequest instance, must not be null
	 */
	private HttpEntityEnclosingRequestBase addEntityToRequestBase(
			HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (entity != null) {
			requestBase.setEntity(entity);
		}

		return requestBase;
	}

	/**
	 * Enclosing entity to hold stream of gzip decoded data for accessing
	 * HttpEntity contents
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}

	static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {

				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
