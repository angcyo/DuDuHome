package com.dudu.navi.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.vauleObject.NaviEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/11/25.
 */
public class NaviProcess {

    private static NaviProcess naviProcess;

    private Context mContext;

    private AMapNaviListener mAmapNaviListener;

    private List<NaviLatLng> mStartPoints = new ArrayList<>();

    private List<NaviLatLng> mEndPoints = new ArrayList<>();

    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态

    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态

    private Logger log;

    private int step;

    public NaviProcess(Context context){
        this.mContext = context;
        log = LoggerFactory.getLogger("lbs.navi");

    }

    public static NaviProcess getInstance(Context context){

        if(naviProcess==null)
            naviProcess = new NaviProcess(context);

        return naviProcess;
    }

    public void initNaviProcess(){
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    public void initNaviListener(){
        log.debug("initNaviListener");
        AMapNavi.getInstance(mContext).setAMapNaviListener(getAMapNaviListener());
        AMapNavi.getInstance(mContext).startGPS();
    }

    public void destoryAmapNavi(){
        log.debug("destoryAmapNavi");
        AMapNavi.getInstance(mContext).removeAMapNaviListener(mAmapNaviListener);
        AMapNavi.getInstance(mContext).stopNavi();
        AMapNavi.getInstance(mContext).destroy();
    }

    private int calculateDriverRoute(Navigation navigation) {
        log.debug("calculateDriverRoute");

        int code = CALCULATEERROR;
//        double[] cur_Location = LocationUtils.getInstance(mContext).getCurrentLocation();
//        if (cur_Location != null && mEndPoint!=null) {
//            System.out.print("路线规划");
//            NaviLatLng naviLatLng = new NaviLatLng(cur_Location[0],
//                    cur_Location[1]);
//            mEndPoints.clear();
//            mEndPoints.add(mEndPoint);
//            mStartPoints.clear();
//            mStartPoints.add(naviLatLng);
//            if (AMapNavi.getInstance(mContext).calculateDriveRoute(mStartPoints, mEndPoints, null,
//                    driveMode)) {
//                code = CALCULATESUCCESS;
//            } else {
//                code = CALCULATEERROR;
//            }
//        }

        return code;
    }


    private AMapNaviListener getAMapNaviListener() {
        if (mAmapNaviListener == null) {


            mAmapNaviListener = new AMapNaviListener() {

                @Override
                public void onTrafficStatusUpdate() {
                    log.debug("[{}] 路况更新", step++);
                }

                @Override
                public void onStartNavi(int arg0) {
                    log.debug("[{}] 启动导航后", step++);
                }

                @Override
                public void onReCalculateRouteForYaw() {

                    EventBus.getDefault().post(new NaviEvent.NaviVoiceBroadcast("您已偏离路线"));
                    log.debug("[{}] 步行或驾车导航时,出现偏航后需要重新计算路径", step++);
                }

                @Override
                public void onReCalculateRouteForTrafficJam() {
                    log.debug("[{}] 驾车导航时，如果前方遇到拥堵时需要重新计算路径", step++);
                }

                @Override
                public void onLocationChange(AMapNaviLocation location) {
                    log.debug("[{}] GPS位置有更新", step++);
                }

                @Override
                public void onInitNaviSuccess() {
                    log.debug("[{}] 导航创建成功", step++);
                }

                @Override
                public void onInitNaviFailure() {
                    log.debug("[{}] 导航创建失败", step++);
                }

                @Override
                public void onGetNavigationText(int arg0, String arg1) {
                    log.debug("[{}] 导航播报信息", step++);
                    EventBus.getDefault().post(new NaviEvent.NaviVoiceBroadcast(arg1));

                }

                @Override
                public void onEndEmulatorNavi() {
                    log.debug("[{}] 模拟导航停止", step++);
                }

                @Override
                public void onCalculateRouteSuccess() {
                    log.debug("[{}] 步行或者驾车路径规划成功", step++);


//                    if(MapManager.getInstance().isNavi()||MapManager.getInstance().isNaviBack()){
//                        log.debug("[{}] 导航过程中路线规划成功", step++);
//                        return;
//                    }
//                    MapManager.getInstance().setSearchType(0);
//                    switch (naviType){
//
//                        case Navigation.NAVI_NORMAL:
//                        case Navigation.NAVI_TWO:
//
//                            naviClass = NaviCustomActivity.class;
//                            break;
//
//                        case Navigation.NAVI_BACK:
//                            naviClass = NaviBackActivity.class;
//                            break;
//
//                    }
//                    LocationUtils.getInstance(mContext).setNaviStartPoint
//                            (mStartPoints.get(0).getLatitude(), mStartPoints.get(0).getLongitude());
//
//                    LocationUtils.getInstance(mContext).setNaviStartPoint
//                            (mEndPoint.getLatitude(), mEndPoint.getLongitude());
//                    Activity topActivity = ActivitiesManager.getInstance().getTopActivity();
//                    Intent standIntent = new Intent(topActivity,naviClass);
//                    standIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    topActivity.startActivity(standIntent);
//                    topActivity.finish();
                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    log.debug("[{}] 步行或者驾车路径规划失败", step++);
                    String playText = "路径规划出错,请检查网络";
                    EventBus.getDefault().post(new NaviEvent.NaviVoiceBroadcast(playText));
                }

                @Override
                public void onArrivedWayPoint(int arg0) {
                    log.debug("[{}] 驾车路径导航到达某个途经点", step++);
                }

                @Override
                public void onArriveDestination() {
                    log.debug("[{}] 到达目的地后", step++);
                }

                @Override
                public void onGpsOpenStatus(boolean arg0) {
                    log.debug("[{}] 用户手机GPS设置是否开启：{}", step++, arg0);
                }

                @Override
                public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                    log.debug("[{}] 导航引导信息", step++);
                }

                @Override
                public void onNaviInfoUpdate(NaviInfo arg0) {
                    log.debug("[{}] 当驾车或者步行实时导航或者模拟导航有位置变化时", step++);
                }
            };
        }
        return mAmapNaviListener;
    }

}
