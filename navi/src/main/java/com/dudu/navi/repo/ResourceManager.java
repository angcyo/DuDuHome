package com.dudu.navi.repo;

import android.content.Context;
import android.content.Intent;

import com.amap.api.services.core.PoiItem;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.vauleObject.NaviDriveMode;
import com.dudu.navi.vauleObject.NavigationType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lxh on 2015/11/25.
 */
public class ResourceManager {

    private static ResourceManager resourceManager;

    private Context mContext;

    private List<NaviDriveMode> driveModeList = new ArrayList<>();

    private String cur_locationDesc;

    private List<PoiResultInfo> poiResultList = new ArrayList<PoiResultInfo>();

    private List<PoiItem> poiItems;

    public ResourceManager(Context context) {

        this.mContext = context;
    }

    public static ResourceManager getInstance(Context context) {

        if (resourceManager == null)
            resourceManager = new ResourceManager(context);
        return resourceManager;
    }

    public void init() {

        initDriveMode();
    }

    /**
     * 初始化导航驾驶模式数据
     */
    private void initDriveMode() {

        driveModeList.add(0, NaviDriveMode.SPEEDFIRST);
        driveModeList.add(1, NaviDriveMode.SAVEMONEY);
        driveModeList.add(2, NaviDriveMode.SHORTDESTANCE);
        driveModeList.add(3, NaviDriveMode.NOEXPRESSWAYS);
        driveModeList.add(4, NaviDriveMode.FASTESTTIME);
        driveModeList.add(5, NaviDriveMode.AVOIDCONGESTION);
    }

    public List<NaviDriveMode> getDriveModeMap() {
        return driveModeList;
    }


    public void setCur_locationDesc(String cur_locationDesc) {
        this.cur_locationDesc = cur_locationDesc;
    }

    public String getCur_locationDesc() {
        return cur_locationDesc;
    }

    public void setPoiResultList(List<PoiResultInfo> poiResultList) {
        this.poiResultList = poiResultList;
    }

    public List<PoiResultInfo> getPoiResultList() {
        return poiResultList;
    }

    public List<PoiItem> getPoiItems() {
        return poiItems;
    }

    public void setPoiItems(List<PoiItem> poiItems) {

        this.poiItems = poiItems;
    }
}
