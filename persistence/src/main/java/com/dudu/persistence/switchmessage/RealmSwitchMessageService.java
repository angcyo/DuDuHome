package com.dudu.persistence.switchmessage;

import com.dudu.persistence.rx.RealmObservable;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/2/20.
 */
public class RealmSwitchMessageService implements SwitchMessageService {
    @Override
    public Observable<SwitchMessage> findSwitch(final String key) {
        return RealmObservable.object(new Func1<Realm, RealmSwitchMessage>() {
            @Override
            public RealmSwitchMessage call(Realm realm) {
                RealmSwitchMessage realmData = realm.where(RealmSwitchMessage.class)
                        .equalTo(SwitchMessage.SWITCH_KEY_ROW, key).findFirst();
                if(realmData == null){
                    realmData = new RealmSwitchMessage();
                    realmData.setSwitchKey(key);
                }
                return realmData;
            }
        }).map(new Func1<RealmSwitchMessage, SwitchMessage>() {
            @Override
            public SwitchMessage call(RealmSwitchMessage realmUser) {
                return new SwitchMessage(realmUser);
            }
        });
    }

    @Override
    public Observable<SwitchMessage> saveSwitch(final SwitchMessage message) {
        return RealmObservable.object(new Func1<Realm, RealmSwitchMessage>() {
            @Override
            public RealmSwitchMessage call(Realm realm) {
                RealmSwitchMessage realmSwitchMessage = new RealmSwitchMessage();
                realmSwitchMessage.setSwitchKey(message.getSwitchKey());
                realmSwitchMessage.setSwitchValue(message.isSwitchOpened());
                return realm.copyToRealmOrUpdate(realmSwitchMessage);
            }
        }).map(new Func1<RealmSwitchMessage, SwitchMessage>() {
            @Override
            public SwitchMessage call(RealmSwitchMessage realmUser) {
                // map to UI object
                return new SwitchMessage(realmUser);
            }
        });
    }
}
