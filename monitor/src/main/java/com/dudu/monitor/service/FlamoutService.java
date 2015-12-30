package com.dudu.monitor.service;

import android.content.Context;

import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.event.PowerOffEvent;
import com.dudu.monitor.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by lxh on 2015/12/18.
 */
public class FlamoutService {
    private static final int POWEROFF_TIME = 48;
    private ScheduledExecutorService lowVoltageScheduled = null;
    private Subscription powerOffSub = null;
    private Subscription lowVoltageSub = null;
    private Context mContext;
    private int lowCount = 0;
    private long lowTime;
    private boolean isFirstLow = false;
    private Logger log;

    public FlamoutService(Context context) {
        mContext = context;
        log = LoggerFactory.getLogger("obd.flamout");
    }

    public void init() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    public void onEventBackgroundThread(CarStatus event) {
        switch (event) {
            case ONLINE:

                if (powerOffSub != null) {
                    powerOffSub.unsubscribe();
                    powerOffSub = null;
                }
                if (lowVoltageSub != null) {
                    lowVoltageSub.unsubscribe();
                    lowVoltageSub = null;
                }
                if (lowVoltageScheduled != null) {
                    lowVoltageScheduled.shutdown();
                    lowVoltageScheduled = null;
                }
                lowCount = 0;
                break;
            case OFFLINE:
                log.debug("---------CAR_OFFLINE");
                lowVoltageScheduled = Executors.newScheduledThreadPool(1);
                lowVoltageScheduled.scheduleAtFixedRate(lowVoltageThread, 20, 20, TimeUnit.SECONDS);
                powerOffSub = Observable.timer(POWEROFF_TIME, TimeUnit.HOURS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        log.debug("power off");
                        EventBus.getDefault().post(new PowerOffEvent());
                    }
                });
                break;
        }

    }

    Thread lowVoltageThread = new Thread() {

        @Override
        public void run() {
            if (lowVoltageSub != null)
                return;
            lowVoltage();
        }
    };

    private void lowVoltage() {
        log.debug("lowVoltageThread [{}]", Monitor.getInstance(mContext).getCur_batteryV());
        if (Monitor.getInstance(mContext).getCur_batteryV() <= 11.5) {
            if (!isFirstLow) {
                isFirstLow = true;
                lowTime = System.currentTimeMillis();
            }
            lowCount++;
            if (lowCount >= 60 && (lowTime != 0 && getDifftime() >= 30)) {
                lowVoltageSub = Observable.timer(10, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (Monitor.getInstance(mContext).getCur_batteryV() <= 11.5) {
                            EventBus.getDefault().post(new PowerOffEvent());
                        } else {
                            lowVoltageSub = null;
                        }
                    }
                });
            }
        }
    }

    private long getDifftime() {
        return TimeUtils.dateDiff(TimeUtils.format(TimeUtils.format1),
                TimeUtils.dateLongFormatString(lowTime, TimeUtils.format1),
                TimeUtils.format1, "");
    }
}
