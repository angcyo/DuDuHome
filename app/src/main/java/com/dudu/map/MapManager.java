package com.dudu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.ui.activity.LocationActivity;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.ui.activity.NaviBackActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;

import java.io.Serializable;
import java.util.List;

public class MapManager {

    public static final int SEARCH_POI = 1;             // poi地点搜索（我要去XXX，导航到XXX）
    public static final int SEARCH_NAVI = 2;            // 打开导航搜索（打开导航）
    public static final int SEARCH_NEARBY = 3;          // 附近POI(附近的XXX)
    public static final int SEARCH_COMMONADDRESS = 4;   // 搜索常用地址
    public static final int SEARCH_PLACE_LOCATION = 5;  // 搜索某地的位置(如我现在的位置，XXX地的位置)
    public static final int SEARCH_NEAREST = 6;         // 搜索最近的XXX
    public static final int SEARCH_DEFAULT = 0;

    public static final int ADDRESS_VIEW_COUNT = 4;				// 每页显示4条

    public static final String HAS_KEYWORD = "HAS_KEYWORD";

    public static final String SEARCH_KEYWORD = "SEARCH_KEYWORD";

    public static final String ISMANUAL = "isManual";

    private static MapManager mInstance;

    private boolean isShowAddress = false;              //	是否正在地图页面中展示地址选择列表或路线规划策略选择列表

    private boolean isNavi = false;                     //  是否正在导航

    private boolean isNaviBack = false;                  // 是否正在导航返程中

    private boolean isSearch = false;                   // 是否为语音“打开导航后”的搜索

    private int searchType = 0;

    private String commonAddressType;

    public String getCommonAddressType() {
        return commonAddressType;
    }

    public void setCommonAddressType(String commonAddressType) {
        this.commonAddressType = commonAddressType;

    }

    private MapManager() {

    }

    public boolean isShowAddress() {
        return isShowAddress;
    }

    public void setShowAddress(boolean isShowAddress) {
        this.isShowAddress = isShowAddress;
    }

    public boolean isNavi() {
        return isNavi;
    }

    public void setNavi(boolean isNavi) {
        this.isNavi = isNavi;
    }

    public boolean isNaviBack() {
        return isNaviBack;
    }

    public void setNaviBack(boolean isNaviBack) {
        this.isNaviBack = isNaviBack;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }

    public static MapManager getInstance() {
        if (mInstance == null) {
            mInstance = new MapManager();
        }

        return mInstance;
    }

    public void mapControl(Serializable data,
                           String poiKeyWord, int type) {

		Activity topActivity = ActivitiesManager.getInstance().getTopActivity();

        setSearchType(type);

		if (topActivity != null && topActivity instanceof LocationMapActivity) {

			((LocationMapActivity) topActivity).handlerSarch(data,poiKeyWord);

		} else {

			Bundle bundle = new Bundle();
			bundle.putSerializable(Constants.PARAM_MAP_DATA, data);
			if (!TextUtils.isEmpty(poiKeyWord)) {
				bundle.putBoolean(HAS_KEYWORD, true);
				bundle.putString(SEARCH_KEYWORD, poiKeyWord);
			}

			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(topActivity, LocationMapActivity.class);
            topActivity.startActivity(intent);
		}
    }

}
