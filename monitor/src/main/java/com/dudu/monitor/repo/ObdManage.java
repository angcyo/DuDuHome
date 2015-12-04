package com.dudu.monitor.repo;

import com.dudu.monitor.event.CarDriveSpeedState;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.event.XfaOBDEvent;
import com.dudu.monitor.valueobject.FlamoutData;
import com.dudu.monitor.valueobject.ObdData;

import org.scf4a.EventRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class ObdManage {
    private static ObdManage instance = null;

    private static final String REALTIME = "$OBD-RT";                  // 实时数据标志
    private static final String TOTALDATA = "$OBD-TT";                 // 统计数据标志
    private static final String FLAMOUT = "$OBD-ST";                   // 熄火数据标志

    private Logger log;

    private FlamoutData flamoutData = null;
    private List<ObdData> obdDataList;

    private int curSpeed;//当前车速
    private float curRpm;//当前转速

    private boolean isNotice_start = false;
    private boolean isNotice_flamout = false;

    private int acc_spd, break_spd;

    private boolean isxfaOBd = false;

    public static ObdManage getInstance() {
        if (instance == null) {
            synchronized (ObdManage.class) {
                if (instance == null) {
                    instance = new ObdManage();
                }
            }
        }
        return instance;
    }

    public ObdManage() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        log = LoggerFactory.getLogger("monitor");

        obdDataList = new ArrayList<>();
    }

    public void onEventBackgroundThread(EventRead.L1ReadDone event) {
        final byte[] obdData = event.getData();
        try {
            String obdDataString = new String(obdData, "UTF-8");
            log.debug("monitor- 收到obd数据: ", obdDataString);
            parseOBDData(obdDataString);
        } catch (Exception e) {
            log.error("monitor-OBD数据解析异常", e);
            e.printStackTrace();
        }
    }


    public void onEventBackgroundThread(XfaOBDEvent xfaOBDEvent) {
        isxfaOBd = true;
        parseXfaOBDData(xfaOBDEvent.getObdData());

    }


    private void parseOBDData(String obdDataString) {
        if (obdDataString.startsWith(REALTIME)) {
            parseRealtimeData(obdDataString);

        } else if (obdDataString.startsWith(TOTALDATA)) {
            parseTotalData(obdDataString);

        } else if (obdDataString.startsWith(FLAMOUT)) {
            parseFlamoutData(obdDataString);

        }
    }

    private void parseRealtimeData(String obdDataString) {
        ObdData obdData;
        if (isxfaOBd)
            obdData = new ObdData(obdDataString);
        else
            obdData = new ObdData(obdDataString, 1);

        curSpeed = obdData.getSpeed();
        curRpm = obdData.getEngineSpeed();

        obdDataList.add(obdData);

        if (!isNotice_start) {
            isNotice_flamout = false;
            isNotice_start = true;
            log.info("monitor- 发送CarStatus(CarStatus.CAR_ONLINE))事件");
            EventBus.getDefault().post(new CarStatus(CarStatus.CAR_ONLINE));
        }

        if (obdData.misMatch()) {
            EventBus.getDefault().post(new CarDriveSpeedState(6));
        }
    }

    private void parseTotalData(String obdDataString) {
        String[] obdDataStringArray = obdDataString.split(",");
        int acc = Integer.parseInt(new String(obdDataStringArray[9]));
        if (acc > acc_spd) {
            EventBus.getDefault().post(new CarDriveSpeedState(1));
        }
        acc_spd = acc;

        int b_spd = Integer.parseInt(new String(obdDataStringArray[10].trim()));
        if (b_spd > break_spd) {
            EventBus.getDefault().post(new CarDriveSpeedState(2));
        }
        break_spd = b_spd;
    }

    private void parseFlamoutData(String obdDataString) {
        if (isxfaOBd)
            flamoutData = new FlamoutData(obdDataString, 1);
        else
            flamoutData = new FlamoutData(obdDataString);

        if (!isNotice_flamout) {
            isNotice_start = false;
            isNotice_flamout = true;
            log.info("monitor- 发送CarStatus(CarStatus.CAR_OFFLINE)事件");
            EventBus.getDefault().post(new CarStatus(CarStatus.CAR_OFFLINE));
        }
    }

    /* 释放资源*/
    public void release() {
        EventBus.getDefault().unregister(this);
        instance = null;
    }


    public FlamoutData getFlamoutData() {
        return flamoutData;
    }

    public void setFlamoutData(FlamoutData flamoutData) {
        this.flamoutData = flamoutData;
    }

    public List<ObdData> getObdDataList() {
        return obdDataList;
    }

    public int getCurSpeed() {
        return curSpeed;
    }

    public float getCurRpm() {
        return curRpm;
    }


    private void parseXfaOBDData(String obddata) {


        if (obddata.startsWith("BD$")) {
            parseRealtimeData(obddata);
            getAccAndBreak(obddata);
        } else if (obddata.contains("$OBD-DR$")) {
            parseFlamoutData(obddata);
        } else if (obddata.contains("CONNECTED")) {
            // 点火标志
            if (!isNotice_start) {
                isNotice_flamout = false;
                isNotice_start = true;
                EventBus.getDefault().post(new CarStatus(CarStatus.CAR_ONLINE));
            }
        }

    }

    private void getAccAndBreak(String result) {
        String[] obdStr = result.split(";");
        for (int i = 0; i < obdStr.length; i++) {
            String s = obdStr[i];
            if (s.startsWith("A")) {
                int acc = Integer.parseInt(s.substring(1, s.length()));
                if (acc > acc_spd)
                    EventBus.getDefault().post(new CarDriveSpeedState(1));
                acc_spd = acc;
            } else if (s.startsWith("B")) {
                int b_spd = Integer.parseInt(s.substring(1, s.length()));
                if (b_spd > break_spd)
                    EventBus.getDefault().post(new CarDriveSpeedState(2));
                break_spd = b_spd;
            }
        }
    }
}
