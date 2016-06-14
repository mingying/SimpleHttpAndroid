package com.mingying.http.httpstacks;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;

/**
 * 执行网络请求的接口
 *
 * Created by Administrator on 2016/6/14.
 */
public interface HttpStack {

    /**
     * 执行请求
     *
     * @param request
     * @return response
     */
   public Response performRequest(Request<?> request);
}
