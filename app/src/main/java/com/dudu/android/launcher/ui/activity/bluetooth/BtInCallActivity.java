package com.dudu.android.launcher.ui.activity.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.SliderRelativeLayout;
import com.dudu.android.launcher.utils.Constant;

/**
 * Created by Administrator on 2016/1/19.
 */
public class BtInCallActivity extends BaseTitlebarActivity {

    private TextView mPhoneNumberView;

    private SliderRelativeLayout mSlider;

    @Override
    public int initContentView() {
        return R.layout.activity_blue_tooth_caller;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mPhoneNumberView = (TextView) findViewById(R.id.tv_phone_number);
        mSlider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
    }

    @Override
    public void initListener() {
        mSlider.setOnPhoneActionListener(new SliderRelativeLayout.OnPhoneActionListener() {
            @Override
            public void onAcceptPhone() {
                finish();
            }

            @Override
            public void onRejectPhone() {
                finish();
            }
        });
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumberView.setText(intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER));
        }
    }
}
