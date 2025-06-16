package com.yanmir.applacteossanjose;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class MultipartRequest extends Request<String> {
    private final Response.Listener<String> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, DataPart> mByteData;

    public MultipartRequest(int method, String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener, Map<String, String> headers,
                            Map<String, DataPart> byteData) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mHeaders = headers;
        this.mByteData = byteData;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    private final String boundary = "apiclient-" + System.currentTimeMillis();

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, DataPart> entry : mByteData.entrySet()) {
                buildPart(bos, entry.getKey(), entry.getValue());
            }
            bos.write(("--" + boundary + "--\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private void buildPart(ByteArrayOutputStream bos, String key, DataPart dataFile) throws IOException {
        bos.write(("--" + boundary + "\r\n").getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + dataFile.getFileName() + "\"\r\n").getBytes());
        bos.write(("Content-Type: " + dataFile.getType() + "\r\n").getBytes());
        bos.write("\r\n".getBytes());
        bos.write(dataFile.getContent());
        bos.write("\r\n".getBytes());
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data, String type) {
            fileName = name;
            content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}