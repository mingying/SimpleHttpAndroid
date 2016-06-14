package com.mingying.http.core;

import android.util.Log;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;
import com.mingying.http.cache.Cache;
import com.mingying.http.cache.LruMemCache;
import com.mingying.http.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

/**
 *  网络请求Executor,继承自Thread,从网络请求队列中循环读取请求并且执行
 *
 *  Created by Administrator on 2016/6/14.
 */
public final class NetworkExecutor extends Thread{

    private BlockingQueue<Request<?>> mQueue;

    private HttpStack mHttpStack;

    private boolean isStop = false;

    /**
     * 请求缓存
     */
    private static Cache<String, Response> mReqCache = new LruMemCache();

    /**
     * 结果分发器,将结果投递到主线程
     */
    private ResponseDelivery mResponseDelivery = new ResponseDelivery();

    public NetworkExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        mQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        try {
            while (!isStop){
                //Retrieves and removes the head of this queue, waiting if necessary
                // until an element becomes available.
                Request<?> request = mQueue.take();
                Log.e("chl","is running" + request.getUrl());
                if (request.isCanceled()){
                    continue;
                }
                Response response = null;
                // 缓存
                if (isUseCache(request)){
                    response = mReqCache.get(request.getUrl());
                }else{
                    //从网络上取数据
                    response = mHttpStack.performRequest(request);
                    // 如果该请求需要缓存,那么请求成功则缓存到mResponseCache中
                    if (request.shouldCache() && response != null && response.getStatusCode() == 200){
                        mReqCache.put(request.getUrl(), response);
                    }
                }

                //分发结果
                mResponseDelivery.deliveryResponse(request,response);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        isStop = true;
        interrupt();
    }

    private boolean isUseCache(Request<?> request){
        if (request.shouldCache() && mReqCache.get(request.getUrl())!=null){
            return true;
        }
        return false;
    }
}
