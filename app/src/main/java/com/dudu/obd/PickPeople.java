package com.dudu.obd;

import android.content.Context;
import android.content.Intent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;

import org.scf4a.Event;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/2.
 */
public class PickPeople {

    private Context mContext;

    private AMapNaviListener mAmapNaviListener;

    private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();

    private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    private NaviLatLng mEndPoint;

    private boolean startNavi = false;

    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态

    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态

    private AMapNavi mAmapNavi;


    public PickPeople(){

    }

    public void init(Context context){
        mContext = context;
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        mAmapNavi = AMapNavi.getInstance(context);// 初始化导航引擎
        mAmapNavi.setAMapNaviListener(getAMapNaviListener());
        mAmapNavi.startGPS();
    }

    public void onEventBackgroundThread(PickPeopleEvent event){
        startNavi = true;
        double[] location = event.getLocation();
        naviGaode(location[0],location[1]);
    }

    private void naviGaode(double lat,double lon){
        int driverIndex = calculateDriverRoute(lat, lon);
        mEndPoint = new NaviLatLng(lat, lon);
        if (driverIndex == CALCULATEERROR) {

            ToastUtils.showTip("路线计算失败,检查参数情况");
            return;
        }
    }

    private int calculateDriverRoute(double elat, double elon) {

        int code = CALCULATEERROR;
        double[] cur_Location = LocationUtils.getInstance(mContext).getCurrentLocation();
        if (cur_Location != null) {
            System.out.print("路线规划");
            NaviLatLng naviLatLng = new NaviLatLng(cur_Location[0],
                    cur_Location[1]);
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

                    if(startNavi){
                        VoiceManager.getInstance().clearMisUnderstandCount();
                        VoiceManager.getInstance().startSpeaking(arg1, SemanticConstants.TTS_DO_NOTHING, false);
                    }

                }

                @Override
                public void onEndEmulatorNavi() {
                }

                @Override
                public void onCalculateRouteSuccess() {
                    if (startNavi) {

                        LocationUtils.getInstance(mContext).setNaviStartPoint
                                (mStartPoints.get(0).getLatitude(), mStartPoints.get(0).getLongitude());

                        LocationUtils.getInstance(mContext).setNaviStartPoint
                                (mEndPoint.getLatitude(), mEndPoint.getLongitude());

                        ActivitiesManager.getInstance().closeTargetActivity(
                                NaviCustomActivity.class);
                        Intent standIntent = new Intent(LauncherApplication.getContext().getBaseContext(),NaviCustomActivity.class);
                        standIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(standIntent);
                        startNavi = false;
                    }

                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    if(startNavi){
                        String playText = "路径规划出错";
                        VoiceManager.getInstance().clearMisUnderstandCount();
                        VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING,false);
                        FloatWindowUtil.showMessage(playText,
                                FloatWindow.MESSAGE_OUT);
                        System.out.print("------------------"+playText);
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



}
