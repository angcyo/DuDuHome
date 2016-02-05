package com.dudu.android.launcher.robbery;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.model.volley.MultipartRequest;
import com.dudu.android.launcher.model.volley.MultipartRequestParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/2/5.
 */
public class RobberyCheckRequest {
    private final RequestQueue queue;
    private String uploadUrl = "http://127.0.0.1:8080/external/mirror/robbery/";

    private static RobberyCheckRequest mInstance = new RobberyCheckRequest();

    private Logger logger;

    public static RobberyCheckRequest getInstance() {
        return mInstance;
    }

    private RobberyCheckRequest() {
        logger = LoggerFactory.getLogger("RobberyCheckRequest");
        queue = Volley.newRequestQueue(LauncherApplication.getContext());
    }

    public void requeset(String account, int type, int value) {
        uploadUrl = uploadUrl + account + "/" + type + "/" + value;
        MultipartRequestParams multiPartParams = new MultipartRequestParams();
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, multiPartParams, uploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                logger.debug("请求完成");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logger.error("请求出错:" + error.getMessage());
            }
        });
        queue.add(multipartRequest);
    }
}
