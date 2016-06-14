package com.mingying.http.requests;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;

/**
 * Created by caihanlin on 16/6/14.
 */
public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method,String url,RequestListener<String> requestListener){
        super(method,url,requestListener);
    }

    @Override
    public String parseResponse(Response response) {

        return new String(response.getRawData());
    }
}
