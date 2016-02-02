package com.dudu.android.launcher.ui.activity.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;

public class BtCallingActivity extends BaseTitlebarActivity implements OnClickListener {

    private Button mTerminateButton;

    @Override
    public int initContentView() {
        return R.layout.activity_blue_tooth_calling;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTerminateButton = (Button) findViewById(R.id.calling_terminate_button);
    }

    @Override
    public void initListener() {
        mTerminateButton.setOnClickListener(this);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calling_terminate_button:
                Intent intent = new Intent("wld.btphone.bluetooth.CALL_TERMINATION");
                sendBroadcast(intent);
                finish();
                break;
        }
    }
}
