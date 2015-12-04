package com.dudu.obd;

import android.content.Context;

import com.dudu.monitor.event.XfaOBDEvent;
import com.sd.core.callback.OBDDataListener;
import com.sd.sdk.SuperOBD;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/12/4.
 */
public class XfaOBD implements OBDDataListener {
    private SuperOBD superOBD;
    private Logger log;

    public XfaOBD(){
        log = LoggerFactory.getLogger("odb.xfa");
    }

    public void initXfaOBD(Context context){
        log.debug("init xfa obd");
        superOBD = SuperOBD.getInstance(context, this);
        superOBD.init();

    }

    @Override
    public void startIntent(String s, int i) {

    }

    @Override
    public void OBDTYPEData(String s) {

    }

    @Override
    public void VINData(String s) {

    }

    @Override
    public void RPMData(String s) {

    }

    @Override
    public void SPEEDData(String s) {

    }

    @Override
    public void TEMPERATUREData(String s) {

    }

    @Override
    public void AIRData(String s) {

    }

    @Override
    public void THROTTELPOSData(String s) {

    }

    @Override
    public void VOLTAGEData(String s) {

    }

    @Override
    public void MPHData(String s) {

    }

    @Override
    public void REMOILData(String s) {

    }

    @Override
    public void SENSORVOLTAGEData(String s) {

    }

    @Override
    public void CARSTATUS(String s) {

    }

    @Override
    public void DRONData(String s) {

    }

    @Override
    public void DTCData(JSONObject jsonObject) {

    }

    @Override
    public void resultData(String s) {
        log.debug("xfa obd data:{}",s);
        EventBus.getDefault().post(new XfaOBDEvent(s));
    }
}
