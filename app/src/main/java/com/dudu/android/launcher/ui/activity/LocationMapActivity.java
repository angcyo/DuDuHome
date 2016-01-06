package com.dudu.android.launcher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.dialog.RouteSearchPoiDialog;
import com.dudu.android.launcher.ui.dialog.StrategyChoiseDialog;
import com.dudu.android.launcher.ui.view.CleanableCompletaTextView;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.ViewAnimation;
import com.dudu.event.MapResultShow;
import com.dudu.map.NavigationClerk;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.navi.NavigationManager;
import com.dudu.navi.entity.Navigation;
import com.dudu.navi.entity.PoiResultInfo;
import com.dudu.navi.entity.Point;
import com.dudu.navi.vauleObject.NavigationType;
import com.dudu.navi.vauleObject.SearchType;
import com.dudu.voice.semantic.SemanticType;
import com.dudu.voice.semantic.engine.SemanticProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by pc on 2015/11/3.
 */
public class LocationMapActivity extends BaseNoTitlebarAcitivity implements LocationSource {

    private static final String TAG = "LocationMapActivity";
    private LinearLayout endLocationLL;
    private CleanableCompletaTextView search_edit;
    private Button search_btn;
    private Button search_enter;
    private Button mBackButton;

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener listener;
    private PoiOverlay poiOverlay;

    private Logger log;

    private LatLonPoint latLonPoint;

    private NavigationManager navigationManager;

    private Handler mHandler;

    private AMapLocation amapLocation;

    private RouteSearchPoiDialog addressDialog; // 地址选择弹出框

    private StrategyChoiseDialog strategyDialog;// 优先策略选择弹出框


    private Runnable getLocatinRunable = new Runnable() {
        @Override
        public void run() {

            if (mHandler != null)
                mHandler.postDelayed(this, 2000);
            amapLocation = Monitor.getInstance(LauncherApplication.getContext()).getCurrentLocation();
            if (amapLocation != null) {
                log.debug("LocationMapActivity amapLocation");
                if (listener != null) {
                    listener.onLocationChanged(amapLocation);// 显示系统小蓝点
                }
            }
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

        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE
                        ? R.anim.back_key_disappear : R.anim.back_key_appear, LocationMapActivity.this);
            }
        }, 3000);

        navigationManager = NavigationManager.getInstance(getApplicationContext());
    }

    @Override
    public void initListener() {
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchManual();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationMapActivity.this, MainActivity.class));
                finish();
            }
        });

        search_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (View.VISIBLE == endLocationLL.getVisibility())
                    endLocationLL.setVisibility(View.GONE);
                else
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
                    searchManual();
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void initDatas() {

        navigationManager = NavigationManager.getInstance(getApplicationContext());
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

    }

    private void searchManual() {
        if (TextUtils.isEmpty(search_edit.getText().toString()))
            return;
        if (containsEmoji(search_edit.getText().toString())) {
            ToastUtils.showToast("抱歉，您输入的关键字有误，请重新输入");
            return;
        }
        navigationManager.setKeyword(search_edit.getText().toString());
        navigationManager.setSearchType(SearchType.SEARCH_PLACE);
        NavigationClerk.getInstance().setIsManual(true);
        NavigationClerk.getInstance().doSearch();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        getLocation();
        NavigationClerk.getInstance().doSearch();
    }

    private void setUpMap() {
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
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curPoint[0], curPoint[1]), 15));
        // 初始语音播报资源
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE
                        ? R.anim.back_key_disappear : R.anim.back_key_appear, LocationMapActivity.this);
            }
        });
    }

    private void getLocation() {
        if (mHandler != null) {
            mHandler.postDelayed(getLocatinRunable, 1000);
        }
    }

    public void onEventMainThread(MapResultShow event) {
        log.debug("---------MapResultShow");
        switch (event) {
            case ADDRESS:
                switch (navigationManager.getSearchType()) {
                    case SEARCH_PLACE:
                    case SEARCH_NEARBY:
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        poiOverlay = new PoiOverlay(aMap, navigationManager.getAmapPoiItem());
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                        break;
                }
                showAddressManual();
                break;
            case STRATEGY:
                showStrategyDialog(NavigationClerk.getInstance().getChoosepoiResult());
                break;
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
        navigationManager.setSearchType(SearchType.SEARCH_DEFAULT);
        NavigationClerk.getInstance().setIsShowAddress(false);
        NavigationClerk.getInstance().setIsManual(false);
        NavigationClerk.getInstance().disMissProgressDialog();
        NavigationClerk.getInstance().removeCallback();
        dissMissDialog();
        if (mHandler != null && getLocatinRunable != null) {
            mHandler.removeCallbacks(getLocatinRunable);
        }
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        super.onDestroy();
    }

    public void showAddressManual() {

        if (addressDialog != null && addressDialog.isShowing()) {
            addressDialog.dismiss();
        }
        addressDialog = new RouteSearchPoiDialog(this);
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
                if (position >= 20)
                    return;
                addressDialog.dismiss();
                NavigationClerk.getInstance().chooseAddress(position);
            }
        });
    }

    /**
     * 手动搜索时弹出的策略选择框
     */
    private void showStrategyDialog(final PoiResultInfo choosePoint) {
        if (strategyDialog != null && strategyDialog.isShowing()) {
            strategyDialog.dismiss();
        }
        if (addressDialog != null && addressDialog.isShowing()) {
            addressDialog.dismiss();
        }
        String address = choosePoint.getAddressDetial();
        String[] end_Address = new String[]{
                choosePoint.getAddressTitle(),
                TextUtils.isEmpty(address) ? "中国" : address};
        strategyDialog = new StrategyChoiseDialog(this, end_Address,
                new LatLonPoint(choosePoint.getLatitude(), choosePoint.getLongitude()));
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
                        Point point = new Point(choosePoint.getLatitude(), choosePoint.getLongitude());
                        NavigationClerk.getInstance().startNavigation(new Navigation(point, navigationManager.getDriveModeList().get(position),
                                NavigationType.NAVIGATION));
                    }
                });
    }

    public void dissMissDialog() {
        if (addressDialog != null && addressDialog.isShowing())
            addressDialog.dismiss();
        if (strategyDialog != null && strategyDialog.isShowing())
            strategyDialog.dismiss();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }
}
