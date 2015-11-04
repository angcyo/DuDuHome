package com.dudu.obd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.android.libble.BleConnectMain;

import org.scf4a.ConnSession;
import org.scf4a.Event;
import org.scf4a.EventRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class BleOBD {
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChara;
    private BluetoothDevice mBluetoothDevice;
    private PrefixReadL1 readL1;
    private Logger log;
    private static final String REALTIME = "$OBD-RT";                  // 实时数据标志
    private static final String TOTALDATA = "$OBD-TT";                 // 统计数据标志
    private static final String FLAMOUT = "$OBD-ST";                   // 熄火数据标志

    private boolean isNotice_start = false;
    private boolean isNotice_flamout = false;

    private List<OBDData> obdCollectionList = new ArrayList<>(); // OBD 数据

    private FlamoutData flamoutData ;

    private LinkedList<CarStatusManager.CarStatusListener> carStatusListeners;

    private int speed = 0;

    private float revolution = 0;

    private float battery = 0;

    private DriveBehaviorHappend.DriveBehaviorHappendListener driveBehaviorHappendListener;

    private  String[] rtData, stData,ttData;

    private int acc_spd, break_spd;

    public BleOBD() {
        readL1 = new PrefixReadL1();
        log = LoggerFactory.getLogger("odb.ble");
    }

    public void initOBD() {
        ConnSession.getInstance();
        BleConnectMain.getInstance().init(LauncherApplication.getContext());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().unregister(readL1);
        EventBus.getDefault().register(readL1);
        EventBus.getDefault().post(new Event.StartScanner());
        obdCollectionList = new ArrayList<OBDData>();
        carStatusListeners = CarStatusManager.getInstance().getmCarStateListenerList();
        driveBehaviorHappendListener = DriveBehaviorHappend.getInstance().getListener();
    }

    public void onEventMainThread(Event.BackScanResult event) {
        BluetoothDevice device = event.getDevice();
        log.debug("Try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.BLE, false));
    }


    public void onEventMainThread(Event.BLEInit event) {
        mBluetoothGatt = event.getBluetoothGatt();
        mWriteChara = event.getWriteChara();

        mBluetoothDevice = event.getDevice();
        final String devAddr = mBluetoothDevice.getAddress();
    }

    public void onEventBackgroundThread(EventRead.L1ReadDone event) {
        final byte[] data = event.getData();

        try {
            log.debug("Receive OBD Data: = {}", new String(data, "UTF-8"));
            parseOBDData(new String(data, "UTF-8"));
        } catch (Exception e) {
            log.error("OBD Parse exception", e);
            e.printStackTrace();
        }
    }

    private void parseOBDData(String result){

        if(result.startsWith(REALTIME)){

            parseRealtimeData(result);

        }else if(result.startsWith(TOTALDATA)){
            parseTotalData(result);

        }else if(result.startsWith(FLAMOUT)){

            parseFlamoutData(result);
        }

    }


    private void parseRealtimeData(String result){

        rtData = result.split(",");

        speed = Integer.parseInt(rtData[2]);
        revolution = Float.parseFloat(rtData[1]);
        battery = Float.parseFloat(rtData[0].split("=")[1]);

        OBDData obdData = new OBDData();
        obdData.setSpd(speed);
        obdData.setBatteryV(battery);
        obdData.setEngSpd(revolution);
        obdData.setEngLoad(Float.parseFloat(rtData[4]));
        obdData.setCuron(Float.parseFloat(rtData[5]));
        obdData.setEngCoolant(Float.parseFloat(rtData[3]));
        obdData.setTime(TimeUtils.dateLongFormatString(
                System.currentTimeMillis(), TimeUtils.format1));
        obdData.setRunState(1);

        obdCollectionList.add(obdData);

        misMatch();

        if (!isNotice_start) {
            isNotice_flamout = false;
            isNotice_start = true;
            if (!carStatusListeners.isEmpty()) {
                for (int i = 0; i < carStatusListeners.size(); i++) {
                    carStatusListeners.get(i).onCarStateChange(1);
                }
            }

        }



    }


    private void parseTotalData(String result){
        ttData = result.split(",");

        int acc = Integer.parseInt(new String(ttData[9]));
        if(acc > acc_spd){
            if(driveBehaviorHappendListener!=null)
                driveBehaviorHappendListener.onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_HARDACCL);
        }
        acc_spd = acc;
        int b_spd = Integer.parseInt(new String(ttData[10].trim()));

        if(b_spd > break_spd){
            if(driveBehaviorHappendListener!=null)
                driveBehaviorHappendListener.onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_HARDBRAK);
        }

    }

    private void parseFlamoutData(String result){

        stData = result.split(",");
        flamoutData = new FlamoutData();
        flamoutData.setFuels(Float.parseFloat(stData[6]));
        flamoutData.setMiles(Float.parseFloat(stData[3]));
        flamoutData.setTimes(Integer.parseInt(stData[2]) *60);
        flamoutData.setMaxrpm(Integer.parseInt(stData[8]));
        flamoutData.setMaxspd(Integer.parseInt(stData[7]));
        flamoutData.setCreateTime(TimeUtils.dateLongFormatString(
                System.currentTimeMillis(), TimeUtils.format1));
        flamoutData.setMethod("driveDatas");
        flamoutData.setObeId("");
        flamoutData.setPower(0);


        if (!isNotice_flamout) {
            isNotice_start = false;
            if (!carStatusListeners.isEmpty()) {
                isNotice_flamout = true;
                for (int i = 0; i < carStatusListeners.size(); i++) {
                    carStatusListeners.get(i).onCarStateChange(0);
                }
            }
        }
    }

    public List<OBDData> getObdCollectionList(){
        return obdCollectionList;
    }

    public FlamoutData getFlamoutData(){

        return flamoutData;
    }



    // 转速不匹配判定
    private void misMatch() {
        boolean first = speed < 30 && revolution > 3000;
        boolean second = (speed < 60 && speed > 30) && revolution > 3500;
        boolean third = (speed < 90 && speed > 60) && revolution > 4000;
        boolean forth = (speed < 110 && speed > 90) && revolution > 4500;
        boolean five = (speed < 130 && speed > 110) && revolution > 5000;
        boolean six = (speed < 150 && speed > 130) && revolution > 5500;
        if (first || second || third || forth || five || six) {
            if(driveBehaviorHappendListener!=null)
                driveBehaviorHappendListener.onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_MISMATCH);
        }
    }
}
