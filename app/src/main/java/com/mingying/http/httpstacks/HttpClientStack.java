package com.mingying.http.httpstacks;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;
import com.mingying.http.config.HttpClientConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/14.
 */
public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig mConfig = HttpClientConfig.getConfig();

    DefaultHttpClient mHttpClient = new DefaultHttpClient();

    @Override
    public Response performRequest(Request<?> request) {
        try {
            mHttpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,mConfig.mUserAgent);
            HttpUriRequest httpRequest = createHttpRequest(request);
            // 添加连接参数
            setConnectionParams(httpRequest);
            // 添加header
            addHeaders(httpRequest, request.getHeaders());
            // https配置
            configHttps(request);
            // 执行请求
            HttpResponse response = mHttpClient.execute(httpRequest);
            // 构建Response
            Response rawResponse = new Response(response.getStatusLine());
            // 设置Entity
            rawResponse.setEntity(response.getEntity());
            return rawResponse;
        } catch (Exception e) {
        }

        return null;
    }

    /**
     * 设置连接参数,这里比较简单啊.一些优化设置就没有写了.
     *
     * @param httpUriRequest
     */
    private void setConnectionParams(HttpUriRequest httpUriRequest) {
        HttpParams httpParams = httpUriRequest.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, mConfig.mReadTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, mConfig.mConnectTimeOut);
    }

    /**
     * 如果是https请求,则使用用户配置的SSLSocketFactory进行配置.
     *
     * @param request
     */
    private void configHttps(Request<?> request) {
        SSLSocketFactory sslSocketFactory = mConfig.getSocketFactory();
        if (request.isHttps() && sslSocketFactory != null) {
            Scheme sch = new Scheme("https", sslSocketFactory, 443);
            mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
    }

    /**
     * 根据请求类型创建不同的Http请求
     *
     * @param request
     * @return
     */
    static HttpUriRequest createHttpRequest(Request<?> request) {
        HttpUriRequest httpUriRequest = null;
        switch (request.getHttpMethod()) {
            case GET:
                httpUriRequest = new HttpGet(request.getUrl());
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(request.getUrl());
                break;
            case POST: {
                httpUriRequest = new HttpPost(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPost) httpUriRequest, request);
            }
            break;
            case PUT: {
                httpUriRequest = new HttpPut(request.getUrl());
                httpUriRequest.addHeader(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
                setEntityIfNonEmptyBody((HttpPut) httpUriRequest, request);
            }
            break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }

        return httpUriRequest;
    }

    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    /**
     * 将请求参数设置到HttpEntity中
     *
     * @param httpRequest
     * @param request
     */
    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
                                                Request<?> request) {
        byte[] body = request.getBody();
        if (body != null) {
            HttpEntity entity = new ByteArrayEntity(body);
            httpRequest.setEntity(entity);
        }
    }
}
