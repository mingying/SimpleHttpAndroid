package com.mingying.http.core;

import com.mingying.http.base.Request;
import com.mingying.http.httpstacks.HttpStack;
import com.mingying.http.httpstacks.HttpStackFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列, 使用优先队列,使得请求可以按照优先级进行处理. [ Thread Safe ]
 *
 * Created by Administrator on 2016/6/14.
 */
public final class RequestQueue {

    /**
     *  请求队列
     */
    private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<Request<?>>();

    /**
     * 默认的核心数
     */
    public static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors()+1;

    /**
     * CPU核心数 + 1个分发线程数
     */
    private int mDispatcherNums = DEFAULT_CORE_NUMS;

    /**
     * 执行网络请求的线程组
     */
    private NetworkExecutor[] mDispatchers = null;

    /**
     * Http请求的真正执行者
     */
    private HttpStack mHttpStack;

    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);


    public RequestQueue(int coreNums, HttpStack httpStack){
        mDispatcherNums = coreNums;
        mHttpStack = httpStack != null ? httpStack: HttpStackFactory.createHttpStack();
    }

    /**
     * 启动NetworkExecutors
     */
    private void startNetworkExecutors(){
        mDispatchers = new NetworkExecutor[mDispatcherNums];
        for(int i = 0;i<mDispatcherNums;i++){
            mDispatchers[i] = new NetworkExecutor(mRequestQueue,mHttpStack);
            mDispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startNetworkExecutors();
    }

    /**
     * 停止NetworkExecutor
     */
    public void stop(){
        if (mDispatchers != null && mDispatchers.length>0){
            for(int i = 0;i<mDispatcherNums;i++){
                mDispatchers[i].quit();
            }
        }
    }

    public void addRequest(Request<?> request){
        if(!mRequestQueue.contains(request)){
            request.setSerialNumber(generateSerialNumber());
            mRequestQueue.add(request);
        }
    }

    /**
     * 为每个请求生成一个系列号
     *
     * @return 序列号
     */
    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }

    public void clear() {
        mRequestQueue.clear();
    }

    public BlockingQueue<Request<?>> getAllRequests() {
        return mRequestQueue;
    }

}
