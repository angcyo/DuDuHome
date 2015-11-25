package com.dudu.navi.service;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 2015/11/25.
 */
public class NaviProcess {

    private static NaviProcess naviProcess;

    private Context mContext;

    public NaviProcess(Context context){
        this.mContext = context;

    }

    public static NaviProcess getInstance(Context context){

        if(naviProcess==null)
            naviProcess = new NaviProcess(context);

        return naviProcess;
    }

    public void initNaviProcess(){

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

    }



    public void onEventBackgroundThread(){


    }
}
