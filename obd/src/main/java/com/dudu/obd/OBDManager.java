package com.dudu.obd;

import com.dudu.obd.service.BleOBD;

/**
 * Created by Administrator on 2015/11/19.
 */
public class OBDManager {

    private static OBDManager obdManager;

    private OBDResourceManager obdResourceManager;

    private BleOBD bleOBD;
    public OBDManager() {

    }

    private static OBDManager getInstance() {
        if (obdManager == null)
            obdManager = new OBDManager();
        return obdManager;
    }

    public void initOBD() {
        obdResourceManager =  OBDResourceManager.getInstance();
        obdResourceManager.initOBDResourceManager();

    }
}
