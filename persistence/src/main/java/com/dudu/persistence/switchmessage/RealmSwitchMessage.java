package com.dudu.persistence.switchmessage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2016/2/20.
 */
public class RealmSwitchMessage extends RealmObject {

    @PrimaryKey
    private String switchKey;

    private boolean switchValue;

    public String getSwitchKey() {
        return switchKey;
    }

    public void setSwitchKey(String switchKey) {
        this.switchKey = switchKey;
    }

    public boolean getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(boolean switchValue) {
        this.switchValue = switchValue;
    }
}
