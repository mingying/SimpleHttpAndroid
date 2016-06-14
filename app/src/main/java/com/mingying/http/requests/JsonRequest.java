package com.mingying.http.requests;

import com.mingying.http.base.Request;
import com.mingying.http.base.Response;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/13.
 */
public class JsonRequest extends Request<JSONObject> {

    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener){
        super(method, url, listener);
    }

    @Override
    public JSONObject parseResponse(Response response) {

        return null;
    }
}
