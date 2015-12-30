package com.dudu.navi.service;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.dudu.monitor.Monitor;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.Util.NaviUtils;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.event.NaviEvent;
import com.dudu.navi.vauleObject.NavigationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

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

    private NavigationType navigationType;

    private AMapLocation cur_location;

    private boolean isSucess = false;

    private boolean iscalculate = false;
    public NaviProcess(Context context) {
        this.mContext = context;
        log = LoggerFactory.getLogger("lbs.navi");

    }

    public static NaviProcess getInstance(Context context) {

        if (naviProcess == null)
            naviProcess = new NaviProcess(context);

        return naviProcess;
    }

    public void initNaviProcess() {

    }

    public void initNaviListener() {
        log.debug("initNaviListener");
        AMapNavi.getInstance(mContext).setAMapNaviListener(getAMapNaviListener());
        AMapNavi.getInstance(mContext).startGPS();
        AMapNavi.getInstance(mContext).setDetectedMode(1);
    }

    public void destoryAmapNavi() {
        log.debug("destoryAmapNavi");
        AMapNavi.getInstance(mContext).removeAMapNaviListener(mAmapNaviListener);
        AMapNavi.getInstance(mContext).stopNavi();
        AMapNavi.getInstance(mContext).destroy();

    }

    public void calculateDriverRoute(Navigation navigation) {
        log.debug("calculateDriverRoute");
        isSucess = false;
        iscalculate = true;
        Observable.timer(20, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isSucess) {
                            iscalculate = false;
                            EventBus.getDefault().post(NavigationType.CALCULATEERROR);
                        }
                    }
                });
        navigationType = navigation.getType();
        switch (NaviUtils.getOpenMode(mContext)) {
            case OUTSIDE:
                NavigationManager.getInstance(mContext).setNavigationType(NavigationType.NAVIGATION);
                NaviPara naviPara = new NaviPara();
                naviPara.setTargetPoint(new LatLng(navigation.getDestination().latitude,
                        navigation.getDestination().longitude));
                naviPara.setNaviStyle(navigation.getDriveMode().ordinal());
                try {
                    AMapUtils.openAMapNavi(naviPara, mContext);
                } catch (AMapException e) {
                    e.printStackTrace();
                }
                break;
            case INSIDE:
                initNaviListener();
                calculateInside(navigation);
                break;
        }

    }

    private int calculateInside(Navigation navigation) {
        log.debug("-----calculateInside");
        cur_location = Monitor.getInstance(mContext).getCurrentLocation();

        int code = CALCULATEERROR;
        NaviLatLng mEndPoint = new NaviLatLng(navigation.getDestination().latitude, navigation.getDestination().longitude);
        if (cur_location != null && mEndPoint != null) {
            NaviLatLng naviLatLng = new NaviLatLng(cur_location.getLatitude(), cur_location.getLongitude());
            mEndPoints.clear();
            mEndPoints.add(new NaviLatLng(navigation.getDestination().latitude, navigation.getDestination().longitude));
            mStartPoints.clear();
            mStartPoints.add(naviLatLng);
            if (AMapNavi.getInstance(mContext).calculateDriveRoute(mStartPoints, mEndPoints, null,
                    navigation.getDriveMode().ordinal())) {
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
                    log.debug("[{}] 路况更新", step++);
                }

                @Override
                public void onStartNavi(int arg0) {
                    log.debug("[{}] 启动导航后", step++);
                }

                @Override
                public void onReCalculateRouteForYaw() {

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
                    EventBus.getDefault().post(new NaviEvent.NavigationInfoBroadcast(arg1));

                }

                @Override
                public void onEndEmulatorNavi() {
                    log.debug("[{}] 模拟导航停止", step++);
                }

                @Override
                public void onCalculateRouteSuccess() {
                    log.debug("[{}] 步行或者驾车路径规划成功", step++);
                   if(iscalculate){
                       isSucess = true;
                       iscalculate = false;
                       EventBus.getDefault().post(navigationType);
                   }


                }

                @Override
                public void onCalculateRouteFailure(int arg0) {

                }

                @Override
                public void onArrivedWayPoint(int arg0) {
                    log.debug("[{}] 驾车路径导航到达某个途经点", step++);
                }

                @Override
                public void onArriveDestination() {
                    log.debug("[{}] 到达目的地后", step++);
                    NavigationManager.getInstance(mContext).setIsNavigatining(false);
                    EventBus.getDefault().post(NavigationType.NAVIGATION_END);
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

                @Override
                public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

                }

                @Override
                public void showCross(AMapNaviCross aMapNaviCross) {

                }

                @Override
                public void hideCross() {

                }

                @Override
                public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

                }

                @Override
                public void hideLaneInfo() {

                }
            };
        }
        return mAmapNaviListener;
    }

}
