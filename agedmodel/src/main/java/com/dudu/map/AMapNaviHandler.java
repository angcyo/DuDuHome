package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.dudu.agedmodel.AgedMapActivity;
import com.dudu.agedmodel.AgedNavigationActivity;
import com.dudu.utils.LocationUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/26.
 */
public class AMapNaviHandler{
    private Context mContext;
    private List<NaviLatLng> startPoints=new ArrayList<>();
    private List<NaviLatLng> endPoints=new ArrayList<>();
    private NaviLatLng endPoint;
    private int codeDriver= AMapNavi.DrivingDefault;
    private static AMapNaviHandler navigationHandler;

    public static AMapNaviHandler getInstance(Context context){
        if(navigationHandler==null){
            navigationHandler = new AMapNaviHandler(context);
        }
        return  navigationHandler;
    }
    public AMapNaviHandler(Context context) {
        mContext=context;
    }
    public void initNavigationHandle(){
        AMapNavi.getInstance( mContext).setAMapNaviListener(new MyAMapNaviListener());
        AMapNavi.getInstance(mContext).startGPS();
    }
    public void startNavi(double [] destination){
        handleNavigation(destination);
    }

    private void handleNavigation(double [] destination) {
        double[] startPoint= LocationUtils.getInstance(mContext).getCurrentCoordinate();
        NaviLatLng  mStartPoint = new NaviLatLng(startPoint[0], startPoint[1]);
        startPoints.add(mStartPoint);
        endPoint=new NaviLatLng(destination[0],destination[1]);
        endPoints.add(endPoint);
        /**
         * 导航计算路线
         * 开始坐标，结束坐标，路线规划类型，默认速度最快
         * */
        boolean isSuccess=AMapNavi.getInstance(mContext).calculateDriveRoute(startPoints,endPoints,null,codeDriver);
        if (isSuccess){
        }else {
        }

    }

    private class MyAMapNaviListener implements AMapNaviListener{

        @Override
        public void onInitNaviFailure() {
            Log.v("ji..", "[{}] 导航创建失败");

        }

        @Override
        public void onInitNaviSuccess() {
            Log.v("ji..","[{}] 导航创建成功");
        }

        @Override
        public void onStartNavi(int i) {
            Log.v("ji..", "[{}] 启动导航后");

        }

        @Override
        public void onTrafficStatusUpdate() {
            Log.v("ji..","[{}] 路况更新");

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
            Log.v("ji.."," [{}] GPS位置有更新");

        }

        @Override
        public void onGetNavigationText(int i, String s) {
            Log.v("ji..","[{}] 导航播报信息:"+s);

        }

        @Override
        public void onEndEmulatorNavi() {
            Log.v("ji..","[{}] 模拟导航停止");

        }

        @Override
        public void onArriveDestination() {
            Log.v("ji..","[{}] 驾车路径导航到达某个途经点");
        }

        @Override
        public void onCalculateRouteSuccess() {
            Log.v("ji..","[{}] 步行或者驾车路径规划成功");
            mContext.startActivity(new Intent(mContext, AgedNavigationActivity.class));

        }

        @Override
        public void onCalculateRouteFailure(int i) {
            mContext.startActivity(new Intent(mContext, AgedNavigationActivity.class));
            Log.v("ji..","[{}] 步行或者驾车路径规划失败");

        }

        @Override
        public void onReCalculateRouteForYaw() {
            Log.v("ji..","[{}] 驾车导航时，如果前方遇到拥堵时需要重新计算路径");

        }

        @Override
        public void onReCalculateRouteForTrafficJam() {
            Log.v("ji.."," [{}] 步行或驾车导航时,出现偏航后需要重新计算路径");

        }

        @Override
        public void onArrivedWayPoint(int i) {
            Log.v("ji..","[{}] 启动导航后");

        }

        @Override
        public void onGpsOpenStatus(boolean b) {


        }

        @Override
        public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {


        }

        @Override
        public void onNaviInfoUpdate(NaviInfo naviInfo) {


        }
    }
    public void destoryAmapNavi(){
        AMapNavi.getInstance(mContext).removeAMapNaviListener(new MyAMapNaviListener());
        AMapNavi.getInstance(mContext).stopNavi();
        AMapNavi.getInstance(mContext).destroy();
        navigationHandler = null;
        Activity activity=(Activity)mContext;
        if(activity instanceof AgedMapActivity){
            activity.finish();
        }
    }
}
