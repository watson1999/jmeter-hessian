package com.watson.hessian.utils;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * HttpClientPool
 * <p>
 * Created by manji on 10/01/2019.
 */
public class HttpClientPool {

    // 自实例
    private static final HttpClientPool ME = new HttpClientPool();
    // 连接池
    public final PoolingHttpClientConnectionManager POOL = new PoolingHttpClientConnectionManager();

    public static HttpClientPool getInstance() {
        return ME;
    }

    /**
     * 初始化
     */
    public void init(int maxTotal, int maxRoute) {
        // 最大连接数
        POOL.setMaxTotal(maxTotal);
        // 每个路由基础的连接数据
        POOL.setDefaultMaxPerRoute(maxRoute);
    }

    /**
     * 关闭
     */
    public void close() {
        POOL.close();
    }
}