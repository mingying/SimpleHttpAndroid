package com.mingying.http.core;

import android.os.Handler;
import android.os.Looper;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;

import java.util.concurrent.Executor;

/**
 * 请求结果投递类,将请求结果投递给UI线程
 *
 * Created by Administrator on 2016/6/14.
 */
public class ResponseDelivery implements Executor {

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理请求结果,将其执行在UI线程
     *
     * @param request
     * @param response
     */
    public void deliveryResponse(final Request<?> request, final Response response) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };
        execute(runnable);
    }

    @Override
    public void execute(Runnable command) {
        mMainHandler.post(command);
    }
}
