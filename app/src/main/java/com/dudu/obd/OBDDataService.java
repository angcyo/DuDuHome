package com.dudu.obd;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import com.amap.api.location.AMapLocation;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.conn.ActiveDevice;
import com.dudu.conn.Connection;
import com.dudu.conn.ConnectionEvent;
import com.dudu.conn.SendMessage;
import com.dudu.map.AmapLocationHandler;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scf4a.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.greenrobot.event.EventBus;

/**
 * 采集OBD数据,GPS数据
 */
public class OBDDataService extends Service implements
        DriveBehaviorHappend.DriveBehaviorHappendListener {
    public final static int SENSOR_SLOW = 0; // 传感器频率为20HZ左右(或20HZ以下)
    public final static int SENSOR_NORMAL = 1; // 传感器频率为 40HZ左右
    public final static int SENSOR_FASTER = 2; // 传感器频率为60HZ左右
    public final static int SENSOR_FASTEST = 3; // 传感器频率为最快的频率

    private static final String DRIVE_DATAS = "driveDatas";
    private static final String OBD_DATA = "obdDatas";
    private static final String COORDINATES = "coordinates";

    private static String TAG = "OBDDataService";

    private List<JSONArray> positionAry_list; // 存放要发送的定位点的队列
    private List<JSONArray> postOBDDataArr; // 存放要发送的OBD数据队列
    private Integer mSyncObj = new Integer(0);
    private boolean isAlive = true;
    private long delayTime = 30 * 1000;// 每隔30s发送一次数据
    private Connection conn;
    private boolean isOpen = false;
    private Handler mhandler;
    private int carState;
    private String obe_id = "111";
    private String gpsStr, obdStr, fStr;

    private SensorManager mSensorManager;
    private MyAccSensorEventListener mMysensorEventListener;
    private MyGyrSensorEventListener mGyrSensorEventListener;
    private Sensor mAcceSensor; // 加速度传感器;
    private Sensor mGyroscopSensor; // 陀螺仪
    private List<MotionData> mAcceList;
    private List<MotionData> mGyrList;

    private MySensorRunnable myRunnable;
    private int speed = 0;
    private float revolution = 0;
    private Logger log;

    private BleOBD bleOBD;

    private AmapLocationHandler amapLocationHandler;

    private AMapLocation last_Location;// 前一个位置点

    private AMapLocation cur_Location; // 当前位置点

    private SendMessage sendMessage;

    private Gson gson;

    /**
     * 采集数据线程 30s 将所有数据风封装到JSONArray里
     */
    private Thread dataCollectionThread = new Thread(new Runnable() {

        @Override
        public void run() {
            while (isAlive) {
                putGpsDataToJSON();
                putOBDData();

                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });
    /**
     * 发送数据线程
     */
    private Thread dataSendThread = new Thread(new Runnable() {

        @Override
        public void run() {
            int gpsSize = 0;
            int obdSize = 0;
            synchronized (mSyncObj) {
                while (isAlive || gpsSize > 0 || obdSize > 0) {
                    if (positionAry_list != null)
                        gpsSize = positionAry_list.size();
                    if (gpsSize > 0)
                        sendGpsData(positionAry_list.get(0));
                    if (postOBDDataArr != null)
                        obdSize = postOBDDataArr.size();
                    if (obdSize > 0)
                        sendOBDData(postOBDDataArr.get(0));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

					/* 及时更新列表的大小 */
                    if (positionAry_list != null) {
                        gpsSize = positionAry_list.size();
                    } else {
                        gpsSize = 0;
                    }
                    if (postOBDDataArr != null) {
                        obdSize = postOBDDataArr.size();
                    } else {
                        obdSize = 0;
                    }
                    if (postOBDDataArr != null)
                        obdSize = postOBDDataArr.size();
                    else
                        obdSize = 0;
                    if (carState == 0)
                        sendFlameOutData();
                }

            }
        }
    });


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log = LoggerFactory.getLogger("odb.service");
        init();
    }


    private void init() {
        positionAry_list = new ArrayList<>();
        postOBDDataArr = new ArrayList<>();
        mhandler = new Handler();
        amapLocationHandler = new AmapLocationHandler();
        amapLocationHandler.init(this);

        obe_id = DeviceIDUtil.getAndroidID(this);
        sendMessage = SendMessage.getInstance(this);
        gson = new Gson();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        initSensor();
        initConn();

        log.debug("odbservice startCommand");
        bleOBD = new BleOBD();
        bleOBD.initOBD(getApplicationContext());
        DriveBehaviorHappend.getInstance().setListener(this);
        try {
            if (conn != null && !isOpen && !conn.isAlive()) {
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        conn.start();
                    }
                },10*1000);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        activeDevice();

        if (dataCollectionThread == null) {
            dataCollectionThread = new Thread();
        }
        if (!dataCollectionThread.isAlive()) {
            dataCollectionThread.start();
        }
        if (dataSendThread == null) {
            dataSendThread = new Thread();
        }
        if (!dataSendThread.isAlive()) {
            dataSendThread.start();
        }
    }

    private void initConn() {
        log.debug("initConn");
        conn = Connection.getInstance(this);
    }

    // 初始化传感器相关
    private void initSensor() {
        log.debug("initSensor");

        myRunnable = new MySensorRunnable();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMysensorEventListener = new MyAccSensorEventListener();
        mGyrSensorEventListener = new MyGyrSensorEventListener();
        mAcceSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscopSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE); // 陀螺仪
        mSensorManager.registerListener(mMysensorEventListener, mAcceSensor,
                SENSOR_NORMAL);
        mSensorManager.registerListener(mGyrSensorEventListener,
                mGyroscopSensor, SENSOR_NORMAL);
        mAcceList = Collections.synchronizedList(new ArrayList<MotionData>());
        mGyrList = Collections.synchronizedList(new ArrayList<MotionData>());
        mhandler.postDelayed(myRunnable, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isAlive = false;
        conn.closeConn();
        amapLocationHandler.stopLocation();
    }


    // 将gps数据转换为JSON 格式
    private void putGpsDataToJSON() {
        log.debug("putGpsDataToJSON");
        JSONArray positionAry = new JSONArray();
        try {
            if (!amapLocationHandler.getGpsDataListToSend().isEmpty()) {
                for (int i = 0; i < amapLocationHandler.getGpsDataListToSend().size(); i++) {
                    MyGPSData position = amapLocationHandler.getGpsDataListToSend().get(i);
                    if (position != null) {
                        positionAry.put(i,
                                new JSONObject(gson.toJson(position)));
                    }
                }
                positionAry_list.add(positionAry);
                amapLocationHandler.getGpsDataListToSend().clear();
            }
        } catch (JSONException e) {

            log.error("putGpsDataToJSON error", e);
        }
    }

    // 发送GPS数据
    private void sendGpsData(JSONArray gpsData) {
        log.debug("sendGpsData");
        sendMessage.sendGPSDatas(gpsData);
        positionAry_list.remove(0);
    }

    // 发送OBD数据
    private void sendOBDData(JSONArray obdData) {
        log.debug("sendOBDData");
        sendMessage.sendOBDDatas(obdData);
        postOBDDataArr.remove(0);
    }

    // 发送熄火数据
    private void sendFlameOutData() {

        if (bleOBD.getFlamoutData() != null) {
            log.debug("sendFlameOutData");
            last_Location = amapLocationHandler.getLast_Location();
            if (last_Location != null) {
                MyGPSData flameOutgps = new MyGPSData(last_Location.getLatitude(),
                        last_Location.getLongitude(), last_Location.getSpeed(),
                        last_Location.getAltitude(), last_Location.getBearing(),
                        TimeUtils.dateLongFormatString(last_Location.getTime(),
                                TimeUtils.format1), last_Location.getAccuracy(), 0);

                JSONArray positionAry = new JSONArray();
                try {
                    positionAry.put(new JSONObject(gson.toJson(flameOutgps)));

                    sendMessage.sendFlameOutData(bleOBD.getFlamoutData(), positionAry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }


    }


    /**
     * 驾驶行为事件发生时的通知
     *
     * @param type
     */
    @Override
    public void onDriveBehaviorHappend(int type) {
        switch (type) {
            case DriveBehaviorHappend.TYPE_HARDACCL:
                putEventGPS(DriveBehaviorHappend.TYPE_HARDACCL);
                break;
            case DriveBehaviorHappend.TYPE_HARDBRAK:
                putEventGPS(DriveBehaviorHappend.TYPE_HARDBRAK);
                break;
            case DriveBehaviorHappend.TYPE_HARDTURN:
                putEventGPS(DriveBehaviorHappend.TYPE_HARDTURN);
                break;
            case DriveBehaviorHappend.TYPE_SNAP:
                putEventGPS(DriveBehaviorHappend.TYPE_SNAP);
                break;
            case DriveBehaviorHappend.TYPE_FATIGUEDRIVING:
                putEventGPS(DriveBehaviorHappend.TYPE_FATIGUEDRIVING);
                break;
            case DriveBehaviorHappend.TYPE_MISMATCH:
                putEventGPS(DriveBehaviorHappend.TYPE_MISMATCH);
                break;
        }
    }

    /**
     * 事件点存放
     *
     * @param type
     */
    private void putEventGPS(int type) {
        log.debug("putEventGPS:{}", type);
        cur_Location = amapLocationHandler.getCur_Location();
        if (cur_Location != null) {
            MyGPSData event = new MyGPSData(cur_Location.getLatitude(),
                    cur_Location.getLongitude(), cur_Location.getSpeed(),
                    cur_Location.getAltitude(), cur_Location.getBearing(),
                    TimeUtils.dateLongFormatString(cur_Location.getTime(),
                            TimeUtils.format1), cur_Location.getAccuracy(),
                    type);
            amapLocationHandler.getGpsDataListToSend().add(event);
        }
    }

    // 将OBD数据存放在JSONArray中
    private void putOBDData() {
        log.debug("putOBDData");

        if (bleOBD != null && !bleOBD.getObdCollectionList().isEmpty()) {
            JSONArray jsArr = new JSONArray();
            for (int i = 0; i < bleOBD.getObdCollectionList().size(); i++) {
                OBDData obdData = bleOBD.getObdCollectionList().get(i);
                if (obdData != null) {
                    try {
                        jsArr.put(new JSONObject(gson.toJson(obdData)));
                    } catch (JSONException e) {

                        log.error("putOBDData error ", e);

                    }
                }
            }
            postOBDDataArr.add(jsArr);
            bleOBD.getObdCollectionList().clear();
        }

    }


    // 疲劳驾驶判定,如果驾驶4小时后还未熄火，则每隔15分钟再判定一次是否为疲劳驾驶
    private void noticeFating() {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (carState == 1)
                    mhandler.postDelayed(this, 15 * 60 * 1000);
                onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_FATIGUEDRIVING);
            }
        }, 4 * 60 * 60 * 1000);
    }

    public void onEventBackgroundThread(BleOBD.CarStatus event) {
        carState = event.getCarStatus();
        log.debug("onEvent CarStatus:{}", carState);
        if (carState == BleOBD.CarStatus.CAR_ONLINE){
            noticeFating();
        }else{

        }
    }

    public void onEventBackgroundThread(ConnectionEvent.SessionStateChange event) {
        log.debug("ConnectionEvent.SessionStateChange");
        if (event.getSessonState() == event.SESSION_OPEND)
            isOpen = true;
    }

    // 传感器监听
    class MyAccSensorEventListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            MotionData motionData = new MotionData();
            motionData.mX = event.values[0];
            motionData.mY = event.values[1];
            motionData.mZ = event.values[2];
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if (mAcceList == null)
                        mAcceList = Collections
                                .synchronizedList(new ArrayList<MotionData>());
                    mAcceList.add(motionData);
                    break;
            }
            motionData = null;
        }
    }

    class MyGyrSensorEventListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            MotionData motionData = new MotionData();
            motionData.mX = event.values[0];
            motionData.mY = event.values[1];
            motionData.mZ = event.values[2];
            switch (event.sensor.getType()) {
                case Sensor.TYPE_GYROSCOPE:
                    if (mGyrList == null)
                        mGyrList = Collections
                                .synchronizedList(new ArrayList<MotionData>());
                    mGyrList.add(motionData);
                    break;
            }
        }

    }

    // 急转弯
    private class MySensorRunnable implements Runnable {
        @Override
        public void run() {
            mhandler.postDelayed(this, 1000);
            if (!mAcceList.isEmpty()) {
                float x_sum = 0;
                for (int i = 0; i < mAcceList.size(); i++) {
                    x_sum += mAcceList.get(i).mX;
                }
                float avg_x = Math.abs(x_sum / (mAcceList.size())); // 加速度传感器的X轴平均值
                // 速度大于0 x轴方向的绝对值大雨0.5则认为发生了急转弯
                if (speed > 0 && avg_x >= 0.5) {
                    onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_HARDTURN);
                }
            }
            mAcceList.clear();
            if (!mGyrList.isEmpty()) {
                float gx_sum = 0;
                for (int i = 0; i < mGyrList.size(); i++) {
                    gx_sum += mGyrList.get(i).mX;
                }
                float avg_gx = Math.abs(gx_sum / (mGyrList.size()));
                // Log.d(TAG,"--------陀螺仪："+ avg_gx);
            }
            mGyrList.clear();
        }
    }

    private void activeDevice() {

        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ActiveDevice.getInstance(OBDDataService.this).getActiveFlag()== ActiveDevice.ACTIVE_OK){

                }else {
                     sendMessage.sendActiveDeviceData();
                }

            }
        }, 20 * 1000);

    }

}
