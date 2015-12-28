package com.dudu.monitor.repo;

import android.content.Context;
import android.content.SharedPreferences;

import com.dudu.network.NetworkManage;
import com.dudu.network.event.ActiveDevice;
import com.dudu.network.event.ActiveDeviceRes;
import com.dudu.network.event.CheckDeviceActiveRes;
import com.dudu.network.event.RebootDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/12/2.
 * Description :
 */
public class ActiveDeviceManage{
    private static ActiveDeviceManage instance = null;

    public static int ACTIVE_OK = 1;
    public static int UNACTIVE = 0;

    private Context mContext;
    private SharedPreferences sp;
    private Logger log;
    /* 激活状态*/
    private int activeState = 0;

    public static  ActiveDeviceManage getInstance(Context context){
        if (instance == null){
            synchronized (ActiveDeviceManage.class){
                if (instance == null){
                    instance = new ActiveDeviceManage(context);
                }
            }
        }
        return instance;
    }

    private ActiveDeviceManage(Context context) {
        mContext = context;
        sp = context.getSharedPreferences("ActiveDevice", Context.MODE_PRIVATE);

        log = LoggerFactory.getLogger("monitor");

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        activeState = getActiveFlag();
    }

    public  void setActiveFlag(int flag){
        if(sp!=null){
            sp.edit().putInt("activeFlag",flag).commit();
        }

    }

    public int getActiveFlag(){
        if(sp!=null){
            return sp.getInt("activeFlag", 0);
        }
        return 0;
    }

    public void onEventBackgroundThread(ActiveDeviceRes activeDeviceRes){
        log.info("收到--设备激活响应----" + activeDeviceRes.isActive());
        if(activeDeviceRes.isActive()){
            activeState = 1;
            setActiveFlag(ACTIVE_OK);
        }
    }

    public void onEventBackgroundThread(CheckDeviceActiveRes checkDeviceActiveRes){
        log.info("收到-检查-设备激活响应----"+ checkDeviceActiveRes.isActive());
        if(checkDeviceActiveRes.isActive()){
            activeState = 1;
            setActiveFlag(ACTIVE_OK);
        }else {
            log.info("monitor-发送-设备激活-----信息");
            NetworkManage.getInstance().sendMessage(new ActiveDevice(mContext));
        }
    }

    /**
     * 接受重启的命令
     * */
    public void onEventBackgroundThread(RebootDevice rebootDevice){
        if (rebootDevice.isRebootDeviceFlag()){
            reboot();
        }
    }

    private void reboot() {
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.boot", "reboot");
    }

    public int getActiveState() {
        return activeState;
    }
}
