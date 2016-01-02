package com.dudu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.utils.CarStatusUtils;
import com.dudu.android.launcher.utils.IPConfig;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.calculation.Calculation;
import com.dudu.conn.FlowManage;
import com.dudu.conn.PortalUpdate;
import com.dudu.conn.SendLogs;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.event.PowerOffEvent;
import com.dudu.network.NetworkManage;
import com.dudu.obd.ObdInit;
import com.dudu.storage.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;


/**
 * Created by dengjun on 2015/12/2.
 * Description :
 */
public class MainService extends Service {
    private NetworkManage networkManage;
    private Monitor monitor;
    private Logger log;

    private FlowManage flowManage;
    private SendLogs sendLogs;
    private PortalUpdate portalUpdate;

    private Calculation calculation;

    private Storage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        log = LoggerFactory.getLogger("service");

        log.info("启动主服务------------");

        initNetWork();

        ObdInit.initOBD(this);

        monitor = Monitor.getInstance(this);
        monitor.startWork();

        flowManage = FlowManage.getInstance(this);

        sendLogs = SendLogs.getInstance(this);

        portalUpdate = PortalUpdate.getInstance(this);

        calculation = Calculation.getInstance(this);

        storage = Storage.getInstance();
        storage.init();

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    private void initNetWork() {
        String ip;
        int port;
        if (IPConfig.getInstance(this).isTest_Server()|| Utils.isDemoVersion(this)) {
            ip = IPConfig.getInstance(this).getTestServerIP();
            port = IPConfig.getInstance(this).getTestServerPort();
        } else {
            ip = IPConfig.getInstance(this).getServerIP();
            port = IPConfig.getInstance(this).getServerPort();
        }
        networkManage = NetworkManage.getInstance();
        networkManage.init(ip, port);
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

        calculation.release();

        storage.release();

        EventBus.getDefault().unregister(this);
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


    public void onEvent(CarStatus event) {
        switch (event) {
            case ONLINE:
                EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.ON));
                CarStatusUtils.saveCarStatus(true);
                break;
            case OFFLINE:
                EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.OFF));
                CarStatusUtils.saveCarStatus(false);
                break;

        }
    }

    public void onEvent(PowerOffEvent event) {
        SystemPropertiesProxy.getInstance().set(this, "persist.sys.boot", "shutdown");
    }
}