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
import com.dudu.monitor.Monitor;
import com.dudu.network.NetworkManage;
import com.dudu.network.event.FlowSynConfiguration;
import com.dudu.network.event.FlowUpload;
import com.dudu.network.event.GetFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonitorService extends Service {

    private static final String TAG = "MonitorService";

    private static int WAKE_INTERVAL_MS = 30 * 1000;

    private DbHelper mDbHelper;

    private float mMobileRx = 0, mMobileTx = 0, mMobileTotalRx = 0, mMobileTotalTx = 0;

    private float mOldMobileRx = 0, mOldMobileTx = 0;

    private float mDeltaRx = 0, mDeltaTx = 0;

    private MonitorThread mMonitorThread;

    private boolean mMonitoring = true;

    private Context mContext;

    private Logger log;

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

        mContext = this;

        log = LoggerFactory.getLogger("monitor");

        WAKE_INTERVAL_MS = Integer.valueOf(SharedPreferencesUtil.getStringValue(mContext, Constants.KEY_FLOW_FREQUENCY, "30")) * 1000;

        mOldMobileRx = TrafficStats.getMobileRxBytes() / 1024;
        mOldMobileTx = TrafficStats.getMobileTxBytes() / 1024;

        mMonitorThread = new MonitorThread();
        mMonitorThread.setPriority(Thread.NORM_PRIORITY - 1);
        mMonitorThread.start();

//        NetworkManage.getInstance().sendMessage(new FlowSynConfiguration(this));
        if (Monitor.getInstance(mContext).isDeviceActived()){
            NetworkManage.getInstance().sendMessage(new GetFlow(this));
        }
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
                try {
                    if (Monitor.getInstance(mContext).isDeviceActived()){
                        NetworkManage.getInstance().sendMessage(new FlowSynConfiguration(mContext));
                    }

                    //单位kb
                    mMobileRx = TrafficStats.getMobileRxBytes() / 1024;//
                    mMobileTx = TrafficStats.getMobileTxBytes() / 1024;

                    if (mMobileRx == -1 && mMobileRx == -1) {
                        continue;
                    } else {
                        mDeltaRx = mMobileRx - mOldMobileRx;//时间段内接收消耗的流量
                        mOldMobileRx = mMobileRx;
                        mDeltaTx = mMobileTx - mOldMobileTx;//时间段内发送消耗的流量
                        mOldMobileTx = mMobileTx;

                        mDeltaRx = (Math.round(mDeltaRx * 100.0)) / 100;
                        mDeltaTx = (Math.round(mDeltaTx * 100.0)) / 100;

    //                    log.debug("时间段内接收消耗的流量：mDeltaRx = {}, mDeltaTx = {}", mDeltaRx, mDeltaTx);
                    }

                    mMobileTotalRx += mDeltaRx;
                    mMobileTotalTx += mDeltaTx;

                    log.info("mMobileTotalRx = {},mMobileTotalTx = {}", mMobileTotalRx, mMobileTotalTx);
                    if (mMobileTotalRx != 0 || mMobileTotalTx != 0) {
                        log.debug("更新数据库");
                        Date date = new Date();

                        if (mDbHelper.checkRecord(1, date)) {
                            float up = mDbHelper.getProFlowUp(1, date);
                            float dw = mDbHelper.getProFlowDw(1, date);
    //                        log.debug("读数据库：up = {}, dw = {}", up, dw);
                            mMobileTotalRx += dw;
                            mMobileTotalTx += up;
                            refreshFlowData();
                            float totalFlow = mDeltaRx + mDeltaTx;//开机到现在已使用总流量
    //                        log.debug("时间段内接收消耗的总流量：{}", totalFlow);
                            if ( Monitor.getInstance(mContext).isDeviceActived()){
                                NetworkManage.getInstance().sendMessage(new FlowUpload(mContext, totalFlow, mFormat.format(date)));
                            }
                            mDbHelper.updateFlow(mMobileTotalRx, mMobileTotalTx, 1, date);
                        } else {
                            mDbHelper.insertFlow(mMobileTotalTx, mMobileTotalRx, 1, date);
                        }

                        mMobileTotalRx = 0;
                        mMobileTotalTx = 0;
                    }
                } catch (Exception e) {
                    log.error("异常：{}", e);
                }

                try {
                    Thread.sleep(WAKE_INTERVAL_MS);
                } catch (Exception e) {
                    log.error("异常：{}", e);
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
     */
    private void refreshFlowData() {
        float primaryRemainingFlow = Float.parseFloat(SharedPreferencesUtil.getStringValue(MonitorService.this, Constants.KEY_REMAINING_FLOW, "1024000"));//无值的时候先给1024M
        log.debug("refreshFlowData剩余总流量：{}，mDeltaRx + mDeltaTx = {}", primaryRemainingFlow, (mDeltaRx + mDeltaTx));
//        float timelyRemainingFlow = primaryRemainingFlow - mMobileTotalRx - mMobileTotalTx;

        float timelyRemainingFlow = primaryRemainingFlow - mDeltaRx - mDeltaTx;//更新剩余流量应该是减去时间段内消耗的流量
//        log.debug("timelyRemainingFlow剩余总流量：{}", timelyRemainingFlow);
        SharedPreferencesUtil.putStringValue(MonitorService.this, Constants.KEY_REMAINING_FLOW, String.valueOf(timelyRemainingFlow));
    }

}
