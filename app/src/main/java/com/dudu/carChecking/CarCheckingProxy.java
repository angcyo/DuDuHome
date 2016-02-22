package com.dudu.carChecking;

import android.content.Intent;

import com.dudu.aios.ui.activity.VehicleAnimationActivity;
import com.dudu.commonlib.CommonLib;
import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.workflow.common.ObservableFactory;
import com.dudu.workflow.common.ReceiverDataFlow;
import com.dudu.workflow.obd.CarCheckFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Subscription;

/**
 * Created by lxh on 2016/2/21.
 */
public class CarCheckingProxy {

    private static CarCheckingProxy carCheckingProxy;

    private List<Subscription> subList;

    private boolean isABSbroadcasted, isWSBbroadcasted, isTCMbroadcasted, isECMbroadcasted, isSRSbroadcasted;

    private boolean isClearedFault;

    private Logger log;

    public CarCheckingProxy() {

        subList = new ArrayList<>();
<<<<<<< HEAD
        EventBus.getDefault().register(this);
=======
        log = LoggerFactory.getLogger("carChecking");

>>>>>>> 34ab05f... add carChecking log
    }

    public static CarCheckingProxy getInstance() {

        if (carCheckingProxy == null) {
            carCheckingProxy = new CarCheckingProxy();
        }
        return carCheckingProxy;
    }


    public void startCarChecking() {

        log.debug("carChecking startCarChecking");

        try {
            CarCheckFlow.startCarCheck();
        } catch (Exception e) {
            log.error("carChecking error ",e);

        }
    }

    public void registerCarCheckingError() {

        log.debug("carChecking registerCarCheckingError");
        try {
            Subscription tcm = ObservableFactory.engineFailed().subscribe(s -> {
                if (!isTCMbroadcasted || isClearedFault) {
                    isTCMbroadcasted = true;
                    isClearedFault = false;
                    showCheckingError(CarCheckType.TCM);
                }

            });
            subList.add(tcm);

        } catch (Exception e) {
           log.error("carChecking error ",e);
        }

        try {

            Subscription abs = ObservableFactory.ABSFailed().subscribe(s -> {
                if (!isABSbroadcasted || isClearedFault) {
                    isABSbroadcasted = true;
                    isClearedFault = false;
                    showCheckingError(CarCheckType.ABS);
                }
            });

            subList.add(abs);
        } catch (Exception e) {
            log.error("carChecking error ",e);
        }


        try {
            Subscription ecm = ObservableFactory.gearboxFailed().subscribe(s -> {

                if (!isECMbroadcasted || isClearedFault) {
                    isECMbroadcasted = true;
                    isClearedFault = false;

                    showCheckingError(CarCheckType.ECM);

                }

            });
            subList.add(ecm);
        } catch (Exception e) {
            log.error("carChecking error ",e);

        }


        try {
            Subscription srs = ObservableFactory.SRSFailed().subscribe(s -> {
                if (!isSRSbroadcasted || isClearedFault) {
                    isSRSbroadcasted = true;
                    isClearedFault = false;

                    showCheckingError(CarCheckType.SRS);
                }
            });
            subList.add(srs);
        } catch (Exception e) {
            log.error("carChecking error ", e);

        }

    }

    public void onEventMainThread(ReceiverData receiverData) {
        if(ReceiverDataFlow.getRobberyReceiveData(receiverData)){
            if(receiverData.getSwitch1Value().equals("1")){
                 if (!isWSBbroadcasted || isClearedFault) {
                            isWSBbroadcasted = true;
                            isClearedFault = false;
                            showCheckingError(CarCheckType.WSB);
                        }
            }
            ReceiverDataFlow.saveRobberyReceiveData(receiverData);
        }
    }


    public void clearFault() {

        log.debug("carChecking clearFault");

        try {
            isClearedFault = true;
            CarCheckFlow.clearCarCheckError();
        } catch (Exception e) {
            log.error("carChecking error ",e);
        }

    }

    public void showCheckingError(CarCheckType type) {

        log.debug("carChecking showCheckingError {}",type);

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
                intent.putExtra("vehicle", "gearbox");
                break;
            case ECM:
                playText = "来自发动机控制模块或动力传动控制模块的数据无效,";
                intent.putExtra("vehicle", "engine");
                break;
            case WSB:
                playText = "左前轮胎压异常,";
                intent.putExtra("vehicle", "wsb");
                break;

        }
        playText = "检测到您的车辆" + playText + "已经为您找到以下汽车修理店，选择第几个前往修理或退出";
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
