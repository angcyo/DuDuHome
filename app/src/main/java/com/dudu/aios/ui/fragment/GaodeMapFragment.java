package com.dudu.aios.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.MapView;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.android.launcher.R;

/**
 * Created by dudusmart on 16/2/11.
 */
public class GaodeMapFragment extends BaseFragment{

    private MapView mapView;

    @Override
    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.gaode_map_layout, null);
        initView(view);
        return view;
    }


    private void initView(View view){

        mapView = (MapView)view.findViewById(R.id.gaode_MapView);

    }

    private void initData(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
