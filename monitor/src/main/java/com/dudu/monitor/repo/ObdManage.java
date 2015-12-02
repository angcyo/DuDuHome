package com.dudu.monitor.repo;

import com.dudu.monitor.event.CarStatus;
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

    public static  ObdManage getInstance(){
        if (instance == null){
            synchronized (ObdManage.class){
                if (instance == null){
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

    public void onEventBackgroundThread(EventRead.L1ReadDone event){
        final byte[] obdData = event.getData();
        try {
            String obdDataString = new String(obdData, "UTF-8");
            log.debug("monitor- 收到obd数据: ",obdDataString);
            parseOBDData(obdDataString);
        } catch (Exception e) {
            log.error("monitor-OBD数据解析异常", e);
            e.printStackTrace();
        }
    }

    private void parseOBDData(String obdDataString){
        if(obdDataString.startsWith(REALTIME)){
            parseRealtimeData(obdDataString);

        }else if(obdDataString.startsWith(TOTALDATA)){
            parseTotalData(obdDataString);

        }else if(obdDataString.startsWith(FLAMOUT)){
            parseFlamoutData(obdDataString);

        }
    }

    private void parseRealtimeData(String obdDataString){
        ObdData obdData = new ObdData(obdDataString);
        curSpeed = obdData.getSpeed();
        curRpm = obdData.getEngineSpeed();

        obdDataList.add(obdData);

        if (!isNotice_start) {
            isNotice_flamout = false;
            isNotice_start = true;
            log.info("monitor- 发送CarStatus(CarStatus.CAR_ONLINE))事件");
            EventBus.getDefault().post(new CarStatus(CarStatus.CAR_ONLINE));
        }
    }

    private void parseTotalData(String obdDataString){

    }

    private void parseFlamoutData(String obdDataString){
        flamoutData = new FlamoutData(obdDataString);
        if (!isNotice_flamout) {
            isNotice_start = false;
            isNotice_flamout = true;
            log.info("monitor- 发送CarStatus(CarStatus.CAR_OFFLINE)事件");
            EventBus.getDefault().post(new CarStatus(CarStatus.CAR_OFFLINE));
        }
    }

    /* 释放资源*/
    public void release(){
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
}
