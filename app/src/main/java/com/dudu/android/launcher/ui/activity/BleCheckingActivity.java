package com.dudu.android.launcher.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.event.BleStateChange;
import com.dudu.event.InitEvent;
import com.dudu.obd.OBDDataService;
import com.dudu.voice.semantic.VoiceManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import ch.qos.logback.core.android.SystemPropertiesProxy;
import de.greenrobot.event.EventBus;

public class BleCheckingActivity extends BaseTitlebarActivity {

    private ProgressBar mCheckingProgressBar;

    private TextView mCheckingTextView;

    @Override
    public int initContentView() {
        return R.layout.activity_ble_checking;
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        mCheckingProgressBar = (ProgressBar) findViewById(R.id.obd_checking_pb);
        mCheckingTextView = (TextView) findViewById(R.id.obd_checking_tv);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);

        if (Utils.isDemoVersion(BleCheckingActivity.this)) {

            initAfterBTFT();

            EventBus.getDefault().post(new BleStateChange(BleStateChange.BLECONNECTED));
            return;
        }

        checkBTFT();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initAfterBTFT() {

        // 1.初始化语音
        initMSC();

        // 2.打开蓝牙
        openBlueTooth();
    }

    private void checkBTFT() {
        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        boolean need_bt = !"1".equals(sps.get("persist.sys.bt", "0"));
        boolean need_ft = !"1".equals(sps.get("persist.sys.ft", "0"));
        Intent intent;
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.qualcomm.factory");
        if ((need_bt || need_ft) && intent != null) {
            //close wifi ap for ft test
            WifiApAdmin.closeWifiAp(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // 关闭ADB调试端口
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext,
                    "persist.sys.usb.config", "charging");

            initAfterBTFT();

            startOBDService();
        }
    }

    public void onEventMainThread(BleStateChange event) {
        LogUtils.e("BleCheckingActivity", event.toString());
        switch (event.getConnState()) {
            case BleStateChange.BLEDISCONNECTED:
                mCheckingProgressBar.setVisibility(View.GONE);
                mCheckingTextView.setText(R.string.obd_checking_unconnected);
                break;
            case BleStateChange.BLECONNECTED:
                mCheckingProgressBar.setVisibility(View.GONE);
                startActivity(new Intent(BleCheckingActivity.this,
                        MainActivity.class));
                finish();
                break;
        }
    }

    private void initMSC() {
        // 初始化语音模块
        StringBuffer param = new StringBuffer();
        param.append("appid=" + Constants.XUFEIID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);

        SpeechUtility.createUtility(this, param.toString());

        VoiceManager.getInstance().startWakeup();
    }

    private void openBlueTooth() {
        //初始化蓝牙的适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                //如果蓝牙没有开启的话，则开启蓝牙
                bluetoothAdapter.enable();
            }
        }
    }

    private void startOBDService() {
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(BleCheckingActivity.this,
                "persist.sys.gps", "start");
        Intent i = new Intent(this, OBDDataService.class);
        startService(i);
    }

}
