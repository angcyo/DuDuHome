package com.dudu.android.launcher.ui.activity.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.utils.Constant;

public class BtOutCallActivity extends BaseTitlebarActivity implements View.OnClickListener {

    private Button mTerminateButton;

    private TextView mContactNameView;

    @Override
    public int initContentView() {
        return R.layout.activity_blue_tooth_dialing;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTerminateButton = (Button) findViewById(R.id.dialing_terminate_button);
        mContactNameView = (TextView) findViewById(R.id.dialing_name_textView);
    }

    @Override
    public void initListener() {
        mTerminateButton.setOnClickListener(this);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra(Constant.EXTRA_CONTACT_NAME);
            if (!TextUtils.isEmpty(name)) {
                mContactNameView.setText(name);
            } else {
                String number = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
                mContactNameView.setText(number);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialing_terminate_button:
                Intent intent = new Intent("wld.btphone.bluetooth.CALL_TERMINATION");
                sendBroadcast(intent);
                finish();
                break;
        }
    }
}
