package com.mingying.http.cache;

/**
 * 请求缓存接口
 *
 * Created by Administrator on 2016/6/14.
 */
public interface Cache<K, V> {

    public void put(K key,V value);

    public V get(K key);

    public void remove(K key);
}
