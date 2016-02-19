package com.dudu.aios.ui.map;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.dudu.aios.ui.map.event.ChooseEvent;
import com.dudu.aios.ui.map.observable.MapObservable;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.GaodeMapLayoutBinding;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.monitor.Monitor;
import com.dudu.voice.VoiceManagerProxy;
import com.dudu.voice.semantic.constant.TTSType;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;

/**
 * Created by lxh on 16/2/11.
 */
public class GaodeMapActivity extends Activity implements LocationSource {

    private GaodeMapLayoutBinding binding;

    private MapObservable mapObservable;

    private AMap aMap;

    private OnLocationChangedListener listener;

    private Handler mHandler;

    private AMapLocation amapLocation;


    private Runnable getLocatinRunable = new Runnable() {
        @Override
        public void run() {

            if (mHandler != null)
                mHandler.postDelayed(this, 2000);
            amapLocation = Monitor.getInstance(LauncherApplication.getContext()).getCurrentLocation();
            if (amapLocation != null) {
                if (listener != null) {
                    listener.onLocationChanged(amapLocation);// 显示系统小蓝点
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActivitiesManager.getInstance().addActivity(this);

        binding = DataBindingUtil.setContentView(this, R.layout.gaode_map_layout);
        mapObservable = new MapObservable(binding);
        mapObservable.init();

        binding.setMap(mapObservable);

        binding.gaodeMapView.onCreate(savedInstanceState);

        binding.mapListView.setHasFixedSize(true);
        binding.mapListView.setLayoutManager(new LinearLayoutManager(this));

        mHandler = new Handler();

        initMap();

        initView();

    }

    private void initMap() {
        if (aMap == null) {
            aMap = binding.gaodeMapView.getMap();
        }
        setLocationStyle();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        aMap.setOnMapTouchListener(motionEvent -> {
          mapObservable.displayList();

        });

    }

    private void setLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        myLocationStyle.radiusFillColor(Color.argb(80, 0, 0, 180));
        myLocationStyle.strokeWidth(0.1f);
        aMap.setMyLocationStyle(myLocationStyle);
    }


    private void initView() {


        binding.mapSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    mapObservable.showDelete.set(true);
                } else {
                    mapObservable.showDelete.set(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.mapSearchEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mapObservable.searchManual(null);
                return true;
            }
            return false;
        });

        binding.mapListView.setHasFixedSize(true);

    }


    public void onBackPressed(View view) {
        super.onBackPressed();
        finish();
    }

    private void getLocation() {
        if (mHandler != null) {
            mHandler.postDelayed(getLocatinRunable, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ActivitiesManager.getInstance().removeActivity(this);

        binding.gaodeMapView.onDestroy();

        if (mHandler != null && getLocatinRunable != null) {
            mHandler.removeCallbacks(getLocatinRunable);
        }
        mapObservable.release();

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.gaodeMapView.onResume();
        getLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.gaodeMapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }



}
