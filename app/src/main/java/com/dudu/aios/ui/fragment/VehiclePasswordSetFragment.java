package com.dudu.aios.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.dudu.aios.ui.fragment.base.BaseVehicleFragment;
import com.dudu.android.launcher.R;

/**
 * Created by Administrator on 2016/2/16.
 */
public class VehiclePasswordSetFragment extends BaseVehicleFragment {
    @Override
    public View getVehicleChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.guard_password_set_layout, null);
        initListener();
        return view;
    }

    private void initListener() {

    }
}
