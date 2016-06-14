package com.mingying.http.httpstacks;

import android.text.TextUtils;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;
import com.mingying.http.config.HttpUrlConnConfig;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2016/6/14.
 */
public class HttpUrlConnStack implements HttpStack {
    /**
     * 配置Https
     */
    HttpUrlConnConfig mConfig = HttpUrlConnConfig.getsHttpUrlConnConfig();

    @Override
    public Response performRequest(Request<?> request) {
        if (request == null || TextUtils.isEmpty(request.getUrl())){
            return null;
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = createUrlConnection(request.getUrl());
            // 构建HttpURLConnection
            urlConnection = createUrlConnection(request.getUrl());
            // 设置headers
            setRequestHeaders(urlConnection, request);
            // 设置Body参数
            setRequestParams(urlConnection, request);
            // https 配置
            configHttps(request);
            return fetchResponse(urlConnection);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }


        return null;
    }

    private HttpURLConnection createUrlConnection(String urlAddr) throws IOException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlAddr);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return urlConnection;
    }

    /**
     * 设置请求头信息
     * @param connection
     * @param request
     */
    private void setRequestHeaders(HttpURLConnection connection, Request<?> request) {
        Map<String, String> headers = request.getHeaders();
        Set<String> keyset = headers.keySet();
        for (String key:keyset){
            connection.addRequestProperty(key,headers.get(key));
        }
    }

    protected void setRequestParams(HttpURLConnection connection, Request<?> request) throws IOException {
        Request.HttpMethod method = request.getHttpMethod();
        connection.setRequestMethod(method.toString());
        byte[] body = request.getBody();

        if(body != null){
            connection.setDoOutput(true);
            connection.addRequestProperty(Request.HEADER_CONTENT_TYPE,request.getBodyContentType());
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(body);
            dataOutputStream.close();
        }
    }

    private void configHttps(Request<?> request) {
        if (request.isHttps()) {
            SSLSocketFactory sslFactory = mConfig.getSslSocketFactory();
            // 配置https
            if (sslFactory != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(mConfig.getHostnameVerifier());
            }

        }
    }

    private Response fetchResponse(HttpURLConnection connection) throws IOException {

        // Initialize HttpResponse with data from the HttpURLConnection.
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        // 状态行数据
        StatusLine responseStatus = new BasicStatusLine(protocolVersion,
                connection.getResponseCode(), connection.getResponseMessage());
        // 构建response
        Response response = new Response(responseStatus);
        // 设置response数据
        response.setEntity(entityFromURLConnwction(connection));
        addHeadersToResponse(response, connection);
        return response;
    }

    /**
     * 执行HTTP请求之后获取到其数据流,即返回请求结果的流
     *
     * @param connection
     * @return
     */
    private HttpEntity entityFromURLConnwction(HttpURLConnection connection) {
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = connection.getErrorStream();
        }

        // TODO : GZIP
        entity.setContent(inputStream);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());

        return entity;
    }

    private void addHeadersToResponse(BasicHttpResponse response, HttpURLConnection connection) {
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                Header h = new BasicHeader(header.getKey(), header.getValue().get(0));
                response.addHeader(h);
            }
        }
    }


}
