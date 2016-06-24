package com.uugty.uu.common.asynhttp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import com.uugty.uu.common.asynhttp.service.APPRestClient;

import android.content.Context;

public class HttpClientSslHelper {

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

    public static HttpClient getSslHttpClient(Context pContext) {

        HttpClient httpsClient = (DefaultHttpClient) APPRestClient.getAsyncHttpClient().getHttpClient();

        try {

            // 服务器端需要验证的客户端证书

        	KeyStore trustStore = KeyStore.getInstance(KeyStore
        	          .getDefaultType());

             

            // 客户端信任的服务器端证书

           // trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

             

            //InputStream ksIn = pContext.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);

            InputStream tsIn = pContext.getResources().getAssets().open(KEY_STORE_TRUST_PATH);

            try {

                //keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());

                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                try {

                    //ksIn.close();

                } catch (Exception ignore) {

                }

                try {

                    tsIn.close();

                } catch (Exception ignore) {

                }

            }

            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);

            Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
            
            //socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            httpsClient.getConnectionManager().getSchemeRegistry().register(sch);

        } catch (KeyManagementException e) {

            e.printStackTrace();

        } catch (UnrecoverableKeyException e) {

            e.printStackTrace();

        } catch (KeyStoreException e) {

            e.printStackTrace();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return httpsClient;

    }

}
