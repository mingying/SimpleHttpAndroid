package com.mingying.http.httpstacks;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;
import com.mingying.http.config.HttpClientConfig;

import org.apache.http.client.HttpClient;

/**
 * Created by Administrator on 2016/6/14.
 */
public class HttpClientStack implements HttpStack {

    /**
     * 使用HttpClient执行网络请求时的Https配置
     */
    HttpClientConfig mConfig = HttpClientConfig.getConfig();

    @Override
    public Response performRequest(Request<?> request) {
        return null;
    }
}
