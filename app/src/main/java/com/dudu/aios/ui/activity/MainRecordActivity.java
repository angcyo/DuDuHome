package com.dudu.aios.ui.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.base.BaseFragmentManagerActivity;
import com.dudu.aios.ui.fragment.FlowFragment;
import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.aios.ui.fragment.PhotoFragment;
import com.dudu.aios.ui.fragment.PhotoListFragment;
import com.dudu.aios.ui.fragment.SafetyMainFragment;
import com.dudu.aios.ui.fragment.VideoFragment;
import com.dudu.aios.ui.fragment.VideoListFragment;
import com.dudu.aios.ui.fragment.base.BaseManagerFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.aios.ui.voice.VoiceFragment;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.TFlashCardReceiver;
import com.dudu.android.launcher.broadcast.WeatherAlarmReceiver;
import com.dudu.android.launcher.utils.AdminReceiver;
import com.dudu.event.DeviceEvent;
import com.dudu.init.InitManager;
import com.dudu.navi.event.NaviEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wysaid.camera.CameraInstance;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class MainRecordActivity extends BaseFragmentManagerActivity {
    private static final int SET_PREVIEW = 0;
    private static final int INIT_FRAGMENTS = 1;

    private AlarmManager mAlarmManager;

    private TFlashCardReceiver mTFlashCardReceiver;

    private Logger log_init;

    private DevicePolicyManager mPolicyManager;

    private ComponentName componentName;

    private static final int MY_REQUEST_CODE = 9999;

    private static final String MAIN_FRAGMENT = "mainfragment";
    private static final String SAFETY_FRAGMENT = "safetyFragment";
    private static final String DRIVINGRECORD_FRAGMENT = "drivingRecordFragment";
    private static final String PHOTO_FRAGMENT = "photoFragment";
    private static final String PHOTOLIST_FRAGMENT = "photoListFragment";
    private static final String VIDEO_FRAGMENT = "videoFragment";
    private static final String FLOW_FRAGMENT = "flowFragment";
    private static final String VOICE_FRAGMENT = "voiceFragment";
    private static final String VIDEOLIST_FRAGMENT = "videoListFragment";

    @Override
    public int fragmentViewId() {
        return R.id.container;
    }

    @Override
    public Map<String, Class<? extends BaseManagerFragment>> baseFragmentWithTag() {
        Map<String, Class<? extends BaseManagerFragment>> fragmentMap = new HashMap<>();
        fragmentMap.put(MAIN_FRAGMENT,MainFragment.class);
        fragmentMap.put(SAFETY_FRAGMENT,SafetyMainFragment.class);
//        fragmentMap.put(DRIVINGRECORD_FRAGMENT,DrivingRecordFragment.class);
        fragmentMap.put(PHOTO_FRAGMENT,PhotoFragment.class);
        fragmentMap.put(PHOTOLIST_FRAGMENT,PhotoListFragment.class);
        fragmentMap.put(VIDEO_FRAGMENT,VideoFragment.class);
        fragmentMap.put(FLOW_FRAGMENT,FlowFragment.class);
        fragmentMap.put(VOICE_FRAGMENT,VoiceFragment.class);
        fragmentMap.put(VIDEOLIST_FRAGMENT,VideoListFragment.class);
        return fragmentMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPreview();

        initFragment(savedInstanceState);

        initData();

    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_record, null);
    }

    private void initData() {
        log_init = LoggerFactory.getLogger("init.start");

        log_init.debug("MainActivity 调用onCreate方法初始化...");

        EventBus.getDefault().unregister(this);

        EventBus.getDefault().register(this);

        InitManager.getInstance().init();

        setWeatherAlarm();

        registerTFlashCardReceiver();

        // 获取设备管理服务
        mPolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        // 自己的AdminReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(this, AdminReceiver.class);
    }


    private void initFragment(Bundle savedInstanceState) {
        switchToStackByTag(MAIN_FRAGMENT);
    }

    public void replaceFragment(String name) {
        switch (name) {
            case FragmentConstants.FRAGMENT_MAIN_PAGE:
                switchToStackByTag(MAIN_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_VEHICLE_INSPECTION:
                switchToStackByTag(SAFETY_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_DRIVING_RECORD:
                switchToStackByTag(DRIVINGRECORD_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_VIDEO_LIST:
                switchToStackByTag(VIDEOLIST_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_VIDEO:
                switchToStackByTag(VIDEO_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_PHOTO_LIST:
                switchToStackByTag(PHOTOLIST_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_PHOTO:
                switchToStackByTag(PHOTO_FRAGMENT);
                break;

            case FragmentConstants.FRAGMENT_FLOW:
                switchToStackByTag(FLOW_FRAGMENT);
                break;
            case FragmentConstants.VOICE_FRAGMENT:
                switchToStackByTag(VOICE_FRAGMENT);
                break;
            default:
                switchToStackByTag(MAIN_FRAGMENT);
                break;

        }

        if (!name.equals(FragmentConstants.VOICE_FRAGMENT)) {
            LauncherApplication.lastFragment = name;
        }
    }

    private void setWeatherAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WeatherAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 20);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30 * 60 * 1000, pi);
    }

    private void registerTFlashCardReceiver() {
        mTFlashCardReceiver = new TFlashCardReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        registerReceiver(mTFlashCardReceiver, intentFilter);
    }



    private void cancelWeatherAlarm() {
        Intent intent = new Intent(this, WeatherAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        mAlarmManager.cancel(pi);
    }

    public void onEventMainThread(DeviceEvent.Screen event) {
        log_init.debug("DeviceEvent.Screen {}", event.getState());
        if (event.getState() == DeviceEvent.OFF) {
            if (mPolicyManager.isAdminActive(componentName)) {
                mPolicyManager.lockNow();// 锁屏
            } else {
                activeManage(); //获取权限
            }
        }
    }

    private void activeManage() {
        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        // 权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);

        // 描述(additional explanation) 在申请权限时出现的提示语句
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "激活后就能一键锁屏了");

        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取权限成功，立即锁屏并finish自己，否则继续获取权限
        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mPolicyManager.lockNow();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        observableFactory.getCommonObservable(baseBinding).hasTitle.set(true);

        cameraView.onResume();
//        cameraView.resumePreview();
        if (LauncherApplication.startRecord) {
            replaceFragment(FragmentConstants.FRAGMENT_DRIVING_RECORD);

        } else {
            replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
        }
        LauncherApplication.startRecord = false;
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.HIDE);

    }


    @Override
    protected void onPause() {
        super.onPause();
//        CameraInstance.getInstance().stopCamera();
        cameraView.release(null);
        cameraView.onPause();

//        cameraView.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CameraInstance.getInstance().stopCamera();

        log_init.debug("MainRecordActivity 调用onDestroy释放资源...");

        InitManager.getInstance().unInit();

        cancelWeatherAlarm();

        unregisterReceiver(mTFlashCardReceiver);
    }

}