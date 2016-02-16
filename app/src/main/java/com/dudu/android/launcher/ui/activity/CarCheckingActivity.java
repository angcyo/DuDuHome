package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.aios.ui.view.VehicleCheckResultView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.carChecking.CarCheckingView;

/**
 * Created by Administrator on 2016/2/2.
 */
public class CarCheckingActivity extends BaseNoTitlebarAcitivity implements View.OnClickListener {

    private int[] icons = {R.drawable.vehicle_device_health, R.drawable.vehicle_suggest_check, R.drawable.vehicle_suggest_maintain};

    private CarCheckingView mAnimationView;

    private VehicleCheckResultView engineVehicleCheckResultView, gearboxVehicleCheckResultView, absVehicleCheckResultView, wsbVehicleCheckResultView, rsrVehicleCheckResultView;

    private TextView tvEnginePrompt, tvGearboxPrompt, tvAbsPrompt, tvWsbPrompt, tvSrsPrompt;

    private ImageView iconEnginePrompt, iconGearboxPrompt, iconAbsPrompt, iconWsbPrompt, iconSrsPrompt;

    private ImageButton buttonBack;

    @Override
    public int initContentView() {
        return R.layout.activity_car_checking;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mAnimationView = (CarCheckingView) findViewById(R.id.car_checking_view);
        engineVehicleCheckResultView = (VehicleCheckResultView) findViewById(R.id.engine_vehicleCheckResult);
        gearboxVehicleCheckResultView = (VehicleCheckResultView) findViewById(R.id.gearbox_vehicleCheckResult);
        absVehicleCheckResultView = (VehicleCheckResultView) findViewById(R.id.abs_vehicleCheckResult);
        wsbVehicleCheckResultView = (VehicleCheckResultView) findViewById(R.id.wsb_vehicleCheckResult);
        rsrVehicleCheckResultView = (VehicleCheckResultView) findViewById(R.id.srs_vehicleCheckResult);

        tvEnginePrompt = (TextView) findViewById(R.id.engine_prompt_text);
        tvGearboxPrompt = (TextView) findViewById(R.id.gearbox_prompt_text);
        tvAbsPrompt = (TextView) findViewById(R.id.abs_prompt_text);
        tvWsbPrompt = (TextView) findViewById(R.id.wsb_prompt_text);
        tvSrsPrompt = (TextView) findViewById(R.id.srs_prompt_text);

        iconEnginePrompt = (ImageView) findViewById(R.id.engine_prompt_icon);
        iconGearboxPrompt = (ImageView) findViewById(R.id.gearbox_prompt_icon);
        iconAbsPrompt = (ImageView) findViewById(R.id.abs_prompt_icon);
        iconWsbPrompt = (ImageView) findViewById(R.id.wsb_prompt_icon);
        iconSrsPrompt = (ImageView) findViewById(R.id.srs_prompt_icon);

        buttonBack = (ImageButton) findViewById(R.id.button_back);
    }

    @Override
    public void initListener() {
        buttonBack.setOnClickListener(this);

    }

    @Override
    public void initDatas() {
        engineVehicleCheckResultView.setProgress(100);
        setVehicleCheckState(100, tvEnginePrompt, iconEnginePrompt);

        gearboxVehicleCheckResultView.setProgress(20);
        setVehicleCheckState(20, tvGearboxPrompt, iconGearboxPrompt);

        absVehicleCheckResultView.setProgress(30);
        setVehicleCheckState(30, tvAbsPrompt, iconAbsPrompt);

        wsbVehicleCheckResultView.setProgress(60);
        setVehicleCheckState(60, tvWsbPrompt, iconWsbPrompt);

        rsrVehicleCheckResultView.setProgress(90);
        setVehicleCheckState(90, tvSrsPrompt, iconSrsPrompt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                mAnimationView.stopAnim();
                finish();
                break;
        }
    }

    private void setVehicleCheckState(int grade, TextView textView, ImageView imageView) {
        if (grade == 100) {
            textView.setText(getString(R.string.vehicle_device_health));
            imageView.setImageResource(icons[0]);
        } else if (grade >= 50) {
            textView.setText(getString(R.string.vehicle_suggest_check));
            imageView.setImageResource(icons[1]);
        } else {
            textView.setText(getString(R.string.vehicle_suggest_maintain));
            imageView.setImageResource(icons[2]);
        }
    }
}
