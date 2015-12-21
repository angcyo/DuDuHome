package com.dudu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

        networkManage = NetworkManage.getInstance();
        networkManage.init();

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
        switch (event.getCarStatus()) {
            case CarStatus.CAR_ONLINE:
                EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.ON));
                break;
            case CarStatus.CAR_OFFLINE:
                EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.OFF));
                break;

        }
    }

    public void onEvent(PowerOffEvent event) {

    }
}