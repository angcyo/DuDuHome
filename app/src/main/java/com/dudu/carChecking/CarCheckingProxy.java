package com.dudu.carChecking;

import android.content.Intent;

import com.dudu.aios.ui.activity.CarCheckingActivity;
import com.dudu.aios.ui.activity.VehicleAnimationActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.commonlib.CommonLib;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.obd.CarCheckFlow;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by lxh on 2016/2/21.
 */
public class CarCheckingProxy {

    private static CarCheckingProxy carCheckingProxy;

    private List<Subscription> subList;

    private String lastFault = "";

    public CarCheckingProxy() {

        subList = new ArrayList<>();

    }


    public static CarCheckingProxy getInstance() {

        if (carCheckingProxy == null) {
            carCheckingProxy = new CarCheckingProxy();
        }
        return carCheckingProxy;
    }


    public void startCarChecking() {

        try {
            CarCheckFlow.startCarCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCarCheckingError() {

        try {
            Subscription tcm = ObservableFactory.engineFailed().subscribe(s -> {

                if (!lastFault.equals(s)) {
                    showCheckingError(CarCheckType.TCM);
                }
                lastFault = s;
            });

            subList.add(tcm);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            Subscription abs = ObservableFactory.ABSFailed().subscribe(s -> {
                if (!lastFault.equals(s)) {
                    showCheckingError(CarCheckType.ABS);
                }
                lastFault = s;
            });

            subList.add(abs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Subscription ecm = ObservableFactory.gearboxFailed().subscribe(s -> {
                if (!lastFault.equals(s)) {
                    showCheckingError(CarCheckType.ECM);
                }
                lastFault = s;
            });
            subList.add(ecm);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Subscription srs = ObservableFactory.SRSFailed().subscribe(s -> {
                if (!lastFault.equals(s)) {
                    showCheckingError(CarCheckType.SRS);
                }
                lastFault = s;
            });
            subList.add(srs);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void clearFault() {

        try {
            CarCheckFlow.clearCarCheckError();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                playText = "质量或体积空气流量传感器B电路发生故障,";
                intent.putExtra("vehicle", "tcm");
                break;
            case ECM:
                playText = "来自发动机控制模块/动力传动控制模块的数据无效,";
                intent.putExtra("vehicle", "ecm");

                break;
        }
        playText = "检测到您的车辆" + playText + "已经为您找到以下汽车修理店，选择第几个前往修理或退出";
        CommonLib.getInstance().getContext().startActivity(intent);
        VoiceManagerProxy.getInstance().startSpeaking(playText, TTSType.TTS_START_UNDERSTANDING, false);
    }

    public void checkend() {
        for (Subscription sub : subList) {
            if (!sub.isUnsubscribed()) sub.unsubscribe();
        }
        subList.clear();
    }


}
