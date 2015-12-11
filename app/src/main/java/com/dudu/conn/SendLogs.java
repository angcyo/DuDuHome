package com.dudu.conn;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.http.MultipartRequest;
import com.dudu.http.MultipartRequestParams;
import com.dudu.network.event.LogSend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/11/16.
 */
public class SendLogs {
    private RequestQueue queue;
    private static SendLogs instance = null;
    private static final String DUDU_FOLDER = "/sdcard/dudu";
    private static final String CRASH_FOLDER = "/sdcard/dudu/crash";
    private static final String LOGBACK_FOLDER = "/sdcard/logback";
    private static final String TMP_FOLDER = "/sdcard/dudu/tmp";
    public static final String LOGS_NAME = "logs.zip";
    private Context mContext;
    private Logger log;

    public static SendLogs getInstance(Context context) {
        if (instance == null) {
            synchronized (SendLogs.class) {
                if (instance == null) {
                    instance = new SendLogs(context);
                }
            }
        }
        return instance;
    }

    public SendLogs(Context context) {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        log = LoggerFactory.getLogger("network");
        mContext = context;
    }
    /**
     * Logs上传处理处理
     *
     */
    public void onEventBackgroundThread(LogSend logSend) {
        final String url = logSend.getUrl();
        log.info("收到日志上传事件 上传地址：{}", url);
        queue = Volley.newRequestQueue(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("日志上传-----------");
                    File dirfile = new File(DUDU_FOLDER, "crash");
                    if (dirfile.exists()) {
                        FileUtils.copyFolder(CRASH_FOLDER, LOGBACK_FOLDER);
                    }
                    try {
                        File dirFile = new File(DUDU_FOLDER, "tmp");
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        FileUtils.zipFolder(LOGBACK_FOLDER, TMP_FOLDER + "/" + LOGS_NAME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    File file = new File(TMP_FOLDER, LOGS_NAME);
                    MultipartRequestParams multiPartParams = new MultipartRequestParams();
                    multiPartParams.put("upload_logs", file, LOGS_NAME);
                    multiPartParams.put("obeId", DeviceIDUtil.getIMEI(mContext));
                    MultipartRequest multipartRequest = new MultipartRequest
                            (Request.Method.POST, multiPartParams, url, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    log.info("日志上传响应信息：{}", response);
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    log.error("日志上传错误响应：{}", error.toString());
                                }
                            });
                    queue.add(multipartRequest);
                } catch (Exception e) {
                    log.error("异常 {}", e);
                }
            }
        }).start();
    }
    public void release(){
        EventBus.getDefault().unregister(this);
        instance = null;
    }
}
