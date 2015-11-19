package com.dudu.android.launcher.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
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
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.MapEntity;
import com.dudu.android.launcher.bean.MapLocation;
import com.dudu.android.launcher.bean.MapSlots;
import com.dudu.android.launcher.bean.MapSlotsLoc;
import com.dudu.android.launcher.bean.PoiResultInfo;
import com.dudu.android.launcher.bean.RestaurantEntity;
import com.dudu.android.launcher.bean.RestaurantSlots;
import com.dudu.android.launcher.bean.StrategyMethod;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog;
import com.dudu.android.launcher.ui.dialog.StrategyChoiseDialog;
import com.dudu.android.launcher.ui.view.CleanableCompletaTextView;
import com.dudu.android.launcher.utils.CommonAddressUtil;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.Coordinate;
import com.dudu.android.launcher.utils.FloatWindow;
import com.dudu.android.launcher.utils.FloatWindowUtil;
import com.dudu.android.launcher.utils.JourneyTool;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.ViewAnimation;
import com.dudu.map.AmapLocationChangeEvent;
import com.dudu.map.MapManager;
import com.dudu.map.Navigation;
import com.dudu.map.NavigationHandler;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.VoiceManager;
import com.dudu.voice.semantic.chain.ChoiseChain;
import com.dudu.voice.semantic.chain.ChoosePageChain;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/3.
 */
