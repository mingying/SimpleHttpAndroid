package com.mingying.http.cache;

import android.support.v4.util.LruCache;

import com.mingying.http.base.Response;

/**
 * Created by Administrator on 2016/6/14.
 */
public class LruMemCache implements Cache<String, Response>{

    private LruCache<String,Response> mResponseCache;

    public LruMemCache(){

        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMenory = (int)(Runtime.getRuntime().maxMemory()/1024);

        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = 1024*10;

        mResponseCache = new LruCache<String, Response>(cacheSize){

            @Override
            protected int sizeOf(String key, Response value) {
                // 重写此方法来衡量每个Response的大小，默认返回Response的数量1。
                return value.getRawData().length / 1024;
            }
        };

    }

    @Override
    public void put(String key, Response value) {
        mResponseCache.put(key, value);
    }

    @Override
    public Response get(String key) {
        return mResponseCache.get(key);
    }

    @Override
    public void remove(String key) {
        mResponseCache.remove(key);
    }
}
