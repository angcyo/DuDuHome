package com.dudu.aios.ui.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.aios.ui.view.VehicleCheckResultView;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.carChecking.CarCheckingProxy;
import com.dudu.carChecking.CarCheckingView;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.SceneType;
import com.dudu.voice.semantic.engine.SemanticEngine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CarCheckingActivity extends BaseActivity implements View.OnClickListener {

    private int[] icons = {R.drawable.vehicle_fine_bg, R.drawable.vehicle_problem_bg};

    private CarCheckingView mAnimationView;

    private VehicleCheckResultView engineVehicleCheckResultView, gearboxVehicleCheckResultView, absVehicleCheckResultView, wsbVehicleCheckResultView, rsrVehicleCheckResultView;

    private TextView tvEnginePrompt, tvGearboxPrompt, tvAbsPrompt, tvWsbPrompt, tvSrsPrompt;

    private ImageView iconEnginePrompt, iconGearboxPrompt, iconAbsPrompt, iconWsbPrompt, iconSrsPrompt;

    private ImageButton buttonBack;

    private ImageView buttonCarChecking;

    private RelativeLayout animContainer;

    private LinearLayout engineContainer, gearboxContainer, absContainer, wsbContainer, srsContainer;

    private TextView mDataText;

    private ProgressThread progressThread;

    private Handler handler = new ProgressHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initListener();

        initDatas();

        SemanticEngine.getProcessor().switchSemanticType(SceneType.CAR_CHECKING);
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_car_checking, null);
    }

    public void initView() {
        mDataText = (TextView) findViewById(R.id.text_date);

        animContainer = (RelativeLayout) findViewById(R.id.anim_container);
        mAnimationView = new CarCheckingView(this, "suv", 3);
        mAnimationView.setZOrderOnTop(true);
        mAnimationView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        animContainer.addView(mAnimationView);
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

        engineContainer = (LinearLayout) findViewById(R.id.engine_container);
        gearboxContainer = (LinearLayout) findViewById(R.id.gearbox_container);
        absContainer = (LinearLayout) findViewById(R.id.abs_container);
        wsbContainer = (LinearLayout) findViewById(R.id.wsb_container);
        srsContainer = (LinearLayout) findViewById(R.id.srs_container);

        buttonCarChecking = (ImageButton) findViewById(R.id.button_vehicle_clear);

        buttonBack = (ImageButton) findViewById(R.id.button_back);
    }

    public void initListener() {
        buttonBack.setOnClickListener(this);

        engineContainer.setOnClickListener(this);
        gearboxContainer.setOnClickListener(this);
        absContainer.setOnClickListener(this);
        wsbContainer.setOnClickListener(this);
        srsContainer.setOnClickListener(this);

        buttonCarChecking.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnimationView.stopAnim();
        VoiceManagerProxy.getInstance().onStop();
    }

    public void initDatas() {

        // setVehicleCheckState(30, engineVehicleCheckResultView, tvEnginePrompt, iconEnginePrompt, engineContainer);
        progressThread = new ProgressThread(30);
        progressThread.start();
        // vehicleCheckResultView.setProgress(grade, 1);
        tvEnginePrompt.setText(getString(R.string.check_details));
        iconEnginePrompt.setImageResource(icons[1]);
        engineContainer.setEnabled(true);

        setVehicleCheckState(80, gearboxVehicleCheckResultView, tvGearboxPrompt, iconGearboxPrompt, gearboxContainer);

        setVehicleCheckState(70, absVehicleCheckResultView, tvAbsPrompt, iconAbsPrompt, absContainer);

        setVehicleCheckState(60, wsbVehicleCheckResultView, tvWsbPrompt, iconWsbPrompt, wsbContainer);

        setVehicleCheckState(100, rsrVehicleCheckResultView, tvSrsPrompt, iconSrsPrompt, srsContainer);

        obtainDate();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, VehicleAnimationActivity.class);
        switch (v.getId()) {
            case R.id.button_back:
//                buttonBack.setEnabled(false);
                mAnimationView.stopAnim();
                finish();
                return;
            case R.id.engine_container:
                intent.putExtra("vehicle", "engine");
                break;
            case R.id.gearbox_container:
                intent.putExtra("vehicle", "gearbox");
                break;
            case R.id.abs_container:
                intent.putExtra("vehicle", "abs");
                break;
            case R.id.wsb_container:
                intent.putExtra("vehicle", "wsb");
                break;
            case R.id.srs_container:
                intent.putExtra("vehicle", "srs");
                break;
            case R.id.button_vehicle_clear:
                intent = null;
                CarCheckingProxy.getInstance().clearFault();
                break;
        }
        if (intent != null) {
            mAnimationView.stopAnim();
            startActivityForResult(intent, 2);// 请求码
        }

    }

    private void setVehicleCheckState(int grade, VehicleCheckResultView vehicleCheckResultView, TextView textView, ImageView imageView, View view) {
        if (grade >= 50) {
            vehicleCheckResultView.setProgress(grade, 0);
            textView.setText(getString(R.string.device_fine));
            imageView.setImageResource(icons[0]);
            view.setEnabled(false);
        } else {
            vehicleCheckResultView.setProgress(grade, 1);
            textView.setText(getString(R.string.check_details));
            imageView.setImageResource(icons[1]);
            view.setEnabled(true);
        }
    }

    @Override
    /**
     * 当跳转的activity(被激活的activity)使用完毕,销毁的时候调用该方法
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            mAnimationView.setIsAppear(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SemanticEngine.getProcessor().switchSemanticType(SceneType.HOME);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SemanticEngine.getProcessor().switchSemanticType(SceneType.CAR_CHECKING);
    }

    class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            engineVehicleCheckResultView.setProgress(msg.arg1, 1);
        }
    }

    class ProgressThread extends Thread {

        private int progress;

        public ProgressThread(int progress) {
            this.progress = progress;
        }

        @Override
        public void run() {
            int index = 0;
            while (index <= progress) {
                index++;
                Message message = Message.obtain();
                message.arg1 = index;
                handler.sendMessageDelayed(message, 2000);
                LogUtils.v("view", "index:" + index);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void obtainDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());//获取当前时间
        mDataText.setText(sdf.format(date));
    }

}
