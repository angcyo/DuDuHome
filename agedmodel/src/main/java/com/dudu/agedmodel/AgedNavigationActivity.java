package com.dudu.agedmodel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.dudu.event.LocationChangeEvent;
import com.dudu.map.AMapNaviHandler;
import com.dudu.utils.Contacts;
import com.dudu.utils.NaviSettingUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

public class AgedNavigationActivity extends NoTitleActivity implements AMapNaviViewListener {
    private AMapNaviView aMapNaviView;
    private boolean mDayNightFlag = NaviSettingUtil.DAY_MODE;// 默认为白天模式
    private boolean mDeviationFlag = NaviSettingUtil.YES_MODE;// 默认进行偏航重算
    private boolean mJamFlag = NaviSettingUtil.YES_MODE;// 默认进行拥堵重算
    private boolean mTrafficFlag = NaviSettingUtil.OPEN_MODE;// 默认进行交通播报
    private boolean mCameraFlag = NaviSettingUtil.OPEN_MODE;// 默认进行摄像头播报
    private boolean mScreenFlag = NaviSettingUtil.YES_MODE;// 默认是屏幕常亮
    private int mThemeStle;
    private Logger log;
    private Handler handler;
    private AgedNavigationActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_aged_navigation);
        initView(savedInstanceState);
        initData();
    }

    private void initView(Bundle savedInstanceState) {
        aMapNaviView = (AMapNaviView) findViewById(R.id.customAMapNavi);
        aMapNaviView.onCreate(savedInstanceState);
        log = LoggerFactory.getLogger("lbs.navi");
    }


    private void initData() {
        aMapNaviView.setAMapNaviViewListener(this);
        setAMapNaviViewParams();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        handler = new MyHandler();
    }


    //设置参数
    private void setAMapNaviViewParams() {
        AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
        viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
        viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
        viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
        viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
        viewOptions.setTrafficInfoUpdateEnabled(true);// 设置是否更新路况
        viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
        viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
        viewOptions.setNaviViewTopic(mThemeStle);// 设置导航界面主题样式
        viewOptions.setTrafficLayerEnabled(true);
        viewOptions.setTrafficLine(true);
        viewOptions.setTrafficBarEnabled(true);
        viewOptions.setLeaderLineEnabled(Color.RED);
        aMapNaviView.setViewOptions(viewOptions);
        aMapNaviView.getMap().setTrafficEnabled(true);
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    public void onEventMainThread(LocationChangeEvent event) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAMapNaviViewParams();
        AMapNavi.getInstance(this).startGPS();
        AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        aMapNaviView.onResume();
        handler.sendEmptyMessageDelayed(0, 10000);


    }

    @Override
    public void onPause() {
        aMapNaviView.onPause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        AMapNaviHandler.getInstance(getApplicationContext()).destoryAmapNavi();
        aMapNaviView.onDestroy();
        super.onDestroy();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(mActivity, AgedModelMainActivity.class);
            intent.putExtra(Contacts.CLASS_TYPE, Contacts.END_NAV_TYPE);
            startActivity(intent);
            finish();
        }
    }


}
