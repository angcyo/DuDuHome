package com.dudu.android.launcher.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NaviSettingUtil;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.android.launcher.utils.ViewAnimation;
import com.dudu.map.NavigationClerk;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * 实时导航界面
 */
public class NaviCustomActivity extends BaseNoTitlebarAcitivity implements
        AMapNaviViewListener {

    private static final String TAG = "NaviCustomActivity";

    private AMapNaviView mAmapAMapNaviView;
    // 导航可以设置的参数
    private boolean mDayNightFlag = NaviSettingUtil.DAY_MODE;// 默认为白天模式
    private boolean mDeviationFlag = NaviSettingUtil.YES_MODE;// 默认进行偏航重算
    private boolean mJamFlag = NaviSettingUtil.YES_MODE;// 默认进行拥堵重算
    private boolean mTrafficFlag = NaviSettingUtil.OPEN_MODE;// 默认进行交通播报
    private boolean mCameraFlag = NaviSettingUtil.OPEN_MODE;// 默认进行摄像头播报
    private boolean mScreenFlag = NaviSettingUtil.YES_MODE;// 默认是屏幕常亮
    // 导航界面风格
    private int mThemeStle;
    // 导航监听

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private Button back_button;

    private Handler mHandler;

    private Logger log;

    private String playText;

    private Runnable buttonRunnable = new Runnable() {
        @Override
        public void run() {
            buttonAnimation();
        }
    };

    @Override
    public int initContentView() {
        return R.layout.activity_navicustom;

    }


    @Override
    public void initView(Bundle savedInstanceState) {

        mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.customnavimap);
        mAmapAMapNaviView.onCreate(savedInstanceState);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        setAmapNaviViewOptions();
        back_button = (Button) findViewById(R.id.back_button);
        log = LoggerFactory.getLogger("lbs.navi");
    }

    @Override
    public void initListener() {
        back_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(ActivitiesManager.getInstance().getTopActivity(),
                            MainActivity.class));
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    startActivity(new Intent(NaviCustomActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }

    @Override
    public void initDatas() {
        mHandler = new Handler();

        mAmapAMapNaviView.getMap().setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                backButtonAutoHide();
                mAmapAMapNaviView.onTouch(motionEvent);
            }
        });
    }

    private void backButtonAutoHide() {
        if (back_button.getVisibility() != View.VISIBLE) {
            buttonAnimation();
        }

        mHandler.removeCallbacks(buttonRunnable);
        mHandler.postDelayed(buttonRunnable, 3000);
    }


    private void buttonAnimation() {
        ViewAnimation.startAnimation(back_button, back_button.getVisibility() == View.VISIBLE
                ? R.anim.back_key_disappear : R.anim.back_key_appear, NaviCustomActivity.this);
    }

    /**
     * 设置导航的参数
     */
    private void setAmapNaviViewOptions() {
        if (mAmapAMapNaviView == null) {
            return;
        }

        AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
        viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
        viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
        viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
        viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
        viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
        viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
        viewOptions.setNaviViewTopic(mThemeStle);// 设置导航界面主题样式
        viewOptions.setTrafficLayerEnabled(true);
        viewOptions.setTrafficLine(true);
        viewOptions.setLeaderLineEnabled(Color.RED);
        viewOptions.setCrossDisplayShow(false);
        viewOptions.setCrossDisplayEnabled(false);
        int time = Integer.parseInt(TimeUtils.format(TimeUtils.format6));
        if (time > 18 || time < 5)
            viewOptions.setNaviNight(true);
        mAmapAMapNaviView.setViewOptions(viewOptions);
        mAmapAMapNaviView.getMap().setTrafficEnabled(true);
    }

    // 全程预览
    public void mapPriview() {
        if (mAmapAMapNaviView != null && mAmapAMapNaviView.getMap() != null) {
            FloatWindowUtil.removeFloatWindow();
            mAmapAMapNaviView.getMap().moveCamera(CameraUpdateFactory.zoomTo(11));
        }

    }

    // 路况播报
    public void trafficInfo() {
        if (mAmapAMapNaviView != null && mAmapAMapNaviView.getViewOptions() != null) {
            FloatWindowUtil.removeFloatWindow();
            playText = "路况播报已打开";
            if (mAmapAMapNaviView.getViewOptions().isTrafficInfoUpdateEnabled()) {
                playText = "已经为您打开路况播报";
            } else {
                AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
                viewOptions.setTrafficInfoUpdateEnabled(true);
                mAmapAMapNaviView.setViewOptions(viewOptions);
            }
            mAmapAMapNaviView.getViewOptions().setTrafficInfoUpdateEnabled(true);

            VoiceManager.getInstance().startSpeaking(playText, SemanticConstants.TTS_DO_NOTHING);
        }
    }

    // 关闭路况播报
    public void closeTraffic() {
        if (mAmapAMapNaviView != null && mAmapAMapNaviView.getViewOptions() != null) {
            FloatWindowUtil.removeFloatWindow();
            if (mAmapAMapNaviView.getViewOptions().isTrafficInfoUpdateEnabled()) {
                AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
                viewOptions.setTrafficInfoUpdateEnabled(false);
                mAmapAMapNaviView.setViewOptions(viewOptions);
            }
            VoiceManager.getInstance().startSpeaking("路况播报已关闭", SemanticConstants.TTS_DO_NOTHING);
        }
    }

    public void closePriview() {
        FloatWindowUtil.removeFloatWindow();
        mAmapAMapNaviView.getMap().moveCamera(CameraUpdateFactory.zoomTo(18));
    }

    // 返程
    public void goBack() {
        final double[] points = LocationUtils.getInstance(this).getNaviStartPoint();
        FloatWindowUtil.removeFloatWindow();

        if (points != null) {
//            VoiceManager.getInstance().startSpeaking("正在为您进行路线规划", SemanticConstants.TTS_DO_NOTHING, false);
//            mHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    EventBus.getDefault().post(new Navigation(points, Navigation.NAVI_BACK, AMapNavi.DrivingDefault));
//                }
//            }, 800);

        }
    }


    /**
     * 导航界面返回按钮监听
     */
    @Override
    public void onNaviCancel() {
        finish();
    }

    /**
     * 点击设置按钮的事件
     */
    @Override
    public void onNaviSetting() {
    }

    @Override
    public void onNaviMapMode(int arg0) {

    }

    @Override
    public void onNaviTurnClick() {
        Intent intent = new Intent(NaviCustomActivity.this,
                SimpleHudActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Bundle bundle = new Bundle();
        bundle.putInt(NaviSettingUtil.ACTIVITYINDEX, NaviSettingUtil.SIMPLEHUDNAVIE);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    private void processBundle(Bundle bundle) {
        if (bundle != null) {
            mDayNightFlag = bundle.getBoolean(NaviSettingUtil.DAY_NIGHT_MODE,
                    mDayNightFlag);
            mDeviationFlag = bundle.getBoolean(NaviSettingUtil.DEVIATION, mDeviationFlag);
            mJamFlag = bundle.getBoolean(NaviSettingUtil.JAM, mJamFlag);
            mTrafficFlag = bundle.getBoolean(NaviSettingUtil.TRAFFIC, mTrafficFlag);
            mCameraFlag = bundle.getBoolean(NaviSettingUtil.CAMERA, mCameraFlag);
            mScreenFlag = bundle.getBoolean(NaviSettingUtil.SCREEN, mScreenFlag);
            mThemeStle = bundle.getInt(NaviSettingUtil.THEME);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAmapNaviViewOptions();
        NavigationManager.getInstance(this).setNavigationType(NavigationType.NAVIGATION);
        NavigationManager.getInstance(this).setIsNavigatining(true);
        AMapNavi.getInstance(this).startGPS();
        AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
        Bundle bundle = getIntent().getExtras();
        processBundle(bundle);
        if (bundle != null) {
            String type = bundle.getString("type");
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case Constants.NAVI_TRAFFIC:
                        trafficInfo();
                        break;
                    case Constants.NAVI_PREVIEW:
                        mapPriview();
                        break;
                    case Constants.RERURN_JOURNEY:
                        goBack();
                        break;
                    case Constants.CLOSE + Constants.NAVI_TRAFFIC:
                        closeTraffic();
                        break;
                    case Constants.CLOSE + Constants.NAVI_PREVIEW:
                        closePriview();
                        break;
                    default:
                        break;
                }
            }
        }
        mAmapAMapNaviView.onResume();

        backButtonAutoHide();
    }

    @Override
    public void onPause() {
        mAmapAMapNaviView.onPause();
        NavigationClerk.getInstance().setIsShowAddress(false);
        super.onPause();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        NavigationManager.getInstance(this).existNavigation();
        VoiceManager.getInstance().clearMisUnderstandCount();
        VoiceManager.getInstance().startSpeaking("导航结束", SemanticConstants.TTS_DO_NOTHING, false);
        try {
            mAmapAMapNaviView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onLockMap(boolean arg0) {


    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return super.onTouchEvent(event);

    }
}
