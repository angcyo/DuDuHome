package com.dudu.navi.repo;

import android.content.Context;

import com.dudu.navi.vauleObject.NaviDriveMode;

import java.util.EnumMap;

/**
 * Created by lxh on 2015/11/25.
 */
public class ResourceManager {

    private static ResourceManager resourceManager;

    private Context mContext;

    private EnumMap<NaviDriveMode, String> driveModeMap;

    public ResourceManager(Context context){

        this.mContext = context;
    }

    public static ResourceManager getInstance(Context context){

        if(resourceManager==null)
            resourceManager = new ResourceManager(context);
        return resourceManager;
    }
    public void init(){

        initDriveMode();
    }

    /**
     * 初始化导航驾驶模式数据
     */
    private void initDriveMode(){

        driveModeMap = new EnumMap<NaviDriveMode, String>(
                NaviDriveMode.class );
        driveModeMap.put(NaviDriveMode.SPEEDFIRST,"速度最快");
        driveModeMap.put(NaviDriveMode.SAVEMONEY,"避免收费");
        driveModeMap.put(NaviDriveMode.SHORTDESTANCE,"距离最短");
        driveModeMap.put(NaviDriveMode.NOEXPRESSWAYS,"不走高速快速路");
        driveModeMap.put(NaviDriveMode.FASTESTTIME,"时间最短且躲避拥堵");
        driveModeMap.put(NaviDriveMode.AVOIDCONGESTION,"避免收费且躲避拥堵");

    }

    public EnumMap<NaviDriveMode, String> getDriveModeMap(){
        return driveModeMap;
    }

}
