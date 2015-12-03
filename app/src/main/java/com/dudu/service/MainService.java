package com.dudu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dudu.conn.FlowManage;
import com.dudu.conn.PortalUpdate;
import com.dudu.conn.SendLogs;
import com.dudu.monitor.Monitor;
import com.dudu.network.NetworkManage;
import com.dudu.obd.BleOBD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dengjun on 2015/12/2.
 * Description :
 */
public class MainService extends Service{
    private NetworkManage networkManage;
    private Monitor monitor;
    private BleOBD bleOBD;
    private Logger log;
    private FlowManage flowManage;
    private SendLogs sendLogs;
    private PortalUpdate portalUpdate;

    @Override
    public void onCreate() {
        super.onCreate();
        log = LoggerFactory.getLogger("service");

        log.info("启动主服务------------");

        networkManage = NetworkManage.getInstance();
        networkManage.init();

        monitor = Monitor.getInstance(this);
        monitor.startWork();

        bleOBD = new BleOBD();
        bleOBD.initOBD(this);

        flowManage=FlowManage.getInstance(this);

        sendLogs=SendLogs.getInstance(this);

        portalUpdate=PortalUpdate.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.info("关闭主服务------------");
        monitor.stopWork();
        networkManage.release();
        flowManage.release();
        sendLogs.release();
        portalUpdate.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
