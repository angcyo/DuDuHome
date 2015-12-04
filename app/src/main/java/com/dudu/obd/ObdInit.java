package com.dudu.obd;

import android.content.Context;

import com.dudu.android.launcher.utils.Utils;

/**
 * Created by lxh on 2015/12/3.
 */
public class ObdInit {


    public static void initOBD(Context context){

        switch (Utils.getOBDType(context)){
            case "thread":
                BleOBD bleOBD = new BleOBD();
                bleOBD.initOBD(context);
                break;
            case "pod":
                PodOBD podOBD = new PodOBD();
                podOBD.init(context);
                break;
            case "xfa":
                XfaOBD xfaOBD = new XfaOBD();
                xfaOBD.initXfaOBD(context);
                break;
            default:
                BleOBD bleOBD2 = new BleOBD();
                bleOBD2.initOBD(context);
                break;
        }

    }
}
