package com.dudu.workflow.switchmessage;

import com.dudu.persistence.switchmessage.SwitchMessage;
import com.dudu.persistence.switchmessage.SwitchMessageService;
import com.dudu.workflow.common.CommonParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/2/20.
 */
public class SwitchDataFlow {

    private static final String TAG = "SwitchDataFlow";

    private Logger logger = LoggerFactory.getLogger(TAG);

    private SwitchMessageService switchMessageService;

    public SwitchDataFlow(SwitchMessageService switchMessageService) {
        this.switchMessageService = switchMessageService;
    }

    public void init(){

    }

    public void saveRobberyState(boolean opened) {
        logger.debug("保存防劫状态为" + (opened ? "开启" : "关闭"));
        saveSwitchState(SwitchMessage.ROBBERY_STATE_KEY, opened);
    }

    public void saveRobberySwitch(int type, boolean opened) {
        logger.debug("保存防劫开关" + type + "状态为" + (opened ? "开启" : "关闭"));
        saveSwitchState(SwitchMessage.ROBBERY_SWITCH_KEY + type, opened);
    }

    public void saveGuardSwitch(boolean opened) {
        logger.debug("保存防盗开关状态为" + (opened ? "开启" : "关闭"));
        saveSwitchState(SwitchMessage.GUARD_SWITCH_KEY, opened);
    }

    public Observable<Boolean> getRobberyState() {
        return getSwitchState(SwitchMessage.ROBBERY_STATE_KEY);
    }

    public Observable<Boolean> getRobberySwitch(int type) {
        return getSwitchState(SwitchMessage.ROBBERY_SWITCH_KEY + type);
    }

    public Observable<Boolean> getGuardSwitch() {
        return getSwitchState(SwitchMessage.GUARD_SWITCH_KEY);
    }

    public Observable<RobberySwitches> getRobberySwitches() {
        return Observable.combineLatest(getRobberySwitch(CommonParams.HEADLIGHT), getRobberySwitch(CommonParams.PARK), getRobberySwitch(CommonParams.GUN), new Func3<Boolean, Boolean, Boolean, RobberySwitches>() {
            @Override
            public RobberySwitches call(Boolean headLightLocked, Boolean parkLocked, Boolean gunLocked) {
                RobberySwitches switchs = new RobberySwitches();
                switchs.setHeadlight(headLightLocked);
                switchs.setPark(parkLocked);
                switchs.setGun(gunLocked);
                logger.debug("headLightLocked: " + headLightLocked + "; parkLocked" + parkLocked + "; gunLocked: " + gunLocked);
                return switchs;
            }
        });
    }

    private void saveSwitchState(String key, boolean opened) {
        SwitchMessage switchMessage = new SwitchMessage(key, opened);
        switchMessageService.saveSwitch(switchMessage)
                .subscribeOn(Schedulers.newThread())
                .subscribe(message -> {
                    logger.debug(message.getSwitchKey() + "保存为" + message.isSwitchOpened() + "成功");
                });
    }

    private Observable<Boolean> getSwitchState(String key) {
        return switchMessageService.findSwitch(key)
                .map(message -> {
                    logger.debug(message.getSwitchKey() + "的值为：" + message.isSwitchOpened());
                    return message.isSwitchOpened();
                });
    }
}
