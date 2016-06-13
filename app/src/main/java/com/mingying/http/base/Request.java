package com.mingying.http.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/13.
 */
public abstract class Request<T> implements Comparable<Request<T>> {


    public interface RequestListener<T>{
        void onComplete(int code,T response, String error);
    }

    /**
     * 优先级枚举
     */
    public static enum Priority{
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    public static enum HttpMethod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String mHttpMethod;
        private HttpMethod(String method){
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    /**
     * 请求序列号
     */
    private int mSerialNum = 0;
    /**
     * 优先级默认设置为Normal
     */
    private Priority mPriority = Priority.NORMAL;
    /**
     * 是否取消该请求
     */
    private boolean isCancel = false;
    /**
     *  是否缓存
     */
    private boolean mShouldCache = true;
    /**
     * 请求Url
     */
    private String mUrl;
    /**
     * 请求的方法
     */
    HttpMethod mHttpMethod = HttpMethod.GET;
    /**
     * 请求的Header
     */
    private Map<String, String> mHeaders = new HashMap<String, String>();
    /**
     * 请求参数
     */
    private Map<String, String> mBodyParams = new HashMap<String, String>();
    /**
     * 请求Listener
     */
    protected RequestListener<T> mRequestListener;

    public Request(HttpMethod method, String url, RequestListener<T> listener){
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }

    /**
     * 从原生的网络请求中解析结果,子类覆写
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);

    /**
     * 处理Response,该方法运行在UI线程.
     *
     * @param response
     */
    public void deliveryResponse(Response response){
        T result = parseResponse(response);
        if (mRequestListener != null){
            // TODO: 2016/6/13 回调请求结果
            int code  = 0;
            String error = null;
            mRequestListener.onComplete(code,result,error);
        }
    }

    public String getUrl() {
        return mUrl;
    }



    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mBodyParams;
    }


    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * 返回POST或者PUT请求时的Body参数字节数组
     *
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * 将参数转换为Url编码的参数串
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    // 用于对请求的排序处理,根据优先级和加入到队列的序号进行排序
    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return myPriority.equals(another) ? this.getSerialNumber() - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }



}
