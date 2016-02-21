package com.dudu.aios.ui.vehicle;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.repo.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public class SearchAddress {

    private Context context;

    private LatLonPoint latLonPoint;

    private PoiSearch.Query query;// Poi查询条件类

    private PoiSearch poiSearch;// POI搜索

    private List<PoiItem> poiItems = null;

    private List<PoiResultInfo> poiResultList = new ArrayList<>();

    private AMapLocation cur_location;

    private String cityCode = "";

    /**
     * 回调接口
     */
    private OnObtainAddressListener onObtainAddressListener;

    public SearchAddress(Context context) {
        this.context = context;
    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnGestureLockViewListener(OnObtainAddressListener listener) {
        this.onObtainAddressListener = listener;
    }

    public void search(String keyword) {
        cur_location = Monitor.getInstance(context).getCurrentLocation();
        if (cur_location != null) {
            latLonPoint = new LatLonPoint(cur_location.getLatitude(), cur_location.getLongitude());
        }
        cityCode = LocationUtils.getInstance(context).getCurrentCity();
        doSearch(keyword);
    }

    private void doSearch(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            query = new PoiSearch.Query(keyword, "", cityCode);
            query.setPageSize(20);// 设置每页最多返回多少条poi item
            query.setPageNum(0);// 设置查第一页
            poiSearch = new PoiSearch(context, query);
            if (latLonPoint != null)
                poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 2000));
        }
        poiSearch.setOnPoiSearchListener(onPoiSearchListener);
        poiSearch.searchPOIAsyn();

    }

    private PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {

        @Override
        public void onPoiSearched(PoiResult poiResult, int code) {
            ResourceManager.getInstance(context).getPoiResultList().clear();
            if (code == 0) {
                if (poiResult != null && poiResult.getQuery() != null) {
                    // 取得搜索到的poiitems有多少页
                    poiItems = poiResult.getPois();
                    // 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        if (onObtainAddressListener != null) {
                            setPoiList();
                            onObtainAddressListener.onAddress(poiResultList);
                        }

                    }
                }
            }
        }
    };

    public interface OnObtainAddressListener {
        void onAddress(List<PoiResultInfo> poiResultList);
    }


    private void setPoiList() {
        LatLng startPoints_gaode = null;
        if (Monitor.getInstance(context).getCurrentLocation() != null) {
            startPoints_gaode = new LatLng(Monitor.getInstance(context).getCurrentLocation().getLatitude(),
                    Monitor.getInstance(context).getCurrentLocation().getLongitude());
        }

        if (startPoints_gaode != null && !poiItems.isEmpty()) {
            ResourceManager.getInstance(context).setPoiItems(poiItems);
            poiResultList.clear();
            for (int i = 0; i < poiItems.size(); i++) {
                PoiResultInfo poiResultInfo = new PoiResultInfo();
                poiResultInfo.setAddressDetial(poiItems.get(i).getSnippet());
                poiResultInfo.setAddressTitle(poiItems.get(i).getTitle());
                poiResultInfo.setLatitude(poiItems.get(i).getLatLonPoint()
                        .getLatitude());
                poiResultInfo.setLongitude(poiItems.get(i).getLatLonPoint()
                        .getLongitude());

                LatLng endPoints_gaode = new LatLng(poiItems.get(i)
                        .getLatLonPoint().getLatitude(), poiItems.get(i)
                        .getLatLonPoint().getLongitude());
                poiResultInfo.setDistance(AMapUtils.calculateLineDistance(startPoints_gaode, endPoints_gaode));
                Log.v("kkk", "distance:" + AMapUtils.calculateLineDistance(startPoints_gaode, endPoints_gaode));
                poiResultList.add(poiResultInfo);
            }
        }
    }

}