public class LocationMapActivity extends BaseNoTitlebarAcitivity implements LocationSource,GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "LocationMapActivity";
    private LinearLayout endLocationLL;
    private CleanableCompletaTextView search_edit;
    private Button search_btn;
    private Button search_enter;
    private Button mBackButton;

    private MapView mapView;
    private AMap aMap;
    private LocationSource.OnLocationChangedListener listener;

    private ProgressDialog progDialog = null;// 搜索时进度条

    private RouteSearchPoiDialog addressDialog; // 地址选择弹出框
    private StrategyChoiseDialog strategyDialog;// 优先策略选择弹出框


    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiOverlay poiOverlay = null;
    private String cur_locationDesc = "";
    private String cityCode = "";
    private List<PoiItem> poiItems = null;
    private List<PoiResultInfo> poiResultList = new ArrayList<PoiResultInfo>();
    private int chooseType;

    private String searchKeyWord;

    private AMapLocation cur_location;
    private NaviLatLng mEndPoint = new NaviLatLng();

    private boolean isManual = false;

    private boolean isAddressManual = false;

    private List<StrategyMethod> mStrategyMethods = new ArrayList<StrategyMethod>();// 记录行车策略的数组

    private String[] end_Address;

    private Handler mhandler;

    private MapManager mapManager;

    private VoiceManager mVoiceManager;

    private boolean isAddCommonAddress = false;

    private String naviAddress;

    private Bundle bundle;

    private Logger log;

    private static final int REMOVEWINDOW_TIME = 9 * 1000;

    private Bundle locBundle;

    private String locProvider;

    private GeocodeSearch geocoderSearch;

    private LatLonPoint latLonPoint;

    private boolean isGetCurdesc = false;

    private int satellite = 0;

    private View view_Satellite;
    private Runnable removeWindowRunnable = new Runnable() {

        @Override
        public void run() {
            FloatWindowUtil.removeFloatWindow();
        }
    };


    @Override
    public int initContentView() {
        return R.layout.location;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        log = LoggerFactory.getLogger("lbs.map");

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        endLocationLL = (LinearLayout) findViewById(R.id.endLocationLL);
        search_edit = (CleanableCompletaTextView) findViewById(R.id.search_edit);
        search_btn = (Button) findViewById(R.id.search_btn);
        mBackButton = (Button) findViewById(R.id.back_button);
        search_enter = (Button) findViewById(R.id.search_enter);
        mhandler = new Handler();

        if (progDialog == null)
            progDialog = new ProgressDialog(LocationMapActivity.this);
        if (progDialog.isShowing())
            progDialog.dismiss();
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);

        view_Satellite = findViewById(R.id.view_Satellite);

        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE
                        ? R.anim.back_key_disappear : R.anim.back_key_appear,LocationMapActivity.this);
            }
        },3000);
    }

    @Override
    public void initListener() {
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKeyWord = search_edit.getText().toString();
                mapManager.setSearchType(MapManager.SEARCH_POI);
                search();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        search_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLocationLL.setVisibility(View.VISIBLE);
            }
        });

        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    isManual = true;
                    mapManager.setSearchType(MapManager.SEARCH_POI);
                    searchKeyWord = search_edit.getText().toString();
                    search();

                    return true;
                }

                return false;
            }
        });

    }


    @Override
    public void initDatas() {

        initResources();

        mapManager = MapManager.getInstance();

        mVoiceManager = VoiceManager.getInstance();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        bundle = getIntent().getExtras();

        if (!isManual) {
            endLocationLL.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        if (latLonPoint != null) {
            handlerOpenNavi();
        }

    }

    private void getMapEntity(Serializable data) {
        if (data instanceof MapEntity) {
            startLocationInit((MapEntity) data);
        } else if (data instanceof RestaurantEntity) {
            startRestaurantLocationInit((RestaurantEntity) data);
        }
    }

    private void setUpMap() {
        cityCode = LocationUtils.getInstance(this).getCurrentCityCode();
        double[] curPoint = LocationUtils.getInstance(this).getCurrentLocation();
        latLonPoint = new LatLonPoint(curPoint[0], curPoint[1]);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
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
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curPoint[0],curPoint[1]), 15));
        // 初始语音播报资源
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE
                        ? R.anim.back_key_disappear : R.anim.back_key_appear,LocationMapActivity.this);
            }
        });
    }

    public void onEventMainThread(AmapLocationChangeEvent event) {

        cur_location = event.getAMapLocation();
        if (cur_location != null) {

            latLonPoint = new LatLonPoint(cur_location.getLatitude(), cur_location.getLongitude());

            if (listener != null) {
                listener.onLocationChanged(cur_location);// 显示系统小蓝点
            }
            locBundle = cur_location.getExtras();
            locProvider = cur_location.getProvider();
        }

    }

    public void getCur_locationDesc() {

        if (locProvider!=null&&locProvider.equals("lbs")) {

            cur_locationDesc = locBundle.getString("desc");
            String playText = "您好，您现在在" + cur_locationDesc;
            mVoiceManager.startSpeaking(playText,SemanticConstants.TTS_START_UNDERSTANDING,true);
            toNaivActivity(REMOVEWINDOW_TIME);
        }
        else{
            isGetCurdesc = true;
        }
    }

    public String getCityCode() {

        if (locProvider!=null&&locProvider.equals("lbs")) {
            cityCode = locBundle.getString("citycode");
        }
        return cityCode;
    }

    public void handlerSearch(Serializable data, String keyWord) {

        if (data != null) {
            getMapEntity(data);
        }
        if (keyWord != null) {
            this.searchKeyWord = keyWord;
        }
        search();

    }

    public void handlerOpenNavi() {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
        switch (mapManager.getSearchType()) {

            case MapManager.SEARCH_NAVI:
                if (!Constants.CURRENT_POI.equals(searchKeyWord)) {

                    if (!isManual) {
                        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NAVIGATION);
                        mhandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                String playText = "您好，请说出您想去的地方或者关键字";
                                VoiceManager.getInstance().startSpeaking(
                                        playText, SemanticConstants.TTS_START_UNDERSTANDING);
                            }
                        }, 200);

                    }

                } else {
                    getCur_locationDesc();
                }

                break;
            case MapManager.SEARCH_POI:
            case MapManager.SEARCH_NEARBY:
            case MapManager.SEARCH_PLACE_LOCATION:
            case MapManager.SEARCH_NEAREST:
                Serializable data = bundle.getSerializable(Constants.PARAM_MAP_DATA);
                if (data != null) {
                    getMapEntity(data);

                } else {

                    boolean hasKeyword = bundle.getBoolean(MapManager.HAS_KEYWORD, false);
                    if (hasKeyword) {
                        searchKeyWord = bundle.getString(MapManager.SEARCH_KEYWORD);
                    }
                }
                search();
                break;

            case MapManager.SEARCH_COMMONADDRESS:
                isAddCommonAddress = true;
                mhandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        String playText = "您好，请说出您要添加的地址";
                        VoiceManager.getInstance().startSpeaking(
                                playText, SemanticConstants.TTS_START_UNDERSTANDING);
                    }
                }, 200);
                break;
        }

    }

    public void startRestaurantLocationInit(RestaurantEntity restaurantEntity) {

        if (restaurantEntity == null) {
            Toast.makeText(this, "无法识别，请从新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        RestaurantSlots slots = restaurantEntity.getRestaurantSlots();
        if (slots != null) {
            mapManager.setSearchType(MapManager.SEARCH_NEARBY);
            searchKeyWord = slots.getCategory();
        }
        Log.d(TAG, "---------resuaurantkeyWord:" + searchKeyWord);
    }

    public void startLocationInit(MapEntity mapEntity) {
        if (mapEntity == null) {
            Toast.makeText(this, "无法识别，请从新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        MapSlots slots = mapEntity.getSlots();
        MapSlotsLoc location = slots.getEndLoc();
        if (location != null) {
            if (!TextUtils.isEmpty(location.getPoi())) {
                searchKeyWord = location.getPoi();

            } else {
                if (!TextUtils.isEmpty(location.getAreaAddr())) {
                    searchKeyWord = location.getAreaAddr();
                } else {
                    if (!TextUtils.isEmpty(location.getCity())) {
                        searchKeyWord = location.getCity();
                    }
                }

            }

        } else {

            MapLocation mapLocation = slots.getLocation();
            if (mapLocation != null) {
                if (!TextUtils.isEmpty(mapLocation.getPoi())) {
                    searchKeyWord = mapLocation.getPoi();
                } else {

                    if (!TextUtils.isEmpty(mapLocation.getAreaAddr())) {
                        searchKeyWord = mapLocation.getAreaAddr();
                    } else {
                        if (!TextUtils.isEmpty(mapLocation.getCity())) {
                            searchKeyWord = mapLocation.getCity();
                        }
                    }
                }

            }

        }

    }

    private void search() {
        Log.d(TAG, "searchKeyWord ： " + searchKeyWord);
        if (!TextUtils.isEmpty(searchKeyWord)) {
            if (Constants.CURRENT_POI.equals(searchKeyWord)) {
                getCur_locationDesc();
                return;
            }
            FloatWindowUtil.removeFloatWindow();
            showProgressDialog();// 显示进度框
            query = new PoiSearch.Query(searchKeyWord, "", getCityCode());
            query.setPageSize(20);// 设置每页最多返回多少条poi item
            query.setPageNum(0);// 设置查第一页
            poiSearch = new PoiSearch(this, query);
            if (mapManager.getSearchType() == MapManager.SEARCH_NEARBY) {
                poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 2000));
            }
            poiSearch.setOnPoiSearchListener(onPoiSearchListener);
            poiSearch.searchPOIAsyn();
        } else {

            String playText = "您好，关键字有误，请重新输入";
            if (isManual) {
                Toast.makeText(this, playText, Toast.LENGTH_SHORT).show();
            } else {
                mVoiceManager.startSpeaking(playText,
                        SemanticConstants.TTS_START_UNDERSTANDING, false);
            }
        }

    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog != null) {
            progDialog.setMessage("正在搜索:\n" + searchKeyWord);
            progDialog.show();
        }
    }


    private void initResources() {

        StrategyMethod strategy_one = new StrategyMethod(getString(R.string.navi_strategy_speed),
                AMapNavi.DrivingDefault);

        StrategyMethod strategy_two = new StrategyMethod(getString(R.string.navi_strategy_cost),
                AMapNavi.DrivingSaveMoney);

        StrategyMethod strategy_three = new StrategyMethod(getString(R.string.navi_strategy_distance),
                AMapNavi.DrivingShortDistance);

        StrategyMethod strategy_four = new StrategyMethod(getString(R.string.navi_strategy_nohighway),
                AMapNavi.DrivingNoExpressways);

        StrategyMethod strategy_five = new StrategyMethod(getString(R.string.navi_strategy_timenojam),
                AMapNavi.DrivingFastestTime);

        StrategyMethod strategy_six = new StrategyMethod(getString(R.string.navi_strategy_costnojam),
                AMapNavi.DrivingAvoidCongestion);

        mStrategyMethods.add(strategy_one);
        mStrategyMethods.add(strategy_two);
        mStrategyMethods.add(strategy_three);
        mStrategyMethods.add(strategy_four);
        mStrategyMethods.add(strategy_five);
        mStrategyMethods.add(strategy_six);


    }

    /**
     * 弹出策略选择框
     */
    private void showStrategyDialog(LatLonPoint[] points) {
        if (isManual) {
            if (strategyDialog != null && strategyDialog.isShowing()) {
                strategyDialog.dismiss();
            }
            strategyDialog = new StrategyChoiseDialog(LocationMapActivity.this, end_Address, points);

            Window dialogWindow = strategyDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.x = 10; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.alpha = 0.8f; // 透明度
            dialogWindow.setAttributes(lp);
            strategyDialog.show();
            strategyDialog
                    .setOnStrategyClickListener(new StrategyChoiseDialog.OnStrategyClickListener() {

                        @Override
                        public void onStrategyClick(int position) {
                            strategyDialog.dismiss();
                            startNavigation(position);
                        }
                    });
        }
    }


    PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {

        @Override
        public void onPoiSearched(PoiResult poiResult, int code) {
            dissmissProgressDialog();

            switch (code) {

                case 0:
                    if (poiResult != null && poiResult.getQuery() != null) {

                        // 取得搜索到的poiitems有多少页
                        poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        List<SuggestionCity> suggestionCities = poiResult
                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                        aMap.clear();// 清理之前的图标
                        if (poiItems != null && poiItems.size() > 0) {
                            setPoiList();
                            handlerPoiResult();
                        } else {
                            VoiceManager.getInstance().stopUnderstanding();
                            VoiceManager.getInstance().startSpeaking(
                                    getString(R.string.no_result),
                                    SemanticConstants.TTS_DO_NOTHING);

                        }
                    } else {

                        VoiceManager.getInstance().stopUnderstanding();
                        VoiceManager.getInstance().startSpeaking(
                                getString(R.string.no_result),
                                SemanticConstants.TTS_DO_NOTHING);

                    }
                    break;
                case 27:
                    log.debug("搜索失败,请检查网络连接");
                    VoiceManager.getInstance().stopUnderstanding();
                    FloatWindowUtil.showMessage(getString(R.string.error_network),
                            FloatWindow.MESSAGE_IN);

                    break;
                case 32:
                    VoiceManager.getInstance().stopUnderstanding();
                    FloatWindowUtil.showMessage(getString(R.string.error_key),
                            FloatWindow.MESSAGE_IN);

                    break;
                default:
                    log.debug("未知错误，请稍后重试!错误码为:{}", code);
                    VoiceManager.getInstance().stopUnderstanding();
                    FloatWindowUtil.showMessage(getString(R.string.error_other) + code,
                            FloatWindow.MESSAGE_IN);


                    break;

            }

        }

        @Override
        public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

        }
    };

    private void handlerPoiResult() {
        SemanticProcessor.getProcessor().switchSemanticType(
                SemanticType.MAP_CHOISE);
        if (mapManager.getSearchType() == MapManager.SEARCH_PLACE_LOCATION) {

            String playText = "您好，" + searchKeyWord + "的位置为：" + poiResultList.get(0).getAddressDetial();
            mVoiceManager.startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING, true);

            return;
        }
        if (mapManager.getSearchType() == MapManager.SEARCH_NEAREST) {
            PoiResultInfo startpoiItem = poiResultList.get(0);
            mEndPoint = new NaviLatLng(startpoiItem.getLatitude(), startpoiItem.getLongitude());
            showStrategyMethod();
            return;
        }
        chooseType = 1;
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        poiOverlay = new PoiOverlay(aMap, poiItems);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
        mapManager.setShowAddress(true);
        if (isManual) {
            showAddressDialog();
        } else {
            removeCallback();
            final String playText = "请选择列表中的地址";
            mVoiceManager.clearMisUnderstandCount();
            mVoiceManager.startSpeaking(
                    playText, SemanticConstants.TTS_START_UNDERSTANDING, false);
            FloatWindowUtil.showAddress(poiResultList,
                    new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(
                                AdapterView<?> arg0,
                                View arg1,
                                int position, long arg3) {
                            isAddressManual = true;
                            mVoiceManager.stopUnderstanding();
                            chooseAddress(position);
                        }
                    });
        }

    }

    private void chooseAddress(int position) {
        try {
            PoiResultInfo startpoiItem = (isManual || isAddressManual) ? poiResultList.get(position)
                    : poiResultList.get(position - 1);
            if (startpoiItem != null) {

                LatLonPoint point = new LatLonPoint(
                        startpoiItem.getLatitude(),
                        startpoiItem.getLongitude());

                mEndPoint.setLatitude(startpoiItem.getLatitude());
                mEndPoint.setLongitude(startpoiItem.getLongitude());

                naviAddress = startpoiItem.getAddressTitle();
                if (addressDialog != null && addressDialog.isShowing()) {
                    addressDialog.dismiss();
                }
                if (isAddCommonAddress) {
                    mapManager.setShowAddress(false);
                    FloatWindowUtil.removeFloatWindow();
                    String addType = mapManager.getCommonAddressType();
                    CommonAddressUtil.setCommonAddress(addType, this, startpoiItem.getAddressTitle());
                    CommonAddressUtil.setCommonLocation(addType,
                            this, point.getLatitude(), point.getLongitude());
                    VoiceManager.getInstance().startSpeaking("添加" + startpoiItem.getAddressTitle() + "为" + addType + "地址成功！",
                            SemanticConstants.TTS_DO_NOTHING, true);

                    return;
                }
                if (isManual) {

                    chooseType = 2;
                    String address = startpoiItem.getAddressDetial();
                    end_Address = new String[]{
                            startpoiItem.getAddressTitle(),
                            TextUtils.isEmpty(address) ? "中国" : address};
                    LatLonPoint startPoint = new LatLonPoint(
                            cur_location.getLatitude(),
                            cur_location.getLongitude());
                    LatLonPoint[] points = {startPoint, point};
                    showStrategyDialog(points);
                } else {

                    if (!isAddressManual && position > poiResultList.size()) {
                        mVoiceManager.stopUnderstanding();
                        String playText = "选择错误，请重新选择";
                        mVoiceManager.startSpeaking(playText,
                                SemanticConstants.TTS_START_UNDERSTANDING, false);
                        return;
                    }
                    showStrategyMethod();

                }

            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            if (!isManual) {
                if (position > poiItems.size()) {
                    mVoiceManager.stopUnderstanding();
                    String playText = "选择错误，请重新选择";
                    mVoiceManager.startSpeaking(playText,
                            SemanticConstants.TTS_START_UNDERSTANDING, false);
                }
            }
        }

    }

    private void showStrategyMethod() {
        mVoiceManager.stopUnderstanding();
        mVoiceManager.clearMisUnderstandCount();
        removeCallback();
        chooseType = 2;
        String playText = "请选择路线优先策略。";
        mVoiceManager.startSpeaking(playText,
                SemanticConstants.TTS_START_UNDERSTANDING, false);
        FloatWindowUtil.showStrategy(mStrategyMethods,
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0, View view,
                            int position, long arg3) {
                        startNavigation(position);
                    }
                });

    }

    // 选择路径规划策略
    private void chooseDriveMode(int position) {
        if(poiResultList.isEmpty())
            return;

        if (position > mStrategyMethods.size()) {
            mVoiceManager.stopUnderstanding();
            String playText = "选择错误，请重新选择";
            VoiceManager.getInstance().startSpeaking(playText,
                    SemanticConstants.TTS_START_UNDERSTANDING, false);
            return;
        }
        startNavigation(mStrategyMethods.get(position - 1).getDriveMode());

    }

    public void startChooseResult(int size, int type) {

        if (type == ChoiseChain.TYPE_NORMAL) {

            if (chooseType == 1) {
                chooseAddress(size);
            } else {
                chooseDriveMode(size);
            }
        } else {

            if (isManual) {
                addressDialog.choosePage(size);
            } else {
                FloatWindowUtil.chooseAddressPage(ChoosePageChain.CHOOSE_PAGE, size);
            }
        }

    }


    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
        }
    }

    /**
     * 弹出地址选择框
     */
    private void showAddressDialog() {

        if (isManual) {
            if (addressDialog != null && addressDialog.isShowing()) {
                addressDialog.dismiss();
            }
            addressDialog = new RouteSearchPoiDialog(this,
                    poiResultList);

            Window dialogWindow = addressDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.x = 10; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.alpha = 0.8f; // 透明度
            dialogWindow.setAttributes(lp);
            addressDialog.show();
            addressDialog.setOnListClickListener(new RouteSearchPoiDialog.OnListItemClick() {
                @Override
                public void onListItemClick(int position) {
                    addressDialog.dismiss();
                    chooseAddress(position);
                }
            });
        }
    }

    private void setPoiList() {
        if (latLonPoint != null && !poiItems.isEmpty()) {
            // 高德坐标转换为真实坐标
            double[] startPoints_gaode = Coordinate.chinatowg( latLonPoint.getLongitude(), latLonPoint.getLatitude());
            poiResultList.clear();
            for (int i = 0; i < poiItems.size(); i++) {
                PoiResultInfo poiResultInfo = new PoiResultInfo();
                poiResultInfo.setAddressDetial(poiItems.get(i).getSnippet());
                poiResultInfo.setAddressTitle(poiItems.get(i).getTitle());
                poiResultInfo.setLatitude(poiItems.get(i).getLatLonPoint()
                        .getLatitude());
                poiResultInfo.setLongitude(poiItems.get(i).getLatLonPoint()
                        .getLongitude());

                double[] endPoints_gaode = Coordinate.chinatowg(poiItems.get(i)
                        .getLatLonPoint().getLongitude(), poiItems.get(i)
                        .getLatLonPoint().getLatitude());
                poiResultInfo.setDistance(JourneyTool.getDistance(startPoints_gaode[1], startPoints_gaode[0],
                        endPoints_gaode[1], endPoints_gaode[0]));
                poiResultList.add(poiResultInfo);
            }

            Collections.sort(poiResultList, new PoiResultInfo.MyComparator());
        }
    }


    // 开始导航
    private void startNavigation(int driveMode) {
        NavigationHandler.getInstance(getApplicationContext()).initNavigationHandle();
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        mapManager.setSearchType(0);
        FloatWindowUtil.removeFloatWindow();
        mVoiceManager.stopUnderstanding();
        if (progDialog != null) {
            progDialog.setMessage("路径规划中...");
            progDialog.show();
        }
        double[] destination = {mEndPoint.getLatitude(), mEndPoint.getLongitude()};
        mapManager.setNavi(false);
        mapManager.setNaviBack(false);
        EventBus.getDefault().post(new Navigation(destination, Navigation.NAVI_NORMAL, driveMode));
    }

    public void choosePage(int type) {


        if (type == ChoosePageChain.NEXT_PAGE) {
            if (isManual) {
                if (addressDialog != null)
                    addressDialog.nextPage();
            } else {

                FloatWindowUtil.chooseAddressPage(ChoosePageChain.NEXT_PAGE, 0);
            }

        } else {
            if (isManual) {
                if (addressDialog != null)
                    addressDialog.lastPage();
            } else {

                FloatWindowUtil.chooseAddressPage(ChoosePageChain.LAST_PAGE, 0);
            }

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
        mapManager.setSearchType(0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MapManager.getInstance().setSearchType(0);
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        if (progDialog != null && progDialog.isShowing()) {
            progDialog.dismiss();
            progDialog = null;
        }
        if (addressDialog != null && addressDialog.isShowing())
            addressDialog.dismiss();
        if (strategyDialog != null && strategyDialog.isShowing())
            strategyDialog.dismiss();
        removeCallback();
        poiSearch = null;
        super.onDestroy();

        SemanticProcessor.getProcessor().switchSemanticType(SemanticType.NORMAL);
    }

    public void removeCallback() {

        if (mhandler != null && removeWindowRunnable != null) {
            mhandler.removeCallbacks(removeWindowRunnable);
        }
    }

    public void toNaivActivity(int t) {

        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mapManager.isNavi() || mapManager.isNaviBack()){
                    FloatWindowUtil.removeFloatWindow();
                    startActivity(new Intent(LocationMapActivity.this, NaviCustomActivity.class));
                    finish();
                }
            }
        }, t);

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    &&!TextUtils.isEmpty(result.getRegeocodeAddress().getFormatAddress())) {
                cityCode = result.getRegeocodeAddress().getCity();
                cur_locationDesc = "您好，您现在在"+result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                if(isGetCurdesc){
                    mVoiceManager.startSpeaking(cur_locationDesc, SemanticConstants.TTS_START_UNDERSTANDING,true);
                    toNaivActivity(REMOVEWINDOW_TIME);
                }
                isGetCurdesc = false;
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    public void onEventMainThread(GpsStatus gpsStatus){
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                .iterator();
        satellite = 0;
        while (iters.hasNext() && satellite <= maxSatellites) {
            satellite++;
        }
        if(satellite > 0 &&(locProvider!=null&&!locProvider.equals("lbs"))){
            view_Satellite.setBackgroundColor(Color.GREEN);
        }
        log.debug("搜索到{}颗卫星", satellite);

    }


}
