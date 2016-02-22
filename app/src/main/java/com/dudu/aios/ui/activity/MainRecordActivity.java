package com.dudu.aios.ui.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.fragment.FlowFragment;
import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.aios.ui.fragment.PhotoFragment;
import com.dudu.aios.ui.fragment.PhotoListFragment;
import com.dudu.aios.ui.fragment.RobberyFragment;
import com.dudu.aios.ui.fragment.SafetyFragment;
import com.dudu.aios.ui.fragment.SafetyMainFragment;
import com.dudu.aios.ui.fragment.VideoFragment;
import com.dudu.aios.ui.fragment.VideoListFragment;
import com.dudu.aios.ui.fragment.video.DrivingRecordFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.aios.ui.voice.VoiceFragment;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.TFlashCardReceiver;
import com.dudu.android.launcher.broadcast.WeatherAlarmReceiver;
import com.dudu.android.launcher.utils.AdminReceiver;
import com.dudu.event.DeviceEvent;
import com.dudu.init.InitManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wysaid.camera.CameraInstance;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MainRecordActivity extends BaseActivity {
    private static final int SET_PREVIEW = 0;

    private FragmentTransaction ft;

    private FragmentManager fm;

    private AlarmManager mAlarmManager;

    private TFlashCardReceiver mTFlashCardReceiver;

    private Logger log_init;

    private DevicePolicyManager mPolicyManager;

    private ComponentName componentName;

    private static final int MY_REQUEST_CODE = 9999;

    private MainFragment mainFragment;

    private SafetyMainFragment safetyFragment;

    private DrivingRecordFragment drivingRecordFragment;

    private PhotoFragment photoFragment;

    private PhotoListFragment photoListFragment;

    private VideoFragment videoFragment;

    private FlowFragment flowFragment;

    private VoiceFragment voiceFragment;

    private VideoListFragment videoListFragment;

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
        if (savedInstanceState == null) {
            mainFragment = new MainFragment();
            safetyFragment = new SafetyMainFragment();
            drivingRecordFragment = new DrivingRecordFragment();
            photoFragment = new PhotoFragment();
            photoListFragment = new PhotoListFragment();
            videoFragment = new VideoFragment();
            voiceFragment = new VoiceFragment();
            flowFragment = new FlowFragment();
            videoListFragment = new VideoListFragment();
        }

        fm = this.getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.container, new MainFragment());
        ft.commit();
    }

    public void replaceFragment(String name) {
        ft = fm.beginTransaction();
        //  ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);

        switch (name) {
            case FragmentConstants.FRAGMENT_MAIN_PAGE:

                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                ft.replace(R.id.container, mainFragment);

                break;

            case FragmentConstants.FRAGMENT_VEHICLE_INSPECTION:

                if (safetyFragment == null) {
                    safetyFragment = new SafetyMainFragment();
                }
                ft.replace(R.id.container, safetyFragment);
                break;

            case FragmentConstants.FRAGMENT_DRIVING_RECORD:
                DrivingRecordFragment drivingRecordFragment = new DrivingRecordFragment();
                drivingRecordFragment.setMainRecordActivity(this);
                ft.replace(R.id.container, drivingRecordFragment);
                break;

            case FragmentConstants.FRAGMENT_VIDEO_LIST:
                if(videoListFragment==null){
                    videoListFragment = new VideoListFragment();
                }
                ft.replace(R.id.container, videoListFragment);
                break;

            case FragmentConstants.FRAGMENT_VIDEO:
                ft.replace(R.id.container, videoFragment);
                break;

            case FragmentConstants.FRAGMENT_PHOTO_LIST:
                ft.replace(R.id.container, photoListFragment);
                break;

            case FragmentConstants.FRAGMENT_PHOTO:
                ft.replace(R.id.container, photoFragment);
                break;

            case FragmentConstants.FRAGMENT_FLOW:
                ft.replace(R.id.container, flowFragment);
                break;
            case FragmentConstants.VOICE_FRAGMENT:
                ft.replace(R.id.container, voiceFragment);
                break;
            default:
                ft.replace(R.id.container, mainFragment);
                break;

        }
        ft.commit();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        log_init.debug("MainRecordActivity 调用onDestroy释放资源...");

        InitManager.getInstance().unInit();

        cancelWeatherAlarm();

        unregisterReceiver(mTFlashCardReceiver);
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
        replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
        observableFactory.getCommonObservable(baseBinding).hasTitle.set(true);

        cameraView.onResume();
//        cameraView.resumePreview();
    }


    @Override
    protected void onPause() {
        super.onPause();
        CameraInstance.getInstance().stopCamera();
        cameraView.release(null);
        cameraView.onPause();

//        cameraView.stopPreview();
    }


}