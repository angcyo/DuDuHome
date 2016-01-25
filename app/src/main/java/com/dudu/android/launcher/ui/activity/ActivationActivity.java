package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.dialog.WifiPasswordSetDialog;
import com.dudu.android.launcher.ui.view.SlideSwitch;
import com.dudu.android.launcher.ui.view.SlideSwitch.OnSwitchChangedListener;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.WifiApAdmin;

public class ActivationActivity extends BaseTitlebarActivity {

    private SlideSwitch slideSwitch;

    private TextView wifiPasswordSet;

    private WifiPasswordSetDialog passwordSetDialog;

    private Button backButton;

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public int initContentView() {
        return R.layout.activation_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        slideSwitch = (SlideSwitch) findViewById(R.id.switch_wifi);
        wifiPasswordSet = (TextView) findViewById(R.id.wifiPasswordSet);
        backButton = (Button) findViewById(R.id.back_button);
    }

    @Override
    public void initListener() {
        slideSwitch.setOnSwitchChangedListener(new OnSwitchChangedListener() {

            @Override
            public void onSwitchChanged(SlideSwitch obj, int status) {
                toggleWifiAp();
            }
        });

        wifiPasswordSet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showWifiSettingDialog();
            }
        });
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivationActivity.this.finish();
            }
        });
    }

    @Override
    public void initDatas() {
        slideSwitch.setStatus(WifiApAdmin.isWifiApEnabled(ActivationActivity.this));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private void toggleWifiAp() {
        if (WifiApAdmin.isWifiApEnabled(ActivationActivity.this)) {
            WifiApAdmin.closeWifiAp(ActivationActivity.this);
        } else {
            WifiApAdmin.startWifiAp(ActivationActivity.this);
        }
    }

    private void showWifiSettingDialog() {
        if (passwordSetDialog == null) {
            passwordSetDialog = WifiPasswordSetDialog.createDialog(
                    ActivationActivity.this, mWifiStateCallBack);
            passwordSetDialog.setCanceledOnTouchOutside(true);
            Window dialogWindow = passwordSetDialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
        }

        passwordSetDialog.show();
    }

    WifiApAdmin.WifiSettingStateCallback mWifiStateCallBack = new WifiApAdmin.WifiSettingStateCallback() {

        @Override
        public void onWifiStateChanged(boolean open) {
            if (open) {
                slideSwitch.setStatus(open);
                slideSwitch.invalidate();
            }
        }
    };
}
