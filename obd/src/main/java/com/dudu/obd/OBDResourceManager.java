package com.dudu.obd;


public class OBDResourceManager {

    private static OBDResourceManager obdResourceManager;

    public static OBDResourceManager getInstance(){

        if(obdResourceManager == null){
            obdResourceManager = new OBDResourceManager();
        }

        return obdResourceManager;
    }

    public void initOBDResourceManager(){



    }


}
