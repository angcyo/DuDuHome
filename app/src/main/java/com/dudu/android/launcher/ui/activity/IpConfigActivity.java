package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.ReceiverRegister;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.utils.IPConfig;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.network.NetworkManage;
import com.dudu.workflow.CommonParams;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lxh on 2016/1/2.
 */
public class IpConfigActivity extends BaseTitlebarActivity {

    private EditText editText_ip;

    private EditText editText_port;

    private EditText editText_Testip;

    private EditText editText_Testport;

    private EditText editText_UserName;

    private Button btn_save;

    private Button btn_reset;

    private Button btnBack;

    private RadioGroup radioGroup;
    private RadioButton radioBtnFormal, radioBtnTest;

    private IPConfig ipConfig;

    private boolean isTest = true;

    private String ip, testIP;

    private int port, testPort;

    @Override
    public int initContentView() {

        return R.layout.ip_congfig_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        ipConfig = IPConfig.getInstance(this);

        editText_ip = (EditText) findViewById(R.id.ip_edt);

        editText_port = (EditText) findViewById(R.id.port_edt);

        editText_Testip = (EditText) findViewById(R.id.test_ip_edt);

        editText_Testport = (EditText) findViewById(R.id.edt_testPort);

        editText_UserName = (EditText) findViewById(R.id.edt_username);

        btn_save = (Button) findViewById(R.id.btn_ip_save);

        btn_reset = (Button) findViewById(R.id.btn_ip_reset);

        radioGroup = (RadioGroup) findViewById(R.id.ip_radioGroup);

        radioBtnFormal = (RadioButton) findViewById(R.id.radioBtnFormal);

        radioBtnTest = (RadioButton) findViewById(R.id.radioBtnTest);

        btnBack = (Button)findViewById(R.id.back_button);

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



    @Override
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
                finish();
            }
        });

    }

    @Override
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
        CommonParams.getInstance().setUserName(userName);
        ReceiverRegister.registPushManager(userName);
        if (ipConfig.changeConfig(ip, testIP, port, testPort, isTest)) {
            ToastUtils.showToast("修改成功！");
            NetworkManage.getInstance().release();
            if (isTest) {
                ip = testIP;
                port = testPort;
            }
            Observable.timer(5, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    NetworkManage.getInstance().init(ip,port);
                }
            });
        }
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
}
