package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/3.
 */
public class NavigationHandler {

    private Context mContext;

    private AMapNaviListener mAmapNaviListener;

    private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();

    private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    private NaviLatLng mEndPoint;

    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态

    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态

    private AMapNavi mAmapNavi;

    private int naviType = 0;

    private int driveMode = AMapNavi.DrivingDefault;

    private double[] destination;

    private String naviAddress;

    private VoiceManager mVoiceManager = VoiceManager.getInstance();

    private Handler mHandler;

    private Class naviClass;
    public NavigationHandler(){

    }

    public void initNavigationHandle(Context context){

        mContext = context;
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        mAmapNavi = AMapNavi.getInstance(context);// 初始化导航引擎
        mAmapNavi.setAMapNaviListener(getAMapNaviListener());
        mAmapNavi.startGPS();
        mHandler = new Handler();
    }


    public void onEventBackgroundThread(Navigation event){

        naviType = event.getType();
        driveMode = event.getDriveMode();
        destination = event.getDestination();
        naviAddress = event.getNaviAddress();
        handleNavigation();

    }

    private void handleNavigation(){

        mEndPoint = new NaviLatLng(destination[0], destination[1]);
        int driverIndex = calculateDriverRoute();
        if (driverIndex == CALCULATEERROR) {
            return;
        }

    }

    private int calculateDriverRoute() {

        int code = CALCULATEERROR;
        double[] cur_Location = LocationUtils.getInstance(mContext).getCurrentLocation();
        if (cur_Location != null && mEndPoint!=null) {
            System.out.print("路线规划");
            NaviLatLng naviLatLng = new NaviLatLng(cur_Location[0],
                    cur_Location[1]);
            mEndPoints.clear();
            mEndPoints.add(mEndPoint);
            mStartPoints.clear();
            mStartPoints.add(naviLatLng);
            if (mAmapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null,
                    driveMode)) {
                code = CALCULATESUCCESS;
            } else {
                code = CALCULATEERROR;
            }
        }
        System.out.print("路线计算：" + code);
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
                    System.out.print("onStartNavi");
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


                    VoiceManager.getInstance().clearMisUnderstandCount();
                    VoiceManager.getInstance().startSpeaking(arg1, SemanticConstants.TTS_DO_NOTHING, false);


                }

                @Override
                public void onEndEmulatorNavi() {
                }

                @Override
                public void onCalculateRouteSuccess() {

                    MapManager.getInstance().setSearchType(0);
                    switch (naviType){

                        case Navigation.NAVI_NORMAL:
                        case Navigation.NAVI_TWO:

                            naviClass = NaviCustomActivity.class;
                            break;

                        case Navigation.NAVI_BACK:
                            naviClass = NaviBackActivity.class;
                            break;
                    }

                    LocationUtils.getInstance(mContext).setNaviStartPoint
                            (mStartPoints.get(0).getLatitude(), mStartPoints.get(0).getLongitude());

                    LocationUtils.getInstance(mContext).setNaviStartPoint
                                (mEndPoint.getLatitude(), mEndPoint.getLongitude());

                    ActivitiesManager.getInstance().closeTargetActivity(
                                NaviCustomActivity.class);
                    Activity topActivity = ActivitiesManager.getInstance().getTopActivity();
                    Intent standIntent = new Intent(topActivity,naviClass);
                    standIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    topActivity.startActivity(standIntent);
                    topActivity.finish();

                }

                @Override
                public void onCalculateRouteFailure(int arg0) {

                    String playText = "路径规划出错,请检查网络";
                    VoiceManager.getInstance().clearMisUnderstandCount();
                    VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING, true);
                    removeWindow();


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

    private void  removeWindow(){

        if(mHandler!=null){

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FloatWindowUtil.removeFloatWindow();
                }
            },2000);
        }

    }

    public void destoryAmapNavi(){

        mAmapNavi.removeAMapNaviListener(mAmapNaviListener);
        mAmapNavi.destroy();
    }
}
