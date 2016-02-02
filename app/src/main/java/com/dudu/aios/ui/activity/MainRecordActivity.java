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
import android.view.Window;

import com.dudu.aios.ui.fragment.DrivingRecordFragment;
import com.dudu.aios.ui.fragment.FlowFragment;
import com.dudu.aios.ui.fragment.MainFragment;
import com.dudu.aios.ui.fragment.PhotoFragment;
import com.dudu.aios.ui.fragment.PhotoListFragment;
import com.dudu.aios.ui.fragment.VehicleFragment;
import com.dudu.aios.ui.fragment.VideoFragment;
import com.dudu.aios.ui.fragment.VideoListFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.TFlashCardReceiver;
import com.dudu.android.launcher.broadcast.WeatherAlarmReceiver;
import com.dudu.android.launcher.utils.AdminReceiver;
import com.dudu.event.DeviceEvent;
import com.dudu.init.InitManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MainRecordActivity extends Activity {

    private FragmentTransaction ft;

    private FragmentManager fm;

    private AlarmManager mAlarmManager;

    private TFlashCardReceiver mTFlashCardReceiver;

    private Logger log_init;

    private DevicePolicyManager mPolicyManager;

    private ComponentName componentName;

    private static final int MY_REQUEST_CODE = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        initData();
    }

    private void initData() {
        log_init = LoggerFactory.getLogger("init.start");

        log_init.debug("MainActivity 调用onCreate方法初始化...");

        EventBus.getDefault().unregister(this);

        EventBus.getDefault().register(this);

        initFragment();

        InitManager.getInstance().init();

        setWeatherAlarm();

        registerTFlashCardReceiver();

        // 获取设备管理服务
        mPolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        // 自己的AdminReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(this, AdminReceiver.class);
    }

    private void initFragment() {
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
                ft.replace(R.id.container, new MainFragment());
                break;

            case FragmentConstants.FRAGMENT_VEHICLE_INSPECTION:
                ft.replace(R.id.container, new VehicleFragment());
                break;

            case FragmentConstants.FRAGMENT_DRIVING_RECORD:
                ft.replace(R.id.container, new DrivingRecordFragment());
                break;

            case FragmentConstants.FRAGMENT_VIDEO_LIST:
                ft.replace(R.id.container, new VideoListFragment());
                break;

            case FragmentConstants.FRAGMENT_VIDEO:
                ft.replace(R.id.container, new VideoFragment());
                break;

            case FragmentConstants.FRAGMENT_PHOTO_LIST:
                ft.replace(R.id.container, new PhotoListFragment());
                break;

            case FragmentConstants.FRAGMENT_PHOTO:
                ft.replace(R.id.container, new PhotoFragment());
                break;

            case FragmentConstants.FRAGMENT_FLOW:
                ft.replace(R.id.container, new FlowFragment());
                break;

        }
        ft.commit();
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

        log_init.debug("MainActivity 调用onDestroy释放资源...");

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
}
