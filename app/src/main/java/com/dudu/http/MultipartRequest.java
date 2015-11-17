package com.dudu.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;

import android.util.Log;

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
      
    public MultipartRequest(int method,MultipartRequestParams params, String url, Listener<String> listener,   
            ErrorListener errorListener) {  
        super(method, url, null);  
        // TODO Auto-generated constructor stub  
        this.params = params;  
        this.errorListener = errorListener;   
        this.listener = listener;  
    }  
  
    @Override  
    public byte[] getBody() throws AuthFailureError {  
        // TODO Auto-generated method stub  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        if(params != null) {  
            httpEntity = params.getEntity();   
            try {  
                httpEntity.writeTo(baos);  
//                String str = new String(baos.toByteArray());  
//                Log.d("lxh","bodyString is :" + str); 
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
                Log.e("lxh","IOException writing to ByteArrayOutputStream");  
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
        // TODO Auto-generated method stub  
        Map<String, String> headers = super.getHeaders();  
        if (null == headers || headers.equals(Collections.emptyMap())) {  
            headers = new HashMap<String, String>();  
        }  
        return headers;  
    }  
      
    @Override  
    public String getBodyContentType() {  
        // TODO Auto-generated method stub  
        return httpEntity.getContentType().getValue();  
    }  
      
    @Override  
    protected void deliverResponse(String response) {  
        // TODO Auto-generated method stub  
    	if (listener != null) {  
    		listener.onResponse(response);  
        } 
    	Log.d("lxh", "应答：" + response);
    }  
      
    @Override  
    public void deliverError(VolleyError error) {  
        // TODO Auto-generated method stub  
        if(errorListener != null) {  
            errorListener.onErrorResponse(error); 
            Log.d("lxh", "上传错误：" + error);
        }  
        
    }  

}
