package com.dudu.aios.ui.activity;


import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.carChecking.VehicleCheckResultAnimation;

public class VehicleAnimationActivity extends BaseNoTitlebarAcitivity implements View.OnClickListener {

    private RelativeLayout container;

    private ImageButton buttonBack;

    private VehicleCheckResultAnimation vehicleCheckResultAnimation;

    @Override
    public int initContentView() {
        return R.layout.activity_vehicle_animation;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        container = (RelativeLayout) findViewById(R.id.vehicle_anim_container);
        buttonBack = (ImageButton) findViewById(R.id.button_back);
    }

    @Override
    public void initListener() {
        buttonBack.setOnClickListener(this);

    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            String path = intent.getStringExtra("vehicle");
            String state = intent.getStringExtra("state");
            vehicleCheckResultAnimation = new VehicleCheckResultAnimation(this, path, state);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container.addView(vehicleCheckResultAnimation, params);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                vehicleCheckResultAnimation.stopAnim();
                finish();
                break;
        }

    }
}
