package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordBindService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    class MediaPrepareTask extends AsyncTask<Void, Void, Void> {
//
//        boolean prepared = false;
//
//        @Override
//        protected void onPreExecute() {
//            prepared = prepareMediaRecorder();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            logger.debug("开启录像初始化线程: MediaPrepareTask");
//            try {
//                if (prepared) {
//                    try {
//                        mediaRecorder.start();
//
//                        EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.ON));
//
//                        isRecording = true;
//                    } catch (Exception e) {
//                        stopRecord();
//
//                        doStartPreview();
//                    }
//                } else {
//                    stopRecord();
//
//                    doStartPreview();
//                }
//            } catch (Exception e) {
//                logger.error("录像准备过程出错...");
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            logger.debug("录像开启流程完毕...");
//        }
//    }

}
