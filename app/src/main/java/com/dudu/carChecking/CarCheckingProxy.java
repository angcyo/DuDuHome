package com.dudu.carChecking;

import android.content.Intent;

import com.dudu.aios.ui.activity.VehicleAnimationActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
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
import rx.schedulers.Schedulers;

import static com.dudu.carChecking.CarCheckType.WSB;

/**
 * Created by lxh on 2016/2/21.
 */
public class CarCheckingProxy {

    private static CarCheckingProxy carCheckingProxy;

    private boolean isABSbroadcasted, isWSBbroadcasted, isTCMbroadcasted, isECMbroadcasted, isSRSbroadcasted;

    private boolean isClearedFault;

    private Logger log;

    private List<Subscription> registerSubList;
    private List<CarCheckType> unregisterSubList;

    private CarCheckingProxy() {
    }

    public void init() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        registerSubList = new ArrayList<>();
        unregisterSubList = new ArrayList<>();
        unregisterSubList.add(CarCheckType.TCM);
        unregisterSubList.add(CarCheckType.SRS);
        unregisterSubList.add(CarCheckType.ECM);
        unregisterSubList.add(CarCheckType.ABS);
        unregisterSubList.add(WSB);

        log = LoggerFactory.getLogger("carChecking");
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
            log.error("carChecking error ", e);

        }
    }

    public void registerTCM() {

        log.debug("carChecking registerTCM");
        try {
            log.debug("carChecking engineFailed start");
            Subscription tcm = ObservableFactory.engineFailed()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(s -> {
                        log.debug("carChecking engineFailed got it");
                        if (!isTCMbroadcasted) {
                            isTCMbroadcasted = true;
                            showCheckingError(CarCheckType.TCM);
                        }

                    });
            log.debug("carChecking engineFailed end");
            registerSubList.add(tcm);

        } catch (Exception e) {
            log.error("carChecking error ", e);
        }

    }

    private void registerABS() {
        log.debug("carChecking ABSFailed start");
        try {


            Subscription abs = ObservableFactory.ABSFailed()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(s -> {
                        if (!isABSbroadcasted || isClearedFault) {
                            isABSbroadcasted = true;
                            isClearedFault = false;
                            showCheckingError(CarCheckType.ABS);
                        }
                    });
            log.debug("carChecking ABSFailed start");

            registerSubList.add(abs);
        } catch (Exception e) {
            log.error("carChecking error ", e);
        }
    }

    private void registerECM() {
        log.debug("carChecking ECMFailed start");

        try {
            Subscription ecm = ObservableFactory.gearboxFailed()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(s -> {
                        log.debug("carChecking ECMFailed got it");

                        if (!isECMbroadcasted || isClearedFault) {
                            isECMbroadcasted = true;
                            isClearedFault = false;

                            showCheckingError(CarCheckType.ECM);
                        }

                    });
            registerSubList.add(ecm);
        } catch (Exception e) {
            log.error("carChecking error ", e);
        }
    }

    private void registerSRS() {
        log.debug("carChecking SRSFailed start");
        try {
            Subscription srs = ObservableFactory.SRSFailed()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(s -> {

                        log.debug("carChecking SRSFailed got it");
                        if (!isSRSbroadcasted) {
                            isSRSbroadcasted = true;

                            showCheckingError(CarCheckType.SRS);
                        }
                    });
            registerSubList.add(srs);
        } catch (Exception e) {
            log.error("carChecking error ", e);
        }
    }

    public void onEventMainThread(ReceiverData receiverData) {
        if (ReceiverDataFlow.getRobberyReceiveData(receiverData)) {
            if (receiverData.getSwitch1Value().equals("1")) {
                if (!isWSBbroadcasted || isClearedFault) {
                    isWSBbroadcasted = true;
                    isClearedFault = false;
                    showCheckingError(WSB);
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

            regNext();
            VoiceManagerProxy.getInstance().startSpeaking("清除故障码成功", TTSType.TTS_DO_NOTHING, false);

            if (ActivitiesManager.getInstance().getTopActivity() instanceof VehicleAnimationActivity) {
                ActivitiesManager.getInstance().closeTargetActivity(VehicleAnimationActivity.class);
            }

        } catch (Exception e) {
            log.error("carChecking error ", e);
        }
    }

    private void regNext() {
        unregisterAll();

        if (unregisterSubList.size() > 0) {
            CarCheckType type = unregisterSubList.get(0);
            unregisterSubList.remove(0);
            log.debug("carChecking regNext type {}", type);
            switch (type) {
                case SRS:
                    registerSRS();
                    break;
                case TCM:
                    registerTCM();
                    break;
                case ECM:
                    registerECM();
                    break;
                case ABS:
                    registerABS();
                    break;
                case WSB:
                    break;
            }
        }
    }

    public void showCheckingError(CarCheckType type) {

        log.debug("carChecking showCheckingError {}", type);

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
            case ECM:
                playText = "质量或体积空气流量传感器B电路发生故障,";
                intent.putExtra("vehicle", "engine");
                break;
            case TCM:
                playText = "来自发动机控制模块或动力传动控制模块的数据无效,";
                intent.putExtra("vehicle", "gearbox");
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

    public void unregisterAll() {
        for (Subscription sub : registerSubList) {
            if (!sub.isUnsubscribed()) sub.unsubscribe();
        }
        registerSubList.clear();
    }

    public void registerCarCheckingError() {
        regNext();
    }
}
