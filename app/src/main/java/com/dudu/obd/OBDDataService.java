package com.dudu.obd;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.LocationFilter;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.obd.Connection.OnRecieveCallBack;
import com.dudu.obd.Connection.StartNaviCallBack;
import com.dudu.obd.Connection.onSessionStateChangeCallBack;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 采集OBD数据,GPS数据
 */
public class OBDDataService extends Service implements AMapLocationListener,
        LocationSource, onSessionStateChangeCallBack, OnRecieveCallBack,
        DriveBehaviorHappend.DriveBehaviorHappendListener, StartNaviCallBack ,CarStatusManager.CarStatusListener{
    public final static int SENSOR_SLOW = 0; // 传感器频率为20HZ左右(或20HZ以下)
    public final static int SENSOR_NORMAL = 1; // 传感器频率为 40HZ左右
    public final static int SENSOR_FASTER = 2; // 传感器频率为60HZ左右
    public final static int SENSOR_FASTEST = 3; // 传感器频率为最快的频率
    private static final String DRIVE_DATAS = "driveDatas";
    private static final String OBD_DATA = "obdDatas";
    private static final String COORDINATES = "coordinates";
    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态
    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态
    private static String TAG = "OBDDataService";
    private static OBDDataService mOBDDataService;

    String flamoutStr = "";
    NaviLatLng mEndPoint;
    private LocationManagerProxy mLocationManagerProxy;
    private int GPSdataTime = 0;// 第几个GPS点
    private AMapLocation last_Location;// 前一个位置点
    private AMapLocation cur_Location; // 当前位置点
    private boolean isAvalable = false; // 标志定位点是否有效
    private List<AMapLocation> unAvalableList; // 存放通过第一阶段但没通过第二阶段过滤的点
    private List<MyGPSData> gpsDataListToSend; // 通过过滤后的定位点的集合
    private List<JSONArray> positionAry_list; // 存放要发送的定位点的队列
    private List<JSONArray> postOBDDataArr; // 存放要发送的OBD数据队列
    private Integer mSyncObj = new Integer(0);
    private boolean isAlive = true;
    private long delayTime = 30 * 1000;// 每隔30s发送一次数据
    private boolean isFirstTime_post = true;
    private Connection conn;
    private boolean isOpen = false;
    private Handler mhandler;
    private int carState;
    private int acc_spd, break_spd;
    private String obe_id = "111";
    private String gpsStr, obdStr, fStr;
    private boolean isNotice_start = false;
    private boolean isNotice_flamout = false;
    private SensorManager mSensorManager;
    private MyAccSensorEventListener mMysensorEventListener;
    private MyGyrSensorEventListener mGyrSensorEventListener;
    private Sensor mAcceSensor; // 加速度传感器;
    private Sensor mGyroscopSensor; // 陀螺仪
    private List<MotionData> mAcceList;
    private List<MotionData> mGyrList;
    private boolean isFirstRun = true; // 第一个点
    private boolean isFirstLoc = true; // 是否第一次定位成功
    private MySensorRunnable myRunnable;
    private int speed = 0;
    private float revolution = 0;
    private AMapNavi mAmapNavi;
    private AMapNaviListener mAmapNaviListener;
    private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    private boolean startNavi;
    private Logger log;
    private BleOBD bleOBD;
    /**
     * 采集数据线程 30s 将所有数据风封装到JSONArray里
     */
    private Thread dataCollectionThread = new Thread(new Runnable() {

        @Override
        public void run() {
            while (isAlive) {
                putGpsDataToJSON();
                putOBDData();
                if (isFirstTime_post) {
                    isFirstTime_post = false;
                } else {
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                        // TODO Auto-generated catch block
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
        log = LoggerFactory.getLogger("odb.service");
//        log.info("OBDDataService onStartCommand");
        Log.d(TAG,"OBDDataService onStartCommand");
        bleOBD = new BleOBD();
        bleOBD.initOBD();
        DriveBehaviorHappend.getInstance().setListener(this);
        try {
            if (conn != null && !isOpen) {
//				conn.closeConn();
//				conn.interrupt();
//				conn = null;
//				initConn();
//				conn.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "OBDDataService create");
        mAmapNavi = AMapNavi.getInstance(this);// 初始化导航引擎
        init();
        initMapLocation();

    }

    private void init() {
        gpsDataListToSend = new ArrayList<MyGPSData>();
        positionAry_list = new ArrayList<JSONArray>();
        unAvalableList = new ArrayList<AMapLocation>();
        postOBDDataArr = new ArrayList<JSONArray>();
        mhandler = new Handler();
        initSensor();
        initConn();
        mAmapNavi.setAMapNaviListener(getAMapNaviListener());
        mAmapNavi.startGPS();
    }

    private void initConn() {
        conn = Connection.getInstance(this);
        conn.addStateChangeCallBack(this);
        conn.addReceivedCallBack(this);
        conn.setStartNaviCallBack((StartNaviCallBack) this);
    }

    // 初始化传感器相关
    private void initSensor() {
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

    // 初始化定位相关
    private void initMapLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 1000, 10, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isAlive = false;
        conn.closeConn();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        String provider = location.getProvider();
        if (GPSdataTime < 2 && !provider.equals("lbs")) {
            GPSdataTime++;
            return;
        }
        // 保存当前定位点
        LocationUtils.getInstance(this).setCurrentLocation(
                location.getLatitude(), location.getLongitude());
        // m每秒转换成千米每小时
        if (location.hasSpeed() && location.getSpeed() > 0)
            location.setSpeed(location.getSpeed() * 36 / 10);
        if (isFirstLoc) {
            last_Location = location;
            isFirstLoc = false;
        }
        // 第一阶段过滤
        if (LocationFilter.checkStageOne(location.getLatitude(),
                location.getLongitude(), location.getAccuracy(),
                location.getBearing())) {
            // 第一个点，只用第一阶段过滤和速度过滤
            if (isFirstRun) {
                if (LocationFilter.checkSpeed(location.getSpeed())) {
                    isFirstRun = false;
                    isAvalable = true;
                } else {
                    isAvalable = false;
                    unAvalableList.add(location);
                }
            } else {
                if (location.getSpeed() > 2
                        && LocationFilter.checkStageTwo(last_Location
                        .getSpeed(), location.getSpeed(), TimeUtils
                        .dateLongFormatString(last_Location.getTime(),
                                TimeUtils.format1), TimeUtils
                        .dateLongFormatString(location.getTime(),
                                TimeUtils.format1))) { // 如果不是第一个点且速度大于2，则需通过第二阶段过滤
                    Log.w("lxh", "第二阶段过滤成功");
                    isAvalable = true;
                    unAvalableList.clear();
                } else if (location.getSpeed() >= 0
                        && location.getSpeed() <= 2
                        && LocationFilter.checkStageTwo(last_Location
                        .getSpeed(), location.getSpeed(), TimeUtils
                        .dateLongFormatString(last_Location.getTime(),
                                TimeUtils.format1), TimeUtils
                        .dateLongFormatString(location.getTime(),
                                TimeUtils.format1))
                        && LocationFilter
                        .checkSpeedDValue(location.getSpeed(), location
                                        .getSpeed(), TimeUtils
                                        .dateLongFormatString(
                                                location.getTime(),
                                                TimeUtils.format1), TimeUtils
                                        .dateLongFormatString(
                                                location.getTime(),
                                                TimeUtils.format1), location
                                        .getLatitude(),
                                location.getLongitude(), location
                                        .getLatitude(), location
                                        .getLongitude())) { // 速度小于2，需经过第二阶段过滤

                    Log.w("lxh", "第三阶段过滤成功");
                    // 和静态过滤)
                    isAvalable = true;
                    unAvalableList.clear();
                } else {
                    isAvalable = false;
                    unAvalableList.add(location);
                    if (unAvalableList.size() == 3) {
                        // 如果第一个点和第二个点通过第二阶段过滤，则再将第二个点和第三个点用第二阶段的规则过滤，否则清空列表
                        if (LocationFilter.checkStageTwo(unAvalableList.get(0)
                                        .getSpeed(), unAvalableList.get(1).getSpeed(),
                                TimeUtils.dateLongFormatString(unAvalableList
                                        .get(0).getTime(), TimeUtils.format1),
                                TimeUtils.dateLongFormatString(unAvalableList
                                        .get(1).getTime(), TimeUtils.format1))) {
                            if (LocationFilter.checkStageTwo(unAvalableList
                                            .get(1).getSpeed(), unAvalableList.get(2)
                                            .getSpeed(), TimeUtils
                                            .dateLongFormatString(unAvalableList.get(1)
                                                    .getTime(), TimeUtils.format1),
                                    TimeUtils.dateLongFormatString(
                                            unAvalableList.get(2).getTime(),
                                            TimeUtils.format1))) {
                                isAvalable = true;
                                location = unAvalableList.get(2);
                                // unAvalableList.clear();
                            } else {
                                unAvalableList.clear();
                            }
                        } else {
                            unAvalableList.clear();
                        }
                    }
                }
            }
            if (isAvalable) {
                MyGPSData myGpsData = new MyGPSData(location.getLatitude(),
                        location.getLongitude(), location.getSpeed(),
                        location.getAltitude(), location.getBearing(),
                        TimeUtils.dateLongFormatString(location.getTime(),
                                TimeUtils.format1), location.getAccuracy(), 0);
                if (gpsDataListToSend != null
                        && !gpsDataListToSend.contains(myGpsData)) {
                    gpsDataListToSend.add(myGpsData);
                }
                unAvalableList.clear();
            }

        } else {
            Log.d(TAG, "GPS未通过过滤");
        }

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

        // 更新preLocation
        last_Location = location;

        cur_Location = location;
    }



    // 将gps数据转换为JSON 格式
    private void putGpsDataToJSON() {
        JSONArray positionAry = new JSONArray();
        try {
            if (gpsDataListToSend != null && gpsDataListToSend.size() > 0) {
                for (int i = 0; i < gpsDataListToSend.size(); i++) {
                    MyGPSData position = gpsDataListToSend.get(i);
                    if (position != null) {
                        Gson gson = new Gson();
                        positionAry.put(i,
                                new JSONObject(gson.toJson(position)));
                    }
                }
                positionAry_list.add(positionAry);
                gpsDataListToSend.clear();
                Log.d(TAG, "collect gpsData:" + positionAry.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 发送GPS数据
    private void sendGpsData(JSONArray gpsData) {
        gpsStr = new ExtraDataProcess().getUpLoadGpsData(gpsData, obe_id)
                .toString();
        conn.sendMessage(gpsStr, true);
        positionAry_list.remove(0);
    }

    // 发送OBD数据
    private void sendOBDData(JSONArray obdData) {
        obdStr = new ExtraDataProcess().getUpLoadOBDData(obdData, obe_id)
                .toString();
        conn.sendMessage(obdStr, true);
        postOBDDataArr.remove(0);
    }

    // 发送熄火数据
    private void sendFlameOutData() {
        Gson gson = new Gson();
        fStr = gson.toJson(bleOBD.getFlamoutData());
        conn.sendMessage(fStr, true);
        if (last_Location != null) {
            MyGPSData flameOutgps = new MyGPSData(last_Location.getLatitude(),
                    last_Location.getLongitude(), last_Location.getSpeed(),
                    last_Location.getAltitude(), last_Location.getBearing(),
                    TimeUtils.dateLongFormatString(last_Location.getTime(),
                            TimeUtils.format1), last_Location.getAccuracy(), 0);
            Gson gson2 = new Gson();
            String flameOutgpsStr = gson2.toJson(flameOutgps);
            conn.sendMessage(flameOutgpsStr, true);
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {

    }

    @Override
    public void deactivate() {
        // TODO Auto-generated method stub
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

    @Override
    public void onSessionStateChange(int state) {
        if (state == Connection.SESSION_OPEND) {
            isOpen = true;
        }
    }

    @Override
    public void OnRecieveFromServerMsg(String method, String resultCode,
                                       String resultDes) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Received msg:" + method);
        switch (method) {
            case DRIVE_DATAS:
                fStr = null;
                break;
            case OBD_DATA:
                obdStr = null;
                break;
            case COORDINATES:
                gpsStr = null;
                break;
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
        if (cur_Location != null) {
            MyGPSData event = new MyGPSData(cur_Location.getLatitude(),
                    cur_Location.getLongitude(), cur_Location.getSpeed(),
                    cur_Location.getAltitude(), cur_Location.getBearing(),
                    TimeUtils.dateLongFormatString(cur_Location.getTime(),
                            TimeUtils.format1), cur_Location.getAccuracy(),
                    type);
            gpsDataListToSend.add(event);
        }
    }

    // 将OBD数据存放在JSONArray中
    private void putOBDData() {

        if (!bleOBD.getObdCollectionList().isEmpty()) {
            JSONArray jsArr = new JSONArray();
            for (int i = 0; i < bleOBD.getObdCollectionList().size(); i++) {
                OBDData obdData = bleOBD.getObdCollectionList().get(i);
                Gson obd = new Gson();
                try {
                    if (obdData != null)
                        jsArr.put(new JSONObject(obd.toJson(obdData)));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

    // 车速与发动机转速不匹配
    private void misMatch() {
        boolean first = speed < 30 && revolution > 3000;
        boolean second = (speed < 60 && speed > 30) && revolution > 3500;
        boolean third = (speed < 90 && speed > 60) && revolution > 4000;
        boolean forth = (speed < 110 && speed > 90) && revolution > 4500;
        boolean five = (speed < 130 && speed > 110) && revolution > 5000;
        boolean six = (speed < 150 && speed > 130) && revolution > 5500;
        if (first || second || third || forth || five || six) {
            onDriveBehaviorHappend(DriveBehaviorHappend.TYPE_MISMATCH);
        }
    }

    @Override
    public void startNavi(double lat, double lon) {
        Log.d(TAG, "startNavi");
        if (cur_Location != null) {
            startNavi = true;
            naviGaode(lat, lon);
        }
    }

    // 高德导航
    private void naviGaode(double lat, double lon) {
        System.out.println("---导航");
        mEndPoint = new NaviLatLng(lat, lon);
        int driverIndex = calculateDriverRoute(lat, lon);
        if (driverIndex == CALCULATEERROR) {
            ToastUtils.showTip("路线计算失败,检查参数情况");
            return;
        }
    }

    // 高德路径规划
    private int calculateDriverRoute(double elat, double elon) {
        int code = CALCULATEERROR;
        if (cur_Location != null) {
            NaviLatLng naviLatLng = new NaviLatLng(cur_Location.getLatitude(),
                    cur_Location.getLongitude());
            NaviLatLng endLatlon = new NaviLatLng(elat, elon);
            mEndPoints.clear();
            mEndPoints.add(endLatlon);
            mStartPoints.clear();
            mStartPoints.add(naviLatLng);
            if (mAmapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null,
                    AMapNavi.DrivingDefault)) {
                code = CALCULATESUCCESS;
            } else {
                code = CALCULATEERROR;
            }
        }
        return code;
    }

    private AMapNaviListener getAMapNaviListener() {
        if (mAmapNaviListener == null) {

            mAmapNaviListener = new AMapNaviListener() {

                @Override
                public void onTrafficStatusUpdate() {

                }

                @Override
                public void onStartNavi(int arg0) {
                    System.out.println("-------onStartNavi");
                }

                @Override
                public void onReCalculateRouteForYaw() {
                }

                @Override
                public void onReCalculateRouteForTrafficJam() {
                }

                @Override
                public void onLocationChange(AMapNaviLocation location) {
                }

                @Override
                public void onInitNaviSuccess() {
                }

                @Override
                public void onInitNaviFailure() {

                }

                @Override
                public void onGetNavigationText(int arg0, String arg1) {
                    if(startNavi)
					    VoiceManager.getInstance().startSpeaking(arg1, SemanticConstants.TTS_DO_NOTHING,false);
                }

                @Override
                public void onEndEmulatorNavi() {
                }

                @Override
                public void onCalculateRouteSuccess() {
                    if (startNavi) {
                        LocationUtils.getInstance(OBDDataService.this).setNaviStartPoint
                                (mStartPoints.get(0).getLatitude(), mStartPoints.get(0).getLongitude());

                        LocationUtils.getInstance(OBDDataService.this).setNaviStartPoint
                                (mEndPoint.getLatitude(), mEndPoint.getLongitude());

						ActivitiesManager.getInstance().closeTargetActivity(
								NaviCustomActivity.class);
						Intent standIntent = new Intent(getBaseContext(),NaviCustomActivity.class);
						standIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(standIntent);
                        startNavi = false;
                    }

                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    if(startNavi){
                        String playText = "路径规划出错";
                        VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING,false);
                        FloatWindowUtil.showMessage(playText,
                                FloatWindow.MESSAGE_OUT);
                    }

                }

                @Override
                public void onArrivedWayPoint(int arg0) {
                }

                @Override
                public void onArriveDestination() {
                }

                @Override
                public void onGpsOpenStatus(boolean arg0) {
                }

                @Override
                public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                }

                @Override
                public void onNaviInfoUpdate(NaviInfo arg0) {
                }
            };
        }
        return mAmapNaviListener;
    }

    @Override
    public void onCarStateChange(int state) {
        carState = state;
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
}
