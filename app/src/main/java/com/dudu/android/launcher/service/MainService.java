package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.utils.CarStatusUtils;
import com.dudu.android.launcher.utils.IPConfig;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.calculation.Calculation;
import com.dudu.carChecking.CarCheckingProxy;
import com.dudu.conn.FlowManage;
import com.dudu.conn.PortalUpdate;
import com.dudu.conn.SendLogs;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.event.PowerOffEvent;
import com.dudu.network.NetworkManage;
import com.dudu.storage.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;


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
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;


    private ScheduledExecutorService carCheckingExecutor;


    @Override
    public void onCreate() {
        super.onCreate();
        log = LoggerFactory.getLogger("service");

        log.info("启动主服务------------");

        initNetWork();

//        ObdInit.initOBD(this);

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
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        startCarChecking();
    }

    private void initNetWork() {
        String ip;
        int port;
        if (IPConfig.getInstance(this).isTest_Server() || Utils.isDemoVersion(this)) {
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

        releaseWakeLock();

        EventBus.getDefault().unregister(this);

        stopCarChecking();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireLock();
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
                CarStatusUtils.saveCarStatus(true);
                log.debug("接收到点火通知-亮屏");
                WifiApAdmin.startWifiAp(this);
                if (mWakeLock == null) {
                    acquireLock();
                }
                break;
            case OFFLINE:
                log.debug("接收到熄火通知-灭屏");
                releaseWakeLock();
                Observable
                        .timer(15, TimeUnit.SECONDS)
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                WifiApAdmin.closeWifiAp(MainService.this);
                                log.debug("关闭热点");
                            }
                        });
                break;

        }
    }

    private void acquireLock() {
        log.debug("启动wakelock");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "screenswakelock");
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        log.debug("释放wakelock");
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
        CarStatusUtils.saveCarStatus(false);
    }

    public void onEvent(PowerOffEvent event) {
        SystemPropertiesProxy.getInstance().set(this, "persist.sys.boot", "shutdown");
    }

    public void onEventMainThread(DeviceEvent.Screen event) {
        log.debug("DeviceEvent.Screen {}", event.getState());
        if (event.getState() == DeviceEvent.ON) {
            releaseWakeLock();
            acquireLock();
        }
    }

    private void startCarChecking() {
        carCheckingExecutor = Executors.newScheduledThreadPool(1);
        carCheckingExecutor.scheduleAtFixedRate(carCheckingThread, 5, 5, TimeUnit.SECONDS);

    }

    private void stopCarChecking() {
        if (carCheckingExecutor != null) {
            carCheckingExecutor.shutdown();
            carCheckingExecutor = null;
        }
    }

    Thread carCheckingThread = new Thread() {

        @Override
        public void run() {
            super.run();
            CarCheckingProxy.getInstance().startCarChecking();
        }
    };

}