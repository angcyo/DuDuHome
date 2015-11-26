package com.dudu.agedmodel;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dudu.event.LocationChangeEvent;
import com.dudu.map.AMapNaviHandler;
import com.dudu.utils.LocationUtils;

import java.util.List;

import de.greenrobot.event.EventBus;


public class AgedMapActivity extends NoTitleActivity implements LocationSource, GeocodeSearch.OnGeocodeSearchListener {
    private MapView mapView;
    private EditText txtMessage;
    private String cityCode;
    private LatLonPoint latLonPoint;
    private AMap aMap;
    private GeocodeSearch geocodeSearch;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiOverlay poiOverlay = null;
    private List<PoiItem> poiItems = null;
    private LocationSource.OnLocationChangedListener listener;
    private Handler handler = new MyHandler();
    private static final String NEAR_ADDRESS="附近的湘菜馆";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aged_map);
        initView(savedInstanceState);
        initData();
    }

    private void initView(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        txtMessage = (EditText) findViewById(R.id.txt_message);
    }

    private void initData() {
        EventBus.getDefault().register(this);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        txtMessage.setText(NEAR_ADDRESS);
    }

    private void setUpMap() {
        cityCode = LocationUtils.getInstance(this).getCurrentCityCode();
        double[] location = LocationUtils.getInstance(this).getCurrentCoordinate();
        Log.v("ji..", location[0] + "");
        latLonPoint = new LatLonPoint(location[0], location[1]);
        //自定义系统的小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));//设置小蓝点的图标
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(80, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
        aMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏地图放大缩小按钮
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location[0], location[1]), 15));
        // 初始语音播报资源
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        Log.v("ji..", "您好，您现在在" + regeocodeResult.getRegeocodeAddress().getFormatAddress()
                + "附近");
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (latLonPoint != null) {
            handlerOpenNavi();
        }
        handler.sendEmptyMessageDelayed(0, 10000);
    }

    private void handlerOpenNavi() {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
        search();

    }

    private void search() {
        query = new PoiSearch.Query(NEAR_ADDRESS, "", cityCode);
        query.setPageSize(20);// 设置每页最多返回多少条poi item
        query.setPageNum(0);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 2000));
        poiSearch.setOnPoiSearchListener(onPoiSearchListener);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }

    }

    PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            if (i == 0) {
                if (poiResult != null && poiResult.getQuery() != null) {
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0; j < poiItems.size(); j++) {
                            sb.append(poiItems.get(j).getSnippet() + "\n");
                        }
                        Log.v("ji..", sb.toString());
                        handlerPoiResult();

                    }
                }
            }
        }

        @Override
        public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

        }
    };

    private void handlerPoiResult() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        poiOverlay = new PoiOverlay(aMap, poiItems);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
    }

    public void onEventMainThread(LocationChangeEvent event) {
        listener.onLocationChanged(event.getLocation());// 显示系统小蓝点
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            AMapNaviHandler.getInstance(AgedMapActivity.this).initNavigationHandle();
            double[] destination = {poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude()};
            AMapNaviHandler.getInstance(AgedMapActivity.this).startNavi(destination);
        }

        ;
    }
}
