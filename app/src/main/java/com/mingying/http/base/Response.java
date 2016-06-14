package com.mingying.http.base;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2016/6/13.
 */
public class Response extends BasicHttpResponse{

    public byte[] mRawData = new byte[0];

    public Response(StatusLine statusLine) {
        super(statusLine);
    }

    public Response(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    @Override
    public void setEntity(HttpEntity entity) {
        super.setEntity(entity);
        mRawData = entityToBytes(getEntity());
    }

    public byte[] getRawData() {
        return mRawData;
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return getStatusLine().getReasonPhrase();
    }

    /** Reads the contents of HttpEntity into a byte[]. */
    private byte[] entityToBytes(HttpEntity entity) {
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
