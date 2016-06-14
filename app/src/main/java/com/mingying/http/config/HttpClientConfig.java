package com.mingying.http.config;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2016/6/14.
 */
public class HttpClientConfig extends HttpConfig {

    private static HttpClientConfig sConfig = new HttpClientConfig();

    //httpClient的SSLSocketFactory所在的包为org.apache.http.conn.ssl.SSLSocketFactory；
    // 而HttpURLConnection的SSLSocketFactory所在的包却是javax.net.ssl.SSLSocketFactory。
    SSLSocketFactory mSslSocketFactory;

    private HttpClientConfig() {

    }

    public static HttpClientConfig getConfig() {
        return sConfig;
    }

    /**
     * 配置https请求的SSLSocketFactory与HostnameVerifier
     *
     * @param sslSocketFactory
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory) {
        mSslSocketFactory = sslSocketFactory;
    }

    public SSLSocketFactory getSocketFactory() {
        return mSslSocketFactory;
    }
}
