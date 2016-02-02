package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.carChecking.CarCheckingView;

/**
 * Created by Administrator on 2016/2/2.
 */
public class CarCheckingActivity extends BaseNoTitlebarAcitivity {

    private CarCheckingView mAnimationView;

    @Override
    public int initContentView() {
        return R.layout.activity_car_checking;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mAnimationView = (CarCheckingView) findViewById(R.id.car_checking_view);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initDatas() {

    }
}
