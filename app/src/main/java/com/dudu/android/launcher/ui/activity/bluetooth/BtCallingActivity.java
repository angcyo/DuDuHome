package com.dudu.android.launcher.ui.activity.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;

public class BtCallingActivity extends BaseActivity implements OnClickListener {

    private Button mTerminateButton;

    private ImageButton mBackButton;

    private ImageButton mContactsButton;

    private TextView mNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String number = intent.getStringExtra("number");
            if (number != null) {
                mNumberText.setText(number);
            }
        }
    }

    private void initView() {
        mTerminateButton = (Button) findViewById(R.id.calling_terminate_button);
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mNumberText = (TextView) findViewById(R.id.caller_name);
        mContactsButton = (ImageButton) findViewById(R.id.button_contacts);
    }


    private void initListener() {
        mTerminateButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mContactsButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calling_terminate_button:
                Intent intent = new Intent("wld.btphone.bluetooth.CALL_TERMINATION");
                sendBroadcast(intent);
                finish();
                break;
            case R.id.button_back:
                finish();
                break;
            case R.id.button_contacts:
                startActivity(new Intent(this, BtContactsActivity.class));
                break;
        }
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_blue_tooth_calling, null);
    }
}
