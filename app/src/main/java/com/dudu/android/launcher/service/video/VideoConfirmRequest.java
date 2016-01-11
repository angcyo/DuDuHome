package com.dudu.android.launcher.service.video;

import android.content.Context;

import com.dudu.android.launcher.utils.DeviceIDUtil;


import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/12/17.
 * Description :
 */
public class VideoConfirmRequest {
//    private String confirmStartVideoUrl = "http://dudu.gotunnel.org/confirmStartVideo";
    //debug
    private String confirmStartVideoUrl = "http://192.168.0.50:8080/confirmStartVideo";
    private VideoTransfer videoTransfer;
    private Context mContext;
    private Logger log;

    public VideoConfirmRequest(VideoTransfer videoTransfer, Context mContext) {
        this.videoTransfer = videoTransfer;
        this.mContext = mContext;
        log = LoggerFactory.getLogger("video.VideoManager");
    }

    public void confirmStartVideo(){
        new Thread(){
            @Override
            public void run() {
                try {
                    log.info("发送确认请求-------------确认地址：{}", confirmStartVideoUrl);
//                    JSONObject paramJsonObject = new JSONObject();
//                    paramJsonObject.put("obeId", DeviceIDUtil.getIMEI(mContext));
//                    String param = paramJsonObject.toString();
//                    log.info("请求参数：{}",param);
                    httpUrlConnection(confirmStartVideoUrl,DeviceIDUtil.getIMEI(mContext));
                } catch (Exception e) {
                    log.error("响应错误--confirmStartVideo", e);
                }
            }
        }.start();
    }

    private void proConfirmResInfo(String confirmResInfo){
        if (confirmResInfo == null)
            return;
        try {
            JSONObject confirmResJsonobject = new JSONObject(confirmResInfo);
            String resultCode = confirmResJsonobject.getString("resultCode");
            String method = confirmResJsonobject.getString("method");
            if (resultCode.equals("200")){
                log.info("确认上传---开启上传线程");
                videoTransfer.uploadVideo();
            }else {
                log.error("响应错误");
            }
        } catch (JSONException e) {
            log.error("异常：", e);
        }
    }


    private void httpUrlConnection(String urlPath, String obeId){
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();

            ////设置连接属性
            httpConn.setDoOutput(true);//使用 URL 连接进行输出
            httpConn.setDoInput(true);//使用 URL 连接进行输入
            httpConn.setUseCaches(false);//忽略缓存
            httpConn.setRequestMethod("POST");//设置URL请求方法

//            byte[] requestStringBytes = param.getBytes("UTF-8");
//            httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
//            log.info("requestStringBytes.length = {}", requestStringBytes.length);
//            httpConn.setRequestProperty("Content-Type", "application/octet-stream");
//            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//            httpConn.setRequestProperty("Charset", "UTF-8");
//            httpConn.se
//            httpConn.setReadTimeout();
            String obeIdToSend= URLEncoder.encode(obeId, "utf-8");
            httpConn.setRequestProperty("obeId", obeIdToSend);


            //建立输出流，并写入数据
//            OutputStream outputStream = httpConn.getOutputStream();
//            outputStream.write(requestStringBytes);
//            outputStream.flush();
//            outputStream.close();
            //获得响应状态
            int responseCode = httpConn.getResponseCode();

            if(HttpURLConnection.HTTP_OK == responseCode) {//连接成功
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                //处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                log.info("confirmStartVideo响应信息：{}", sb.toString());
                proConfirmResInfo(sb.toString());
            }else {
                log.error("响应错误------");
            }
        } catch (MalformedURLException e) {
            log.error("异常：", e);
        } catch (IOException e) {
            log.error("异常：", e);
        }catch (Exception e){
            log.error("异常：", e);
        }
    }


    public void setConfirmStartVideoUrl(String confirmStartVideoUrl) {
        this.confirmStartVideoUrl = confirmStartVideoUrl;
    }
}
