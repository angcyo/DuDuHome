package com.dudu.android.launcher.model.volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class MultipartRequest extends Request<String>{

	private ErrorListener errorListener = null;  
    private Listener<String> listener = null;  
    private MultipartRequestParams params = null;  
    private HttpEntity httpEntity = null;  
    private Logger log;
    public MultipartRequest(int method,MultipartRequestParams params, String url, Listener<String> listener,   
            ErrorListener errorListener) {  
        super(method, url, null);  
        this.params = params;
        this.errorListener = errorListener;   
        this.listener = listener;
        log = LoggerFactory.getLogger("net.http");
    }  
  
    @Override  
    public byte[] getBody() throws AuthFailureError {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(params != null) {
            httpEntity = params.getEntity();   
            try {  
                httpEntity.writeTo(baos);  
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("IOException writing to ByteArrayOutputStream {}", e);
            }  
        }  
        return baos.toByteArray();  
    }  
      
    @Override  
    protected Response<String> parseNetworkResponse(NetworkResponse response) {  
    	String parsed = "";  
        try {  
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));  
        } catch (UnsupportedEncodingException e) {  
            parsed = new String(response.data);  
        }  
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response)); 
    }  
  
    @Override  
    public Map<String, String> getHeaders() throws AuthFailureError {  
        Map<String, String> headers = super.getHeaders();
        if (null == headers || headers.equals(Collections.emptyMap())) {  
            headers = new HashMap<>();
        }  
        return headers;  
    }  
      
    @Override  
    public String getBodyContentType() {  
        return httpEntity.getContentType().getValue();
    }  
      
    @Override  
    protected void deliverResponse(String response) {  
    	if (listener != null) {
    		listener.onResponse(response);  
        }
        log.debug("http deliverResponse {}", response);
    }
      
    @Override  
    public void deliverError(VolleyError error) {  
        if(errorListener != null) {
            errorListener.onErrorResponse(error); 
           log.debug("http deliverError {}",error);
        }  
        
    }  

}
