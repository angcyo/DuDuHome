package com.dudu.conn;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.http.MultipartRequest;
import com.dudu.http.MultipartRequestParams;

import java.io.File;

/**
 * Created by Administrator on 2015/11/16.
 */
public class SendLogs {
    private RequestQueue queue;
    private static final String DUDU_FOLDER = "/sdcard/dudu";
    private static final String CRASH_FOLDER = "/sdcard/dudu/crash";
    private static final String LOGBACK_FOLDER = "/sdcard/logback";
    private static final String TMP_FOLDER = "/sdcard/dudu/tmp";
    public static final String LOGS_NAME = "logs.zip";

    public void logsSend(final Context context, final String url) {
        queue = Volley.newRequestQueue(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dirfile = new File(DUDU_FOLDER, "crash");
                if (dirfile.exists()) {
                    FileUtils.copyFolder(CRASH_FOLDER, LOGBACK_FOLDER);
                }
                try {
                    File dirFile=new File(DUDU_FOLDER,"tmp");
                    if (!dirFile.exists()){
                        dirFile.mkdirs();
                    }
                    FileUtils.zipFolder(LOGBACK_FOLDER, TMP_FOLDER + "/" + LOGS_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                File file = new File(TMP_FOLDER, LOGS_NAME);
                MultipartRequestParams multiPartParams = new MultipartRequestParams();
                multiPartParams.put("upload_logs", file, LOGS_NAME);
                multiPartParams.put("obeId", DeviceIDUtil.getIMEI(context));
                MultipartRequest multipartRequest = new MultipartRequest
                        (Request.Method.POST, multiPartParams, url, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                queue.add(multipartRequest);
            }
        }).start();
    }
}
