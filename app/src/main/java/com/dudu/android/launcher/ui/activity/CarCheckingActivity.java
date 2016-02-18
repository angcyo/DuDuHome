package com.dudu.android.launcher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.aios.ui.activity.VehicleAnimationActivity;
import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.view.VehicleCheckResultView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.carChecking.CarCheckingView;

public class CarCheckingActivity extends BaseActivity implements View.OnClickListener {

    private int[] icons = {R.drawable.vehicle_fine_bg, R.drawable.vehicle_problem_bg};

    private CarCheckingView mAnimationView;

    private VehicleCheckResultView engineVehicleCheckResultView, gearboxVehicleCheckResultView, absVehicleCheckResultView, wsbVehicleCheckResultView, rsrVehicleCheckResultView;

    private TextView tvEnginePrompt, tvGearboxPrompt, tvAbsPrompt, tvWsbPrompt, tvSrsPrompt;

    private ImageView iconEnginePrompt, iconGearboxPrompt, iconAbsPrompt, iconWsbPrompt, iconSrsPrompt;

    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initListener();

        initDatas();


    }

    public void initView() {
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

    public void initListener() {
        buttonBack.setOnClickListener(this);

        iconEnginePrompt.setOnClickListener(this);
        iconGearboxPrompt.setOnClickListener(this);
        iconAbsPrompt.setOnClickListener(this);
        iconWsbPrompt.setOnClickListener(this);
        iconSrsPrompt.setOnClickListener(this);

    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate( R.layout.activity_car_checking,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnimationView.stopAnim();
    }

    public void initDatas() {

        setVehicleCheckState(100, engineVehicleCheckResultView, tvEnginePrompt, iconEnginePrompt);

        setVehicleCheckState(20, gearboxVehicleCheckResultView, tvGearboxPrompt, iconGearboxPrompt);

        setVehicleCheckState(30, absVehicleCheckResultView, tvAbsPrompt, iconAbsPrompt);

        setVehicleCheckState(60, wsbVehicleCheckResultView, tvWsbPrompt, iconWsbPrompt);

        setVehicleCheckState(90, rsrVehicleCheckResultView, tvSrsPrompt, iconSrsPrompt);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, VehicleAnimationActivity.class);
        switch (v.getId()) {
            case R.id.button_back:
                mAnimationView.stopAnim();
                finish();
                return;
            case R.id.engine_prompt_icon:
                intent.putExtra("vehicle", "engine");
                intent.putExtra("state", "red");
                break;
            case R.id.gearbox_prompt_icon:
                intent.putExtra("vehicle", "gearbox");
                intent.putExtra("state", "red");
                break;
            case R.id.abs_prompt_icon:
                intent.putExtra("vehicle", "abs");
                intent.putExtra("state", "red");
                break;
            case R.id.wsb_prompt_icon:
                intent.putExtra("vehicle", "wsb");
                intent.putExtra("state", "red");
                break;
            case R.id.srs_prompt_icon:
                intent.putExtra("vehicle", "srs");
                intent.putExtra("state", "red");
                break;
        }
        if (intent != null) {
            mAnimationView.stopAnim();
            startActivity(intent);

        }

    }

    private void setVehicleCheckState(int grade, VehicleCheckResultView vehicleCheckResultView, TextView textView, ImageView imageView) {
        if (grade >= 50) {
            vehicleCheckResultView.setProgress(grade, 0);
            textView.setText(getString(R.string.device_fine));
            imageView.setImageResource(icons[0]);
            imageView.setEnabled(false);
            LogUtils.v("vehicle", "false");
        } else {
            vehicleCheckResultView.setProgress(grade, 1);
            textView.setText(getString(R.string.check_details));
            imageView.setImageResource(icons[1]);
            imageView.setEnabled(true);
        }
    }
}
