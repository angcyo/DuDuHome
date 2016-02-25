package com.dudu.android.launcher.ui.activity.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;

/**
 * Created by Administrator on 2016/1/19.
 */
public class BtInCallActivity extends BaseActivity implements View.OnClickListener {

    private Button mAcceptButton, mDropButton;

    private ImageButton mBackButton;

    private TextView mCallerName, mCallerNumber;

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
            String iNumber = intent.getStringExtra(Constants.EXTRA_PHONE_NUMBER);
            String name = intent.getStringExtra(Constants.EXTRA_CONTACT_NAME);
            String number = getPhoneNumber(iNumber);
            LogUtils.v("phone", "name:" + name + " number:" + number);
            mCallerNumber.setText(number);
            mCallerName.setText(name);
        }
    }

    private String getPhoneNumber(String iNumber) {
        String number = iNumber.substring(0, 3) + " " + iNumber.substring(3, 7) + " " + iNumber.substring(7, 11);
        return number;
    }

    private void initListener() {
        mAcceptButton.setOnClickListener(this);
        mDropButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    private void initView() {
        mAcceptButton = (Button) findViewById(R.id.button_accept);
        mDropButton = (Button) findViewById(R.id.button_drop);
        mBackButton = (ImageButton) findViewById(R.id.button_back);
        mCallerName = (TextView) findViewById(R.id.caller_name);
        mCallerNumber = (TextView) findViewById(R.id.caller_number);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_blue_tooth_caller, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_accept:
                acceptPhone();
                break;
            case R.id.button_drop:
                rejectPhone();
                finish();
                break;
            case R.id.button_back:
                break;
        }
    }

    private void rejectPhone() {
        Intent intent = new Intent("wld.btphone.bluetooth.CALL_REJECT");
        sendBroadcast(intent);
    }

    private void acceptPhone() {
        Intent intent = new Intent("wld.btphone.bluetooth.CALL_ACCEPT");
        sendBroadcast(intent);
    }

}
