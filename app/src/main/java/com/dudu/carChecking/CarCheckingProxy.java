package com.dudu.carChecking;

import android.content.Intent;

import com.dudu.aios.ui.activity.CarCheckingActivity;
import com.dudu.aios.ui.activity.VehicleAnimationActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.commonlib.CommonLib;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.TTSType;

import rx.Subscription;

/**
 * Created by lxh on 2016/2/21.
 */
public class CarCheckingProxy {

    private static CarCheckingProxy carCheckingProxy;

    private Subscription carCheckingErrorSub = null;

    public static CarCheckingProxy getInstance() {

        if (carCheckingProxy == null) {
            carCheckingProxy = new CarCheckingProxy();
        }
        return carCheckingProxy;
    }


    public void startCarChecking() {


        if (!(ActivitiesManager.getInstance().getTopActivity() instanceof CarCheckingActivity)) {
            Intent intent = new Intent(CommonLib.getInstance().getContext(), CarCheckingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommonLib.getInstance().getContext().startActivity(intent);
        }

        getCarCheckingError();

    }


    private void getCarCheckingError() {
    }


    public void showCheckingError(CarCheckType type) {

        String playText = "";

        Intent intent = new Intent(CommonLib.getInstance().getContext(), VehicleAnimationActivity.class);

        switch (type) {

            case SRS:
                playText = "乘客座椅安全带传感器故障,";
                intent.putExtra("vehicle", "srs");
                break;
            case ABS:
                playText = "回油泵电路发生故障,";
                intent.putExtra("vehicle", "abs");
                break;
            case TCM:
                playText = "来自发动机控制模块/动力传动控制模块的数据无效,";
                intent.putExtra("vehicle", "tcm");
                break;
            case ECM:
                playText = "质量或体积空气流量传感器B电路发生故障,";
                intent.putExtra("vehicle", "ecm");
                break;
        }
        playText = "检测到您的车辆"+playText + "已经为您找到以下汽车修理店，选择第几个前往修理或退出";
        CommonLib.getInstance().getContext().startActivity(intent);
        VoiceManagerProxy.getInstance().startSpeaking(playText, TTSType.TTS_START_UNDERSTANDING, false);
    }

    public void stopCarChecking() {
        if (carCheckingErrorSub != null && !carCheckingErrorSub.isUnsubscribed()) {
            carCheckingErrorSub.unsubscribe();
        }
    }


}
