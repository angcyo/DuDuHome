package com.dudu.aios.ui.fragment.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/16.
 */
public abstract class BaseVehicleFragment extends BaseFragment {

    private RelativeLayout childVehicleViewContainer;

    @Override

    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.prevent_rob_layout, null);
        initVehicleView(view);
        return view;
    }

    private void initVehicleView(View view) {

        childVehicleViewContainer = (RelativeLayout) view.findViewById(R.id.vehicle_right_layout);

        ViewGroup.LayoutParams rightParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        childVehicleViewContainer.addView(getVehicleChildView(), rightParams);
    }

    public abstract View getVehicleChildView();
}
