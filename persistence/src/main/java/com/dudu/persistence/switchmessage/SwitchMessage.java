package com.dudu.persistence.switchmessage;

/**
 * Created by Administrator on 2016/2/20.
 */
public class SwitchMessage {

    public static final String SWITCH_KEY_ROW = "switchKey";
    public static final String ROBBERY_STATE_KEY = "robbery_state";
    public static final String ROBBERY_SWITCH_KEY = "robbery_switch";
    public static final String GUARD_SWITCH_KEY = "guard_switch";

    private String switchKey;

    private boolean isSwitchOpened;

    public SwitchMessage(String switchKey, boolean switchOpened){
        this.switchKey = switchKey;
        this.isSwitchOpened = switchOpened;
    }

    public SwitchMessage(RealmSwitchMessage realmSwitchMessage){
        this.switchKey = realmSwitchMessage.getSwitchKey();
        this.isSwitchOpened = realmSwitchMessage.getSwitchValue();
    }

    public String getSwitchKey() {
        return switchKey;
    }

    public void setSwitchKey(String switchKey) {
        this.switchKey = switchKey;
    }

    public boolean isSwitchOpened() {
        return isSwitchOpened;
    }

    public void setSwitchOpened(boolean switchOpened) {
        this.isSwitchOpened = switchOpened;
    }
}
