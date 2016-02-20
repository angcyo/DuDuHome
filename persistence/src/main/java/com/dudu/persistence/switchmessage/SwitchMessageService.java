package com.dudu.persistence.switchmessage;

import rx.Observable;

/**
 * Created by Administrator on 2016/2/20.
 */
public interface SwitchMessageService {

    public Observable<SwitchMessage> findSwitch(String Key);

    public Observable<SwitchMessage> saveSwitch(SwitchMessage message);
}
