package com.dudu.aios.ui.map;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.databinding.GaodeMapLayoutBinding;

/**
 * Created by lxh on 16/2/11.
 */
public class GaodeMapActivity extends Activity implements LocationSource {

    private GaodeMapLayoutBinding binding;

    private MapObservable mapObservable;

    private AMap aMap;

    private OnLocationChangedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.gaode_map_layout);
        mapObservable = new MapObservable(binding);
        binding.setMap(mapObservable);

        binding.gaodeMapView.onCreate(savedInstanceState);

        binding.mapListView.setHasFixedSize(true);
        binding.mapListView.setLayoutManager(new LinearLayoutManager(this));

        initMap();
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

    public void onBackPressed(View view) {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.gaodeMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.gaodeMapView.onResume();
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
