package com.dudu.obd;

import android.content.Context;

/**
 * Created by lxh on 2015/12/3.
 */
public class ObdInit {
    public static final int INIT_BLEOBD = 0;

    public static final int INIT_PODOBD = 1;

    public static void initOBD(int type,Context context){

        switch (type){
            case INIT_BLEOBD:
                BleOBD bleOBD = new BleOBD();
                bleOBD.initOBD(context);
                break;
            case INIT_PODOBD:
                PodOBD podOBD = new PodOBD();
                podOBD.init(context);
                break;
        }

    }
}
