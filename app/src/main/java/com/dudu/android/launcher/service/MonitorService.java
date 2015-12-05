package com.dudu.android.launcher.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;

import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.network.NetworkManage;
import com.dudu.network.event.FlowSynConfiguration;
import com.dudu.network.event.FlowUpload;


public class MonitorService extends Service {

    private static final String TAG = "MonitorService";

    private static final int WAKE_INTERVAL_MS = 5000;

    private DbHelper mDbHelper;

    private float mMobileRx = 0, mMobileTx = 0, mMobileTotalRx = 0, mMobileTotalTx = 0;

    private float mOldMobileRx = 0, mOldMobileTx = 0;

    private float mDeltaRx = 0, mDeltaTx = 0;

    private MonitorThread mMonitorThread;

    private boolean mMonitoring = true;

    private Context mContext;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDbHelper = DbHelper.getDbHelper();
        mContext=this;

        mOldMobileRx = TrafficStats.getMobileRxBytes() / 1024;
        mOldMobileTx = TrafficStats.getMobileTxBytes() / 1024;

        mMonitorThread = new MonitorThread();
        mMonitorThread.setPriority(Thread.NORM_PRIORITY - 1);
        mMonitorThread.start();

        NetworkManage.getInstance().sendMessage(new FlowSynConfiguration(this));

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private class MonitorThread extends Thread {

        @Override
        public void run() {
            while (mMonitoring) {
                if (Thread.interrupted()) {
                    return;
                }

                mMobileRx = TrafficStats.getMobileRxBytes() / 1024;
                mMobileTx = TrafficStats.getMobileTxBytes() / 1024;

                if (mMobileRx == -1 && mMobileRx == -1) {
                    continue;
                } else {
                    mDeltaRx = mMobileRx - mOldMobileRx;
                    mOldMobileRx = mMobileRx;
                    mDeltaTx = mMobileTx - mOldMobileTx;
                    mOldMobileTx = mMobileTx;

                    mDeltaRx = (Math.round(mDeltaRx * 100.0)) / 100;
                    mDeltaTx = (Math.round(mDeltaTx * 100.0)) / 100;
                }

                mMobileTotalRx += mDeltaRx;
                mMobileTotalTx += mDeltaTx;

                if (mMobileTotalRx != 0 || mMobileTotalTx != 0) {
                    LogUtils.v(TAG, "上传流量到数据库...");
                    Date date = new Date();

                    if (mDbHelper.checkRecord(1, date)) {
                        float up = mDbHelper.getProFlowUp(1, date);
                        float dw = mDbHelper.getProFlowDw(1, date);

                        mMobileTotalRx += dw;
                        mMobileTotalTx += up;
                        refreshFlowData();
                        float totalFlow = mDeltaRx + mDeltaTx;
                        NetworkManage.getInstance().sendMessage(new FlowUpload(mContext, totalFlow, mFormat.format(date)));
                        mDbHelper.updateFlow(mMobileTotalRx, mMobileTotalTx, 1, date);
                    } else {
                        mDbHelper.insertFlow(mMobileTotalTx, mMobileTotalRx, 1, date);
                    }

                    mMobileTotalRx = 0;
                    mMobileTotalTx = 0;
                }

                try {
                    Thread.sleep(WAKE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    LogUtils.e(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMonitorThread == null) {
            return;
        }

        mMonitoring = false;
        mMonitorThread.interrupt();
        try {
            mMonitorThread.join();
        } catch (InterruptedException e) {
            // 忽略
        }

        mMonitorThread = null;
    }
    /**
     * 更新SharedPreferences里面的剩余流量的数据
     * */
    private void refreshFlowData(){
        float primaryRemainingFlow=Float.parseFloat(SharedPreferencesUtil.getStringValue(MonitorService.this, Constants.KEY_REMAINING_FLOW,"0"));
        float timelyRemainingFlow=primaryRemainingFlow-mMobileTotalRx-mMobileTotalTx;
        SharedPreferencesUtil.putStringValue(MonitorService.this,Constants.KEY_REMAINING_FLOW,String.valueOf(timelyRemainingFlow));
    }

}
