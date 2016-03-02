package com.dudu.android.launcher.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.utils.InstallerUtils;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.ReceiverRegister;
import com.dudu.android.launcher.ui.dialog.IPConfigDialog;
import com.dudu.android.launcher.utils.IPConfig;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.init.InitManager;
import com.dudu.navi.event.NaviEvent;
import com.dudu.network.NetworkManage;
import com.dudu.obd.ObdInit;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.workflow.common.CommonParams;
import com.dudu.workflow.common.DataFlowFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lxh on 2016/1/2.
 */
public class IpConfigActivity extends BaseActivity {

    private EditText editText_ip;

    private EditText editText_port;

    private EditText editText_Testip;

    private EditText editText_Testport;

    private EditText editText_UserName;

    private Button btn_save;

    private Button btn_reset;

    private Button btnBack;

    private Button btnOpenGsp;

    private Button btnOpenMap;

    private RadioGroup radioGroup;
    private RadioButton radioBtnFormal, radioBtnTest;

    private IPConfig ipConfig;

    private boolean isTest = true;

    private String ip, testIP;

    private int port, testPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        initListener();
        initDatas();
    }

    public void initView(Bundle savedInstanceState) {
        ipConfig = IPConfig.getInstance(this);

        editText_ip = (EditText) findViewById(R.id.ip_edt);

        editText_port = (EditText) findViewById(R.id.port_edt);

        editText_Testip = (EditText) findViewById(R.id.test_ip_edt);

        editText_Testport = (EditText) findViewById(R.id.edt_testPort);

        editText_UserName = (EditText) findViewById(R.id.edt_username);

        btn_save = (Button) findViewById(R.id.btn_ip_save);

        btn_reset = (Button) findViewById(R.id.btn_ip_reset);

        btnOpenGsp = (Button) findViewById(R.id.openGps);

        btnOpenMap = (Button) findViewById(R.id.openRMap);

        radioGroup = (RadioGroup) findViewById(R.id.ip_radioGroup);

        radioBtnFormal = (RadioButton) findViewById(R.id.radioBtnFormal);

        radioBtnTest = (RadioButton) findViewById(R.id.radioBtnTest);

        btnBack = (Button) findViewById(R.id.back_button);

        isTest = ipConfig.isTest_Server();
        if (isTest) {
            radioBtnTest.setChecked(true);
        } else {
            radioBtnFormal.setChecked(true);
        }

        editText_ip.setText(ipConfig.getServerIP());
        editText_Testip.setText(ipConfig.getTestServerIP());
        editText_port.setText(ipConfig.getServerPort() + "");
        editText_Testport.setText(ipConfig.getTestServerPort() + "");
        editText_UserName.setText(CommonParams.getInstance().getUserName() + "");
    }


    public void initListener() {

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIp();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioBtnFormal) {
                    isTest = false;
                } else if (checkedId == R.id.radioBtnTest) {
                    isTest = true;
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IpConfigActivity.this, MainRecordActivity.class));
                finish();
            }
        });

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstallerUtils.openApp(IpConfigActivity.this, "org.gyh.rmaps");
                EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
            }
        });

        btnOpenGsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstallerUtils.openApp(IpConfigActivity.this, "com.chartcross.gpstestplus");
                EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
            }
        });

    }

    public void initDatas() {
    }


    private void changeIp() {

        if (TextUtils.isEmpty(editText_ip.getText().toString())
                || TextUtils.isEmpty(editText_port.getText().toString())
                || TextUtils.isEmpty(editText_Testip.getText().toString())
                || TextUtils.isEmpty(editText_Testport.getText().toString())) {
            return;
        }

        ip = editText_ip.getText().toString();
        port = Integer.parseInt(editText_port.getText().toString());
        testIP = editText_Testip.getText().toString();
        testPort = Integer.parseInt(editText_Testport.getText().toString());
        String userName = editText_UserName.getText().toString();

        DataFlowFactory.getUserDataFlow().saveUserName(userName);
        ReceiverRegister.registPushManager(userName);
        if (ipConfig.changeConfig(ip, testIP, port, testPort, isTest)) {
            //修改成功

            NetworkManage.getInstance().release();
            if (isTest) {
                ip = testIP;
                port = testPort;
            }
            Observable.timer(5, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    NetworkManage.getInstance().init(ip, port);
                }
            });
        }
        startActivity(new Intent(IpConfigActivity.this, MainRecordActivity.class));
        finish();
    }


    private void reset() {
        editText_ip.setText(ipConfig.getServerIP());
        editText_port.setText(ipConfig.getServerPort() + "");
        editText_Testip.setText(ipConfig.getTestServerIP());
        editText_Testport.setText(ipConfig.getTestServerPort() + "");
        if (isTest) {
            radioBtnTest.setChecked(true);
        } else {
            radioBtnFormal.setChecked(true);
        }
    }

    public void enterSetting(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
        startActivity(intent);
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
    }

    public void startFactory(View view) {

        if (!InitManager.getInstance().isFinished()) {
            return;
        }

        //关闭语音
        VoiceManagerProxy.getInstance().stopUnderstanding();

        //关闭Portal
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(this, "persist.sys.nodog", "stop");

        //关闭热点
        WifiApAdmin.closeWifiAp(this);

        //stop bluetooth
        ObdInit.uninitOBD(this);

        PackageManager packageManager = getPackageManager();
        startActivity(new Intent(packageManager.getLaunchIntentForPackage("com.qualcomm.factory")));
        if (Utils.isDemoVersion(this)) {
            EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
        }
    }

    public void setOBD2Simulator(View view) {
        new IPConfigDialog().showDialog(this);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.ip_congfig_layout, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.HIDE);
    }
}
