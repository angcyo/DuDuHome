package com.dudu.map;

import android.content.Context;

import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.ui.activity.LocationActivity;

import java.io.Serializable;

public class MapManager {

    public static final int SEARCH_POI = 1;    // poi搜索
    public static final int SEARCH_NAVI = 2;    // 导航搜索
    public static final int SEARCH_NEARBY = 3;    //  附近POI
    public static final int SEARCH_FROM_TO = 4;     // 从某点到某点

    private static MapManager mInstance;

    private boolean isShowAddress = false;              //	是否正在地图页面中展示地址选择列表或路线规划策略选择列表

    private boolean isNavi = false;                     //  是否正在导航

    private boolean isNaviBack = false;                  // 是否正在导航返程中

    private boolean isSearch = false;                   // 是否为语音“打开导航后”的搜索

    private int searchType = 0;                        // POI 搜索的类型  1、代表poi。某点的位置。 2、代表导航。3、代表附近的poi查询。4、代表从某点到某点。

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

    public void mapControl(Context context, Serializable data,
                           String poiKeyWord, int type) {
//		Activity activity = ActivitiesManager.getInstance().getTopActivity();
//		if (activity != null && activity instanceof LocationActivity) {
//			choiseType(data, (LocationActivity) activity, poiKeyWord, type);
//		} else {
//			if (activity instanceof NaviCustomActivity) {
//				if(!isNavi)
////					ActivitiesManager.getInstance().closeTargetActivity(
////							NaviCustomActivity.class);
//				if(!isNaviBack)
//					ActivitiesManager.getInstance().closeTargetActivity(NaviBackActivity.class);
//				List<Activity> activities = ActivitiesManager.getInstance()
//						.getTargetActivity(LocationActivity.class);
//				if (!activities.isEmpty()) {
//					activity = activities.get(0);
//				}
//				if (activity != null && activity instanceof LocationActivity) {
//					choiseType(data, (LocationActivity) activity, poiKeyWord,
//							type);
//				}
//			}
//
//			Bundle bundle = new Bundle();
//			bundle.putSerializable(Constants.PARAM_MAP_DATA, data);
//			if (type == 3) {
//				bundle.putBoolean("isPoi", true);
//				bundle.putString("poiKeyWord", poiKeyWord);
//			}
//
//			if (type == 4) {
//				bundle.putBoolean("isSearchPoi", true);
//				bundle.putString("poiKeyWord", poiKeyWord);
//			}
//
//			bundle.putBoolean("isManual", false);
//			Intent intent = new Intent();
//			intent.putExtras(bundle);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setClass(context, LocationActivity.class);
//			context.startActivity(intent);
//		}
    }

    private static void choiseType(Serializable data,
                                   LocationActivity activity, String poiKeyWord, int type) {
        switch (type) {
            case 1:
            case 2:
                activity.startLocationInit((MapEntity) data, true, false);
                break;
            case 3:
                activity.startLocationPoi(poiKeyWord, false);
                break;
            case 4:
                activity.startSearchPoi(poiKeyWord, false);
                break;
            case 5:
                break;
        }
    }

}
